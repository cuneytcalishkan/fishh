/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.net.MulticastSocket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 *
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

    public Client(MulticastSocket ms, String savePath, int responsePort, int rTimeout, int mcastPort, String mcastAddress) {
        this.ms = ms;
        this.savePath = savePath;
        this.responsePort = responsePort;
        this.responseTimeout = rTimeout;
        this.mcastPort = mcastPort;
        this.mcastAddress = mcastAddress;
        this.executor = Executors.newCachedThreadPool();
    }

    public void download(String fileName, String server, int port) {
        executor.execute(new FileDownloader(fileName, server, port, savePath));
    }

    public void search(String fileName) {
        executor.execute(new Search(ms, responsePort, responseTimeout, fileName, mcastPort, mcastAddress));
    }
}
