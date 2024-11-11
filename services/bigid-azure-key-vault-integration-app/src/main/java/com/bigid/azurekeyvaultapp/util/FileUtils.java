package com.bigid.azurekeyvaultapp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Service
public class FileUtils {
    static Logger logger = LoggerFactory.getLogger(FileUtils.class);

    private FileUtils() {
    }

    public static String readFileContentFromInputStream(InputStream inputStream) {
        StringBuilder contentBuilder = new StringBuilder();
        try {
            InputStreamReader isReader = new InputStreamReader(inputStream);

            BufferedReader reader = new BufferedReader(isReader);
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line);
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }

        return contentBuilder.toString();
    }
}
