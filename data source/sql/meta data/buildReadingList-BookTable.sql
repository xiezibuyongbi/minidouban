USE MiniDouban;
CREATE TABLE ReadingList_Book (
  list_id INT(11) UNSIGNED,
  book_id INT(11) UNSIGNED,
  FOREIGN KEY (list_id) REFERENCES ReadingList (list_id),
  FOREIGN KEY (book_id) REFERENCES Book (book_id),
  PRIMARY KEY (list_id, book_id)
);