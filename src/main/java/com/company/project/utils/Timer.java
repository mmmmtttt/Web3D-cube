package com.company.project.utils;
import com.alibaba.fastjson.JSONArray;
import com.company.project.dto.PositionDTO;
import com.company.project.dto.UserInfoDTO;
import com.company.project.web.request.Position;
import com.corundumstudio.socketio.SocketIOServer;
import java.util.Collection;
import java.util.Map;
import java.util.TimerTask;

public class Timer {
    private final SocketIOServer server;
    private java.util.Timer mTimer;
    //每一个room对应一个timer
    private String roomID;
    //对应SocketService类中的内层嵌套map
    private Map<String, UserInfoDTO> clients;

    public Timer(SocketIOServer server, String roomID, Map<String, UserInfoDTO> clients){
        this.server=server;
        this.mTimer = new java.util.Timer();
        this.roomID = roomID;
        this.clients = clients;
    }

    private TimerTask mTask = new TimerTask() {
        @Override
        public void run() {
            //获取所有的userInfo
            JSONArray jsonArray = new JSONArray();
            Collection<UserInfoDTO> userInfoDTOS = clients.values();
            for (UserInfoDTO u:userInfoDTOS) {
                Position position = u.getPosition();
                Integer socketID = u.getSocketId();
                PositionDTO positionDTO = new PositionDTO(socketID,position.getX(),position.getY(),position.getZ(),position.getRotation(),position.getAction());
                jsonArray.add(positionDTO);
            }
            server.getNamespace("/user").getRoomOperations(roomID).sendEvent("position", jsonArray.toJSONString());
        }
    };


    public void start() {
        mTimer.scheduleAtFixedRate(mTask, 0,1000);
    }

    public void stop(){
        mTimer.cancel();  //Terminates this timer,discarding any currently scheduled tasks.
        mTimer.purge();   // Removes all cancelled tasks from this timer's task queue.
        mTimer = new java.util.Timer();
        mTask = new TimerTask() {
            @Override
            public void run() {
                JSONArray jsonArray = new JSONArray();
                Collection<UserInfoDTO> userInfoDTOS = clients.values();
                for (UserInfoDTO u:userInfoDTOS) {
                    Position position = u.getPosition();
                    Integer socketID = u.getSocketId();
                    PositionDTO positionDTO = new PositionDTO(socketID,position.getX(),position.getY(),position.getZ(),position.getRotation(),position.getAction());
                    jsonArray.add(positionDTO);
                }
                server.getNamespace("/user").getRoomOperations(roomID).sendEvent("position", jsonArray.toJSONString());
            }
        };
        System.out.println("stopped");
    }

}
