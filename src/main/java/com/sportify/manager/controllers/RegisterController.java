package com.sportify.manager.controllers;

import com.sportify.manager.facade.RegisterFacade;
import com.sportify.manager.frame.RegisterFrame;

/**
 * Control layer for the registration flow.
 */
public class RegisterController {

    private final RegisterFacade registerFacade;
    private RegisterFrame registerFrame;

    public RegisterController() {
        this(RegisterFacade.createRegisterFacade());
    }

    public RegisterController(RegisterFacade registerFacade) {
        this.registerFacade = registerFacade;
    }

    /**
     * Wires the boundary to the controller.
     * @param registerFrame UI boundary
     */
    public void setRegisterFrame(RegisterFrame registerFrame) {
        this.registerFrame = registerFrame;
    }

    /**
     * Triggered by the boundary; reads inputs and calls the facade.
     */
    public void onClick() {
        if (registerFrame == null) {
            return;
        }

        String userId = registerFrame.getUserId().trim();
        String displayName = registerFrame.getUserName().trim();
        String email = registerFrame.getEmail().trim();
        String password = registerFrame.getPassword();
        String confirmPassword = registerFrame.getConfirmPassword();
        String role = registerFrame.getRole();

        if (userId.isEmpty() || password.isEmpty()) {
            registerFrame.showError("Please fill all required fields.");
            return;
        }

        if (confirmPassword != null && !confirmPassword.isEmpty() && !password.equals(confirmPassword)) {
            registerFrame.showError("Passwords do not match.");
            return;
        }

        String effectiveName = displayName.isEmpty() ? userId : displayName;
        String effectiveRole = (role == null || role.isEmpty()) ? "MEMBER" : role;

        boolean success = registerFacade.registerWithId(userId, effectiveName, password, email, effectiveRole);

        if (success) {
            registerFrame.showSuccess("Registration successful!");
            registerFrame.showLoginScreen();
        } else {
            registerFrame.showError("Registration failed. Check password policy or existing user.");
        }
    }
}
