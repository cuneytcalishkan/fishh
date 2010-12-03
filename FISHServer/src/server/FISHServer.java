/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import util.HibernateUtil;

/**
 *
 * @author Cuneyt
 */
public class FISHServer {

    private Executor executor;
    private int port;

    /**
     * The server that listens for incoming connections on the given port and
     * creates handler threads for each incoming request. The handler threads
     * are executed in a cached thread pool where new threads are created if there
     * is no available executor or the handler is assigned to an available executor.
     * @param port the port on which to listen the connections
     */
    public FISHServer(int port) {
        this.port = port;
        executor = Executors.newCachedThreadPool();
        HibernateUtil.getSession();
        Timer timer = new Timer(true);
        timer.schedule(new Pinger(), new Date(System.currentTimeMillis() + 10000), 60000);
    }

    /**
     * Listens for an incoming connection on the specified port and creates a new
     * handler thread for each connection.
     */
    public void listen() {
        try {
            ServerSocket listener = new ServerSocket(port);
            while (true) {
                Socket socket = listener.accept();
                System.out.println("Connection established with " + socket.getInetAddress());
                ConnectionHandler handler = new ConnectionHandler(socket);
                executor.execute(handler);
            }
        } catch (Exception e) {
            System.out.println(e);
            System.exit(-1);
        }
    }
}
