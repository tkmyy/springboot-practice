package com.example.booksearch.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookList {

    private List<Book> books;
    public List<Book> getBooks() { return books; };
    public void setBooks(List<Book> books) { this.books = books; }
}
