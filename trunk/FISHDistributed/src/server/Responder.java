/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Helper;

/**
 * This class receives the messages sent to the multicast group and assigns
 * a new <code>Inform</code> thread for each search request.
 * @author CUNEYT
 */
public class Responder extends Thread {

    private MulticastSocket ms;
    private int uploadPort;
    private ArrayList<Object> sharedFiles;
    private String sharePath;
    private Executor executor;

    /**
     * This class receives the messages sent to the multicast group and assigns
     * a new <code>Inform</code> thread for each search request.
     * @param ms the <code>MulticastSocket</code> to listen
     * @param uploadPort the port number on which to upload files
     * @param sharePath the path of the shared files
     */
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
            Helper.discoverFiles(sharePath, sharedFiles);
            byte[] buf;
            DatagramPacket dp = null;
            while (true) {
                buf = new byte[1024];
                dp = new DatagramPacket(buf, buf.length);
                ms.receive(dp);
                String[] message = (new String(buf, 0, dp.getLength()).trim()).split(",");
                //&& !dp.getAddress().equals(InetAddress.getLocalHost())
                if (message[0].equals(Helper.SEARCH)) {
                    executor.execute(new Inform(dp, uploadPort, sharedFiles));
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Responder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
