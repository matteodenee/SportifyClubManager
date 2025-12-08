package SportifyClubManager.src;

public class LoginController {

    // controleur pour le bouton login
    private final LoginFacade loginFacade;
    private LoginFrame loginFrame;

    public LoginController() {
        // par defaut on prend la facade globale
        this(LoginFacade.createLoginFacade());
    }

    public LoginController(LoginFacade loginFacade) {
        this.loginFacade = loginFacade;
    }

    public void setLoginFrame(LoginFrame frame) {
        // la vue se connecte au controleur
        this.loginFrame = frame;
    }

    public void onClick(String id, String pwd) {
        // clic sur le bouton login
        User user = loginFacade.login(id, pwd);

        if (loginFrame == null) {
            // pas de vue attachee donc on sort
            return;
        }

        if (user != null) {
            loginFrame.showLoginSuccess(user);
        } else {
            loginFrame.showLoginError();
        }
    }
}