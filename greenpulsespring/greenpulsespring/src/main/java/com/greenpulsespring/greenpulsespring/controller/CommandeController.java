package com.greenpulsespring.greenpulsespring.controller;

import com.greenpulsespring.greenpulsespring.entities.Commande;
import com.greenpulsespring.greenpulsespring.entities.Commande_Produit;
import com.greenpulsespring.greenpulsespring.entities.Produit;
import com.greenpulsespring.greenpulsespring.entities.Utilisateur;
import com.greenpulsespring.greenpulsespring.service.CommandeService;
import com.greenpulsespring.greenpulsespring.service.ProduitService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class CommandeController {
    @Autowired
    ProduitService produitService;
    @Autowired
    private CommandeService commandeService;

    @GetMapping("/ajouterAuPanier/Produit")
    public String afficherCommande(HttpSession session, Model model) {
        Commande commande = (Commande) session.getAttribute("panierCommande");

        if (commande == null) {
            commande = new Commande();
            commande.setPrix(0);
            session.setAttribute("panierCommande", commande);
        }

        List<Commande_Produit> produitsPanier = commande.getProduits();
        model.addAttribute("commandeProduits", produitsPanier);
        return "commande";
    }

    @PostMapping("/ajouterAuPanier/Produit")
    public String ajouterProduit(@RequestParam int idProduit, Model model, HttpSession session) {
        String message = "Erreur lors de l'ajout de produit";
        model.addAttribute("message", message);

        Produit produit = null;
        Optional<Produit> produitOptional = produitService.findById(idProduit);

        if (produitOptional.isPresent()) {
            produit = produitOptional.get();
        }
        Commande commande = (Commande) session.getAttribute("panierCommande");

        if (commande == null) {
            commande = new Commande();
            commande.setPrix(0);
            session.setAttribute("panierCommande", commande);
        }

        //RÉCUPÈRE l'utilisateur connecté
        Utilisateur acheteur = (Utilisateur) session.getAttribute("utilisateurConnecte");
        if (acheteur == null) {
            return "redirect:/connexion"; // ou message d’erreur
        }

        // Associe l’acheteur à la commande si ce n’est pas déjà fait
        if (commande.getAcheteur() == null) {
            commande.setAcheteur(acheteur);
        }

        commandeService.ajouterProduitPanier(commande, produit);
        session.setAttribute("panierCommande", commande);


        List<Commande_Produit> produitsPanier = commande.getProduits();
        model.addAttribute("produit", produit);
        model.addAttribute("commandeProduits", produitsPanier);

        return "commande";
    }

    @PostMapping("/produit/delete/{id}")
    public String supprimerProduitDuPanier(@PathVariable(name = "id") Integer id,
                                           HttpSession session,
                                           RedirectAttributes redirectAttributes) {
        Commande commande = (Commande) session.getAttribute("panierCommande");

        if (commande != null) {
            commandeService.supprimerProduitDuPanier(commande, id);
            redirectAttributes.addFlashAttribute("message",
                    "Le produit ID " + id + " a été supprimé du panier avec succès.");
        } else {
            redirectAttributes.addFlashAttribute("message",
                    "Le panier est vide ou inexistant.");
        }

        return "redirect:/ajouterAuPanier/Produit"; // redirige vers le panier
    }

    //valider le panier
    @PostMapping("/commande/valider")
    public String validerCommande(HttpSession session, RedirectAttributes redirectAttributes) {
        // Récupération de l'utilisateur connecté
        Utilisateur acheteur = (Utilisateur) session.getAttribute("utilisateurConnecte");
        if (acheteur == null) {
            redirectAttributes.addFlashAttribute("message", "Veuillez vous connecter pour valider la commande.");
            return "redirect:/connexion";
        }

        // Récupération de la commande (panier) depuis la session
        Commande commande = (Commande) session.getAttribute("panierCommande");

        // Si elle n'est pas en session, tenter de la récupérer en base
        if (commande == null) {
            commande = commandeService.findCommandePanierParUtilisateur(acheteur);
        }
        if (commande != null) {
            session.setAttribute("panierCommande", commande);
            System.out.println("Panier restauré : " + commande);  // Ajoute un log pour vérifier
        } else {
            System.out.println("Aucun panier trouvé pour cet utilisateur.");
        }

        // Vérification de l'existence de la commande
        if (commande == null || commande.getProduits() == null || commande.getProduits().isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Votre panier est vide.");
            return "redirect:/ajouterAuPanier/Produit";
        }

        // Modifier l'état de la commande
        commande.setEtat("En attente");

        // Enregistrer la commande mise à jour
        commandeService.enregistrerCommande(commande);

        // Supprimer le panier de la session
        session.removeAttribute("panierCommande");

        redirectAttributes.addFlashAttribute("message", "Commande validée avec succès !");
        return "redirect:/";
    }


    /*@GetMapping("/producteur/commandes")
    public String afficherCommandesReçues(HttpSession session, Model model) {
        Utilisateur producteur = (Utilisateur) session.getAttribute("utilisateurConnecte");

        if (producteur == null || !"Producteur".equals(producteur.getRole().getNom())) {
            return "redirect:/connexion"; // redirige si l'utilisateur n'est pas connecté ou pas producteur
        }

        List<Commande> commandesReçues = commandeService.getCommandesParProducteur((long) producteur.getId());

        model.addAttribute("utilisateur", producteur);
        model.addAttribute("commandesReçues", commandesReçues);

        return "historique_commandes";
    }*/

    @GetMapping("/producteur/commande/{id}/etat")
    public String changerEtatCommande(@PathVariable Integer id, @RequestParam String nouvelEtat, RedirectAttributes redirectAttributes) {
        try {
            commandeService.changerEtatCommande(id, nouvelEtat);
            redirectAttributes.addFlashAttribute("message", "L'état de la commande a été mis à jour.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Erreur lors de la mise à jour de l'état de la commande.");
        }
        return "redirect:/profil"; // Redirige vers la page de profil du producteur
    }

    /**/

    @PostMapping("/Commande/TraitementCommande")
    public String afficherTraitementCommande(Model model, RedirectAttributes redirectAttributes) {
        return "TraiterCommande";
    }

//    @GetMapping("/quantite/ajout")
//    public String ajouterQuantite(@RequestParam Integer produitId, @ModelAttribute Commande commande) {
//        for (Commande_Produit cp : commande.getProduits()) {
//            if (cp.getProduit().getId().equals(produitId)) {
//                cp.setQuantite(cp.getQuantite() + 1);
//                break;
//            }
//        }
//        return "redirect:/commande";
//    }
//
//
//    @GetMapping("/quantite/reduction")
//    public String ReduireQuantite(@RequestParam Integer produitId, @ModelAttribute Commande commande) {
//        for (Commande_Produit cp : commande.getProduits()) {
//            if (cp.getProduit().getId().equals(produitId)) {
//                cp.setQuantite(cp.getQuantite() - 1);
//                break;
//            }
//        }
//        return "redirect:/commande";
//    }

    @GetMapping("/admin/historique-commandes")
    public String afficherToutesLesCommandesAdmin(HttpSession session, Model model) {
        Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateurConnecte");

        if (utilisateurConnecte == null || !"Administrateur".equals(utilisateurConnecte.getRole().getNom())) {
            return "redirect:/connexion";
        }

        List<Commande> toutesCommandes = commandeService.getToutesCommandes();
        model.addAttribute("utilisateur", utilisateurConnecte); // Pour afficher les infos de l'admin
        model.addAttribute("toutesCommandes", toutesCommandes);

        return "profil"; // Ou une autre vue dédiée si tu préfères
    }

    @GetMapping("/admin/commande/{id}/etat")
    public String changerEtatCommandeParAdmin(@PathVariable Integer id,
                                              @RequestParam String nouvelEtat,
                                              RedirectAttributes redirectAttributes) {
        try {
            commandeService.changerEtatCommande(id, nouvelEtat);
            redirectAttributes.addFlashAttribute("message", "État de la commande mis à jour par l'administrateur.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Erreur lors de la mise à jour de l'état.");
        }
        return "redirect:/admin/historique-commandes";
    }

}


