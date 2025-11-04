
public class AppBase64 {
    public static void main(String[] args) {
        // Konversi string ke Base64 dengan
        // membaca file plain.txt dan simpan hasilnya di plain-ke-base64.txt
        try {
            java.nio.file.Path inputPath = java.nio.file.Paths.get("plain.txt");
            java.nio.file.Path outputPath = java.nio.file.Paths.get("plain-ke-base64.txt");

            // Baca semua byte dari file plain.txt
            byte[] fileBytes = java.nio.file.Files.readAllBytes(inputPath);

            // Encode byte ke Base64
            String base64Encoded = java.util.Base64.getEncoder().encodeToString(fileBytes);

            // Tulis hasil Base64 ke file plain-base64.txt
            java.nio.file.Files.write(outputPath, base64Encoded.getBytes());

            System.out.println("File berhasil dikonversi ke Base64 dan disimpan di plain-base64.txt");
        } catch (java.io.IOException e) {
            System.err.println("Terjadi kesalahan: " + e.getMessage());
        }

        // Konversi Base64 ke string dengan
        // membaca file base64.txt dan simpan hasilnya di base64-ke-plain.txt
        try {
            java.nio.file.Path inputPath = java.nio.file.Paths.get("base64.txt");
            java.nio.file.Path outputPath = java.nio.file.Paths.get("base64-ke-plain.txt");

            // Baca semua byte dari file base64.txt
            byte[] base64Bytes = java.nio.file.Files.readAllBytes(inputPath);

            // Decode Base64 ke byte asli
            byte[] decodedBytes = java.util.Base64.getDecoder().decode(new String(base64Bytes));

            // Tulis hasil decode ke file base64-ke-plain.txt
            java.nio.file.Files.write(outputPath, decodedBytes);

            System.out.println("File berhasil dikonversi dari Base64 dan disimpan di base64-ke-plain.txt");
        } catch (java.io.IOException e) {
            System.err.println("Terjadi kesalahan: " + e.getMessage());
        }
    }
}
