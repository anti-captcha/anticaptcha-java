package com.anti_captcha.Helper;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class StringHelper {
    private StringHelper() {
    }

    public static String toCamelCase(String s) {
        String[] parts = s.split("_");
        StringBuilder camelCase = new StringBuilder();

        for (String part : parts) {
            camelCase.append(part.substring(0, 1).toUpperCase())
                    .append(part.substring(1).toLowerCase());
        }

        return camelCase.substring(0, 1).toLowerCase() + camelCase.substring(1);
    }

    /**
     * Reads a file and encodes it in base64, or returns null when it cannot be read.
     */
    public static String imageFileToBase64String(String path) {
        try {
            return Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(path)));
        } catch (Exception e) {
            DebugHelper.out("Could not read " + path + ": " + e.getMessage(), DebugHelper.Type.ERROR);

            return null;
        }
    }

    /**
     * Encodes bytes in base64.
     */
    public static String toBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
