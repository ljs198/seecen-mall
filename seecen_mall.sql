/*
 Navicat Premium Dump SQL

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80023 (8.0.23)
 Source Host           : localhost:3306
 Source Schema         : seecen_mall

 Target Server Type    : MySQL
 Target Server Version : 80023 (8.0.23)
 File Encoding         : 65001

 Date: 08/09/2026 13:18:48
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_chat_message
-- ----------------------------
DROP TABLE IF EXISTS `ai_chat_message`;
CREATE TABLE `ai_chat_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `session_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_session`(`user_id` ASC, `session_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 96 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_chat_message
-- ----------------------------
INSERT INTO `ai_chat_message` VALUES (1, 1, 'e6719930-a3bb-4d70-84b9-c02b1b345f85', 'user', 'hello, what products do you have?', '2026-08-26 10:04:24');
INSERT INTO `ai_chat_message` VALUES (2, 1, 'e6719930-a3bb-4d70-84b9-c02b1b345f85', 'assistant', '抱歉，AI服务暂时不可用，请稍后再试。', '2026-08-26 10:04:25');
INSERT INTO `ai_chat_message` VALUES (3, 1, '47979cb2-e830-4cff-8aa4-af8bd55b4448', 'user', 'hello, what products do you have?', '2026-08-26 10:07:50');
INSERT INTO `ai_chat_message` VALUES (4, 1, '47979cb2-e830-4cff-8aa4-af8bd55b4448', 'assistant', '抱歉，AI服务暂时不可用，请稍后再试。', '2026-08-26 10:07:50');
INSERT INTO `ai_chat_message` VALUES (5, 1, '63480d06-6e11-452e-a26d-786ab6eeabfd', 'user', 'hello', '2026-08-26 10:10:24');
INSERT INTO `ai_chat_message` VALUES (6, 1, '63480d06-6e11-452e-a26d-786ab6eeabfd', 'assistant', 'Hello! 😊 How can I help you today? Whether you have questions about products, orders, or anything else—I\'m here to assist!', '2026-08-26 10:10:26');
INSERT INTO `ai_chat_message` VALUES (7, 1, '29b76979-f31d-4835-8867-53d176fc96f8', 'user', 'What products do you have?', '2026-08-26 10:10:36');
INSERT INTO `ai_chat_message` VALUES (8, 1, '29b76979-f31d-4835-8867-53d176fc96f8', 'assistant', 'I offer a variety of virtual goods — such as digital gift cards, software licenses, and online course access codes. To help you better, could you tell me what type of product you\'re interested in? For example: “Steam gift card”, “Adobe Creative Cloud license”, or “Coursera course code”. I can then check real-time availability, price, and stock for you! 😊', '2026-08-26 10:10:38');
INSERT INTO `ai_chat_message` VALUES (9, 1, '6cb30543-0666-4197-aa12-35b9253f556b', 'user', 'hello', '2026-08-26 10:13:49');
INSERT INTO `ai_chat_message` VALUES (10, 1, '6cb30543-0666-4197-aa12-35b9253f556b', 'assistant', 'Hello! 😊 How can I help you today? Whether you have questions about products, orders, or anything else—I\'m here to assist!', '2026-08-26 10:13:50');
INSERT INTO `ai_chat_message` VALUES (11, 2, '21489182-5e79-4926-a65c-85dc08cbc5e5', 'user', '你好', '2026-08-26 10:31:08');
INSERT INTO `ai_chat_message` VALUES (12, 2, '21489182-5e79-4926-a65c-85dc08cbc5e5', 'assistant', '你好！很高兴为您服务～😊  \n请问有什么可以帮您的？比如想了解某款商品，还是需要查询订单状态？', '2026-08-26 10:31:10');
INSERT INTO `ai_chat_message` VALUES (13, 2, 'c1ae07fe-650c-4906-aae7-3e5db84f98a2', 'user', '查询订单', '2026-08-26 10:33:00');
INSERT INTO `ai_chat_message` VALUES (14, 2, 'c1ae07fe-650c-4906-aae7-3e5db84f98a2', 'assistant', '您好！请问您的订单号是多少？我帮您查询订单状态。😊', '2026-08-26 10:33:01');
INSERT INTO `ai_chat_message` VALUES (15, 1, 'dce115c9-9186-4b25-b071-0433a13dcf56', 'user', 'hello', '2026-08-26 10:37:11');
INSERT INTO `ai_chat_message` VALUES (16, 1, 'dce115c9-9186-4b25-b071-0433a13dcf56', 'assistant', '你好！很高兴为你服务～😊  \n有什么我可以帮你的吗？比如查询商品信息、了解订单状态，或者其他问题？', '2026-08-26 10:37:12');
INSERT INTO `ai_chat_message` VALUES (17, 2, '13cb5449-1249-4e40-a92c-f2a0048d59bb', 'user', '查订单', '2026-08-26 10:38:24');
INSERT INTO `ai_chat_message` VALUES (18, 2, '13cb5449-1249-4e40-a92c-f2a0048d59bb', 'assistant', '您好！请问您的订单号是多少？我帮您查询订单状态。😊', '2026-08-26 10:38:25');
INSERT INTO `ai_chat_message` VALUES (19, 2, 'c1f5e72d-f7df-4156-a19f-4031d8e52d0c', 'user', '查询订单', '2026-08-26 10:38:46');
INSERT INTO `ai_chat_message` VALUES (20, 2, 'c1f5e72d-f7df-4156-a19f-4031d8e52d0c', 'assistant', '您好！请问您的订单号是多少？我来帮您查询订单状态。', '2026-08-26 10:38:47');
INSERT INTO `ai_chat_message` VALUES (21, 2, 'a7ec256f-6c5d-40e5-a728-97e6e41d81e0', 'user', '查询订单', '2026-08-26 10:41:19');
INSERT INTO `ai_chat_message` VALUES (22, 2, 'a7ec256f-6c5d-40e5-a728-97e6e41d81e0', 'assistant', '您好！请问您的订单号是多少？我帮您查询订单状态。😊', '2026-08-26 10:41:20');
INSERT INTO `ai_chat_message` VALUES (23, 1, '2c5c3976-9498-4f2f-907c-47f7bfc07c69', 'user', 'hello', '2026-08-26 10:43:50');
INSERT INTO `ai_chat_message` VALUES (24, 1, '2c5c3976-9498-4f2f-907c-47f7bfc07c69', 'assistant', '你好！很高兴为你服务～😊  \n有什么我可以帮你的吗？比如查询商品信息、了解订单状态，或者其他问题？', '2026-08-26 10:43:51');
INSERT INTO `ai_chat_message` VALUES (25, 1, '89b6e500-0af1-4f38-a623-f0430b13fc0c', 'user', 'hi', '2026-08-26 10:43:51');
INSERT INTO `ai_chat_message` VALUES (26, 1, '89b6e500-0af1-4f38-a623-f0430b13fc0c', 'assistant', '你好！很高兴为你服务～😊  \n有什么我可以帮你的吗？比如查询商品信息、了解订单状态，或者其他问题？', '2026-08-26 10:43:52');
INSERT INTO `ai_chat_message` VALUES (27, 1, '5967b63f-e777-4b9e-b463-d7716204bc07', 'user', 'hi', '2026-08-26 10:44:44');
INSERT INTO `ai_chat_message` VALUES (28, 1, '5967b63f-e777-4b9e-b463-d7716204bc07', 'assistant', '你好！很高兴为你服务～😊  \n有什么我可以帮你的吗？比如查询商品信息、了解订单状态，或者其他问题？', '2026-08-26 10:44:45');
INSERT INTO `ai_chat_message` VALUES (29, 2, '5b065c79-e0a4-437a-b93c-7a60d043d9b5', 'user', '查询订单', '2026-08-26 10:46:29');
INSERT INTO `ai_chat_message` VALUES (30, 2, '5b065c79-e0a4-437a-b93c-7a60d043d9b5', 'assistant', '您好！请问您的订单号是多少？我来帮您查询订单状态。', '2026-08-26 10:46:30');
INSERT INTO `ai_chat_message` VALUES (31, 2, '7257b402-11c5-408f-b272-6aa065ecdef7', 'user', 'java课程多少前', '2026-08-26 10:51:23');
INSERT INTO `ai_chat_message` VALUES (32, 2, '7257b402-11c5-408f-b272-6aa065ecdef7', 'assistant', '请问您指的是哪一门Java课程？可以提供课程名称或关键字吗？我来帮您查询价格和相关信息。', '2026-08-26 10:51:24');
INSERT INTO `ai_chat_message` VALUES (33, 2, '67b95185-30ac-4a57-a4de-50b5c21dee6b', 'user', 'java课程多少钱', '2026-08-26 10:52:46');
INSERT INTO `ai_chat_message` VALUES (34, 2, '67b95185-30ac-4a57-a4de-50b5c21dee6b', 'assistant', '抱歉，暂未找到名称中包含“java课程”的商品。您是否记错了名称？或者需要我帮您查找其他类型的课程（如编程入门、Python、前端等）？欢迎随时告诉我 😊', '2026-08-26 10:52:49');
INSERT INTO `ai_chat_message` VALUES (35, 2, '67b95185-30ac-4a57-a4de-50b5c21dee6b', 'user', 'Spring Boot 微服务开发 价格多少', '2026-08-26 10:53:09');
INSERT INTO `ai_chat_message` VALUES (36, 2, '67b95185-30ac-4a57-a4de-50b5c21dee6b', 'assistant', '“Spring Boot 微服务开发”课程价格为 **129.0 元**，目前库存充足（剩余 997 份），课程内容涵盖从零搭建 Spring Boot 微服务架构。需要我帮您下单或了解详情吗？😊', '2026-08-26 10:53:11');
INSERT INTO `ai_chat_message` VALUES (39, 2, 'c294c22a-a624-4817-9bf1-7ff9f2ea4c6d', 'user', 'nihao', '2026-08-26 11:00:30');
INSERT INTO `ai_chat_message` VALUES (40, 2, 'c294c22a-a624-4817-9bf1-7ff9f2ea4c6d', 'assistant', '你好！很高兴为您服务～😊  \n请问有什么可以帮您的？比如想了解某款商品，或者查询订单状态？', '2026-08-26 11:00:31');
INSERT INTO `ai_chat_message` VALUES (41, 2, 'c294c22a-a624-4817-9bf1-7ff9f2ea4c6d', 'user', '我买了什么东西', '2026-08-26 11:00:52');
INSERT INTO `ai_chat_message` VALUES (42, 2, 'c294c22a-a624-4817-9bf1-7ff9f2ea4c6d', 'assistant', '您好！要帮您查买了什么，我需要知道您的订单号哦～  \n方便您提供一下订单号吗？😊', '2026-08-26 11:00:53');
INSERT INTO `ai_chat_message` VALUES (43, 2, 'c294c22a-a624-4817-9bf1-7ff9f2ea4c6d', 'user', '查询我的所有订单', '2026-08-26 11:01:07');
INSERT INTO `ai_chat_message` VALUES (44, 2, 'c294c22a-a624-4817-9bf1-7ff9f2ea4c6d', 'assistant', '您好！目前我需要订单号才能查询具体订单信息（比如商品、状态等）。  \n如果您有多个订单，可以提供任意一个订单号，我马上帮您查～  \n或者您也可以告诉我大概的下单时间或商品名称，我帮您缩小范围！😊', '2026-08-26 11:01:09');
INSERT INTO `ai_chat_message` VALUES (45, 2, 'c294c22a-a624-4817-9bf1-7ff9f2ea4c6d', 'user', '20260825112958893997', '2026-08-26 11:01:41');
INSERT INTO `ai_chat_message` VALUES (46, 2, 'c294c22a-a624-4817-9bf1-7ff9f2ea4c6d', 'assistant', '您的订单已查询到：  \n✅ 订单号：20260825112958893997  \n✅ 状态：已支付  \n💰 金额：357.00 元  \n⏰ 创建 & 支付时间：2026-08-25 11:29:58  \n\n如需查看具体买了什么商品，欢迎告诉我商品名称或截图订单详情，我可进一步帮您确认～ 😊', '2026-08-26 11:01:45');
INSERT INTO `ai_chat_message` VALUES (47, 2, 'e92729f2-c75d-4705-8972-b603bfcab83b', 'user', '20260825112958893997', '2026-08-26 11:02:43');
INSERT INTO `ai_chat_message` VALUES (48, 2, 'e92729f2-c75d-4705-8972-b603bfcab83b', 'assistant', '您的订单 20260825112958893997 已成功支付，金额为 ¥357.00，支付时间为 2026-08-25 11:29:58。如有其他问题，欢迎随时咨询！😊', '2026-08-26 11:02:46');
INSERT INTO `ai_chat_message` VALUES (49, 2, 'e92729f2-c75d-4705-8972-b603bfcab83b', 'user', 'niha', '2026-08-26 11:22:05');
INSERT INTO `ai_chat_message` VALUES (50, 2, 'e92729f2-c75d-4705-8972-b603bfcab83b', 'assistant', '你好！很高兴为您服务～😊 请问有什么可以帮您的？', '2026-08-26 11:22:08');
INSERT INTO `ai_chat_message` VALUES (51, 2, '871856f0-83e0-4037-8dd2-fac173924aca', 'user', 'nihao', '2026-08-26 11:22:14');
INSERT INTO `ai_chat_message` VALUES (52, 2, '871856f0-83e0-4037-8dd2-fac173924aca', 'assistant', '你好！很高兴为您服务～😊  \n请问有什么可以帮您的？比如想了解某款商品，或查询订单状态？', '2026-08-26 11:22:15');
INSERT INTO `ai_chat_message` VALUES (53, 2, 'e92729f2-c75d-4705-8972-b603bfcab83b', 'user', 'niho', '2026-08-26 11:22:19');
INSERT INTO `ai_chat_message` VALUES (54, 2, 'e92729f2-c75d-4705-8972-b603bfcab83b', 'assistant', '你好！😊 请问有什么可以帮您的？', '2026-08-26 11:22:20');
INSERT INTO `ai_chat_message` VALUES (55, 2, 'e92729f2-c75d-4705-8972-b603bfcab83b', 'user', 'adf', '2026-08-26 11:23:25');
INSERT INTO `ai_chat_message` VALUES (56, 2, 'e92729f2-c75d-4705-8972-b603bfcab83b', 'assistant', '您好！看起来可能是输入有误～请问您需要咨询商品信息、订单状态，还是其他帮助？我很乐意为您解答！', '2026-08-26 11:23:26');
INSERT INTO `ai_chat_message` VALUES (57, 2, 'e92729f2-c75d-4705-8972-b603bfcab83b', 'user', 'aa', '2026-08-26 11:27:32');
INSERT INTO `ai_chat_message` VALUES (58, 2, 'e92729f2-c75d-4705-8972-b603bfcab83b', 'assistant', '您好！😊 请问您需要了解什么商品或订单信息？欢迎随时告诉我～', '2026-08-26 11:27:33');
INSERT INTO `ai_chat_message` VALUES (59, 2, 'e92729f2-c75d-4705-8972-b603bfcab83b', 'user', 'asdf', '2026-08-26 11:27:38');
INSERT INTO `ai_chat_message` VALUES (60, 2, 'e92729f2-c75d-4705-8972-b603bfcab83b', 'assistant', '您好！😊 看起来可能是误触输入～请问您想查询商品信息、订单状态，还是需要其他帮助？我随时为您服务！', '2026-08-26 11:27:39');
INSERT INTO `ai_chat_message` VALUES (61, 2, 'e92729f2-c75d-4705-8972-b603bfcab83b', 'user', 'adfasf', '2026-08-26 11:29:56');
INSERT INTO `ai_chat_message` VALUES (62, 2, 'e92729f2-c75d-4705-8972-b603bfcab83b', 'assistant', '您好！😊 似乎输入不太清晰～请问您需要查询某款商品（比如名字、价格、库存），还是想了解某个订单的状态？告诉我，我马上帮您查！', '2026-08-26 11:29:57');
INSERT INTO `ai_chat_message` VALUES (63, 2, 'd53bba34-ac01-4552-a73b-c3d2a7b28ed7', 'user', '你好', '2026-08-26 11:30:05');
INSERT INTO `ai_chat_message` VALUES (64, 2, 'd53bba34-ac01-4552-a73b-c3d2a7b28ed7', 'assistant', '你好！很高兴为您服务～😊  \n请问有什么可以帮您的？比如想了解某款商品，还是需要查询订单状态？', '2026-08-26 11:30:06');
INSERT INTO `ai_chat_message` VALUES (65, 2, '718f2333-e6e5-4914-95e4-0a2bd6764247', 'user', '你好', '2026-08-27 08:50:07');
INSERT INTO `ai_chat_message` VALUES (66, 2, '718f2333-e6e5-4914-95e4-0a2bd6764247', 'assistant', '你好！很高兴为您服务～😊  \n请问有什么可以帮您的？比如想了解某款商品，还是需要查询订单状态？', '2026-08-27 08:50:10');
INSERT INTO `ai_chat_message` VALUES (67, 2, '718f2333-e6e5-4914-95e4-0a2bd6764247', 'user', '查询我买过的商品', '2026-08-27 08:50:21');
INSERT INTO `ai_chat_message` VALUES (68, 2, '718f2333-e6e5-4914-95e4-0a2bd6764247', 'assistant', '您好！为了帮您查询购买过的商品，需要您提供订单号哦～  \n如果您记得订单号，可以直接告诉我；如果不确定，也可以查看下单时的短信、邮件或App订单列表。😊', '2026-08-27 08:50:22');
INSERT INTO `ai_chat_message` VALUES (69, 2, '718f2333-e6e5-4914-95e4-0a2bd6764247', 'user', '你是使用的什么模型', '2026-08-27 08:51:23');
INSERT INTO `ai_chat_message` VALUES (70, 2, '718f2333-e6e5-4914-95e4-0a2bd6764247', 'assistant', '我是通义千问（Qwen），由通义实验室研发的超大规模语言模型。我擅长回答问题、创作文字，比如写故事、写公文、写邮件、写剧本、逻辑推理、编程等等，还能表达观点，玩游戏等。如果您有任何问题或需要帮助，欢迎随时告诉我！😊', '2026-08-27 08:51:24');
INSERT INTO `ai_chat_message` VALUES (71, 2, '718f2333-e6e5-4914-95e4-0a2bd6764247', 'user', '查询下java类的商品价格', '2026-08-27 09:01:59');
INSERT INTO `ai_chat_message` VALUES (72, 2, '718f2333-e6e5-4914-95e4-0a2bd6764247', 'assistant', '抱歉，未找到名称中包含“java类”的商品。可能是关键词不匹配，或者该商品暂未上架～  \n您可以尝试提供更具体的商品名（如“Java编程入门”“Java开发实战”等），或告诉我您想了解的类别（如图书、课程、工具等），我来帮您进一步查找！😊', '2026-08-27 09:02:01');
INSERT INTO `ai_chat_message` VALUES (73, 2, '718f2333-e6e5-4914-95e4-0a2bd6764247', 'user', 'Java编程入门 商品详情', '2026-08-27 09:06:47');
INSERT INTO `ai_chat_message` VALUES (74, 2, '718f2333-e6e5-4914-95e4-0a2bd6764247', 'assistant', '抱歉，当前系统中暂未找到名称包含“Java编程入门”的商品。  \n可能是商品已下架、名称略有差异，或尚未录入系统。  \n您也可以提供更准确的全名、ISBN号，或截图商品页面，我来帮您进一步确认～ 😊', '2026-08-27 09:06:50');
INSERT INTO `ai_chat_message` VALUES (75, 2, '718f2333-e6e5-4914-95e4-0a2bd6764247', 'user', 'Spring Boot 微服务开发 商品详情', '2026-08-27 09:07:23');
INSERT INTO `ai_chat_message` VALUES (76, 2, '718f2333-e6e5-4914-95e4-0a2bd6764247', 'assistant', '已为您查到商品信息：\n\n✅ 商品名称：Spring Boot 微服务开发  \n💰 价格：129.0 元  \n📦 库存：997 件（充足）  \n📝 描述：从零搭建Spring Boot微服务架构  \n\n需要帮您下单、了解配套资源，或查询其他商品吗？😊', '2026-08-27 09:07:27');
INSERT INTO `ai_chat_message` VALUES (77, 2, '718f2333-e6e5-4914-95e4-0a2bd6764247', 'user', 'Spring Boot 微服务开发 商品详情', '2026-08-27 09:13:26');
INSERT INTO `ai_chat_message` VALUES (78, 2, '718f2333-e6e5-4914-95e4-0a2bd6764247', 'assistant', '✅ 商品名称：Spring Boot 微服务开发  \n💰 价格：129.0 元  \n📦 库存：997 件（充足）  \n📝 描述：从零搭建Spring Boot微服务架构  \n\n如需下单、了解配套源码/视频，或查询其他商品，欢迎随时告诉我～ 😊', '2026-08-27 09:13:31');
INSERT INTO `ai_chat_message` VALUES (79, 2, 'd53bba34-ac01-4552-a73b-c3d2a7b28ed7', 'user', 'hi', '2026-09-03 14:54:07');
INSERT INTO `ai_chat_message` VALUES (80, 2, '718f2333-e6e5-4914-95e4-0a2bd6764247', 'user', 'hi', '2026-09-03 15:23:34');
INSERT INTO `ai_chat_message` VALUES (81, 2, '718f2333-e6e5-4914-95e4-0a2bd6764247', 'assistant', 'Hi～很高兴再次为您服务！😊  \n有什么可以帮您的吗？比如查询商品、订单，或需要文件/数据库协助？', '2026-09-03 15:23:36');
INSERT INTO `ai_chat_message` VALUES (82, 2, '718f2333-e6e5-4914-95e4-0a2bd6764247', 'user', '看看我的订单', '2026-09-03 15:23:58');
INSERT INTO `ai_chat_message` VALUES (83, 2, '718f2333-e6e5-4914-95e4-0a2bd6764247', 'assistant', '您好！为了帮您查询订单，请提供您的订单号（通常为12位或16位数字字母组合，如：ORD2024XXXXXX）。  \n我将立即为您查询状态、金额和物流信息～ 📦✨', '2026-09-03 15:24:00');
INSERT INTO `ai_chat_message` VALUES (84, 2, '734c6d94-01bc-4396-8a0e-8a31fc7177df', 'user', '看下我的订单：20260903160506568761', '2026-09-03 16:08:45');
INSERT INTO `ai_chat_message` VALUES (85, 2, '734c6d94-01bc-4396-8a0e-8a31fc7177df', 'assistant', '正在为您查询订单状态，请稍候…\n\n您的订单 **20260903160506568761** 已成功支付 ✅  \n- 订单金额：¥258.00  \n- 创建时间：2026-09-03 16:05:07  \n- 支付时间：2026-09-03 16:05:07  \n\n如有其他问题，欢迎随时告诉我！😊', '2026-09-03 16:08:51');
INSERT INTO `ai_chat_message` VALUES (86, 2, '5fe457c4-52d4-4184-8cf6-cad2225efff7', 'user', '有什么课程？', '2026-09-06 21:35:37');
INSERT INTO `ai_chat_message` VALUES (87, 2, '5fe457c4-52d4-4184-8cf6-cad2225efff7', 'assistant', '您好！目前我主要负责虚拟商品咨询、订单查询、文件操作及数据库查询等服务。如果您想了解课程相关的信息，可能需要访问我们的学习平台或联系课程顾问哦～  \n\n如果您有具体需求，比如：  \n- 查看某门课程的介绍/价格/库存（请提供课程名称）  \n- 查询已购课程订单状态（请提供订单号）  \n- 查阅课程资料文件（如PDF、视频等）  \n- 查询课程数据库信息  \n\n欢迎随时告诉我，我很乐意帮您！😊', '2026-09-06 21:35:40');
INSERT INTO `ai_chat_message` VALUES (88, 2, '5fe457c4-52d4-4184-8cf6-cad2225efff7', 'user', '我最近的一个订单是什么', '2026-09-06 21:36:04');
INSERT INTO `ai_chat_message` VALUES (89, 2, '5fe457c4-52d4-4184-8cf6-cad2225efff7', 'assistant', '您好！为了帮您查询最近的订单状态，我需要您的订单号。  \n您可以在下单后收到的短信、邮件，或在账户订单列表中找到它（通常是一串数字或字母数字组合，如 `ORD2024123456`）。\n\n请提供订单号，我马上为您查询～ 😊', '2026-09-06 21:36:06');
INSERT INTO `ai_chat_message` VALUES (90, 2, '5fe457c4-52d4-4184-8cf6-cad2225efff7', 'user', '你好？', '2026-09-06 21:36:35');
INSERT INTO `ai_chat_message` VALUES (91, 2, '5fe457c4-52d4-4184-8cf6-cad2225efff7', 'assistant', '你好！😊 很高兴为您服务～  \n请问有什么可以帮您的？比如查询商品、订单状态，查看文件，或数据库信息等，我随时准备协助！', '2026-09-06 21:36:36');
INSERT INTO `ai_chat_message` VALUES (92, 2, '0e3caa3a-e0d6-4fbf-b729-e64f7361d22f', 'user', '你能做什么', '2026-09-07 09:28:00');
INSERT INTO `ai_chat_message` VALUES (93, 2, '0e3caa3a-e0d6-4fbf-b729-e64f7361d22f', 'assistant', '你好！😊 我是虚拟商品客服助手，可以帮你：\n\n✅ 查询商品信息（价格、库存、描述）——只需告诉我商品名称  \n✅ 查询订单状态（待支付/已支付/已取消、金额、时间）——请提供订单号  \n✅ 查看或操作文件（读取、写入、搜索、列出目录等）  \n✅ 查询数据库中的数据（如用户、订单、商品表等）\n\n需要我帮你做什么？比如：“查一下无线耳机的价格”、“订单号12345的状态”或“看看config.txt内容”～', '2026-09-07 09:28:03');
INSERT INTO `ai_chat_message` VALUES (94, 2, 'cdcfd23a-b9de-4c35-b4a9-6f9421ad2363', 'user', '有哪些商品', '2026-09-07 14:59:56');
INSERT INTO `ai_chat_message` VALUES (95, 2, 'cdcfd23a-b9de-4c35-b4a9-6f9421ad2363', 'assistant', '我需要查询当前可售商品列表。让我帮您获取商品信息：\n\n目前在售商品有：\n\n✅ Vue3 从入门到实战（¥99，库存996）  \n✅ Spring Boot 微服务开发（¥129，库存993）  \n✅ Photoshop CC 教程（¥59，库存997）  \n✅ Office 365 年度会员（¥198，库存500）  \n✅ VS Code Pro 插件包（¥29，库存999）\n\n需要了解某款商品的详情，或帮您下单？😊', '2026-09-07 15:00:00');

-- ----------------------------
-- Table structure for oms_cart_item
-- ----------------------------
DROP TABLE IF EXISTS `oms_cart_item`;
CREATE TABLE `oms_cart_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `quantity` int NOT NULL DEFAULT 1,
  `checked` tinyint NOT NULL DEFAULT 1,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of oms_cart_item
-- ----------------------------

-- ----------------------------
-- Table structure for oms_order
-- ----------------------------
DROP TABLE IF EXISTS `oms_order`;
CREATE TABLE `oms_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_id` bigint NOT NULL,
  `total_amount` decimal(10, 2) NOT NULL,
  `pay_status` tinyint NOT NULL DEFAULT 0 COMMENT '0待支付/1已支付/2已取消',
  `pay_time` datetime NULL DEFAULT NULL,
  `deleted` tinyint NOT NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of oms_order
-- ----------------------------
INSERT INTO `oms_order` VALUES (1, '20260825112958893997', 2, 357.00, 1, '2026-08-25 11:29:58', 0, '2026-08-25 11:29:58', '2026-08-25 11:29:58');
INSERT INTO `oms_order` VALUES (2, '20260903160506568761', 2, 258.00, 1, '2026-09-03 16:05:07', 0, '2026-09-03 16:05:07', '2026-09-03 16:05:07');

-- ----------------------------
-- Table structure for oms_order_item
-- ----------------------------
DROP TABLE IF EXISTS `oms_order_item`;
CREATE TABLE `oms_order_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `product_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `product_price` decimal(10, 2) NOT NULL,
  `quantity` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of oms_order_item
-- ----------------------------
INSERT INTO `oms_order_item` VALUES (1, 1, 1, 'Vue3 从入门到实战', 99.00, 1);
INSERT INTO `oms_order_item` VALUES (2, 1, 2, 'Spring Boot 微服务开发', 129.00, 2);
INSERT INTO `oms_order_item` VALUES (3, 2, 2, 'Spring Boot 微服务开发', 129.00, 2);

-- ----------------------------
-- Table structure for pms_category
-- ----------------------------
DROP TABLE IF EXISTS `pms_category`;
CREATE TABLE `pms_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `parent_id` bigint NOT NULL DEFAULT 0,
  `sort` int NOT NULL DEFAULT 0,
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `deleted` tinyint NOT NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of pms_category
-- ----------------------------
INSERT INTO `pms_category` VALUES (1, '数字课程', 0, 1, 'Reading', 0, '2026-08-24 16:20:22');
INSERT INTO `pms_category` VALUES (2, '软件工具', 0, 2, 'SetUp', 0, '2026-08-24 16:20:22');
INSERT INTO `pms_category` VALUES (3, '电子书', 0, 3, 'Notebook', 0, '2026-08-24 16:20:22');
INSERT INTO `pms_category` VALUES (4, '会员服务', 0, 4, 'StarFilled', 0, '2026-08-24 16:20:22');
INSERT INTO `pms_category` VALUES (5, '编程开发', 1, 1, 'Headset', 0, '2026-08-24 16:20:22');
INSERT INTO `pms_category` VALUES (6, '设计创意', 1, 2, 'Collection', 0, '2026-08-24 16:20:22');
INSERT INTO `pms_category` VALUES (7, '办公软件', 2, 1, 'Monitor', 0, '2026-08-24 16:20:22');
INSERT INTO `pms_category` VALUES (8, '开发工具', 2, 2, 'Trophy', 0, '2026-08-24 16:20:22');
INSERT INTO `pms_category` VALUES (9, '技术书籍', 3, 1, 'Collection', 0, '2026-08-24 16:20:22');
INSERT INTO `pms_category` VALUES (10, '小说文学', 3, 2, 'Notebook', 0, '2026-08-24 16:20:22');

-- ----------------------------
-- Table structure for pms_product
-- ----------------------------
DROP TABLE IF EXISTS `pms_product`;
CREATE TABLE `pms_product`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `category_id` bigint NULL DEFAULT NULL,
  `price` decimal(10, 2) NOT NULL,
  `original_price` decimal(10, 2) NULL DEFAULT NULL,
  `stock` int NOT NULL DEFAULT 0,
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `status` tinyint NOT NULL DEFAULT 1,
  `sales_count` int NOT NULL DEFAULT 0,
  `deleted` tinyint NOT NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of pms_product
-- ----------------------------
INSERT INTO `pms_product` VALUES (1, 'Vue3 从入门到实战', 5, 99.00, 199.00, 996, '/api/uploads/images/2026/09/02/product-1.svg', '全面讲解Vue3Composition API、Pinia、Vue Router等核心技术', '<h2>课程大纲</h2><p>1. Vue3基础</p><p>2. Composition API</p><p>3. Pinia状态管理</p><p>4. Vue Router</p><p>5. 项目实战</p>', 1, 131, 0, '2026-08-24 16:20:22', '2026-09-02 15:39:36');
INSERT INTO `pms_product` VALUES (2, 'Spring Boot 微服务开发', 5, 129.00, 259.00, 993, '/api/uploads/images/2026/09/02/product-2.svg', '从零搭建Spring Boot微服务架构', '<h2>课程内容</h2><p>Spring Boot核心</p><p>MyBatis Plus</p><p>Spring Security</p><p>微服务架构</p>', 1, 92, 0, '2026-08-24 16:20:22', '2026-09-02 15:38:35');
INSERT INTO `pms_product` VALUES (3, 'Photoshop CC 教程', 6, 59.00, 119.00, 997, '/api/uploads/images/2026/09/02/product-3.svg', 'PS入门到精通，涵盖海报设计、照片修图等', '<h2>课程内容</h2><p>基础工具</p><p>图层管理</p><p>蒙版与通道</p><p>实战案例</p>', 1, 258, 0, '2026-08-24 16:20:22', '2026-09-02 15:39:38');
INSERT INTO `pms_product` VALUES (4, 'Office 365 年度会员', 8, 198.00, 398.00, 500, '/api/uploads/images/2026/09/02/product-4.svg', '正版Office 365一年使用权，含Word/Excel/PPT', '<h2>会员权益</h2><p>正版授权</p><p>1TB OneDrive</p><p>多设备使用</p>', 1, 512, 0, '2026-08-24 16:20:22', '2026-09-02 15:39:39');
INSERT INTO `pms_product` VALUES (5, 'VS Code Pro 插件包', 9, 29.00, 49.00, 999, '/api/uploads/images/2026/09/02/product-5.svg', '精选20款高效VS Code插件', '<h2>插件列表</h2><p>代码格式化</p><p>Git增强</p><p>主题美化</p>', 1, 1024, 0, '2026-08-24 16:20:22', '2026-09-02 15:39:41');
INSERT INTO `pms_product` VALUES (6, 'Java编程思想（电子版）', 10, 39.00, 79.00, 998, '/api/uploads/images/2026/09/02/product-6.svg', '经典Java学习书籍电子版', '<h2>书籍介绍</h2><p>Java编程经典之作</p><p>涵盖Java核心技术</p>', 1, 731, 0, '2026-08-24 16:20:22', '2026-09-02 15:39:44');
INSERT INTO `pms_product` VALUES (7, 'AI智能客服系统源码', 5, 299.00, 599.00, 98, '/api/uploads/images/2026/09/02/product-7.svg', '完整的AI智能客服系统源码，含Spring AI集成', '<h2>项目内容</h2><p>Spring AI集成</p><p>Function Calling</p><p>SSE流式对话</p>', 1, 34, 0, '2026-08-24 16:20:22', '2026-09-02 15:39:48');
INSERT INTO `pms_product` VALUES (8, '网站VIP年卡', 4, 88.00, 188.00, 999, '/api/uploads/images/2026/09/02/product-8.svg', '全站资源免费下载一年', '<h2>VIP权益</h2><p>全站资源免费下载</p><p>专属客服</p><p>优先更新</p>', 1, 2048, 0, '2026-08-24 16:20:22', '2026-09-02 15:39:51');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'USER',
  `status` tinyint NOT NULL DEFAULT 1,
  `deleted` tinyint NOT NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$Dt9ARpfwM14a/9KHNHwlz.378JohUOgmyqoGUk7Eaw1L/qlXnpxZa', '系统管理员', NULL, 'ADMIN', 1, 0, '2026-08-24 16:20:22', '2026-09-07 09:47:32');
INSERT INTO `sys_user` VALUES (2, 'user', '$2a$10$EmsBciVAsoQpWVBYf6Skzu1kLKoauaJuD.CfLqAKJZftg4jKzq2Z2', '测试用户', NULL, 'USER', 1, 0, '2026-08-24 16:20:22', '2026-08-24 17:45:23');

SET FOREIGN_KEY_CHECKS = 1;
