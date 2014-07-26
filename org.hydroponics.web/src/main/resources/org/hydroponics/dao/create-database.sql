--
-- Table structure for database
--
DROP TABLE IF EXISTS `CALIBRE`;
CREATE TABLE `CALIBRE` (
  `CALIBRE_ID` int(11) NOT NULL AUTO_INCREMENT,
  `timestamp` datetime DEFAULT NULL,
  `temperature` int(2) NOT NULL,
  `humidity` int(2) NOT NULL,
  `current` int(4) NOT NULL,
  `moisture` int(4) NOT NULL,
  `GROW_ID` int(11) NOT NULL,
   PRIMARY KEY (`CALIBRE_ID`)
);

DROP TABLE IF EXISTS `FERTILIZER`;
CREATE TABLE `FERTILIZER` (
  `FERTILIZER_ID` int(11) NOT NULL AUTO_INCREMENT,
  `timestamp` datetime DEFAULT NULL,
  `fertilizer` int(11) NOT NULL,
  `GROW_ID` int(11) NOT NULL,
  PRIMARY KEY (`FERTILIZER_ID`)
);

DROP TABLE IF EXISTS `GROW`;
CREATE TABLE `GROW` (
  `GROW_ID` BIGINT NOT NULL AUTO_INCREMENT,
  `name` varchar(40) NOT NULL,
  `vegetation` datetime DEFAULT NULL,
  `flower` datetime DEFAULT NULL,
  `end` datetime DEFAULT NULL,
  `result` INT DEFAULT NULL,
  `plants` INT DEFAULT NULL,
  PRIMARY KEY (`GROW_ID`)
);

DROP TABLE IF EXISTS `IMAGE`;
CREATE TABLE `IMAGE` (
  `IMAGE_ID` int(11) NOT NULL AUTO_INCREMENT,
  `GROW_ID` int(11) NOT NULL,
  `timestamp` datetime DEFAULT NULL,
  `height` int(11) DEFAULT NULL,
  `width` int(11) DEFAULT NULL,
  `mimeType` varchar(30) DEFAULT NULL,
  `thumbnail` blob,
  `image` mediumblob,
  PRIMARY KEY (`IMAGE_ID`)
);

DROP TABLE IF EXISTS `SWITCH`;
CREATE TABLE `SWITCH` (
  `SWITCH_ID` int(11) NOT NULL,
  `name` varchar(40) NOT NULL,
  PRIMARY KEY (`SWITCH_ID`)
);
