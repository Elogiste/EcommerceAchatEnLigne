package com.greenpulsespring.greenpulsespring.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name="message")
public class Messages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(length =2000, nullable = false)
    private String message;
    @Column(length = 2000,name = "nom_fichier")
    private String nomFichier;
    @Column(length = 40, nullable = false)
    private String etat;
    @ManyToOne
    @JoinColumn(name = "id_envoyeur", nullable = false)
    private Utilisateur id_envoyeur;
    @ManyToOne
    @JoinColumn(name = "id_receveur", nullable = false)
    private Utilisateur id_receveur;



    @Column(name = "lu")
    private Boolean lu = false;
    public Boolean isLu() {
        return lu != null ? lu : false;
    }
    public void setLu(Boolean lu) {
        this.lu = lu;
    }

    @Transient // pour ne pas l'enregistrer en base
    private String dateFormattee;

    public String getDateFormattee() {
        return dateFormattee;
    }

    public void setDateFormattee(String dateFormattee) {
        this.dateFormattee = dateFormattee;
    }
    @Column(name = "date_envoie")
    private LocalDateTime dateDenvoie;

    public LocalDateTime getDateDenvoie() {
        return dateDenvoie;
    }

    public void setDateDenvoie(LocalDateTime dateDenvoie) {
        this.dateDenvoie = dateDenvoie;

    }
    public Messages() {
    }

    public Messages(String message, String etat, Utilisateur id_envoyeur, Utilisateur id_receveur) {

        this.message = message;
        this.etat=etat;
        this.id_envoyeur = id_envoyeur;
        this.id_receveur = id_receveur;
    }

    public Messages(int id, String message, String etat, Utilisateur id_envoyeur, Utilisateur id_receveur) {
        this.id = id;
        this.message = message;
        this.etat=etat;
        this.id_envoyeur = id_envoyeur;
        this.id_receveur = id_receveur;
    }


    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }
    public void setId(int id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setId_envoyeur(Utilisateur id_envoyeur) {
        this.id_envoyeur = id_envoyeur;
    }

    public void setId_receveur(Utilisateur id_receveur) {
        this.id_receveur = id_receveur;
    }
    public String getNomFichier() {
        return nomFichier;
    }
    public int getId() {
        return id;
    }

    public String getEtat() {
        return etat;
    }

    public String getMessage() {
        return this.message;
    }

    public Utilisateur getId_envoyeur() {
        return id_envoyeur;
    }

    public Utilisateur getId_receveur() {
        return id_receveur;
    }
}
