/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50728
 Source Host           : localhost:3306
 Source Schema         : shiro_demo

 Target Server Type    : MySQL
 Target Server Version : 50728
 File Encoding         : 65001

 Date: 17/11/2019 22:39:34
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `role_name` varchar(255) NOT NULL,
  `permission` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of role_permission
-- ----------------------------
BEGIN;
INSERT INTO `role_permission` VALUES (1, 'user', 'user:read');
INSERT INTO `role_permission` VALUES (2, 'user', 'user_role:read');
INSERT INTO `role_permission` VALUES (3, 'user', 'role_permission:read');
INSERT INTO `role_permission` VALUES (4, 'admin', 'user:*');
INSERT INTO `role_permission` VALUES (5, 'admin', 'user_role:*');
INSERT INTO `role_permission` VALUES (6, 'admin', 'role_permission:*');
COMMIT;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `salt` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
BEGIN;
INSERT INTO `user` VALUES (2, 'admin', 'c4744b84e1c4543fb0e7af22dbc90139df01b71b824c4d53a0e7610f2f70f375', '22239cd5cfb44ef8872fb6231d94c07d');
INSERT INTO `user` VALUES (3, '111', 'daaf4b5abeed56ea2c027a133195b20da3b70c7f1e696369f7ef2d845e2efa83', '0cd1c8500f764b32bb8bbf1e610e6594');
INSERT INTO `user` VALUES (4, '123', 'ccfde4bcda94fda8efcbca23f7d62be17add4818036cc6aa3704da97aeed9ef8', '1a042a1764e94443898202585bba879b');
COMMIT;

-- ----------------------------
-- Table structure for user_permission
-- ----------------------------
DROP TABLE IF EXISTS `user_permission`;
CREATE TABLE `user_permission` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `permission` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `role_name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_role
-- ----------------------------
BEGIN;
INSERT INTO `user_role` VALUES (1, 'ling', 'user');
INSERT INTO `user_role` VALUES (2, 'admin', 'admin');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
