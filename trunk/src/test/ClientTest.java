/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import client.Client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Helper;

/**
 *
 * @author CUNEYT
 */
public class ClientTest {

    public static void main(String[] args) {
        String defaultPath = System.getProperty("USER.DIR");
        String defaultServer = "localhost";
        int defaultPort = 5555;
        String commands = Helper.SHARE + "\n" + Helper.SEARCH + "\n" + Helper.UNSHARE;
        boolean done = false;
        if (args.length > 0) {
            defaultPath = args[0];
        }
        if (args.length > 1) {
            defaultServer = args[1];
        }
        if (args.length > 2) {
            try {
                defaultPort = Integer.parseInt(args[3]);
            } catch (NumberFormatException nfe) {
                System.out.println("USAGE: java test.ClientTest");
                System.out.println("USAGE: java test.ClientTest [shared path]");
                System.out.println("USAGE: java test.ClientTest [shared path] [server address]");
                System.out.println("USAGE: java test.ClientTest [shared path] [server address] [server port]");
                System.exit(0);
            }
        }

        try {
            System.out.println("Trying to connect " + defaultServer + " on " + defaultPort);
            Client c = new Client(defaultPath, defaultServer, defaultPort);
            System.out.println("You can use one of the following commands:");
            System.out.println(commands);
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String line;
            while (!done) {
                System.out.print("Enter your choice:");
                line = in.readLine();
                if (line.equals(Helper.SHARE)) {
                    c.share();
                } else if (line.equals(Helper.SEARCH)) {
                    System.out.print("Enter the file name to search:");
                    line = in.readLine();
                    c.search(line);
                } else if (line.equals(Helper.UNSHARE)) {
                    c.unshare();
                    done = true;
                } else if (line.equals("help")) {
                    System.out.println(commands);
                } else {
                    System.out.println("Invalid command! Type help to see commands.");
                }
            }

        } catch (UnknownHostException ex) {
            Logger.getLogger(ClientTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
