package com.greenpulsespring.greenpulsespring.rest;

import com.greenpulsespring.greenpulsespring.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UtilisateurRestController {
    @Autowired
    UtilisateurService service;
    //RESTFul webservice avec l'uri ci-dessous
    /*
    @PostMapping("/utilisateurs/check_email")
    public String verifierDoublonEmail(@Param("email") String email) {
        //Pour la simplicité on retourne pas un Objet JSON mais plutôt une chaine de charactère
        // Selon la valeur que retourne la méthode isEmailUnique
        return service.isEmailUnique(email) ? "OK" : "Doublon";
    }
*/
    @PostMapping("/utilisateurs/check_email")
    public String verifierDoublonEmail(@Param("email") String email, @Param("id") Integer id) {
        //Pour la simplicité on retourne pas un Objet JSON mais plutôt une chaine de charactère
        // Selon la valeur que retourne la méthode isEmailUnique
        return service.isEmailUnique(email,id) ? "OK" : "Doublon";
    }
}
