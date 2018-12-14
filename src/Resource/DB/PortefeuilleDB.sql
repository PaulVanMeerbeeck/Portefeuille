CREATE TABLE `test`.`Categorie` (
  `Code` varchar(10) NOT NULL,
  `Omschrijving` varchar(60) NOT NULL,
  PRIMARY KEY (`Code`),
  UNIQUE KEY `Code_UNIQUE` (`Code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `test`.`Risico` (
  `Code` int(11) NOT NULL,
  `Omschrijving` varchar(80) NOT NULL,
  PRIMARY KEY (`Code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE `test`.`Effect` (
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


CREATE TABLE `test`.`Divident` (
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

CREATE TABLE `test`.`Kalender` (
  `TickerId` varchar(60) NOT NULL,
  `Maand` int(11) NOT NULL,
  `Dag` int(11) NOT NULL,
  `Divident` decimal(10,4) NOT NULL,
  `Voorheffing` decimal(4,3) NOT NULL,
  PRIMARY KEY (`TickerId`,`Maand`,`Dag`),
  CONSTRAINT `TickerId_FK` FOREIGN KEY (`TickerId`) REFERENCES `Effect` (`TickerId`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `test`.`transactie` (
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

CREATE ALGORITHM=UNDEFINED DEFINER=`PVM_SCHEMA`@`localhost` SQL SECURITY DEFINER VIEW `test`.`divident_uitkeringen` AS select `e`.`Naam` AS `Naam`,`k`.`Maand` AS `Maand`,`k`.`Dag` AS `Dag`,round(`k`.`Divident`,2) AS `Divident`,sum(`t`.`Aantal`) AS `Aantal`,round((`k`.`Divident` * sum(`t`.`Aantal`)),2) AS `Bruto`,round(((sum(`t`.`Aantal`) * `k`.`Divident`) * (1 - `k`.`Voorheffing`)),2) AS `Netto` from ((`pvm`.`effect` `e` join `pvm`.`transactie` `t`) join `pvm`.`kalender` `k`) where ((`e`.`TickerId` = `k`.`TickerId`) and (`e`.`TickerId` = `t`.`Ticker`)) group by `t`.`Ticker`,`k`.`Maand` order by `k`.`Maand`,`k`.`Dag`;
CREATE ALGORITHM=UNDEFINED DEFINER=`PVM_SCHEMA`@`localhost` SQL SECURITY DEFINER VIEW `test`.`toestand` AS select `e`.`Naam` AS `Naam`,sum(`t`.`Aantal`) AS `Aantal`,round(sum((`t`.`Prijs` * `t`.`Aantal`)),2) AS `Aankoop`,round(sum((`t`.`Makelaarsloon` + `t`.`Beurstaks`)),2) AS `Kosten`,round(sum((((`t`.`Prijs` * `t`.`Aantal`) + `t`.`Makelaarsloon`) + `t`.`Beurstaks`)),2) AS `Prijs`,round((sum(`t`.`Aantal`) * `e`.`Koers`),2) AS `Waarde`,round(((sum(`t`.`Aantal`) * `e`.`Koers`) - sum((((`t`.`Prijs` * `t`.`Aantal`) + `t`.`Makelaarsloon`) + `t`.`Beurstaks`))),2) AS `Winst`,`e`.`Categorie` AS `Cat` from (`pvm`.`transactie` `t` join `pvm`.`effect` `e`) where (`t`.`Ticker` = `e`.`TickerId`) group by `t`.`Ticker` order by `e`.`Naam`;


