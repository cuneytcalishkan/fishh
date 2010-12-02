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
 * This class is responsible for sending a <code>SEARCH</code> request to the
 * multicast group over the specified <code>MulticastSocket</code> and given
 * <code>port</code> for the specified file name. After sending the search request,
 * collects the responses on the specified <code>port</code> for the specified amount of seconds.
 * @author CUNEYT
 */
public class Search implements Runnable {

    private MulticastSocket ms;
    private int responsePort;
    private int responseTimeout;
    private String fileName;
    private int mcastPort;
    private String mcastAddress;

    /**
     * This class is responsible for sending a <code>SEARCH</code> request to the
     * multicast group over the specified <code>MulticastSocket</code> and given
     * <code>port</code> for the specified file name. After sending the search request,
     * collects the responses on the specified <code>port</code> for the specified amount of seconds.
     *
     * @param ms the <code>MulticastSocket</code> on which to send <code>DatagramPacket</code> messages
     * @param responsePort the port number on which to listen for responses
     * @param rTimeout the number of seconds to wait for responses
     * @param fileName the file to be searched
     * @param mcastPort the port on which to send multicast messages
     * @param mcastAddress the IP address of the multicast group
     */
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
            DatagramPacket dp = new DatagramPacket(buf, buf.length,
                    InetAddress.getByName(mcastAddress), mcastPort);
            ms.send(dp);
            ds = new DatagramSocket(responsePort);
            buf = new byte[1024];
            dp = new DatagramPacket(buf, buf.length);
            long expire = System.currentTimeMillis() + responseTimeout * 1000;
            while (System.currentTimeMillis() < expire) {
                ds.setSoTimeout((int) (expire - System.currentTimeMillis()));
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
        } catch (IOException ex) {
            System.out.println(ex);
        } finally {
            if (ds != null) {
                ds.close();
            }
        }

        if (!result.isEmpty()) {
            System.out.println("\n..::Search results for \"" + fileName + "\"::..");
            for (String response : result) {
                System.out.println(response);
            }
        } else {
            System.out.println("\nNo results for the search \"" + fileName + "\"");
        }

    }
}
