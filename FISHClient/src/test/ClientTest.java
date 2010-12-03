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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Configure;
import util.Helper;

/**
 *
 * @author CUNEYT
 */
public class ClientTest {

    public static void main(String[] args) {
        String commands = Helper.SHARE + "\n" + Helper.SEARCH + "\n" + Helper.DOWNLOAD + "\nquit";
        String USAGE = "USAGE: java test.ClientTest [shared path] [save path] [server address] [server port] [upload port] [ping port]";
        String sharePath = System.getProperty("USER.DIR");
        String savePath = System.getProperty("USER.DIR");
        boolean done = false;
        Configure conf = null;
        String server = null;
        int serverPort;
        int sharePort;
        int pingPort;
        try {
            conf = new Configure();
            server = conf.getProperty("server");
            serverPort = Integer.parseInt(conf.getProperty("serverPort"));
            sharePort = Integer.parseInt(conf.getProperty("sharePort"));
            pingPort = Integer.parseInt(conf.getProperty("pingPort"));
        } catch (IOException ioe) {
            server = "localhost";
            serverPort = 8088;
            sharePort = 8087;
            pingPort = 8089;
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
            server = args[2];
        }
        if (args.length > 3) {
            try {
                serverPort = Integer.parseInt(args[3]);
                if (serverPort < 1 || serverPort > 65535) {
                    throw new NumberFormatException("Port number must be between 1-65535");
                }
            } catch (NumberFormatException nfe) {
                System.out.println(USAGE);
                System.exit(0);
            }
        }
        if (args.length > 4) {
            try {
                sharePort = Integer.parseInt(args[4]);
                if (sharePort < 1 || sharePort > 65535) {
                    throw new NumberFormatException("Port number must be between 1-65535");
                }
            } catch (NumberFormatException nfe) {
                System.out.println(USAGE);
                System.exit(0);
            }
        }
        if (args.length > 5) {
            try {
                pingPort = Integer.parseInt(args[5]);
                if (pingPort < 1 || pingPort > 65535) {
                    throw new NumberFormatException("Port number must be between 1-65535");
                }
            } catch (NumberFormatException nfe) {
                System.out.println(USAGE);
                System.exit(0);
            }
        }

        try {
            System.out.println("Trying to connect " + server + " on " + serverPort);
            Client c = new Client(sharePath, savePath, server, serverPort, sharePort, pingPort);
            System.out.println(commands);
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String line;
            List<String> result;
            while (!done) {
                System.out.print("Enter your choice:");
                line = in.readLine();
                if (line.equals(Helper.SHARE)) {
                    c.share();
                } else if (line.equals(Helper.SEARCH)) {
                    System.out.print("Enter the file name to search:");
                    line = in.readLine();
                    result = c.search(line);
                    for (String r : result) {
                        System.out.println(r);
                    }
                } else if (line.equals(Helper.DOWNLOAD)) {
                    try {
                        System.out.print("File name:");
                        String fn = in.readLine();
                        System.out.print("Server address:");
                        String sa = in.readLine();
                        System.out.print("Server port:");
                        int sp = Integer.parseInt(in.readLine());
                        if (sp < 1 || sp > 65535) {
                            throw new NumberFormatException("Port number has to be between 1-65535");
                        }
                        c.download(fn, sa, sp);
                    } catch (NumberFormatException nfe) {
                        System.out.println(nfe);
                    }
                } else if (line.equals("help")) {
                    System.out.println(commands);
                } else if (line.equals("quit")) {
                    c.unshare();
                    c.closeConnection();
                    done = true;
                } else {
                    System.out.println("Invalid command! Type help to see commands.");
                }
            }
            System.exit(1);
        } catch (UnknownHostException ex) {
            Logger.getLogger(ClientTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
