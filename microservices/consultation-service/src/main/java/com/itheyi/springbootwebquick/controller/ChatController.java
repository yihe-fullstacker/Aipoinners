package com.itheyi.springbootwebquick.controller;

import com.itheyi.springbootwebquick.mapper.ChatMessageRepository;
import com.itheyi.springbootwebquick.mapper.ConsultationRepository;
import com.itheyi.springbootwebquick.mapper.UserRepository;
import com.itheyi.springbootwebquick.entity.ChatMessage;
import com.itheyi.springbootwebquick.entity.Consultation;
import com.itheyi.springbootwebquick.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//这块代码用于患者于医生的实时交流，对应医患实时聊天功能
@RestController
@RequestMapping("/api/chat")
@CrossOrigin(
        originPatterns = {
                "http://localhost:*",
                "http://127.0.0.1:*",
                "http://47.108.52.206",
                "http://47.108.52.206:*",
                "https://47.108.52.206",
                "https://47.108.52.206:*"
        },
        allowCredentials = "true"
)
public class ChatController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private UserRepository userRepository;
    //注入以上三个对象，可以进行数据相关的操作

    /*这是一个 按问诊ID查询聊天记录 的标准 REST 接口，返回按时间升序排列的消息列表。*/
    @GetMapping("/messages" )//医生患者聊天窗口消息的历史加载
    public ResponseEntity<List<ChatMessage>> listMessages(@RequestParam("consultationId") Long consultationId) {
        List<ChatMessage> list = chatMessageRepository.findByConsultationIdOrderByCreatedAtAsc(consultationId);
        //查找完数据，构建并返回响应体，聊天历史记录。
        return ResponseEntity.ok(list);
    }

    //接收前端发送的聊天消息请求，将消息保存到数据库，并返回保存后的完整消息对象。
    @PostMapping("/send")//发送消息（患者发+医生回）这段代码本身并没有“哪一行专门代表患者发送，
    // 哪一行专门代表医生回复”——它是一个通用的消息发送逻辑，既可以处理患者发消息，也可以处理医生回消息。
    public ResponseEntity<ChatMessage> sendMessage(@RequestBody SendChatRequest request) {
        Consultation consultation = consultationRepository.findById(request.getConsultationId())
                .orElseThrow(() -> new IllegalArgumentException("会诊记录不存在"));

        User sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("发送用户不存在"));

        ChatMessage msg = new ChatMessage();//创建了一个空白聊天记录单
        msg.setConsultation(consultation);//这条消息绑定到具体的某次问诊
        msg.setSender(sender);
        msg.setSenderRole(sender.getRole());//角色判定，判断是医生发送信息还是患者发送信息
        msg.setContent(request.getContent());//发送消息内容

        ChatMessage saved = chatMessageRepository.save(msg);//保存到数据库中
        return ResponseEntity.ok(saved);//将数据返回给前端
    }

    public static class SendChatRequest {
        private Long consultationId;
        private Long senderId;
        private String content;

        public Long getConsultationId() {
            return consultationId;
        }

        public void setConsultationId(Long consultationId) {
            this.consultationId = consultationId;
        }

        public Long getSenderId() {
            return senderId;
        }

        public void setSenderId(Long senderId) {
            this.senderId = senderId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
