# --- !Ups
CREATE TABLE `accounts` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'アカウントID',
  `name` varchar(255) NOT NULL COMMENT 'アカウント名',
  `display_name` varchar(255) NOT NULL COMMENT '表示名',
  `mail` varchar(255) DEFAULT NULL COMMENT 'メールアドレス',
  `password` text NOT NULL COMMENT 'パスワード',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='アカウント';

CREATE TABLE `tasks` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'タスクID',
  `account_id` int(11) NOT NULL COMMENT 'アカウントID',
  `name` varchar(255) DEFAULT NULL COMMENT '件名',
  `status` varchar(255) DEFAULT NULL COMMENT 'ステータス',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT fk_accounts_id
   FOREIGN KEY fk_account_id(account_id)
   REFERENCES accounts (id)
   ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='アカウント';

# --- !Downs
DROP TABLE `accounts`;
DROP TABLE `tasks`;

