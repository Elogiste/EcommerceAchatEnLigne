package com.greenpulsespring.greenpulsespring.controller;



import com.greenpulsespring.greenpulsespring.entities.Messages;
import com.greenpulsespring.greenpulsespring.entities.Utilisateur;
import com.greenpulsespring.greenpulsespring.service.MessageService;

import com.greenpulsespring.greenpulsespring.service.UtilisateurService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class MessageController {
    @Autowired
    MessageService messageService;




    @Autowired
    UtilisateurService utilisateurService;

    @GetMapping("/EnvoyerMessages")
    public String afficherUtilisateursAvecChat(Model model, HttpSession session) {
        List<Messages> listMessages = messageService.afficherMessages();
        model.addAttribute("listMessages", listMessages);
        model.addAttribute("messages", new Messages()); List<Utilisateur> listUtilisateurs = utilisateurService.afficherUtilisateurs();
        model.addAttribute("listUtilisateurs", listUtilisateurs);
        //

        // Calcul des messages non lus par utilisateur
        Map<Integer, Integer> messagesNonLusParUtilisateur = new HashMap<>();
        Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateurConnecte");

        // Parcours des messages pour compter ceux qui sont non lus
        for (Messages msg : listMessages) {
            if (!msg.isLu() && msg.getId_receveur().getId().equals(utilisateurConnecte.getId())) {
                int idEnvoyeur = msg.getId_envoyeur().getId();
                // Incrémentation du nombre de messages non lus par l'envoyeur
                messagesNonLusParUtilisateur.put(idEnvoyeur, messagesNonLusParUtilisateur.getOrDefault(idEnvoyeur, 0) + 1);
            }
        }
        // Ajout de l'information dans le modèle
        model.addAttribute("messagesNonLusParUtilisateur", messagesNonLusParUtilisateur);


        Map<String, List<Messages>> messagesByConversation = new HashMap<>();


        for (Messages message : listMessages) {
            //Créer une clé unique par pairs d'envoyeurs et receveurs
            String conversationKey = getConversationNumero(message.getId_envoyeur().getId(), message.getId_receveur().getId());

            // Grouper messages par  clés de conversation
            messagesByConversation
                    .computeIfAbsent(conversationKey, k -> new ArrayList<>())
                    .add(message);


        }


        model.addAttribute("messagesByConversation", messagesByConversation);
      // String notification = (String) session.getAttribute("notification");
        //String notificationFrom = (String) session.getAttribute("notificationFrom");
       // Integer notificationId = (Integer) session.getAttribute("notificationId");

        //if (notification != null) {
          //  model.addAttribute("notification", notification);
          //  model.addAttribute("notificationFrom", notificationFrom);
          //  model.addAttribute("notificationId", notificationId);

       // }
        //

        return "EnvoyerMessages";
    }

    @GetMapping("/Messages/{Id_receveur}")
    public String pageMessages(@PathVariable("Id_receveur") String Id_receveur, Model model, HttpSession session) {

        Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateurConnecte");

        if (utilisateurConnecte == null) {
            return "redirect:/connexion";
        }

        int idReceveur = Integer.parseInt(Id_receveur);
        model.addAttribute("idUtilisateur", utilisateurConnecte.getId());
        model.addAttribute("idReceveur", idReceveur);

        List<Messages> listMessages = messageService.getMessagesPourUtilisateur(utilisateurConnecte.getId());
        model.addAttribute("listMessages", listMessages);


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


        // Convertir liens
        for (Messages msg : listMessages) {
            msg.setMessage(messageService.convertirLiens(msg.getMessage()));
            msg.setDateFormattee(formatDate(msg.getDateDenvoie()));
            if (msg.getId_envoyeur().getId() == idReceveur && msg.getId_receveur().getId() == utilisateurConnecte.getId()) {
                // lecture en mémoire
                msg.setLu(true);
                //session.removeAttribute("notification");
               // session.removeAttribute("notificationFrom");
             //   session.removeAttribute("notificationId");
            }

        }
        List<Utilisateur> listUtilisateur = utilisateurService.afficherUtilisateurs();
        model.addAttribute("listUtilisateur", listUtilisateur);


        Utilisateur utilisateur = messageService.getUtilisateurById(idReceveur);
        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("messages", new Messages());


        return "Messages";
    }


    @PostMapping("/messages/save")
    public String saveMessage(@Valid Messages messages, @RequestParam("idReceveur")Integer idReceveur, @RequestParam("fichier") MultipartFile fichier, BindingResult bindingResult, HttpSession session, RedirectAttributes redirectAttributes) {

        Utilisateur envoyeurId = ( Utilisateur) session.getAttribute("utilisateurConnecte");

        Utilisateur receveur = messageService.getUtilisateurById(idReceveur);

        if(envoyeurId==null){
            redirectAttributes.addFlashAttribute("errorMessage","session Expirée");
            return "redirect:/connexion";
        }
        if (messages.getId() != 0) {

            messages.setEtat("modifié");
        }else{messages.setEtat("envoyé");}




            if(receveur!=null){
        messages.setId_receveur(receveur);
    }else{
                redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
                return "redirect:/Messages";
            }

        Utilisateur envoyeur = messageService.getUtilisateurById(envoyeurId.getId());
        if (envoyeur != null) {

            messages.setId_envoyeur(envoyeur);

        } else {

            redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
            return "redirect:/Messages/"+ idReceveur;
        }


        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.messages", bindingResult);
            redirectAttributes.addFlashAttribute("messages", messages);
            return "redirect:/Messages/"+ idReceveur;
        }

        // Gestion du fichier
        if (fichier != null && !fichier.isEmpty()) {
            try {
                String chemin = fichier.getOriginalFilename();
                String typeContenu = fichier.getContentType();

                System.out.println("chemin : " + chemin);
                System.out.println("typeContenu : " + typeContenu);

                String fileName = StringUtils.cleanPath(chemin);
                System.out.println("fileName : " + fileName);

                File directory = new File("src/main/resources/static/images");
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                File serverFile = new File(directory.getAbsolutePath() + File.separator + fileName);

                // Si le fichier n'existe pas, on le sauvegarde
                if (!serverFile.exists()) {
                    fichier.transferTo(serverFile);
                }

                // Dans tous les cas, on référence le fichier dans le message
                messages.setNomFichier(fileName);

            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors du téléchargement du fichier.");
                return "redirect:/Messages/" + idReceveur;
            }
        }



// Set la date d'envoie à la date présente
        messages.setDateDenvoie(LocalDateTime.now());

        messageService.enregistrer(messages);





//Passer la notification
        //session.setAttribute("notification", "Vous avez reçu un nouveau message de " + envoyeur.getNom());
        //session.setAttribute("notificationFrom", envoyeur.getPrenom());
      //  session.setAttribute("notificationId", receveur.getId());


        redirectAttributes.addFlashAttribute("message", "Message sent successfully!");
        return "redirect:/Messages/"+ idReceveur;
    }


    //Supprimer Le message
    @GetMapping("/delete/message/{messageId}")
    public String supprimermessage(@PathVariable("messageId") Integer messageId,@RequestParam("idReceveur")Integer idReceveur,
                                             RedirectAttributes redirectAttributes) {
        try {
           messageService.supprimerMessage(messageId);
            redirectAttributes.addFlashAttribute("message", "Message");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Erreur : " + e.getMessage());
        }

        return "redirect:/Messages/"+idReceveur;
    }


    @GetMapping("/admin/delete/message/{messageId}")
    public String suppressionAdminMessage(@PathVariable("messageId") Integer messageId,
                                   RedirectAttributes redirectAttributes) {
        try {
            messageService.supprimerMessage(messageId);
            redirectAttributes.addFlashAttribute("message", "Message");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Erreur : " + e.getMessage());
        }

        return "redirect:/EnvoyerMessages";
    }


    //Modifier Le message
    @GetMapping("/messages/edit/{id}")
    public String editMessage(@PathVariable("id") int id, @RequestParam("idReceveur") int idReceveur, Model model) {
        Optional<Messages> optionalMessage = messageService.findById(id);

        if (optionalMessage.isPresent()) {
            model.addAttribute("messages", optionalMessage.get()); // Ajoute le message au modèle
            model.addAttribute("idReceveur", idReceveur); // Ajoute l'id du receveur
            return "Messages"; // Remplace "editMessage" par le nom de ta vue Thymeleaf
        } else {
            model.addAttribute("errorMessage", "Message non trouvé");
            return "redirect:/connexion";
        }
    }
//Formatter LocalDateTime
    public String formatDate(LocalDateTime dateDenvoie) {
        if (dateDenvoie == null) {
            return "";  // si la date est null
        }

        Duration durree = Duration.between(dateDenvoie, LocalDateTime.now());

        // si le messages est envoyé avant 60 secondes
        if (durree.getSeconds() < 60) {
            return " il y a "+ durree.getSeconds() + " secondes";
        }

        // si le messages est envoyé avant 60 minutes
        if (durree.toMinutes() < 60) {
            return " il y a "+durree.toMinutes() + " minutes";
        }

        // si le messages est envoyé avant 24 heures
        if (durree.toHours() < 24) {
            return " il y a "+ durree.toHours() + " heures ";
        }

        // si le messages est envoyé il ya plus de 24 heures
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateDenvoie.format(formatter);
    }
//numéros de conversations
    private String getConversationNumero(Integer envoyeurId, Integer receveurId) {
        Integer user1 = Math.min( envoyeurId, receveurId);
        Integer user2 = Math.max( envoyeurId, receveurId);

        // Retourne une clé de conversation format "user1-user2"
        return user1 + "-" + user2;
    }

}
