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
    private InputStream isr;
    private FileOutputStream fos;

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
            String result = reader.readLine();
            if (result.equals(Helper.NOT_FOUND)) {
                System.out.println("\n" + fileName + " is not shared any more.");
                closeConnection();
                return;
            }
            long fileLength = Long.parseLong(reader.readLine());
            File save = new File(savePath);
            if (!save.isDirectory()) {
                save = save.getParentFile();
            }
            fileName = save.getAbsolutePath() + File.separator + fileName;
            isr = socket.getInputStream();
            fos = new FileOutputStream(fileName);
            System.out.println("\nSaving file to " + fileName);
            int ch;
            while ((ch = isr.read()) != -1) {
                fos.write(ch);
            }
            fos.flush();
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
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (socket != null) {
                socket.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (isr != null) {
                isr.close();
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
