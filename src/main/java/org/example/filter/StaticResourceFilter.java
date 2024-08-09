package org.example.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.example.entity.Article;
import org.example.service.ArticleService;
import org.example.util.LoggerUtil;
import org.slf4j.Logger;

@WebFilter(
    value = {"/*"},
    initParams = @WebInitParam(name = "encoding", value = "utf-8")
)
public class StaticResourceFilter implements Filter {

    private final Logger logger = LoggerUtil.getLogger();

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        logger.info("Request URI: {}", req.getRequestURI());
        // /user/form.html 요청을 /static/user/form.html로 리디렉션
        if (req.getRequestURI().equals("/user/form.html")) {
            res.sendRedirect("/static/user/form.html");
        } else if (req.getRequestURI().equals("/qna/form.html")) {
            res.sendRedirect("/static/qna/form.html");
        } else if (req.getRequestURI().equals("/error/not-same-author.html")) {
            res.sendRedirect("/static/error/not-same-author.html");
        }
        else if (req.getRequestURI().equals("/")) {
            ArticleService articleService = new ArticleService();

// lastItemId가 전달되지 않았으면(null이면) 처음부터 시작하는 것으로 처리
            String lastItemIdParam = req.getParameter("lastItemId");
            Long lastItemId = (lastItemIdParam == null || lastItemIdParam.isEmpty()) ? 0L : Long.parseLong(lastItemIdParam);

            int pageSize = req.getParameter("pageSize") == null ? 15 : Integer.parseInt(req.getParameter("pageSize"));

// lastItemId와 pageSize를 사용하여 현재 페이지의 articles를 가져옴
            List<Article> articles = articleService.findAll(lastItemId, pageSize);
            req.setAttribute("articles", articles);
            req.setAttribute("size", pageSize);

// 현재 페이지의 첫 번째 article_id를 JSP로 전달 (prev 버튼을 위해 사용)
            if (!articles.isEmpty()) {
                req.setAttribute("firstItemIdOfCurrentPage", articles.get(0).getArticleId());
            }

// 다음 페이지로 넘어갈 때 사용할 lastItemId를 결정
            if (!articles.isEmpty()) {
                req.setAttribute("lastItemId", articles.get(articles.size() - 1).getArticleId());
            } else {
                req.setAttribute("lastItemId", null); // 더 이상 아이템이 없을 때
            }
// 페이지가 가득 찼다면 더 많은 아이템이 있음
            req.setAttribute("hasMoreItems", articles.size() == pageSize);
            req.getRequestDispatcher("/WEB-INF/main.jsp").forward(req, res);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }
}