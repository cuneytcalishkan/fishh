/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 *
 * @author CUNEYT
 */
public class FileServer extends Thread {

    private int port;
    private String basePath;
    private Executor executor;
    private boolean done = false;

    /**
     * Listens for the connections coming to the specified port number and creates
     * a new <code>FileUploader</code> which is executed in a cached thread pool.
     * @param basePath the base path to the shared files
     * @param p the port number on which to listen
     */
    public FileServer(String basePath, int p) {
        this.basePath = basePath;
        this.port = p;
        this.executor = Executors.newCachedThreadPool();
    }

    @Override
    public void run() {
        try {
            ServerSocket server = new ServerSocket(port);
            while (!done) {
                Socket s = server.accept();
                FileUploader uploader = new FileUploader(s, basePath);
                executor.execute(uploader);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
