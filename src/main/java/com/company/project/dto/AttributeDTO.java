package com.company.project.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttributeDTO {
    /**
     * 用于前端展示用户档案页面 - 个人五边形战绩
     */
    Integer correct;
    Integer wrong;
    @JSONField(name="CA")
    Integer CA;
    @JSONField(name="CN")
    Integer CN;
    @JSONField(name="Cypher")
    Integer Cypher;
    @JSONField(name="DS")
    Integer DS;
    @JSONField(name="Other")
    Integer Other;

}
