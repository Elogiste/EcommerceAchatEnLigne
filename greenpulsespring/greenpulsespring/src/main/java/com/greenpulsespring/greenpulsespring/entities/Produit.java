package com.greenpulsespring.greenpulsespring.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Entity
@Table(name = "produit")
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Le nom ne peut pas être vide")
    private String nom;

    @NotBlank(message = "Le type ne peut pas être vide")
    private String type;

    @NotNull(message = "La quantité ne peut pas être vide")
    private Double quantite;

    private String description;

    @NotNull(message = "Le prix ne peut pas être vide")
    private Double prix;

    @NotNull(message = "La date d'ajout ne peut pas être vide")
    private LocalDate dateAjout;

    private String photo;

    private String label;

    private String choixPrixUnitaire;
    private Double prix_unitaire;

    private LocalDate date_recolte;

    private String origine_provenance;




    @ManyToOne
    @JoinColumn(name = "producteur_id", nullable = false)
    private Utilisateur producteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id")
    private Commande commande;

    public Produit() {}

    public Produit(int id, String nom, String type, double quantite, String description, double prix, LocalDate dateAjout, String photo, Utilisateur producteur) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.quantite = quantite;
        this.description = description;
        this.prix = prix;
        this.dateAjout = dateAjout;
        this.photo = photo;
        this.producteur = producteur;
    }

    // Getters & Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getQuantite() {
        return quantite;
    }

    public void setQuantite(Double quantite) {
        this.quantite = quantite;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public LocalDate getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(LocalDate dateAjout) {
        this.dateAjout = dateAjout;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Utilisateur getProducteur() {
        return producteur;
    }

    public void setProducteur(Utilisateur producteur) {
        this.producteur = producteur;
    }

    public Commande getCommande() {
        return commande;
    }


    // Affichage

    public String afficherTitreDesColonnes() {
        return String.format(" %-10s  %30s %15s %15s %15s %15s %25s", "Id", "Nom", "Description", "Prix", "Date_Ajout", "Photo", "Id_Producteur") +
                "\n --------------------------------------------------------------------------------------------------------------------------------------";
    }

    @Override
    public String toString() {
        return String.format(" %-10d  %30s %15s %15.2f %15s %15s %25s ",
                this.id, this.nom, this.description, this.prix, this.dateAjout, this.photo,
                (this.producteur != null ? this.producteur.getId() : "N/A"));
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Double getPrix_unitaire() {
        return prix_unitaire;
    }

    public void setPrix_unitaire(Double prix_unitaire) {
        this.prix_unitaire = prix_unitaire;
    }

    public LocalDate getDate_recolte() {
        return date_recolte;
    }

    public void setDate_recolte(LocalDate date_recolte) {
        this.date_recolte = date_recolte;
    }

    public String getOrigine_provenance() {
        return origine_provenance;
    }

    public void setOrigine_provenance(String origine_provenance) {
        this.origine_provenance = origine_provenance;
    }

    public String getChoixPrixUnitaire() {
        return choixPrixUnitaire;
    }

    public void setChoixPrixUnitaire(String choixPrixUnitaire) {
        this.choixPrixUnitaire = choixPrixUnitaire;
    }
}
