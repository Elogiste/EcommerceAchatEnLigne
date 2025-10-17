package com.greenpulsespring.greenpulsespring.service;

import com.greenpulsespring.greenpulsespring.entities.Commande;
import com.greenpulsespring.greenpulsespring.entities.Commande_Produit;
import com.greenpulsespring.greenpulsespring.entities.Produit;
import com.greenpulsespring.greenpulsespring.entities.Utilisateur;
import com.greenpulsespring.greenpulsespring.repos.CommandeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.TreeMap;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommandeService {

    @Autowired
    private CommandeRepository commandeRepository;

    public void ajouterProduitPanier(Commande commande, Produit produit) {
        if (commande.getDateLivraison() == null && commande.getEtat() == null) {
            Date dateLivraison = Date.valueOf(LocalDate.now().plusDays(7));
            commande.setDateLivraison(dateLivraison.toLocalDate());
            commande.setEtat("En attente");
        }

        boolean produitExiste = false;

        for (Commande_Produit cp : commande.getProduits()) {
            if (cp.getProduit().getId() == produit.getId()) {
                cp.setQuantite(cp.getQuantite() + 1);
                produitExiste = true;
                break;
            }
        }

        if (!produitExiste) {
            Commande_Produit commandeProduit = new Commande_Produit();
            commandeProduit.setProduit(produit);
            commandeProduit.setCommande(commande);
            commandeProduit.setQuantite(1);
            commande.getProduits().add(commandeProduit);
        }


        double prixTotal = commande.getPrix() + produit.getPrix();
        commande.setPrix(prixTotal);

        commandeRepository.save(commande);

    }

    public void supprimerProduitDuPanier(Commande commande, Integer idProduit) {
        Commande_Produit aSupprimer = null;

        for (Commande_Produit cp : commande.getProduits()) {
            if (cp.getProduit().getId() == idProduit) {
                aSupprimer = cp;
                break;
            }
        }

        if (aSupprimer != null) {
            double prixRetire = aSupprimer.getProduit().getPrix() * aSupprimer.getQuantite();
            commande.getProduits().remove(aSupprimer);
            commande.setPrix(commande.getPrix() - prixRetire);
        }

        commandeRepository.save(commande);
    }

    public void enregistrerCommande(Commande commande) {
        commandeRepository.save(commande);
    }

    public List<Commande> trouverCommandesParAcheteur(Utilisateur acheteur) {
        return commandeRepository.findByAcheteur(acheteur);
    }


    /*public List<Commande> getCommandesParProducteur(Long idProducteur) {
        return commandeRepository.findAll().stream()
            .filter(c -> c.getProduits().stream()
                    .anyMatch(cp -> cp.getProduit().getProducteur().getId().equals(idProducteur)))
            .toList();
    }*/

    public int getQuantitePanier(Utilisateur acheteur) {
        return commandeRepository.findByAcheteur(acheteur).stream()
                .filter(c -> c.getEtat() == null || "En attente".equalsIgnoreCase(c.getEtat()))
                .flatMap(c -> c.getProduits().stream())
                .mapToInt(Commande_Produit::getQuantite)
                .sum();
    }

    public int getNombreCommandesParAcheteur(Utilisateur acheteur) {
        return commandeRepository.findByAcheteur(acheteur).size();
    }

    public int getNombreCommandesParProducteur(Utilisateur producteur) {
        return (int) commandeRepository.findAll().stream()
                .filter(c -> c.getProduits().stream()
                        .anyMatch(cp -> cp.getProduit().getProducteur().getId().equals(producteur.getId())))
                .count();
    }

    public List<Commande> getCommandesParProducteur(Long idProducteur) {
        return commandeRepository.findCommandesByProducteur(idProducteur);
    }

    public int getNombreTotalCommandesAcheteurs() {
        return commandeRepository.findAll().size(); // ou filtrer si tu veux uniquement acheteurs
    }

    public int getNombreTotalCommandesProducteurs() {
        return (int) commandeRepository.findAll().stream()
                .flatMap(c -> c.getProduits().stream())
                .map(cp -> cp.getProduit().getProducteur().getId())
                .distinct()
                .count();
    }

    public List<Commande> getToutesCommandes() {
        return commandeRepository.findAll();
    }


    public void changerEtatCommande(Integer commandeId, String nouvelEtat) {
        Commande commande = commandeRepository.findById(commandeId).orElseThrow(() -> new RuntimeException("Commande non trouvée"));
        commande.setEtat(nouvelEtat);
        commandeRepository.save(commande);
    }

    public Commande findCommandePanierParUtilisateur (Utilisateur utilisateur){
        return commandeRepository.findByAcheteurAndEtat(utilisateur, "Panier").orElse(null);
    }
//Ajout de map pour la gestion du graphique dans le dashboard
    public Map<String, Integer> getAchatsParMois(Utilisateur acheteur) {
        return commandeRepository.findByAcheteur(acheteur).stream()
                .filter(c -> c.getDateLivraison() != null)
                .collect(Collectors.groupingBy(
                        c -> c.getDateLivraison().getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH),
                        TreeMap::new,
                        Collectors.summingInt(c -> (int) c.getPrix())
                ));
    }

    public Map<String, Integer> getVentesParProduit(Utilisateur producteur) {
        List<Commande> commandes = commandeRepository.findAll();  // Récupérer toutes les commandes

        Map<String, Integer> ventes = new HashMap<>();

        // Parcourir toutes les commandes
        for (Commande commande : commandes) {
            for (Commande_Produit cp : commande.getProduits()) {
                // Vérifier si le producteur du produit correspond au producteur spécifié
                if (cp.getProduit().getProducteur().getId().equals(producteur.getId())) {
                    String nomProduit = cp.getProduit().getNom();
                    ventes.put(nomProduit, ventes.getOrDefault(nomProduit, 0) + cp.getQuantite());  // Ajouter les ventes
                }
            }
        }

        return ventes;
    }

    public Map<String, Integer> getStatsGlobalesParMois() {
        return commandeRepository.findAll().stream()
                .filter(c -> c.getDateLivraison() != null)
                .collect(Collectors.groupingBy(
                        c -> c.getDateLivraison().getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH),
                        TreeMap::new,
                        Collectors.summingInt(c -> (int) c.getPrix())
                ));
    }

    public double getTotalVentes() {
        return commandeRepository.findAll().stream()
                .filter(c -> c.getDateLivraison() != null) // Optionnel : filtrer les commandes livrées
                .mapToDouble(Commande::getPrix)  // Calcule la somme des prix des commandes
                .sum();
    }

    public long getCommandesEnAttente() {
        return commandeRepository.findAll().stream()
                .filter(c -> c.getEtat() == null || "En attente".equalsIgnoreCase(c.getEtat()))  // Commandes en attente
                .count();
    }

    public List<String> getProduitsPopulaires(Utilisateur utilisateur) {
        Map<String, Integer> ventesParProduit = getVentesParProduit(utilisateur);  // Utilise la méthode déjà existante pour récupérer les ventes par produit
        return ventesParProduit.entrySet().stream()
                .sorted((entry1, entry2) -> Integer.compare(entry2.getValue(), entry1.getValue()))  // Trie par quantité vendue
                .limit(3)  // Prendre les 3 produits les plus populaires
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public double getTotalVentesAcheteur(Utilisateur utilisateur) {
        return commandeRepository.findAll().stream()
                .filter(c -> c.getAcheteur().equals(utilisateur) && c.getDateLivraison() != null)
                .mapToDouble(Commande::getPrix)
                .sum();
    }

    public long getCommandesEnAttenteProducteur(Utilisateur utilisateur) {
        return commandeRepository.findAll().stream()
                .filter(c -> "En attente".equalsIgnoreCase(c.getEtat()) &&
                        c.getProduits().stream()
                                .anyMatch(cp -> cp.getProduit().getProducteur().getId().equals(utilisateur.getId())))
                .count();
    }

    public List<String> getProduitsPopulairesProducteur(Utilisateur utilisateur) {
        Map<String, Integer> ventesParProduit = getVentesParProduit(utilisateur);
        return ventesParProduit.entrySet().stream()
                .sorted((entry1, entry2) -> Integer.compare(entry2.getValue(), entry1.getValue()))
                .limit(3) // Limiter aux 3 produits les plus populaires
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public double getTotalVentesAdmin() {
        return commandeRepository.findAll().stream()
                .filter(c -> c.getDateLivraison() != null)
                .mapToDouble(Commande::getPrix)
                .sum();
    }

    public long getCommandesEnAttenteAdmin() {
        return commandeRepository.findAll().stream()
                .filter(c -> c.getEtat() == null || "En attente".equalsIgnoreCase(c.getEtat()))
                .count();
    }

    public Map<String, Integer> getVentesParProduitAdmin() {
        List<Commande> commandes = commandeRepository.findAll();
        Map<String, Integer> ventes = new HashMap<>();

        for (Commande commande : commandes) {
            for (Commande_Produit cp : commande.getProduits()) {
                String nomProduit = cp.getProduit().getNom();
                ventes.put(nomProduit, ventes.getOrDefault(nomProduit, 0) + cp.getQuantite());
            }
        }

        return ventes;
    }

    public double getTotalVentesProducteur(Utilisateur producteur) {
        return commandeRepository.findAll().stream()
                .flatMap(c -> c.getProduits().stream())
                .filter(cp -> cp.getProduit().getProducteur().getId().equals(producteur.getId()))
                .mapToDouble(cp -> cp.getQuantite() * cp.getProduit().getPrix())
                .sum();
    }

    public List<String> getProduitsPopulairesAdmin() {
        Map<String, Integer> ventesParProduit = new HashMap<>();

        // Parcours de toutes les commandes pour collecter les ventes
        for (Commande commande : commandeRepository.findAll()) {
            for (Commande_Produit cp : commande.getProduits()) {
                String nomProduit = cp.getProduit().getNom();
                ventesParProduit.put(nomProduit,
                        ventesParProduit.getOrDefault(nomProduit, 0) + cp.getQuantite());
            }
        }

        // Tri décroissant et récupération des 3 plus populaires
        return ventesParProduit.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

}
