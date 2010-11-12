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
 */
public class Helper {

    public static final String SHARE = "share";
    public static final String UNSHARE = "unshare";
    public static final String SEARCH = "search";
    public static final String UNKNOWN = "unknown command";
    public static final String NOT_FOUND = "not-found";
    public static final String SEPARATOR = "*";

    public static BufferedReader getBufferedReader(Socket s) throws IOException {
        return new BufferedReader(new InputStreamReader(s.getInputStream()));
    }

    public static PrintWriter getPrintWriter(Socket s) throws IOException {
        return (new PrintWriter(s.getOutputStream(), true));
    }

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
            } else {
                if (f.getName().equalsIgnoreCase(fileName)) {
                    result = f;
                    break;
                }
            }
        }
        return result;
    }

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
                result.add(f.getName());
            }
        }
    }

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
