/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
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
    private FileOutputStream osw;
    private InputStream isr;
    private Client c;

    /**
     * Downloads the specified file from the given server on the given port and saves
     * the downloaded file to the specified save path. If the save path is the same place
     * where the given client shares files, the server is called for an update of the
     * shared files.
     * @param file the file name to download
     * @param server the server IP address
     * @param p the server's port number
     * @param savePath the path where to save the downloaded file
     * @param client the <code>Client</code> that downloads the file
     */
    public FileDownloader(String file, String server, int p, String savePath, Client client) {
        this.fileName = file;
        this.server = server;
        this.port = p;
        this.savePath = savePath;
        this.c = client;
    }

    public void run() {
        try {
            socket = new Socket(server, port);
            reader = Helper.getBufferedReader(socket);
            writer = Helper.getPrintWriter(socket);
            writer.println(fileName);
            String found = reader.readLine();
            if (found.equals(Helper.NOT_FOUND)) {
                System.out.println("\n" + fileName + " is not shared any more.");
                closeConnection();
                return;
            }
            long length = Long.parseLong(reader.readLine());
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
            osw = new FileOutputStream(fileName);
            isr = socket.getInputStream();
            System.out.println("\nDownloading file to " + fileName);

            int ch;
            System.out.println(length);
            while ((ch = isr.read()) != -1) {
                osw.write(ch);
                osw.flush();
                length--;
            }
            System.out.println(length);
            System.out.println("\nFinished downloading " + fileName
                    + "," + (length == 0 ? "successfully." : "incomplete."));
            writer.println();
            reader.readLine();
            if (savePath.equals(c.getBasePath())) {
                c.share();
            }
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
            if (osw != null) {
                osw.flush();
                osw.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
