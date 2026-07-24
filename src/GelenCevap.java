import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class GelenCevap extends JFrame {

    public GelenCevap() {
        setTitle("Gelen Cevaplar");
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JButton backButton = new JButton("←");
        backButton.setFont(new Font("Arial", Font.BOLD, 15));
        backButton.setBackground(new Color(70, 130, 180));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(5, 17, 2, 8));
        backButton.addActionListener(e -> {
            new OgrenciSecimSayfasi().setVisible(true);
            dispose();
        }); // pencereyi kapatır

        // Başlık etiketi
        JLabel title = new JLabel("GELEN CEVAPLAR", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(new Color(70, 180, 235));
        title.setOpaque(true);
        title.setBackground(new Color(25, 50, 100));
        title.setBorder(new EmptyBorder(15, 0, 15, 0));

        // Geri butonu + Başlık için panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(25, 50, 100));
        titlePanel.add(backButton, BorderLayout.WEST);
        titlePanel.add(title, BorderLayout.CENTER);

        // Tablo modeli ve tablo
        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);

        model.addColumn("Öğretim Üyesi");
        model.addColumn("Sunulan Tarih");
        model.addColumn("Sunulan Saat");
        model.addColumn("Önerilen Saat");
        model.addColumn("Durum");

        // Tablo stil ayarları
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(new Color(25, 50, 100));
        table.setGridColor(Color.LIGHT_GRAY);
        table.setBackground(Color.WHITE);

        // Durum sütunu özel gösterimi
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);

                if (column == 4) {
                    String durum = value.toString().toLowerCase();
                    setFont(getFont().deriveFont(Font.BOLD));
                    switch (durum) {
                        case "onaylandi" -> {
                            setText("Onaylandı");
                            setForeground(new Color(0, 128, 0));
                        }
                        case "reddedildi" -> {
                            setText("Reddedildi");
                            setForeground(Color.RED);
                        }
                        default -> {
                            setText("Beklemede");
                            setForeground(Color.GRAY);
                        }
                    }
                } else {
                    setForeground(Color.BLACK);
                }
                return c;
            }
        });

        // Panel yapısı
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        mainPanel.setBackground(new Color(25, 50, 100));
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Veritabanı bağlantısı ve veri çekme
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:randevu.db")) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM onaylama");

            while (rs.next()) {
                String ogretimUyesi = rs.getString("ogretim_uyesi_ad");
                String tarih = rs.getString("tarih");
                String saat = rs.getString("saat");
                String onerilenSaat = rs.getString("onerilen_saat");
                String durum = rs.getString("durum");
                model.addRow(new Object[]{ogretimUyesi, tarih, saat, onerilenSaat, durum});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + e.getMessage());
        }

        // Temizle butonu
        JButton clearButton = new JButton("Temizle");
        clearButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        clearButton.setBackground(new Color(244, 67, 54));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.setPreferredSize(new Dimension(100, 35));
        clearButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Tüm veriler silinsin mi?", "Onay", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                model.setRowCount(0); // JTable'dan verileri sil
                try (Connection conn = DriverManager.getConnection("jdbc:sqlite:randevu.db")) {
                    Statement stmt = conn.createStatement();
                    stmt.executeUpdate("DELETE FROM onaylama");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Silme hatası: " + ex.getMessage());
                }
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(25, 50, 100));
        bottomPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        bottomPanel.add(clearButton);

        // Arayüzü birleştir
        add(titlePanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GelenCevap::new);
    }
}