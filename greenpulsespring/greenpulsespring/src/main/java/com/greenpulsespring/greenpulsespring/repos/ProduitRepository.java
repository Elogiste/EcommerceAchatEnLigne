package com.greenpulsespring.greenpulsespring.repos;

import com.greenpulsespring.greenpulsespring.entities.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Integer> {
    public List<Produit> findByNom(String nom);

    List<Produit> findByPhoto(String photo);

    List<Produit> findByNomContainingIgnoreCase(String nom);

    List<Produit> findByProducteurId(Long id);
}