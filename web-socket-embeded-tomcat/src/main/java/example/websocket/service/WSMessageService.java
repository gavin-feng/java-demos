package example.websocket.service;

import example.websocket.WebSocketDemo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("webSocketMessageService")
@Slf4j
public class WSMessageService {
    @Autowired
    private WebSocketDemo wwebSocketDemo;

    /**
     * @Title: sendToAllTerminal
     * @Description: 调用websocket类给用户下的所有终端发送消息
     * @param @param userId 用户id
     * @param @param message 消息
     * @param @return 发送成功返回true，否则返回false
     */
    public Boolean sendToAllTerminal(Long userId,String message){
        log.debug("向用户{}的消息：{}",userId,message);
        if(wwebSocketDemo.sendMessageToUser(userId,message)){
            return true;
        }else{
            return false;
        }
    }
}
