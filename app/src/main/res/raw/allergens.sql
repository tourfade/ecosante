--liquibase formatted sql

--changeset FadelTOURE1:(dbms:postgresql failOnError:true)
INSERT INTO allergen(code,name, description) 
    VALUES
    (100,'Acariens','En particulier Dermatophagoides pteronyssinus et Dermatophagoides farinae'),
    (101,'Pollens de graminées','dactyle, phléole, ivraie'),
    (102,'Poils de chat',''),
    (103,'Blattes et cafards',''),
    (104,'Pollens de bétulacées','aulne, bouleau, charme, noisetier'),
    (105,'Moisissures','Alternaria, Cladosporium'),
    (106,'Lavande',''),
    (107,'Langue','essence de lavande'),
    
    (108,'Drupacées','Allergie Alimentaire'),
    (109,'Ombellifères','Allergie Alimentaire'),
    (110,'Œuf','Allergie Alimentaire'),
    (111,'Crustacés','Allergie Alimentaire'),
    (112,'Poisson','Allergie Alimentaire'),
    (113,'Lait','Allergie Alimentaire'),
    (114,'Blé','Allergie Alimentaire'),
    (115,'Légumineuses','Allergie Alimentaire'),
    (116,'Banane','Allergie Alimentaire'),
    (117,'Avocat','Allergie Alimentaire'),
    (118,'Kiwi','Allergie Alimentaire'),
    (119,'Moules','Allergie Alimentaire'),
    (120,'Pommes de terre ','Allergie Alimentaire'),
    (121,'Tournesol','Allergie Alimentaire'),
    (122,'Bœuf','Allergie Alimentaire'),
    (123,'Arachide','Allergie Alimentaire'),
    (124,'Mangue','Allergie Alimentaire'),
    
    (125,'Sérums et vaccins','Allergie Medicamenteuse'),
    (126,'Contrastant radiologique injecté','les molécules des produits contrastant contenant de l''iode, on croit souvent à tort qu''il s''agit d''une allergie à l''iode1. Allergie Medicamenteuse'),
    (127,'Antibiotiques','pénicillines, céphalosporines. Allergie Medicamenteuse'),
    (128,'Aspirine','Allergie Medicamenteuse'),
    (129,'Procaïne','Allergie Medicamenteuse'),
    
    (130,'Venin d''hyménoptères','Allergie au Venin'),
    (131,'Apidae ','abeille, bourdon. Allergie au Venin'),
    (132,'Vespidae ','guêpe vespula, guêpe poliste, vrai frelon. Allergie au Venin');
    

    








