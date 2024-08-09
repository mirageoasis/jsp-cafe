package org.example.repository;

import org.example.entity.Article;
import org.example.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.example.util.LoggerUtil;
import org.slf4j.Logger;

public class ArticleRepositoryDBImpl implements ArticleRepository {
    private static ArticleRepository instance;
    private final Logger logger = LoggerUtil.getLogger();


    private ArticleRepositoryDBImpl() {
        // Private constructor to prevent instantiation
    }

    public static ArticleRepository getInstance() {
        if (instance == null) {
            instance = new ArticleRepositoryDBImpl();
        }
        return instance;
    }

    @Override
    public Article save(Article article) {
        String sql = "INSERT INTO articles (title, content, author) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, article.getTitle());
            pstmt.setString(2, article.getContent());
            pstmt.setString(3, article.getAuthor());
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    article.setArticleId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to save article", e);
        }
        return article;
    }

    @Override
    public List<Article> findAll(long lastItemId, int pageSize) {
        List<Article> articles = new ArrayList<>();

        // lastItemId에 따라 SQL 쿼리를 다르게 작성
        String sql;
        if (lastItemId == 0) {
            // 첫 페이지 로드: OFFSET 없이 최신 글부터 가져오기
            sql = "SELECT * FROM articles WHERE deleted = FALSE ORDER BY article_id DESC LIMIT ?";
        } else {
            // 특정 lastItemId 이후의 레코드를 가져오기
            sql = "SELECT * FROM articles WHERE deleted = FALSE AND article_id < ? ORDER BY article_id DESC LIMIT ?";
        }

        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (lastItemId == 0) {
                // 첫 페이지일 경우, LIMIT만 설정
                pstmt.setInt(1, pageSize);
            } else {
                // lastItemId가 있는 경우, article_id < lastItemId 조건과 LIMIT 설정
                pstmt.setLong(1, lastItemId);
                pstmt.setInt(2, pageSize);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Article article = new Article(
                        rs.getInt("article_id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("author")
                    );
                    articles.add(article);
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to find articles", e);
        }
        return articles;
    }


    @Override
    public Optional<Article> findById(int id) {
        String sql = "SELECT * FROM articles WHERE article_id = ? AND deleted = FALSE";
        Article article = null;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    article = new Article(
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("author")
                    );
                    article.setArticleId(rs.getInt("article_id"));
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to find article", e);
        }
        return Optional.ofNullable(article);
    }

    @Override
    public void update(int id, String title, String content, String userId) {
        String sql = "UPDATE articles SET title = ?, content = ? WHERE article_id = ? AND author = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.setInt(3, id);
            pstmt.setString(4, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Failed to update article", e);
        }
    }

    @Override
    public void deleteById(int id) {
        //soft delete
        String sql = "UPDATE articles SET deleted = TRUE WHERE article_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Failed to delete article", e);
        }
    }

    @Override
    public int getTotalPage(int pageSize) {
        String sql = "SELECT COUNT(*) FROM articles WHERE deleted = FALSE";
        int totalPage = 0;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                totalPage = (rs.getInt(1) + pageSize - 1) / pageSize;
            }
        } catch (SQLException e) {
            logger.error("Failed to get total page", e);
        }
        return totalPage;
    }

    @Override
    public boolean hasNext(int pageSize) {
        return true;
    }
}