package com.greenpulsespring.greenpulsespring.controller;

import com.google.zxing.WriterException;
import com.greenpulsespring.greenpulsespring.entities.Commande;
import com.greenpulsespring.greenpulsespring.entities.Produit;
import com.greenpulsespring.greenpulsespring.entities.Utilisateur;  // Import de l'entité Utilisateur
import com.greenpulsespring.greenpulsespring.repos.ProduitRepository;
import com.greenpulsespring.greenpulsespring.service.ProduitService;
import com.greenpulsespring.greenpulsespring.util.QrCodeGenerator;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class ProduitController {

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private ProduitService produitService;

    // Afficher la liste des produits
    @GetMapping("/produits")
    public String afficherLesProduits(Model model, HttpSession session, Commande commande) {
        Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateurConnecte");
        List<Produit> listeProduits;

        if (utilisateurConnecte != null && utilisateurConnecte.getRole().getNom().equals("PRODUCTEUR")) {
            // Producteur : afficher uniquement ses produits
            Long idProducteur = Long.valueOf(utilisateurConnecte.getId());
            listeProduits = produitService.getProduitsParProprietaire(idProducteur);
        } else {
            // Acheteur ou non connecté : afficher tous les produits
            listeProduits = produitService.getTousLesProduits();
        }

        for (Produit produit : listeProduits) {
            commande = produit.getCommande(); // Par exemple, supposer que chaque produit a une commande associée
            model.addAttribute("commande", commande);
        }

        model.addAttribute("listeProduits", listeProduits);
        model.addAttribute("role", utilisateurConnecte != null ? utilisateurConnecte.getRole().getNom() : null);
        model.addAttribute("userId", utilisateurConnecte != null ? utilisateurConnecte.getId() : null);

        return "produits";
    }

    // Afficher le formulaire d'ajout de produit
    @GetMapping("/ajouterProduit")
    public String afficherFormulaire(Model model) {
        model.addAttribute("produit", new Produit());
        return "ajouterProduit";
    }

    // Afficher le formulaire d'ajout de produit
    @PostMapping("/ajouterProduit/save")
    public String ajouterProduit(@Valid @ModelAttribute("produit") Produit produit,
                                 BindingResult bindingResult,
                                 @RequestParam("fileImage") MultipartFile multipartFile,
                                 Model model, HttpSession session) throws IOException {

        // Vérification si l'utilisateur est connecté
        Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateurConnecte");
        if (utilisateurConnecte == null) {
            model.addAttribute("message", "Veuillez vous connecter avant d'ajouter un produit.");
            return "redirect:/connexion";  // Redirige l'utilisateur vers la page de connexion
        }

        // Si des erreurs de validation existent, ne pas enregistrer le produit
        if (bindingResult.hasErrors()) {
            model.addAttribute("produit", produit);
            return "ajouterProduit";
        }

        // Gestion du fichier image
        if (!multipartFile.isEmpty()) {
            // Récupérer le nom du fichier et le type de contenu
            String chemin = multipartFile.getOriginalFilename();
            String typeContenu = multipartFile.getContentType();

            // Nettoyer le nom du fichier pour éviter des problèmes de sécurité
            String fileName = StringUtils.cleanPath(chemin);
            produit.setPhoto(fileName);

            // Créer le répertoire d'images si nécessaire
            File directory = new File("src/main/resources/static/images/produits");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Sauvegarder l'image sur le serveur
            File serverFile = new File(directory.getAbsolutePath() + File.separator + fileName);
            multipartFile.transferTo(serverFile);
        }

        // Associer le produit à l'utilisateur connecté
        produit.setProducteur(utilisateurConnecte);  // Lier le produit à l'utilisateur connecté

        // Appeler le service pour ajouter le produit
        produitService.ajouterProduit(produit, session);

        // Message de succès
        model.addAttribute("message", "Le produit a été ajouté avec succès.");
        return "redirect:/produits";  // Redirige vers la liste des produits
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public String handleMissingServletRequestPartException(MissingServletRequestPartException ex) {
        // Vous pouvez loguer l'exception ou afficher les détails dans la page d'erreur
        System.out.println("Erreur : " + ex.getMessage());
        return "error";
    }


    // Rechercher un produit par son nom
    @GetMapping("/rechercher/produit")
    public String rechercherProduit(Model model, @Param("nom") String nom) {
        List<Produit> listeProduits = produitService.rechercherProduitParNom(nom);
        model.addAttribute("listeProduits", listeProduits);
        model.addAttribute("nom", nom);
        return "produits";
    }

    // Télécharger l'image d'un produit
    @GetMapping("/images/produits/{fileId}")
    public void telechargerFichierProduit(@PathVariable String fileId, HttpServletResponse response) throws IOException {
        // Définir le répertoire où les images sont stockées
        File directory = new File("src/main/resources/static/images/produits");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Créer un fichier correspondant à l'ID de l'image
        File file = new File(directory.getAbsolutePath() + File.separator + fileId);
        if (file.exists()) {
            // Définir le type de contenu pour l'image
            response.setContentType("image/jpeg");
            response.setHeader("Content-Disposition", "inline; filename=\"" + fileId + "\"");

            // Lire et envoyer le fichier dans la réponse HTTP
            try (FileInputStream fileInputStream = new FileInputStream(file);
                 OutputStream outputStream = response.getOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        }
    }



    //
    @GetMapping("/modifierProduit/{id}")
    public String modifierProduit(@PathVariable("id") int id, Model model, HttpSession session) {
        Produit produit = null;
        Optional<Produit> produitOpt = produitService.findById(id);
        if(produitOpt.isPresent()){
            produit = produitOpt.get();
        }
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateurConnecte");

        // Vérification de propriété
        if (produit == null || utilisateur == null ||
                (!Objects.equals(produit.getProducteur().getId(), utilisateur.getId())
                        && !utilisateur.getRole().getNom().equals("Administrateur"))) {
            return "redirect:/produits";
        }


        model.addAttribute("produit", produit); // pré-remplissage
        return "ajouterProduit"; // réutilisation de la page
    }

    //@PostMapping("/modifierProduit/save")
    @PostMapping("/modifierProduit/save")
    public String enregistrerModification(@ModelAttribute("produit") Produit produit,
                                          @RequestParam("fileImage") MultipartFile fileImage,
                                          HttpSession session) throws IOException {

        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateurConnecte");

        if (utilisateur == null) {
            return "redirect:/connexion";
        }
        Optional<Produit> produitOpt = produitService.findById(produit.getId());
        if (!produitOpt.isPresent()) {
            return "redirect:/produits";
        }

        Produit produitExistant = produitOpt.get();

        // Vérification de propriété
        if (!Objects.equals(produitExistant.getProducteur().getId(), utilisateur.getId())
                && !utilisateur.getRole().getNom().equals("Administrateur")) {
            return "redirect:/produits";
        }


        // Gérer l'image si elle a été modifiée
        if (!fileImage.isEmpty()) {
            String fileName = StringUtils.cleanPath(fileImage.getOriginalFilename());
            File uploadDir = new File("src/main/resources/static/images/produits");

            // Supprimer l'ancienne image si elle existe
            if (produitExistant.getPhoto() != null) {
                File ancienneImage = new File(uploadDir, produitExistant.getPhoto());
                if (ancienneImage.exists()) {
                    ancienneImage.delete();  // Supprimer l'ancienne image
                }
            }

            // Sauvegarder la nouvelle image
            fileImage.transferTo(new File(uploadDir, fileName));
            produitExistant.setPhoto(fileName);
        }

        // Mettre à jour les champs modifiables
        produitExistant.setNom(produit.getNom());
        produitExistant.setType(produit.getType());
        produitExistant.setDescription(produit.getDescription());
        produitExistant.setQuantite(produit.getQuantite());
        produitExistant.setPrix(produit.getPrix());
        produitExistant.setDateAjout(produit.getDateAjout());

        produitRepository.save(produitExistant);

        return "redirect:/produits";
    }


    @PostMapping("/supprimerProduit/{id}")
    public String supprimerProduit(@PathVariable("id") int id, HttpSession session) {
        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateurConnecte");

        if (utilisateur != null) {
            produitService.supprimerProduitAvecRelations(id, utilisateur);
        }

        return "redirect:/produits";
    }

    @GetMapping("/qr/{id}")
    public String qrCodeProduit(@PathVariable int id, Model model) {
        Produit produit = produitRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Produit non trouvé"));

        String url = "https://f93f-204-48-94-47.ngrok-free.app/produit/" + id;
        byte[] image;
        try {
            image = QrCodeGenerator.getQRCodeImage(url, 250, 250);
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Erreur lors de la génération du QR Code", e);
        }

        String qrcode = Base64.getEncoder().encodeToString(image);
        model.addAttribute("url", url);
        model.addAttribute("qrcode", qrcode);
        model.addAttribute("produit", produit);

        return "qr_produit";
    }

    @GetMapping("/produit/{id}")
    public String afficherProduit(@PathVariable int id, Model model) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé"));
        model.addAttribute("produit", produit);
        return "produit_detail";
    }

    @GetMapping("/parametres")
    public String afficherParametres(Model model) {
        return "settings";
    }

}
