package com.ryj.employment.controller;

import com.ryj.employment.entity.Article;
import com.ryj.employment.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @GetMapping("/list")
    public List<Article> getArticleList() {
        // 查询所有文章数据
        return articleService.list();
    }

    @GetMapping("/{id}")
    public Article getArticleById(Long id) {
        // 根据ID查询文章
        return articleService.getById(id);
    }
}