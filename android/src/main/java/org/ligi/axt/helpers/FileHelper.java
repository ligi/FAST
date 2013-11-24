package org.ligi.axt.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class FileHelper {
    private final File file;

    public FileHelper(File file) {
        this.file = file;
    }

    /**
     * reads a file to a string
     *
     * @return the content of the file
     * @throws IOException
     */
    public String loadToString() throws IOException {
        return loadToString(Charset.defaultCharset());
    }

    public String loadToString(Charset charset) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        FileChannel fc = stream.getChannel();
        MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
        fc.close();
        stream.close();
        return charset.decode(bb).toString();
    }

    public boolean writeString(String string) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(string);
            fileWriter.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean deleteRecursive() {
        return deleteRecursive(file);
    }

    public boolean deleteRecursive(File file2delete) {
        if (!file2delete.isDirectory()) {
            return false;
        }

        for (String child:file2delete.list()) {
            File temp = new File(file2delete, child);
            if (temp.isDirectory()) {
                deleteRecursive(temp);
            } else {
                temp.delete();
            }
        }

        return file2delete.delete();

    }
}
