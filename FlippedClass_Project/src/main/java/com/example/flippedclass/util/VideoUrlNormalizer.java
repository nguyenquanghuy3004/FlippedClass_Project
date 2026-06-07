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
            return normalized;
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
