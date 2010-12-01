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
 *
 * @author CUNEYT
 */
public class Inform implements Runnable {

    private DatagramPacket dp;
    private int uploadPort;
    private ArrayList<Object> sharedFiles;

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
