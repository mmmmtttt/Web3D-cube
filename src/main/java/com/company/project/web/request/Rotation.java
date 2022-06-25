package com.company.project.web.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rotation {
    @JSONField(name="_x")
    private float _x;
    @JSONField(name="_y")
    private float _y;
    @JSONField(name="_z")
    private float _z;
    @JSONField(name="_w")
    private float _w;
}
