package dev.jab125.remapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipFile;

public class Purifier {
	public static void purify(Path zip, String file) throws IOException {
		try(var fs = FileSystemUtil.getJarFileSystem(zip,false)) {
			Path pathInZipfile = fs.getPath(file);
			Files.delete(pathInZipfile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
