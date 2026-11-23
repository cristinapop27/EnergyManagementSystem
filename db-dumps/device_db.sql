-- MySQL dump 10.13  Distrib 8.4.11, for Linux (x86_64)
--
-- Host: localhost    Database: device_db
-- ------------------------------------------------------
-- Server version	8.4.11

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `device_db`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `device_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `device_db`;

--
-- Table structure for table `device`
--

DROP TABLE IF EXISTS `device`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `device` (
  `id` binary(16) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `energy_class` enum('A','B','C','D','E','F','G') DEFAULT NULL,
  `max_consumption` double NOT NULL,
  `name` varchar(255) NOT NULL,
  `user_id` binary(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK59ml053k8nl7jglwg7k5hit5g` (`user_id`),
  CONSTRAINT `FK59ml053k8nl7jglwg7k5hit5g` FOREIGN KEY (`user_id`) REFERENCES `user_ref` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `device`
--

LOCK TABLES `device` WRITE;
/*!40000 ALTER TABLE `device` DISABLE KEYS */;
INSERT INTO `device` VALUES (_binary 'Z\Ó¥bµDÕê\Ë]Y\„\Ï','washer+drier','A',2,'washing machine',_binary 'u∂\Z\€@#èû^E”ìè\≈'),(_binary 'ÜZØo\Í;@vµhúÄ\Û\Ÿ','Electric Oven','A',2,'Oven',_binary '^\”\ˆC\‹C™V(\‹ooC'),(_binary '\“\rEz\ÿO\Ù£É)QÅ\‡\…','big fridge','A',2.5,'Fridge',_binary 'u∂\Z\€@#èû^E”ìè\≈');
/*!40000 ALTER TABLE `device` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_ref`
--

DROP TABLE IF EXISTS `user_ref`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_ref` (
  `id` binary(16) NOT NULL,
  `name` varchar(255) NOT NULL,
  `role` enum('ADMIN','CLIENT') NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKdhefmxeianlr0juhff9puvj8v` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_ref`
--

LOCK TABLES `user_ref` WRITE;
/*!40000 ALTER TABLE `user_ref` DISABLE KEYS */;
INSERT INTO `user_ref` VALUES (_binary '^\”\ˆC\‹C™V(\‹ooC','bibi','CLIENT','bibi'),(_binary '>\Ä\Z¿\rI¡õ\n2\˜òU£∏','Petra','ADMIN','petra'),(_binary 'u∂\Z\€@#èû^E”ìè\≈','Cristina','CLIENT','cris'),(_binary 'îoZÖ{\›FÖäL´\Í˚','Administrator','ADMIN','admin');
/*!40000 ALTER TABLE `user_ref` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-03 18:04:30
