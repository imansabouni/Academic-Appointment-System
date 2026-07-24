// (Diğer importlar aynı)
import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.sql.*;
import java.util.*;

public class MusaitSaatEkleme extends JFrame {
    // Tüm değişkenler aynı
    private JPanel takvimPanel;
    private JComboBox<String> aySecComboBox;
    private JComboBox<String> saatComboBox;
    private JTextField ogretimUyesiAdTextField;
    private JButton kaydetBtn;
    private DefaultListModel<String> secilenListModel;
    private JList<String> secilenList;

    private Map<LocalDate, String> secilenTarihSaatMap = new LinkedHashMap<>();
    private LocalDate secilenTarih = null;
    private int secilenAyOffset = 0;
    private JButton secilenGunBtn = null;

    public MusaitSaatEkleme() {
        setTitle("📅 Müsait Gün ve Saat Belirleme");
        setSize(800, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(25, 50, 100));

        // Üst panel
        JPanel baslikPanel = new JPanel(new BorderLayout());
        baslikPanel.setBackground(new Color(25, 50, 100));
        JLabel baslik = new JLabel("Takvimden birden fazla gün seçin, her biri için saat belirtin", SwingConstants.CENTER);
        baslik.setFont(new Font("Segoe UI", Font.BOLD, 24));
        baslik.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        baslik.setForeground(new Color(70, 180, 235));
        baslikPanel.add(baslik, BorderLayout.CENTER);

        // Sol üst köşe
        JButton anaSayfayaDonBtn = new JButton("←");
        anaSayfayaDonBtn.setBackground(new Color(70, 130, 180));
        anaSayfayaDonBtn.setForeground(Color.WHITE);
        anaSayfayaDonBtn.setFocusPainted(false);
        anaSayfayaDonBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        anaSayfayaDonBtn.setPreferredSize(new Dimension(50, 30));
        anaSayfayaDonBtn.addActionListener(e -> {
            new OgretmenSecimSayfasi().setVisible(true);
            dispose();
        });

        JPanel solPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        solPanel.setBackground(new Color(25, 50, 100));
        solPanel.add(anaSayfayaDonBtn);
        baslikPanel.add(solPanel, BorderLayout.WEST);

        // Ay seçimi
        String[] aylar = new String[4];
        LocalDate bugun = LocalDate.now();
        for (int i = 0; i < 4; i++) {
            aylar[i] = bugun.plusMonths(i).getMonth().getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault()) + " " + bugun.plusMonths(i).getYear();
        }
        aySecComboBox = new JComboBox<>(aylar);
        aySecComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        aySecComboBox.addActionListener(e -> {
            secilenAyOffset = aySecComboBox.getSelectedIndex();
            ayTakvimiOlustur(LocalDate.now().plusMonths(secilenAyOffset));
        });
        aySecComboBox.setBackground(new Color(50, 90, 150));
        aySecComboBox.setForeground(Color.WHITE);

        // Takvim paneli
        takvimPanel = new JPanel(new GridLayout(0, 7, 6, 6));
        takvimPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        takvimPanel.setBackground(new Color(50, 90, 150));
        add(takvimPanel, BorderLayout.CENTER);

        // Alt panel
        JPanel altPanel = new JPanel();
        altPanel.setLayout(new BoxLayout(altPanel, BoxLayout.Y_AXIS));
        altPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        altPanel.setBackground(new Color(25, 50, 100));
        altPanel.add(aySecComboBox, BorderLayout.EAST);
        add(baslikPanel, BorderLayout.NORTH);

        // Öğretim Üyesi
        JPanel hocaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        hocaPanel.setBackground(new Color(25, 50, 100));
        JLabel hocalabel=new JLabel("Öğretim Üyesi Adı:      ");
        hocalabel.setForeground(Color.WHITE);
        hocalabel.setFont(new Font("Segoe UI",Font.BOLD, 14));
        
        hocaPanel.add(hocalabel);
        ogretimUyesiAdTextField = new JTextField(15);
        hocaPanel.add(ogretimUyesiAdTextField);
        altPanel.add(hocaPanel);

        // Saat seçimi paneli
        JPanel saatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        saatPanel.setBackground(new Color(25, 50, 100));
        JLabel saatlabel=new JLabel("Saat Seçin:          ");
        saatlabel.setForeground(Color.WHITE);
        saatlabel.setFont(new Font("Segoe UI",Font.BOLD, 14));
        saatPanel.add(saatlabel);
        saatComboBox = new JComboBox<>(new String[]{
            "09:00 - 09:20", "10:20 - 10:40", "11:40 - 12:00",
            "13:00 - 13:20", "14:20 - 12:40", "16:40 - 17:00"
        });
        saatPanel.add(saatComboBox);

