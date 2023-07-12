package com.example.booksearch.controller;

import com.example.booksearch.dao.BookDao;
import com.example.booksearch.domain.Book;
import com.example.booksearch.domain.BookList;
import com.example.booksearch.domain.QueryForm;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/book")
public class BookController {

    private final BookDao bookDao;

    public BookController(BookDao bookDao) {
        this.bookDao = bookDao;
    }

    @GetMapping(path = "/search")
    public String getSearchPage(@ModelAttribute QueryForm queryForm, Model model) {
        return "book/search";
    }

    @PostMapping(path = "/search")
    public String search(RedirectAttributes attributes, HttpSession session, @ModelAttribute @Validated QueryForm queryForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "book/search";
        }
        BookList books = bookDao.search(queryForm.getQuery());
        session.setAttribute("searchResult", books);
        session.setAttribute("queryForm", queryForm);
        attributes.addFlashAttribute("queryForm", queryForm);
        return "redirect:/book/list";
    }

    @GetMapping(path = "/list")
    public String list(Model model, HttpSession session, @RequestParam(defaultValue = "0") int page, @ModelAttribute QueryForm queryForm) {
        if (model.getAttribute("queryForm") == null) {
            return "redirect:/book/search";
        }
        BookList books = (BookList) session.getAttribute("searchResult");
        queryForm = (QueryForm) session.getAttribute("queryForm");
        int pageSize = 10;
        int totalPages = (int) Math.ceil((double) books.getBooks().size() / pageSize);
        if (books.getBooks().isEmpty()) {
            model.addAttribute("message", "該当する商品はありません");
            totalPages = 1;
        }
        int start = page * pageSize;
        int end = Math.min((page + 1) * pageSize, books.getBooks().size());
        List<Book> pageItems = books.getBooks().subList(start, end);
        Page<Book> bookPage = new PageImpl<>(pageItems, PageRequest.of(page, pageSize), books.getBooks().size());
        model.addAttribute("data", bookPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("queryForm", queryForm);
        return "book/list";
    }
}
