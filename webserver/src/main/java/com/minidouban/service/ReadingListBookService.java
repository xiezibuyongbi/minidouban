package com.minidouban.service;

import com.minidouban.dao.BookRepository;
import com.minidouban.dao.ReadingListBookRepository;
import com.minidouban.dao.ReadingListRepository;
import com.minidouban.pojo.Book;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ReadingListBookService {
    @Resource
    private ReadingListBookRepository readingListBookRepository;

    @Resource
    private ReadingListRepository readingListRepository;

    @Resource
    private BookRepository bookRepository;

    public int addBookToList(long listId, long bookId) {
        if (!readingListRepository.existsById(listId)) {
            return 0;
        }
        if (readingListBookRepository.findByListIdAndBookId(listId, bookId) != null) {
            return 0;
        }
        return readingListBookRepository.addBookToList(listId, bookId);
    }

    public List<Book> getBooksInList(long listId) {
        ArrayList<Book> books = new ArrayList<>();
        readingListBookRepository.findByListId(listId)
                .forEach(x -> books.add(bookRepository.findByBookId(x.getBookId())));
        return books;
    }

    public int removeBookFromList(long listId, long bookId) {
        return readingListBookRepository.deleteByListIdAndBookId(listId, bookId);
    }

    @Deprecated
    public int emptyReadingList(long listId) {
        return readingListBookRepository.deleteAllByListId(listId);
    }
}
