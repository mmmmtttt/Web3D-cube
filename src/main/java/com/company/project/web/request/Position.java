package com.company.project.web.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Position {
    private float x;
    private float y;
    private float z;
    private Rotation rotation;
    private String action;
    public static final String idle = "idle";
    public static final String walking = "walking";
}
