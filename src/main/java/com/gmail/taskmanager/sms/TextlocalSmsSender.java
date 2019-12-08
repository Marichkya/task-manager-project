package com.gmail.taskmanager.sms;

import com.gmail.taskmanager.dto.TaskToNotifyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class TextlocalSmsSender implements SmsSender {
    @Value("${textlocal.apiKey}")
    private String apiKey;
    @Value("${textlocal.sender}")
    private String sender;

    @Autowired
    private SmsMessage smsMessage;

    public void sendSms(TaskToNotifyDTO task) {
        try {
            String textMessage = smsMessage.toFormMessage(task);

            // Construct data
            String apiKey = "apikey=" + this.apiKey;
            String message = "&message=" + textMessage;
            String sender = "&sender=" + this.sender;
            String numbers = "&numbers=" + task.getPhone();

            // Send data
            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.txtlocal.com/send/?").openConnection();
            String data = apiKey + numbers + message + sender;
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
            conn.getOutputStream().write(data.getBytes("UTF-8"));
            final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                stringBuffer.append(line);
            }
            rd.close();

            System.out.println(stringBuffer.toString());
        } catch (Exception e) {
            System.out.println("Error SMS " + e);
        }
    }
}
