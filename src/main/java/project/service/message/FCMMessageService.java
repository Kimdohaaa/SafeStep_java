package project.service.message;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.*;
import okhttp3.Request;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional
public class FCMMessageService {
    private final String API_URL = "https://fcm.googleapis.com/fcm/send";
    private final String SERVER_KEY = "";  // Firebase 콘솔에서 서버키 가져와서 바꾸기

    public void sendMessageTo(String targetToken, String title, String body) throws IOException {
        JSONObject message = new JSONObject();
        message.put("to", targetToken);

        JSONObject notification = new JSONObject();
        notification.put("title", title);
        notification.put("body", body);

        message.put("notification", notification);

        RequestBody requestBody = RequestBody.create(
                message.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .post(requestBody)
                .addHeader("Authorization", "key=YOUR_SERVER_KEY")
                .addHeader("Content-Type", "application/json")
                .build();

        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();

        System.out.println("FCM 응답 코드: " + response.code());
        System.out.println("FCM 응답 바디: " + response.body().string());
    }

}
