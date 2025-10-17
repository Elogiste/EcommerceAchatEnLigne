package com.greenpulsespring.greenpulsespring.controller;


import com.greenpulsespring.greenpulsespring.entities.Commande;
import com.greenpulsespring.greenpulsespring.entities.Produit;
import com.greenpulsespring.greenpulsespring.entities.Role;
import com.greenpulsespring.greenpulsespring.entities.Utilisateur;
import com.greenpulsespring.greenpulsespring.service.CommandeService;
import com.greenpulsespring.greenpulsespring.service.ProduitService;
import com.greenpulsespring.greenpulsespring.service.UtilisateurNotFoundException;
import com.greenpulsespring.greenpulsespring.service.UtilisateurService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Controller
public class UtilisateurController {


    @Autowired
    private UtilisateurService service;

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private ProduitService produitService;


    @GetMapping("/utilisateurs")
    public String afficherUtilisateur(Model model) {

        List<Utilisateur> listUtilisateurs = service.afficherUtilisateurs();
        model.addAttribute("listUtilisateurs", listUtilisateurs);
        return "utilisateurs";
    }

    @GetMapping("/utilisateurs/new")
    public String afficherFormulaireUtilisateur(Model model) {
        List<Role> listRoles = service.afficherRoles();
        model.addAttribute("listRoles", listRoles);
        model.addAttribute("utilisateur", new Utilisateur());
        return "inscription";
    }

    @PostMapping("/utilisateurs/save")
    public String ajouterUtilisateur(@Valid Utilisateur utilisateur, BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes,
                                     @RequestParam("fileImage") MultipartFile multipartFile,
                                     Model model) throws IOException {

        // Par défaut, rendre l'utilisateur actif
        utilisateur.setActive(true);

        if (multipartFile.isEmpty()) {
            bindingResult.rejectValue("photo", "error.utilisateur", "Veuillez télécharger une photo.");
        }

        String chemin = multipartFile.getOriginalFilename();
        String fileName = StringUtils.cleanPath(chemin);
        utilisateur.setPhoto(fileName);

        File directory = new File("src/main/resources/static/images/utilisateurs");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File serverFile = new File(directory.getAbsolutePath() + File.separator + fileName);

        if (!multipartFile.isEmpty() && serverFile.exists()) {
            bindingResult.rejectValue("photo", "error.utilisateur", "Une image avec ce nom existe déjà. Veuillez renommer le fichier.");
        }

        if (bindingResult.hasErrors()) {
            List<Role> listRoles = service.afficherRoles();
            model.addAttribute("listRoles", listRoles);
            return "inscription";
        }

        multipartFile.transferTo(serverFile);
        service.enregistrer(utilisateur);

        redirectAttributes.addFlashAttribute("message", "L'utilisateur a été ajouté avec succès.");
        return "redirect:/";
    }

    @GetMapping("/images/utilisateurs/{fileId}")
    public void telechargerFichier(@PathVariable String fileId, HttpServletResponse response) throws IOException, UtilisateurNotFoundException {
        File directory = new File("src/main/resources/static/images/utilisateurs");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory.getAbsolutePath() + File.separator + fileId);
        List<Utilisateur> listeUtilisateur = service.chercherPhoto(fileId);

