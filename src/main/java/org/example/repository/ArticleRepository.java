package org.example.repository;

import java.util.List;
import java.util.Optional;
import org.example.entity.Article;

public interface ArticleRepository {
    Article save(Article article);
    List<Article> findAll(long lastItemId, int pageSize);
    Optional<Article> findById(int i);
    void update(int i, String title, String content, String userId);
    void deleteById(int i);

    int getTotalPage(int pageSize);

    boolean hasNext(int pageSize);
}
