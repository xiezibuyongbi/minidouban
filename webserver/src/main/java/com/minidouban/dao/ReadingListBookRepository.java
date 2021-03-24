package com.minidouban.dao;

import com.minidouban.pojo.ReadingListBook;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadingListBookRepository {
    public ReadingListBook findByListIdAndBookId(@Param("listId") long listId, @Param("bookId") long bookId);

    public List<ReadingListBook> findByListId(@Param("listId") long listId);

    public int addBookToList(@Param("listId") long listId, @Param("bookId") long bookId);

    public int deleteAllByListId(@Param("listId") long listId);

    public int deleteByListIdAndBookId(@Param("listId") long listId, @Param("bookId") long bookId);
}
