USE MiniDouban;
CREATE TABLE Book (
  title VARCHAR(128) NOT NULL,
  book_id INT(11) UNSIGNED,
  cover_link VARCHAR(128),
  authors VARCHAR(192) NOT NULL,
  comment_num INT(11) UNSIGNED NOT NULL,
  brief TEXT,
  link VARCHAR(128) NOT NULL,
  -- possible null
  rating FLOAT (3, 1) UNSIGNED,
  -- quite possibly not null
  publisher VARCHAR(48),
  pub_year YEAR (4) NOT NULL,
  -- probably null
  price FLOAT(7, 3) UNSIGNED,
  PRIMARY KEY(book_id)
);