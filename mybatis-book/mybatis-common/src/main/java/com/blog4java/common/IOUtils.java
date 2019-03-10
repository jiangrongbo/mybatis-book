package com.blog4java.common;

import java.io.Closeable;
import java.io.IOException;

public abstract class IOUtils {

    public static void closeQuietly(Closeable closeable) {
        if(closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeQuietly(AutoCloseable closeable) {
        if(closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
