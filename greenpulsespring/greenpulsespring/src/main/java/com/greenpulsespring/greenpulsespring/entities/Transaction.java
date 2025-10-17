package com.greenpulsespring.greenpulsespring.entities;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name="transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_commande", nullable = false)
    private Commande id_commande;

    @Column(name = "date_livraison")
    private Date dateTransaction;
    @Column(nullable = false)
    private double montant;
    @Column(length = 40,nullable = false)
    private String modePaiement;
    @Column(length = 40,nullable = false)
    private String etatTransaction;
    public Transaction(){

    }
    public Transaction(int id, Commande id_commande, Date dateTransaction, double montant, String modePaiement, String etatTransaction) {
        this.id = id;
        this.id_commande = id_commande;
        this.dateTransaction = dateTransaction;
        this.montant = montant;
        this.modePaiement = modePaiement;
        this.etatTransaction = etatTransaction;
    }

    public Transaction(Commande id_commande, Date dateTransaction, double montant, String modePaiement, String etatTransaction) {
        this.id_commande = id_commande;
        this.dateTransaction = dateTransaction;
        this.montant = montant;
        this.modePaiement = modePaiement;
        this.etatTransaction = etatTransaction;
    }

    public Date getDateTransaction() {
        return dateTransaction;
    }

    public double getMontant() {
        return montant;
    }

    public String getEtatTransaction() {
        return etatTransaction;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public Commande getId_commande() {
        return id_commande;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setId_commande(Commande id_commande) {
        this.id_commande = id_commande;
    }

    public void setDateTransaction(Date dateTransaction) {
        this.dateTransaction = dateTransaction;
    }
    public void setEtatTransaction(String etatTransaction) {
        this.etatTransaction = etatTransaction;
    }
    public void setMontant(double montant) {
        this.montant = montant;
    }

    public void setModePaiement(String modePaimement) {
        this.modePaiement = modePaimement;
    }



    @Override
    public String toString() {
        return "Transaction{" + "id=" + id + ", id_commande=" + id_commande + ", dateTransaction=" + dateTransaction + ", montant=" + montant + ", modePaimement=" + modePaiement + ", etatTransaction=" + etatTransaction + '}';
    }

  /*  public boolean effectuerPaiement(Commande commande, float montant){
        boolean retour = false;

        if(commande.getPrix() == montant){
            this.montant = montant;
            this.dateTransaction = new Date();
            this.etatTransaction = "Payée";

            CommandeImpDao commandeDao = new CommandeImpDao();
        if (commandeDao.updateCommande(commande)) {
            retour = true;
            }
        } else {
            System.out.println("Le montant ne correspond pas au prix de la commande.");
        }
        return retour;
        }*/

   /* public boolean annulerPaiement(Commande commande){
        boolean retour = false;

        this.etatTransaction = "Annulée";

        CommandeImpDao commandeDao = new CommandeImpDao();
        commande.setEtat("Annulée");
        if (commandeDao.updateCommande(commande)) {
            retour = true;
        }

        return retour;

    }*/

}