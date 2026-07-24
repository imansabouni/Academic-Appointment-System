import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class OgretmenSecimSayfasi extends JFrame {

    public OgretmenSecimSayfasi() {
        setTitle("ÖĞRETMEN İŞLEM EKRANI");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(25, 50, 100));
        add(panel);

        // Geri Butonu
        JButton backButton = new JButton("←");
        backButton.setFont(new Font("Arial", Font.BOLD, 15));
        backButton.setBounds(20, 20, 50, 30);
        backButton.setBackground(new Color(70, 130, 180));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        panel.add(backButton);

        backButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        // Başlık
        JLabel baslikLabel = new JLabel("Ders Takvimi ve Talepler", SwingConstants.CENTER);
        baslikLabel.setFont(new Font("Arial Black", Font.BOLD, 24));
        baslikLabel.setForeground(new Color(180, 220, 250));
        baslikLabel.setBounds(50, 20, 500, 40);
        panel.add(baslikLabel);

        // TAKVİM
        ImageIcon takvimIcon = new ImageIcon("takvim.png");
        Image takvimImg = takvimIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        JLabel takvimLabel = new JLabel(new ImageIcon(takvimImg));
        takvimLabel.setBounds(100, 100, 120, 120);
        takvimLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.add(takvimLabel);

        JLabel takvimText = new JLabel("Saat Belirle", SwingConstants.CENTER);
        takvimText.setForeground(new Color(180, 220, 250));
        takvimText.setFont(new Font("Arial", Font.BOLD, 18));
        takvimText.setBounds(100, 220, 120, 25);
        panel.add(takvimText);

        // TALEPLER
        ImageIcon emailIcon = new ImageIcon("email_logo.png");
        Image emailImg = emailIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        JLabel emailLabel = new JLabel(new ImageIcon(emailImg));
        emailLabel.setBounds(360, 100, 120, 120);
        emailLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.add(emailLabel);

        JLabel emailText = new JLabel("Talepleri Gör", SwingConstants.CENTER);
        emailText.setForeground(new Color(180, 220, 250));
        emailText.setFont(new Font("Arial", Font.BOLD, 18));
        emailText.setBounds(360, 220, 120, 25);
        panel.add(emailText);

        // Mouse olayları
        takvimLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new MusaitSaatEkleme().setVisible(true);
                dispose();
            }
        });

        emailLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new OgretimUyesiPaneli().setVisible(true);
                dispose();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OgretmenSecimSayfasi().setVisible(true));
    }
} 