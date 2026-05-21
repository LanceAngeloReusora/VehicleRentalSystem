import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
 
public class FirebaseAuth {
 
   
    private static final String WEB_API_KEY = "AIzaSyBP0Cgw3Y3NGtysh00DgO0XiCcLxlwGLrE";
 
    private static final String SIGN_IN_URL =
            "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + WEB_API_KEY;
 
    private static final String SIGN_UP_URL =
            "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + WEB_API_KEY;
 
    private static final String RESET_URL =
            "https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode?key=" + WEB_API_KEY;
 
 
    public static class AuthResult {
        public final boolean success;
        public final String  email;       // non-null on success
        public final String  displayName; // may be empty
        public final String  idToken;     // JWT — non-null on success
        public final String  errorMessage;
 
        AuthResult(boolean success, String email, String displayName,
                   String idToken, String errorMessage) {
            this.success      = success;
            this.email        = email;
            this.displayName  = displayName;
            this.idToken      = idToken;
            this.errorMessage = errorMessage;
        }
    }
 
    // ── Sign In ────────────────────────────────────────────────────────────
 
    /**
     * Sign in with email + password.
     * @return AuthResult with success=true and idToken on success,
     *         or success=false with a human-readable errorMessage.
     */
    public static AuthResult signIn(String email, String password) {
        String body = buildJson(email, password);
        return call(SIGN_IN_URL, body);
    }
 
    // ── Sign Up ────────────────────────────────────────────────────────────
 
    /**
     * Create a new account with email + password.
     */
    public static AuthResult signUp(String email, String password) {
        String body = buildJson(email, password);
        return call(SIGN_UP_URL, body);
    }
 
    // ── Password Reset ─────────────────────────────────────────────────────
 
    /**
     * Send a password-reset email.
     * @return true if the request was accepted by Firebase (email sent).
     */
    public static boolean sendPasswordReset(String email) {
        String body = "{\"requestType\":\"PASSWORD_RESET\",\"email\":\"" + email + "\"}";
        try {
            HttpURLConnection conn = openPost(RESET_URL);
            writeBody(conn, body);
            int code = conn.getResponseCode();
            return code == 200;
        } catch (Exception e) {
            return false;
        }
    }
 
    // ── Internals ──────────────────────────────────────────────────────────
 
    private static String buildJson(String email, String password) {
        // Simple manual build — no JSON library required.
        return "{\"email\":\"" + escapeJson(email) + "\","
                + "\"password\":\"" + escapeJson(password) + "\","
                + "\"returnSecureToken\":true}";
    }
 
    private static AuthResult call(String endpoint, String body) {
        try {
            HttpURLConnection conn = openPost(endpoint);
            writeBody(conn, body);
 
            int responseCode = conn.getResponseCode();
            String response;
 
            if (responseCode == 200) {
                response = readStream(conn.getInputStream());
                String email       = extractJsonField(response, "email");
                String displayName = extractJsonField(response, "displayName");
                String idToken     = extractJsonField(response, "idToken");
                return new AuthResult(true, email, displayName, idToken, null);
            } else {
                response = readStream(conn.getErrorStream());
                String msg = extractNestedField(response, "message");
                return new AuthResult(false, null, null, null, friendlyError(msg));
            }
 
        } catch (Exception e) {
            return new AuthResult(false, null, null, null,
                    "Network error: " + e.getMessage());
        }
    }
 
    private static HttpURLConnection openPost(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(8000);
        conn.setReadTimeout(8000);
        return conn;
    }
 
    private static void writeBody(HttpURLConnection conn, String body) throws IOException {
        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }
    }
 
    private static String readStream(InputStream is) throws IOException {
        if (is == null) return "";
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        }
    }
 
    /** Extracts a JSON string field value from a flat JSON response. */
    private static String extractJsonField(String json, String field) {
        String key = "\"" + field + "\"";
        int idx = json.indexOf(key);
        if (idx < 0) return "";
        int colon = json.indexOf(':', idx + key.length());
        if (colon < 0) return "";
        // Skip whitespace after colon
        int start = colon + 1;
        while (start < json.length() && json.charAt(start) == ' ') start++;
        if (json.charAt(start) != '"') return "";
        start++; // skip opening quote
        int end = json.indexOf('"', start);
        return end < 0 ? "" : json.substring(start, end);
    }
 
    /** For Firebase error responses the message is nested: {"error":{"message":"..."}} */
    private static String extractNestedField(String json, String field) {
        return extractJsonField(json, field);
    }
 
    /** Convert Firebase error codes to readable messages. */
    private static String friendlyError(String code) {
        if (code == null || code.isEmpty()) return "Unknown error. Check your connection.";
        return switch (code) {
            case "EMAIL_NOT_FOUND"      -> "No account found with that email.";
            case "INVALID_PASSWORD"     -> "Incorrect password. Please try again.";
            case "USER_DISABLED"        -> "This account has been disabled.";
            case "EMAIL_EXISTS"         -> "An account with this email already exists.";
            case "WEAK_PASSWORD : Password should be at least 6 characters"
                                        -> "Password must be at least 6 characters.";
            case "INVALID_EMAIL"        -> "Please enter a valid email address.";
            case "TOO_MANY_ATTEMPTS_TRY_LATER"
                                        -> "Too many failed attempts. Try again later.";
            default                     -> code;
        };
    }
 
    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
