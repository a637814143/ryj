/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 50726
 Source Host           : localhost:3306
 Source Schema         : cs211180_20220109100628

 Target Server Type    : MySQL
 Target Server Version : 50726
 File Encoding         : 65001

 Date: 25/03/2022 16:11:53
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for access_token
-- ----------------------------
DROP TABLE IF EXISTS `access_token`;
CREATE TABLE `access_token`  (
  `token_id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '临时访问牌ID',
  `token` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '临时访问牌',
  `info` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `maxage` int(2) NOT NULL DEFAULT 2 COMMENT '最大寿命：默认2小时',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间：',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间：',
  `user_id` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '用户编号:',
  PRIMARY KEY (`token_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 107 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '临时访问牌' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of access_token
-- ----------------------------
INSERT INTO `access_token` VALUES (57, '5accf85cb6a7f06f0aa2968deadaec1b', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (58, '46ff1d4d07714f046ba07b34bffe0af9', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (59, 'ed9d6cba9826fda1beafcd9326be7a86', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (60, 'c99763c1833ea0785d9e2b81da3fd28f', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (61, '33fbfaccd6d1cb9143e4129bd919d4b0', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (62, '493e13da5f293ba67a56a0fe3e1fa6cf', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (63, 'c4b48e9e2160db09c703041a8fee0a1f', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (64, 'd13cdaefd3823c360c959a02a262f71d', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (65, '6c6ff426fd77ea5a2025ce5ed2e42c8a', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (66, '80930065a61ffcdd5cbb75f60932973c', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (67, '94114763cf2e3b020495d8a27096d4ef', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (68, '761052c551c97c9317bc3aa475c85b84', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (69, '7c44ef14131a0ba7c16aa16cef104065', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (70, '96380f3d9542c80d04bdade1cf7635a5', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (71, 'bfdc7acfcbf5763fda81945b60961222', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (72, '170a598e51ae8ae2badde20a42fe171d', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (73, 'c82c357488c75926a92d8a9608d4b367', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (74, '4d35290c023f407a820f37dbbb1ceb09', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (75, '8d19162776682b695c0f62f3c7a92fec', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (76, 'a7ea2cdc9a2be179e19200e593ad5a69', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (77, 'c79a554f9832adc01f19682c5d576bc4', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (78, '1c7d95001fa09951a679841c8100ad1f', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (79, '776da1bcdd01ddb3cbf0a37fa13fc5b0', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (80, 'd336e88e57c329d0166931292c1fac41', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (81, '37a40f526a6c82fc6110b512802d35bf', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (82, '691ad331771f4109206d58aeee572371', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (83, '9942e458886219960d3344b4a6a6fbec', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (84, 'e9939a8b7ccf9f548f0bbb5664981f96', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (85, 'f5b27912060d1909bef61fab9d96faae', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (86, '7c5888682f1d449eb1b62f0054a79fbf', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (87, '00dfdc6ac21c4a9da80fd71c990764d1', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (88, '3cce592bc72840ab932ce96d85a194da', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (89, '43fdaa989a644ad683ef4b4d488e8629', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (90, 'd6a3cecadacff0dbd6b43b25372cc2a2', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (91, '5570bc5b07b3589f4ef8553bd46eb0d1', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (92, '5570bc5b07b3589f4ef8553bd46eb0d1', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (93, '26c553bd2ee2ab6605d18dfd310d85f9', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (94, '3fd52f81236ed2c37ff91a6696d4e47a', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (95, '893332e9ee67d60d8312b3700c58a359', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (96, 'b7844068ade535b2e517df4a40948703', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (97, '179b37a5e1893c3af6b946bd5a1c8625', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (98, '3a47b8a040a83ebbc9194cb255dc668c', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (99, 'afa60196afb77dcc2b520ed13a817560', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (100, '7fc6d9b324f8c0a3a1784d04ef132692', NULL, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 1);
INSERT INTO `access_token` VALUES (101, '3939608dec2a477f81b637cc0221cc08', NULL, 2, '2022-01-20 17:12:11', '2022-01-20 17:12:11', 1);
INSERT INTO `access_token` VALUES (103, '13d64dfa14f34b81b856f7e46ffb5f3f', NULL, 2, '2022-01-20 17:15:42', '2022-01-20 17:15:42', 1);
INSERT INTO `access_token` VALUES (104, 'a30529f632d4458ea33b0407190c421e', NULL, 2, '2022-01-20 17:17:45', '2022-01-20 17:17:45', 3);
INSERT INTO `access_token` VALUES (105, 'b2d0a95097a04f55a5f9fa714916ad74', NULL, 2, '2022-01-20 17:21:24', '2022-01-20 17:21:24', 2);
INSERT INTO `access_token` VALUES (106, 'c0ecfc34ddb94511b6f70767a75b9b4a', NULL, 2, '2022-01-20 17:23:02', '2022-01-20 17:23:02', 2);

-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article`  (
  `article_id` mediumint(8) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '文章id：[0,8388607]',
  `title` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '标题：[0,125]用于文章和html的title标签中',
  `type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '文章分类：[0,1000]用来搜索指定类型的文章',
  `hits` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '点击数：[0,1000000000]访问这篇文章的人次',
  `praise_len` int(11) NOT NULL DEFAULT 0 COMMENT '点赞数',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间：',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间：',
  `source` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源：[0,255]文章的出处',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '来源地址：[0,255]用于跳转到发布该文章的网站',
  `tag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签：[0,255]用于标注文章所属相关内容，多个标签用空格隔开',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '正文：文章的主体内容',
  `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '封面图',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '文章描述',
  PRIMARY KEY (`article_id`, `title`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文章：用于内容管理系统的文章' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of article
-- ----------------------------
INSERT INTO `article` VALUES (1, '河南张村老艺人孙宝水把非遗泥塑送进滨州市实验学校课堂', '学校', 680, 0, '2022-01-20 09:06:38', '2022-01-20 09:06:38', NULL, NULL, NULL, '<p>3月10日，火把李庙会之前，惠民县河南张村的泥娃娃老艺人孙宝水到滨州市实验学校，向孩子们传授泥娃娃传统的上色技艺。</p><p class=\"ql-align-center\"><img src=\"http://n.sinaimg.cn/sinakd2021320s/290/w1080h810/20210320/e5af-kmrcukz2922169.png\"></p><p>滨州实验学校是滨州市开展泥娃娃教学最好的学校，专门设有非遗进课堂的教学项目，教授美术的孙波老师也是一位泥娃娃研究者。收集了全国各地的泥娃娃，为孩子们横向比较各种娃娃的差异。尤其是对惠民县的河南张泥娃娃具有独到的见解，走进非遗教室，展架上摆满了全国各地的泥娃娃，孙波老师在课堂上详细讲解河南张泥娃娃的制作过程，教授学生们制作模具、脱泥娃娃、上色等工艺。既传承了滨州市唯一的泥娃娃非遗项目，又拓展了教学内容，丰富了同学们的阅历，教学过程生动活泼。</p><p class=\"ql-align-center\"><img src=\"http://n.sinaimg.cn/sinakd2021320s/126/w1080h646/20210320/f48e-kmrcukz2922167.png\"></p><p class=\"ql-align-center\"><img src=\"http://n.sinaimg.cn/sinakd2021320s/12/w1080h532/20210320/127b-kmrcukz2922273.png\"></p><p class=\"ql-align-center\"><img src=\"http://n.sinaimg.cn/sinakd2021320s/165/w1080h685/20210320/16a5-kmrcukz2922274.png\"></p><p class=\"ql-align-center\"><img src=\"http://n.sinaimg.cn/sinakd2021320s/753/w1080h473/20210320/9e84-kmrcukz2922308.png\"></p><p class=\"ql-align-center\"><img src=\"http://n.sinaimg.cn/sinakd2021320s/769/w1080h489/20210320/4b50-kmrcukz2922310.png\"></p><p class=\"ql-align-center\"><img src=\"http://n.sinaimg.cn/sinakd2021320s/100/w1080h620/20210320/ddb1-kmrcukz2922347.png\"></p><p>河南张村的泥娃娃老艺人孙宝水也是一位制作传统泥娃娃的高手，他家里从祖辈开始就制作“小泥活”，作品包括“泥狗狗”、“泥猫咪”、小相公、弥勒佛等这些小泥活，完全是用红泥制作的，外型变化多端非常可爱，是河南张村保留很少的“小泥活”的发起之家。</p><p>孙宝水已经70多岁了，在非遗课堂上演示了用鸡蛋黄加鲜艳的传统颜料为泥娃娃上色的绝活。由于传统颜料是水性色，可以勾画出非常精细的线条，不像油漆那样拖不开笔，可以将非常小的娃娃刻画的很精致，增加了小娃娃的卖相。大杜村的杜翠，也是利用水性色的精细刻画优点，提高销售量的。7厘米高的“小相公”经过孙宝水大爷的刻画产生了画龙点睛的效果，观赏性骤然提高。</p><p>孙宝水不顾晕车的不适，忙了一上午，中午简单吃饭后，下午仍然坚持为孩子们授课，演示使用传统颜料上色，还辅导孩子们脱模。为了传授泥娃娃制作技术，孙宝水也是拼尽了全力。</p><p class=\"ql-align-center\"><img src=\"http://n.sinaimg.cn/sinakd2021320s/290/w1080h810/20210320/0d8a-kmrcukz2922348.png\"></p><p class=\"ql-align-center\"><img src=\"http://n.sinaimg.cn/sinakd2021320s/290/w1080h810/20210320/da36-kmrcukz2922412.png\"></p><p class=\"ql-align-center\"><img src=\"http://n.sinaimg.cn/sinakd2021320s/60/w1080h580/20210320/8bfe-kmrcukz2922405.png\"></p><p class=\"ql-align-center\"><img src=\"http://n.sinaimg.cn/sinakd2021320s/746/w1080h466/20210320/ed33-kmrcukz2922475.png\"></p><p class=\"ql-align-center\"><img src=\"http://n.sinaimg.cn/sinakd2021320s/722/w1080h442/20210320/1854-kmrcukz2922476.png\"></p><p class=\"ql-align-center\">这种黑色的娃娃是河南淮阳的“泥泥狗”，是一种古化石级的艺术形式，代表着一种原始符号</p><p class=\"ql-align-center\"><img src=\"http://n.sinaimg.cn/sinakd2021320s/188/w1080h708/20210320/5bac-kmrcukz2922538.png\"></p><p class=\"ql-align-center\">这个“画模”虽然只有核桃大小，却生动刻画出两只狮子相互撕咬的场面。</p><p class=\"ql-align-center\"><img src=\"http://n.sinaimg.cn/sinakd2021320s/427/w1018h1809/20210320/1baf-kmrcukz2922544.png\"></p><p class=\"ql-align-center\">双脸的陶娃。</p><p class=\"ql-align-center\"><img src=\"http://n.sinaimg.cn/sinakd2021320s/119/w1080h1439/20210320/b823-kmrcukz2922638.png\"></p><p class=\"ql-align-center\"><img src=\"http://n.sinaimg.cn/sinakd2021320s/119/w1080h1439/20210320/2035-kmrcukz2922636.png\"></p><p class=\"ql-align-center\">经过孙宝水上色的小娃娃。</p><p class=\"ql-align-center\"><img src=\"http://n.sinaimg.cn/sinakd2021320s/119/w1080h1439/20210320/d4da-kmrcukz2922735.png\"></p><p class=\"ql-align-center\">经孙宝水上色后的小泥活。</p><p class=\"ql-align-center\"><img src=\"http://n.sinaimg.cn/sinakd2021320s/490/w1080h1010/20210320/6614-kmrcukz2922730.png\"></p><p class=\"ql-align-center\">泥猫咪。</p><p class=\"ql-align-center\"><img src=\"http://n.sinaimg.cn/sinakd2021320s/312/w1080h832/20210320/f85d-kmrcukz2922798.png\"></p><p class=\"ql-align-center\"><img src=\"http://n.sinaimg.cn/sinakd2021320s/119/w1080h1439/20210320/5c3f-kmrcukz2922801.png\"></p><p class=\"ql-align-center\"><img src=\"http://n.sinaimg.cn/sinakd2021320s/290/w1080h810/20210320/b315-kmrcukz2922871.png\"><strong>杜增香（杜翠）的小娃娃。</strong></p><p><br></p>', NULL, '2022年01月14日 12:51 新浪网');
INSERT INTO `article` VALUES (2, '让爱与温暖照进孩子的心里 衡阳幼高专耒阳学院走进特殊学校', '学校', 522, 0, '2022-01-20 09:06:38', '2022-01-20 09:06:38', NULL, NULL, NULL, '<p>阳春三月万树青，正是致敬雷锋时。3月23日下午，阳光格外灿烂，衡阳幼高专耒阳学院的志愿者们怀揣着爱和关怀走进了耒阳市特殊学校。志愿者们的到来让整个学校顿时热闹了起来，灵动的舞姿，美妙的歌声，博得了孩子们的阵阵掌声，让这群孩子们感受到了春天的气息和生命的活力，拉进了彼此心灵的距离，整个校园变得特别温暖、有爱。活动最后，志愿者们将精心准备好的小礼物分发给了特殊学校的孩子们。</p><p><img src=\"http://n.sinaimg.cn/sinakd20210401ac/272/w640h432/20210401/ec44-knaqvqn9876034.jpg\"></p><p><img src=\"http://n.sinaimg.cn/sinakd20210401ac/272/w640h432/20210401/dde1-knaqvqn9876037.jpg\"></p><p><img src=\"http://n.sinaimg.cn/sinakd20210401ac/272/w640h432/20210401/4438-knaqvqn9876030.jpg\"></p>', NULL, '2022年01月14日 12:51 新浪网');
INSERT INTO `article` VALUES (3, '金溪仰山学校：课堂展风采 教研促成长（图）', '学校', 701, 0, '2022-01-20 09:06:38', '2022-01-20 09:06:38', NULL, NULL, NULL, '<p>大江网/抚州头条客户端讯 通讯员<strong>王谷平</strong>报道：为了提高教师课堂教学水平，追求课堂教学的有效性，打造高效课堂，近日，金溪县仰山学校开展了精彩的数学组教学研讨活动。</p><p class=\"ql-align-center\"><img src=\"http://n.sinaimg.cn/sinakd2021319s/33/w500h333/20210319/31f4-kmrcukz0380047.jpg\"><strong>精彩的课堂王谷平提供</strong></p><p>此次教研由该校五年级数学教师陈红执教《分数的产生和意义》。课堂上，学生通过动手操作分别找出了圆、正方形，线段等，教师自然而又巧妙地诠释了本节课重难点。陈红对教材把握精准，紧扣学科要素，课堂扎实有效，营造轻松的课堂氛围，特别是在小组建设上，培养了学生自主学习能力、合作能力及团队意识。</p><p>课后，全体数学老师对课堂进行了点评，既肯定了课堂展现的亮点，又对陈红的困惑发表了自己的见解。该校校长黄国胜充分肯定了陈红精湛的教学能力，丰富的教学经验及扎实的教学基本功，为老师们树立了榜样。</p><p class=\"ql-align-center\"><img src=\"http://n.sinaimg.cn/sinakd2021319s/33/w500h333/20210319/f138-kmrcukz0380048.jpg\"><strong>教师们在录播室听课王谷平提供</strong></p><p><br></p>', NULL, '2022年01月14日 12:51 新浪网');
INSERT INTO `article` VALUES (4, '枣庄市特教学校组织年轻教师课堂教学集中研讨活动', '学校', 655, 0, '2022-01-20 09:06:38', '2022-01-20 09:06:38', NULL, NULL, NULL, '<p>阳春三月，新的征程，新的起点。为促进年轻教师的不断成长，历练年轻教师的教学基本功，提高年轻教师的教学能力和水平，营造互相学习，共同进步的教学氛围，我校于2021年3月24日组织年轻教师集中研讨活动。</p><p>4位年轻教师们经过精心准备，为大家呈现了一节节目标明确、构思精巧、结构完整、方法灵活、形式多样的集中研讨教学活动。结对教师听课指导。</p><p>声部一年级教师李书豪《数11～20的数》</p><p><br></p><p><img src=\"http://n.sinaimg.cn/sinakd20210324ac/486/w640h2246/20210324/93d5-kmvwsvx6772078.jpg\"></p><p>声部一年级教师王子豪《早上好》</p><p><br></p><p><img src=\"http://n.sinaimg.cn/sinakd20210324ac/477/w640h2237/20210324/b4b4-kmvwsvx6772080.jpg\"></p><p>培智部四年级教师侯杰《敏捷训练》</p><p><br></p><p><img src=\"http://n.sinaimg.cn/sinakd20210324ac/619/w640h2379/20210324/eeba-kmvwsvx6772126.jpg\"></p><p>培智部四年级教师魏巍《交通安全很重要》</p><p><img src=\"http://n.sinaimg.cn/sinakd20210324ac/452/w640h2212/20210324/2e50-kmvwsvx6772081.jpg\"></p>', NULL, '2022年01月14日 12:51 新浪网');
INSERT INTO `article` VALUES (5, '【今日晋宁】联姻!区融媒体中心成为区中等专业学校实训基地', '学校', 846, 0, '2022-01-20 09:06:38', '2022-01-20 09:06:38', NULL, NULL, NULL, '<p>3月30日，晋宁区融媒体中心与晋宁区中等专业学校举行产教融合教学实训基地签约暨挂牌仪式。实训基地旨在充分发挥晋宁区融媒体中心及晋宁区中等专业学校双方资源优势，促进产学结合，提高办学水平和人才培养质量，增强学生的社会实践能力和实操能力。</p><p><img src=\"http://n.sinaimg.cn/sinakd20210401ac/200/w640h360/20210401/19cd-knaqvqn9804979.jpg\"></p><p><img src=\"http://n.sinaimg.cn/sinakd20210401ac/236/w640h396/20210401/7860-knaqvqn9804990.jpg\"></p><p>此次双方合作办学，是充分发挥区融媒体中心及晋宁区中等专业学校双方办学的资源优势，促进产学结合，实践与教学结合，提高办学水平和人才培养质量的有益尝试。</p><p>深化晋宁区融媒体中心与晋宁区中等专业学校的产学结合，一方面能够丰富教学形式，拓宽教学渠道，培养学生的综合素质和实际能力，使学生成为应用型人才，为毕业后服务社会奠定更加扎实的基础。另一方面，深化产学结合能够拓宽融媒体中心人才使用渠道，为融媒工作注入更多新鲜血液。双方充分利用各自优势条件，通过广泛交流合作，必将达到优势互补、资源共享的目的，最终实现双方共同进步。</p><p><img src=\"http://n.sinaimg.cn/sinakd20210401ac/166/w640h326/20210401/175a-knaqvqn9804988.jpg\"></p><p><img src=\"http://n.sinaimg.cn/sinakd20210401ac/190/w640h350/20210401/c34a-knaqvqn9804991.jpg\"></p>', NULL, '2022年01月14日 12:51 新浪网');
INSERT INTO `article` VALUES (6, '教智融合云端一体—星洋学校小学部“三大工程”课堂教学展示活动', '学校', 338, 0, '2022-01-20 09:06:38', '2022-01-20 09:06:38', NULL, NULL, NULL, '<p>为切实推进园区教育“教智融合深化年”的活动开展，充分发挥学校党员、骨干、行政的示范、先锋、领航作用。2021年3月16日～18日，苏州工业园区星洋学校小学部开展了主题为“教智融合，云端一体”的“三大工程”课堂教学展示活动。本次活动共有19位老师进行了课堂教学展示，涉及到语文、数学、英语、音乐、体育、美术、信息、劳技等学科。</p><p>活动中，来自语文学科的向日萍、高小梅、丁鲁笑老师分别执教了《肥皂泡》《杨氏之子》《骑鹅旅行记》。教学中，向老师重点指导学生学习抓关键词和想象画面的学习策略读懂课文，感受肥皂泡的美丽与可爱。高老师以读为基本路径，努力让学生在读中理解，读中感悟。丁老师紧扣单元语文要素，聚焦略读课文的阅读提示，巧借结构聚合图梳理主要情节。英语学科的吴骏老师执教的是《My day》，吴老师通过线上线下相结合的各种游戏，帮助学生高效巩固了教学重难点。又利用思维导图，帮助学生复述课文。周福娟老师执教的是五年级劳技《小鹿书签》一课，教学中通过看一看、拆一拆、试一试的方法让学生探究小鹿的穿编规律掌握穿编技巧。整堂课充实有趣，学生们乐在其中。景淑丽老师执教的是五年级美术《多彩的民族传统纹样》，她以传统纹样八角纹为切入口，从八角纹的文化寓意到各民族八角纹样式欣赏，再运用分割法设计现代感八角纹。陈春晖、戎光苏老师分别执教了音乐课《芦笛》《京调》。陈老师在教学中由浅入深，由简至繁，层层递进教唱扎实。戎老师的教学自信大方，学生课堂参与度很高，教学过程生动而高效。</p><p><img src=\"http://n.sinaimg.cn/sinakd20108/290/w1080h810/20210322/8694-kmrcukz7015960.png\"></p><p><img src=\"http://n.sinaimg.cn/sinakd20108/290/w1080h810/20210322/74d7-kmrcukz7016151.png\"></p><p><img src=\"http://n.sinaimg.cn/sinakd20108/290/w1080h810/20210322/ef7a-kmrcukz7016289.png\"></p><p><img src=\"http://n.sinaimg.cn/sinakd20108/200/w1080h720/20210322/29f2-kmrcukz7016367.png\"></p><p>本次展示活动中，数学老师参与的积极性最高，共展示了6节高质量的课堂教学。童晓花老师执教《分数的意义》，教学中关注技术与教学的融合创新。刘珊珊老师在《浸没问题》一课的教学中通过仿真实验、模型以及小组的讨论交流启发学生观察、发现水面以下物体体积与上升部分体积的关系，建立浸没问题的等量关系模型，并引导学生应用模型解决简单的问题。纪亚老师教学《圆柱、圆锥的整理与练习》一课时，注重引导学生正确有条理地通过思维导图进行整理，结合学生的描述同时借助媒体的直观演示，使学生对圆柱和圆锥的计算公式的推导过程与转化思想有了更为明晰的认识。王军老师执教的是《认识三角形》，教学中突出学生主体性，通过动手操作、合作交流，建构三角形的数学模型，促进学生数学核心素养的提升。夏小进老师在《用方向和距离确定位置》一课的教学中让大家感受到了执教者对数学、儿童以及教学的思考在不断深入，彰显了对学习的理解和把握。刘亚芳老师执教的是《认识长方形和正方形》，整个教学过程都以学生自主学习为主，学生通过量一量、折一折、比一比等方法了解长方形和正方形的特征。</p><p><img src=\"http://n.sinaimg.cn/sinakd20108/200/w1080h720/20210322/dbdc-kmrcukz7016394.png\"></p><p><img src=\"http://n.sinaimg.cn/sinakd20108/290/w1080h810/20210322/c458-kmrcukz7016426.png\"></p><p><img src=\"http://n.sinaimg.cn/sinakd20108/290/w1080h810/20210322/ebfa-kmrcukz7016529.jpg\"></p>', NULL, '2022年01月14日 12:51 新浪网');
INSERT INTO `article` VALUES (7, '金溪仰山学校：课堂展风采 教研促成长（图）', '学校', 224, 0, '2022-01-20 09:06:38', '2022-01-20 09:06:38', NULL, NULL, NULL, '<p>大江网/抚州头条客户端讯 通讯员<strong>王谷平</strong>报道：为了提高教师课堂教学水平，追求课堂教学的有效性，打造高效课堂，近日，金溪县仰山学校开展了精彩的数学组教学研讨活动。</p><p class=\"ql-align-center\"><img src=\"http://n.sinaimg.cn/sinakd2021319s/33/w500h333/20210319/31f4-kmrcukz0380047.jpg\"><strong>精彩的课堂王谷平提供</strong></p><p>此次教研由该校五年级数学教师陈红执教《分数的产生和意义》。课堂上，学生通过动手操作分别找出了圆、正方形，线段等，教师自然而又巧妙地诠释了本节课重难点。陈红对教材把握精准，紧扣学科要素，课堂扎实有效，营造轻松的课堂氛围，特别是在小组建设上，培养了学生自主学习能力、合作能力及团队意识。</p><p>课后，全体数学老师对课堂进行了点评，既肯定了课堂展现的亮点，又对陈红的困惑发表了自己的见解。该校校长黄国胜充分肯定了陈红精湛的教学能力，丰富的教学经验及扎实的教学基本功，为老师们树立了榜样。</p><p class=\"ql-align-center\"><img src=\"http://n.sinaimg.cn/sinakd2021319s/33/w500h333/20210319/f138-kmrcukz0380048.jpg\"><strong>教师们在录播室听课王谷平提供</strong></p><p><br></p>', NULL, '2022年01月14日 12:51 新浪网');
INSERT INTO `article` VALUES (8, '教智融合云端一体—星洋学校小学部“三大工程”课堂教学展示活动', '学校', 311, 0, '2022-01-20 09:06:38', '2022-01-20 09:06:38', NULL, NULL, NULL, '<p>为切实推进园区教育“教智融合深化年”的活动开展，充分发挥学校党员、骨干、行政的示范、先锋、领航作用。2021年3月16日～18日，苏州工业园区星洋学校小学部开展了主题为“教智融合，云端一体”的“三大工程”课堂教学展示活动。本次活动共有19位老师进行了课堂教学展示，涉及到语文、数学、英语、音乐、体育、美术、信息、劳技等学科。</p><p>活动中，来自语文学科的向日萍、高小梅、丁鲁笑老师分别执教了《肥皂泡》《杨氏之子》《骑鹅旅行记》。教学中，向老师重点指导学生学习抓关键词和想象画面的学习策略读懂课文，感受肥皂泡的美丽与可爱。高老师以读为基本路径，努力让学生在读中理解，读中感悟。丁老师紧扣单元语文要素，聚焦略读课文的阅读提示，巧借结构聚合图梳理主要情节。英语学科的吴骏老师执教的是《My day》，吴老师通过线上线下相结合的各种游戏，帮助学生高效巩固了教学重难点。又利用思维导图，帮助学生复述课文。周福娟老师执教的是五年级劳技《小鹿书签》一课，教学中通过看一看、拆一拆、试一试的方法让学生探究小鹿的穿编规律掌握穿编技巧。整堂课充实有趣，学生们乐在其中。景淑丽老师执教的是五年级美术《多彩的民族传统纹样》，她以传统纹样八角纹为切入口，从八角纹的文化寓意到各民族八角纹样式欣赏，再运用分割法设计现代感八角纹。陈春晖、戎光苏老师分别执教了音乐课《芦笛》《京调》。陈老师在教学中由浅入深，由简至繁，层层递进教唱扎实。戎老师的教学自信大方，学生课堂参与度很高，教学过程生动而高效。</p><p><img src=\"http://n.sinaimg.cn/sinakd20108/290/w1080h810/20210322/8694-kmrcukz7015960.png\"></p><p><img src=\"http://n.sinaimg.cn/sinakd20108/290/w1080h810/20210322/74d7-kmrcukz7016151.png\"></p><p><img src=\"http://n.sinaimg.cn/sinakd20108/290/w1080h810/20210322/ef7a-kmrcukz7016289.png\"></p><p><img src=\"http://n.sinaimg.cn/sinakd20108/200/w1080h720/20210322/29f2-kmrcukz7016367.png\"></p><p>本次展示活动中，数学老师参与的积极性最高，共展示了6节高质量的课堂教学。童晓花老师执教《分数的意义》，教学中关注技术与教学的融合创新。刘珊珊老师在《浸没问题》一课的教学中通过仿真实验、模型以及小组的讨论交流启发学生观察、发现水面以下物体体积与上升部分体积的关系，建立浸没问题的等量关系模型，并引导学生应用模型解决简单的问题。纪亚老师教学《圆柱、圆锥的整理与练习》一课时，注重引导学生正确有条理地通过思维导图进行整理，结合学生的描述同时借助媒体的直观演示，使学生对圆柱和圆锥的计算公式的推导过程与转化思想有了更为明晰的认识。王军老师执教的是《认识三角形》，教学中突出学生主体性，通过动手操作、合作交流，建构三角形的数学模型，促进学生数学核心素养的提升。夏小进老师在《用方向和距离确定位置》一课的教学中让大家感受到了执教者对数学、儿童以及教学的思考在不断深入，彰显了对学习的理解和把握。刘亚芳老师执教的是《认识长方形和正方形》，整个教学过程都以学生自主学习为主，学生通过量一量、折一折、比一比等方法了解长方形和正方形的特征。</p><p><img src=\"http://n.sinaimg.cn/sinakd20108/200/w1080h720/20210322/dbdc-kmrcukz7016394.png\"></p><p><img src=\"http://n.sinaimg.cn/sinakd20108/290/w1080h810/20210322/c458-kmrcukz7016426.png\"></p><p><img src=\"http://n.sinaimg.cn/sinakd20108/290/w1080h810/20210322/ebfa-kmrcukz7016529.jpg\"></p>', NULL, '2022年01月14日 12:51 新浪网');
INSERT INTO `article` VALUES (9, '阜南县新村镇中心学校观摩学习——感受口琴魅力，汇聚教学智慧', '学校', 830, 0, '2022-01-20 09:06:38', '2022-01-20 09:06:38', NULL, NULL, NULL, '<p><strong>运营总监：鲍安常LJ007</strong></p><p><strong>2021年4月1日整理</strong></p><p>为了让广大学生深刻感受口琴的魅力，更好地促进新村镇中心学校口琴进课堂工作走深做实。2021年3月31日上午，新村镇中心学校诚邀中国大众音乐协会口琴专业委员会会长白燕生一行深入新村镇中心小学课堂进行口琴教学指导，全镇中小学校长和音乐教师共同参加观摩学习。</p><p><img src=\"http://n.sinaimg.cn/sinakd20210401ac/150/w1000h750/20210401/7fd6-knaqvqn9800279.jpg\"></p><p>白会长在音乐教室里以生动有趣的语言开场，瞬间拉近了与孩子们的距离。形象巧妙地介绍了音名和唱名的相关知识，为学生们打开了音乐学习的大门，并结合自身学习经历向学生们分享了演奏口琴的技巧。同时针对互动交流中出现的问题进行专业纠正和耐心指导，鼓励学生们在日常学习中勤加练习，对长音和短音进行针对性训练，用心用情记住声音的特点，体验音乐变换带来的无穷乐趣。</p><p><img src=\"http://n.sinaimg.cn/sinakd20210401ac/150/w1000h750/20210401/d1ea-knaqvqn9800276.jpg\"></p><p>随后在座谈会上，全镇音乐教师针对教学实践和参赛演出等相关问题进行深入探讨交流，大家一致认为要丰富开展形式加强学科融合，通过自身不断地学习，夯实口琴基本功，以真抓实干的举措让口琴在学生心田立根发芽，达到悠悠琴声响彻校园莘莘学子幸福成长的良好效果。</p><p><img src=\"http://n.sinaimg.cn/sinakd20210401ac/150/w1000h750/20210401/1f53-knaqvqn9800274.jpg\"></p>', NULL, '2022年01月14日 12:51 新浪网');
INSERT INTO `article` VALUES (10, '枣庄市特教学校组织年轻教师课堂教学集中研讨活动', '学校', 67, 0, '2022-01-20 09:06:38', '2022-01-20 09:06:38', NULL, NULL, NULL, '<p>阳春三月，新的征程，新的起点。为促进年轻教师的不断成长，历练年轻教师的教学基本功，提高年轻教师的教学能力和水平，营造互相学习，共同进步的教学氛围，我校于2021年3月24日组织年轻教师集中研讨活动。</p><p>4位年轻教师们经过精心准备，为大家呈现了一节节目标明确、构思精巧、结构完整、方法灵活、形式多样的集中研讨教学活动。结对教师听课指导。</p><p>声部一年级教师李书豪《数11～20的数》</p><p><br></p><p><img src=\"http://n.sinaimg.cn/sinakd20210324ac/486/w640h2246/20210324/93d5-kmvwsvx6772078.jpg\"></p><p>声部一年级教师王子豪《早上好》</p><p><br></p><p><img src=\"http://n.sinaimg.cn/sinakd20210324ac/477/w640h2237/20210324/b4b4-kmvwsvx6772080.jpg\"></p><p>培智部四年级教师侯杰《敏捷训练》</p><p><br></p><p><img src=\"http://n.sinaimg.cn/sinakd20210324ac/619/w640h2379/20210324/eeba-kmvwsvx6772126.jpg\"></p><p>培智部四年级教师魏巍《交通安全很重要》</p><p><img src=\"http://n.sinaimg.cn/sinakd20210324ac/452/w640h2212/20210324/2e50-kmvwsvx6772081.jpg\"></p>', NULL, '2022年01月14日 12:51 新浪网');

-- ----------------------------
-- Table structure for article_type
-- ----------------------------
DROP TABLE IF EXISTS `article_type`;
CREATE TABLE `article_type`  (
  `type_id` smallint(5) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类ID：[0,10000]',
  `display` smallint(4) UNSIGNED NOT NULL DEFAULT 100 COMMENT '显示顺序：[0,1000]决定分类显示的先后顺序',
  `name` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '分类名称：[2,16]',
  `father_id` smallint(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT '上级分类ID：[0,32767]',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述：[0,255]描述该分类的作用',
  `icon` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '分类图标：',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '外链地址：[0,255]如果该分类是跳转到其他网站的情况下，就在该URL上设置',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间：',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间：',
  PRIMARY KEY (`type_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文章频道：用于汇总浏览文章，在不同频道下展示不同文章。' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of article_type
-- ----------------------------
INSERT INTO `article_type` VALUES (1, 100, '学校', 0, NULL, NULL, NULL, '2022-01-20 09:06:38', '2022-01-20 09:06:38');

-- ----------------------------

-- ----------------------------
-- Table structure for collect
-- ----------------------------
DROP TABLE IF EXISTS `collect`;
CREATE TABLE `collect`  (
  `collect_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '收藏ID：',
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '收藏人ID：',
  `source_table` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '来源表：',
  `source_field` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '来源字段：',
  `source_id` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '来源ID：',
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标题：',
  `img` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '封面：',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间：',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间：',
  PRIMARY KEY (`collect_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '收藏：' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of collect
-- ----------------------------

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `comment_id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '评论ID：',
  `user_id` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '评论人ID：',
  `reply_to_id` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '回复评论ID：空为0',
  `content` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '内容：',
  `nickname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '昵称：',
  `avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像地址：[0,255]',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间：',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间：',
  `source_table` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '来源表：',
  `source_field` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '来源字段：',
  `source_id` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '来源ID：',
  PRIMARY KEY (`comment_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '评论：' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comment
-- ----------------------------

-- ----------------------------
-- Table structure for contract_agreement
-- ----------------------------
DROP TABLE IF EXISTS `contract_agreement`;
CREATE TABLE `contract_agreement`  (
  `contract_agreement_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '合同协议ID',
  `enterprise_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '企业名称',
  `student` int(11) NULL DEFAULT 0 COMMENT '学生',
  `enterprise_industry` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '企业行业',
  `person_in_charge` int(11) NULL DEFAULT 0 COMMENT '负责人',
  `contract_agreement` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '合同协议',
  `examine_state` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '已通过' COMMENT '审核状态',
  `recommend` int(11) NOT NULL DEFAULT 0 COMMENT '智能推荐',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`contract_agreement_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '合同协议' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of contract_agreement
-- ----------------------------
INSERT INTO `contract_agreement` VALUES (1, '企业名称1', 0, '企业行业1', 0, '点此下载', '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `contract_agreement` VALUES (2, '企业名称2', 0, '企业行业2', 0, '点此下载', '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `contract_agreement` VALUES (3, '企业名称3', 0, '企业行业3', 0, '点此下载', '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `contract_agreement` VALUES (4, '企业名称4', 0, '企业行业4', 0, '点此下载', '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `contract_agreement` VALUES (5, '企业名称5', 0, '企业行业5', 0, '点此下载', '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `contract_agreement` VALUES (6, '企业名称6', 0, '企业行业6', 0, '点此下载', '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `contract_agreement` VALUES (7, '企业名称7', 0, '企业行业7', 0, '点此下载', '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `contract_agreement` VALUES (8, '企业名称8', 0, '企业行业8', 0, '点此下载', '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');

-- ----------------------------
-- Table structure for enterprise_information
-- ----------------------------
DROP TABLE IF EXISTS `enterprise_information`;
CREATE TABLE `enterprise_information`  (
  `enterprise_information_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '企业信息ID',
  `enterprise_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '企业名称',
  `business_address` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '企业地址',
  `enterprise_industry` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '企业行业',
  `company_profile` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '企业简介',
  `enterprise_picture` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '企业图片',
  `person_in_charge` int(11) NULL DEFAULT 0 COMMENT '负责人',
  `examine_state` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '已通过' COMMENT '审核状态',
  `recommend` int(11) NOT NULL DEFAULT 0 COMMENT '智能推荐',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`enterprise_information_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '企业信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of enterprise_information
-- ----------------------------
INSERT INTO `enterprise_information` VALUES (1, '企业名称1', '企业地址1', '企业行业1', '企业简介1', '点此播放', 0, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `enterprise_information` VALUES (2, '企业名称2', '企业地址2', '企业行业2', '企业简介2', '点此播放', 0, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `enterprise_information` VALUES (3, '企业名称3', '企业地址3', '企业行业3', '企业简介3', '点此播放', 0, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `enterprise_information` VALUES (4, '企业名称4', '企业地址4', '企业行业4', '企业简介4', '点此播放', 0, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `enterprise_information` VALUES (5, '企业名称5', '企业地址5', '企业行业5', '企业简介5', '点此播放', 0, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `enterprise_information` VALUES (6, '企业名称6', '企业地址6', '企业行业6', '企业简介6', '点此播放', 0, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `enterprise_information` VALUES (7, '企业名称7', '企业地址7', '企业行业7', '企业简介7', '点此播放', 0, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `enterprise_information` VALUES (8, '企业名称8', '企业地址8', '企业行业8', '企业简介8', '点此播放', 0, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');

-- ----------------------------
-- Table structure for enterprise_users
-- ----------------------------
DROP TABLE IF EXISTS `enterprise_users`;
CREATE TABLE `enterprise_users`  (
  `enterprise_users_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '企业用户ID',
  `gender` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '性别',
  `examine_state` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '已通过' COMMENT '审核状态',
  `recommend` int(11) NOT NULL DEFAULT 0 COMMENT '智能推荐',
  `user_id` int(11) NOT NULL DEFAULT 0 COMMENT '用户ID',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`enterprise_users_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '企业用户' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of enterprise_users
-- ----------------------------
INSERT INTO `enterprise_users` VALUES (1, '', '已通过', 0, 4, '2022-01-20 17:12:53', '2022-01-20 17:12:53');

-- ----------------------------
-- Table structure for industry_management
-- ----------------------------
DROP TABLE IF EXISTS `industry_management`;
CREATE TABLE `industry_management`  (
  `industry_management_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '行业管理ID',
  `industry_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '行业名称',
  `industry_number` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '行业编号',
  `industry_details` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '行业详情',
  `examine_state` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '已通过' COMMENT '审核状态',
  `recommend` int(11) NOT NULL DEFAULT 0 COMMENT '智能推荐',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`industry_management_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '行业管理' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of industry_management
-- ----------------------------
INSERT INTO `industry_management` VALUES (1, '行业名称1', '行业编号1', '行业详情1', '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `industry_management` VALUES (2, '行业名称2', '行业编号2', '行业详情2', '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `industry_management` VALUES (3, '行业名称3', '行业编号3', '行业详情3', '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `industry_management` VALUES (4, '行业名称4', '行业编号4', '行业详情4', '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `industry_management` VALUES (5, '行业名称5', '行业编号5', '行业详情5', '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `industry_management` VALUES (6, '行业名称6', '行业编号6', '行业详情6', '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `industry_management` VALUES (7, '行业名称7', '行业编号7', '行业详情7', '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `industry_management` VALUES (8, '行业名称8', '行业编号8', '行业详情8', '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');

-- ----------------------------
-- Table structure for interview_information
-- ----------------------------
DROP TABLE IF EXISTS `interview_information`;
CREATE TABLE `interview_information`  (
  `interview_information_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '面试信息ID',
  `student` int(11) NULL DEFAULT 0 COMMENT '学生',
  `enterprise_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '企业名称',
  `enterprise_industry` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '企业行业',
  `person_in_charge` int(11) NULL DEFAULT 0 COMMENT '负责人',
  `invitation_information` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '邀请信息',
  `examine_state` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '已通过' COMMENT '审核状态',
  `recommend` int(11) NOT NULL DEFAULT 0 COMMENT '智能推荐',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`interview_information_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '面试信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of interview_information
-- ----------------------------
INSERT INTO `interview_information` VALUES (1, 0, '企业名称1', '企业行业1', 0, '邀请信息1', '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `interview_information` VALUES (2, 0, '企业名称2', '企业行业2', 0, '邀请信息2', '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `interview_information` VALUES (3, 0, '企业名称3', '企业行业3', 0, '邀请信息3', '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `interview_information` VALUES (4, 0, '企业名称4', '企业行业4', 0, '邀请信息4', '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `interview_information` VALUES (5, 0, '企业名称5', '企业行业5', 0, '邀请信息5', '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `interview_information` VALUES (6, 0, '企业名称6', '企业行业6', 0, '邀请信息6', '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `interview_information` VALUES (7, 0, '企业名称7', '企业行业7', 0, '邀请信息7', '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `interview_information` VALUES (8, 0, '企业名称8', '企业行业8', 0, '邀请信息8', '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');

-- ----------------------------
-- Table structure for message_wall
-- ----------------------------
DROP TABLE IF EXISTS `message_wall`;
CREATE TABLE `message_wall`  (
  `message_wall_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '留言墙ID',
  `message_title` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '留言标题',
  `message_time` date NULL DEFAULT NULL COMMENT '留言时间',
  `message_person` int(11) NULL DEFAULT 0 COMMENT '留言人',
  `message_content` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '留言内容',
  `cover` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '封面',
  `examine_state` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '已通过' COMMENT '审核状态',
  `recommend` int(11) NOT NULL DEFAULT 0 COMMENT '智能推荐',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`message_wall_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '留言墙' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message_wall
-- ----------------------------
INSERT INTO `message_wall` VALUES (1, '留言标题1', '2022-01-20', 0, '留言内容1', '点此播放', '已通过', 0, '2022-01-20 17:06:37', '2022-01-20 17:06:37');
INSERT INTO `message_wall` VALUES (2, '留言标题2', '2022-01-20', 0, '留言内容2', '点此播放', '已通过', 0, '2022-01-20 17:06:37', '2022-01-20 17:06:37');
INSERT INTO `message_wall` VALUES (3, '留言标题3', '2022-01-20', 0, '留言内容3', '点此播放', '已通过', 0, '2022-01-20 17:06:37', '2022-01-20 17:06:37');
INSERT INTO `message_wall` VALUES (4, '留言标题4', '2022-01-20', 0, '留言内容4', '点此播放', '已通过', 0, '2022-01-20 17:06:37', '2022-01-20 17:06:37');
INSERT INTO `message_wall` VALUES (5, '留言标题5', '2022-01-20', 0, '留言内容5', '点此播放', '已通过', 0, '2022-01-20 17:06:37', '2022-01-20 17:06:37');
INSERT INTO `message_wall` VALUES (6, '留言标题6', '2022-01-20', 0, '留言内容6', '点此播放', '已通过', 0, '2022-01-20 17:06:37', '2022-01-20 17:06:37');
INSERT INTO `message_wall` VALUES (7, '留言标题7', '2022-01-20', 0, '留言内容7', '点此播放', '已通过', 0, '2022-01-20 17:06:37', '2022-01-20 17:06:37');
INSERT INTO `message_wall` VALUES (8, '留言标题8', '2022-01-20', 0, '留言内容8', '点此播放', '已通过', 0, '2022-01-20 17:06:37', '2022-01-20 17:06:37');

-- ----------------------------
-- Table structure for notice_announcement
-- ----------------------------
DROP TABLE IF EXISTS `notice_announcement`;
CREATE TABLE `notice_announcement`  (
  `notice_announcement_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '通知公告ID',
  `announcement_title` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公告标题',
  `announcement_no` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '公告编号',
  `release_time` date NULL DEFAULT NULL COMMENT '发布时间',
  `announcement_content` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '公告内容',
  `examine_state` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '已通过' COMMENT '审核状态',
  `recommend` int(11) NOT NULL DEFAULT 0 COMMENT '智能推荐',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`notice_announcement_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '通知公告' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notice_announcement
-- ----------------------------
INSERT INTO `notice_announcement` VALUES (1, '公告标题1', '公告编号1', '2022-01-20', '此处可上传文字、图片、视频、超链接、表格等内容区1', '已通过', 0, '2022-01-20 17:06:37', '2022-01-20 17:06:37');
INSERT INTO `notice_announcement` VALUES (2, '公告标题2', '公告编号2', '2022-01-20', '此处可上传文字、图片、视频、超链接、表格等内容区2', '已通过', 0, '2022-01-20 17:06:37', '2022-01-20 17:06:37');
INSERT INTO `notice_announcement` VALUES (3, '公告标题3', '公告编号3', '2022-01-20', '此处可上传文字、图片、视频、超链接、表格等内容区3', '已通过', 0, '2022-01-20 17:06:37', '2022-01-20 17:06:37');
INSERT INTO `notice_announcement` VALUES (4, '公告标题4', '公告编号4', '2022-01-20', '此处可上传文字、图片、视频、超链接、表格等内容区4', '已通过', 0, '2022-01-20 17:06:37', '2022-01-20 17:06:37');
INSERT INTO `notice_announcement` VALUES (5, '公告标题5', '公告编号5', '2022-01-20', '此处可上传文字、图片、视频、超链接、表格等内容区5', '已通过', 0, '2022-01-20 17:06:37', '2022-01-20 17:06:37');
INSERT INTO `notice_announcement` VALUES (6, '公告标题6', '公告编号6', '2022-01-20', '此处可上传文字、图片、视频、超链接、表格等内容区6', '已通过', 0, '2022-01-20 17:06:37', '2022-01-20 17:06:37');
INSERT INTO `notice_announcement` VALUES (7, '公告标题7', '公告编号7', '2022-01-20', '此处可上传文字、图片、视频、超链接、表格等内容区7', '已通过', 0, '2022-01-20 17:06:37', '2022-01-20 17:06:37');
INSERT INTO `notice_announcement` VALUES (8, '公告标题8', '公告编号8', '2022-01-20', '此处可上传文字、图片、视频、超链接、表格等内容区8', '已通过', 0, '2022-01-20 17:06:37', '2022-01-20 17:06:37');

-- ----------------------------
-- Table structure for praise
-- ----------------------------
DROP TABLE IF EXISTS `praise`;
CREATE TABLE `praise`  (
  `praise_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '点赞ID：',
  `user_id` int(11) UNSIGNED NOT NULL DEFAULT 0 COMMENT '点赞人：',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间：',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间：',
  `source_table` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '来源表：',
  `source_field` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '来源字段：',
  `source_id` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '来源ID：',
  `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '点赞状态:1为点赞，0已取消',
  PRIMARY KEY (`praise_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 222 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '点赞：' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of praise
-- ----------------------------
INSERT INTO `praise` VALUES (2, 1, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 'article', 'article_id', 7, 1);
INSERT INTO `praise` VALUES (25, 5, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 'article', 'article_id', 9, 1);
INSERT INTO `praise` VALUES (26, 5, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 'article', 'article_id', 7, 1);
INSERT INTO `praise` VALUES (27, 5, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 'article', 'article_id', 7, 1);
INSERT INTO `praise` VALUES (44, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 'forum', 'forum_id', 2, 1);
INSERT INTO `praise` VALUES (50, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 'forum', 'forum_id', 2, 1);
INSERT INTO `praise` VALUES (54, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 'article', 'article_id', 9, 1);
INSERT INTO `praise` VALUES (57, 0, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 'article', 'article_id', 10, 1);
INSERT INTO `praise` VALUES (86, 0, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 'article', 'article_id', 6, 1);
INSERT INTO `praise` VALUES (101, 7, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 'article', 'article_id', 7, 1);
INSERT INTO `praise` VALUES (108, 2, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 'article', 'article_id', 8, 1);
INSERT INTO `praise` VALUES (221, 0, '2022-01-14 07:32:09', '2022-01-14 07:32:09', 'article', 'article_id', 2, 1);

-- ----------------------------
-- Table structure for recruitment_information
-- ----------------------------
DROP TABLE IF EXISTS `recruitment_information`;
CREATE TABLE `recruitment_information`  (
  `recruitment_information_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '招聘信息ID',
  `enterprise_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '企业名称',
  `business_address` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '企业地址',
  `enterprise_industry` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '企业行业',
  `enterprise_picture` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '企业图片',
  `person_in_charge` int(11) NULL DEFAULT 0 COMMENT '负责人',
  `company_profile` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '企业简介',
  `professional_requirements` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '专业要求',
  `age_requirements` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '年龄要求',
  `character` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '性格',
  `educational_requirements` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '学历要求',
  `graduation_school_requirements` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '毕业学校要求',
  `recruitment_occupation` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '招聘职业',
  `certificate_requirements` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '证书要求',
  `occupational_category` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '职业类别',
  `type_of_work` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '工作类型',
  `working_province` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '工作省份',
  `working_years` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '工作年限',
  `a_monthly_salary` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '月薪',
  `is_it_wrapped` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否包住',
  `working_hours` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '工作时间',
  `compensation_and_benefits` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '薪酬福利',
  `job_description` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '职业描述',
  `hits` int(11) NOT NULL DEFAULT 0 COMMENT '点击数',
  `examine_state` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '已通过' COMMENT '审核状态',
  `recommend` int(11) NOT NULL DEFAULT 0 COMMENT '智能推荐',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`recruitment_information_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '招聘信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of recruitment_information
-- ----------------------------
INSERT INTO `recruitment_information` VALUES (1, '企业名称1', '企业地址1', '企业行业1', '点此播放', 0, '企业简介1', '专业要求1', '年龄要求1', '性格1', '学历要求1', '毕业学校要求1', '招聘职业1', '证书要求1', '职业类别1', '工作类型1', '工作省份1', '工作年限1', '月薪1', '是否包住1', '工作时间1', '薪酬福利1', '职业描述1', 276, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:21:28');
INSERT INTO `recruitment_information` VALUES (2, '企业名称2', '企业地址2', '企业行业2', '点此播放', 0, '企业简介2', '专业要求2', '年龄要求2', '性格2', '学历要求2', '毕业学校要求2', '招聘职业2', '证书要求2', '职业类别2', '工作类型2', '工作省份2', '工作年限2', '月薪2', '是否包住2', '工作时间2', '薪酬福利2', '职业描述2', 699, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `recruitment_information` VALUES (3, '企业名称3', '企业地址3', '企业行业3', '点此播放', 0, '企业简介3', '专业要求3', '年龄要求3', '性格3', '学历要求3', '毕业学校要求3', '招聘职业3', '证书要求3', '职业类别3', '工作类型3', '工作省份3', '工作年限3', '月薪3', '是否包住3', '工作时间3', '薪酬福利3', '职业描述3', 111, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `recruitment_information` VALUES (4, '企业名称4', '企业地址4', '企业行业4', '点此播放', 0, '企业简介4', '专业要求4', '年龄要求4', '性格4', '学历要求4', '毕业学校要求4', '招聘职业4', '证书要求4', '职业类别4', '工作类型4', '工作省份4', '工作年限4', '月薪4', '是否包住4', '工作时间4', '薪酬福利4', '职业描述4', 71, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `recruitment_information` VALUES (5, '企业名称5', '企业地址5', '企业行业5', '点此播放', 0, '企业简介5', '专业要求5', '年龄要求5', '性格5', '学历要求5', '毕业学校要求5', '招聘职业5', '证书要求5', '职业类别5', '工作类型5', '工作省份5', '工作年限5', '月薪5', '是否包住5', '工作时间5', '薪酬福利5', '职业描述5', 880, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `recruitment_information` VALUES (6, '企业名称6', '企业地址6', '企业行业6', '点此播放', 0, '企业简介6', '专业要求6', '年龄要求6', '性格6', '学历要求6', '毕业学校要求6', '招聘职业6', '证书要求6', '职业类别6', '工作类型6', '工作省份6', '工作年限6', '月薪6', '是否包住6', '工作时间6', '薪酬福利6', '职业描述6', 14, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `recruitment_information` VALUES (7, '企业名称7', '企业地址7', '企业行业7', '点此播放', 0, '企业简介7', '专业要求7', '年龄要求7', '性格7', '学历要求7', '毕业学校要求7', '招聘职业7', '证书要求7', '职业类别7', '工作类型7', '工作省份7', '工作年限7', '月薪7', '是否包住7', '工作时间7', '薪酬福利7', '职业描述7', 517, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `recruitment_information` VALUES (8, '企业名称8', '企业地址8', '企业行业8', '点此播放', 0, '企业简介8', '专业要求8', '年龄要求8', '性格8', '学历要求8', '毕业学校要求8', '招聘职业8', '证书要求8', '职业类别8', '工作类型8', '工作省份8', '工作年限8', '月薪8', '是否包住8', '工作时间8', '薪酬福利8', '职业描述8', 252, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');

-- ----------------------------
-- Table structure for resume_information
-- ----------------------------
DROP TABLE IF EXISTS `resume_information`;
CREATE TABLE `resume_information`  (
  `resume_information_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '简历信息ID',
  `enterprise_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '企业名称',
  `enterprise_industry` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '企业行业',
  `student` int(11) NULL DEFAULT 0 COMMENT '学生',
  `student_resume` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '学生简历',
  `person_in_charge` int(11) NULL DEFAULT 0 COMMENT '负责人',
  `examine_state` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '已通过' COMMENT '审核状态',
  `recommend` int(11) NOT NULL DEFAULT 0 COMMENT '智能推荐',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`resume_information_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '简历信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of resume_information
-- ----------------------------
INSERT INTO `resume_information` VALUES (1, '企业名称1', '企业行业1', 0, '点此下载', 0, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `resume_information` VALUES (2, '企业名称2', '企业行业2', 0, '点此下载', 0, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `resume_information` VALUES (3, '企业名称3', '企业行业3', 0, '点此下载', 0, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `resume_information` VALUES (4, '企业名称4', '企业行业4', 0, '点此下载', 0, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `resume_information` VALUES (5, '企业名称5', '企业行业5', 0, '点此下载', 0, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `resume_information` VALUES (6, '企业名称6', '企业行业6', 0, '点此下载', 0, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `resume_information` VALUES (7, '企业名称7', '企业行业7', 0, '点此下载', 0, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `resume_information` VALUES (8, '企业名称8', '企业行业8', 0, '点此下载', 0, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');

-- ----------------------------
-- Table structure for slides
-- ----------------------------
DROP TABLE IF EXISTS `slides`;
CREATE TABLE `slides`  (
  `slides_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '轮播图ID：',
  `title` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标题：',
  `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '内容：',
  `url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '链接：',
  `img` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '轮播图：',
  `hits` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '点击量：',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间：',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间：',
  PRIMARY KEY (`slides_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '轮播图：' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of slides
-- ----------------------------
INSERT INTO `slides` VALUES (1, '轮播图1', '内容1', '/article/details?article=1', '/api/upload/image_1615199295249.jpg', 246, '2022-01-20 09:06:38', '2022-01-20 09:06:38');
INSERT INTO `slides` VALUES (2, '轮播图2', '内容2', '/article/details?article=2', '/api/upload/image_1615199295267.jpg', 944, '2022-01-20 09:06:38', '2022-01-20 09:06:38');
INSERT INTO `slides` VALUES (3, '轮播图3', '内容3', '/article/details?article=3', '/api/upload/image_1615199295239.jpg', 588, '2022-01-20 09:06:38', '2022-01-20 09:06:38');
INSERT INTO `slides` VALUES (4, '轮播图4', '内容4', '/article/details?article=4', '/api/upload/image_1615199295282.jpg', 486, '2022-01-20 09:06:38', '2022-01-20 09:06:38');
INSERT INTO `slides` VALUES (5, '轮播图5', '内容5', '/article/details?article=5', '/api/upload/image_1615199295315.jpg', 581, '2022-01-20 09:06:38', '2022-01-20 09:06:38');
INSERT INTO `slides` VALUES (6, '轮播图6', '内容6', '/article/details?article=6', '/api/upload/image_1615199295249.jpg', 63, '2022-01-20 09:06:38', '2022-01-20 09:06:38');

-- ----------------------------
-- Table structure for student
-- ----------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student`  (
  `student_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '学生ID',
  `gender` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '性别',
  `personal_profile` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '个人简介',
  `full_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `certificate` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '证书',
  `age` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '年龄',
  `major` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '专业',
  `registered_residence` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '户籍地',
  `nation` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '民族',
  `education` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '学历',
  `graduation_school` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '毕业学校',
  `id_number` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '身份证号',
  `awards` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '获奖情况',
  `employment_intention` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '就业意向',
  `social_practice` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '社会实践',
  `examine_state` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '已通过' COMMENT '审核状态',
  `recommend` int(11) NOT NULL DEFAULT 0 COMMENT '智能推荐',
  `user_id` int(11) NOT NULL DEFAULT 0 COMMENT '用户ID',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`student_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '学生' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student
-- ----------------------------
INSERT INTO `student` VALUES (1, '', '', '', '', '', '', '', '', '', '', '', '', '', '', '已通过', 0, 2, '2022-01-20 17:12:30', '2022-01-20 17:12:30');

-- ----------------------------
-- Table structure for student_work_province
-- ----------------------------
DROP TABLE IF EXISTS `student_work_province`;
CREATE TABLE `student_work_province`  (
  `student_work_province_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '学生工作省份ID',
  `province_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '省份名称',
  `number_of_students` int(11) NULL DEFAULT 0 COMMENT '学生数量',
  `examine_state` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '已通过' COMMENT '审核状态',
  `recommend` int(11) NOT NULL DEFAULT 0 COMMENT '智能推荐',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`student_work_province_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '学生工作省份' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_work_province
-- ----------------------------
INSERT INTO `student_work_province` VALUES (1, '省份名称1', 5, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `student_work_province` VALUES (2, '省份名称2', 10, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `student_work_province` VALUES (3, '省份名称3', 15, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `student_work_province` VALUES (4, '省份名称4', 20, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `student_work_province` VALUES (5, '省份名称5', 25, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `student_work_province` VALUES (6, '省份名称6', 30, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `student_work_province` VALUES (7, '省份名称7', 35, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');
INSERT INTO `student_work_province` VALUES (8, '省份名称8', 40, '已通过', 0, '2022-01-20 17:06:38', '2022-01-20 17:06:38');

-- ----------------------------
-- Table structure for teacher
-- ----------------------------
DROP TABLE IF EXISTS `teacher`;
CREATE TABLE `teacher`  (
  `teacher_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '老师ID',
  `gender` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '性别',
  `examine_state` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '已通过' COMMENT '审核状态',
  `recommend` int(11) NOT NULL DEFAULT 0 COMMENT '智能推荐',
  `user_id` int(11) NOT NULL DEFAULT 0 COMMENT '用户ID',
  `create_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`teacher_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '老师' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of teacher
-- ----------------------------
INSERT INTO `teacher` VALUES (1, '', '已通过', 0, 3, '2022-01-20 17:12:41', '2022-01-20 17:12:41');

-- ----------------------------
-- Table structure for upload
-- ----------------------------
DROP TABLE IF EXISTS `upload`;
CREATE TABLE `upload`  (
  `upload_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '上传ID',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件名',
  `path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '访问路径',
  `file` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件路径',
  `display` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '显示顺序',
  `father_id` int(11) NULL DEFAULT 0 COMMENT '父级ID',
  `dir` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件夹',
  `type` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件类型',
  PRIMARY KEY (`upload_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of upload
-- ----------------------------
INSERT INTO `upload` VALUES (1, 'movie.mp4', '/upload/movie.mp4', '', NULL, 0, NULL, 'video');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` mediumint(8) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID：[0,8388607]用户获取其他与用户相关的数据',
  `state` smallint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '账户状态：[0,10](1可用|2异常|3已冻结|4已注销)',
  `user_group` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所在用户组：[0,32767]决定用户身份和权限',
  `login_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '上次登录时间：',
  `phone` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号码：[0,11]用户的手机号码，用于找回密码时或登录时',
  `phone_state` smallint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '手机认证：[0,1](0未认证|1审核中|2已认证)',
  `username` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '用户名：[0,16]用户登录时所用的账户名称',
  `nickname` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '昵称：[0,16]',
  `password` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '密码：[0,32]用户登录所需的密码，由6-16位数字或英文组成',
  `email` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '邮箱：[0,64]用户的邮箱，用于找回密码时或登录时',
  `email_state` smallint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '邮箱认证：[0,1](0未认证|1审核中|2已认证)',
  `avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像地址：[0,255]',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间：',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户账户：用于保存用户登录信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 1, '管理员', '2022-01-14 07:32:09', NULL, 0, 'admin', 'admin', 'bfd59291e825b5f2bbf1eb76569f8fe7', '', 0, '/api/upload/avatar.jpg', '2022-01-14 07:32:09');
INSERT INTO `user` VALUES (2, 1, '学生', '2022-01-20 17:12:30', '', 0, '112233', '学生', 'd0970714757783e6cf17b26fb8e2298f', '', 0, '', '2022-01-20 17:12:30');
INSERT INTO `user` VALUES (3, 1, '老师', '2022-01-20 17:12:41', '', 0, '332211', '老师', '71f55003c9a36b40c4a094908f11fb77', '', 0, '', '2022-01-20 17:12:41');
INSERT INTO `user` VALUES (4, 1, '企业用户', '2022-01-20 17:12:53', '', 0, '223311', '企业', 'e8063112f23f46de67c00ea437fe9eca', '', 0, '', '2022-01-20 17:12:53');

-- ----------------------------
-- Table structure for user_group
-- ----------------------------
DROP TABLE IF EXISTS `user_group`;
CREATE TABLE `user_group`  (
  `group_id` mediumint(8) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户组ID：[0,8388607]',
  `display` smallint(4) UNSIGNED NOT NULL DEFAULT 100 COMMENT '显示顺序：[0,1000]',
  `name` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '名称：[0,16]',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述：[0,255]描述该用户组的特点或权限范围',
  `source_table` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '来源表：',
  `source_field` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '来源字段：',
  `source_id` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '来源ID：',
  `register` smallint(1) UNSIGNED NULL DEFAULT 0 COMMENT '注册位置:',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间：',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间：',
  PRIMARY KEY (`group_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户组：用于用户前端身份和鉴权' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_group
-- ----------------------------
INSERT INTO `user_group` VALUES (1, 100, '游客', NULL, '', '', 0, 0, '2022-01-20 09:06:38', '2022-01-20 09:06:38');
INSERT INTO `user_group` VALUES (2, 100, '管理员', NULL, '', '', 0, NULL, '2022-01-20 09:06:38', '2022-01-20 09:06:38');
INSERT INTO `user_group` VALUES (3, 100, '学生', NULL, 'student', 'student_id', 0, 3, '2022-01-20 09:06:38', '2022-01-20 09:06:38');
INSERT INTO `user_group` VALUES (4, 100, '老师', NULL, 'teacher', 'teacher_id', 0, 1, '2022-01-20 09:06:38', '2022-01-20 09:06:38');
INSERT INTO `user_group` VALUES (5, 100, '企业用户', NULL, 'enterprise_users', 'enterprise_users_id', 0, 1, '2022-01-20 09:06:38', '2022-01-20 09:06:38');

SET FOREIGN_KEY_CHECKS = 1;
