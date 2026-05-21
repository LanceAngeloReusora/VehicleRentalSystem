import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MainGUI {

    // ── Data ──────────────────────────────────────────────────────────
    static BikeRentalService service = new BikeRentalService();

    // ── Colors ────────────────────────────────────────────────────────
    static final Color BG        = new Color(245, 245, 245);
    static final Color SIDEBAR   = new Color(30,  30,  40);
    static final Color SIDEBAR_H = new Color(55,  55,  70);
    static final Color ACCENT    = new Color(99,  102, 241);
    static final Color WHITE     = Color.WHITE;
    static final Color TEXT      = new Color(30,  30,  30);
    static final Color MUTED     = new Color(120, 120, 130);
    static final Color SUCCESS   = new Color(34,  197, 94);
    static final Color DANGER    = new Color(239, 68,  68);
    static final Color WARNING   = new Color(234, 179, 8);
    static final Color INFO      = new Color(99,  102, 241);

    // ── Main frame ────────────────────────────────────────────────────
    static JFrame frame;
    static JPanel contentPanel;
    static CardLayout cardLayout;

    // ── Sidebar buttons ───────────────────────────────────────────────
    static JButton[] navButtons;
    static String[] navNames  = {"Dashboard","Customers","Bikes","Helmets","Rentals","Reservations","Maintenance"};
    static String[] navIcons  = {"⊞","👤","🚲","🪖","📋","📅","🔧"};
    static String[] cardNames = {"dashboard","customers","bikes","helmets","rentals","reservations","maintenance"};

    public static void main(String[] args) {
        // Seed data
        service.addBike(new MountainBike("M1","Trek"));
        service.addBike(new BMXBike("B1","Haro"));
        service.addBike(new RoadBike("R1","Giant"));
        service.addBike(new ElectricBike("E1","Xiaomi"));
        service.addBike(new JapaneseBike("J1","Bridgestone"));
        service.addHelmet(new Helmet("H1", Helmet.Size.SMALL));
        service.addHelmet(new Helmet("H2", Helmet.Size.MEDIUM));
        service.addHelmet(new Helmet("H3", Helmet.Size.LARGE));
        service.addHelmet(new Helmet("H4", Helmet.Size.MEDIUM));

        SwingUtilities.invokeLater(MainGUI::buildFrame);
    }

    // ═════════════════════════════════════════════════════════════════
    //  FRAME
    // ═════════════════════════════════════════════════════════════════
    static void buildFrame() {
        frame = new JFrame("Bike Rental System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1050, 680);
        frame.setMinimumSize(new Dimension(900, 600));
        frame.setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.add(buildSidebar(), BorderLayout.WEST);

        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG);

        for (String name : cardNames) contentPanel.add(new JPanel(), name);

        root.add(contentPanel, BorderLayout.CENTER);
        frame.setContentPane(root);
        frame.setVisible(true);

        navigate("dashboard");
    }

    // ═════════════════════════════════════════════════════════════════
    //  SIDEBAR
    // ═════════════════════════════════════════════════════════════════
    static JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setBackground(SIDEBAR);
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setPreferredSize(new Dimension(190, 0));
        side.setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));

        JLabel title = new JLabel("  🚲 Bike Rental");
        title.setForeground(WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        side.add(title);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(60, 60, 80));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        side.add(sep);
        side.add(Box.createVerticalStrut(8));

        navButtons = new JButton[navNames.length];
        for (int i = 0; i < navNames.length; i++) {
            final String card = cardNames[i];
            JButton btn = new JButton(navIcons[i] + "  " + navNames[i]);
            btn.setForeground(new Color(180, 180, 200));
            btn.setBackground(SIDEBAR);
            btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
            btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { if (!btn.getBackground().equals(SIDEBAR_H)) btn.setBackground(SIDEBAR_H); }
                public void mouseExited (MouseEvent e) { if (!btn.getBackground().equals(ACCENT)) btn.setBackground(SIDEBAR); }
            });
            btn.addActionListener(e -> navigate(card));
            navButtons[i] = btn;
            side.add(btn);
        }
        return side;
    }

    static void navigate(String card) {
        for (int i = 0; i < cardNames.length; i++) {
            if (cardNames[i].equals(card)) {
                navButtons[i].setBackground(ACCENT);
                navButtons[i].setForeground(WHITE);
            } else {
                navButtons[i].setBackground(SIDEBAR);
                navButtons[i].setForeground(new Color(180, 180, 200));
            }
        }
        contentPanel.removeAll();
        for (String name : cardNames) {
            if (name.equals(card)) {
                contentPanel.add(buildPanel(card), name);
            } else {
                contentPanel.add(new JPanel(), name);
            }
        }
        cardLayout.show(contentPanel, card);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    static JPanel buildPanel(String card) {
        return switch (card) {
            case "dashboard"    -> dashboardPanel();
            case "customers"    -> customersPanel();
            case "bikes"        -> bikesPanel();
            case "helmets"      -> helmetsPanel();
            case "rentals"      -> rentalsPanel();
            case "reservations" -> reservationsPanel();
            case "maintenance"  -> maintenancePanel();
            default             -> new JPanel();
        };
    }

    // ═════════════════════════════════════════════════════════════════
    //  HELPERS
    // ═════════════════════════════════════════════════════════════════
    static JPanel pageWrapper(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 20));
        lbl.setForeground(TEXT);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        p.add(lbl, BorderLayout.NORTH);
        return p;
    }

    static JPanel card() {
        JPanel c = new JPanel();
        c.setBackground(WHITE);
        c.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 230), 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        return c;
    }

    static JButton primaryBtn(String text) {
        JButton b = new JButton(text);
        b.setBackground(ACCENT);
        b.setForeground(WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return b;
    }

    static JButton secondaryBtn(String text) {
        JButton b = new JButton(text);
        b.setBackground(WHITE);
        b.setForeground(TEXT);
        b.setFont(new Font("SansSerif", Font.PLAIN, 12));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 210), 1, true),
                BorderFactory.createEmptyBorder(7, 14, 7, 14)));
        return b;
    }

    static JLabel badge(String text, Color bg, Color fg) {
        JLabel l = new JLabel(text);
        l.setOpaque(true);
        l.setBackground(bg);
        l.setForeground(fg);
        l.setFont(new Font("SansSerif", Font.BOLD, 10));
        l.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        return l;
    }

    static JTable buildTable(String[] cols, Object[][] data) {
        DefaultTableModel model = new DefaultTableModel(data, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        table.getTableHeader().setBackground(new Color(240, 240, 248));
        table.getTableHeader().setForeground(MUTED);
        table.setGridColor(new Color(235, 235, 240));
        table.setSelectionBackground(new Color(238, 238, 255));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        return table;
    }

    static JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setForeground(MUTED);
        return l;
    }

    static JTextField field(String placeholder) {
        JTextField f = new JTextField();
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.setToolTipText(placeholder);
        f.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 215), 1, true),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        return f;
    }

    static JComboBox<String> combo(String[] items) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return c;
    }

    static void toast(Component parent, String msg, boolean success) {
        JOptionPane.showMessageDialog(parent, msg,
                success ? "Success" : "Error",
                success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }

    static String phpFmt(double v) { return String.format("PHP %.2f", v); }

    // ═════════════════════════════════════════════════════════════════
    //  DASHBOARD
    // ═════════════════════════════════════════════════════════════════
    static JPanel dashboardPanel() {
        JPanel page = pageWrapper("Dashboard");

        JPanel body = new JPanel(new BorderLayout(16, 16));
        body.setBackground(BG);

        // Stat cards
        JPanel stats = new JPanel(new GridLayout(1, 4, 12, 0));
        stats.setBackground(BG);
        long avBikes   = service.getBikes().stream().filter(Bike::isAvailable).count();
        long avHelmets = service.getHelmets().stream().filter(Helmet::isAvailable).count();
        long actRent   = service.getRentals().stream().filter(r -> !r.isReturned()).count();
        long actRes    = service.getReservations().stream().filter(Reservation::isActive).count();

        stats.add(statCard("Available Bikes",   String.valueOf(avBikes),  new Color(224, 231, 255)));
        stats.add(statCard("Active Rentals",    String.valueOf(actRent),  new Color(220, 252, 231)));
        stats.add(statCard("Reservations",      String.valueOf(actRes),   new Color(254, 249, 195)));
        stats.add(statCard("Available Helmets", String.valueOf(avHelmets),new Color(255, 237, 213)));

        // Recent rentals table
        JPanel tableCard = card();
        tableCard.setLayout(new BorderLayout());
        JLabel tl = new JLabel("Recent Rentals");
        tl.setFont(new Font("SansSerif", Font.BOLD, 13));
        tl.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        tableCard.add(tl, BorderLayout.NORTH);

        ArrayList<Rental> rent = service.getRentals();
        String[] cols = {"Rental ID","Customer","Bike","Hours","Total","Status"};
        int n = rent.size();
        int show = Math.min(8, n);
        Object[][] data = new Object[show][6];
        for (int i = 0; i < show; i++) {
            Rental r = rent.get(n - 1 - i);
            data[i] = new Object[]{ r.getRentalId(), r.getCustomer().getName(),
                    r.getBike().getBrand()+" ("+r.getBike().getBikeId()+")",
                    r.getBookedHours()+"h", phpFmt(r.getTotalCost()),
                    r.isReturned() ? "Returned" : "Active" };
        }
        tableCard.add(new JScrollPane(buildTable(cols, data)), BorderLayout.CENTER);

        body.add(stats, BorderLayout.NORTH);
        body.add(tableCard, BorderLayout.CENTER);
        page.add(body, BorderLayout.CENTER);
        return page;
    }

    static JPanel statCard(String label, String value, Color bg) {
        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(bg);
        c.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(bg.darker(), 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        JLabel v = new JLabel(value);
        v.setFont(new Font("SansSerif", Font.BOLD, 28));
        v.setForeground(TEXT);
        JLabel l = new JLabel(label);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        l.setForeground(MUTED);
        c.add(v, BorderLayout.CENTER);
        c.add(l, BorderLayout.SOUTH);
        return c;
    }

    // ═════════════════════════════════════════════════════════════════
    //  CUSTOMERS
    // ═════════════════════════════════════════════════════════════════
    static JPanel customersPanel() {
        JPanel page = pageWrapper("Customers");

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        top.setBackground(BG);
        JButton reg = primaryBtn("+ Register Customer");
        reg.addActionListener(e -> registerCustomerDialog());
        top.add(reg);

        ArrayList<Customer> custs = service.getCustomers();
        String[] cols = {"ID","Name","Contact","Discount","Actions"};
        Object[][] data = new Object[custs.size()][5];
        for (int i = 0; i < custs.size(); i++) {
            Customer c = custs.get(i);
            data[i] = new Object[]{ c.getCustomerId(), c.getName(), c.getContact(),
                    String.format("%.0f%%", c.getDiscountRate()*100), "History" };
        }

        JTable table = buildTable(cols, data);
        table.getColumn("Actions").setCellRenderer(new ButtonRenderer("History"));
        table.getColumn("Actions").setCellEditor(new ButtonEditor(new JCheckBox(), "History", row -> {
            Customer c = custs.get(row);
            showHistoryDialog(c);
        }));

        JPanel tableCard = card();
        tableCard.setLayout(new BorderLayout());
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel body = new JPanel(new BorderLayout(0, 10));
        body.setBackground(BG);
        body.add(top, BorderLayout.NORTH);
        body.add(tableCard, BorderLayout.CENTER);
        page.add(body, BorderLayout.CENTER);
        return page;
    }

    static void registerCustomerDialog() {
        JDialog dlg = new JDialog(frame, "Register Customer", true);
        dlg.setSize(380, 320);
        dlg.setLocationRelativeTo(frame);
        JPanel p = new JPanel(new GridLayout(0, 1, 6, 6));
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        p.setBackground(WHITE);

        JTextField fId      = field("e.g. C001");
        JTextField fName    = field("Full name");
        JTextField fContact = field("09XXXXXXXXX");
        JTextField fPromo   = field("WELCOME (optional)");

        p.add(fieldLabel("Customer ID")); p.add(fId);
        p.add(fieldLabel("Name"));        p.add(fName);
        p.add(fieldLabel("Contact"));     p.add(fContact);
        p.add(fieldLabel("Promo Code"));  p.add(fPromo);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.setBackground(WHITE);
        JButton cancel = secondaryBtn("Cancel");
        JButton ok     = primaryBtn("Register");
        cancel.addActionListener(e -> dlg.dispose());
        ok.addActionListener(e -> {
            String id      = fId.getText().trim();
            String name    = fName.getText().trim();
            String contact = fContact.getText().trim();
            String promo   = fPromo.getText().trim().toUpperCase();
            if (id.isEmpty() || name.isEmpty() || contact.isEmpty()) {
                toast(dlg, "All fields except promo are required.", false); return;
            }
            if (!contact.matches("\\d+")) {
                toast(dlg, "Contact must be numbers only.", false); return;
            }
            if (service.findCustomer(id) != null) {
                toast(dlg, "Customer ID already exists.", false); return;
            }
            double discount = 0;
            if (promo.equals("WELCOME")) discount = 0.05;
            else if (!promo.isEmpty()) toast(dlg, "Invalid promo code. No discount applied.", false);

            service.registerCustomer(new Customer(id, name, contact, discount));
            dlg.dispose();
            navigate("customers");
            toast(frame, "Customer registered successfully!", true);
        });
        btns.add(cancel); btns.add(ok);

        dlg.setLayout(new BorderLayout());
        dlg.add(p, BorderLayout.CENTER);
        dlg.add(btns, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    static void showHistoryDialog(Customer c) {
        JDialog dlg = new JDialog(frame, "Rental History — " + c.getName(), true);
        dlg.setSize(560, 400);
        dlg.setLocationRelativeTo(frame);

        JPanel header = new JPanel(new GridLayout(0, 1, 2, 2));
        header.setBackground(new Color(245, 245, 255));
        header.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        header.add(bold(c.getName()));
        header.add(muted("ID: " + c.getCustomerId() + "  |  Contact: " + c.getContact() + "  |  Discount: " + String.format("%.0f%%", c.getDiscountRate()*100)));

        ArrayList<Rental> cRentals = new ArrayList<>();
        for (Rental r : service.getRentals()) if (r.getCustomer().getCustomerId().equals(c.getCustomerId())) cRentals.add(r);

        String[] cols = {"Rental ID","Bike","Hours","Total","Status"};
        Object[][] data = new Object[cRentals.size()][5];
        double total = 0;
        for (int i = 0; i < cRentals.size(); i++) {
            Rental r = cRentals.get(i);
            data[i] = new Object[]{ r.getRentalId(), r.getBike().getBrand()+" ("+r.getBike().getBikeId()+")",
                    r.getBookedHours()+"h", phpFmt(r.getTotalCost()), r.isReturned()?"Returned":"Active" };
            total += r.getTotalCost();
        }

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        footer.add(bold("Total Spent: " + phpFmt(total)));
        JButton close = primaryBtn("Close");
        close.addActionListener(e -> dlg.dispose());
        footer.add(close);

        dlg.setLayout(new BorderLayout());
        dlg.add(header, BorderLayout.NORTH);
        dlg.add(new JScrollPane(buildTable(cols, data)), BorderLayout.CENTER);
        dlg.add(footer, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // ═════════════════════════════════════════════════════════════════
    //  BIKES
    // ═════════════════════════════════════════════════════════════════
    static JPanel bikesPanel() {
        JPanel page = pageWrapper("Bikes");
        ArrayList<Bike> bikeList = service.getBikes();
        String[] cols = {"ID","Brand","Type","Rate/hr","Condition","Status"};
        Object[][] data = new Object[bikeList.size()][6];
        for (int i = 0; i < bikeList.size(); i++) {
            Bike b = bikeList.get(i);
            String status = b.isUnderMaintenance() ? "Maintenance" : b.isAvailable() ? "Available" : "Rented";
            data[i] = new Object[]{ b.getBikeId(), b.getBrand(), b.getType(),
                    phpFmt(b.getRatePerHour()), b.getCondition(), status };
        }
        JPanel tableCard = card();
        tableCard.setLayout(new BorderLayout());
        tableCard.add(new JScrollPane(buildTable(cols, data)), BorderLayout.CENTER);
        page.add(tableCard, BorderLayout.CENTER);
        return page;
    }

    // ═════════════════════════════════════════════════════════════════
    //  HELMETS
    // ═════════════════════════════════════════════════════════════════
    static JPanel helmetsPanel() {
        JPanel page = pageWrapper("Helmets");
        ArrayList<Helmet> helmetList = service.getHelmets();
        String[] cols = {"ID","Size","Fee","Status"};
        Object[][] data = new Object[helmetList.size()][4];
        for (int i = 0; i < helmetList.size(); i++) {
            Helmet h = helmetList.get(i);
            data[i] = new Object[]{ h.getHelmetId(), h.getSize(), phpFmt(Helmet.getHelmetFee()),
                    h.isAvailable() ? "Available" : "In Use" };
        }
        JPanel tableCard = card();
        tableCard.setLayout(new BorderLayout());
        tableCard.add(new JScrollPane(buildTable(cols, data)), BorderLayout.CENTER);
        page.add(tableCard, BorderLayout.CENTER);
        return page;
    }

    // ═════════════════════════════════════════════════════════════════
    //  RENTALS
    // ═════════════════════════════════════════════════════════════════
    static JPanel rentalsPanel() {
        JPanel page = pageWrapper("Rentals");

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        top.setBackground(BG);
        JButton newRent   = primaryBtn("+ New Rental");
        JButton returnBtn = secondaryBtn("↩ Return Bike");
        newRent.addActionListener(e -> rentDialog());
        returnBtn.addActionListener(e -> returnDialog());
        top.add(returnBtn); top.add(newRent);

        ArrayList<Rental> rentList = service.getRentals();
        String[] cols = {"Rental ID","Customer","Bike","Helmet","Hrs","Base","Late","Dmg","Total","Status"};
        Object[][] data = new Object[rentList.size()][10];
        for (int i = 0; i < rentList.size(); i++) {
            Rental r = rentList.get(i);
            data[i] = new Object[]{ r.getRentalId(), r.getCustomer().getName(),
                    r.getBike().getBrand()+"("+r.getBike().getBikeId()+")",
                    r.getHelmet()!=null ? r.getHelmet().getHelmetId() : "—",
                    r.getBookedHours(), phpFmt(r.getBaseCost()),
                    phpFmt(r.getLateFee()), phpFmt(r.getDamagePenalty()),
                    phpFmt(r.getTotalCost()), r.isReturned()?"Returned":"Active" };
        }

        JPanel tableCard = card();
        tableCard.setLayout(new BorderLayout());
        tableCard.add(new JScrollPane(buildTable(cols, data)), BorderLayout.CENTER);

        JPanel body = new JPanel(new BorderLayout(0, 10));
        body.setBackground(BG);
        body.add(top, BorderLayout.NORTH);
        body.add(tableCard, BorderLayout.CENTER);
        page.add(body, BorderLayout.CENTER);
        return page;
    }

    static void rentDialog() {
        ArrayList<Customer> custs  = service.getCustomers();
        ArrayList<Bike>     avBikes = new ArrayList<>();
        for (Bike b : service.getBikes()) if (b.isAvailable()) avBikes.add(b);
        ArrayList<Helmet>   avHelm = new ArrayList<>();
        for (Helmet h : service.getHelmets()) if (h.isAvailable()) avHelm.add(h);

        if (custs.isEmpty())  { toast(frame,"Register a customer first.",false); return; }
        if (avBikes.isEmpty()){ toast(frame,"No bikes available.",false); return; }

        JDialog dlg = new JDialog(frame, "New Rental", true);
        dlg.setSize(420, 460);
        dlg.setLocationRelativeTo(frame);

        JPanel p = new JPanel(new GridLayout(0, 1, 6, 6));
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));
        p.setBackground(WHITE);

        String[] custItems = custs.stream().map(c->c.getName()+" ("+c.getCustomerId()+") — "+(int)(c.getDiscountRate()*100)+"% off").toArray(String[]::new);
        String[] bikeItems = avBikes.stream().map(b->b.getBrand()+" — "+b.getType()+" @ PHP "+b.getRatePerHour()+"/hr ("+b.getBikeId()+")").toArray(String[]::new);
        String[] helmItems = new String[avHelm.size()+1];
        helmItems[0] = "None";
        for (int i=0; i<avHelm.size(); i++) helmItems[i+1] = avHelm.get(i).getHelmetId()+" ("+avHelm.get(i).getSize()+") — PHP 50";
        String[] payItems = {"CASH","GCASH","MAYA","CREDIT_DEBIT","BANK_TRANSFER"};

        JComboBox<String> cbCust = combo(custItems);
        JComboBox<String> cbBike = combo(bikeItems);
        JTextField fHours = field("e.g. 2");
        JComboBox<String> cbHelm = combo(helmItems);
        JComboBox<String> cbPay  = combo(payItems);
        JLabel previewLabel = new JLabel("<html><i>Select options above to see cost.</i></html>");
        previewLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        previewLabel.setForeground(MUTED);

        p.add(fieldLabel("Customer")); p.add(cbCust);
        p.add(fieldLabel("Bike"));     p.add(cbBike);
        p.add(fieldLabel("Hours"));    p.add(fHours);
        p.add(fieldLabel("Helmet"));   p.add(cbHelm);
        p.add(fieldLabel("Payment"));  p.add(cbPay);
        p.add(fieldLabel("Cost Preview")); p.add(previewLabel);

        ActionListener preview = e -> {
            try {
                int h = Integer.parseInt(fHours.getText().trim());
                Bike b = avBikes.get(cbBike.getSelectedIndex());
                Customer c = custs.get(cbCust.getSelectedIndex());
                double base = b.getRatePerHour() * h;
                double disc = base * c.getDiscountRate();
                double cost = base - disc;
                double hFee = cbHelm.getSelectedIndex() > 0 ? 50.0 : 0;
                previewLabel.setText("<html><b>Total: " + phpFmt(cost+hFee) + "</b>"
                        + (disc>0?" <font color=gray>(discount: "+phpFmt(disc)+")</font>":"")
                        + (hFee>0?" + helmet PHP 50":"") + "</html>");
            } catch (Exception ignored) {}
        };
        cbCust.addActionListener(preview); cbBike.addActionListener(preview);
        cbHelm.addActionListener(preview); fHours.addActionListener(preview);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.setBackground(WHITE);
        JButton cancel = secondaryBtn("Cancel");
        JButton ok     = primaryBtn("Confirm & Pay");
        cancel.addActionListener(e -> dlg.dispose());
        ok.addActionListener(e -> {
            int hrs;
            try { hrs = Integer.parseInt(fHours.getText().trim()); if(hrs<1) throw new Exception(); }
            catch (Exception ex) { toast(dlg,"Enter a valid number of hours.",false); return; }

            Customer c  = custs.get(cbCust.getSelectedIndex());
            Bike b      = avBikes.get(cbBike.getSelectedIndex());
            Helmet helm = cbHelm.getSelectedIndex()>0 ? avHelm.get(cbHelm.getSelectedIndex()-1) : null;
            String rid  = "R"+c.getCustomerId()+"-"+b.getBikeId();
            if (service.findActiveRental(rid) != null) { toast(dlg,"Active rental already exists for this bike/customer.",false); return; }

            Rental rental = new Rental(rid, c, b, hrs, helm);
            service.addRental(rental);
            String pay = (String) cbPay.getSelectedItem();
            new Payment("P-"+rid, rental.getTotalCost(), Payment.Method.valueOf(pay)).processPayment();
            dlg.dispose();
            navigate("rentals");
            toast(frame, "Rental created! Payment received via "+pay, true);
        });
        btns.add(cancel); btns.add(ok);

        dlg.setLayout(new BorderLayout());
        dlg.add(p, BorderLayout.CENTER);
        dlg.add(btns, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    static void returnDialog() {
        ArrayList<Rental> active = new ArrayList<>();
        for (Rental r : service.getRentals()) if (!r.isReturned()) active.add(r);
        if (active.isEmpty()) { toast(frame,"No active rentals.",false); return; }

        JDialog dlg = new JDialog(frame, "Return Bike", true);
        dlg.setSize(400, 360);
        dlg.setLocationRelativeTo(frame);

        JPanel p = new JPanel(new GridLayout(0, 1, 6, 6));
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));
        p.setBackground(WHITE);

        String[] rentItems = active.stream().map(r->r.getRentalId()+" — "+r.getCustomer().getName()+" / "+r.getBike().getBrand()+" ("+r.getBookedHours()+"h booked)").toArray(String[]::new);
        JComboBox<String> cbRent = combo(rentItems);
        JTextField fActual = field("Must be >= booked hours");
        JComboBox<String> cbCond = combo(new String[]{"GOOD","DAMAGED (+PHP 500)"});
        JComboBox<String> cbPay  = combo(new String[]{"CASH","GCASH","MAYA","CREDIT_DEBIT","BANK_TRANSFER"});
        JLabel previewLabel = new JLabel(" ");
        previewLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        p.add(fieldLabel("Rental"));        p.add(cbRent);
        p.add(fieldLabel("Actual Hours"));  p.add(fActual);
        p.add(fieldLabel("Bike Condition")); p.add(cbCond);
        p.add(fieldLabel("Payment (extra charges)")); p.add(cbPay);
        p.add(fieldLabel("Summary")); p.add(previewLabel);

        ActionListener preview = e -> {
            try {
                Rental r = active.get(cbRent.getSelectedIndex());
                int act = Integer.parseInt(fActual.getText().trim());
                int extra = Math.max(0, act - r.getBookedHours());
                double late = extra > 0 ? r.getBike().getRatePerHour() * extra * 1.5 : 0;
                double dmg  = cbCond.getSelectedIndex()==1 ? 500 : 0;
                double total = r.getBaseCost() + r.getHelmetFee() + late + dmg;
                previewLabel.setText("<html>Total: <b>"+phpFmt(total)+"</b>"
                        + (late>0?" | Late: "+phpFmt(late):"")
                        + (dmg>0?" | Damage: "+phpFmt(dmg):"")+"</html>");
            } catch (Exception ignored) {}
        };
        cbRent.addActionListener(preview); cbCond.addActionListener(preview); fActual.addActionListener(preview);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.setBackground(WHITE);
        JButton cancel = secondaryBtn("Cancel");
        JButton ok     = primaryBtn("Confirm Return");
        cancel.addActionListener(e -> dlg.dispose());
        ok.addActionListener(e -> {
            int act;
            try { act = Integer.parseInt(fActual.getText().trim()); } catch(Exception ex) { toast(dlg,"Enter valid hours.",false); return; }
            Rental r = active.get(cbRent.getSelectedIndex());
            if (act < r.getBookedHours()) { toast(dlg,"Actual hours cannot be less than booked hours ("+r.getBookedHours()+").",false); return; }
            Bike.Condition cond = cbCond.getSelectedIndex()==1 ? Bike.Condition.DAMAGED : Bike.Condition.GOOD;
            r.returnBike(act, cond);
            double extras = r.getLateFee() + r.getDamagePenalty();
            if (extras > 0) {
                String pay = (String) cbPay.getSelectedItem();
                new Payment("EP-"+r.getRentalId(), extras, Payment.Method.valueOf(pay)).processPayment();
            }
            dlg.dispose();
            navigate("rentals");
            toast(frame, "Bike returned!" + (extras>0?" Extra charges collected.":"" ), true);
        });
        btns.add(cancel); btns.add(ok);

        dlg.setLayout(new BorderLayout());
        dlg.add(p, BorderLayout.CENTER);
        dlg.add(btns, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // ═════════════════════════════════════════════════════════════════
    //  RESERVATIONS
    // ═════════════════════════════════════════════════════════════════
    static JPanel reservationsPanel() {
        JPanel page = pageWrapper("Reservations");

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        top.setBackground(BG);
        JButton newRes = primaryBtn("+ New Reservation");
        newRes.addActionListener(e -> reserveDialog());
        top.add(newRes);

        ArrayList<Reservation> resList = service.getReservations();
        String[] cols = {"ID","Customer","Bike","Hours","Date","Status","Actions"};
        Object[][] data = new Object[resList.size()][7];
        for (int i = 0; i < resList.size(); i++) {
            Reservation r = resList.get(i);
            data[i] = new Object[]{ r.getReservationId(), r.getCustomer().getName(),
                    r.getBike().getBrand()+" ("+r.getBike().getBikeId()+")",
                    r.getReservedHours()+"h",
                    r.getReservationDateTime().format(DateTimeFormatter.ofPattern("MM/dd HH:mm")),
                    r.isActive() ? "Active" : "Done/Cancelled",
                    r.isActive() ? "Confirm | Cancel" : "—" };
        }

        JTable table = buildTable(cols, data);
        if (!resList.isEmpty()) {
            table.getColumn("Actions").setCellRenderer(new ButtonRenderer("Actions"));
            table.getColumn("Actions").setCellEditor(new ButtonEditor(new JCheckBox(), "Actions", row -> {
                Reservation r = resList.get(row);
                if (!r.isActive()) return;
                String[] options = {"Confirm Reservation","Cancel Reservation","Close"};
                int choice = JOptionPane.showOptionDialog(frame,
                        "Reservation: "+r.getReservationId()+"\nCustomer: "+r.getCustomer().getName()+"\nBike: "+r.getBike().getBrand()+" ("+r.getReservedHours()+"h)",
                        "Manage Reservation", JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (choice == 0) {
                    String[] pays = {"CASH","GCASH","MAYA","CREDIT_DEBIT","BANK_TRANSFER"};
                    String pay = (String) JOptionPane.showInputDialog(frame,"Select payment method:","Payment",JOptionPane.PLAIN_MESSAGE,null,pays,"CASH");
                    if (pay == null) return;
                    Rental confirmed = r.confirmReservation();
                    if (confirmed != null) {
                        service.addRental(confirmed);
                        new Payment("P-"+confirmed.getRentalId(), confirmed.getTotalCost(), Payment.Method.valueOf(pay)).processPayment();
                        navigate("reservations");
                        toast(frame,"Reservation confirmed! Rental "+confirmed.getRentalId()+" created.",true);
                    }
                } else if (choice == 1) {
                    r.cancelReservation();
                    navigate("reservations");
                    toast(frame,"Reservation cancelled.",true);
                }
            }));
        }

        JPanel tableCard = card();
        tableCard.setLayout(new BorderLayout());
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel body = new JPanel(new BorderLayout(0,10));
        body.setBackground(BG);
        body.add(top, BorderLayout.NORTH);
        body.add(tableCard, BorderLayout.CENTER);
        page.add(body, BorderLayout.CENTER);
        return page;
    }

    static void reserveDialog() {
        ArrayList<Customer> custs   = service.getCustomers();
        ArrayList<Bike>     avBikes = new ArrayList<>();
        for (Bike b : service.getBikes()) if (b.isAvailable()) avBikes.add(b);

        if (custs.isEmpty())   { toast(frame,"Register a customer first.",false); return; }
        if (avBikes.isEmpty()) { toast(frame,"No bikes available.",false); return; }

        JDialog dlg = new JDialog(frame,"New Reservation",true);
        dlg.setSize(380, 260);
        dlg.setLocationRelativeTo(frame);

        JPanel p = new JPanel(new GridLayout(0,1,6,6));
        p.setBorder(BorderFactory.createEmptyBorder(16,16,8,16));
        p.setBackground(WHITE);

        String[] custItems = custs.stream().map(c->c.getName()+" ("+c.getCustomerId()+")").toArray(String[]::new);
        String[] bikeItems = avBikes.stream().map(b->b.getBrand()+" — "+b.getType()+" ("+b.getBikeId()+")").toArray(String[]::new);

        JComboBox<String> cbCust = combo(custItems);
        JComboBox<String> cbBike = combo(bikeItems);
        JTextField fHours = field("e.g. 2");

        p.add(fieldLabel("Customer")); p.add(cbCust);
        p.add(fieldLabel("Bike"));     p.add(cbBike);
        p.add(fieldLabel("Hours"));    p.add(fHours);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.setBackground(WHITE);
        JButton cancel = secondaryBtn("Cancel");
        JButton ok = primaryBtn("Reserve");
        cancel.addActionListener(e -> dlg.dispose());
        ok.addActionListener(e -> {
            int hrs;
            try { hrs = Integer.parseInt(fHours.getText().trim()); if(hrs<1) throw new Exception(); }
            catch (Exception ex) { toast(dlg,"Enter valid hours.",false); return; }
            Customer c = custs.get(cbCust.getSelectedIndex());
            Bike b     = avBikes.get(cbBike.getSelectedIndex());
            String rid = "RES"+c.getCustomerId()+"-"+b.getBikeId();
            if (service.findActiveReservation(rid) != null) { toast(dlg,"Active reservation already exists.",false); return; }
            Reservation res = new Reservation(rid, c, b, LocalDateTime.now(), hrs);
            service.addReservation(res);
            dlg.dispose();
            navigate("reservations");
            toast(frame,"Reservation created: "+rid,true);
        });
        btns.add(cancel); btns.add(ok);

        dlg.setLayout(new BorderLayout());
        dlg.add(p, BorderLayout.CENTER);
        dlg.add(btns, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // ═════════════════════════════════════════════════════════════════
    //  MAINTENANCE
    // ═════════════════════════════════════════════════════════════════
    static JPanel maintenancePanel() {
        JPanel page = pageWrapper("Bike Maintenance");
        ArrayList<Bike> bikeList = service.getBikes();
        String[] cols = {"ID","Brand","Type","Rate/hr","Condition","Status","Action"};
        Object[][] data = new Object[bikeList.size()][7];
        for (int i = 0; i < bikeList.size(); i++) {
            Bike b = bikeList.get(i);
            String status = b.isUnderMaintenance() ? "Under Maintenance" : b.isAvailable() ? "Available" : "Rented";
            data[i] = new Object[]{ b.getBikeId(), b.getBrand(), b.getType(),
                    phpFmt(b.getRatePerHour()), b.getCondition(), status,
                    b.isUnderMaintenance() ? "Clear" : "Flag" };
        }

        JTable table = buildTable(cols, data);
        table.getColumn("Action").setCellRenderer(new ButtonRenderer("Action"));
        table.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), "Action", row -> {
            Bike b = bikeList.get(row);
            if (b.isUnderMaintenance()) {
                service.clearFromMaintenance(b.getBikeId());
                navigate("maintenance");
                toast(frame,"Bike "+b.getBikeId()+" cleared and available.",true);
            } else {
                service.flagForMaintenance(b.getBikeId());
                navigate("maintenance");
                toast(frame,"Bike "+b.getBikeId()+" flagged for maintenance.",true);
            }
        }));

        JPanel tableCard = card();
        tableCard.setLayout(new BorderLayout());
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);
        page.add(tableCard, BorderLayout.CENTER);
        return page;
    }

    // ═════════════════════════════════════════════════════════════════
    //  MISC LABEL HELPERS
    // ═════════════════════════════════════════════════════════════════
    static JLabel bold(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        return l;
    }
    static JLabel muted(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        l.setForeground(MUTED);
        return l;
    }

    // ═════════════════════════════════════════════════════════════════
    //  TABLE BUTTON RENDERER / EDITOR
    // ═════════════════════════════════════════════════════════════════
    static class ButtonRenderer extends DefaultTableCellRenderer {
        String label;
        ButtonRenderer(String label) { this.label = label; }
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            JButton btn = secondaryBtn(v != null ? v.toString() : label);
            return btn;
        }
    }

    interface RowAction { void run(int row); }

    static class ButtonEditor extends DefaultCellEditor {
        JButton button;
        int row;
        RowAction action;
        boolean clicked;

        ButtonEditor(JCheckBox cb, String label, RowAction action) {
            super(cb);
            this.action = action;
            button = secondaryBtn(label);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable t, Object v, boolean sel, int r, int c) {
            row = r;
            button.setText(v != null ? v.toString() : "");
            clicked = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (clicked) action.run(row);
            clicked = false;
            return button.getText();
        }
    }
}