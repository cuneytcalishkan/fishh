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
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import util.Helper;

/**
 *
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
                System.out.println(fileName + " is not shared any more.");
                closeConnection();
                return;
            }
            long fileLength = Long.parseLong(reader.readLine());
            File save = new File(savePath);
            if (!save.isDirectory()) {
                save = save.getParentFile();
            }
            fileName = save.getAbsolutePath() + File.separator + fileName;
            InputStream isr = socket.getInputStream();
            OutputStream fos = new FileOutputStream(fileName);
            System.out.println("Saving file to " + fileName);
            System.out.println("Started downloading " + fileName + " from " + server + " on port " + port);
            byte[] buf = new byte[1024];
            while (isr.read(buf) != -1) {
                fos.write(buf);
            }
            fos.flush();
            fos.close();
            isr.close();
            closeConnection();
            System.out.println("Finished downloading " + fileName);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    private void closeConnection() throws IOException {
        reader.close();
        writer.close();
        socket.close();
    }
}
