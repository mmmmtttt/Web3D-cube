package com.company.project.web;
import com.company.project.core.Result;
import com.company.project.core.ResultGenerator;
import com.company.project.service.impl.SocketIOServiceImpl;
import com.company.project.web.request.ChatMessage;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class SocketIOController {

    @Resource
    private SocketIOServiceImpl socketIOService;

//    @GetMapping(value = "/pushMessageToUser")
//    public Result pushMessageToUser(@RequestBody ChatMessage message) {
//        socketIOService.pushMessageToUser(userId, msgContent);
//        return ResultGenerator.genSuccessResult();
//    }

//    @RequestMapping("/push")
//    @ResponseBody
//    public SinoHttpResponse<Boolean> pushMsgByService(@RequestBody ChatMessage chatMessage){
//        SocketIONamespace namespace = socketIOServer.getNamespace(chatMessage.getNamespace());
//        Collection<SocketIOClient> allClients = namespace.getAllClients();
//        for (SocketIOClient client : allClients) {
//            client.sendEvent(chatMessage.getEventName(),chatMessage.getMessage());
//        }
//        return SinoHttpResponse.success(true);
//    }

}
