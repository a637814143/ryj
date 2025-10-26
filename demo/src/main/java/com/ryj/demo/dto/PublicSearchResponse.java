package com.ryj.demo.dto;

import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class PublicSearchResponse {

    private List<PublicSearchResult> results;

    private long total;

    private Map<String, Long> breakdown;

    private List<String> suggestions;
}
