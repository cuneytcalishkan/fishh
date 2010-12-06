/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.net.Socket;
import java.util.List;
import java.util.TimerTask;
import model.FFile;
import model.Peer;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

/**
 * The instances of this class are scheduled for being executed in a specified
 * time period. Pings all of the Peers in the system to check whther they are
 * alive or not. If a Peer does not respond, all of its entries are removed
 * from the system in order to keep the entries consistent.
 * @author CUNEYT
 */
public class Pinger extends TimerTask {

    public void run() {

        Session session = HibernateUtil.getSession();
        Transaction t = null;
        List<Peer> peers = session.createCriteria(Peer.class).list();
        for (Peer peer : peers) {
            try {
                System.out.println("ping: " + peer.getAddress() + " on " + peer.getPingPort());
                Socket s = new Socket(peer.getAddress(), peer.getPingPort());
                s.close();
            } catch (Exception ex) {
                try {
                    t = session.beginTransaction();
                    List<FFile> peerFiles = peer.getFileList();
                    for (FFile f : peerFiles) {
                        f.getPeerList().remove(peer);
                        if (f.getPeerList().isEmpty()) {
                            session.delete(f);
                        }
                    }
                    session.delete(peer);
                    t.commit();
                    session.close();
                } catch (Exception e) {
                    t.rollback();
                    System.out.println(e);
                }
                System.out.println(ex);
            }
        }

    }
}
