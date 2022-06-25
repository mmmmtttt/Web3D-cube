package com.company.project.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.BeanContext;
import com.alibaba.fastjson.serializer.ContextValueFilter;
import com.company.project.core.ServiceException;
import com.company.project.dao.RecordMapper;
import com.company.project.dao.UserMapper;
import com.company.project.dto.AnswerResult;
import com.company.project.dto.CheckpointState;
import com.company.project.dto.Contributor;
import com.company.project.dto.UserInfoDTO;
import com.company.project.model.Record;
import com.company.project.web.request.*;
import com.company.project.model.User;
import com.company.project.utils.JwtUtils;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnEvent;
import org.springframework.stereotype.Service;
import com.company.project.utils.Timer;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class SocketIOServiceImpl {

    private SocketIOServer socketIOServer;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RecordMapper recordMapper;

    /**
     * 存放已连接的客户端
     * 嵌套的Map，外层键为roomID，内层键为username
     */
    //private static Map<String, SocketIOClient> clientMap = new ConcurrentHashMap<>();
    private static Map<Integer, Map<String, UserInfoDTO>> clients = new ConcurrentHashMap<>();
    //存放对应于每个room的计时器，map的键值就是roomID
    private static Map<Integer, Timer> timers = new ConcurrentHashMap<>();
    //记录线索的状态，外层键为roomID，内层键为checkpointId 0-未解开
    private static Map<Integer,Map<Integer,CheckpointState>> checkpoint = new ConcurrentHashMap<>();
    //记录线索的答案，外层键为roomID，内层键为checkpointId
    private static Map<Integer,Map<Integer,String>> answers = new ConcurrentHashMap<>();

    private String namespace = "/user";

    //newuser方法的json过滤器，用于向前端传输数据时过滤掉position信息
    private static ContextValueFilter newuserFilter = new ContextValueFilter() {
        public Object process(BeanContext context, Object object, String name, Object value) {
            if (name.equals("position")) {
                return null;
            } else {
                return value;
            }
        }
    };

    public SocketIOServiceImpl(SocketIOServer server) {
        this.socketIOServer = server;
        //初始化所有的room和对应的Map,目前暂定3个room
        for (int i = 1; i < 4; i++) {
            Map<String, UserInfoDTO> map = new ConcurrentHashMap<>();
            clients.put(i, map);
            Timer timer = new Timer(server, String.valueOf(i), clients.get(i));
            timers.put(i, timer);
            Map<Integer, CheckpointState> roomCheckpoints = new HashMap<>();
            checkpoint.put(i,roomCheckpoints);
            Map<Integer,String> roomAnswers = new HashMap<>();
            answers.put(i,roomAnswers);
        }
        //针对room 1 添加已经设定好的线索和相应答案
        resetCheckpointOfRoom1();
        initializeAnswers();
        server.removeNamespace("/");
        SocketIONamespace namespace1 = server.addNamespace(namespace);

        //增加connect和disconnect
        namespace1.addConnectListener(this::onConnect);
        namespace1.addDisconnectListener(this::onDisConnect);
        //增加其他的listener
        namespace1.addEventListener("new_user", String.class, (client, param, ackRequest) -> {
            onNewUserEvent(client, ackRequest, param);
        });
        namespace1.addEventListener("message", ChatMessage.class, (client, param, ackRequest) -> {
            onMessageEvent(client, ackRequest, param);
        });
        namespace1.addEventListener("position", Position.class, (client, param, ackRequest) -> {
            onPositionEvent(client, ackRequest, param);
        });
        namespace1.addEventListener("try_answer", TryAnswer.class, (client, param, ackRequest) -> {
            onTryAnswerEvent(client, ackRequest, param);
        });
        namespace1.addEventListener("checkpoint_state", String.class, (client, param, ackRequest) -> {
            onCheckpointStateEvent(client, ackRequest, param);
        });
    }


    //所有namespace的广播
    @OnEvent(value = "broadcast")
    public void pushToAll(ChatMessage message) {
        Collection<SocketIONamespace> allNamespaces = socketIOServer.getAllNamespaces();
        allNamespaces.forEach(x -> {
            x.getBroadcastOperations().sendEvent("broadcast", message);
        });
    }

//    public void pushToNameSpace(ChatMessage message) {
//        SocketIONamespace namespace = socketIOServer.getNamespace("/" + message.getNamespace().replace("/", ""));
//        namespace.getBroadcastOperations().sendEvent("notification", message);
//    }

    /**
     * 点对点消息
     *
     * @param client
     * @param request
     * @param data
     */
    @OnEvent(value = "p2p")
    public void onP2PEvent(SocketIOClient client, AckRequest request, String data) {
        System.out.println("发来消息 >>> " + data);
        UUID sessionId = client.getSessionId();
        socketIOServer.getNamespace(client.getNamespace().getName()).getClient(sessionId).sendEvent("p2p", "点对点消息的返回" + Math.random());
    }


    //@OnConnect
    public void onConnect(SocketIOClient client) {
        if (client != null) {
            System.out.println("onConnect > " + client.getNamespace().getName() + "-" + client.getSessionId().toString());
            //获取roomID
            String roomId = client.getHandshakeData().getSingleUrlParam("room");
            if (roomId == null || roomId.equals("")) {
                //websocket如何处理异常-直接向前端发消息？
                //throw new ServiceException("room 为空");
                System.out.println("empty room");
            } else {
                client.joinRoom(roomId);
                //以下得到 token
                String username = getUsernameByToken(client);
                //广播connect通知
                socketIOServer.getNamespace(client.getNamespace().getName()).getRoomOperations(roomId).sendEvent("connect", JSON.toJSONString(new ChatMessage(username, username + " has joint.")));
                // 把给新加入玩家发new_user事件的处理放在了new_user的handler里
                // 将新用户加入map中,check user exists?
                User user = userMapper.findUserByName(username);
                Integer socketID = user.getId();
                Rotation rotation = new Rotation(0,0,0,1);
                Position position = new Position(0,0,0,rotation,Position.idle);
                UserInfoDTO userInfoDTO = new UserInfoDTO(user,socketID,position);
                clients.get(Integer.parseInt(roomId)).put(username,userInfoDTO);
                //向其他用户广播新用户信息
                socketIOServer.getNamespace(client.getNamespace().getName()).getRoomOperations(roomId).sendEvent("new_user", client, "[" + JSON.toJSONString(userInfoDTO, newuserFilter) + "]");

                //若新加入的client是这个room中的第一个，则启动timer
                if (socketIOServer.getNamespace(client.getNamespace().getName()).getRoomOperations(roomId).getClients().size() == 1) {
                    timers.get(Integer.parseInt(roomId)).start();//获取指定的timer 启动
                }
            }

        } else {
            System.out.println("client is null.");
            throw new ServiceException("客户端为空");
        }
    }

    //@OnDisconnect
    public void onDisConnect(SocketIOClient client) {
        String roomId = client.getHandshakeData().getSingleUrlParam("room");
        if (roomId == null || roomId.equals("")) {
            //throw new ServiceException("room 为空");
            System.out.println("empty room");
        } else {
            String username = getUsernameByToken(client);
            Integer socketID = clients.get(Integer.parseInt(roomId)).get(username).getSocketId();
            client.leaveRoom(roomId);
            clients.get(Integer.parseInt(roomId)).remove(username);
            String deluser = "{\"socketId\":" + socketID + "}";
            socketIOServer.getNamespace(client.getNamespace().getName()).getRoomOperations(roomId).sendEvent("del_user", deluser);
            socketIOServer.getNamespace(client.getNamespace().getName()).getRoomOperations(roomId).sendEvent("disconnect", JSON.toJSONString(new ChatMessage(username, username + " has left.")));
            if (socketIOServer.getNamespace(client.getNamespace().getName()).getRoomOperations(roomId).getClients().size() == 0) {
                timers.get(Integer.parseInt(roomId)).stop();//如果房间空了，计时器就停止
            }
        }
        client.disconnect();

        System.out.println("onDisconnect > " + client.getNamespace().getName() + "-" + client.getSessionId().toString());
    }

    /*
    向新加入的用户发送房间中的其他人的数据,单独拿出来的原因是，如果在connect里给前端发newuser，
    前端可能刚connect完，还没注册newuser的handler，那么就会错过这个消息
     */
    public void onNewUserEvent(SocketIOClient client, AckRequest request, String nocontent) {
        if (client != null) {
            System.out.println("onNewUser > " + client.getNamespace().getName() + "-" + client.getSessionId().toString());
            //获取roomID
            String roomId = client.getHandshakeData().getSingleUrlParam("room");
            if (roomId == null || roomId.equals("")) {
                //websocket如何处理异常-直接向前端发消息？
                //throw new ServiceException("room 为空");
                System.out.println("empty room");
            } else {
                String username = getUsernameByToken(client);
                JSONArray jsonArray = new JSONArray();
                //过滤掉用户自身
                jsonArray.addAll(clients.get(Integer.parseInt(roomId)).values().stream().filter((user)->!user.getUsername().equals(username)).collect(Collectors.toSet()));
                System.out.println(JSON.toJSONString(jsonArray));
                client.sendEvent("new_user", JSON.toJSONString(jsonArray));
            }
        }
    }

    //@OnEvent(value = "message")
    public void onMessageEvent(SocketIOClient client, AckRequest request, ChatMessage message) {
        System.out.println(client.getRemoteAddress().toString() + "发来消息：" + message.getMessage());
        String roomId = client.getHandshakeData().getSingleUrlParam("room");
        if (roomId == null || roomId.equals("")) {
            //throw new ServiceException("room为空");
            System.out.println("empty room");
        } else {
            socketIOServer.getNamespace(client.getNamespace().getName()).getRoomOperations(roomId).sendEvent("message", JSON.toJSONString(message));
        }
    }

    //@OnEvent(value = "position")
    //接收前端定期发送的position事件并更新数据结构
    public void onPositionEvent(SocketIOClient client, AckRequest request, Position position) {
//        System.out.println(client.getRemoteAddress().toString() + "位置更改：" + position.getX());
        String roomId = client.getHandshakeData().getSingleUrlParam("room");
        if (roomId == null || roomId.equals("")) {
        } else {
            String username = getUsernameByToken(client);
            //更新map
            clients.get(Integer.parseInt(roomId)).get(username).setPosition(position);
            //向其他玩家广播位置更改
            //PositionDTO positionDTO = new PositionDTO(username,position.getX(),position.getY(),position.getZ());
            //socketIOServer.getNamespace(client.getNamespace().getName()).getRoomOperations(roomId).sendEvent("position", client, JSON.toJSONString(positionDTO));
        }
    }

    public synchronized void onTryAnswerEvent(SocketIOClient client, AckRequest request, TryAnswer tryAnswer) {
        System.out.println(client.getRemoteAddress().toString() + "");
        String roomId = client.getHandshakeData().getSingleUrlParam("room");
        if (roomId == null || "".equals(roomId)) return;
        Integer id = getUserIdByToken(client);
        String username = getUsernameByToken(client);
        Integer roomID = Integer.parseInt(roomId);
        Integer checkpointId = tryAnswer.getCheckpoint();
        if (checkpoint.get(Integer.parseInt(roomId)).get(checkpointId).getState() == 1) {
            AnswerResult result = new AnswerResult(tryAnswer.getCheckpoint(), "cracked", id);
            socketIOServer.getNamespace(client.getNamespace().getName()).getRoomOperations(roomId)
                    .sendEvent("answer_result", JSON.toJSONString(result));
            return;
        }
        //对answer进行过滤
        String newAnswer = answerFilter(tryAnswer.getAnswer());

        if (answers.get(Integer.parseInt(roomId)).get(checkpointId).equals(newAnswer)) {
            if (checkpointId == 7) {
                // 答对了开门的密码
                // 更改checkpoint状态
                checkpoint.get(Integer.parseInt(roomId)).get(checkpointId).setState(1);
                List<Contributor> contributors = recordMapper.getContributors(roomID);
                Victory victory = new Victory();
                victory.setWinnerId(id);
                victory.setAchievement(contributors);
                socketIOServer.getNamespace(client.getNamespace().getName()).getRoomOperations(roomId)
                        .sendEvent("victory", JSON.toJSONString(victory));
                resetCheckpointOfRoom1();
                recordMapper.welcomeVictory(roomID);
                return;
            }
            AnswerResult result = new AnswerResult(tryAnswer.getCheckpoint(), "true", id);
            socketIOServer.getNamespace(client.getNamespace().getName()).getRoomOperations(roomId)
                    .sendEvent("answer_result", JSON.toJSONString(result));
            //更改checkpoint状态
            checkpoint.get(Integer.parseInt(roomId)).get(checkpointId).setState(1);

            //查看数据库中是否已经存在该条目
            if(recordMapper.findRecord(username,roomID,checkpointId)){
                //若存在则直接更新
                recordMapper.updateSuccess(id,roomID,checkpointId);
            }else {
                //若不存在则插入
                Record record = new Record(id,roomID,checkpointId,getTag(checkpointId),1,"succeeded",0);
                recordMapper.addRecord(record);
            }

        } else {
            AnswerResult result = new AnswerResult(tryAnswer.getCheckpoint(), "false", id);
            //查看数据库中是否已经存在该条目
            if (checkpointId == 7) {
                // 房间密码答错了不需要和数据库交互
                client.sendEvent("answer_result", JSON.toJSONString(result));
                return;
            }
            if(recordMapper.findRecord(username,roomID,checkpointId)){
                //若存在则直接更新
                recordMapper.updateFailure(id,roomID,checkpointId);
            }else {
                //若不存在则插入
                Record record = new Record(id,roomID,checkpointId,getTag(checkpointId),1,"failed",0);
                recordMapper.addRecord(record);
            }
            client.sendEvent("answer_result", JSON.toJSONString(result));
        }
    }

    public void onCheckpointStateEvent(SocketIOClient client, AckRequest request, String nocontent) {
        if (client != null) {
            System.out.println("onCheckpoint > " + client.getNamespace().getName() + "-" + client.getSessionId().toString());
            //获取roomID
            String roomId = client.getHandshakeData().getSingleUrlParam("room");
            if (roomId == null || roomId.equals("")) {
                System.out.println("empty room");
            } else {
                JSONArray jsonArray = new JSONArray();
                jsonArray.addAll(checkpoint.get(Integer.parseInt(roomId)).values());
                System.out.println(JSON.toJSONString(jsonArray));
                client.sendEvent("checkpoint_state", JSON.toJSONString(jsonArray));
            }
        }
    }

    /*----------------------------- 以下为工具函数 --------------------------------------*/


    private String getParamByClient(SocketIOClient client) {
        return client.getHandshakeData().getSingleUrlParam("username");
    }

    private String getUsernameByToken(SocketIOClient client) {
        return JwtUtils.getName(client.getHandshakeData().getSingleUrlParam(JwtUtils.TOKEN));
    }

    private Integer getUserIdByToken(SocketIOClient client) {
        return JwtUtils.getUserId(client.getHandshakeData().getSingleUrlParam(JwtUtils.TOKEN));
    }

    /**
     * 此方法为获取client连接中的参数，需要修改!!!
     * 获取客户端url中的loginUserId参数
     *
     * @param client
     * @return
     */
    private String getParamsByClient(SocketIOClient client) {
        // 从请求的连接中拿出参数（这里的loginUserId必须是唯一标识）
        Map<String, List<String>> params = client.getHandshakeData().getUrlParams();
        List<String> list = params.get("loginUserId");
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 获取连接的客户端ip地址
     *
     * @param client: 客户端
     * @return java.lang.String
     */
    private String getIpByClient(SocketIOClient client) {
        String sa = client.getRemoteAddress().toString();
        return sa.substring(1, sa.indexOf(":"));
    }

    private void initializeAnswers(){
        answers.get(1).put(0,"BB23");
        answers.get(1).put(1,"01");
        answers.get(1).put(2,"SYNACK");
        answers.get(1).put(3,"ricedumpling");
        answers.get(1).put(4,"01120");
        answers.get(1).put(5,"byte");
        answers.get(1).put(6,"byte");
        answers.get(1).put(7,"30122");
    }

    //一种原始的过滤方法...不知道合不合适
    private String answerFilter(String answer){
        String str2 = answer.replaceAll(" ", "");
        String str3 = str2.replaceAll("-", "");
        String str4 = str3.replaceAll("/", "");
        return str4;
    }

    //根据checkpointId获取相应的标签，最终victory无标签
    private String getTag(int checkpoint){
        switch (checkpoint){
            case 0:
            case 1:
                return "CA";
            case 2:
                return "CN";
            case 3:
                return "Cypher";
            case 4:
                return "DS";
            case 5:
            case 6:
                return "Other";
            default:return "";
        }
    }

    private void resetCheckpointOfRoom1() {
        for(int i = 0; i < 8; i++){
            CheckpointState state = new CheckpointState(i,0);
            checkpoint.get(1).put(i,state);
        }
    }

}
