import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegistrationForm extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JComboBox<String> userTypeComboBox;
    private JButton registerButton, backButton, forgotPasswordButton;

    public RegistrationForm() {
        setTitle("Kullanıcı Kaydı - Akademik Randevu ve Takip Sistemi");
        setSize(500, 500);  // Boyutu 500x500 yaptık
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Pencereyi ortala

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(25, 50, 100));

        // Geri butonu
        backButton = new JButton("←");
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

        JLabel titleLabel = new JLabel("Kullanıcı Kaydı");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(70, 180, 235));
        titleLabel.setBounds(180, 20, 250, 30);  // Ortalamaya yakın konum
        panel.add(titleLabel);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        emailLabel.setForeground(Color.WHITE);
        emailLabel.setBounds(70, 80, 120, 30);
        panel.add(emailLabel);

        emailField = new JTextField();
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setBounds(220, 80, 180, 30);
        emailField.setBackground(new Color(236, 240, 241));
        panel.add(emailField);

        JLabel passwordLabel = new JLabel("Şifre:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setBounds(70, 130, 120, 30);
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBounds(220, 130, 180, 30);
        passwordField.setBackground(new Color(236, 240, 241));
        panel.add(passwordField);

        JLabel firstNameLabel = new JLabel("İsim:");
        firstNameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        firstNameLabel.setForeground(Color.WHITE);
        firstNameLabel.setBounds(70, 180, 120, 30);
        panel.add(firstNameLabel);

        firstNameField = new JTextField();
        firstNameField.setFont(new Font("Arial", Font.PLAIN, 14));
        firstNameField.setBounds(220, 180, 180, 30);
        firstNameField.setBackground(new Color(236, 240, 241));
        panel.add(firstNameField);

        JLabel lastNameLabel = new JLabel("Soyisim:");
        lastNameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        lastNameLabel.setForeground(Color.WHITE);
        lastNameLabel.setBounds(70, 230, 120, 30);
        panel.add(lastNameLabel);

        lastNameField = new JTextField();
        lastNameField.setFont(new Font("Arial", Font.PLAIN, 14));
        lastNameField.setBounds(220, 230, 180, 30);
        lastNameField.setBackground(new Color(236, 240, 241));
        panel.add(lastNameField);

        JLabel userTypeLabel = new JLabel("Kullanıcı Türü:");
        userTypeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userTypeLabel.setForeground(Color.WHITE);
        userTypeLabel.setBounds(70, 280, 120, 30);
        panel.add(userTypeLabel);

        userTypeComboBox = new JComboBox<>(new String[]{"Öğrenci", "Öğretmen"});
        userTypeComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        userTypeComboBox.setBounds(220, 280, 180, 30);
        userTypeComboBox.setBackground(new Color(236, 240, 241));
        panel.add(userTypeComboBox);

        registerButton = new JButton("Kayıt Ol");
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.setBackground(new Color(70, 130, 180));
        registerButton.setForeground(Color.WHITE);
        registerButton.setBounds(170, 340, 160, 40);
        registerButton.setFocusPainted(false);
        registerButton.setBorder(BorderFactory.createEmptyBorder());
        panel.add(registerButton);

        // Şifremi Unuttum butonu
        forgotPasswordButton = new JButton("Şifremi Unuttum");
        forgotPasswordButton.setFont(new Font("Arial", Font.PLAIN, 13));
        forgotPasswordButton.setBounds(170, 390, 160, 30);
        forgotPasswordButton.setFocusPainted(false);
        panel.add(forgotPasswordButton);

        registerButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String userType = userTypeComboBox.getSelectedItem().toString().toLowerCase();

            if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurunuz.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$")) {
                JOptionPane.showMessageDialog(this, "Geçerli bir email adresi giriniz.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (password.length() < 6) {
                JOptionPane.showMessageDialog(this, "Şifre en az 6 karakter olmalıdır.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (userAlreadyExists(email)) {
                JOptionPane.showMessageDialog(this, "Bu kullanıcı zaten kayıtlı. Lütfen giriş yapın.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean isRegistered = registerUser(email, password, firstName, lastName, userType);

            if (isRegistered) {
                JOptionPane.showMessageDialog(this, "Kayıt başarılı.");
                new LoginFrame().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Kayıt sırasında bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        });

        forgotPasswordButton.addActionListener(e -> {
            String email = JOptionPane.showInputDialog(this, "Şifrenizi sıfırlamak için e-posta adresinizi girin:");
            if (email != null && !email.trim().isEmpty()) {
                if (!userAlreadyExists(email)) {
                    JOptionPane.showMessageDialog(this, "Bu email ile kayıtlı bir kullanıcı bulunamadı.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String newPassword = JOptionPane.showInputDialog(this, "Yeni şifrenizi girin:");
                if (newPassword != null && newPassword.length() >= 6) {
                    updatePassword(email, newPassword);
                    JOptionPane.showMessageDialog(this, "Şifre başarıyla güncellendi.");
                } else {
                    JOptionPane.showMessageDialog(this, "Şifre en az 6 karakter olmalı.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        add(panel);
    }

    private boolean userAlreadyExists(String email) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:randevu.db")) {
            String kontrolSQL = "SELECT COUNT(*) FROM kullanici WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(kontrolSQL);
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();
            boolean exists = rs.next() && rs.getInt(1) > 0;
            rs.close();
            checkStmt.close();
            return exists;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean registerUser(String email, String password, String firstName, String lastName, String userType) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:randevu.db")) {
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS kullanici (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE, sifre TEXT, isim TEXT, soyisim TEXT, rol TEXT)");
            stmt.close();

            String hashedPassword = hashPassword(password);

            String sql = "INSERT INTO kullanici (email, sifre, isim, soyisim, rol) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, firstName);
            pstmt.setString(4, lastName);
            pstmt.setString(5, userType);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Veritabanı hatası: " + e.getMessage());
            return false;
        }
    }

    private void updatePassword(String email, String newPassword) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:randevu.db")) {
            String hashedPassword = hashPassword(newPassword);
            String sql = "UPDATE kullanici SET sifre = ? WHERE email = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String hashPassword(String password) {
        return password; // Hashleme yapma, şifreyi düz olarak döndür
    }


    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new RegistrationForm().setVisible(true));
    }
}
