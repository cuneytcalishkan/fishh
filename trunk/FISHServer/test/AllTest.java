/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.List;
import model.FFile;
import model.Peer;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import util.HibernateUtil;

/**
 *
 * @author CUNEYT
 */
public class AllTest {

    private FFile file;
    private Peer peer;

    public AllTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void createFileTest() {

        Session session = HibernateUtil.getSession();
        Transaction ta = session.beginTransaction();
        file = new FFile("test1.txt");
        session.persist(file);
        file = new FFile("test2.txt");
        session.persist(file);
        file = new FFile("test3.txt");
        session.persist(file);
        ta.commit();
        session.close();
        file = null;
    }

    @Test
    public void listFilesTest() {
        Session session = HibernateUtil.getSession();
        Transaction ta = session.beginTransaction();

        List<FFile> files = session.createCriteria(FFile.class).list();
        for (FFile fil : files) {
            System.out.println(fil.toString());
        }
        ta.commit();
        session.close();
    }

    @Test
    public void createPeerTest() {
        Session session = HibernateUtil.getSession();
        Transaction ta = session.beginTransaction();
        peer = new Peer("127.0.0.2", 8088, 8089);
        session.saveOrUpdate(peer);
        peer = new Peer("192.168.1.2", 8088, 8089);
        session.saveOrUpdate(peer);
        peer = new Peer("192.168.1.3", 8088, 8089);
        session.saveOrUpdate(peer);
        ta.commit();
        session.close();
    }

    @Test
    public void listPeersTest() {
        Session session = HibernateUtil.getSession();
        Transaction ta = session.beginTransaction();
        List<Peer> peers = session.createCriteria(Peer.class).list();
        for (Peer pe : peers) {
            System.out.println(pe.toString());
        }
        ta.commit();
        session.close();
    }

    @Test
    public void addPeersToFiles() {
        Session session = HibernateUtil.getSession();
        Transaction ta = session.beginTransaction();
        List<Peer> peers = session.createCriteria(Peer.class).list();
        List<FFile> files = session.createCriteria(FFile.class).list();


        Peer p = peers.get(0);
        p.addFile(files.get(0));
        p.addFile(files.get(2));
        session.persist(p);
        p = peers.get(1);
        p.addFile(files.get(1));
        p.addFile(files.get(2));
        session.persist(p);
        p = peers.get(2);
        //p.addFile(files.get(0));
        p.addFile(files.get(1));
        session.persist(p);
        ta.commit();
        session.close();
    }

    @Test
    public void deletePeers() {
        Session session = HibernateUtil.getSession();
        Transaction ta = session.beginTransaction();
        List<Peer> peers = session.createCriteria(Peer.class).list();
        Peer p = peers.get(0);
        List<FFile> peerFiles = p.getFileList();
        for (FFile f : peerFiles) {
            f.getPeerList().remove(p);
            if (f.getPeerList().isEmpty()) {
                session.delete(f);
            }
        }
        session.delete(p);
        ta.commit();
        session.close();
    }
}
