/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import util.Helper;

/**
 *
 * @author CUNEYT
 */
public class ClientTest {

    private Client client;

    public ClientTest() {
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
    // @Test
    // public void hello() {}
    @Test
    public void testClientFileDiscovery() {
        try {
            client = new Client("D:\\COURSES\\KTH\\2010-2011_Fall\\Network Programming With Java\\Homework\\HW1", "localhost", 8088);
            File f = Helper.getFile("D:\\COURSES\\KTH\\2010-2011_Fall\\Network Programming With Java\\Homework\\HW1", "menu.htm");
            System.out.println(f);
        } catch (UnknownHostException ex) {
            Logger.getLogger(ClientTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
