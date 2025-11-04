package org.delcom.starter.controllers;

import java.lang.reflect.Method;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HomeControllerTest {

    private HomeController controller;

    @BeforeEach
    void setUp() {
        controller = new HomeController();
    }

    // 1. Tes untuk informasiNim
    @Test
    void testInformasiNim_AllCases() {
        // Kasus valid
        String resultValid = controller.informasiNim("11S23001");
        assertTrue(resultValid.contains("Sarjana Informatika") && resultValid.contains("Angkatan: 2023"));

        // Kasus tidak valid (terlalu pendek atau null)
        assertTrue(controller.informasiNim("123").contains("minimal 8 karakter"));
        assertTrue(controller.informasiNim(null).contains("minimal 8 karakter"));

        // Kasus prodi tidak diketahui
        assertTrue(controller.informasiNim("99X23123").contains("Unknown"));
    }

    // 2. Tes untuk perolehanNilai
    @Test
    void testPerolehanNilai_Valid() {
        String data = "UAS|85|40\nUTS|75|30\nPA|90|20\nK|100|10";
        String b64 = Base64.getEncoder().encodeToString(data.getBytes());
        String result = controller.perolehanNilai(b64);
        assertTrue(result.contains("84.50") && result.contains("Grade: B"));
    }

    /**
     * TEST TAMBAHAN UNTUK MENGHILANGKAN BARIS KUNING DI perolehanNilai
     * Mencakup semua kondisi if di dalam loop.
     */
    @Test
    void testPerolehanNilai_FullBranchCoverage() {
        String data = 
            "UAS|90|50\n" +         // Baris valid
            "\n" +                  // Baris kosong -> (mencakup line.isEmpty())
            "Tugas|80|0\n" +        // Bobot 0 -> (mencakup else dari if (bobot > 0))
            "Invalid Line\n" +      // Baris tanpa '|' -> (mencakup else dari if (line.contains("|")))
            "Hanya|Dua\n" +         // Format salah (2 bagian) -> (mencakup else dari if (parts.length == 3))
            "Nilai|abc|def\n" +     // Format angka salah (NumberFormatException)
            "---\n" +               // Break loop
            "Ini|tidak|dihitung";
            
        String b64 = Base64.getEncoder().encodeToString(data.getBytes());
        String result = controller.perolehanNilai(b64);
        
        // Hanya baris pertama yang dihitung (90 * 50% = 45.00)
        assertEquals("Nilai Akhir: 45.00 (Total Bobot: 50%)\nGrade: E", result);
    }
    
    @Test
    void testPerolehanNilai_InvalidBase64() {
        assertThrows(IllegalArgumentException.class, () -> controller.perolehanNilai("!@#"));
    }
    

    // 3. Tes untuk perbedaanL
    @Test
    void testPerbedaanL_AllCases() {
        // Kasus valid
        String b64Valid = Base64.getEncoder().encodeToString("UULL".getBytes());
        assertTrue(controller.perbedaanL(b64Valid).contains("Perbedaan Jarak: 8"));

        // Kasus dengan karakter tidak valid (untuk coverage switch-case)
        String b64InvalidChar = Base64.getEncoder().encodeToString("U R D L X Y Z".getBytes());
        assertTrue(controller.perbedaanL(b64InvalidChar).contains("Perbedaan Jarak: 0"));
    }

    // 4. Tes untuk palingTer
    @Test
    void testPalingTer_Valid() {
        String text = "terbaik terbaik termahal";
        String b64 = Base64.getEncoder().encodeToString(text.getBytes());
        assertTrue(controller.palingTer(b64).contains("'terbaik' (muncul 2 kali)"));
    }

    @Test
    void testPalingTer_FullBranchCoverage() {
        // Kasus 1: Tidak ditemukan kata 'ter' -> mencakup if (freq.isEmpty())
        String noTer = Base64.getEncoder().encodeToString("hello world".getBytes());
        assertEquals("Tidak ditemukan kata yang berawalan 'ter'.", controller.palingTer(noTer));

        // Kasus 2: Teks dengan spasi ganda -> mencakup if (!word.isEmpty())
        String doubleSpace = Base64.getEncoder().encodeToString("tercepat  terlambat".getBytes());
        assertTrue(controller.palingTer(doubleSpace).contains("muncul 1 kali"));
        
        // Kasus 3: Beberapa kata 'ter' dengan frekuensi berbeda
        String multiple = "terbaik terendah terbaik terburuk terendah terbaik";
        String b64Multiple = Base64.getEncoder().encodeToString(multiple.getBytes());
        assertTrue(controller.palingTer(b64Multiple).contains("'terbaik' (muncul 3 kali)"));
    }

    @Test
    void testPalingTer_SingleWordTer() {
        // Kasus khusus: hanya satu kata "ter"
        String singleTer = Base64.getEncoder().encodeToString("ter".getBytes());
        String result = controller.palingTer(singleTer);

        // Pastikan hasil sesuai dengan kondisi frekuensi tunggal
        assertTrue(result.contains("'ter' (muncul 1 kali)"));
    }

    // Tes untuk helper method (calculateGrade) untuk memastikan 100%
    @Test
    void testCalculateGrade_Coverage() throws Exception {
        Method method = HomeController.class.getDeclaredMethod("calculateGrade", double.class);
        method.setAccessible(true);
        assertEquals("A", method.invoke(controller, 90.0));
        assertEquals("B", method.invoke(controller, 80.0));
        assertEquals("C", method.invoke(controller, 70.0));
        assertEquals("D", method.invoke(controller, 60.0));
        assertEquals("E", method.invoke(controller, 50.0));
    }

   @Test
    void testPalingTer_WithEmptyStringInWords() {
        // Ada spasi ganda supaya muncul word kosong ("")
        String text = "terbaik  "; // <- ada 2 spasi di akhir
        String b64 = Base64.getEncoder().encodeToString(text.getBytes());
        String result = controller.palingTer(b64);

        assertTrue(result.contains("'terbaik' (muncul 1 kali)"));
    }

}
