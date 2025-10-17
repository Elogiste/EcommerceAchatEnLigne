package com.greenpulsespring.greenpulsespring.service;

import com.greenpulsespring.greenpulsespring.entities.Produit;
import com.greenpulsespring.greenpulsespring.entities.Utilisateur;
import com.greenpulsespring.greenpulsespring.repos.CommandeProduitRepository;
import com.greenpulsespring.greenpulsespring.repos.ProduitRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProduitService {

    @Autowired
    private ProduitRepository produitRepository;

    public List<Produit> afficherLesProduits() {return (List<Produit>)  produitRepository.findAll();}

    public void ajouterProduit(Produit produit, HttpSession session) {
        // Vérifie si l'utilisateur est connecté
        Utilisateur producteur = (Utilisateur) session.getAttribute("utilisateurConnecte");

        if (producteur == null) {
            throw new IllegalStateException("Aucun utilisateur connecté. Impossible d'ajouter un produit.");
        }

        // Vérifie si le produit n'est pas null avant de tenter de l'ajouter
        if (produit == null) {
            throw new IllegalArgumentException("Le produit à ajouter est invalide.");
        }

        // Lier le produit à l'utilisateur connecté
        produit.setProducteur(producteur);

        // Sauvegarder le produit dans la base de données
        produitRepository.save(produit);
    }


    public Optional<Produit> findById(int idProduit) {
        return produitRepository.findById(idProduit);
    }

    public List<Produit> chercherPhoto(String fileId) {
        return produitRepository.findByPhoto(fileId);
    }

    public List<Produit> rechercherProduitParNom(String nom) {
        if (nom != null && !nom.trim().isEmpty()) {
            return produitRepository.findByNomContainingIgnoreCase(nom);
        }
        return produitRepository.findAll(); // Affiche tout si aucun nom n'est fourni
    }

    public List<Produit> getProduitsParProprietaire(Long userId) {
        return produitRepository.findByProducteurId(userId);
    }

    public List<Produit> getTousLesProduits() {
        return produitRepository.findAll();
    }

    public int getNombreProduitsParProducteur(Utilisateur producteur) {
        return produitRepository.findByProducteurId((long) producteur.getId()).size();
    }

    public List<Produit> getProduitsParProducteur(Utilisateur producteur) {
        return produitRepository.findByProducteurId((long) producteur.getId());
    }

    @Autowired
    private CommandeProduitRepository commandeProduitRepository;

    public void supprimerProduitAvecRelations(int id, Utilisateur utilisateur) {
        Optional<Produit> produitOpt = produitRepository.findById(id);
        if (produitOpt.isPresent()) {
            Produit produit = produitOpt.get();

            // Si l'utilisateur est le producteur ou un administrateur
            if (produit.getProducteur().getId().equals(utilisateur.getId())
                    || utilisateur.getRole().getNom().equals("Administrateur")) {

                // Supprimer les relations avant de supprimer le produit
                commandeProduitRepository.deleteByProduit(produit);
                produitRepository.delete(produit);
            }
        }
    }

}
