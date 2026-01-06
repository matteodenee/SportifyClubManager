package licenceManagement.UI.controllers;

import TypeSportManagement.TypeSport;
import UserManagent.User;
import licenceManagement.Bl.Licence;
import licenceManagement.Bl.facade.LicenceFacade;
import licenceManagement.Enum.TypeLicence;
import licenceManagement.UI.frames.LicenceFrame;

public class LicenceController {

    private LicenceFrame licenceFrame;
    private User currentUser;

    // Injection de la vue
    public void setLicenceFrame(LicenceFrame licenceFrame) {
        this.licenceFrame = licenceFrame;
    }

    // Injection de l'utilisateur connecté
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    // Action appelée par la vue
    public void onDemandeLicence(TypeSport sport, TypeLicence type) {

        if (sport == null || type == null) {
            licenceFrame.showError("Veuillez remplir tous les champs.");
            return;
        }

        if (currentUser == null) {
            licenceFrame.showError("Utilisateur non connecté.");
            return;
        }

        Licence licence = new Licence(
            sport,
            type,
            null,
            null,
            currentUser,
            null
        );

        LicenceFacade facade = LicenceFacade.createLicenceFacade();
        facade.demanderLicence(licence);

        licenceFrame.showSuccess();
    }
}
