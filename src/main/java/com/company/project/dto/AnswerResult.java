package com.company.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class AnswerResult {
    int checkpoint;
    String result;
    int socketId;
}
