/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import util.Helper;

/**
 *
 * @author CUNEYT
 */
public class FileUploader implements Runnable {

    private Socket socket;
    private String path;
    private BufferedReader reader;
    private PrintWriter writer;

    /**
     * Uploads a requested file to the client over the socket if the requested file
     * exists or quits when the file is not found.
     * @param socket the socket connected to the client
     * @param path the base path where to search for the requested file
     */
    public FileUploader(Socket socket, String path) {
        this.socket = socket;
        this.path = path;
    }

    public void run() {
        try {
            reader = Helper.getBufferedReader(socket);
            writer = Helper.getPrintWriter(socket);
            String fileName = reader.readLine();
            File file = Helper.getFile(path, fileName);
            if (file == null) {
                writer.println(Helper.NOT_FOUND);
                closeConnection();
                return;
            } else {
                writer.println(Helper.OK);
                writer.println(file.length());
            }
            FileInputStream fis = new FileInputStream(file);
            OutputStream os = socket.getOutputStream();

            System.out.println("Started uploading file " + fileName + " to " + socket.getInetAddress().getHostAddress());
            byte[] buf = new byte[1024];
            while (fis.read(buf) != -1) {
                os.write(buf);
            }
            fis.close();
            os.flush();
            os.close();
            closeConnection();
            System.out.println("Finished uploading file " + fileName);
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
