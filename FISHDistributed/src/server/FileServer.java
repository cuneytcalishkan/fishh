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
 *
 * @author CUNEYT
 */
public class FileServer extends Thread {

    private int port;
    private String sharePath;
    private Executor executor;

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
