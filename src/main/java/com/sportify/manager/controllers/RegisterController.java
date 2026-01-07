package com.sportify.manager.controllers;

import com.sportify.manager.facade.RegisterFacade;
import com.sportify.manager.frame.RegisterFrame;
import javafx.application.Platform;

/**
 * Control layer for the registration flow.
 */
public class RegisterController {

    private final RegisterFacade registerFacade;
    private RegisterFrame registerFrame;

    public RegisterController() {
        // Utilisation de la méthode de création de ta Facade
        this.registerFacade = RegisterFacade.createRegisterFacade();
    }

    public RegisterController(RegisterFacade registerFacade) {
        this.registerFacade = registerFacade;
    }

    public void setRegisterFrame(RegisterFrame registerFrame) {
        this.registerFrame = registerFrame;
    }

    /**
     * Triggered by the RegisterFrame; validates inputs and calls the facade.
     */
    public void onClick() {
        if (registerFrame == null) return;

        // 1. Récupération et nettoyage des données
        String userId = registerFrame.getUserId().trim();
        String displayName = registerFrame.getUserName().trim();
        String email = registerFrame.getEmail().trim();
        String password = registerFrame.getPassword();
        String confirmPassword = registerFrame.getConfirmPassword();
        String role = registerFrame.getRole();

        // 2. Validation des champs obligatoires
        if (userId.isEmpty() || displayName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            registerFrame.showError("Veuillez remplir tous les champs obligatoires.");
            return;
        }

        // 3. Validation de la confirmation du mot de passe
        if (!password.equals(confirmPassword)) {
            registerFrame.showError("Les mots de passe ne correspondent pas.");
            return;
        }

        // 4. Appel à la Facade pour l'insertion en base
        // On s'assure que le rôle n'est pas vide
        String effectiveRole = (role == null || role.isEmpty()) ? "MEMBER" : role;

        boolean success = registerFacade.registerWithId(userId, displayName, password, email, effectiveRole);

        // 5. Gestion du résultat
        if (success) {
            registerFrame.showSuccess("Inscription réussie ! Redirection...");

            // On attend 1.5 seconde pour que l'utilisateur voit le message de succès avant de rediriger
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