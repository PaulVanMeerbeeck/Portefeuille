CREATE 
    ALGORITHM = UNDEFINED 
    DEFINER = `TEST_SCHEMA`@`localhost` 
    SQL SECURITY DEFINER
VIEW `toestand` AS
    SELECT 
        `e`.`Naam` AS `Naam`,
        `e`.`AantalInBezit` AS `Aantal`,
        `e`.`Koers` AS `Koers`,
        ROUND((`e`.`AantalInBezit` * `e`.`Koers`), 2) AS `Waarde`,
        `e`.`AantalGekocht` AS `Aantal gekocht`,
        ROUND(`e`.`AankoopWaarde`, 2) AS `Aankoop`,
        ROUND(`e`.`AankoopKost`, 2) AS `AankoopKosten`,
        ROUND((`e`.`AankoopWaarde` - `e`.`AankoopKost`),
                2) AS `AankoopPrijs`,
        `e`.`AantalVerkocht` AS `Aantal verkocht`,
        ROUND(`e`.`VerkoopWaarde`, 2) AS `Verkoop`,
        ROUND(`e`.`VerkoopKost`, 2) AS `VerkoopKosten`,
        ROUND((`e`.`VerkoopWaarde` + `e`.`VerkoopKost`),
                2) AS `VerkoopPrijs`,
        ROUND(`e`.`AantalInBezit` * (`e`.`Koers` - `e`.`GemiddeldePrijs`),2) AS `Winst`,
        `e`.`Categorie` AS `Cat`
    FROM
        `Effect` `e`
    ORDER BY `e`.`Naam`