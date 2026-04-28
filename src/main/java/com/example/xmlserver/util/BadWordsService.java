package com.example.xmlserver.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class BadWordsService {

    private static final Set<String> BAD_WORDS = new HashSet<>();

    static {
        load();
    }

    private static void load() {
        try {
            InputStream is = BadWordsService.class
                    .getClassLoader()
                    .getResourceAsStream("badwords.txt");

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = reader.readLine()) != null) {
                BAD_WORDS.add(line.trim().toLowerCase());
            }

            System.out.println("Loaded bad words: " + BAD_WORDS);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load bad words", e);
        }
    }

    public static boolean containsBadWords(String text) {
        if (text == null) return false;

        String lower = text.toLowerCase();

        return BAD_WORDS.stream()
                .anyMatch(lower::contains);
    }
}
