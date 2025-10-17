package com.greenpulsespring.greenpulsespring.repos;

import com.greenpulsespring.greenpulsespring.entities.Messages;
import com.greenpulsespring.greenpulsespring.entities.Utilisateur;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends CrudRepository<Messages,Integer> {
    @Query("SELECT m FROM Messages m WHERE m.id_envoyeur = :envoyeur OR m.id_receveur = :receveur")
    public List<Messages> findByEnvoyeurOrReceveur(Utilisateur envoyeur, Utilisateur receveur);
    @Query("SELECT m FROM Messages m WHERE m.id_receveur.nom LIKE %?1% OR m.id_receveur.prenom LIKE %?1%")
    public List<Messages> findAll(String keyword);

    Optional<Messages> findById(Integer id);
}
