package com.example.booksearch.dao;

import com.example.booksearch.domain.Book;
import com.example.booksearch.domain.BookList;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class BookDaoImpl implements BookDao, InitializingBean {
    private static final String GOOGLE_BOOKS_API = "https://www.googleapis.com/books/v1/volumes?q={query}&maxResults=40";

    private WebClient webClient;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        webClient = WebClient.create(GOOGLE_BOOKS_API);
    }

    @Override
    public BookList search(String query) {
        String url = GOOGLE_BOOKS_API;
        Mono<Map<String, Object>> responseMono = webClient.get()
                .uri(url, query)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                });

        Map<String, Object> responseMap = responseMono.block();

        // 'items'キーに対応する値を取得する
        List<Map<String, Object>> itemsList = (List<Map<String, Object>>) responseMap.get("items");

        // Bookオブジェクトのリストを作成
        List<Book> books = new ArrayList<>();
        for (Map<String, Object> itemMap : itemsList) {
            // 'volumeInfo'キーの内容を取得
            Map<String, Object> volumeInfoMap = (Map<String, Object>) itemMap.get("volumeInfo");

            // Bookオブジェクトを作成してリストに追加
            Book book = new Book();
            book.setTitle((String) volumeInfoMap.get("title"));
            book.setPublishedDate((String) volumeInfoMap.get("publishedDate"));
            book.setDescription((String) volumeInfoMap.get("description"));
            books.add(book);
        }

        // BookListオブジェクトを作成して返す
        BookList bookList = new BookList();
        bookList.setBooks(books);
        return bookList;
    }
}


