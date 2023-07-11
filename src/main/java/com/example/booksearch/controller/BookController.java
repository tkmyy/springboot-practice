package com.example.booksearch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/book")
public class BookController {

//    private final BookDao bookDao;

//    public BookController(BookDao bookDao) {
//        this.bookDao = bookDao;
//    }

    @GetMapping("/search")
    public String getSearchPage() {
        return "book/search";
    }

}
