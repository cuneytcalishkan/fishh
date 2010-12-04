/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import util.ByteStream;
import util.Helper;

/**
 * Downloads the specified file from the given server on the given port and saves
 * the downloaded file to the specified save path. If the save path is the same place
 * where the given client shares files, the server is called for an update of the
 * shared files.
 * @author CUNEYT
 */
public class FileDownloader implements Runnable {

    private String fileName;
    private String server;
    private int port;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String savePath;
    private InputStream isr;

    /**
     * Downloads the specified file from the given server on the given port and saves
     * the downloaded file to the specified save path. If the save path is the same place
     * where the given client shares files, the server is called for an update of the
     * shared files.
     * @param file the file name to download
     * @param server the server IP address
     * @param p the server's port number
     * @param savePath the path where to save the downloaded file
     */
    public FileDownloader(String file, String server, int p, String savePath) {
        this.fileName = file;
        this.server = server;
        this.port = p;
        this.savePath = savePath;
    }

    public void run() {
        try {
            socket = new Socket(server, port);
            reader = Helper.getBufferedReader(socket);
            writer = Helper.getPrintWriter(socket);
            writer.println(fileName);
            String found = reader.readLine();
            if (found.equals(Helper.NOT_FOUND)) {
                System.out.println("\n" + fileName + " is not shared.");
                return;
            }
            File save = new File(savePath);
            File[] files = save.listFiles();

            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.getName().equals(fileName)) {
                    file.delete();
                    break;
                }
            }
            fileName = save.getPath() + File.separator + fileName;
            isr = socket.getInputStream();
            System.out.println("\nDownloading file to " + fileName);
            ByteStream.toFile(isr, new File(fileName));
            System.out.println("\nFinished downloading " + fileName);
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            closeConnection();
        }
    }

    /**
     * Closes the necessary input and output stream readers and writers related to the connection or file.
     */
    private void closeConnection() {
        try {
            if (isr != null) {
                isr.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
