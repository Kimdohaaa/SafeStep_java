package project.controller.message;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.service.message.FCMMessageService;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/fcmmsg")
@RequiredArgsConstructor
public class FCMMessageController {
    private  final FCMMessageService fcmMessageService;

    // [1] 푸시 알림 전송용 API
    @PostMapping("/sendNotification")
    public ResponseEntity<?> sendNotification(@RequestBody Map<String, String> payload) throws IOException {
        String token = payload.get("token");
        String title = payload.get("title");
        String body = payload.get("body");


        fcmMessageService.sendMessageTo(token, title, body);
        return ResponseEntity.ok("알림 전송 완료");
    }
}
