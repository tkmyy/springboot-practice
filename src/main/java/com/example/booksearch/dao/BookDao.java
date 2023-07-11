package com.example.booksearch.dao;

import com.example.booksearch.domain.BookList;

public interface BookDao {
    BookList search(String query);
}
