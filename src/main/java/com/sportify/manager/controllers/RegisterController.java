package com.sportify.manager.controllers;

import com.sportify.manager.facade.RegisterFacade;
import com.sportify.manager.frame.RegisterFrame;
import javafx.application.Platform;


public class RegisterController {

    private final RegisterFacade registerFacade;
    private RegisterFrame registerFrame;

    public RegisterController() {

        this.registerFacade = RegisterFacade.createRegisterFacade();
    }

    public RegisterController(RegisterFacade registerFacade) {
        this.registerFacade = registerFacade;
    }

    public void setRegisterFrame(RegisterFrame registerFrame) {
        this.registerFrame = registerFrame;
    }


    public void onClick() {
        if (registerFrame == null) return;


        String userId = registerFrame.getUserId().trim();
        String displayName = registerFrame.getUserName().trim();
        String email = registerFrame.getEmail().trim();
        String password = registerFrame.getPassword();
        String confirmPassword = registerFrame.getConfirmPassword();
        String role = registerFrame.getRole();


        if (userId.isEmpty() || displayName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            registerFrame.showError("Veuillez remplir tous les champs obligatoires.");
            return;
        }


        if (!password.equals(confirmPassword)) {
            registerFrame.showError("Les mots de passe ne correspondent pas.");
            return;
        }


        String effectiveRole = (role == null || role.isEmpty()) ? "MEMBER" : role;

        boolean success = registerFacade.registerWithId(userId, displayName, password, email, effectiveRole);


        if (success) {
            registerFrame.showSuccess("Inscription réussie ! Redirection...");


            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> registerFrame.showLoginScreen());
            }).start();

        } else {
            registerFrame.showError("Échec de l'inscription. L'ID ou l'Email existe peut-être déjà.");
        }
    }
}