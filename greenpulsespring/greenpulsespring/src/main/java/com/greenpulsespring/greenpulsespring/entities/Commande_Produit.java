package com.greenpulsespring.greenpulsespring.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "commande_produit")  // Ajout du nom de la table pour clarifier la structure
public class Commande_Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "commande_id", nullable = false)  // Clarification de la contrainte
    private Commande commande;

    @ManyToOne
    @JoinColumn(name = "produit_id", nullable = false)  // Clarification de la contrainte
    private Produit produit;

    @Column(nullable = false)  // Ajout de la contrainte pour la quantité
    private int quantite;

    // === GETTERS & SETTERS ===
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Commande getCommande() {
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    @Override
    public String toString() {
        return String.format("Commande ID: %d, Produit ID: %d, Quantité: %d",
                commande != null ? commande.getId() : null,
                produit != null ? produit.getId() : null, quantite);
    }
}
