package com.minidouban.dao;

import com.minidouban.pojo.Book;
import com.minidouban.pojo.BookPredicate;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface BookRepository {
    public Book findByBookId(@Param("bookId") long bookId);

    public List<Book> findFuzzily(Map<String, Object> paramMap);

    default public List<Book> findFuzzily(
            @Param("bookPredicate") BookPredicate bookPredicate,
            @Param("offset") int offset, @Param("pageSize") int pageSize) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("offset", offset);
        paramMap.put("pageSize", pageSize);
        for (Field field : bookPredicate.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                paramMap.put(field.getName(), field.get(bookPredicate));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        for (Field field : bookPredicate.getClass().getSuperclass()
                .getDeclaredFields()) {
            try {
                field.setAccessible(true);
                paramMap.put(field.getName(), field.get(bookPredicate));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return findFuzzily(paramMap);
    }

    public List<Book> findByKeyword(@Param("keyword") String keyword,
                                    @Param("offset") int offset,
                                    @Param("pageSize") int pageSize);
}
