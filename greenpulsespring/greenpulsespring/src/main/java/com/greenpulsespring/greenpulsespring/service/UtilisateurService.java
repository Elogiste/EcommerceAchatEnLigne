package com.greenpulsespring.greenpulsespring.service;

import com.greenpulsespring.greenpulsespring.entities.Role;
import com.greenpulsespring.greenpulsespring.entities.Utilisateur;
import com.greenpulsespring.greenpulsespring.repos.RoleRepository;
import com.greenpulsespring.greenpulsespring.repos.UtilisateurRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class UtilisateurService {

    @Autowired
    UtilisateurRepository repos;   //     UtilisateurRepository repos = new  UtilisateurRepository ()
    @Autowired   // il s'agit de l'injection de dépendance
    RoleRepository reposRole;
    public List<Utilisateur> afficherUtilisateurs(){
        return ( List<Utilisateur> ) repos.findAll();
    }

    public List<Role> afficherRoles(){
        return ( List<Role> ) reposRole.findAll();
    }

    public Utilisateur enregistrer(Utilisateur utilisateur){
        return repos.save(utilisateur);
    }

    public List<Utilisateur> chercherPhoto(String photo) throws UtilisateurNotFoundException {


        try{
            return  repos.findByFileName(photo);
        }catch (NoSuchElementException exception){
            throw  new UtilisateurNotFoundException("On ne peut pas trouver un utilisateur avec l'id" + photo);
        }
    }

    public boolean isEmailUnique(String email,Integer id) {
        //On cherche un utilisateur à partir de son email
        Utilisateur userByEmail = repos.getUtilisateurByEmail(email);
        //On vérifie l'unicité de l'email
        // quand on crée un nouveau utilisateur
        // S'il l'utilisateur n'existe pas déjà dans la bd avec l'email passé en paramètre
        // cela suppose que l'email n'existe pas
        // Autrement dit la méthode retourne true alors, l'email est unique parce que
        // l'utilisateur est null
        if (userByEmail == null) return true;

        //  return userByEmail == null; possibilité
        // sinon qu'existe un utilisateur avec le même email

        //Si l' id utilisateur est null, il n'existe pas, isCreatingNew = true sinon false
        boolean isCreatingNewUser = false;
        if (id == null){
            isCreatingNewUser=true;
        }
        //Si l' id utilisateur n'existe pas mais l' email existe,
        //on retourne false car pas email unique, on ne peut pas creer un nouveau utilisateur
        //dans le mode de création utilisateur
        if(isCreatingNewUser){
            //mais l'email existe, on retourne false car pas unique email
            if (userByEmail != null) return false;
        }else{
            //dans le mode d'edition utilisateur
            //Si l'id existe mais l'id qu'on edite  est différent de celui
            //de l'utilisateur possedant l'email,
            //on retourne false, car on ne peut pas creer un nouveau , puisqu email existe
            if (userByEmail.getId() != id) {
                return false;
            }
        }
        //dans tous les cas on peut creer, editer
        return true;

    }

    public void supprimerUtilisateur(Integer id) throws UtilisateurNotFoundException {
        Long countById = repos.countById(id);
        //pas d'utilisateur dans la bd
        if (countById == null || countById == 0) {
            throw new UtilisateurNotFoundException("On ne peut pas trouver un utilisateur avec l'id" + id);
        }

        repos.deleteById(id);
    }

    public Utilisateur trouverUtilisateur(Integer id) throws UtilisateurNotFoundException {

        try {
            Utilisateur utilisateur =  repos.findById(id).get();
            return utilisateur;

        }catch (NoSuchElementException ex){
            throw new UtilisateurNotFoundException("On ne peut pas trouver utilisateur avec l'id " +id);
        }

    }

    public void updateActiveStatus(Integer id, boolean enable) {
        repos.updateActiveStatus(id, enable);
    }

    public List<Utilisateur> rechercherUtilisateur(String keyword) {

        if (keyword != null) {
            return repos.findAll(keyword);
        }

        return  null;
    }

    public Utilisateur rechercherUtilisateurParEmailMotDePasse(String email,String password) {
        //On cherche un utilisateur à partir de son email
        Utilisateur userByEmailAndPassword = repos.getUtilisateurByEmailAndPassword(email,password);
        if(userByEmailAndPassword==null){
            return null;
        }
        return userByEmailAndPassword;
    }

    public List<Utilisateur> rechercherUtilisateurParRole(String nomRole) {
        return repos.findByRoleNom(nomRole);
    }

    // Méthode de recherche par mot-clé
    public List<Utilisateur> rechercherParKeyword(String keyword) {
        // Rechercher par nom, email ou rôle
        return repos.findByNomContainingIgnoreCaseOrEmailContainingIgnoreCaseOrRoleNomContainingIgnoreCase(keyword, keyword, keyword);
    }

    // Méthode pour récupérer tous les utilisateurs
    public List<Utilisateur> trouverTousLesUtilisateurs() {
        return repos.findAll();
    }

    //Pour les stats admin
    public long compterUtilisateurs() {
        return repos.count();
    }

    public long compterUtilisateursActifs(boolean active) {
        return repos.countByActive(active);
    }

    public List<Object[]> compterUtilisateursParRole() {
        return repos.countUtilisateursGroupByRole();
    }



}
