package me.lcproxy.jb.util;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.SneakyThrows;
import me.lcproxy.jb.Main;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class GenFromIndexFile {
    @Getter
    private static final Map<Integer, String[]> cosmetics = Maps.newHashMap();

    @SneakyThrows
    public static void load() {
        BufferedReader reader;
        try {

            InputStream file = getFileFromResourceAsStream("index.txt");
            reader = new BufferedReader(new InputStreamReader(file));
            String line = reader.readLine();

            ArrayList<String> usedNames = new ArrayList<>();
            int lineNum = 0;
            while (line != null) {
                String[] values = line.split(",");
                cosmetics.put(Integer.parseInt(values[0]), values);
                lineNum++;
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Loaded " + cosmetics.size() + " Cosmetics!");
    }

    @SneakyThrows
    public static File getFile(String file) {
        URL resource = GenFromIndexFile.class.getClassLoader().getResource(file);
        if (resource == null) {
            throw new IllegalArgumentException(file + " not found!");
        } else {

            // failed if files have whitespaces or special characters
            //return new File(resource.getFile());

            return new File(resource.toURI());
        }
    }

    private static InputStream getFileFromResourceAsStream(String fileName) {

        // The class loader that loaded the class
        ClassLoader classLoader = GenFromIndexFile.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        // the stream holding the file content
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        }

    }

    private static File getFileFromResource(String fileName) throws URISyntaxException {

        ClassLoader classLoader = Main.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {

            // failed if files have whitespaces or special characters
            //return new File(resource.getFile());

            return new File(resource.toURI());
        }

    }
}
