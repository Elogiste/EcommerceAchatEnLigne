document.addEventListener("DOMContentLoaded", () => {
    const darkModeBouton = document.getElementById("darkModeBouton");
    if (!darkModeBouton) return;

    const activerDarkMode = () => {
        document.documentElement.classList.add("dark-mode");
        localStorage.setItem("darkModeActif", "true");
    };

    const desactiverDarkMode = () => {
        document.documentElement.classList.remove("dark-mode");
        localStorage.setItem("darkModeActif", "false");
    };

    const darkModeActif = localStorage.getItem("darkModeActif") === "true";
    darkModeBouton.checked = darkModeActif;

    if (darkModeActif) activerDarkMode();
    else desactiverDarkMode();

    darkModeBouton.addEventListener("change", () => {
        if (darkModeBouton.checked) {
            activerDarkMode();
        } else {
            desactiverDarkMode();
        }
    });


});
