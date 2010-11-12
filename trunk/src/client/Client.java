/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import util.Helper;

/**
 *
 * @author CUNEYT
 */
public class Client {

    private String filePath;
    private ArrayList<Object> sharedFiles;
    private String server;
    private int serverPort;
    private Socket socket;
    private BufferedReader br;
    private PrintWriter pw;
    private int defaultSharingPort = 8088;

    public Client(String filePath, String server, int port) throws FileNotFoundException, UnknownHostException, IOException {
        this.filePath = filePath;
        this.sharedFiles = new ArrayList<Object>();
        this.server = server;
        this.serverPort = port;
        Helper.discoverFiles(this.filePath, this.sharedFiles);
        this.socket = new Socket(server, port);
        br = Helper.getBufferedReader(socket);
        pw = Helper.getPrintWriter(socket);
    }

    public void share() {
        pw.println(Helper.SHARE);
        pw.println(defaultSharingPort);
        pw.println(Helper.listToString(sharedFiles));
    }

    public void search(String fileName) throws IOException {
        pw.println(Helper.SEARCH);
        pw.println(fileName);
        StringTokenizer st = new StringTokenizer(br.readLine(), Helper.SEPARATOR);
        while (st.hasMoreTokens()) {
            String file = st.nextToken();
            System.out.println(file);
            if (file.equals(Helper.NOT_FOUND)) {
                break;
            }
        }
    }

    public void unshare() throws IOException {
        pw.println(Helper.UNSHARE);
        pw.flush();
        pw.close();
        br.close();
        socket.close();
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public ArrayList<Object> getSharedFiles() {
        return sharedFiles;
    }

    public void setSharedFiles(ArrayList<Object> sharedFiles) {
        this.sharedFiles = sharedFiles;
    }
}
