package br.rochards.item9;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class File {

    private static final int BUFFER_SIZE = 16;

    // try-finally
    public static void copy1(String src, String dst) throws IOException {
        var in = new FileInputStream(src);
        try {
            var out = new FileOutputStream(dst);

            try {
                var buf = new byte[BUFFER_SIZE];
                int n;
                while ((n = in.read(buf)) >= 0)
                    out.write(buf, 0, n);
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    // try-with-resources
    public static void copy2(String src, String dst) throws IOException {
        try (var in = new FileInputStream(src);
             var out = new FileOutputStream(dst)) {

            var buf = new byte[BUFFER_SIZE];
            int n;
            while ((n = in.read(buf)) >= 0) // in pode lançar IOException
                out.write(buf, 0, n); // out pode lançar IOException
        }
    }

    // try-with-resources with catch
    public static void copy3(String src, String dst) {
        try (var in = new FileInputStream(src);
             var out = new FileOutputStream(dst)) {

            var buf = new byte[BUFFER_SIZE];
            int n;
            while ((n = in.read(buf)) >= 0) // read pode lançar IOException
                out.write(buf, 0, n); // out pode lançar IOException
        } catch (IOException ignored) {
            // do something here if you wish
        }
    }
}
