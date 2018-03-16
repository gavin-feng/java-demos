package example.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value="/webSocketDemo/{userId}")
@Component
@Slf4j
public class WebSocketDemo {
    // 如果允许用户在多个终端登录的，记录每个用户下多个终端的连接
    private static Map<Long, Set<Session>> userSocket = new ConcurrentHashMap<>();

    /**
     * @Title: onOpen
     * @Description: websocekt连接建立时的操作
     * @param @param userId 用户id
     * @param @param session websocket连接的session属性
     * @param @throws IOException
     */
    @OnOpen
    public void onOpen(@PathParam("userId") Long userId, Session session) throws IOException {
        //根据该用户当前是否已经在别的终端登录进行添加操作
        if (userSocket.containsKey(userId)) {
            log.debug("当前用户id:{}已有其他终端登录",userId);
            userSocket.get(userId).add(session); //增加该用户set中的连接实例
        }else {
            log.debug("当前用户id:{}第一个终端登录",userId);
            Set<Session> sessions = new HashSet<>();
            sessions.add(session);
            userSocket.put(userId, sessions);
        }
        log.debug("用户{}登录的终端个数是为{}",userId,userSocket.get(userId).size());
        log.debug("当前在线用户数为：{}",userSocket.size());
    }

    /**
     * @Title: onClose
     * @Description: 连接关闭的操作
     */
    @OnClose
    public void onClose(@PathParam("userId") Long userId, Session session){
        //移除当前用户终端登录的websocket信息,如果该用户的所有终端都下线了，则删除该用户的记录
        if (userSocket.get(userId).size() == 0) {
            userSocket.remove(userId);
        }else{
            userSocket.get(userId).remove(session);
        }
        log.debug("onClose: 用户{}登录的终端个数是为{}", userId, userSocket.get(userId).size());
        log.debug("当前在线用户数为：{}",userSocket.size());
    }

    /**
     * @Title: onMessage
     * @Description: 收到消息后的操作
     * @param @param message 收到的消息
     * @param @param session 该连接的session属性
     */
    @OnMessage
    public void onMessage(@PathParam("userId") Long userId, String message, Session session) {
        log.debug("收到来自用户id为：{}的消息：{}",userId,message);
        if(session ==null) {
            log.debug("session null");
        } else {
            sendMessageToUser(userId, message);
        }

    }

    /**
     * @Title: onError
     * @Description: 连接发生错误时候的操作
     * @param @param session 该连接的session
     * @param @param error 发生的错误
     */
    @OnError
    public void onError(@PathParam("userId") Long userId, Session session, Throwable error){
        log.debug("用户id为：{}的连接发送错误",userId);
        error.printStackTrace();
    }

    /**
     * @Title: sendMessageToUser
     * @Description: 发送消息给用户下的所有终端
     * @param @param userId 用户id
     * @param @param message 发送的消息
     * @param @return 发送成功返回true，反则返回false
     */
    public Boolean sendMessageToUser(Long userId, String message){
        if (userSocket.containsKey(userId)) {
            log.debug(" 给用户id为：{}的所有终端发送消息：{}",userId,message);
            for (Session session : userSocket.get(userId)) {
                log.debug("sessionId为:{}", session.getId());
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    e.printStackTrace();
                    log.debug(" 给用户id为：{}发送消息失败",userId);
                    return false;
                }
            }
            return true;
        }
        log.debug("发送错误：当前连接不包含id为：{}的用户",userId);
        return false;
    }

}