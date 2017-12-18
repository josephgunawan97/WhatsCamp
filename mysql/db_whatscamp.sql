-- phpMyAdmin SQL Dump
-- version 4.7.0
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Nov 14, 2017 at 11:42 AM
-- Server version: 5.6.34-log
-- PHP Version: 7.1.5

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_whatscamp`
--

-- --------------------------------------------------------

--
-- Table structure for table `tbl_whatscamp_attendees`
--

CREATE TABLE `tbl_whatscamp_attendees` (
  `attendee_id` int(11) NOT NULL,
  `event_id` int(11) NOT NULL,
  `is_going` int(11) NOT NULL DEFAULT '-1',
  `is_interested` int(11) NOT NULL DEFAULT '-1',
  `is_invited` int(11) NOT NULL DEFAULT '-1',
  `user_id` int(11) NOT NULL DEFAULT '-1',
  `updated_at` int(11) NOT NULL DEFAULT '0',
  `created_at` int(11) NOT NULL DEFAULT '0',
  `is_deleted` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `tbl_whatscamp_attendees`
--

INSERT INTO `tbl_whatscamp_attendees` (`attendee_id`, `event_id`, `is_going`, `is_interested`, `is_invited`, `user_id`, `updated_at`, `created_at`, `is_deleted`) VALUES
(1, 1, 1, -1, -1, 1, 1446963409, 1446963409, 0),
(2, 1, 1, -1, -1, 2, 1446963409, 1446963409, 0),
(3, 1, 1, -1, -1, 3, 1447584285, 1447584285, 0),
(4, 1, 1, -1, -1, 4, 1509123826, 1509123826, 0),
(5, 3, 1, -1, -1, 2, 1509674736, 1509674736, 0),
(6, 2, 1, -1, -1, 2, 1510329481, 1510329481, 0);

-- --------------------------------------------------------

--
-- Table structure for table `tbl_whatscamp_authentication`
--

CREATE TABLE `tbl_whatscamp_authentication` (
  `authentication_id` int(11) NOT NULL,
  `username` text NOT NULL,
  `password` text NOT NULL,
  `name` text NOT NULL,
  `role_id` int(11) NOT NULL,
  `is_deleted` int(11) NOT NULL,
  `deny_access` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `tbl_whatscamp_authentication`
--

INSERT INTO `tbl_whatscamp_authentication` (`authentication_id`, `username`, `password`, `name`, `role_id`, `is_deleted`, `deny_access`) VALUES
(1, 'admin', '21232f297a57a5a743894a0e4a801fc3', 'Admin', 1, 0, 0),
(2, 'guest', '25d55ad283aa400af464c76d713c07ad', 'Guest', 2, 0, 0);

-- --------------------------------------------------------

--
-- Table structure for table `tbl_whatscamp_categories`
--

CREATE TABLE `tbl_whatscamp_categories` (
  `category_id` int(11) NOT NULL,
  `category` text NOT NULL,
  `category_icon` text NOT NULL,
  `created_at` int(11) NOT NULL DEFAULT '0',
  `updated_at` int(11) NOT NULL DEFAULT '0',
  `is_deleted` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `tbl_whatscamp_categories`
--

INSERT INTO `tbl_whatscamp_categories` (`category_id`, `category`, `category_icon`, `created_at`, `updated_at`, `is_deleted`) VALUES
(1, 'Conference', '', 1446963409, 1446963409, 0),
(2, 'Music Festival', '', 1446963409, 1446963409, 0),
(3, 'Concert', '', 1448197622, 1448197639, 0);

-- --------------------------------------------------------

--
-- Table structure for table `tbl_whatscamp_events`
--

CREATE TABLE `tbl_whatscamp_events` (
  `event_id` int(11) NOT NULL,
  `address` text NOT NULL,
  `event_desc` text NOT NULL,
  `gmt_date_set` datetime NOT NULL,
  `is_ticket_available` int(11) NOT NULL DEFAULT '-1',
  `lat` text NOT NULL,
  `lon` text NOT NULL,
  `ticket_url` text NOT NULL,
  `email_address` text NOT NULL,
  `contact_no` text NOT NULL,
  `title` text NOT NULL,
  `user_id` int(11) NOT NULL DEFAULT '-1',
  `is_featured` int(11) NOT NULL DEFAULT '0',
  `photo_url` text NOT NULL,
  `created_at` int(11) NOT NULL DEFAULT '0',
  `updated_at` int(11) NOT NULL DEFAULT '0',
  `is_deleted` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `tbl_whatscamp_events`
--

INSERT INTO `tbl_whatscamp_events` (`event_id`, `address`, `event_desc`, `gmt_date_set`, `is_ticket_available`, `lat`, `lon`, `ticket_url`, `email_address`, `contact_no`, `title`, `user_id`, `is_featured`, `photo_url`, `created_at`, `updated_at`, `is_deleted`) VALUES
(1, 'Universitas Pelita Harapan', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\\n\\nSed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?\\n\\nBut I must explain to you how all this mistaken idea of denouncing pleasure and praising pain was born and I will give you a complete account of the system, and expound the actual teachings of the great explorer of the truth, the master-builder of human happiness. No one rejects, dislikes, or avoids pleasure itself, because it is pleasure, but because those who do not know how to pursue pleasure rationally encounter consequences that are extremely painful. Nor again is there anyone who loves or pursues or desires to obtain pain of itself, because it is pain, but because occasionally circumstances occur in which toil and pain can procure him some great pleasure. To take a trivial example, which of us ever undertakes laborious physical exercise, except to obtain some advantage from it? But who has any right to find fault with a man who chooses to enjoy a pleasure that has no annoying consequences, or one who avoids a pain that produces no resultant pleasure?', '2017-12-16 08:30:00', 1, '-6.228994999999999', '106.61164099999996', 'www.google.com', 'developer@conference.com', '+1234567890', 'iOS Developer Conference', 1, 1, 'http://asianjournal.com/news/files/2013/06/apple.jpg', 1446963409, 1509115560, 0),
(2, 'summarecon mall serpong', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.', '2017-12-31 16:00:00', 1, '-6.2412123', '106.62817240000004', 'www.coachella.com', 'coachella@gmail.com', '+1234567890', 'Coachella 2017', 1, 1, 'http://media.nbcsandiego.com/images/1200*809/Coachella+Art+2015+%287%29.jpg', 1448122385, 1509128247, 0),
(3, 'UPH', 'AA', '2017-11-04 01:00:58', 1, '-6.2285771', '106.6126561', 'x', 'jess@gmail.com', '081511030993', 'X Concert', 6, 0, '', 1509674582, 1509674679, 0),
(4, 'jfjeje', '', '2017-11-23 12:48:32', 0, '-6.2275686', '106.610158', '', 'x.@gmail.com', '04946865', 'ashsh', 2, 0, '', 1510206557, 1510206557, 0);

-- --------------------------------------------------------

--
-- Table structure for table `tbl_whatscamp_event_categories`
--

CREATE TABLE `tbl_whatscamp_event_categories` (
  `event_category_id` int(11) NOT NULL,
  `event_id` int(11) NOT NULL DEFAULT '0',
  `category_id` int(11) NOT NULL DEFAULT '0',
  `created_at` int(11) NOT NULL DEFAULT '0',
  `updated_at` int(11) NOT NULL DEFAULT '0',
  `is_deleted` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `tbl_whatscamp_event_categories`
--

INSERT INTO `tbl_whatscamp_event_categories` (`event_category_id`, `event_id`, `category_id`, `created_at`, `updated_at`, `is_deleted`) VALUES
(1, 1, 1, 1446963409, 1446963409, 0),
(2, 1, 2, 1446963409, 1446963409, 0),
(3, 2, 2, 1447668531, 1447668531, 0),
(4, 3, 2, 1509674582, 1509674582, 0),
(5, 3, 3, 1509674582, 1509674582, 0);

-- --------------------------------------------------------

--
-- Table structure for table `tbl_whatscamp_posts`
--

CREATE TABLE `tbl_whatscamp_posts` (
  `post_id` int(11) NOT NULL,
  `event_id` int(11) NOT NULL DEFAULT '-1',
  `post` text NOT NULL,
  `user_id` int(11) NOT NULL DEFAULT '-1',
  `created_at` int(11) NOT NULL DEFAULT '0',
  `updated_at` int(11) NOT NULL DEFAULT '0',
  `is_deleted` int(11) NOT NULL DEFAULT '0',
  `gmt_date_added` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `tbl_whatscamp_posts`
--

INSERT INTO `tbl_whatscamp_posts` (`post_id`, `event_id`, `post`, `user_id`, `created_at`, `updated_at`, `is_deleted`, `gmt_date_added`) VALUES
(1, 1, 'How to pay for the tickets?', 1, 1446963409, 1446963409, 0, '2015-11-17 08:14:00'),
(2, 1, 'Im going!', 2, 1446963409, 1446963409, 0, '2015-11-15 04:16:00'),
(3, 2, 'Can anyone confirm where I can buy the tickets?', 3, 1443980668, 1443980668, 0, '2015-11-19 09:38:00'),
(4, 1, 'Definitely going to this event.', 2, 1447920791, 1447920791, 0, '2015-11-19 08:13:11');

-- --------------------------------------------------------

--
-- Table structure for table `tbl_whatscamp_users`
--

CREATE TABLE `tbl_whatscamp_users` (
  `user_id` int(11) NOT NULL,
  `full_name` text NOT NULL,
  `login_hash` text NOT NULL,
  `facebook_id` text NOT NULL,
  `twitter_id` text NOT NULL,
  `google_id` text NOT NULL,
  `email` text NOT NULL,
  `deny_access` int(11) NOT NULL,
  `thumb_url` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `tbl_whatscamp_users`
--

INSERT INTO `tbl_whatscamp_users` (`user_id`, `full_name`, `login_hash`, `facebook_id`, `twitter_id`, `google_id`, `email`, `deny_access`, `thumb_url`) VALUES
(1, 'Megan Dee', 'jxEEcSkt2iWEJGOCZ/ShSxuo+rM3ODZkMjA5ZDc1', '261528977363938', '', '', 'mg.user001@gmail.com', 0, 'https://graph.facebook.com/261528977363938/picture?type=large'),
(2, 'Jonathan Moore', '8SX1w2qDgIA7pyshuLVT8/aDsjY4MzE4NWZmMDI2', '', '', '107545188502375828210', 'mg.user001@gmail.com', 0, 'http://img.faceyourmanga.com/mangatars/0/0/39/large_511.png'),
(3, 'John Doe', 'C/wZpX0U9rrQaCisLufUrsj79RpmMzEwMzhiYTg2', '', '2578207831', '', '', 0, 'https://trysomethingnewpimacounty.files.wordpress.com/2011/03/avatar-mike.jpg'),
(4, 'Joseph Gunawan', 'giB5LPCPHInYLfcIRcQ41PWAf2g4ZDAwYjkxNjlj', '10203935550507057', '', '', 'joseph_deangel@yahoo.com', 0, 'http://graph.facebook.com/10203935550507057/picture?type=large'),
(5, 'joseph gunawan', 'NOkRMawY3+gHyt9EOEgrx1SHmUo3MjE4ZDA4MjQ2', '', '', '111488435149021085139', 'joseph.gunawan97@gmail.com', 0, ''),
(6, 'Jessica Sean', 'qVHTH8dS8tFptYXc9BU75Skq6y5kYzRhYTNkYjBm', '', '', '104752873924124579084', 'jessicaseaan@gmail.com', 0, 'https://lh6.googleusercontent.com/-wl81TnJ-Nsc/AAAAAAAAAAI/AAAAAAAAA28/RvlDL_FWcbs/photo.jpg'),
(7, 'Joseph Gunawan', 'Jl7xO0j9HAUM4KdfeKE6qAZNgIo0OWQ1MDkxYTcx', '10203990501400795', '', '', 'joseph_deangel@yahoo.com', 0, 'http://graph.facebook.com/10203990501400795/picture?type=large');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `tbl_whatscamp_attendees`
--
ALTER TABLE `tbl_whatscamp_attendees`
  ADD PRIMARY KEY (`attendee_id`);

--
-- Indexes for table `tbl_whatscamp_authentication`
--
ALTER TABLE `tbl_whatscamp_authentication`
  ADD PRIMARY KEY (`authentication_id`);

--
-- Indexes for table `tbl_whatscamp_categories`
--
ALTER TABLE `tbl_whatscamp_categories`
  ADD PRIMARY KEY (`category_id`);

--
-- Indexes for table `tbl_whatscamp_events`
--
ALTER TABLE `tbl_whatscamp_events`
  ADD PRIMARY KEY (`event_id`);

--
-- Indexes for table `tbl_whatscamp_event_categories`
--
ALTER TABLE `tbl_whatscamp_event_categories`
  ADD PRIMARY KEY (`event_category_id`);

--
-- Indexes for table `tbl_whatscamp_posts`
--
ALTER TABLE `tbl_whatscamp_posts`
  ADD PRIMARY KEY (`post_id`);

--
-- Indexes for table `tbl_whatscamp_users`
--
ALTER TABLE `tbl_whatscamp_users`
  ADD PRIMARY KEY (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `tbl_whatscamp_attendees`
--
ALTER TABLE `tbl_whatscamp_attendees`
  MODIFY `attendee_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;
--
-- AUTO_INCREMENT for table `tbl_whatscamp_authentication`
--
ALTER TABLE `tbl_whatscamp_authentication`
  MODIFY `authentication_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT for table `tbl_whatscamp_categories`
--
ALTER TABLE `tbl_whatscamp_categories`
  MODIFY `category_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `tbl_whatscamp_events`
--
ALTER TABLE `tbl_whatscamp_events`
  MODIFY `event_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
--
-- AUTO_INCREMENT for table `tbl_whatscamp_event_categories`
--
ALTER TABLE `tbl_whatscamp_event_categories`
  MODIFY `event_category_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;
--
-- AUTO_INCREMENT for table `tbl_whatscamp_posts`
--
ALTER TABLE `tbl_whatscamp_posts`
  MODIFY `post_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
--
-- AUTO_INCREMENT for table `tbl_whatscamp_users`
--
ALTER TABLE `tbl_whatscamp_users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
