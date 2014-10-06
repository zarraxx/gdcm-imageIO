package com.oritsh.nativeUtils;

/**
 * Created by zarra on 14-10-4.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class NativeUtils {

    /**
     * Private constructor - this class will never be instanced
     */
    private NativeUtils() {
    }

    public static void loadLibraryFromJar(String path) throws IOException{
        loadLibraryFromJar(path,null);
    }


    public static void loadLibraryFromJar(String path,Class<?> clazz) throws IOException {

        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("The path to be absolute (start with '/').");
        }

// Obtain filename from path
        String[] parts = path.split("/");
        String filename = (parts.length > 1) ? parts[parts.length - 1] : null;

// Split filename to prexif and suffix (extension)
        String prefix = "";
        String suffix = null;
        if (filename != null) {
            parts = filename.split("\\.", 2);
            prefix = parts[0];
            suffix = (parts.length > 1) ? "."+parts[parts.length - 1] : null; // Thanks, davs! :-)
        }

// Check if the filename is okay
        if (filename == null || prefix.length() < 3) {
            throw new IllegalArgumentException("The filename has to be at least 3 characters long.");
        }

// Prepare temporary file
        File temp = File.createTempFile(prefix, suffix);
        temp.deleteOnExit();

        if (!temp.exists()) {
            throw new FileNotFoundException("File " + temp.getAbsolutePath() + " does not exist.");
        }

// Prepare buffer for data copying
        byte[] buffer = new byte[1024];
        int readBytes;

// Open and check input stream
        if (clazz == null)
            clazz = NativeUtils.class;
        InputStream is = clazz.getResourceAsStream(path);
        if (is == null) {
            throw new FileNotFoundException("File " + path + " was not found inside JAR.");
        }

// Open output stream and copy data between source file in JAR and the temporary file
        OutputStream os = new FileOutputStream(temp);
        try {
            while ((readBytes = is.read(buffer)) != -1) {
                os.write(buffer, 0, readBytes);
            }
        } finally {
// If read/write fails, close streams safely before throwing an exception
            os.close();
            is.close();
        }

// Finally, load the library
        System.load(temp.getAbsolutePath());
    }

    public static void load(String library,Class<?> clazz) throws IOException {
        String realFilename = library;
        if (OSInfo.isMacOSX()){
            realFilename = String.format("lib%s.dylib",library);
        }
        else if(OSInfo.isLinux()){
            realFilename = String.format("lib%s.so",library);
        }
        else if(OSInfo.isWindows()){
            realFilename = String.format("lib%s.dll",library);
        }

        loadLibraryFromJar("/lib/"+realFilename,clazz);
    }

    public static void load(String library) throws IOException {
         load(library,null);
    }
}