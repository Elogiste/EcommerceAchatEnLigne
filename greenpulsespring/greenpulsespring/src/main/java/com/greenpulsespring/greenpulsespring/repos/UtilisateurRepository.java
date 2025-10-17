package com.greenpulsespring.greenpulsespring.repos;

import com.greenpulsespring.greenpulsespring.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
    @Query("SELECT u FROM Utilisateur u WHERE u.email = :email")
    //@Param est utilisé pour lier le paramètre method au paramètre Query.
    public Utilisateur getUtilisateurByEmail(@Param("email") String email);

    @Query("SELECT u FROM Utilisateur u WHERE u.email = :email and u.password=:password")
    //@Param est utilisé pour lier le paramètre method au paramètre Query.
    public Utilisateur getUtilisateurByEmailAndPassword(@Param("email") String email,@Param("password") String password);

    //Cette méthode suit la convention spécifié par Spring Data JPA
    public Long countById(Integer id);
    // 2 pour le 2 eme parametre active et 1 pour le 1er param`tre id
    @Query("UPDATE Utilisateur u SET u.active = ?2 WHERE u.id = ?1")
    // Comme c'est une méthode mis à jour on doit utiliser l'annotation @Modifying
    @Modifying
    public void updateActiveStatus(Integer id, boolean active);


    //Le 1 est l'argument pour premier paramètre qui est le mot clé
    @Query("SELECT u FROM Utilisateur u WHERE u.nom  LIKE %?1% OR u.prenom  LIKE %?1%")
    public List<Utilisateur> findAll(String keyword);


    //Ici, "u.roles" fait référence à la liste de rôles associés à un utilisateur.
    // Nous joignons ensuite la relation entre
    // "Utilisateur" et "Role" en utilisant "JOIN u.roles r"
    // pour accéder à l'objet "Role" associé à chaque utilisateur.
    @Query("SELECT u FROM Utilisateur u WHERE u.role.nom = :nomRole")
    //public List<Utilisateur> findByRoleNom(@Param("nomRole") String nomRole);
    List<Utilisateur> findByRoleNom(String nomRole);


    @Query("SELECT u FROM Utilisateur u WHERE u.photo = :fileName")
    public List<Utilisateur> findByFileName(@Param("fileName") String fileName);

    /*
    utiliser la convention de nommage Spring Data JPA pour que Spring puisse générer
    la requête correcte en fonction des paramètres
     */
    // Recherche par nom (utilisation de la convention de nommage)
    public Utilisateur findByNom(String nom);
    //  Recherche par nom et password (utilisation de la convention de nommage)
    public Utilisateur findByNomAndPassword(String nom, String password);

    // Recherche par nom equale (utilisation de la convention de nommage)
    public Utilisateur findByNomEquals(String nom);

//    @Query("SELECT u FROM Utilisateur u WHERE u.photo = :fileName")
    //  public List<Utilisateur> findByFileName(@Param("fileName") String fileName);

    List<Utilisateur> findByPhoto(String photo);

    // Recherche par nom, email ou rôle
    List<Utilisateur> findByNomContainingIgnoreCaseOrEmailContainingIgnoreCaseOrRoleNomContainingIgnoreCase(String nom, String email, String roleNom);

    long countByActive(boolean active);

    @Query("SELECT u.role.nom, COUNT(u) FROM Utilisateur u GROUP BY u.role.nom")
    List<Object[]> countUtilisateursGroupByRole();

}
