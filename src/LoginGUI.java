import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
 
/**
 * LoginGUI — Swing login window.
 *
 * Features:
 *  • Sign In  (Firebase Email/Password)
 *  • Create Account
 *  • Forgot Password  (sends reset email via Firebase)
 *  • Show / hide password toggle
 *  • Non-blocking: auth runs on a background thread so the UI stays responsive
 *
 * Entry point: run LoginGUI.main() instead of MainGUI.main().
 * On successful login, this window closes and MainGUI launches.
 */
public class LoginGUI {
 
    // ── Colors (match MainGUI palette) ─────────────────────────────────────
    private static final Color BG      = new Color(245, 245, 245);
    private static final Color ACCENT  = new Color(99, 102, 241);
    private static final Color WHITE   = Color.WHITE;
    private static final Color TEXT    = new Color(30, 30, 30);
    private static final Color MUTED   = new Color(120, 120, 130);
    private static final Color DANGER  = new Color(239, 68, 68);
    private static final Color SIDEBAR = new Color(30, 30, 40);
 
    private JFrame frame;
 
    // ── Tabs ───────────────────────────────────────────────────────────────
    private JPanel  loginCard;
    private JPanel  signupCard;
    private JButton tabLogin;
    private JButton tabSignup;
 
    // ── Login fields ───────────────────────────────────────────────────────
    private JTextField     loginEmail;
    private JPasswordField loginPassword;
    private JLabel         loginError;
    private JButton        loginBtn;
 
    // ── Sign-up fields ─────────────────────────────────────────────────────
    private JTextField     signupEmail;
    private JPasswordField signupPassword;
    private JPasswordField signupConfirm;
    private JLabel         signupError;
    private JButton        signupBtn;
 
