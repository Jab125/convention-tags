package dev.jab125.remapping;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.mappingio.MappingUtil;
import net.fabricmc.mappingio.adapter.MappingSourceNsSwitch;
import net.fabricmc.mappingio.format.proguard.ProGuardFileReader;
import net.fabricmc.mappingio.format.tiny.Tiny2FileReader;
import net.fabricmc.mappingio.format.tiny.Tiny2FileWriter;
import net.fabricmc.mappingio.tree.MemoryMappingTree;
import net.fabricmc.tinyremapper.IMappingProvider;
import net.fabricmc.tinyremapper.OutputConsumerPath;
import net.fabricmc.tinyremapper.TinyRemapper;
import net.fabricmc.tinyremapper.TinyUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Remapper {
	public static void run(Path input, Path output, Path... classpath) throws IOException {
		try(Stopwatch total = new Stopwatch("Total")) {
			Setup.setup();
			try(Stopwatch remap = new Stopwatch("Remap")) {
				remap(input, output, classpath);
			}
		}
	}

	private static void remap(Path input, Path output, Path... classpath) throws IOException {
		TinyRemapper.Builder builder = TinyRemapper.newRemapper();
		builder.withMappings(TinyUtils.createTinyMappingProvider(Setup.JOINED, "mojang", "intermediary"));
		TinyRemapper remapper = builder.build();
		try (OutputConsumerPath outputConsumer = new OutputConsumerPath.Builder(output).build()) {
			outputConsumer.addNonClassFiles(input, net.fabricmc.tinyremapper.NonClassCopyMode.UNCHANGED, remapper);

			remapper.readInputs(input);
			remapper.readClassPath(classpath);

			remapper.apply(outputConsumer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			remapper.finish();
		}
	}
}
