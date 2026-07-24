import jakarta.mail.*;
import jakarta.mail.internet.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;
import java.util.Properties;

public class OgretimUyesiPaneli extends JFrame {
    private static DefaultTableModel tableModel;
    private static JTable taleplerTablosu;
    private JTextField hocaAdField;
    private JButton kontrolButton, onaylaButton, reddetButton, geriButton;

    public OgretimUyesiPaneli() {
        super("Öğretim Üyesi Randevu Talepleri");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Ana panel
        JPanel anaPanel = new JPanel();
        anaPanel.setLayout(new BoxLayout(anaPanel, BoxLayout.Y_AXIS));
        anaPanel.setBackground(new Color(25, 50, 100));

        // Başlık
        JLabel baslikLabel = new JLabel("Gelen Talepler", SwingConstants.CENTER);
        baslikLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        baslikLabel.setOpaque(true);
        baslikLabel.setForeground(new Color(70, 180, 235));
        baslikLabel.setBackground(new Color(25, 50, 100));
        baslikLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        anaPanel.add(baslikLabel);

        // Input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.setBackground(new Color(25, 50, 100));
        hocaAdField = new JTextField(15);
        kontrolButton = new JButton("Randevuları Getir");
        geriButton = new JButton("←");
        geriButton.setFont(new Font("Arial", Font.BOLD, 15));
        geriButton.setBackground(new Color(70, 130, 180));
        geriButton.setForeground(Color.WHITE);
        geriButton.setFocusPainted(false);

        JLabel isimLabel = new JLabel("   Öğretim Üyesi Adı:          ");
        isimLabel.setFont(new Font("Arial", Font.BOLD, 15));
        isimLabel.setForeground(Color.WHITE);

        inputPanel.add(geriButton);
        inputPanel.add(isimLabel);
        inputPanel.add(hocaAdField);
        inputPanel.add(kontrolButton);
        anaPanel.add(inputPanel);

        // Tablo kolonları
        String[] kolonlar = {"Öğrenci Adı", "Tarih", "Saat", "Durum"};
        tableModel = new DefaultTableModel(kolonlar, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        taleplerTablosu = new JTable(tableModel);
        taleplerTablosu.setBackground(new Color(25, 40, 100));
        taleplerTablosu.setFont(new Font("Arial", Font.BOLD, 12));
        taleplerTablosu.setForeground(new Color(70, 180, 235));
        taleplerTablosu.setSelectionBackground(new Color(70, 130, 180));
        taleplerTablosu.setSelectionForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(taleplerTablosu);
        anaPanel.add(scrollPane);

        JTableHeader header = taleplerTablosu.getTableHeader();
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(new Color(25, 50, 100));
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));

