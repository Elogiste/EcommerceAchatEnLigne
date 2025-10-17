package com.greenpulsespring.greenpulsespring.service;

import com.greenpulsespring.greenpulsespring.entities.Messages;
import com.greenpulsespring.greenpulsespring.entities.Utilisateur;
import com.greenpulsespring.greenpulsespring.repos.MessageRepository;
import com.greenpulsespring.greenpulsespring.repos.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    UtilisateurRepository utilisateurRepository;
    @Autowired
    UtilisateurService utilisateurService;
    public List<Messages> afficherMessages(){
        return ( List<Messages> ) messageRepository.findAll();
    }
    public Messages enregistrer(Messages messages){
        return  messageRepository.save(messages);
    }
//ancienne méthode
   /* public List<Messages> getMessagesPourUtilisateur(String userId) {

        Utilisateur utilisateur = utilisateurService.getUtilisateurById(userId);
        if (utilisateur == null) {
            return new ArrayList<>();
        }



        return messageRepository.findByEnvoyeurOrReceveur(utilisateur, utilisateur);
    }*/
   public List<Messages> getMessagesPourUtilisateur(int userId) {
        Utilisateur utilisateur = getUtilisateurById(userId);
        if (utilisateur == null) {
            return new ArrayList<>();
        }
        return messageRepository.findByEnvoyeurOrReceveur(utilisateur, utilisateur);
    }

    public List<Messages> rechercherMessages(String keyword){

        if (keyword != null) {
            return messageRepository.findAll(keyword);
        }

        return  null;
    }
    public Optional<Messages> findById(Integer id) {
        return messageRepository.findById(id);
    }

    public void supprimerMessage(Integer messageId) {
      Messages messages = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message introuvable"));

           messageRepository.delete(messages);  // Supprime la commande de la base de données

    }
    public String convertirLiens(String message) {
        if (message.contains("<a href=")) {
            return message;
        }
        return message.replaceAll(
                "(https?://\\S+)",
                "<a href=\"$1\" target=\"_blank\">$1</a>"
        );
    }
    //
    public Utilisateur getUtilisateurById(String userId) {

        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is missing or invalid.");
        }

        try {
            int parsedUserId = Integer.parseInt(userId);
            return utilisateurRepository.findById(parsedUserId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } catch (NumberFormatException e) {

            throw new IllegalArgumentException("Invalid user ID format.", e);
        }
    }
    public Utilisateur getUtilisateurById(int userId) {
        return utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Suppose que tu as une méthode pour marquer un message comme "lu"
    public void marquerMessageCommeLu(int messageId) {
        Messages message = messageRepository.findById(messageId).orElse(null);
        if (message != null && message.isLu() == false) {
            message.setLu(true);
            messageRepository.save(message); // Assure-toi de sauvegarder les modifications dans la base de données
        }
    }

}
