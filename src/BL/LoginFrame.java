package SportifyClubManager.src;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class LoginFrame extends JFrame {
    // fenetre Swing pour la page login
    private final LoginController loginController;

    private JTextField idField;
    private JPasswordField pwdField;
    private JLabel messageLabel;

    public LoginFrame() {
        this(new LoginController());
    }

    public LoginFrame(LoginController controller) {
        super("Sportify Club Manager - Login");
        this.loginController = controller;
        this.loginController.setLoginFrame(this);
        initComponents();
    }

    private void initComponents() {
        idField = new JTextField(15);
        pwdField = new JPasswordField(15);
        messageLabel = new JLabel(" ");

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(event -> {
            String id = idField.getText().trim();
            String pwd = new String(pwdField.getPassword());
            loginController.onClick(id, pwd);
        });

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("User id:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(pwdField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(loginButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(messageLabel, gbc);

        setContentPane(panel);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void showLoginSuccess(User user) {
        // message simple en cas de succes
        messageLabel.setText("Welcome " + user.getId() + "!");
    }

    public void showLoginError() {
        // message simple en cas d'erreur
        messageLabel.setText("Invalid credentials.");
    }

    public static void main(String[] args) {
        // entree standard pour lancer la fenetre Swing
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
