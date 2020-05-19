CREATE DATABASE IF NOT EXISTS `TIW_ExamDB`;

USE `TIW_ExamDB`;

DROP TABLE IF EXISTS `quotation_option`;
DROP TABLE IF EXISTS `quotation`;
DROP TABLE IF EXISTS `option`;
DROP TABLE IF EXISTS `product`;
DROP TABLE IF EXISTS `client`;
DROP TABLE IF EXISTS `worker`;
DROP VIEW IF EXISTS `users`;

CREATE TABLE `client` (
  `idclient` int NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  `surname` varchar(45) NOT NULL,
  PRIMARY KEY (`idclient`),
  UNIQUE KEY `idclient_UNIQUE` (`idclient`),
  UNIQUE KEY `username_UNIQUE` (`username`)
);

CREATE TABLE `worker` (
  `idworker` int NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `surname` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idworker`),
  UNIQUE KEY `idworker_UNIQUE` (`idworker`),
  UNIQUE KEY `username_UNIQUE` (`username`)
);

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost`
SQL SECURITY DEFINER VIEW `tiw_examdb`.`users` AS
	select `tiw_examdb`.`worker`.`idworker` AS `userid`,`tiw_examdb`.`worker`.`username` AS `username`,`tiw_examdb`.`worker`.`password` AS `password`,`tiw_examdb`.`worker`.`name` AS `name`,`tiw_examdb`.`worker`.`surname` AS `surname`,'worker' AS `role`
    from `tiw_examdb`.`worker`
    union
    select `tiw_examdb`.`client`.`idclient` AS `userid`,`tiw_examdb`.`client`.`username` AS `username`,`tiw_examdb`.`client`.`password` AS `password`,`tiw_examdb`.`client`.`name` AS `name`,`tiw_examdb`.`client`.`surname` AS `surname`,'client' AS `role`
    from `tiw_examdb`.`client`;

CREATE TABLE `product` (
  `productId` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `img_path` varchar(80) NOT NULL,
  PRIMARY KEY (`productId`),
  UNIQUE KEY `productId_UNIQUE` (`productId`)
);

CREATE TABLE `quotation` (
  `quotationId` int NOT NULL AUTO_INCREMENT,
  `price` int,
  `date` date NOT NULL,
  `clientId` int NOT NULL,
  `workerId` int DEFAULT NULL,
  `productId` int NOT NULL,
  PRIMARY KEY (`quotationId`),
  UNIQUE KEY `quotationId_UNIQUE` (`quotationId`),
  FOREIGN KEY (`clientId`) REFERENCES `client` (`idclient`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`workerId`) REFERENCES `worker` (`idworker`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`productId`) REFERENCES `product` (`productId`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `option` (
  `optionId` int NOT NULL AUTO_INCREMENT,
  `productId` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `Stato` ENUM ("normale","in offerta") DEFAULT "normale",
  PRIMARY KEY (`optionId`,`productId`),
  UNIQUE KEY `optionId_UNIQUE` (`optionId`),
  FOREIGN KEY (`productId`) REFERENCES `product` (`productId`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `quotation_option` (
	`quotationId` int NOT NULL,
	`optionId` int NOT NULL, 
	PRIMARY KEY (`optionId`),
	FOREIGN KEY (`quotationId`) REFERENCES `quotation`(`quotationId`) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (`optionId`) REFERENCES `option`(`optionId`)  ON DELETE CASCADE ON UPDATE CASCADE
);


DROP TRIGGER IF EXISTS `TIW_ExamDB`.`insert_date_in_quotation`;

DELIMITER ;;
CREATE TRIGGER insert_date_in_quotation
BEFORE INSERT ON quotation
FOR EACH ROW
BEGIN
    IF new.date IS NULL THEN
        SET NEW.date = CURDATE();
    END IF;
END
;;

DROP TRIGGER IF EXISTS `TIW_ExamDB`.`no_double_options`;

DELIMITER ;;
CREATE TRIGGER no_double_options
BEFORE INSERT ON `option`
FOR EACH ROW
BEGIN
	if(EXISTS (SELECT name, productId
		FROM `option`
        WHERE name=new.name AND productId=new.productId)
		 ) then signal sqlstate '45000' set message_text = "Option still in the DB!";
	end if; 
END
;;

DROP TRIGGER IF EXISTS `TIW_ExamDB`.`no_double_options`;

DELIMITER ;;
CREATE TRIGGER no_unavailable_options
BEFORE INSERT ON `quotation_option`
FOR EACH ROW
BEGIN
	if((
			SELECT ProductId
            FROM `quotation`
            WHERE quotationId = new.quotationId
		)
        NOT IN
        (
			SELECT productId
            FROM `option`
            WHERE optionid = new.optionId
        )) then signal sqlstate '45000' set message_text = "Option not valid!";
	end if; 
END
;;