package com.company.project.dto;

import com.company.project.web.request.Rotation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PositionDTO {
    Integer socketId;
    float x;
    float y;
    float z;
    Rotation rotation;
    String action;
}
