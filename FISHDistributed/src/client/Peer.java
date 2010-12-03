/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import server.FileServer;
import server.FileUploader;
import server.Inform;
import server.Responder;
import util.Helper;

/**
 * Creates a {@link FileServer FileServer} which listens for connections on the
 * specified upload port to to create a new {@link FileUploader FileUploader}
 * for each connection to upload requested file among the shared files included
 * in the specified share path.
 * Creates a {@link Responder Responder} which listens for the incoming messages
 * from the specified multicast address and port. Whenever a {@link Helper#SEARCH search}
 * request is received, a new {@link Inform informer} is created which searches
 * through the specified share path for the requested file name and sends back
 * an {@link Helper#INFORM inform} message to the peer if any results are found.
 * Creates a {@link Client Client} which enables to perform client side operations
 * of the peer. These operations are {@link Client#search(java.lang.String) search}
 * and {@link Client#download(java.lang.String, java.lang.String, int) download}.
 * @author CUNEYT
 */
public class Peer {

    private String sharePath;
    private String savePath;
    private String mcastAddress;
    private int mcastPort;
    private int uploadPort;
    private int responsePort;
    private int responseTimeout;
    private FileServer fileServer;
    private Responder responder;
    private MulticastSocket ms;
    private Client client;

    /**
     * Creates a {@link FileServer FileServer} which listens for connections on the
     * specified upload port to to create a new {@link FileUploader FileUploader}
     * for each connection to upload requested file among the shared files included
     * in the specified share path.
     * Creates a {@link Responder Responder} which listens for the incoming messages
     * from the specified multicast address and port. Whenever a {@link Helper#SEARCH search}
     * request is received, a new {@link Inform informer} is created which searches
     * through the specified share path for the requested file name and sends back
     * an {@link Helper#INFORM inform} message to the peer if any results are found.
     * Creates a {@link Client Client} which enables to perform client side operations
     * of the peer. These operations are {@link Client#search(java.lang.String) search}
     * and {@link Client#download(java.lang.String, java.lang.String, int) download}.
     * @param sharePath the path to the shared files
     * @param savePath the path where to save the downloaded files
     * @param mcastAddress the multicast group IP address
     * @param mcastPort the multicast group port number
     * @param uploadPort the port number on which to upload files
     * @param responsePort the port number on which to receive search results
     * @param responseTimeout the number of seconds for collecting for search results
     */
    public Peer(String sharePath, String savePath, String mcastAddress,
            int mcastPort, int uploadPort, int responsePort, int responseTimeout) {
        this.sharePath = sharePath;
        this.savePath = savePath;
        this.mcastAddress = mcastAddress;
        this.mcastPort = mcastPort;
        this.uploadPort = uploadPort;
        this.responsePort = responsePort;
        this.responseTimeout = responseTimeout;
        init();
    }

    private void init() {
        try {
            File f = new File(sharePath);
            if (!f.isDirectory()) {
                System.out.println("Shared path has to point to a directory!");
                System.exit(-1);
            }
            f = new File(savePath);
            if (!f.isDirectory()) {
                System.out.println("Save path has to point to a directory!");
                System.exit(-1);
            }
            ms = new MulticastSocket(mcastPort);
            ms.joinGroup(InetAddress.getByName(mcastAddress));
            System.out.println("Joined the multicast group " + mcastAddress + " on " + mcastPort);
            responder = new Responder(ms, uploadPort, sharePath);
            responder.start();
            fileServer = new FileServer(sharePath, uploadPort);
            fileServer.start();
            System.out.println("Sharing files in " + sharePath + " on port " + uploadPort);
            client = new Client(ms, savePath, responsePort, responseTimeout, mcastPort, mcastAddress);
        } catch (IOException ex) {
            System.out.println(ex);
            System.exit(-1);
        }
    }

    /**
     * Calls the {@link Client#download download} method of <code>Client</code> for the
     * specified parameters.
     * @param fileName the file name to be downloaded
     * @param serverAddress the IP address of the peer to download
     * @param serverPort the port number to connect on the peer
     */
    public void download(String fileName, String serverAddress, int serverPort) {
        client.download(fileName, serverAddress, serverPort);
    }

    /**
     * Calls the {@link Client#search search} method of <code>Client</code> for the
     * specified file name.
     * @param fileName the file name to be searched for
     */
    public void search(String fileName) {
        client.search(fileName);
    }
}
