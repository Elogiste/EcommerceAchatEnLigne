package com.greenpulsespring.greenpulsespring.config;

import com.greenpulsespring.greenpulsespring.entities.Utilisateur;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("utilisateurConnecte")
    public Utilisateur utilisateurConnecte(HttpSession session) {
        return (Utilisateur) session.getAttribute("utilisateurConnecte");
    }
}
