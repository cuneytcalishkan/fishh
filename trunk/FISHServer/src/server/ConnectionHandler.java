/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import model.Peer;
import model.FFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.StringTokenizer;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import util.HibernateUtil;
import util.Helper;

/**
 *
 * @author Cuneyt
 */
class ConnectionHandler implements Runnable {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String peerAddress;

    /**
     * Dedicated handler for the connected client. All of the requests and
     * responses from/to the client are handled here.
     *
     * @param socket the socket to communicate over.
     */
    public ConnectionHandler(Socket socket) {
        this.socket = socket;
    }

    /**
     * Reads the commands from the socket requested by the client and invokes the
     * necessary methods to handle these requests.
     */
    public void run() {
        boolean done = false;
        try {
            reader = Helper.getBufferedReader(socket);
            writer = Helper.getPrintWriter(socket);
            peerAddress = socket.getInetAddress().getHostAddress();
            String command = "";
            while (!done) {
                command = reader.readLine();
                if (command == null) {
                    throw new Exception("Connection has been closed by client.");
                }
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
        } catch (Exception ex) {
            unshare();
            System.out.println(ex);
        }

    }

    /**
     * Reads the information needed to create a Peer object and then reads the list
     * of files this Peer is going to share. The data is then stored persistently.
     */
    private void share() {
        unshare();
        Transaction t = null;
        Session session = null;
        try {
            session = HibernateUtil.getSession();
            t = session.beginTransaction();
            int sharePort = Integer.parseInt(reader.readLine());
            int pingPort = Integer.parseInt(reader.readLine());
            Peer peer = (Peer) session.createCriteria(Peer.class).
                    add(Restrictions.eq("address", peerAddress)).uniqueResult();
            if (peer == null) {
                peer = new Peer(peerAddress, sharePort, pingPort);
            }
            String fileName;
            while (((fileName = reader.readLine()) != null) && !fileName.equals(Helper.DONE)) {
                FFile file = (FFile) session.createCriteria(FFile.class).
                        add(Restrictions.eq("name", fileName.toLowerCase())).uniqueResult();
                if (file == null) {
                    file = new FFile(fileName);
                }
                if (!peer.getFileList().contains(file)) {
                    peer.addFile(file);
                }
                if (!file.getPeerList().contains(peer)) {
                    file.addPeer(peer);
                }
                session.persist(peer);
            }
            if (fileName == null) {
                t.rollback();
            } else {
                t.commit();
            }
        } catch (Exception ex) {
            t.rollback();
            System.out.println(ex);
        } finally {
            session.close();
        }
    }

    /**
     * Removes all the entries associated to this client
     */
    private void unshare() {

        Session session = null;
        Transaction t = null;
        try {
            session = HibernateUtil.getSession();
            Peer p = (Peer) session.createCriteria(Peer.class).
                    add(Restrictions.eq("address", peerAddress)).uniqueResult();
            if (p == null) {
                return;
            }
            t = session.beginTransaction();
            List<FFile> peerFiles = p.getFileList();
            for (FFile f : peerFiles) {
                f.getPeerList().remove(p);
                if (f.getPeerList().isEmpty()) {
                    session.delete(f);
                }
            }
            session.delete(p);
            t.commit();
        } catch (Exception ex) {
            t.rollback();
            System.out.println(ex);
        } finally {
            session.close();
        }

    }

    /**
     * Reads the file name to be searched from the socket's input stream reader
     * and creates a search criteria which allows multiple keyword search.
     * The search result may include more than one file and these results
     * are sent back to the client from the socket's output stream.
     * The result is sent as a single line where each entry is separated by
     * <code>Helper.SEPARATOR</code>
     * */
    private void search() {
        Session session = null;
        Transaction t = null;
        try {
            String fileName = reader.readLine();
            String criteria = "%";
            StringTokenizer st = new StringTokenizer(fileName);
            while (st.hasMoreTokens()) {
                criteria += st.nextToken() + "%";
            }
            session = HibernateUtil.getSession();
            t = session.beginTransaction();
            List<FFile> files = session.createCriteria(FFile.class).
                    add(Restrictions.like("name",
                    criteria, MatchMode.ANYWHERE)).list();
            if (files.isEmpty()) {
                writer.println(Helper.NOT_FOUND);
                return;
            }
            String result = "";
            for (FFile f : files) {
                List<Peer> fP = f.getPeerList();
                for (Peer peer : fP) {
                    result += f.getName() + "," + peer.toString() + Helper.SEPARATOR;
                }
            }
            writer.println(result);
            t.commit();
        } catch (IOException ex) {
            t.rollback();
            System.out.println(ex);
        } finally {
            session.close();
        }
    }
}
