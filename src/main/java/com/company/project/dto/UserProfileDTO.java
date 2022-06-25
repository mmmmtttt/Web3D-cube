package com.company.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    /**
     * 用于前端展示用户档案页面 - 个人信息
     */
    String username;
    Integer gender;
    Integer portraitId;
}
