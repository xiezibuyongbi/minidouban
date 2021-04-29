package com.minidouban.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minidouban.component.JedisUtils;
import com.minidouban.dao.BookRepository;
import com.minidouban.pojo.Book;
import com.minidouban.pojo.BookPredicate;
import com.minidouban.pojo.Page;
import com.minidouban.pojo.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.regex.Pattern;

import static org.springframework.util.DigestUtils.md5DigestAsHex;

@Service
public class BookService {
    @Resource
    private BookRepository bookRepository;
    private static final Page<Book> dummy = null;
    @Resource
    private JedisUtils jedisUtils;
    private static final int resultExpireSeconds;
    private static final ObjectMapper objectMapper;

    static {
        resultExpireSeconds = 10 * 60;
        objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD,
                JsonAutoDetect.Visibility.ANY);
    }

    public Book findByBookId(long bookId) {
        return bookRepository.findByBookId(bookId);
    }

    @Transactional
    public Page<Book> findFuzzily(BookPredicate bookPredicate,
                                  PageInfo pageInfo) {
        String key = md5DigestAsHex(
                (bookPredicate.toString() + pageInfo.toString()).getBytes());
        String redisResult = jedisUtils.get(key);
        if (redisResult != null) {
            try {
                return objectMapper.readValue(redisResult,
                        new TypeReference<Page<Book>>() {
                        });
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        Page<Book> page = new Page<>(pageInfo, bookRepository
                .findFuzzily(bookPredicate, pageInfo.getOffset(),
                        pageInfo.getPageSize()));
        try {
            jedisUtils.setExpire(key, resultExpireSeconds,
                    objectMapper.writeValueAsString(page));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return page;
    }

    @Transactional
    public Page<Book> findByKeyword(String keyword, PageInfo pageInfo) {
        if (containsInvalidCharacter(keyword)) {
            return dummy;
        }
        String key = md5DigestAsHex((keyword + pageInfo.toString()).getBytes());
        String redisResult = jedisUtils.get(key);
        if (redisResult != null) {
            try {
                return objectMapper.readValue(redisResult,
                        new TypeReference<Page<Book>>() {
                        });
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        List<Book> queryResult = bookRepository
                .findByKeyword(keyword, pageInfo.getOffset(),
                        pageInfo.getPageSize());
        Page<Book> page = new Page<>(pageInfo, queryResult);
        try {
            jedisUtils.setExpire(key, resultExpireSeconds,
                    objectMapper.writeValueAsString(page));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return page;
    }

    private boolean containsInvalidCharacter(String str) {
        if (str == null) {
            return true;
        }
        str = str.trim();
        if ("".equals(str)) {
            return true;
        }
        final String pattern =
                ".*[\\s`@￥$^……*（()）\\_=\\[\\]｛{}｝\\|、\\\\；;‘'“”\"<>/].*";
        return Pattern.matches(pattern, str);
    }
}
