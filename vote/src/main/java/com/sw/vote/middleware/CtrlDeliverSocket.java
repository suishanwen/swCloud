package com.sw.vote.middleware;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//@Component
//@ServerEndpoint(value = "/websocket/{identity}")
public class CtrlDeliverSocket {
    private static int onlineCount = 0;
    private static final Logger logger = LoggerFactory.getLogger(CtrlDeliverSocket.class);

    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    public static final ConcurrentHashMap<String, CtrlDeliverSocket> wsClientMap = new ConcurrentHashMap<>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 会话标识
     */
    private String identity;


/*    @RequestMapping("/getMessage")
    public String receiver(String body,String taskId){
        return msg;
    }*/

    /**
     * 连接建立成功调用的方法
     *
     * @param session 当前会话session
     */
    @OnOpen
    public void onOpen(@PathParam(value = "identity") String identity, Session session) {
        this.session = session;
        this.identity = identity;
        wsClientMap.put(identity, this);
        addOnlineCount();
        logger.info(identity + ",新链接加入，当前链接数为：" + wsClientMap.size());
    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose() {
        wsClientMap.remove(this.identity);
        subOnlineCount();
        logger.info(this.identity + ",链接关闭，当前链接数为：" + wsClientMap.size());
    }


    /**
     * 给所有客户端群发消息
     *
     * @param message 消息内容
     * @throws IOException e
     */
    @SuppressWarnings("unused")
    public void sendMsgToAll(String message) throws IOException {
        for (Map.Entry<String, CtrlDeliverSocket> entry : wsClientMap.entrySet()) {
            entry.getValue().session.getBasicRemote().sendText(message);
        }
        logger.info("成功群送一条消息:" + wsClientMap.size());
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
        logger.info(this.identity + ",成功发送一条消息:" + message);
    }

    public static synchronized int getOnlineCount() {
        return CtrlDeliverSocket.onlineCount;
    }

    private static synchronized void addOnlineCount() {
        CtrlDeliverSocket.onlineCount++;
    }

    private static synchronized void subOnlineCount() {
        CtrlDeliverSocket.onlineCount--;
    }
}
