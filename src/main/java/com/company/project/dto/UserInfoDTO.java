package com.company.project.dto;
import com.company.project.model.User;
import com.company.project.web.request.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    //这个是后端维护websocket服务器房间中用户信息的类
    String username;
    Integer socketId;//新增socketID,对应user表中的userID
    Portrait portrait;
    Position position;

    public UserInfoDTO(User user, Integer socketId, Position position) {
        this.username = user.getUsername();
        this.socketId = socketId;
        this.portrait = new Portrait(user.getPortraitId(), user.getJacket(), user.getPants());
        this.position = position;
    }
}
