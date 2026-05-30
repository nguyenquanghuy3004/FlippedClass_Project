package com.example.flippedclass.util;

public final class VideoUrlNormalizer {

    private VideoUrlNormalizer() {
    }

    /**
     * Chuẩn hóa URL video local: luôn trả về dạng /uploads/videos/{file}.mp4
     * Link http/https (YouTube, ...) giữ nguyên.
     */
    public static String normalize(String url) {
        if (url == null || url.isBlank()) {
            return url;
        }

        String normalized = url.trim();

        if (normalized.startsWith("http://") || normalized.startsWith("https://")) {
            String pdfFromHttp = extractPdfPathIfPresent(normalized);
            if (pdfFromHttp != null) {
                return pdfFromHttp;
            }
            return normalized;
        }

        String pdfFixed = extractPdfPathIfPresent(normalized);
        if (pdfFixed != null) {
            return pdfFixed;
        }

        if (normalized.matches("(?i)^/?uploads/pdfs/.+")) {
            return normalized.startsWith("/") ? normalized : "/" + normalized;
        }

        // Sửa lỗi FE ghép sai: uploads/videos + uuid.mp4 (thiếu /)
        if (normalized.matches("(?i)^uploads/videos[^/].+")) {
            normalized = "/uploads/videos/" + normalized.substring("uploads/videos".length());
        } else if (normalized.startsWith("uploads/")) {
            normalized = "/" + normalized;
        } else if (!normalized.startsWith("/uploads/videos/")) {
            normalized = "/uploads/videos/" + normalized.replaceFirst("^/+", "");
        }

        return normalized;
    }

    /** Lấy /uploads/pdfs/... nếu URL bị lồng nhầm trong /uploads/videos/... */
    private static String extractPdfPathIfPresent(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        int idx = path.toLowerCase().indexOf("/uploads/pdfs/");
        if (idx < 0) {
            return null;
        }
        String relative = path.substring(idx);
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return relative;
        }
        return relative.startsWith("/") ? relative : "/" + relative;
    }

    public static String toFullUrl(String baseUrl, String url) {
        if (url == null || url.isBlank()) {
            return url;
        }
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }
        String base = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        return base + url;
    }
}
