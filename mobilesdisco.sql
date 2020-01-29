/*
MySQL Data Transfer
Source Host: localhost
Source Database: mobilesdisco
Target Host: localhost
Target Database: mobilesdisco
Date: 5-3-2009 13:25:56
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for spaces
-- ----------------------------
CREATE TABLE `spaces` (
  `id` int(10) NOT NULL,
  `name` varchar(100) NOT NULL,
  `maxusers` int(4) NOT NULL,
  `decorid` int(2) NOT NULL,
  `doorx` int(4) NOT NULL,
  `doory` int(4) NOT NULL,
  `doorz` int(4) NOT NULL,
  `heightmap` text NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for users
-- ----------------------------
CREATE TABLE `users` (
  `name` varchar(15) NOT NULL,
  `password` varchar(15) NOT NULL,
  `email` varchar(50) NOT NULL,
  `age` int(3) NOT NULL,
  `pants` int(2) NOT NULL,
  `shirt` int(2) NOT NULL,
  `head` int(2) NOT NULL,
  `connection` varchar(10) NOT NULL,
  `sex` char(1) NOT NULL,
  `customdata` varchar(50) NOT NULL,
  `registered` char(10) NOT NULL,
  PRIMARY KEY  (`name`),
  UNIQUE KEY `username` (`name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records 
-- ----------------------------
INSERT INTO `spaces` VALUES ('1', 'main', '100', '0', '2', '0', '0', 'XX0xxxxxxxxxxxxx|00000000000000000|0000000000000000|0000000000000000|0000000000000000|0000000000000000|0000000000000000|0000000000000000|0000000000000000|0000000000000000|0000000000000000|0000000000000000|0000000000000000|0000000000000000|0000000000000000|0000000000000000|0000000000000000|0000000000000000|0000000000000000|0000000000000000|0000000000000000|0000000000000000|0000000000000000|0000000000000000|0000000000000000|0000000000000000');
INSERT INTO `spaces` VALUES ('2', 'dance', '100', '1', '4', '4', '4', 'X6666666665432100|X6666666665432100|X6600000000000X00|X6600000000000000|X6600000000000000|X6600000000000000|X660000000000X000|666000000000X1111|X66000000000XX111|X66000000000X1111|X66000000000X1111|X55000000000X1111|X44000000000X1111|X33000000000X1111|X22000000000XX111|X11X00000000X1111|X00000000000X1111|X00000000000XX111');
INSERT INTO `users` VALUES ('Nillus', 'test', 'you@domain.com', '99', '1', '1', '11', 'noidea', 'M', 'MOBIELS DISKO!', '09-02-2009');
INSERT INTO `users` VALUES ('Nillus2', 'lol', 'you@domain.com', '99', '3', '1', '11', 'isdn', 'M', 'MOBIELS DISKO!', '09-02-2009');
INSERT INTO `users` VALUES ('Guy', 'test', 'you@domain.com', '99', '3', '8', '12', 'fixed', 'M', 'I\'m a guy! Mobiles Disco!', '09-02-2009');
INSERT INTO `users` VALUES ('Girl', 'test', 'you@domain.com', '99', '6', '6', '7', 'noidea', 'F', 'I\'m a girl! Mobiles Disco!', '09-02-2009');
INSERT INTO `users` VALUES ('Jorren', 'test', 'jorn@timmy.com', '99', '2', '2', '17', 'fixed', 'M', 'Yeemauw!', '10-02-2009');
INSERT INTO `users` VALUES ('Rutger', 'test', 'rutger@rutgert.com', '12', '4', '2', '17', 'fixed', 'M', 'Pirates!', '10-02-2009');
INSERT INTO `users` VALUES ('Fleur', 'test', 'lol@wat.com', '99', '5', '5', '16', 'isdn', 'F', 'SHAMONE', '10-02-2009');
INSERT INTO `users` VALUES ('fruni', 'test', 'you@domain.com', '99', '3', '3', '11', 'noidea', 'M', 'this is visible for other users!', '11-02-2009');
INSERT INTO `users` VALUES ('NiggaTroll', 'test', 'you@domain.com', '99', '6', '6', '16', 'noidea', 'F', 'this is visible for other users!', '11-02-2009');
