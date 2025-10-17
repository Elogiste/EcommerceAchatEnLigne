package com.greenpulsespring.greenpulsespring.entities;

import jakarta.persistence.*;

import jakarta.validation.constraints.*;

@Entity
@Table(name = "utilisateurs")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "L'adresse e-mail ne peut pas être vide")
    @Email(message = "L'adresse e-mail n'est pas valide")
    @Column(length = 128, nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Le mot de passe ne peut pas être vide")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
            message = "Le mot de passe doit contenir au moins une lettre majuscule, une lettre minuscule, un chiffre et un caractère spécial")
    @Column(length = 64, nullable = false)
    private String password;

    @NotBlank(message = "Le nom ne peut pas être vide")
    @Column(length = 64, nullable = false)
    private String nom;

    @NotBlank(message = "Le prénom ne peut pas être vide")
    @Column(length = 64, nullable = false)
    private String prenom;

    @Column(length = 64)
    private String photo;

    private boolean active;

    // Association avec un seul rôle (ManyToOne)
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    // === CONSTRUCTEURS ===
    public Utilisateur() {}

    public Utilisateur(String nom, String prenom, String email, String password, Role role) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Utilisateur(String email, boolean active, String nom, String prenom, String password, String photo, Role role) {
        this.email = email;
        this.active = active;
        this.nom = nom;
        this.prenom = prenom;
        this.password = password;
        this.photo = photo;
        this.role = role;
    }

    public Utilisateur(int id, String email, boolean active, String nom, String prenom, String password, String photo, Role role) {
        this.id = id;
        this.email = email;
        this.active = active;
        this.nom = nom;
        this.prenom = prenom;
        this.password = password;
        this.photo = photo;
        this.role = role;
    }

    // === GETTERS & SETTERS ===
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }


    // === AFFICHAGE ===
    public String afficherTitreDesColonnes() {
        return String.format(" %-10s  %30s %15s %15s %15s %15s %25s", "Id", "Email", "Active", "Nom", "Prenom", "Password", "Photo") +
                "\n --------------------------------------------------------------------------------------------------------------------------------------";
    }

    @Override
    public String toString() {
        return String.format(" %-10s   %30s %15b %15s %15s %15s %25s ",
                this.id, this.email, this.active, this.nom, this.prenom, this.password, this.photo);
    }
}