        JButton saatEkleBtn = new JButton("Tarihe Ekle");
        saatEkleBtn.setBackground(new Color(76, 175, 80));
        saatEkleBtn.setForeground(Color.WHITE);
        saatPanel.add(saatEkleBtn);

        JButton silBtn = new JButton("Tarihi Sil");
        silBtn.setBackground(new Color(204, 0, 0));
        silBtn.setForeground(Color.WHITE);
        saatPanel.add(silBtn);

        altPanel.add(saatPanel);

        // Liste paneli
        secilenListModel = new DefaultListModel<>();
        secilenList = new JList<>(secilenListModel);
        secilenList.setVisibleRowCount(5);
        secilenList.setFixedCellWidth(400);
        JScrollPane scrollPane = new JScrollPane(secilenList);
        altPanel.add(scrollPane);

        // Kaydet
        kaydetBtn = new JButton("Kaydet");
        kaydetBtn.setBackground(new Color(70, 130, 180));
        kaydetBtn.setForeground(Color.WHITE);
        kaydetBtn.setFocusPainted(false);
        kaydetBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        altPanel.add(Box.createVerticalStrut(10));
        altPanel.add(kaydetBtn);

        add(altPanel, BorderLayout.SOUTH);

        // Olaylar
        saatEkleBtn.addActionListener(e -> {
            if (secilenTarih != null) {
                String saat = (String) saatComboBox.getSelectedItem();
                String tarihVeSaat = secilenTarih.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " - " + saat;
                secilenTarihSaatMap.put(secilenTarih, saat);
                if (!secilenListModel.contains(tarihVeSaat)) {
                    secilenListModel.addElement(tarihVeSaat);
                }
            }
        });

        silBtn.addActionListener(e -> {
            if (secilenTarih != null && secilenTarihSaatMap.containsKey(secilenTarih)) {
                String saat = secilenTarihSaatMap.remove(secilenTarih);
                String tarihVeSaat = secilenTarih.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " - " + saat;
                secilenListModel.removeElement(tarihVeSaat);
                JOptionPane.showMessageDialog(this, "Seçilen tarih kaldırıldı.");
            }
        });

        kaydetBtn.addActionListener(e -> {
            String ogretimUyesiAd = ogretimUyesiAdTextField.getText().trim();
            if (ogretimUyesiAd.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen öğretim üyesi adını girin.");
                return;
            }

            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:randevu.db")) {
                Statement stmt = conn.createStatement();
                stmt.execute("CREATE TABLE IF NOT EXISTS musait_saatler (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "ogretim_uyesi TEXT, tarih TEXT, saat TEXT)");

                String sql = "INSERT INTO musait_saatler (ogretim_uyesi, tarih, saat) VALUES (?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                for (Map.Entry<LocalDate, String> entry : secilenTarihSaatMap.entrySet()) {
                    ps.setString(1, ogretimUyesiAd);
                    ps.setString(2, entry.getKey().toString());
                    ps.setString(3, entry.getValue());
                    ps.addBatch();
                }
                ps.executeBatch();
                JOptionPane.showMessageDialog(this, "Kayıtlar başarıyla eklendi!");
                secilenTarihSaatMap.clear();
                secilenListModel.clear();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Veritabanı hatası oluştu.");
            }
        });

        ayTakvimiOlustur(bugun);
        setVisible(true);
    }

    private void ayTakvimiOlustur(LocalDate ay) {
        takvimPanel.removeAll();
        takvimPanel.setBackground(new Color(25, 50, 100));

        LocalDate ilkGun = ay.withDayOfMonth(1);
        int baslangicGunu = ilkGun.getDayOfWeek().getValue();
        int toplamGun = ay.lengthOfMonth();

        String[] gunler = {"Pzt", "Sal", "Çar", "Per", "Cum", "Cmt", "Paz"};
        for (String gun : gunler) {
            JLabel l = new JLabel(gun, SwingConstants.CENTER);
            l.setFont(new Font("Segoe UI", Font.BOLD, 14));
            l.setForeground(new Color(90, 200, 255));
            takvimPanel.add(l);
        }

        for (int i = 1; i < baslangicGunu; i++) {
            takvimPanel.add(new JLabel(""));
        }

        for (int gun = 1; gun <= toplamGun; gun++) {
            JButton gunBtn = new JButton(String.valueOf(gun));
            gunBtn.setPreferredSize(new Dimension(40, 30));
            gunBtn.setMargin(new Insets(2, 2, 2, 2));
            LocalDate tarih = ay.withDayOfMonth(gun);

            gunBtn.addActionListener(e -> {
                if (secilenGunBtn != null) {
                    secilenGunBtn.setBackground(null);
                }
                secilenGunBtn = gunBtn;
                secilenGunBtn.setBackground(new Color(70, 130, 180));
                secilenTarih = tarih;
            });
            takvimPanel.add(gunBtn);
        }

        takvimPanel.revalidate();
        takvimPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MusaitSaatEkleme::new);
    }
}
