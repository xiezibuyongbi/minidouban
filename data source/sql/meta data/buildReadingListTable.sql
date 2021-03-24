USE MiniDouban;
CREATE TABLE ReadingList (
  list_id INT(11) UNSIGNED AUTO_INCREMENT,
  list_name VARCHAR(32),
  user_id INT(11) UNSIGNED,
  UNIQUE (user_id, list_name),
  FOREIGN KEY (user_id) REFERENCES UserInfo (user_id),
  PRIMARY KEY (list_id)
);