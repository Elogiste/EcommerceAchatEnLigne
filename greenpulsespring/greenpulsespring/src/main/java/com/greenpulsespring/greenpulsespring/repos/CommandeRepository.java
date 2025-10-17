package com.greenpulsespring.greenpulsespring.repos;

import com.greenpulsespring.greenpulsespring.entities.Commande;
import com.greenpulsespring.greenpulsespring.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface CommandeRepository extends JpaRepository<Commande, Integer> {
    List<Commande> findByAcheteur(Utilisateur acheteur);
    Optional<Commande> findByAcheteurAndEtat(Utilisateur acheteur, String etat);
    /*
    public List<Commande> findByProducteurId(Long idProducteur) {
        return this.findAll().stream()
                .filter(commande -> commande.getProduits().stream()
                        .anyMatch(cp -> cp.getProduit().getProducteur().getId().equals(idProducteur)))
                .collect(Collectors.toList());
    }
*/

    @Query("SELECT DISTINCT c FROM Commande c JOIN c.produits cp WHERE cp.produit.producteur.id = :producteurId")
    List<Commande> findCommandesByProducteurId(@Param("producteurId") int producteurId);
    @Query("SELECT c FROM Commande c JOIN c.produits cp WHERE cp.produit.producteur.id = :producteurId")
    List<Commande> findCommandesByProducteur(@Param("producteurId") Long producteurId);

}