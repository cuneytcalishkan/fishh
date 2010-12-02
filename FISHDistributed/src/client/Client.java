/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.net.MulticastSocket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * This class is responsible for performing the client side operations of the
 * application. These operations are {@link #download download} and {@link #search search}.
 * @author CUNEYT
 */
public class Client {

    private MulticastSocket ms;
    private String savePath;
    private Executor executor;
    private int responseTimeout;
    private int responsePort;
    private int mcastPort;
    private String mcastAddress;

    /**
     * This class is responsible for performing the client side operations of the
     * application. These operations are {@link #download download} and {@link #search search}.
     * @param ms the <code>MulticastSocket</code> to be used for communication
     * @param savePath the path to the downloaded files to be saved
     * @param responsePort the <code>port</code> number on which to listen for search responses
     * @param rTimeout the number of seconds to wait for responses
     * @param mcastPort the <code>port</code> on which to communicate with the group
     * @param mcastAddress the IP address of the multicast group
     */
    public Client(MulticastSocket ms, String savePath, int responsePort, int rTimeout, int mcastPort, String mcastAddress) {
        this.ms = ms;
        this.savePath = savePath;
        this.responsePort = responsePort;
        this.responseTimeout = rTimeout;
        this.mcastPort = mcastPort;
        this.mcastAddress = mcastAddress;
        this.executor = Executors.newCachedThreadPool();
    }

    /**
     * Starts a new <code>Thread</code> to download the specified file from the
     * specified server on the specified port.
     * @param fileName the file name to be downloaded
     * @param server the IP address of the server of the specified file
     * @param port the <code>port</code> number of the specified server to connect
     */
    public void download(String fileName, String server, int port) {
        executor.execute(new FileDownloader(fileName, server, port, savePath));
    }

    /**
     * Starts a new <code>Thread</code> to search for the specified file name.
     * @param fileName the file name to be searched for
     */
    public void search(String fileName) {
        executor.execute(new Search(ms, responsePort, responseTimeout, fileName, mcastPort, mcastAddress));
    }
}
