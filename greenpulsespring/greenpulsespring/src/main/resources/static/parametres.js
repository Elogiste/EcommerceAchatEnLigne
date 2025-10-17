document.addEventListener("DOMContentLoaded", () => {
    const tailleTexte = localStorage.getItem("tailleTexte");
    const selectTaille = document.getElementById("tailleTexte");
    if (tailleTexte) {
        document.body.style.fontSize = tailleTexte;
        if (selectTaille) selectTaille.value = tailleTexte;
    }

    if (selectTaille) {
        selectTaille.addEventListener("change", () => {
            const valeur = selectTaille.value;
            document.body.style.fontSize = valeur;
            localStorage.setItem("tailleTexte", valeur);
        });
    }

    // Notification email
    const notifCheckbox = document.getElementById("notifEmail");
    const savedNotif = localStorage.getItem("notifEmail");
    if (notifCheckbox && savedNotif !== null) {
        notifCheckbox.checked = savedNotif === "true";
    }

    if (notifCheckbox) {
        notifCheckbox.addEventListener("change", () => {
            localStorage.setItem("notifEmail", notifCheckbox.checked);
        });
    }

    // Fuseau horaire
    const fuseauSelect = document.getElementById("fuseau");
    const savedFuseau = localStorage.getItem("fuseauHoraire");
    if (fuseauSelect && savedFuseau) {
        fuseauSelect.value = savedFuseau;
    }

    if (fuseauSelect) {
        fuseauSelect.addEventListener("change", () => {
            localStorage.setItem("fuseauHoraire", fuseauSelect.value);
        });
    }
});
