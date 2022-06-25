package com.company.project.web.request;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {

//    private long toId;
//    private long fromId;
    private String username;
    private String message;

}
