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
import java.util.stream.Collectors;

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
    public String list(Model model, HttpSession session, @RequestParam(defaultValue = "0") int page, @ModelAttribute QueryForm queryForm, @RequestParam(name = "filters", required = false) List<String> filters) {
        if (model.getAttribute("queryForm") == null) {
            return "redirect:/book/search";
        }
        BookList books = (BookList) session.getAttribute("searchResult");
        queryForm = (QueryForm) session.getAttribute("queryForm");

        // フィルタリング機能
        List<Book> filteredBooks = filterBooksByLanguage(books.getBooks(), filters); // 言語でフィルタリング
        if (filters != null && !filters.isEmpty()) {
            filteredBooks = filteredBooks.stream()
                    .filter(book -> filters.contains(book.getLanguage()))
                    .collect(Collectors.toList());
        }

        int pageSize = 10;
        int totalPages = (int) Math.ceil((double) filteredBooks.size() / pageSize);

        if (filteredBooks.isEmpty()) {
            model.addAttribute("message", "該当する商品はありません");
            totalPages = 1;
        }
        int start = page * pageSize;
        int end = Math.min((page + 1) * pageSize, filteredBooks.size());
        List<Book> pageItems = filteredBooks.subList(start, end);
        Page<Book> bookPage = new PageImpl<>(pageItems, PageRequest.of(page, pageSize), books.getBooks().size());
        model.addAttribute("data", bookPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("queryForm", queryForm);
        model.addAttribute("selectedFilters", filters);
        model.addAttribute("filterParams", getFilterParams(filters));
        return "book/list";
    }

    private List<Book> filterBooksByLanguage(List<Book> books, List<String> filters) {
        if (filters == null || filters.isEmpty()) {
            return books; // フィルターが指定されていない場合は全ての本を返す
        }
        return books.stream()
                .filter(book -> filters.contains(book.getLanguage())) // 言語がフィルターに含まれている本のみを返す
                .collect(Collectors.toList());
    }
    private String getFilterParams(List<String> filters) {
        if (filters == null || filters.isEmpty()) {
            return ""; // フィルターが指定されていない場合は空文字列を返す
        }
        return "?filters=" + String.join("&filters=", filters); // フィルタリング情報をクエリパラメータ形式に変換
    }
}
