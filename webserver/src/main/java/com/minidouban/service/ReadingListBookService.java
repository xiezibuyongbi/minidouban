package com.minidouban.service;

import com.minidouban.annotation.BusinessPrefix;
import com.minidouban.annotation.ExpireToken;
import com.minidouban.annotation.ItemId;
import com.minidouban.annotation.SendDelMsg;
import com.minidouban.dao.BookRepository;
import com.minidouban.dao.ReadingListBookRepository;
import com.minidouban.dao.ReadingListRepository;
import com.minidouban.pojo.Book;
import com.minidouban.pojo.ReadingListBook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@ExpireToken
public class ReadingListBookService {
    @BusinessPrefix
    private final String redisKeyPrefix = ReadingListBook.getTableName();

    @Resource
    private ReadingListBookRepository readingListBookRepository;

    @Resource
    private ReadingListRepository readingListRepository;

    @Resource
    private BookRepository bookRepository;

    @SendDelMsg
    public int addBookToList(@ItemId("listId") long listId, long bookId) {
        return readingListBookRepository.addBookToList(listId, bookId);
    }

    @Transactional
    @SendDelMsg
    public List<Book> getBooksInList(@ItemId("userId") long listId) {
        ArrayList<Book> books = new ArrayList<>();
        readingListBookRepository.findByListId(listId)
                .forEach(x -> books
                        .add(bookRepository.findByBookId(x.getBookId())));
        return books;
    }

    @SendDelMsg
    public int removeBookFromList(@ItemId("listId") long listId, long bookId) {
        return readingListBookRepository
                .deleteByListIdAndBookId(listId, bookId);
    }

    @Deprecated
    @SendDelMsg
    public int emptyReadingList(@ItemId("listId") long listId) {
        return readingListBookRepository.deleteAllByListId(listId);
    }
}
