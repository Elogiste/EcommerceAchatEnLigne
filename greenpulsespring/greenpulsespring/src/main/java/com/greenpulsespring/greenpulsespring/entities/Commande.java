package com.greenpulsespring.greenpulsespring.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "commande")
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private double prix;

    @Column(nullable = false)
    private String etat;

    @Column(name = "date_livraison", nullable = false)
    private LocalDate dateLivraison;

    // Utilisateur ayant le rôle de "client" ou "acheteur"
    @ManyToOne
    @JoinColumn(name = "acheteur_id", nullable = false)
    private Utilisateur acheteur;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
    private List<Commande_Produit> produits = new ArrayList<>();

    public Commande() {}

    public Commande(double prix, String etat, LocalDate dateLivraison, Utilisateur acheteur) {
        this.prix = prix;
        this.etat = etat;
        this.dateLivraison = dateLivraison;
        this.acheteur = acheteur;
    }

    // === Getters & Setters ===
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public LocalDate getDateLivraison() {
        return dateLivraison;
    }

    public void setDateLivraison(LocalDate dateLivraison) {
        this.dateLivraison = dateLivraison;
    }

    public Utilisateur getAcheteur() {
        return acheteur;
    }

    public void setAcheteur(Utilisateur acheteur) {
        this.acheteur = acheteur;
    }

    public List<Commande_Produit> getProduits() {
        return produits;
    }

    public void setProduits(List<Commande_Produit> produits) {
        this.produits = produits;
    }

    public String afficherTitreDesColonnes() {
        return String.format(" %-10s  %30s %15s %20s %15s", "Id", "Etat", "Prix", "Date Livraison", "Acheteur Id") +
                "\n --------------------------------------------------------------------------------------------------------------------------------------";
    }

    @Override
    public String toString() {
        return String.format(" %-10d  %30s %15.2f %20s %15d",
                this.id, this.etat, this.prix, this.dateLivraison,
                (this.acheteur != null ? this.acheteur.getId() : null));
    }
}
