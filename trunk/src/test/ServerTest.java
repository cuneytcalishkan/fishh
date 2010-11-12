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
        int port = 5555;
        if (args.length == 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException nfe) {
                System.out.println("USAGE: test.ServerTest");
                System.out.println("USAGE: test.ServerTest [port]");
                System.exit(0);
            }
        }
        FISHServer server = new FISHServer(port);
        server.listen();
    }
}
