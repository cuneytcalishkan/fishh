/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import util.Helper;

/**
 *
 * @author CUNEYT
 */
public class Search implements Runnable {

    private MulticastSocket ms;
    private int responsePort;
    private int responseTimeout;
    private String fileName;
    private int mcastPort;
    private String mcastAddress;

    public Search(MulticastSocket ms, int responsePort, int rTimeout, String fileName, int mcastPort, String mcastAddress) {
        this.ms = ms;
        this.responsePort = responsePort;
        this.responseTimeout = rTimeout;
        this.fileName = fileName;
        this.mcastPort = mcastPort;
        this.mcastAddress = mcastAddress;

    }

    public void run() {
        DatagramSocket ds = null;
        ArrayList<String> result = new ArrayList<String>();
        try {
            byte[] buf = (Helper.SEARCH + ","
                    + fileName + ","
                    + String.valueOf(responsePort)).getBytes();
            DatagramPacket dp = new DatagramPacket(buf, buf.length);
            dp.setAddress(InetAddress.getByName(mcastAddress));
            dp.setPort(mcastPort);
            ms.send(dp);
            ds = new DatagramSocket(responsePort);
            buf = new byte[1024];
            dp = new DatagramPacket(buf, buf.length);
            long expire = System.currentTimeMillis() + responseTimeout * 1000;
            ds.setSoTimeout(responseTimeout * 1000);
            while (System.currentTimeMillis() < expire) {
                ds.receive(dp);
                String[] response = (new String(dp.getData()).trim()).split(",");
                if (response[0].equals(Helper.INFORM) && response[1].equals(fileName)) {
                    for (int i = 3; i < response.length; i++) {
                        String line = dp.getAddress().getHostAddress()
                                + "," + response[2] + "," + response[i];
                        result.add(line);
                    }
                }
            }
        } catch (SocketTimeoutException ste) {
            ds.close();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        if (!result.isEmpty()) {
            System.out.println("..::Search results for \"" + fileName + "\"::..");
            for (String response : result) {
                System.out.println(response);
            }
        } else {
            System.out.println("No results for the search \"" + fileName + "\"");
        }

    }
}
