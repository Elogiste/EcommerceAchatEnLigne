package com.greenpulsespring.greenpulsespring.repos;

import com.greenpulsespring.greenpulsespring.entities.Commande_Produit;
import com.greenpulsespring.greenpulsespring.entities.Produit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommandeProduitRepository extends JpaRepository<Commande_Produit, Integer> {

    List<Commande_Produit> findByProduit(Produit produit);
    void deleteByProduit(Produit produit);
}
