import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
 
public class FirebaseConfig {
 
    private static final String API_KEY = "AIzaSyBP0Cgw3Y3NGtysh00DgO0XiCcLxlwGLrE";
 
    private static final String SIGN_IN_URL =
        "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + API_KEY;
 
    public static class AuthResult {
        public final boolean success;
        public final String  message;  
        public final String  idToken;  
 
        AuthResult(boolean success, String message, String idToken) {
            this.success = success;
            this.message = message;
            this.idToken = idToken;
        }
    }
 
    public static AuthResult signIn(String email, String password) {
        try {
            // Build JSON body
            String body = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}",
                escapeJson(email), escapeJson(password)
            );
 
            // Open connection
            URL url = new URL(SIGN_IN_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
 
            // Write body
            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }
 
            int status = conn.getResponseCode();
            InputStream is = (status == 200) ? conn.getInputStream() : conn.getErrorStream();
            String response = readAll(is);
 
            if (status == 200) {
                // Parse idToken and email from JSON response (no external lib needed)
                String idToken    = extractJson(response, "idToken");
                String userEmail  = extractJson(response, "email");
                return new AuthResult(true, userEmail, idToken);
            } else {
                // Parse error message
                String errMsg = extractJson(response, "message");
                return new AuthResult(false, friendlyError(errMsg), null);
            }
 
        } catch (Exception e) {
            return new AuthResult(false, "Connection error: " + e.getMessage(), null);
        }
    }
 
    // ── Helpers ───────────────────────────────────────────────────────
 
    private static String readAll(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }
 
    /** Minimal JSON value extractor — no external library needed. */
    private static String extractJson(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start == -1) return "";
        start += search.length();
        int end = json.indexOf("\"", start);
        return end == -1 ? "" : json.substring(start, end);
    }
 
    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
 
    private static String friendlyError(String firebaseMsg) {
        if (firebaseMsg == null) return "Unknown error.";
        return switch (firebaseMsg) {
            case "EMAIL_NOT_FOUND"      -> "No account found with that email.";
            case "INVALID_PASSWORD"     -> "Incorrect password.";
            case "USER_DISABLED"        -> "This account has been disabled.";
            case "TOO_MANY_ATTEMPTS_TRY_LATER" -> "Too many failed attempts. Try again later.";
            case "INVALID_EMAIL"        -> "Invalid email address.";
            default                     -> "Login failed: " + firebaseMsg;
        };
    }
}
