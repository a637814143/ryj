package com.bs.demo.dto;

import lombok.Data;

@Data
public class GuidanceNoteRequest {
    private Long studentId;
    private String note;
}
