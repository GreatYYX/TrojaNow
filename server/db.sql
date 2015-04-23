SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;


CREATE TABLE IF NOT EXISTS `trojanow_follows` (
  `user_id` int(11) NOT NULL,
  `follow_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`,`follow_id`),
  KEY `user_id` (`user_id`),
  KEY `follow_id` (`follow_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `trojanow_statuses` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `user_id` int(10) NOT NULL,
  `content` text NOT NULL,
  `date` varchar(10) NOT NULL,
  `anonymous` tinyint(1) NOT NULL,
  `temperature` varchar(5) NOT NULL,
  `location` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `trojanow_users` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `user` varchar(10) NOT NULL,
  `password` varchar(40) NOT NULL,
  `nickname` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1;


ALTER TABLE `trojanow_follows`
  ADD CONSTRAINT `trojanow_follows_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `trojanow_users` (`id`),
  ADD CONSTRAINT `trojanow_follows_ibfk_3` FOREIGN KEY (`follow_id`) REFERENCES `trojanow_users` (`id`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
