-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: greenpulsespring
-- ------------------------------------------------------
-- Server version	8.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `commande`
--

DROP TABLE IF EXISTS `commande`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `commande` (
  `id` int NOT NULL AUTO_INCREMENT,
  `date_livraison` date NOT NULL,
  `etat` varchar(255) NOT NULL,
  `prix` double NOT NULL,
  `acheteur_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6c5idm5swn7i506tfulb52t6v` (`acheteur_id`),
  CONSTRAINT `FK6c5idm5swn7i506tfulb52t6v` FOREIGN KEY (`acheteur_id`) REFERENCES `utilisateurs` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `commande`
--

LOCK TABLES `commande` WRITE;
/*!40000 ALTER TABLE `commande` DISABLE KEYS */;
INSERT INTO `commande` VALUES (1,'2025-05-07','Rejetée',494,3),(2,'2025-05-07','Acceptée',123,3),(3,'2025-05-07','Rejetée',123,3),(4,'2025-05-07','En attente',123,3),(5,'2025-05-07','Acceptée',120,3),(6,'2025-05-07','Acceptée',245,3),(7,'2025-05-07','Rejetée',125,3),(8,'2025-05-07','Acceptée',246,7),(9,'2025-05-07','Rejetée',123,3),(10,'2025-05-07','Rejetée',185,3),(11,'2025-05-19','Acceptée',120,3),(12,'2025-05-19','En attente',120,3),(13,'2025-05-20','En attente',25,3),(14,'2025-05-20','Acceptée',25,3),(15,'2025-05-20','En attente',60,3),(16,'2025-05-20','En attente',0,3),(17,'2025-05-21','Acceptée',10,3);
/*!40000 ALTER TABLE `commande` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `commande_produit`
--

DROP TABLE IF EXISTS `commande_produit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `commande_produit` (
  `id` int NOT NULL AUTO_INCREMENT,
  `quantite` int NOT NULL,
  `commande_id` int NOT NULL,
  `produit_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKs9k1rm4xmlmu7ulyhpw4q0svl` (`commande_id`),
  KEY `FKhp4pp75emrkv8kfqq1hn2rk7s` (`produit_id`),
  CONSTRAINT `FKhp4pp75emrkv8kfqq1hn2rk7s` FOREIGN KEY (`produit_id`) REFERENCES `produit` (`id`),
  CONSTRAINT `FKs9k1rm4xmlmu7ulyhpw4q0svl` FOREIGN KEY (`commande_id`) REFERENCES `commande` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `commande_produit`
--

LOCK TABLES `commande_produit` WRITE;
/*!40000 ALTER TABLE `commande_produit` DISABLE KEYS */;
INSERT INTO `commande_produit` VALUES (2,1,1,3),(6,1,5,4),(7,1,6,3),(8,1,6,4),(9,1,7,3),(14,1,10,3),(15,1,10,6),(16,1,10,6),(17,1,11,4),(18,1,12,4),(19,1,12,4),(22,1,15,6),(23,1,16,6),(24,1,17,4);
/*!40000 ALTER TABLE `commande_produit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `message` (
  `id` int NOT NULL AUTO_INCREMENT,
  `etat` varchar(40) NOT NULL,
  `message` varchar(2000) NOT NULL,
  `id_envoyeur` int NOT NULL,
  `id_receveur` int NOT NULL,
  `date_envoie` datetime(6) DEFAULT NULL,
  `nom_fichier` varchar(2000) DEFAULT NULL,
  `lu` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9rc8hgfaigqtiprsc6gycbllu` (`id_envoyeur`),
  KEY `FKsanqegttsx20edta72tw6h8vk` (`id_receveur`),
  CONSTRAINT `FK9rc8hgfaigqtiprsc6gycbllu` FOREIGN KEY (`id_envoyeur`) REFERENCES `utilisateurs` (`id`),
  CONSTRAINT `FKsanqegttsx20edta72tw6h8vk` FOREIGN KEY (`id_receveur`) REFERENCES `utilisateurs` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message`
--

LOCK TABLES `message` WRITE;
/*!40000 ALTER TABLE `message` DISABLE KEYS */;
INSERT INTO `message` VALUES (1,'envoyé','Bonjour cher producteur',3,2,'2025-05-13 14:44:13.429328',NULL,_binary ''),(2,'envoyé','Hi my dear',2,3,'2025-05-13 14:44:55.139841',NULL,_binary ''),(3,'envoyé','Bonjour',3,1,'2025-05-13 14:45:53.271120',NULL,_binary ''),(4,'envoyé','J\'aime le blé',3,1,'2025-05-13 14:46:17.047335','blé.jpeg',_binary ''),(5,'envoyé','buenos dias',1,3,'2025-05-13 14:47:04.446648',NULL,_binary ''),(6,'envoyé','hi',2,3,'2025-05-13 16:07:05.814070',NULL,_binary '\0'),(7,'envoyé','Bonjour',1,2,'2025-05-14 02:19:52.257819',NULL,_binary ''),(8,'envoyé','Acheteur Lleyton',1,3,'2025-05-14 03:17:37.990229',NULL,_binary '\0'),(9,'envoyé','<a href=\"https://youtu.be/E3L4uf1C_QM?si=VYsADKPcsy1523xt\" target=\"_blank\">https://youtu.be/E3L4uf1C_QM?si=VYsADKPcsy1523xt</a>',1,3,'2025-05-14 18:40:38.158632',NULL,_binary '\0');
/*!40000 ALTER TABLE `message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `produit`
--

DROP TABLE IF EXISTS `produit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `produit` (
  `id` int NOT NULL AUTO_INCREMENT,
  `date_ajout` date NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `nom` varchar(255) NOT NULL,
  `photo` varchar(255) DEFAULT NULL,
  `prix` double NOT NULL,
  `quantite` double NOT NULL,
  `type` varchar(255) NOT NULL,
  `producteur_id` int NOT NULL,
  `choix_prix_unitaire` varchar(255) DEFAULT NULL,
  `date_recolte` date DEFAULT NULL,
  `label` varchar(255) DEFAULT NULL,
  `origine_provenance` varchar(255) DEFAULT NULL,
  `prix_unitaire` double DEFAULT NULL,
  `commande_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK12j5p0dwwj788vigi93d8nvmp` (`producteur_id`),
  KEY `FKspek24pjiwwt3bxgt0qsg27x7` (`commande_id`),
  CONSTRAINT `FK12j5p0dwwj788vigi93d8nvmp` FOREIGN KEY (`producteur_id`) REFERENCES `utilisateurs` (`id`),
  CONSTRAINT `FKspek24pjiwwt3bxgt0qsg27x7` FOREIGN KEY (`commande_id`) REFERENCES `commande` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `produit`
--

LOCK TABLES `produit` WRITE;
/*!40000 ALTER TABLE `produit` DISABLE KEYS */;
INSERT INTO `produit` VALUES (3,'2025-04-30','Céréales','Blé','blé.jpeg',5,20,'cereales',2,'Kg',NULL,'Bio,Local','45.399041,-72.721834',5,NULL),(4,'2025-04-30','grain','Maïs','mais.jpg',10,50,'graine',2,'Kg',NULL,NULL,'45.745352,-73.600559',NULL,NULL),(6,'2025-04-30','','Haricot','haricot.jpg',60,20,'graine',8,'Kg',NULL,NULL,'',NULL,NULL);
/*!40000 ALTER TABLE `produit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nom` varchar(40) NOT NULL,
  `description` varchar(150) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_nom` (`nom`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'Administrateur','Peut tout faire'),(2,'Producteur','Produit les produits, gère les prix des produits, les clients, expédition, commandes et rapport de ventes'),(3,'Acheteur','Client potentiel des producteurs');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction`
--

DROP TABLE IF EXISTS `transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction` (
  `id` int NOT NULL AUTO_INCREMENT,
  `date_livraison` datetime(6) DEFAULT NULL,
  `etat_transaction` varchar(40) NOT NULL,
  `mode_paiement` varchar(40) NOT NULL,
  `montant` double NOT NULL,
  `id_commande` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKdglgvbqcwbt28gktnwyudej9q` (`id_commande`),
  CONSTRAINT `FKdglgvbqcwbt28gktnwyudej9q` FOREIGN KEY (`id_commande`) REFERENCES `commande` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction`
--

LOCK TABLES `transaction` WRITE;
/*!40000 ALTER TABLE `transaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `utilisateurs`
--

DROP TABLE IF EXISTS `utilisateurs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `utilisateurs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `prenom` varchar(64) NOT NULL,
  `nom` varchar(64) NOT NULL,
  `email` varchar(128) NOT NULL,
  `password` varchar(64) NOT NULL,
  `photo` varchar(64) DEFAULT NULL,
  `role_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_email` (`email`),
  KEY `FK_utilisateur_role` (`role_id`),
  CONSTRAINT `FK_utilisateur_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `utilisateurs`
--

LOCK TABLES `utilisateurs` WRITE;
/*!40000 ALTER TABLE `utilisateurs` DISABLE KEYS */;
INSERT INTO `utilisateurs` VALUES (1,_binary '','Eloge','Testeur','elogetesteur@example.com','Testeur123','ElogeTesteur.png',1),(2,_binary '','Lucas','Testeur','lucastesteur@example.com','Testeur123','LucasTesteur.png',2),(3,_binary '','Lleyton','Testeur','lleytontesteur@example.com','Testeur123','LleytonTesteur.png',3),(4,_binary '','Acheteur','Testeur','acheteur@testeur.com','@Testeur123','testeur.jpeg',3),(5,_binary '','Producteur','Testeur','producteur@testeur.com','@Testeur123','testeurprod.jpeg',2),(6,_binary '','Eloge','Assiobo','logiciel@logiciel.com','@Eloge123','elogeprod.jpeg',2),(7,_binary '','Eloge','Assiobo','eloge@eloge.com','@Ssiobo123','elogeachat.jpeg',3),(8,_binary '','Joel','Arnaud','joel@gmail.com','@Joel123','Capture.PNG',2),(9,_binary '','Max','Les fermes Max','max@max.com','Fermesm@x123','max.png',2),(10,_binary '','Green Pulse Export','Green Pulse Export','greenpulseexport@greenpulseexport.com','@Greenpulseexport123','greenpulseexport.jpeg',1),(11,_binary '','Youssef','Les fermes AYAD','youssef@youssef.com','@Youssef123','youss.png',2);
/*!40000 ALTER TABLE `utilisateurs` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-15  0:31:08
