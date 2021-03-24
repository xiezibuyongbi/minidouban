package com.minidouban.controller;

import com.minidouban.pojo.ReadingList;
import com.minidouban.component.BeanManager;
import com.minidouban.service.ReadingListService;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {
    static ReadingListService readingListService = BeanManager.getBean(ReadingListService.class);

    private Utils() {
    }

    public static Map<String, Long> generateListMap(HttpSession session) {
        var ref = new Object() {
            Map<String, Long> readingListMap = null;
        };
        ref.readingListMap = (Map<String, Long>) session.getAttribute("readingListMap");
        if (ref.readingListMap == null) {
            List<ReadingList> readingLists = readingListService.getReadingListsOfUser((long) session.getAttribute("userId"));
            ref.readingListMap = new HashMap<>();
            readingLists.forEach(list -> ref.readingListMap.put(list.getListName(), list.getListId()));
            session.setAttribute("readingListMap", ref.readingListMap);
        }
        return ref.readingListMap;
    }
}
