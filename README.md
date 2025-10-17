# Green Pulse - Plateforme de Commerce de Grains en Ligne

## Version 2 : Développement avec Spring Boot

Bienvenue dans le dépôt Git du projet **Green Pulse**, une plateforme web destinée à connecter directement les producteurs de légumineuses avec les acheteurs, en automatisant les processus de mise en relation et de transaction.

---

## Contexte

Le marché des légumineuses est dominé par quelques multinationales. Green Pulse vise à casser ce modèle en fournissant une **plateforme transactionnelle en ligne**, transparente, équitable, et automatisée.

Les technologies suivantes :

- Spring Boot
- Thymeleaf
- Bootstrap
- JavaScript / JQuery / Ajax
- MySQL
- GitLab
- MVC (Model-View-Controller)

---

##  Objectifs de cette version

- Aapplication Java Spring Boot.
- Mettre en place une architecture MVC propre.
- Ajouter des vues dynamiques via **Thymeleaf**.
- Assurer une navigation fluide et interactive (Ajax / JQuery).
- Intégrer une base de données MySQL pour la gestion des utilisateurs, produits et transactions.

---

## Fonctionnalités Implémentées

###  Producteurs
- Création de compte et authentification.
- Gestion de l'inventaire de grains (ajout / modification / suppression).
- Fixation des prix de vente.
- Réception et validation des offres d’achat.
- Suivi des ventes via un tableau de bord personnalisé.

### 🛒 Acheteurs
- Création de compte et connexion sécurisée.
- Recherche de grains disponibles avec filtres (type, prix, localisation...).
- Génération de demandes d'achat.
- Négociation et validation de commande.

###  Administrateurs
- Gestion des comptes utilisateurs (producteurs/acheteurs).
- Surveillance des transactions.
- Gestion des paramètres globaux de la plateforme.

---

## 🗂️ Structure du Projet

green-pulse/
├── src/
│ ├── main/
│ │ ├── java/com/greenpulse/...
│ │ ├── resources/
│ │ │ ├── templates/ # Vues Thymeleaf
│ │ │ ├── static/ # CSS, JS, images
│ │ │ └── application.properties
│ └── test/
├── pom.xml
└── README.md


---

## Technologies Utilisées

- **Backend** : Spring Boot, Spring MVC, Spring Data JPA
- **Frontend** : Thymeleaf, HTML5, CSS3, Bootstrap, JavaScript, JQuery, Ajax
- **Base de données** : MySQL
- **Gestion de version** : GitLab

---

##  Instructions pour exécuter le projet

###  Prérequis

- Java 17+
- Maven
- MySQL
- Git

###  Configuration

1. **Clonez le projet :**

   ```bash
   git clone https://github........(url du repository)
   cd [repository]

2. Créez la base de données MySQL :

CREATE DATABASE greenpulse CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

3. Configurez les identifiants de la base de données dans application.properties :

spring.datasource.url=jdbc:mysql://localhost:3306/greenpulse
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update

4. Lancez l'application :

./mvnw spring-boot:run


5. Accédez à l'application via : http://localhost:8080


### Auteurs / Équipe

Chef de projet : Eloge Assiobo (@elogiste)

Développeurs :  * Eloge Assiobo
                * Lucas Bidault-Meresse
                * Lleyton Habimana

Établissement : Cégep de Rosemont


### Licence

Ce projet est un livrable académique sous la demande de l'entreprise Green Pulse Export. Toute réutilisation est interdite sans autorisation explicite des contributeurs, maîtres d'oeuvre et d'ouvrage du projet.