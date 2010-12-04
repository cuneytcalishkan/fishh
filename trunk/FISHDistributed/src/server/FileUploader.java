/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import util.Helper;

/**
 * Uploads a requested file to the client over the socket if the requested file
 * exists or quits when the file is not found.
 * @author CUNEYT
 */
public class FileUploader implements Runnable {

    private Socket socket;
    private String path;
    private BufferedReader reader;
    private PrintWriter writer;
    private InputStream isr;
    private OutputStream osw;

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
            long length = 1;
            if (file == null) {
                writer.println(Helper.NOT_FOUND);
                closeConnection();
                return;
            }

            isr = new FileInputStream(file);
            osw = socket.getOutputStream();
            length = file.length();
            writer.println(Helper.OK);
            writer.println(length);
            System.out.println("\nUploading file " + fileName + " to " + socket.getInetAddress().getHostAddress());

            int ch;
            while ((ch = isr.read()) != -1) {
                osw.write(ch);
            }
            osw.flush();
            System.out.println("\nFinished uploading file " + fileName);
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
