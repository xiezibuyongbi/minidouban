package com.minidouban.controller;

import com.minidouban.configuration.Prompt;
import com.minidouban.pojo.Book;
import com.minidouban.pojo.BookPredicate;
import com.minidouban.pojo.Page;
import com.minidouban.pojo.PageInfo;
import com.minidouban.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;

import static com.minidouban.controller.Utils.generateListMap;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Controller
public class BookController {
    @Resource
    public BookService bookService;
    private static final int pageSize = 10;

    @GetMapping("/search")
    public String search(Model model, HttpSession session, String keyword, @RequestParam(value = "pageNum", required = false, defaultValue = "0") int pageNum) {
        String username = (String) session.getAttribute("username");
        if (!isEmpty(username)) {
            model.addAttribute("username", username);
            Map<String, Long> readingListMap = generateListMap(session);
            model.addAttribute("readingListsName", readingListMap.keySet());
        }
        if (isEmpty(keyword)) {
            return "search";
        }
        Page<Book> page = bookService.findByKeyword(keyword, PageInfo.of(pageNum, pageSize));
        if (page.isEmpty()) {
            model.addAttribute("msg", Prompt.noSearchResultPrompt);
            return "search";
        }
        model.addAttribute("books", page.getContent());
        return "search";
    }

    @GetMapping("/advanced_search")
    public String advancedSearch(Model model, HttpSession session,
                                 @RequestParam(value = "pageNum", required = false, defaultValue = "0") int pageNum) {
        String username = (String) session.getAttribute("username");
        if (!isEmpty(username)) {
            model.addAttribute("username", username);
            Map<String, Long> readingListMap = generateListMap(session);
            model.addAttribute("readingListsName", readingListMap.keySet());
        }
        BookPredicate bookPredicate = (BookPredicate) session.getAttribute("bookPredicate");
        if (bookPredicate != null) {
            Page<Book> bookSlice = bookService.findFuzzily(bookPredicate, PageInfo.of(pageNum, pageSize));
            model.addAttribute("books", bookSlice.getContent());
        }
        return "advanced_search";
    }

    @PostMapping("/advanced_search")
    public String advancedSearch(HttpSession session,
                                 BookPredicate bookPredicate) {
        session.setAttribute("bookPredicate", bookPredicate);
        return "redirect:/advanced_search";
    }
}
