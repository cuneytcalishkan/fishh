/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CUNEYT
 */
public class Ponger implements Runnable {

    private int port;

    /**
     * The dedicated thread for listening to the pings from the server
     * @param port the port number to listen for the connections
     */
    public Ponger(int port) {
        this.port = port;
    }

    public void run() {
        try {
            ServerSocket ponger = new ServerSocket(port);
            while (true) {
                ponger.accept();
            }
        } catch (IOException ex) {
            Logger.getLogger(Ponger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
