package org.delcom.starter.controllers;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    // Helper untuk format desimal, digunakan oleh perolehanNilai
    private static final DecimalFormat df = createDecimalFormat();

    private static DecimalFormat createDecimalFormat() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        return new DecimalFormat("0.00", symbols);
    }
    
    // Helper untuk decode Base64, digunakan oleh 3 dari 4 method utama
    private String decodeBase64(String strBase64) {
        try {
            byte[] bytes = Base64.getDecoder().decode(strBase64);
            return new String(bytes);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Input Base64 tidak valid.");
        }
    }

    /**
     * 1. Informasi NIM
     */
    @GetMapping("/informasi-nim")
    public String informasiNim(@RequestParam String nim) {
        if (nim == null || nim.length() < 8) {
            return "NIM tidak valid: minimal 8 karakter.";
        }

        String prefix = nim.substring(0, 3);
        String angkatan = "20" + nim.substring(3, 5);
        String lastThree = nim.substring(nim.length() - 3);
        int urutan = Integer.parseInt(lastThree);

        Map<String, String> prodiMap = new HashMap<>();
        prodiMap.put("11S", "Sarjana Informatika");
        prodiMap.put("12S", "Sarjana Sistem Informasi");
        prodiMap.put("14S", "Sarjana Teknik Elektro");
        // Tambahkan prodi lain jika perlu

        String prodi = prodiMap.getOrDefault(prefix, "Unknown");

        return String.format("Informasi NIM %s:\n>> Program Studi: %s\n>> Angkatan: %s\n>> Urutan: %d",
                nim, prodi, angkatan, urutan);
    }

    /**
     * 2. Perolehan Nilai
     */
    @GetMapping("/perolehan-nilai")
    public String perolehanNilai(@RequestParam String strBase64) {
        String data = decodeBase64(strBase64);
        String[] lines = data.split("\n");

        double totalNilai = 0.0;
        int totalBobot = 0;

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.equals("---")) continue;

            if (line.contains("|")) {
                String[] parts = line.split("\\|", 3);
                if (parts.length == 3) {
                    try {
                        double nilai = Double.parseDouble(parts[1].trim());
                        int bobot = Integer.parseInt(parts[2].trim());
                        if (bobot > 0) {
                            totalNilai += nilai * (bobot / 100.0);
                            totalBobot += bobot;
                        }
                    } catch (NumberFormatException ignored) {
                        // Abaikan baris yang format angkanya salah
                    }
                }
            }
        }

        String grade = calculateGrade(totalNilai);
        return String.format("Nilai Akhir: %s (Total Bobot: %d%%)\nGrade: %s",
                df.format(totalNilai), totalBobot, grade);
    }

    // Helper untuk perolehanNilai
    private String calculateGrade(double nilai) {
        if (nilai >= 85) return "A";
        if (nilai >= 75) return "B";
        if (nilai >= 65) return "C";
        if (nilai >= 55) return "D";
        return "E";
    }

    /**
     * 3. Perbedaan L dan Kebalikannya
     */
    @GetMapping("/perbedaan-l")
    public String perbedaanL(@RequestParam String strBase64) {
        String path = decodeBase64(strBase64).trim();
        int[] end1 = calculateEndPoint(path);
        String opposite = reversePath(path);
        int[] end2 = calculateEndPoint(opposite);

        int distance = Math.abs(end1[0] - end2[0]) + Math.abs(end1[1] - end2[1]);

        return String.format("Path Original: %s -> (%d, %d)\nPath Kebalikan: %s -> (%d, %d)\nPerbedaan Jarak: %d",
                path, end1[0], end1[1], opposite, end2[0], end2[1], distance);
    }
    
    // Helper untuk perbedaanL
    private int[] calculateEndPoint(String path) {
        int x = 0, y = 0;
        for (char c : path.toCharArray()) {
            switch (c) {
                case 'U': y++; break;
                case 'D': y--; break;
                case 'L': x--; break;
                case 'R': x++; break;
            }
        }
        return new int[]{x, y};
    }

    // Helper untuk perbedaanL
    private String reversePath(String path) {
        StringBuilder sb = new StringBuilder();
        for (char c : path.toCharArray()) {
            switch (c) {
                case 'U': sb.append('D'); break;
                case 'D': sb.append('U'); break;
                case 'L': sb.append('R'); break;
                case 'R': sb.append('L'); break;
            }
        }
        return sb.toString();
    }

    /**
     * 4. Paling Ter
     */
    @GetMapping("/paling-ter")
    public String palingTer(@RequestParam String strBase64) {
        String text = decodeBase64(strBase64);
        Map<String, Integer> freq = new HashMap<>();
        String[] words = text.toLowerCase().split("\\W+");

        for (String word : words) {
            if (!word.isEmpty() && word.startsWith("ter")) {
                freq.put(word, freq.getOrDefault(word, 0) + 1);
            }
        }

        if (freq.isEmpty()) {
            return "Tidak ditemukan kata yang berawalan 'ter'.";
        }

        String topWord = "";
        int maxCount = 0;
        for (Map.Entry<String, Integer> e : freq.entrySet()) {
            if (e.getValue() > maxCount) {
                maxCount = e.getValue();
                topWord = e.getKey();
            }
        }

        return String.format("Kata 'ter' yang paling sering muncul adalah '%s' (muncul %d kali).", topWord, maxCount);
    }
}