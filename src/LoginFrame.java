import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("Akademik Randevu ve Takip Sistemi - Giriş");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(25, 50, 100));

        JLabel titleLabel = new JLabel("Randevu Sistemi Girişi");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(70, 180, 235));
        titleLabel.setBounds(140, 60, 300, 40);
        panel.add(titleLabel);

        JLabel usernameLabel = new JLabel("Kullanıcı Email:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setBounds(70, 140, 120, 30);
        panel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameField.setBounds(200, 140, 220, 35);
        usernameField.setBackground(new Color(236, 240, 241));
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Şifre:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setBounds(70, 200, 120, 30);
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setBounds(200, 200, 220, 35);
        passwordField.setBackground(new Color(236, 240, 241));
        panel.add(passwordField);

        loginButton = new JButton("Giriş Yap");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(new Color(0, 102, 204));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBounds(180, 280, 140, 45);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder());
        panel.add(loginButton);

        // Sağ üst köşeye kullanıcı ikonu
        ImageIcon icon = new ImageIcon("icon.png"); // Dosya yolunu burada ayarla
        Image scaledIcon = icon.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
        JButton profileButton = new JButton(new ImageIcon(scaledIcon));
        profileButton.setBounds(420, 20, 40, 60);
        profileButton.setBorderPainted(false);
        profileButton.setContentAreaFilled(false);
        profileButton.setFocusPainted(false);
        profileButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(profileButton);

        // Profile ikonuna tıklanınca kayıt ekranı açılır
        profileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RegistrationForm().setVisible(true);
                dispose();
            }
        });

        // Giriş butonuna tıklanınca kontrol
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                boolean isAuthenticated = authenticateUser(username, password);

                if (isAuthenticated) {
                    String role = getUserRole(username, password);

                    if (role.equals("öğrenci")) {
                        SwingUtilities.invokeLater(() -> {
                            new OgrenciSecimSayfasi().setVisible(true);
                            dispose();
                        });
                    } else if (role.equals("öğretmen")) {
                        SwingUtilities.invokeLater(() -> {
                            new OgretmenSecimSayfasi().setVisible(true);
                            dispose();
                        });
                    }
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Kullanıcı adı veya şifre yanlış!", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        add(panel);
    }

    private boolean authenticateUser(String username, String password) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:randevu.db")) {
            String sql = "SELECT * FROM kullanici WHERE email = ? AND sifre = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return true;
            } else {
                System.out.println("Kullanıcı adı veya şifre hatalı.");
                System.out.println("Girilen Kullanıcı Adı: " + username);
                System.out.println("Girilen Şifre: " + password);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Veritabanı hatası: " + e.getMessage());
            return false;
        }
    }

    private String getUserRole(String username, String password) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:randevu.db")) {
            String sql = "SELECT rol FROM kullanici WHERE email = ? AND sifre = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("rol");
            } else {
                System.out.println("Kullanıcı rolü bulunamadı.");
                return "";
            }
        } catch (SQLException e) {
            System.err.println("Veritabanı hatası: " + e.getMessage());
        }
        return "";
    }

    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
