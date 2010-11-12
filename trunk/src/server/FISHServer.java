/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import util.Helper;

/**
 *
 * @author Natan
 */
public class FISHServer {

    private Map<String, List<Peer>> fileList;
    private Executor executor;
    private int port;

    public FISHServer(int port) {
        this.port = port;
        executor = Executors.newCachedThreadPool();
        fileList = new ConcurrentHashMap<String, List<Peer>>();
    }

    public void listen() {
        try {
            ServerSocket listener = new ServerSocket(port);
            while (true) {
                System.out.println("Listening");
                Socket socket = listener.accept();
                System.out.println("Accepted");
                ConnectionHandler registerer = new ConnectionHandler(socket, fileList);
                executor.execute(registerer);
            }
        } catch (Exception e) {
        }
    }
}
