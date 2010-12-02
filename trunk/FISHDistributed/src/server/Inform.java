/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import util.Helper;

/**
 * This <code>Runnable</code> class is responsible for searching the requested
 * file among the shared files and sending an <code>INFORM</code> message back to
 * the client who sent the search message including the port number this peer
 * uses for uploading files.
 * @author CUNEYT
 */
public class Inform implements Runnable {

    private DatagramPacket dp;
    private int uploadPort;
    private ArrayList<Object> sharedFiles;

    /**
     * This <code>Runnable</code> class is responsible for searching the requested
     * file among the shared files and sending an <code>INFORM</code> message back to
     * the client who sent the search message including the port number this peer
     * uses for uploading files.
     * @param dp the <code>DatagramPacket</code> that has been received as a search request
     * @param uploadPort the number of the port that this peer uses for uploading files
     * @param sharedFiles the list of shared files
     */
    public Inform(DatagramPacket dp, int uploadPort, ArrayList<Object> sharedFiles) {
        this.dp = dp;
        this.uploadPort = uploadPort;
        this.sharedFiles = sharedFiles;
    }

    public void run() {

        String[] message = (new String(dp.getData()).trim()).split(",");

        String response = "";
        for (Object file : sharedFiles) {
            if (String.valueOf(file).toLowerCase().contains(message[1].toLowerCase())) {
                response += "," + file.toString();
            }
        }
        if (!response.isEmpty()) {
            try {
                response = Helper.INFORM + "," + message[1] + "," + String.valueOf(uploadPort) + response;
                byte[] buf = response.getBytes();
                DatagramPacket result = new DatagramPacket(buf, buf.length);
                result.setAddress(dp.getAddress());
                result.setPort(Integer.parseInt(message[2]));
                DatagramSocket ds = new DatagramSocket();
                ds.send(result);
            } catch (IOException ex) {
                System.out.println("Exception occured while sending the response to the search.\n" + ex);
            }
        }


    }
}
