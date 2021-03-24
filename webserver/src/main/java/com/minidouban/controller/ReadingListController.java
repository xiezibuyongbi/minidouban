package com.minidouban.controller;

import com.minidouban.service.ReadingListBookService;
import com.minidouban.service.ReadingListService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;

import static com.minidouban.controller.Utils.generateListMap;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Controller
public class ReadingListController {
    @Resource
    ReadingListService readingListService;

    @Resource
    ReadingListBookService readingListBookService;

    @GetMapping("/reading_list")
    public String readingList(Model model, HttpSession session,
                              String selectedListName) {
        Map<String, Long> readingListMap = generateListMap(session);
        model.addAttribute("readingListsName", readingListMap.keySet());
        if (!isEmpty(selectedListName)) {
            model.addAttribute("booksInList", readingListBookService.getBooksInList(readingListMap.get(selectedListName)));
        }
        return "reading_list";
    }

    @PostMapping("/rename-list")
    public String renameList(HttpSession session, String oldListName, String desiredListName) {
        long userId = (long) session.getAttribute("userId");
        if (!isEmpty(oldListName) && !isEmpty(desiredListName)) {
            readingListService.renameReadingList(userId, oldListName, desiredListName);
        }
        return "redirect:/reading_list";
    }

    @PostMapping("create-list")
    public String createList(HttpSession session, String listNameToCreate) {
        Map<String, Long> readingListMap = generateListMap(session);
        if (!isEmpty(listNameToCreate)) {
            long listId = readingListService.createReadingList((long) session.getAttribute("userId"), listNameToCreate);
            if (listId != 0) {
                readingListMap.put(listNameToCreate, listId);
                session.setAttribute("readingListMap", readingListMap);
            }
        }
        return "redirect:/reading_list";
    }

    @PostMapping("/delete-list")
    public String deleteList(HttpSession session, String listNameToDelete) {
        Map<String, Long> readingListMap = generateListMap(session);
        if (!isEmpty(listNameToDelete)) {
            if (readingListService.deleteReadingList((long) session.getAttribute("userId"), readingListMap.get(listNameToDelete)) != 0) {
                readingListMap.remove(listNameToDelete);
                session.setAttribute("readingListMap", readingListMap);
            }
        }
        return "redirect:/reading_list";
    }

    @PostMapping("/delete-all-list")
    public String deleteAllList(HttpSession session, String ifDeleteAllList) {
        Map<String, Long> readingListMap = generateListMap(session);
        if ("1".equals(ifDeleteAllList)) {
            readingListService.deleteAllReadingLists((long) session.getAttribute("userId"));
            readingListMap.clear();
            session.setAttribute("readingListMap", readingListMap);
        }
        return "redirect:/reading_list";
    }

    @PostMapping("/remove-book")
    public String removeBook(RedirectAttributes redirectAttributes, HttpSession session,
                             String listNameDeleteFrom, String[] bookIdToRemove) {
        if (isEmpty(listNameDeleteFrom)) {
            return "redirect:/reading_list";
        }
        long selectedListId = generateListMap(session).get(listNameDeleteFrom);
        if (bookIdToRemove != null) {
            for (String bookId : bookIdToRemove) {
                readingListBookService.removeBookFromList(selectedListId, Long.parseLong(bookId));
            }
        }
        redirectAttributes.addAttribute("selectedListName", listNameDeleteFrom);
        return "redirect:/reading_list";
    }

    @PostMapping("/add-book")
    public String addBookToList(HttpSession session, String from,
                                String[] bookIdToAdd, String selectedListName) {
        Map<String, Long> readingListMap = generateListMap(session);
        long listId = readingListMap.get(selectedListName);
        for (String bookId : bookIdToAdd) {
            readingListBookService.addBookToList(listId, Long.parseLong(bookId));
        }
        switch (from) {
            case "2":
                return "redirect:/advanced_search";
            case "1":
            default:
                return "redirect:/search";
        }
    }

}
