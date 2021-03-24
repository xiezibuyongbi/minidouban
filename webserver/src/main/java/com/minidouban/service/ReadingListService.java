package com.minidouban.service;

import com.minidouban.dao.ReadingListBookRepository;
import com.minidouban.dao.ReadingListRepository;
import com.minidouban.dao.UserRepository;
import com.minidouban.pojo.ReadingList;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


@Service
@Transactional
public class ReadingListService {
    @Resource
    private ReadingListRepository readingListRepository;

    @Resource
    private ReadingListBookRepository readingListBookRepository;

    @Resource
    private UserRepository userRepository;

    public List<ReadingList> getReadingListsOfUser(long userId) {
        if (!userRepository.existsById(userId)) {
            return new ArrayList<>();
        }
        return readingListRepository.findByUserId(userId);
    }

    public int renameReadingList(long userId, String oldListName, String desiredListName) {
        if (containsInvalidCharacter(desiredListName)) {
            return 0;
        }
        if (readingListRepository.findByUserIdAndListName(userId, oldListName) == null) {
            return 0;
        }
        if (readingListRepository.findByUserIdAndListName(userId, desiredListName) != null) {
            return 0;
        }
        return readingListRepository.updateListName(userId, oldListName, desiredListName);
    }

    public long createReadingList(long userId, String listName) {
        if (containsInvalidCharacter(listName)) {
            return 0;
        }
        if (!userRepository.existsById(userId)) {
            return 0;
        }
        if (readingListRepository.findByUserIdAndListName(userId, listName) != null) {
            return 0;
        }
        if (readingListRepository.insert(userId, listName) == 0) {
            return 0;
        }
        return readingListRepository.findByUserIdAndListName(userId, listName).getListId();
    }

    public int deleteReadingList(long userId, long listId) {
        if (readingListRepository.findByUserIdAndListId(userId, listId) == null) {
            return 0;
        }
        readingListBookRepository.deleteAllByListId(listId);
        return readingListRepository.deleteByUserIdAndListId(userId, listId);
    }

    public int deleteAllReadingLists(long userId) {
        if (!userRepository.existsById(userId)) {
            return 0;
        }
        getReadingListsOfUser(userId).forEach(list -> readingListBookRepository.deleteAllByListId(list.getListId()));
        return readingListRepository.deleteAllByUserId(userId);
    }

    private boolean containsInvalidCharacter(String str) {
        if (str == null) {
            return true;
        }
        str = str.trim();
        final String pattern = ".*[\\s~·`!！@#￥$%^……&*（()）\\-——\\-_=+【\\[\\]】｛{}｝\\|、\\\\；;：:‘'“”\"，,《<。.》>、/？?].*";
        if (Pattern.matches(pattern, str) || "".equals(str)) {
            return true;
        }
        return false;
    }
}
