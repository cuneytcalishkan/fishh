/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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

    private int uploadPort;
    private ArrayList<Object> sharedFiles;
    private String fileName;
    private InetAddress targetAddress;
    private int targetPort;

    /**
     * This <code>Runnable</code> class is responsible for searching the requested
     * file among the shared files and sending an <code>INFORM</code> message back to
     * the client who sent the search message including the port number this peer
     * uses for uploading files.
     * @param fileName the file to be searched
     * @param targetAddress the address of the requester
     * @param targetPort the port number to which to send the response
     * @param uploadPort the number of the port that this peer uses for uploading files
     * @param sharedFiles the list of shared files
     */
    public Inform(String fileName, InetAddress targetAddress, int targetPort, int uploadPort, ArrayList<Object> sharedFiles) {
        this.fileName = fileName;
        this.targetAddress = targetAddress;
        this.targetPort = targetPort;
        this.uploadPort = uploadPort;
        this.sharedFiles = sharedFiles;
    }

    public void run() {

        String response = "";
        for (Object file : sharedFiles) {
            if (String.valueOf(file).toLowerCase().contains(fileName.toLowerCase())) {
                response += "," + file.toString();
            }
        }
        if (!response.isEmpty()) {
            try {
                response = Helper.INFORM + "," + String.valueOf(uploadPort) + response;
                byte[] buf = response.getBytes();
                DatagramPacket result = new DatagramPacket(buf, buf.length, targetAddress, targetPort);
                DatagramSocket ds = new DatagramSocket();
                ds.send(result);
            } catch (IOException ex) {
                System.out.println("Exception occured while sending the response to the search.\n" + ex);
            }
        }


    }
}
