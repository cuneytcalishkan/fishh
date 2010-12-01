/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Helper;

/**
 *
 * @author CUNEYT
 */
public class Responder extends Thread {

    private MulticastSocket ms;
    private int uploadPort;
    private ArrayList<Object> sharedFiles;
    private String sharePath;
    private Executor executor;

    public Responder(MulticastSocket ms, int uploadPort, String sharePath) {
        this.ms = ms;
        this.uploadPort = uploadPort;
        this.sharePath = sharePath;
        this.sharedFiles = new ArrayList<Object>();
        this.executor = Executors.newCachedThreadPool();
    }

    @Override
    public void run() {
        try {
            FileServer fileServer = new FileServer(sharePath, uploadPort);
            fileServer.start();
            Helper.discoverFiles(sharePath, sharedFiles);
            byte[] buf;
            DatagramPacket dp = null;
            while (true) {
                buf = new byte[1024];
                dp = new DatagramPacket(buf, buf.length);
                ms.receive(dp);
                String[] message = (new String(buf, 0, dp.getLength()).trim()).split(",");
                if (message[0].equals(Helper.SEARCH)
                        && !dp.getAddress().equals(InetAddress.getLocalHost())) {
                    System.out.println("Message received: " + new String(dp.getData()).trim());
                    System.out.println("from " + dp.getAddress().getHostAddress());
                    executor.execute(new Inform(dp, uploadPort, sharedFiles));
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Responder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
