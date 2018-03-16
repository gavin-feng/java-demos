package example.websocket.controller;

import example.websocket.service.WSMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 通过HTTP请求来向某用户发送消息
 */
@Controller
@RequestMapping("/message")
@Slf4j
public class MessageController {
    @Autowired
    private WSMessageService wsMessageService;
    //请求入口
    @RequestMapping(value="/TestWS",method= RequestMethod.GET)
    @ResponseBody
    public String TestWS(@RequestParam(value="userId",required=true) Long userId,
                         @RequestParam(value="message",required=true) String message){
        log.debug("收到发送请求，向用户{}的消息：{}",userId,message);
        if(wsMessageService.sendToAllTerminal(userId, message)){
            return "发送成功";
        }else{
            return "发送失败";
        }
    }
}

