CREATE TABLE `pvm`.`Categorie` (
  `Code` varchar(10) NOT NULL,
  `Omschrijving` varchar(60) NOT NULL,
  PRIMARY KEY (`Code`),
  UNIQUE KEY `Code_UNIQUE` (`Code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `pvm`.`Risico` (
  `Code` int(11) NOT NULL,
  `Omschrijving` varchar(80) NOT NULL,
  PRIMARY KEY (`Code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE `pvm`.`Effect` (
  `TickerId` varchar(60) NOT NULL,
  `Naam` varchar(60) NOT NULL,
  `ISIN` varchar(60) NOT NULL,
  `Categorie` varchar(10) NOT NULL,
  `Risico` int(11) NOT NULL,
  `Koers` decimal(10,4) NOT NULL DEFAULT '0.0000',
  `Div` decimal(10,4) NOT NULL DEFAULT '0.0000',
  PRIMARY KEY (`TickerId`),
  UNIQUE KEY `TickerId_UNIQUE` (`TickerId`),
  KEY `Risico_idx` (`Risico`),
  KEY `Categorie_idx` (`Categorie`),
  CONSTRAINT `Categorie` FOREIGN KEY (`Categorie`) REFERENCES `Categorie` (`Code`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `Risico` FOREIGN KEY (`Risico`) REFERENCES `Risico` (`Code`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE `pvm`.`Divident` (
  `TickerId` varchar(60) NOT NULL,
  `Datum` date NOT NULL,
  `Divident` decimal(10,2) NOT NULL,
  `Aantal` int(11) NOT NULL,
  `Bruto` decimal(10,2) NOT NULL,
  `Voorheffing` decimal(10,2) NOT NULL,
  `Netto` decimal(10,2) NOT NULL,
  PRIMARY KEY (`TickerId`,`Datum`),
  CONSTRAINT `TickerId_Idx` FOREIGN KEY (`TickerId`) REFERENCES `Effect` (`TickerId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `pvm`.`Kalender` (
  `TickerId` varchar(60) NOT NULL,
  `Maand` int(11) NOT NULL,
  `Dag` int(11) NOT NULL,
  `Divident` decimal(10,4) NOT NULL,
  `Voorheffing` decimal(4,3) NOT NULL,
  PRIMARY KEY (`TickerId`,`Maand`,`Dag`),
  CONSTRAINT `TickerId_FK` FOREIGN KEY (`TickerId`) REFERENCES `Effect` (`TickerId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `pvm`.`transactie` (
  `Ticker` varchar(60) NOT NULL COMMENT 'Ticker id',
  `Datum` date NOT NULL COMMENT 'Transactie datum',
  `Aantal` int(11) NOT NULL,
  `Prijs` decimal(10,4) NOT NULL,
  `Makelaarsloon` decimal(10,2) NOT NULL,
  `Beurstaks` decimal(10,2) NOT NULL,
  PRIMARY KEY (`Ticker`,`Datum`),
  KEY `Ticker` (`Ticker`),
  CONSTRAINT `TickerId` FOREIGN KEY (`Ticker`) REFERENCES `Effect` (`TickerId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `TriggerCode` (
  `Code` varchar(10) NOT NULL,
  `Omschrijving` varchar(60) DEFAULT NULL,
  PRIMARY KEY (`Code`),
  UNIQUE KEY `Code_UNIQUE` (`Code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `AankoopTrigger` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `TickerId` varchar(60) NOT NULL,
  `Code` varchar(10) NOT NULL,
  `Waarde` decimal(10,4) NOT NULL,
  `Aantal` int(11) NOT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `Id_UNIQUE` (`Id`),
  KEY `Ticker_Id_idx` (`TickerId`),
  KEY `Code_Id_idx` (`Code`),
  CONSTRAINT `Code_Id_Fk` FOREIGN KEY (`Code`) REFERENCES `TriggerCode` (`Code`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `Ticker_Id_Fk` FOREIGN KEY (`TickerId`) REFERENCES `Effect` (`TickerId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;

ALTER TABLE `AankoopTrigger` 
ADD COLUMN `Gem.Aank.Waarde` DECIMAL(10,4) NULL AFTER `Aantal`,
ADD COLUMN `Aankoop koers` DECIMAL(10,4) NULL AFTER `Gem.Aank.Waarde`,
ADD COLUMN `Investering` DECIMAL(10,4) NULL AFTER `Aankoop koers`,
ADD COLUMN `Status` VARCHAR(16) NULL AFTER `Investering`,
ADD COLUMN `Datum` DATETIME NOT NULL AFTER `Status`;

CREATE TABLE `VerkoopTrigger` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `TickerId` varchar(60) NOT NULL,
  `Code` varchar(10) NOT NULL,
  `Waarde` decimal(10,4) NOT NULL,
  `Aantal` int(11) NOT NULL,
  `Gem.Aank.Waarde` decimal(10,4) DEFAULT NULL,
  `Verkoop koers` decimal(10,4) DEFAULT NULL,
  `Omzet` decimal(10,4) DEFAULT NULL,
  `Winst` decimal(10,4) DEFAULT NULL,
  `Status` varchar(16) DEFAULT NULL,
  `Datum` datetime NOT NULL,
  PRIMARY KEY (`Id`),
  UNIQUE KEY `Id_UNIQUE` (`Id`),
  KEY `Ticker_Id_idx` (`TickerId`),
  KEY `Code_Id_idx` (`Code`),
  CONSTRAINT `Code_Id` FOREIGN KEY (`Code`) REFERENCES `TriggerCode` (`Code`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `Ticker_Id` FOREIGN KEY (`TickerId`) REFERENCES `Effect` (`TickerId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `pvm`.`Effect` 
	ADD COLUMN `AantalGekocht` int(11) NOT NULL DEFAULT '0' AFTER `Div`,
	ADD COLUMN `AankoopWaarde` decimal(10,4) NOT NULL DEFAULT '0.0000' AFTER `AantalGekocht`,
	ADD COLUMN `AankoopKost` decimal(10,4) NOT NULL DEFAULT '0.0000' AFTER `AankoopWaarde`,
	ADD COLUMN `AantalVerkocht` int(11) NOT NULL DEFAULT '0' AFTER `AankoopKost`,
	ADD COLUMN `VerkoopWaarde` decimal(10,4) NOT NULL DEFAULT '0.0000' AFTER `AantalVerkocht`,
	ADD COLUMN `VerkoopKost` decimal(10,4) NOT NULL DEFAULT '0.0000' AFTER `VerkoopWaarde`;

CREATE ALGORITHM=UNDEFINED DEFINER=`PVM_SCHEMA`@`localhost` SQL SECURITY DEFINER VIEW `pvm`.`divident_uitkeringen` AS select `e`.`Naam` AS `Naam`,`k`.`Maand` AS `Maand`,`k`.`Dag` AS `Dag`,round(`k`.`Divident`,2) AS `Divident`,sum(`t`.`Aantal`) AS `Aantal`,round((`k`.`Divident` * sum(`t`.`Aantal`)),2) AS `Bruto`,round(((sum(`t`.`Aantal`) * `k`.`Divident`) * (1 - `k`.`Voorheffing`)),2) AS `Netto` from ((`pvm`.`effect` `e` join `pvm`.`transactie` `t`) join `pvm`.`kalender` `k`) where ((`e`.`TickerId` = `k`.`TickerId`) and (`e`.`TickerId` = `t`.`Ticker`)) group by `t`.`Ticker`,`k`.`Maand` order by `k`.`Maand`,`k`.`Dag`;
CREATE ALGORITHM=UNDEFINED DEFINER=`PVM_SCHEMA`@`localhost` SQL SECURITY DEFINER VIEW `pvm`.`toestand` AS select `e`.`Naam` AS `Naam`,sum(`t`.`Aantal`) AS `Aantal`,round(sum((`t`.`Prijs` * `t`.`Aantal`)),2) AS `Aankoop`,round(sum((`t`.`Makelaarsloon` + `t`.`Beurstaks`)),2) AS `Kosten`,round(sum((((`t`.`Prijs` * `t`.`Aantal`) + `t`.`Makelaarsloon`) + `t`.`Beurstaks`)),2) AS `Prijs`,round((sum(`t`.`Aantal`) * `e`.`Koers`),2) AS `Waarde`,round(((sum(`t`.`Aantal`) * `e`.`Koers`) - sum((((`t`.`Prijs` * `t`.`Aantal`) + `t`.`Makelaarsloon`) + `t`.`Beurstaks`))),2) AS `Winst`,`e`.`Categorie` AS `Cat` from (`pvm`.`transactie` `t` join `pvm`.`effect` `e`) where (`t`.`Ticker` = `e`.`TickerId`) group by `t`.`Ticker` order by `e`.`Naam`;

UPDATE Effect 
SET AantalGekocht = (SELECT sum(transactie.Aantal)
                     FROM   transactie 
                     WHERE  Effect.TickerId = transactie.Ticker  and transactie.Aantal > 0
                     GROUP BY transactie.Ticker),
    AankoopWaarde = (SELECT sum(transactie.Aantal*transactie.Prijs)
                     FROM   transactie 
                     WHERE  Effect.TickerId = transactie.Ticker and transactie.Aantal > 0
                     GROUP BY transactie.Ticker) ,
    AankoopKost = (SELECT sum(transactie.Makelaarsloon+Beurstaks)
                   FROM   transactie 
                   WHERE  Effect.TickerId = transactie.Ticker and transactie.Aantal > 0
                   GROUP BY transactie.Ticker),
    AantalVerkocht = (SELECT sum(transactie.Aantal)*-1
                      FROM   transactie 
                      WHERE  Effect.TickerId = transactie.Ticker  and transactie.Aantal < 0
                      GROUP BY transactie.Ticker),
    VerkoopWaarde = (SELECT sum(transactie.Aantal*transactie.Prijs)*-1
                     FROM   transactie 
                     WHERE  Effect.TickerId = transactie.Ticker and transactie.Aantal < 0
                     GROUP BY transactie.Ticker) ,
    VerkoopKost = (SELECT sum(transactie.Makelaarsloon+Beurstaks)
                   FROM   transactie 
                   WHERE  Effect.TickerId = transactie.Ticker and transactie.Aantal < 0
                   GROUP BY transactie.Ticker)
WHERE Effect.TickerId = (SELECT  distinct(transactie.Ticker) 
                         FROM   transactie
                         WHERE  Effect.TickerId = transactie.Ticker);
                             
CREATE 
    ALGORITHM = UNDEFINED 
    DEFINER = `PVM_SCHEMA`@`localhost` 
    SQL SECURITY DEFINER
VIEW `toestand` AS
    SELECT 
       `e`.`Naam` AS `Naam`,
        (`e`.`AantalGekocht` - `e`.`AantalVerkocht`) AS `Aantal`,
        `e`.`Koers` AS `Koers`,
        ROUND(((`e`.`AantalGekocht` - `e`.`AantalVerkocht`) * `e`.`Koers`), 2) AS `Waarde`,
        `e`.`AantalGekocht` AS `Aantal gekocht`,
        ROUND(`e`.`AankoopWaarde`, 2) AS `Aankoop`,
        ROUND(`e`.`AankoopKost`, 2) AS `AankoopKosten`,
        ROUND((`e`.`AankoopWaarde` + `e`.`AankoopKost`), 2) AS `AankoopPrijs`,
        `e`.`AantalVerkocht` AS `Aantal verkocht`,
        ROUND(`e`.`VerkoopWaarde`, 2) AS `Verkoop`,
        ROUND(`e`.`VerkoopKost`, 2) AS `VerkoopKosten`,
        ROUND((`e`.`VerkoopWaarde` - `e`.`VerkoopKost`), 2) AS `VerkoopPrijs`,
        ROUND(((((((`e`.`AantalGekocht` - `e`.`AantalVerkocht`) * `e`.`Koers`) - `e`.`AankoopWaarde`) - `e`.`AankoopKost`) - `e`.`VerkoopKost`) + `e`.`VerkoopWaarde`), 2) AS `Winst`,
        `e`.`Categorie` AS `Cat`
    FROM
		`effect` `e`
    ORDER BY `e`.`Naam`