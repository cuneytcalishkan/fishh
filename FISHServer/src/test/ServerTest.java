/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import server.FISHServer;

/**
 *
 * @author CUNEYT
 */
public class ServerTest {

    public static void main(String[] args) {

        int defaultPort = 8088;

        if (args.length == 1) {
            try {
                defaultPort = Integer.parseInt(args[0]);
                if (defaultPort < 1 || defaultPort > 65535) {
                    throw new NumberFormatException("Port number must be between 1 and 65535");
                }
            } catch (NumberFormatException nfe) {
                System.out.println("USAGE: java test.ServerTest <port(1-65536)>");
                System.out.println(nfe.getMessage());
                System.exit(1);
            }
        }

        System.out.println("Starting the FISH server on port " + defaultPort);
        FISHServer server = new FISHServer(defaultPort);
        server.listen();


    }
}
