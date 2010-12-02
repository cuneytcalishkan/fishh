/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is responsible for accepting TCP/IP connections on the specified port
 * to create a new Thread for handling the file request.
 * @author CUNEYT
 */
public class FileServer extends Thread {

    private int port;
    private String sharePath;
    private Executor executor;

    /**
     * This class is responsible for accepting TCP/IP connections on the specified port
     * to create a new Thread for handling the file request.
     * @param base the path to the shared files
     * @param port the port to listen for connections
     */
    public FileServer(String base, int port) {
        this.sharePath = base;
        this.port = port;
        this.executor = Executors.newCachedThreadPool();
    }

    @Override
    public void run() {
        try {
            ServerSocket server = new ServerSocket(port);
            while (true) {
                Socket s = server.accept();
                executor.execute(new FileUploader(s, sharePath));
            }
        } catch (IOException ex) {
            Logger.getLogger(FileServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
