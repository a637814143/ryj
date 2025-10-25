package com.bs.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Teacher {
    private Long id;
    private String name;
    private String department;
    private String email;
    private String phone;
    @Builder.Default
    private Map<Long, String> guidanceNotes = new HashMap<>();
}