        for (Utilisateur utilisateur : listeUtilisateur) {
            if (utilisateur.getPhoto() != null && file.exists()) {
                response.setContentType("image/jpeg");
                response.setHeader("Content-Disposition", "inline; filename=\"" + fileId + "\"");

                FileInputStream fileInputStream = new FileInputStream(file);
                OutputStream outputStream = response.getOutputStream();

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                fileInputStream.close();
                outputStream.flush();
                outputStream.close();
            }
        }
    }

    @GetMapping("/utilisateurs/delete/{id}")
    public String supprimerUtilisateur(@PathVariable("id") Integer id,
                                       RedirectAttributes redirectAttributes) {
        try {
            service.supprimerUtilisateur(id);
            redirectAttributes.addFlashAttribute("message",
                    "L'utilisateur ID " + id + " a été supprimé avec succès ");
        } catch (UtilisateurNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/utilisateurs";
    }

    @GetMapping("/utilisateurs/edit/{id}")
    public String mettreAjourUtilisateur(@PathVariable("id") Integer id,
                                         RedirectAttributes redirectAttributes,
                                         Model model) {
        try {
            Utilisateur utilisateur = service.trouverUtilisateur(id);
            List<Role> listRoles = service.afficherRoles();

            model.addAttribute("listRoles", listRoles);
            model.addAttribute("utilisateur", utilisateur);
            model.addAttribute("pageTitle", "Éditeur Utilisateur (ID : " + id + ")");
            return "inscription";
        } catch (UtilisateurNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", "L'utilisateur avec ID " + id + " est introuvable ");
            return "redirect:/utilisateurs";
        }
    }

    @GetMapping("/utilisateurs/{id}/active/{status}")
    public String mettreAjourStatusActiveUtilisateur(@PathVariable("id") Integer id,
                                                     @PathVariable("status") boolean active,
                                                     RedirectAttributes redirectAttributes) {
        service.updateActiveStatus(id, active);
        String status = active ? "activé" : "désactivé";
        String message = "L'utilisateur ID " + id + " a été " + status;
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/utilisateurs";
    }

    @GetMapping("/rechercher/utilisateurs")
    public String rechercherUtilisateur(Model model, @RequestParam("keyword") String keyword) {
        List<Utilisateur> listUtilisateurs;

        if (keyword != null && !keyword.isEmpty()) {
            // Recherche les utilisateurs qui correspondent au mot-clé (par nom, email ou rôle)
            listUtilisateurs = service.rechercherParKeyword(keyword);
        } else {
            // Si le mot-clé est vide, on affiche tous les utilisateurs
            listUtilisateurs = service.trouverTousLesUtilisateurs();
        }

        // Ajouter la liste des utilisateurs et le mot-clé au modèle
        model.addAttribute("listUtilisateurs", listUtilisateurs);
        model.addAttribute("keyword", keyword);
        return "utilisateurs";
    }



    @GetMapping("/connexion")
    public String afficherFormulaireConnexion(Model model) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setActive(true);
        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("pageTitle", "Connexion");
        return "connexion";
    }

    @PostMapping("/connexion/verification")
    public String verifierConnexion(Model model,
                                    RedirectAttributes redirectAttributes,
                                    @Param("email") String email,
                                    @Param("password") String password, @RequestParam("g-recaptcha-response") String captchaResponse,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {



//Vérification Captcha
        boolean captchaValide = verifyCaptcha(captchaResponse);
        if (!captchaValide) {
            model.addAttribute("erreur", "Captcha invalide. Veuillez réessayer.");
            return "connexion";
        }

        // Rechercher l'utilisateur avec l'email et le mot de passe
        Utilisateur utilisateur = service.rechercherUtilisateurParEmailMotDePasse(email, password);

        if (utilisateur != null) {
            // Créer ou récupérer la session
            HttpSession session = request.getSession(true);
            session.setAttribute("utilisateurConnecte", utilisateur); // Stocker l'utilisateur dans la session
            session.setAttribute("nom", utilisateur.getNom());
            session.setAttribute("prenom", utilisateur.getPrenom());
            session.setAttribute("role", utilisateur.getRole().getNom());

            // Restaurer le panier depuis la base s’il existe
            Commande panier = commandeService.findCommandePanierParUtilisateur(utilisateur);
            if (panier != null) {
                session.setAttribute("panierCommande", panier);  // Stocker le panier dans la session
            }

            // Créer des cookies pour l'email et le mot de passe
            Cookie emailCookie = new Cookie("email", email);
            Cookie mdpCookie = new Cookie("password", password);
            emailCookie.setMaxAge(60 * 60 * 24 * 60); // 60 jours
            mdpCookie.setMaxAge(60 * 60 * 24 * 60);

            response.addCookie(emailCookie);
            response.addCookie(mdpCookie);

            // 🔁 Redirection unique vers l'accueil
            return "redirect:/";
        } else {
            // Utilisateur non trouvé
            redirectAttributes.addFlashAttribute("message", "L'email ou le mot de passe est invalide.");
            return "redirect:/connexion";
        }
    }



    // 👉 Ajoutez cette méthode pour rendre utilisateurConnecte disponible dans toutes les vues
    @ModelAttribute("utilisateurConnecte")
    public Utilisateur utilisateurConnecte(HttpSession session) {
        return (Utilisateur) session.getAttribute("utilisateurConnecte");
    }

    @GetMapping("/deconnexion")
    public String deconnexion(Model model,
                              RedirectAttributes redirectAttributes,
                              HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            String nom = (String) session.getAttribute("nom");
            String prenom = (String) session.getAttribute("prenom");
            session.invalidate();
            if (nom != null) {
                redirectAttributes.addFlashAttribute("deconnexion", "Déconnexion réussie pour " + nom + " " + prenom);
            }
        }
        return "redirect:/";
    }
    @GetMapping("/profil")
    public String afficherProfil(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateurConnecte");

        if (utilisateur == null) {
            redirectAttributes.addFlashAttribute("message", "Veuillez vous connecter pour accéder à votre profil.");
            return "redirect:/connexion";
        }

        model.addAttribute("utilisateur", utilisateur);

        // Acheteur : afficher ses commandes
        if ("Acheteur".equals(utilisateur.getRole().getNom())) {
            List<Commande> commandes = commandeService.trouverCommandesParAcheteur(utilisateur);
            model.addAttribute("commandes", commandes);
        }

        // Producteur : commandes contenant ses produits
        if ("Producteur".equals(utilisateur.getRole().getNom())) {
            List<Commande> commandesReçues = commandeService.getCommandesParProducteur((long) utilisateur.getId());
            model.addAttribute("commandesReçues", commandesReçues);
        }

        // ✅ Administrateur : toutes les commandes
        if ("Administrateur".equals(utilisateur.getRole().getNom())) {
            List<Commande> toutesCommandes = commandeService.getToutesCommandes();
            model.addAttribute("toutesCommandes", toutesCommandes);
        }

        return "profil";
    }

    // vérifier si le captcha est validé
    public boolean verifyCaptcha(String response) {
        try {
            // Clé secrète fournie par Google reCAPTCHA
            String secretKey = "6LeRijgrAAAAAMgtXkGEh95LWIc90zJfLLidZ6DO";
            // URL de l'API Google pour la vérification du reCAPTCHA
            String url = "https://www.google.com/recaptcha/api/siteverify";

// Création d'une instance de RestTemplate pour effectuer des requêtes HTTP
            RestTemplate restTemplate = new RestTemplate();
            // Préparation des paramètres à envoyer dans la requête POST
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("secret", secretKey); //clé secrète
            params.add("response", response); //La réponse du reCAPTCHA fournie par l'utilisateur
            // Configuration des en-têtes HTTP (Content-Type requis par l'API)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // Création de la requête HTTP avec les paramètres et les en-têtes
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            // Envoi de la requête POST à l'API reCAPTCHA et récupération de la réponse
            ResponseEntity<Map> responseEntity = restTemplate.postForEntity(url, request, Map.class);
            // Extraction du corps de la réponse (qui est une Map contenant les données JSON de Google)
            Map<String, Object> body = responseEntity.getBody();

            // Retourne true si le champ "success" est true dans la réponse JSON
            return (Boolean) body.get("success");
        } catch (Exception e) {
            // En cas d'erreur (réseau, parsing, etc.), retourne false
            return false;
        }
    }



    @GetMapping("/dashboard")
    public String afficherDashboard(HttpSession session, Model model) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateurConnecte");

        if (utilisateur == null) {
            return "redirect:/connexion";
        }

        model.addAttribute("utilisateurConnecte", utilisateur);

        String role = utilisateur.getRole().getNom();

        if ("Acheteur".equals(role)) {
            // Infos classiques
            int quantitePanier = commandeService.getQuantitePanier(utilisateur);
            int nombreCommandes = commandeService.getNombreCommandesParAcheteur(utilisateur);
            List<Commande> mesCommandes = commandeService.trouverCommandesParAcheteur(utilisateur);

            model.addAttribute("quantitePanier", quantitePanier);
            model.addAttribute("nombreCommandes", nombreCommandes);
            model.addAttribute("mesCommandes", mesCommandes);

            // Données pour graphique (achats par mois)
            Map<String, Integer> achatsParMois = commandeService.getAchatsParMois(utilisateur);
            model.addAttribute("achatsParMois", achatsParMois);

            // Indicateurs Clés
            double totalVentesAcheteur = commandeService.getTotalVentesAcheteur(utilisateur);
            model.addAttribute("totalVentes", totalVentesAcheteur);
        }

        if ("Producteur".equals(role)) {
            // Infos classiques
            int nombreProduits = produitService.getNombreProduitsParProducteur(utilisateur);
            List<Produit> mesProduits = produitService.getProduitsParProducteur(utilisateur);

            model.addAttribute("nombreProduits", nombreProduits);
            model.addAttribute("mesProduits", mesProduits);

            // Données pour graphique (ventes par produit)
            Map<String, Integer> ventesParProduit = commandeService.getVentesParProduit(utilisateur);
            model.addAttribute("ventesParProduit", ventesParProduit);

        }

        if ("Administrateur".equals(role)) {
            // Infos classiques
            int nombreCommandesAcheteurs = commandeService.getNombreTotalCommandesAcheteurs();
            int nombreCommandesProducteurs = commandeService.getNombreTotalCommandesProducteurs();
            List<Commande> toutesCommandes = commandeService.getToutesCommandes();

            model.addAttribute("nombreCommandesAcheteurs", nombreCommandesAcheteurs);
            model.addAttribute("nombreCommandesProducteurs", nombreCommandesProducteurs);
            model.addAttribute("toutesCommandes", toutesCommandes);

            // Données pour graphique global (ventes par mois)
            Map<String, Integer> statsGlobales = commandeService.getStatsGlobalesParMois();
            model.addAttribute("statsGlobales", statsGlobales);

        }

        return "dashboard";
    }




}
