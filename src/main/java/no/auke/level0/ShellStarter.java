package no.auke.level0;


import net.jxta.platform.NetworkManager;

import java.text.MessageFormat;

public class ShellStarter {


    public static void main(String args[]) {
        System.setProperty("net.jxta.logging.Logging", "FINEST");
        System.setProperty("net.jxta.level", "FINEST");
        System.setProperty("java.util.logging.config.file", "logging.properties");

        NetworkManager manager = null;
        try {
            manager = new NetworkManager(NetworkManager.ConfigMode.EDGE, "HelloWorld");
            System.out.println("Starting JXTA");
            manager.startNetwork();
            System.out.println("JXTA Started");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        System.out.println("Waiting for a rendezvous connection");
        boolean connected = manager.waitForRendezvousConnection(12000);
        System.out.println(MessageFormat.format("Connected :{0}", connected));
        System.out.println("Stopping JXTA");
        manager.stopNetwork();
    }

}