        // Buton paneli
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(25, 50, 100));
        onaylaButton = new JButton("Onayla");
        reddetButton = new JButton("Reddet");
        onaylaButton.setBackground(new Color(76, 175, 80));
        reddetButton.setBackground(new Color(204, 0, 0));
        onaylaButton.setForeground(Color.WHITE);
        reddetButton.setForeground(Color.WHITE);

        buttonPanel.add(onaylaButton);
        buttonPanel.add(reddetButton);
        anaPanel.add(buttonPanel);

        add(anaPanel);

        // Butonların actionları
        kontrolButton.addActionListener(e -> {
            String hocaAd = hocaAdField.getText().trim();
            if (!hocaAd.isEmpty()) {
                loadTaleplerForHoca(hocaAd);
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen bir öğretim üyesi adı girin.");
            }
        });

        geriButton.addActionListener(e -> {
            new OgretmenSecimSayfasi().setVisible(true);
            dispose();
        });

        onaylaButton.addActionListener(e -> {
            int selectedRow = taleplerTablosu.getSelectedRow();
            if (selectedRow != -1) {
                String ogrenciAd = tableModel.getValueAt(selectedRow, 0).toString();
                String tarih = tableModel.getValueAt(selectedRow, 1).toString();
                String saat = tableModel.getValueAt(selectedRow, 2).toString();
                String hocaAd = hocaAdField.getText().trim();

                try (Connection conn = DriverManager.getConnection("jdbc:sqlite:randevu.db")) {
                    String insertSql = "INSERT INTO onaylama (ogrenci_ad, tarih, saat, ogretim_uyesi_ad, durum) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                    insertStmt.setString(1, ogrenciAd);
                    insertStmt.setString(2, tarih);
                    insertStmt.setString(3, saat);
                    insertStmt.setString(4, hocaAd);
                    insertStmt.setString(5, "onaylandi");
                    insertStmt.executeUpdate();

                    String deleteSql = "DELETE FROM randevular WHERE ogrenci_ad = ? AND tarih = ? AND saat = ?";
                    PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                    deleteStmt.setString(1, ogrenciAd);
                    deleteStmt.setString(2, tarih);
                    deleteStmt.setString(3, saat);
                    deleteStmt.executeUpdate();

                    tableModel.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this, "Seçilen randevu onaylandı.");

                    sendEmailToStudent(hocaAd, "onaylandi", null);


                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Hata oluştu.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen bir randevu seçin.");
            }
        });

        reddetButton.addActionListener(e -> {
            int selectedRow = taleplerTablosu.getSelectedRow();
            if (selectedRow != -1) {
                String ogrenciAd = tableModel.getValueAt(selectedRow, 0).toString();
                String tarih = tableModel.getValueAt(selectedRow, 1).toString();
                String saat = tableModel.getValueAt(selectedRow, 2).toString();
                String hocaAd = hocaAdField.getText().trim();
                String yeniSaat = JOptionPane.showInputDialog(this, "Lütfen yeni bir saat önerin:");

                if (yeniSaat != null && !yeniSaat.trim().isEmpty()) {
                    logOnaylama(ogrenciAd, tarih, saat, hocaAd, "reddedildi", yeniSaat);

                    try (Connection conn = DriverManager.getConnection("jdbc:sqlite:randevu.db")) {
                        String deleteSql = "DELETE FROM randevular WHERE ogrenci_ad = ? AND tarih = ? AND saat = ?";
                        PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                        deleteStmt.setString(1, ogrenciAd);
                        deleteStmt.setString(2, tarih);
                        deleteStmt.setString(3, saat);
                        deleteStmt.executeUpdate();

                        tableModel.removeRow(selectedRow);
                        JOptionPane.showMessageDialog(this, "Yeni saat önerildi.");

                        sendEmailToStudent(hocaAd, "yeni saat önerildi", yeniSaat);

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Hata oluştu.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Yeni saat boş olamaz.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen bir randevu seçin.");
            }
        });
    }

    private void loadTaleplerForHoca(String hocaAd) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:randevu.db")) {
            String query = "SELECT r.ogrenci_ad, r.tarih, r.saat, IFNULL(o.durum, 'beklemede') as durum " +
                    "FROM randevular r LEFT JOIN onaylama o ON r.ogrenci_ad = o.ogrenci_ad " +
                    "AND r.tarih = o.tarih AND r.saat = o.saat " +
                    "WHERE r.ogretim_uyesi = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, hocaAd);
            ResultSet rs = stmt.executeQuery();

            tableModel.setRowCount(0);
            boolean veriVarMi = false;

            while (rs.next()) {
                veriVarMi = true;
                tableModel.addRow(new Object[]{
                        rs.getString("ogrenci_ad"),
                        rs.getString("tarih"),
                        rs.getString("saat"),
                        rs.getString("durum")
                });
            }

            if (!veriVarMi) {
                JOptionPane.showMessageDialog(this, "Bu öğretim üyesi için hiç randevu bulunamadı.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Veritabanı hatası.");
        }
    }

    private void logOnaylama(String ogrenciAd, String tarih, String saat, String hocaAd, String durum, String yeniSaat) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:randevu.db")) {
            String sql = "INSERT INTO onaylama (ogrenci_ad, tarih, saat, ogretim_uyesi_ad, durum) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, ogrenciAd);
            stmt.setString(2, tarih);
            stmt.setString(3, yeniSaat != null ? yeniSaat : saat);
            stmt.setString(4, hocaAd);
            stmt.setString(5, durum);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Log kaydı oluşturulamadı.");
        }
    }

    private void sendEmailToStudent(String hocaAd, String durum, String yeniSaat) {
        // Sabit öğrenci e-postası: iman
        String to = "imansabouni1234@gmail.com";  // Buraya kendi e-posta adresini yaz

        final String username = "imansabouni1234@gmail.com";  // Gmail adresin
        final String password = "wfdv kxoh aodv qiif"; 

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new jakarta.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );
            message.setSubject("Randevu Durumu");

            String content;
            if ("onaylandi".equals(durum)) {
                content = String.format("Merhaba İman,\n\nRandevunuz %s  Hoca tarafından onaylandı.", hocaAd);
            } else {
                content = String.format("Merhaba İman,\n\nRandevunuz %s Hoca tarafından reddedildi.", hocaAd);
                if (yeniSaat != null && !yeniSaat.trim().isEmpty()) {
                    content += String.format("\nYeni saat önerisi: %s", yeniSaat);
                }
            }

            message.setText(content);

            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "E-posta gönderilemedi.");
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OgretimUyesiPaneli().setVisible(true));
    }
}
