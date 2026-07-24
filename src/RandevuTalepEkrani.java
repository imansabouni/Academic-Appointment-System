import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.*;

public class RandevuTalepEkrani extends JFrame {

    private static JComboBox<String> saatComboBox;
    private static JComboBox<String> tarihComboBox;
    private static JComboBox<String> hocaComboBox;
    private static JTable randevuTable;
    private static DefaultTableModel tableModel;
    private static JTextField isimTextField;

    public RandevuTalepEkrani() {
        setTitle("Randevu Talep Ekranı");
        setSize(850, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Color anaRenk = new Color(25, 50, 100);
        Color butonYesil = new Color(76, 175, 80);
        Color butonKirmizi = new Color(244, 67, 54);
        Color baslikRenk = new Color(70, 180, 235);
        Color tabloBaslikRenk = new Color(63, 81, 181);
        Color tabloArkaPlan = new Color(70, 180, 235);
        Color tabloSecimRenk = new Color(76, 175, 80);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(anaRenk);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);

        JLabel baslik = new JLabel("RANDEVU TALEP EKRANI");
        baslik.setFont(new Font("Segoe UI", Font.BOLD, 32));
        baslik.setForeground(baslikRenk);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(baslik, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JButton backButton = new JButton("←");
        backButton.setFont(new Font("Arial", Font.BOLD, 15));
        backButton.setBackground(new Color(70, 130, 180));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        panel.add(backButton, gbc);

        backButton.addActionListener(e -> {
            new OgrenciSecimSayfasi().setVisible(true);
            dispose();
        });

        gbc.gridy++;
        JLabel hocaLabel = new JLabel("Öğretim Üyesi:");
        hocaLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        hocaLabel.setForeground(Color.WHITE);
        panel.add(hocaLabel, gbc);

        gbc.gridx = 1;
        hocaComboBox = new JComboBox<>();
        hocaComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        panel.add(hocaComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel tarihLabel = new JLabel("Tarih:");
        tarihLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        tarihLabel.setForeground(Color.WHITE);
        panel.add(tarihLabel, gbc);

        gbc.gridx = 1;
        tarihComboBox = new JComboBox<>();
        tarihComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        panel.add(tarihComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel saatLabel = new JLabel("Saat:");
        saatLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        saatLabel.setForeground(Color.WHITE);
        panel.add(saatLabel, gbc);

        gbc.gridx = 1;
        saatComboBox = new JComboBox<>();
        saatComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        panel.add(saatComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        buttonPanel.setBackground(anaRenk);

        JLabel isimlabel = new JLabel("İsim");
        isimlabel.setForeground(new Color(70, 180, 235));
        isimlabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        buttonPanel.add(isimlabel);

        isimTextField = new JTextField(10);
        isimTextField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        buttonPanel.add(isimTextField);

        JButton randevuButton = new JButton("Randevu Talep Et");
        randevuButton.setBackground(butonYesil);
        randevuButton.setForeground(Color.WHITE);
        randevuButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        randevuButton.setPreferredSize(new Dimension(180, 40));

        JButton randevuSilButton = new JButton("Randevuyu Sil");
        randevuSilButton.setBackground(butonKirmizi);
        randevuSilButton.setForeground(Color.WHITE);
        randevuSilButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        randevuSilButton.setPreferredSize(new Dimension(180, 40));

        buttonPanel.add(randevuButton);
        buttonPanel.add(randevuSilButton);
        panel.add(buttonPanel, gbc);

        gbc.gridy++;
        JLabel tableLabel = new JLabel("Mevcut Randevular:");
        tableLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        tableLabel.setForeground(baslikRenk);
        panel.add(tableLabel, gbc);

        gbc.gridy++;
        tableModel = new DefaultTableModel(new Object[]{"Öğretim Üyesi", "Tarih", "Saat"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        randevuTable = new JTable(tableModel);
        randevuTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        randevuTable.setRowHeight(28);
        randevuTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        randevuTable.getTableHeader().setBackground(tabloBaslikRenk);
        randevuTable.getTableHeader().setForeground(Color.WHITE);
        randevuTable.setBackground(tabloArkaPlan);
        randevuTable.setForeground(Color.BLACK);
        randevuTable.setSelectionBackground(tabloSecimRenk);
        randevuTable.setSelectionForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(randevuTable);
        scrollPane.setPreferredSize(new Dimension(700, 200));
        panel.add(scrollPane, gbc);

        add(panel);
        setVisible(true);

        loadHocalar();

        hocaComboBox.addActionListener(e -> loadTarihler());
        tarihComboBox.addActionListener(e -> loadSaatler());

        randevuButton.addActionListener(e -> {
            String hoca = (String) hocaComboBox.getSelectedItem();
            String tarih = (String) tarihComboBox.getSelectedItem();
            String saat = (String) saatComboBox.getSelectedItem();
            String isim = isimTextField.getText().trim();

            if (hoca == null || tarih == null || saat == null || isim.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!");
                return;
            }

            tableModel.addRow(new Object[]{hoca, tarih, saat});

            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:randevu.db")) {
                String sql = "INSERT INTO randevular (ogrenci_ad, ogretim_uyesi, tarih, saat) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, isim);
                stmt.setString(2, hoca);
                stmt.setString(3, tarih);
                stmt.setString(4, saat);
                stmt.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Veritabanına kayıt hatası!");
            }
        });

        randevuSilButton.addActionListener(e -> {
            int selectedRow = randevuTable.getSelectedRow();
            if (selectedRow != -1) {
                String hoca = tableModel.getValueAt(selectedRow, 0).toString();
                String tarih = tableModel.getValueAt(selectedRow, 1).toString();
                String saat = tableModel.getValueAt(selectedRow, 2).toString();
                tableModel.removeRow(selectedRow);

                try (Connection conn = DriverManager.getConnection("jdbc:sqlite:randevu.db")) {
                    String sql = "DELETE FROM randevular WHERE ogretim_uyesi = ? AND tarih = ? AND saat = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, hoca);
                    stmt.setString(2, tarih);
                    stmt.setString(3, saat);
                    stmt.executeUpdate();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Veritabanı silme hatası!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen silmek için bir randevu seçin.");
            }
        });
    }

    private static void loadHocalar() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:randevu.db")) {
            String sql = "SELECT DISTINCT ogretim_uyesi FROM musait_saatler";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            hocaComboBox.removeAllItems();
            while (rs.next()) {
                hocaComboBox.addItem(rs.getString("ogretim_uyesi"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Hoca bilgileri çekilemedi.");
        }
    }

    private static void loadTarihler() {
        tarihComboBox.removeAllItems();
        saatComboBox.removeAllItems();
        String hoca = (String) hocaComboBox.getSelectedItem();
        if (hoca == null) return;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:randevu.db")) {
            String sql = "SELECT DISTINCT tarih FROM musait_saatler WHERE ogretim_uyesi = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, hoca);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tarihComboBox.addItem(rs.getString("tarih"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Tarih bilgisi çekilemedi.");
        }
    }

    private static void loadSaatler() {
        saatComboBox.removeAllItems();
        String hoca = (String) hocaComboBox.getSelectedItem();
        String tarih = (String) tarihComboBox.getSelectedItem();
        if (hoca == null || tarih == null) return;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:randevu.db")) {
            String sql = "SELECT saat FROM musait_saatler WHERE ogretim_uyesi = ? AND tarih = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, hoca);
            stmt.setString(2, tarih);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                saatComboBox.addItem(rs.getString("saat"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Saat bilgisi çekilemedi.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RandevuTalepEkrani::new);
    }
}