    // ══════════════════════════════════════════════════════════════════════
    //  MAIN — run LoginGUI as the program entry point
    // ══════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginGUI().show());
    }
 
    // ══════════════════════════════════════════════════════════════════════
    //  BUILD
    // ══════════════════════════════════════════════════════════════════════
    public void show() {
        frame = new JFrame("Bike Rental System — Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 560);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
 
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
 
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildBody(),   BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
 
        frame.setContentPane(root);
        frame.setVisible(true);
    }
 
    // ── Header ─────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel();
        p.setBackground(SIDEBAR);
        p.setBorder(BorderFactory.createEmptyBorder(28, 24, 22, 24));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
 
        JLabel icon = new JLabel("🚲");
        icon.setFont(new Font("SansSerif", Font.PLAIN, 40));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JLabel title = new JLabel("Bike Rental System");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JLabel sub = new JLabel("Sign in to continue");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(new Color(180, 180, 200));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        p.add(icon);
        p.add(Box.createVerticalStrut(8));
        p.add(title);
        p.add(Box.createVerticalStrut(4));
        p.add(sub);
        return p;
    }
 
    // ── Body ───────────────────────────────────────────────────────────────
    private JPanel buildBody() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 32, 0, 32));
 
        // Tab row
        JPanel tabs = new JPanel(new GridLayout(1, 2, 4, 0));
        tabs.setBackground(BG);
        tabs.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
 
        tabLogin  = tabBtn("Sign In",  true);
        tabSignup = tabBtn("Create Account", false);
        tabLogin .addActionListener(e -> switchTab(true));
        tabSignup.addActionListener(e -> switchTab(false));
 
        tabs.add(tabLogin);
        tabs.add(tabSignup);
 
        // Card stack (CardLayout)
        CardLayout cl = new CardLayout();
        JPanel cards = new JPanel(cl);
        cards.setBackground(BG);
 
        loginCard  = buildLoginCard();
        signupCard = buildSignupCard();
        cards.add(loginCard,  "login");
        cards.add(signupCard, "signup");
 
        // Keep a reference for switching
        tabLogin.addActionListener(e2 -> cl.show(cards, "login"));
        tabSignup.addActionListener(e2 -> cl.show(cards, "signup"));
 
        wrapper.add(tabs,  BorderLayout.NORTH);
        wrapper.add(cards, BorderLayout.CENTER);
        return wrapper;
    }
 
    // ── Footer ─────────────────────────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel p = new JPanel();
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 0, 14, 0));
        JLabel lbl = new JLabel("Secured by Firebase Authentication");
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lbl.setForeground(MUTED);
        p.add(lbl);
        return p;
    }
 
    // ══════════════════════════════════════════════════════════════════════
    //  LOGIN CARD
    // ══════════════════════════════════════════════════════════════════════
    private JPanel buildLoginCard() {
        JPanel p = formPanel();
 
        loginEmail    = emailField("your@email.com");
        loginPassword = passwordField();
        loginError    = errorLabel();
 
        // Show/hide password
        JPanel pwRow = passwordRow(loginPassword);
 
        // Forgot password link
        JLabel forgot = linkLabel("Forgot password?");
        forgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgot.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { forgotPassword(); }
        });
 
        loginBtn = accentBtn("Sign In");
        loginBtn.addActionListener(e -> doLogin());
 
        p.add(fieldLabel("Email Address"));
        p.add(Box.createVerticalStrut(4));
        p.add(loginEmail);
        p.add(Box.createVerticalStrut(12));
        p.add(fieldLabel("Password"));
        p.add(Box.createVerticalStrut(4));
        p.add(pwRow);
        p.add(Box.createVerticalStrut(4));
 
        JPanel forgotRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        forgotRow.setBackground(WHITE);
        forgotRow.add(forgot);
        p.add(forgotRow);
 
        p.add(Box.createVerticalStrut(8));
        p.add(loginError);
        p.add(Box.createVerticalStrut(12));
        p.add(loginBtn);
 
        return wrap(p);
    }
 
    private void doLogin() {
        String email = loginEmail.getText().trim();
        String pass  = new String(loginPassword.getPassword());
 
        if (email.isEmpty() || pass.isEmpty()) {
            showError(loginError, "Please enter your email and password.");
            return;
        }
 
        setLoading(loginBtn, true, "Signing in…");
        loginError.setText(" ");
 
        new Thread(() -> {
            FirebaseAuth.AuthResult result = FirebaseAuth.signIn(email, pass);
            SwingUtilities.invokeLater(() -> {
                setLoading(loginBtn, false, "Sign In");
                if (result.success) {
                    onLoginSuccess(result.email);
                } else {
                    showError(loginError, result.errorMessage);
                }
            });
        }).start();
    }
 
    // ══════════════════════════════════════════════════════════════════════
    //  SIGN-UP CARD
    // ══════════════════════════════════════════════════════════════════════
    private JPanel buildSignupCard() {
        JPanel p = formPanel();
 
        signupEmail    = emailField("your@email.com");
        signupPassword = passwordField();
        signupConfirm  = passwordField();
        signupError    = errorLabel();
 
        signupBtn = accentBtn("Create Account");
        signupBtn.addActionListener(e -> doSignup());
 
        p.add(fieldLabel("Email Address"));
        p.add(Box.createVerticalStrut(4));
        p.add(signupEmail);
        p.add(Box.createVerticalStrut(12));
        p.add(fieldLabel("Password  (min. 6 characters)"));
        p.add(Box.createVerticalStrut(4));
        p.add(passwordRow(signupPassword));
        p.add(Box.createVerticalStrut(12));
        p.add(fieldLabel("Confirm Password"));
        p.add(Box.createVerticalStrut(4));
        p.add(passwordRow(signupConfirm));
        p.add(Box.createVerticalStrut(8));
        p.add(signupError);
        p.add(Box.createVerticalStrut(12));
        p.add(signupBtn);
 
        return wrap(p);
    }
 
    private void doSignup() {
        String email   = signupEmail.getText().trim();
        String pass    = new String(signupPassword.getPassword());
        String confirm = new String(signupConfirm.getPassword());
 
        if (email.isEmpty() || pass.isEmpty()) {
            showError(signupError, "All fields are required.");
            return;
        }
        if (!pass.equals(confirm)) {
            showError(signupError, "Passwords do not match.");
            return;
        }
        if (pass.length() < 6) {
            showError(signupError, "Password must be at least 6 characters.");
            return;
        }
 
        setLoading(signupBtn, true, "Creating account…");
        signupError.setText(" ");
 
        new Thread(() -> {
            FirebaseAuth.AuthResult result = FirebaseAuth.signUp(email, pass);
            SwingUtilities.invokeLater(() -> {
                setLoading(signupBtn, false, "Create Account");
                if (result.success) {
                    onLoginSuccess(result.email);
                } else {
                    showError(signupError, result.errorMessage);
                }
            });
        }).start();
    }
 
    // ══════════════════════════════════════════════════════════════════════
    //  FORGOT PASSWORD
    // ══════════════════════════════════════════════════════════════════════
    private void forgotPassword() {
        String email = JOptionPane.showInputDialog(
                frame,
                "Enter your email address to receive a reset link:",
                "Forgot Password",
                JOptionPane.PLAIN_MESSAGE);
 
        if (email == null || email.trim().isEmpty()) return;
 
        boolean sent = FirebaseAuth.sendPasswordReset(email.trim());
        if (sent) {
            JOptionPane.showMessageDialog(frame,
                    "Password reset email sent to " + email.trim() + ".\nCheck your inbox.",
                    "Email Sent", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Could not send reset email. Check the address and try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
 
    // ══════════════════════════════════════════════════════════════════════
    //  SUCCESS → launch MainGUI
    // ══════════════════════════════════════════════════════════════════════
    private void onLoginSuccess(String email) {
        frame.dispose();
        MainGUI.buildFrame();   // Opens the main dashboard
    }
 
    // ══════════════════════════════════════════════════════════════════════
    //  TAB SWITCH
    // ══════════════════════════════════════════════════════════════════════
    private void switchTab(boolean loginActive) {
        tabLogin .setBackground(loginActive  ? ACCENT : WHITE);
        tabLogin .setForeground(loginActive  ? WHITE  : TEXT);
        tabSignup.setBackground(!loginActive ? ACCENT : WHITE);
        tabSignup.setForeground(!loginActive ? WHITE  : TEXT);
        loginError .setText(" ");
        signupError.setText(" ");
    }
 
    // ══════════════════════════════════════════════════════════════════════
    //  WIDGET HELPERS
    // ══════════════════════════════════════════════════════════════════════
    private JPanel formPanel() {
        JPanel p = new JPanel();
        p.setBackground(WHITE);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return p;
    }
 
    private JPanel wrap(JPanel inner) {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG);
        outer.add(inner, BorderLayout.NORTH);
        return outer;
    }
 
    /** A password field with a show/hide toggle button beside it. */
    private JPanel passwordRow(JPasswordField pf) {
        JPanel row = new JPanel(new BorderLayout(4, 0));
        row.setBackground(WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
 
        JButton toggle = new JButton("👁");
        toggle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        toggle.setFocusPainted(false);
        toggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggle.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 215), 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        toggle.setBackground(WHITE);
        toggle.setToolTipText("Show / hide password");
        toggle.addActionListener(e -> {
            if (pf.getEchoChar() == 0) {
                pf.setEchoChar('•');
                toggle.setText("👁");
            } else {
                pf.setEchoChar((char) 0);
                toggle.setText("🙈");
            }
        });
 
        row.add(pf,     BorderLayout.CENTER);
        row.add(toggle, BorderLayout.EAST);
        return row;
    }
 
    private JTextField emailField(String placeholder) {
        JTextField f = new JTextField();
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.setToolTipText(placeholder);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 215), 1, true),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        return f;
    }
 
    private JPasswordField passwordField() {
        JPasswordField f = new JPasswordField();
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.setEchoChar('•');
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 215), 1, true),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        return f;
    }
 
    private JButton accentBtn(String text) {
        JButton b = new JButton(text);
        b.setBackground(ACCENT);
        b.setForeground(WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        return b;
    }
 
    private JButton tabBtn(String text, boolean active) {
        JButton b = new JButton(text);
        b.setBackground(active ? ACCENT : WHITE);
        b.setForeground(active ? WHITE  : TEXT);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 215), 1, true),
                BorderFactory.createEmptyBorder(8, 0, 8, 0)));
        return b;
    }
 
    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setForeground(MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
 
    private JLabel errorLabel() {
        JLabel l = new JLabel(" ");
        l.setFont(new Font("SansSerif", Font.PLAIN, 11));
        l.setForeground(DANGER);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
 
    private JLabel linkLabel(String text) {
        JLabel l = new JLabel("<html><u>" + text + "</u></html>");
        l.setFont(new Font("SansSerif", Font.PLAIN, 11));
        l.setForeground(ACCENT);
        return l;
    }
 
    private void showError(JLabel lbl, String msg) {
        lbl.setText(msg);
    }
 
    private void setLoading(JButton btn, boolean loading, String label) {
        btn.setText(loading ? "Please wait…" : label);
        btn.setEnabled(!loading);
    }
}
