package com.company.project.configurer;
import com.company.project.service.impl.SocketIOServiceImpl;
import com.company.project.utils.JwtUtils;
import com.company.project.web.request.ChatMessage;
import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;

@Configuration
@Slf4j
public class NettySocketConfig {

    @Value("${socketio.host}")
    private String host;

    @Value("${socketio.port}")
    private Integer port;

    @Value("${socketio.bossCount}")
    private int bossCount;

    @Value("${socketio.workCount}")
    private int workCount;

    @Value("${socketio.allowCustomRequests}")
    private boolean allowCustomRequests;

    @Value("${socketio.upgradeTimeout}")
    private int upgradeTimeout;

    @Value("${socketio.pingTimeout}")
    private int pingTimeout;

    @Value("${socketio.pingInterval}")
    private int pingInterval;

    @Value("#{'${socketio.namespaces}'.split(',')}")
    private List<String> nameSpaces;

//    // 监听端口
//    private static final Integer SOCKET_PORT = 9099;
//    // Ping消息间隔（毫秒），默认25000。客户端向服务器发送一条心跳消息间隔
//    private static final Integer PING_INTERVAL = 60000;
//    // Ping消息超时时间（毫秒），默认60000，这个时间间隔内没有接收到心跳消息就会发送超时事件
//    private static final Integer PING_TIMEOUT = 180000;
//    // 协议升级超时时间（毫秒），默认10000。HTTP握手升级为ws协议超时时间
//    private static final Integer UPGRADE_TIMEOUT = 10000;

    @Bean
    public SocketIOServer socketIOServer() {
//        SocketConfig socketConfig = new SocketConfig();
//        socketConfig.setTcpNoDelay(true);
//        socketConfig.setSoLinger(0);
//        config.setSocketConfig(socketConfig);

        /*
         * 创建Socket，并设置监听端口
         */
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();

        //解决对此重启服务时，netty端口被占用问题
        com.corundumstudio.socketio.SocketConfig tmpConfig = new com.corundumstudio.socketio.SocketConfig();
        tmpConfig.setReuseAddress(true);
        config.setSocketConfig(tmpConfig);
        config.setHostname(host);
        config.setPort(port);
        config.setBossThreads(bossCount);
        config.setWorkerThreads(workCount);
        config.setAllowCustomRequests(allowCustomRequests);
        config.setUpgradeTimeout(upgradeTimeout);
        config.setPingTimeout(pingTimeout);
        config.setPingInterval(pingInterval);
        //自定义url
        //config.setContext("/myapp");

        // 握手协议参数使用JWT的Token认证方案
        config.setAuthorizationListener(data -> {
            String token = data.getSingleUrlParam(JwtUtils.TOKEN);
            String name = JwtUtils.getName(token);
            // 如果token验证失败，那getName方法会抛出异常，并自己catch，返回null
            return name != null;
        });


        SocketIOServer socketIOServer = new SocketIOServer(config);


        // 监听namespace
//        if (nameSpaces != null && nameSpaces.size() > 0) {
//            for (int i = 0,j = nameSpaces.size(); i < j; i++) {
//                SocketIONamespace space1 = socketIOServer.addNamespace(nameSpaces.get(i));
//                System.out.println(space1.toString());
//
////                space1.addEventListener("message", ChatMessage.class, (client, param, ackRequest) -> {
////                    onData(client, socketIOServer, param);
////                });
//
//                space1.addListeners(new SocketIOServiceImpl(socketIOServer));
//
////                space1.addConnectListener(client -> {
////                    SocketAddress remoteAddress = client.getRemoteAddress();
////                    SocketIONamespace namespace = client.getNamespace();
////                    log.info("用户{}上线", client.getSessionId().toString());
////                        }
////                );
////                space1.addDisconnectListener(client ->
////                        log.info("用户{}离开", client.getSessionId().toString())
////                );
//            }
//        }
        return socketIOServer;
    }


    private void onData(SocketIOClient client, SocketIOServer socketIOServer, ChatMessage message) {
        //这里的response是要来干嘛的?
//        SocketResponse response = new SocketResponse();
//        response.setMessage(param.getMessage());
//        response.setUrl(param.getUrl());
        //SocketIONamespace namespace = client.getNamespace();
        //StringUtils.isEmpty(message.getNamespace())

        String roomId = client.getHandshakeData().getSingleUrlParam("room");
        if (roomId==null || roomId.equals("")) {
            //Collection<SocketIONamespace> allNamespaces = socketIOServer.getAllNamespaces();
            //allNamespaces.forEach(x -> x.getBroadcastOperations().sendEvent("message", message));
            //message为事件的名称，message为发送的内容
            client.getNamespace().getBroadcastOperations().sendEvent("message", message);
        } else {
            //SocketIONamespace namespace = socketIOServer.getNamespace("/" + message.getNamespace());
            client.getNamespace().getRoomOperations(roomId).sendEvent("message", message);
        }
    }

    /**
     * 用于扫描netty-socketio的注解，比如 @OnConnect、@OnEvent
     */

    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketServer) {
        return new SpringAnnotationScanner(socketServer);
    }
}

