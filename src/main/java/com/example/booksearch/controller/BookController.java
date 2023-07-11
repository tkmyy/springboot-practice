package com.example.booksearch.controller;

import com.example.booksearch.dao.BookDao;
import com.example.booksearch.domain.BookList;
import com.example.booksearch.domain.QueryForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String search(RedirectAttributes attributes, @ModelAttribute @Validated QueryForm queryForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "book/search";
        }
        BookList books = bookDao.search(queryForm.getQuery());
        attributes.addFlashAttribute("books", books.getBooks());
        attributes.addFlashAttribute("queryForm", queryForm);
        return "redirect:/book/list";
    }

    @GetMapping(path = "/list")
    public String list(Model model) {
        if (model.getAttribute("queryForm") == null) {
            return "redirect:/book/search";
        }
        return "book/list";
    }
}
