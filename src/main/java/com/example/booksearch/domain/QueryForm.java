package com.example.booksearch.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class QueryForm {
    @NotBlank(message="キーワードを入力してください。")
    @Size(max=30, message="キーワードは30文字以内で入力してください")
    private String query;
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
