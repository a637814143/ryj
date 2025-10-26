package com.ryj.demo.dto;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.Data;

@Data
public class PublicSearchResult {

    private String type;

    private String title;

    private String subtitle;

    private String description;

    private String link;

    private LocalDateTime timestamp;

    private Map<String, Object> metadata;
}
