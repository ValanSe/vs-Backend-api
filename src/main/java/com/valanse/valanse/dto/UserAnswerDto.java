package com.valanse.valanse.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAnswerDto {
    private Integer quizId; // 답변한 퀴즈 식별자
    private String selectedOption; // 선택한 옵션
    private Integer preference; // 퀴즈에 대한 선호도
}
