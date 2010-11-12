/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Helper;

/**
 *
 * @author Natan
 */
class ConnectionHandler implements Runnable {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Map<String, List<Peer>> fileList;

    public ConnectionHandler(Socket socket, Map<String, List<Peer>> fileList) {
        this.socket = socket;
        this.fileList = fileList;
    }

    public void run() {
        boolean done = false;
        try {
            reader = Helper.getBufferedReader(socket);
            writer = Helper.getPrintWriter(socket);
            while (!done) {
                String command = reader.readLine();
                if (command.equals(Helper.SHARE)) {
                    share();
                } else if (command.equals(Helper.UNSHARE)) {
                    unshare();
                    reader.close();
                    writer.close();
                    socket.close();
                    done = true;
                } else if (command.equals(Helper.SEARCH)) {
                    search();
                } else {
                    writer.println(Helper.UNKNOWN);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void share() {
        try {
            int port = Integer.parseInt(reader.readLine());
            Peer peer = new Peer(socket.getInetAddress().getHostAddress(), port);
            String files = reader.readLine();
            StringTokenizer st = new StringTokenizer(files, Helper.SEPARATOR);
            while (st.hasMoreTokens()) {
                String fileName = st.nextToken();
                List<Peer> peerList;
                if (fileList.containsKey(fileName)) {
                    peerList = fileList.get(fileName);
                } else {
                    peerList = Collections.synchronizedList(new ArrayList<Peer>());
                    fileList.put(fileName, peerList);
                }
                peerList.add(peer);
            }
        } catch (IOException ex) {
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void unshare() {
    }

    private void search() {
        try {
            String fileName = reader.readLine();
            List<Peer> files = fileList.get(fileName);
            if (files == null) {
                writer.println(Helper.NOT_FOUND);
                return;
            }
            String fileListToSend = "";
            Iterator<Peer> it = files.iterator();
            synchronized (files) {
                while (it.hasNext()) {
                    Peer peerFound = it.next();
                    fileListToSend += peerFound.toString() + Helper.SEPARATOR;
                }
            }
            writer.println(fileListToSend);
        } catch (IOException ex) {
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
