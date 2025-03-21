HOSTNAME="localhost"
PORT="3306"
USERNAME="test"
PASSWORD="test"

DBNAME="oneid-workbench"

mysql -h${HOSTNAME} -P${PORT} -u${USERNAME} -p${PASSWORD} -e "CREATE DATABASE IF NOT EXISTS ${DBNAME} DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
use ${DBNAME};
-- ----------------------------
-- Table structure for personal_api_permission
-- ----------------------------
CREATE TABLE personal_api_permission (
  id varchar(255) NOT NULL,
  name varchar(255) DEFAULT NULL,
  description varchar(255) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for personal_api_token
-- ----------------------------
CREATE TABLE personal_api_token (
  id varchar(255) NOT NULL,
  token varchar(255) NOT NULL,
  name varchar(255) NOT NULL,
  permission_ids varchar(255) NOT NULL,
  user_id varchar(255) NOT NULL,
  user_name varchar(255) NOT NULL,
  expire_at bigint NOT NULL,
  create_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP   ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE INDEX token_UNIQUE (token ASC) VISIBLE,
  INDEX user_id_UNIQUE (user_id ASC) VISIBLE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for personal_api_url
-- ----------------------------
CREATE TABLE personal_api_url (
  id varchar(255) NOT NULL,
  uri varchar(255) DEFAULT NULL,
  permission_id varchar(255) DEFAULT NULL,
  opt_permission varchar(255) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;"


echo "Database successfully created .... "
echo "Good bye!"
exit