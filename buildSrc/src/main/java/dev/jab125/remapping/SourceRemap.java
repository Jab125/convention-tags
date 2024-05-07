/*
 * This file is part of fabric-loom, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2021-2022 FabricMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.jab125.remapping;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.fabricmc.mappingio.MappingReader;
import net.fabricmc.mappingio.adapter.MappingDstNsReorder;
import net.fabricmc.mappingio.adapter.MappingNsCompleter;
import net.fabricmc.mappingio.adapter.MappingSourceNsSwitch;
import net.fabricmc.mappingio.format.tiny.Tiny2FileReader;
import net.fabricmc.mappingio.tree.MemoryMappingTree;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.mercury.Mercury;
import org.cadixdev.mercury.remapper.MercuryRemapper;
import org.gradle.api.file.ConfigurableFileCollection;
import net.fabricmc.lorenztiny.TinyMappingsReader;

public final class SourceRemap {
	private final File[] classpath;
	private final int javaCompileRelease;

	private final Supplier<Mercury> mercury = new Lazy<>(this::createMercury);

	private SourceRemap(File[] classpath, int javaCompileRelease) {
		try {
			Setup.setup();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		this.classpath = classpath;
		this.javaCompileRelease = javaCompileRelease;
	}

	public static SourceRemap create(File[] classpath, int javaCompileRelease) {
		return new SourceRemap(classpath, javaCompileRelease);
	}

	public void remapSourcesJar(Path source, Path destination) throws IOException {
		if (source.equals(destination)) {
			throw new UnsupportedOperationException("Cannot remap in place");
		}

		Path srcPath = source;
		boolean isSrcTmp = false;

		// Create a temp directory with all of the sources
		if (!Files.isDirectory(source)) {
			isSrcTmp = true;
			srcPath = Files.createTempDirectory("fabric-loom-src");
			unpack(source, srcPath);
		}

		if (!Files.isDirectory(destination) && Files.exists(destination)) {
			Files.delete(destination);
		}

		try (FileSystemUtil.Delegate dstFs = Files.isDirectory(destination) ? null : FileSystemUtil.getJarFileSystem(destination, true)) {
			Path dstPath = dstFs != null ? dstFs.get().getPath("/") : destination;

			doRemap(srcPath, dstPath, source);
			copyNonJavaFiles(srcPath, dstPath, source);
		} finally {
			if (isSrcTmp) {
				Files.walkFileTree(srcPath, new DeletingFileVisitor());
			}
		}
	}

	private void unpack(Path source, Path srcPath) throws IOException {
		srcPath.toFile().mkdirs();
		ZipFile zipFile = new ZipFile(source.toFile());
		for (Iterator<? extends ZipEntry> it = zipFile.entries().asIterator(); it.hasNext(); ) {
			ZipEntry entry = it.next();
			if (entry.isDirectory()) continue;
			Path newLoc = srcPath.resolve(entry.getName());
			newLoc.getParent().toFile().mkdirs();
			InputStream inputStream = zipFile.getInputStream(entry);
			Files.write(newLoc, inputStream.readAllBytes());
		}
	}

	private synchronized void doRemap(Path srcPath, Path dstPath, Path source) {
		try {
			mercury.get().rewrite(srcPath, dstPath);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Could not remap " + source + " fully!");
		}
	}

	public static void copyNonJavaFiles(Path from, Path to, Path source) throws IOException {
		Files.walk(from).forEach(path -> {
			Path targetPath = to.resolve(from.relativize(path).toString());

			if (!path.endsWith(".java") && !Files.exists(targetPath)) {
				try {
					Files.copy(path, targetPath);
				} catch (IOException e) {
					e.printStackTrace();
					System.err.println("Could not copy non-java sources '" + source + "' fully!");
				}
			}
		});
	}

	private MappingSet getMappings() throws IOException {
		Path joined = Setup.JOINED;
		MemoryMappingTree tree = new MemoryMappingTree();
		Tiny2FileReader.read(new FileReader(joined.toFile()), new MappingSourceNsSwitch(new MappingNsCompleter(tree, Map.of("intermediary", "mojang", "official", "mojang")), "mojang"));
		return new TinyMappingsReader(tree, "mojang", "intermediary").read();
	}

	private Mercury createMercury() {
		var mercury = new Mercury();
		mercury.setGracefulClasspathChecks(true);
		mercury.setSourceCompatibilityFromRelease(javaCompileRelease);

		try {
			mercury.getProcessors().add(MercuryRemapper.create(getMappings()));
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to read mercury mappings", e);
		}

		for (File file : classpath) {
			if (file.exists()) {
				mercury.getClassPath().add(file.toPath());
			}
		}

		return mercury;
	}
}