package com.company.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecordDTO {
    /**
     * 用于前端展示用户档案页面 - 个人所有答题记录
     */
    Integer roomId;
    Integer checkpointId;
    String tag;
    Integer trial;
    String result;
}
