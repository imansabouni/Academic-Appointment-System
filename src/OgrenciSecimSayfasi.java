import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class OgrenciSecimSayfasi extends JFrame {

    public OgrenciSecimSayfasi() {
        setTitle("ÖĞRENCİ İŞLEM EKRANI");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(25, 50, 100));
        add(panel);

        // Geri Dön Butonu
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
        JLabel baslikLabel = new JLabel("Gelen Kutusu ve Talepler", SwingConstants.CENTER);
        baslikLabel.setFont(new Font("Arial Black", Font.BOLD, 24));
        baslikLabel.setForeground(new Color(180, 220, 250));
        baslikLabel.setBounds(50, 20, 500, 40);
        panel.add(baslikLabel);

        // TAKVİM (Saat Belirleme)
        ImageIcon talepIcon = new ImageIcon("talep.png");
        Image talepImg = talepIcon.getImage().getScaledInstance(110, 90, Image.SCALE_SMOOTH);
        JLabel talepLabel = new JLabel(new ImageIcon(talepImg));
        talepLabel.setBounds(100, 100, 120, 120);
        talepLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.add(talepLabel);

        JLabel talepText = new JLabel("Randevu İste", SwingConstants.CENTER);
        talepText.setForeground(new Color(180, 220, 250));
        talepText.setFont(new Font("Arial", Font.BOLD, 18));
        talepText.setBounds(100, 220, 120, 25);
        panel.add(talepText);

        // MAİL (Talepleri Gör)
        ImageIcon kutumIcon = new ImageIcon("gelenkutu.png");
        Image kutumImg = kutumIcon.getImage().getScaledInstance(120, 110, Image.SCALE_SMOOTH);
        JLabel kutumLabel = new JLabel(new ImageIcon(kutumImg));
        kutumLabel.setBounds(360, 100, 120, 120);
        kutumLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.add(kutumLabel);

        JLabel emailText = new JLabel("Gelen Kutusu", SwingConstants.CENTER);
        emailText.setForeground(new Color(180, 220, 250));
        emailText.setFont(new Font("Arial", Font.BOLD, 18));
        emailText.setBounds(360, 220, 140, 25); // 120 -> 140 yapıldı
        panel.add(emailText);

        // Mouse olayları
        talepLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new RandevuTalepEkrani().setVisible(true);
                dispose();
            }
        });

        kutumLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new GelenCevap().setVisible(true);
                dispose();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OgrenciSecimSayfasi().setVisible(true));
    }
}
