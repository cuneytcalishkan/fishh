/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import util.Helper;

/**
 *
 * @author CUNEYT
 */
public class Client {

    private String basePath;
    private String savePath;
    private ArrayList<Object> sharedFiles;
    private String server;
    private int serverPort;
    private Socket socket;
    private BufferedReader br;
    private PrintWriter pw;
    private int sharingPort;
    private int pingPort;
    private FileServer fileServer;
    private Ponger ponger;
    private Executor executor;

    /**
     * The Peer which shares files on the server and performs lookups on the server for
     * shared files, downloads files from other Peers, uploads shared files to other
     * Peers when requested.
     * @param sharePath the path to the shared files
     * @param savePath the path where the downloaded files to be saved
     * @param server the server's IP address
     * @param serverPort the server's port number
     * @param sharePort the port on which this client listens for file requests
     * @param pingPort the port on which this client listens for server's ping requests
     * @throws UnknownHostException then the server address is invalid
     * @throws IOException when the socket is not connected or an I/O error occurs while
     * creating the input and output stream readers and writers of the socket.
     */
    public Client(String sharePath, String savePath, String server, int serverPort, int sharePort, int pingPort) throws UnknownHostException, IOException {
        this.basePath = sharePath;
        this.savePath = savePath;
        this.sharedFiles = new ArrayList<Object>();
        this.server = server;
        this.serverPort = serverPort;
        this.sharingPort = sharePort;
        this.pingPort = pingPort;
        this.socket = new Socket(server, serverPort);
        br = Helper.getBufferedReader(socket);
        pw = Helper.getPrintWriter(socket);
        init();
    }

    private void init() {
        File f = new File(basePath);
        if (!f.isDirectory()) {
            System.out.println("Shared path has to point to a directory!");
            System.exit(-1);
        }
        f = new File(savePath);
        if (!f.isDirectory()) {
            System.out.println("Save path has to point to a directory!");
            System.exit(-1);
        }
        fileServer = new FileServer(this.basePath, this.sharingPort);
        fileServer.start();
        ponger = new Ponger(pingPort);
        executor = Executors.newCachedThreadPool();
        executor.execute(ponger);
    }

    /**
     * Downloads the specified file from the given server on the given port
     * @param fileName the file name to be downloaded
     * @param server the server's IP address
     * @param port the port number on which the server listens
     */
    public void download(String fileName, String server, int port) {
        executor.execute(new FileDownloader(fileName, server, port, savePath, this));
    }

    /**
     * Discovers the shred files in the shared files path and sends the list
     * of files to the server. After sending the files, starts to listen for
     * incoming file requests and server's ping requests.
     */
    public void share() {
        try {
            Helper.discoverFiles(this.basePath, this.sharedFiles);
            pw.println(Helper.SHARE);
            pw.println(sharingPort);
            pw.println(pingPort);
            for (Object object : sharedFiles) {
                pw.println(object);
            }
            pw.println(Helper.DONE);
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        }
    }

    /**
     * Sends a search request to the server for the specified file name
     * @param fileName the file name to be searched
     * @return the list of files including file name, Peer address and port
     * @throws IOException if an I/O error occurs
     */
    public List<String> search(String fileName) throws IOException {
        ArrayList<String> result = new ArrayList<String>();
        pw.println(Helper.SEARCH);
        pw.println(fileName);
        StringTokenizer st = new StringTokenizer(br.readLine(), Helper.SEPARATOR);
        while (st.hasMoreTokens()) {
            result.add(st.nextToken());
        }
        return result;
    }

    /**
     * Closes the connections to the server
     * @throws IOException if an I/O error occurs
     */
    public void closeConnection() throws IOException {
        pw.flush();
        pw.close();
        br.close();
        socket.close();
    }

    /**
     * Discovers the files in the shared files path and tells the server
     * to unshare the previous entries but to share the new ones
     */
    public void updateSharedFiles() {
        try {
            sharedFiles = new ArrayList<Object>();
            Helper.discoverFiles(basePath, sharedFiles);
            unshare();
            share();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    /**
     * Sends a request to the server to unshare the entries of this client
     * @throws IOException if an I/O error occurs
     */
    public void unshare() throws IOException {
        pw.println(Helper.UNSHARE);
    }

    /**
     * Gets the server's IP address
     * @return the IP address of the server
     */
    public String getServer() {
        return server;
    }

    /**
     * Sets the server's IP address
     * @param server  the IP address of the server
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * Gets the port number of the server
     * @return the port number
     */
    public int getServerPort() {
        return serverPort;
    }

    /**
     * Sets the port number of the server
     * @param serverPort the port number of the server
     */
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * Gets the list of shared files
     * @return the list of shared files
     */
    public ArrayList<Object> getSharedFiles() {
        return sharedFiles;
    }

    /**
     * Sets the shared files list
     * @param sharedFiles the list of new files to be shared
     */
    public void setSharedFiles(ArrayList<Object> sharedFiles) {
        this.sharedFiles = sharedFiles;
    }

    /**
     * Gets the shared files path
     * @return the path to the shared files
     */
    public String getBasePath() {
        return basePath;
    }
}
