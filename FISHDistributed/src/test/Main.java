/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import client.Peer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Configure;
import util.Helper;

/**
 *
 * @author CUNEYT
 */
public class Main {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {

        String commands = Helper.SEARCH + "\n" + Helper.DOWNLOAD + "\nquit";
        String USAGE = "USAGE: java test.Main "
                + "[shared path] "
                + "[save path] "
                + "[multicast address] "
                + "[multicast port(1-65535)] "
                + "[upload port(1-65535)]";
        String sharePath = System.getProperty("USER.DIR");
        String savePath = System.getProperty("USER.DIR");
        boolean done = false;
        Configure conf = null;
        String mcastAddress = null;
        int mcastPort;
        int uploadPort;
        int responseTimeout;
        int responsePort;
        try {
            conf = new Configure();
            mcastAddress = conf.getProperty("mcastAddress");
            mcastPort = Integer.parseInt(conf.getProperty("mcastPort"));
            uploadPort = Integer.parseInt(conf.getProperty("sharePort"));
            responsePort = Integer.parseInt(conf.getProperty("responsePort"));
            responseTimeout = Integer.parseInt(conf.getProperty("responseTimeout"));
        } catch (IOException ioe) {
            mcastAddress = "224.100.100.100";
            mcastPort = 8088;
            uploadPort = 8087;
            responsePort = 8089;
            responseTimeout = 5;
            System.out.println("Error occured while reading properties file.\n"
                    + "Default properties assigned.");
        }

        if (args.length > 0) {
            sharePath = args[0];
        }
        if (args.length > 1) {
            savePath = args[1];
        }
        if (args.length > 2) {
            mcastAddress = args[2];
        }
        if (args.length > 3) {
            try {
                mcastPort = Integer.parseInt(args[3]);
                if (mcastPort < 1 || mcastPort > 65535) {
                    throw new NumberFormatException("Port number must be between 1-65535");
                }
            } catch (NumberFormatException nfe) {
                System.out.println(nfe);
                System.out.println(USAGE);
                System.exit(0);
            }
        }
        if (args.length > 4) {
            try {
                uploadPort = Integer.parseInt(args[4]);
                if (uploadPort < 1 || uploadPort > 65535) {
                    throw new NumberFormatException("Port number must be between 1-65535");
                }
            } catch (NumberFormatException nfe) {
                System.out.println(nfe);
                System.out.println(USAGE);
                System.exit(0);
            }
        }



        try {
            System.out.println(commands);
            Peer peer = new Peer(sharePath, savePath, mcastAddress, mcastPort, uploadPort, responsePort, responseTimeout);
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String line;
            while (!done) {
                System.out.print("Enter your choice:");
                line = in.readLine();
                if (line.equals(Helper.SEARCH)) {
                    System.out.print("Enter the file name to search:");
                    line = in.readLine();
                    peer.search(line);
                } else if (line.equals(Helper.DOWNLOAD)) {
                    System.out.print("File name:");
                    String fn = in.readLine();
                    System.out.print("Server address:");
                    String sa = in.readLine();
                    System.out.print("Server port:");
                    int sp = Integer.parseInt(in.readLine());
                    peer.download(fn, sa, sp);
                } else if (line.equals("help")) {
                    System.out.println(commands);
                } else if (line.equals("quit")) {
                    done = true;
                } else {
                    System.out.println("Invalid command! Type help to see commands.");
                }
            }
            System.exit(1);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }


    }
}
