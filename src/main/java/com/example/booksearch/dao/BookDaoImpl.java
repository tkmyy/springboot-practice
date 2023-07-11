package com.example.booksearch.dao;

import com.example.booksearch.domain.BookList;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BookDaoImpl implements BookDao, InitializingBean {

    private static final String GOOGLE_BOOKS_API = "https://www.googleapis.com/books/v1/volumes?q={query}&maxResults=40";
    private RestTemplate restTemplate;

    @Override
    public BookList search(String query) {
        ResponseEntity<BookList> response = restTemplate.getForEntity(GOOGLE_BOOKS_API, BookList.class, query);
        return response.getBody();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.restTemplate = new RestTemplate();
    }
}
