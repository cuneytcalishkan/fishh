/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author CUNEYT
 *
 * Includes utility methods like file searching, recursive file discovery, creating
 * reader and writers of a given socket, converting a given list to a string and
 * the communication protocol keywords sent & received between clients and server.
 *
 */
public class Helper {

    /**
     *
     */
    public static final String SHARE = "share";
    /**
     *
     */
    public static final String UNSHARE = "unshare";
    /**
     *
     */
    public static final String SEARCH = "search";
    /**
     *
     */
    public static final String UNKNOWN = "unknown command";
    /**
     * 
     */
    public static final String NOT_FOUND = "not-found";
    /**
     *
     */
    public static final String SEPARATOR = "*";
    /**
     *
     */
    public static final String DONE = "done";
    /**
     *
     */
    public static final String OK = "ok";
    /**
     *
     */
    public static final String DOWNLOAD = "download";
    /**
     *
     */
    public static final String INFORM = "inform";

    /**
     * Returns a buffered reader for the given socket object.
     * @param s The socket object
     * @return the input stream as a BufferedReader of the given socket object
     * @throws IOException
     */
    public static BufferedReader getBufferedReader(Socket s) throws IOException {
        return new BufferedReader(new InputStreamReader(s.getInputStream()));
    }

    /**
     * Returns a <code>PrintWriter</code> for the given socket object.
     * @param s The socket object
     * @return the output stream as a PrintWriter of the given socket object
     * @throws IOException
     */
    public static PrintWriter getPrintWriter(Socket s) throws IOException {
        return (new PrintWriter(s.getOutputStream(), true));
    }

    /**
     *Searches for the given file name in the given path in a recursive path.
     * @param path the base path to search for
     * @param fileName the file name to search for
     * @return the file object if found, null otherwise
     * @throws FileNotFoundException
     */
    public static File getFile(String path, String fileName) throws FileNotFoundException {
        File result = null;
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        String[] files = file.list();
        for (int i = 0; i < files.length; i++) {
            File f = new File(path + File.separator + files[i]);
            if (f.isDirectory()) {
                result = getFile(path + File.separator + f.getName(), fileName);
                if (result != null) {
                    return result;
                }
            } else {
                if (f.getName().equalsIgnoreCase(fileName)) {
                    result = f;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Searches and discovers the files recursively starting from the given path
     * and stores the files in the given List
     * @param path the path to start file discovery
     * @param result the List where the discovered objects are stored
     * @throws FileNotFoundException
     */
    public static void discoverFiles(String path, List<Object> result) throws FileNotFoundException {
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        String[] files = file.list();
        for (int i = 0; i < files.length; i++) {
            File f = new File(path + File.separator + files[i]);
            if (f.isDirectory()) {
                discoverFiles(path + File.separator + f.getName(), result);
            } else {
                if (!result.contains(f.getName())) {
                    result.add(f.getName());
                }
            }
        }
    }

    /**
     * Creates a string from a given List of objects by separating each entry
     * with <code>SEPARATOR</code>
     * @param list the list of items
     * @return the string version of the list separated with <code>SEPARATOR</code>
     */
    public static String listToString(List<Object> list) {
        StringBuilder sb = new StringBuilder();
        for (Iterator<Object> it = list.iterator(); it.hasNext();) {
            Object obj = it.next();
            sb.append(obj.toString());
            sb.append(SEPARATOR);
        }
        String result = sb.toString();
        return result.substring(0, result.length() - 1);
    }
}
