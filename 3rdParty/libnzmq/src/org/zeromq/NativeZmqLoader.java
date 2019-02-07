package org.zeromq;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Loads (i.e. {@link System#load(String)} the native libraries that {@link ZMQ} binds to.
 */
public class NativeZmqLoader {
    public static final String NO_EMBEDDED_LIB_FLAG = "ZMQ_NO_EMBEDDED";
    public static boolean LOADED_EMBEDDED_LIBRARY = false;

    /**
     * Load native libs for ZMQ, unless:
     *   (1) this class has already loaded it once successfully or 
     *   (2) {@link NO_EMBEDDED_LIB_FLAG} is set 
     * 
     */
    public void load() {
        if(!LOADED_EMBEDDED_LIBRARY && System.getProperty(NO_EMBEDDED_LIB_FLAG) == null) {
            LOADED_EMBEDDED_LIBRARY = loadNativeEmbedded("/native/linux/zmq/libjzmq.so")
                && loadNativeEmbedded("/native/linux/zmq/libzmq.so.5");
        } 
    }

    private boolean loadNativeEmbedded(String resourceName) {
        try (InputStream is = App.class.getResourceAsStream(resourceName)) {
            if(is == null) {
                // should hook this up to a log4j that can be configured by the top-level program
                return false;
            } else {
                System.load(streamToTempFile(is, tempNameForResourceName(resourceName)));
            }
            return true;
        } catch (IOException ioe) {
            // should hook this up to a log4j that can be configured by the top-level program
            return false;
        }
    }

    /**
     * Return the last part of the resourceName, where "part" is each part of the
     * resource name separated by either the / character or . character.
     *
     * @param resourceName resource name
     * @return last part of the resource name
     */
    private String tempNameForResourceName(String resourceName) {
        resourceName.replace(".", "/");
        String[] resourceParts = resourceName.split("/");
        return resourceParts[resourceParts.length - 1];
    }

    /**
     * Save input stream into a temp file
     *
     * @param in input stream
     * @param filename temp output filename
     * @return path to the file
     * @throws IOException if IO error
     */
    private String streamToTempFile(InputStream in, String filename) throws IOException {
        final File libfile = File.createTempFile(filename, "");

        try (
            final OutputStream out = new BufferedOutputStream(new FileOutputStream(libfile));
        ) {
            int len = 0;
            byte[] buffer = new byte[8192];
            while ((len = in.read(buffer)) > -1)
                out.write(buffer, 0, len);
            out.close();
            in.close();
        }

        return libfile.getAbsolutePath();
    }
}