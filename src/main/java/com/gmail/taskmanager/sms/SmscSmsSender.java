package com.gmail.taskmanager.sms;

import com.gmail.taskmanager.dto.TaskToNotifyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

@Service
public class SmscSmsSender implements SmsSender {
    @Value("${smsc.user.login}")
    private String login;

    @Value("${smsc.user.password}")
    private String password;

    @Value("${smsc.properties.https}")
    private boolean https;

    @Value("${smsc.properties.charset}")
    private String charset;

    @Value("${smsc.properties.debug}")
    private boolean debug;

    @Value("${smsc.properties.post}")
    private boolean post;

    @Value("${smsc.message.translit}")
    private int translit;

    @Value("${smsc.message.time}")
    private String time;

    @Value("${smsc.message.id}")
    private String id;

    @Value("${smsc.message.format}")
    private int format;

    @Value("${smsc.message.sender}")
    private String sender;

    @Value("${smsc.message.query}")
    private String query;

    @Autowired
    private SmsMessage smsMessage;

    public SmscSmsSender() {
    }

    public void sendSms(TaskToNotifyDTO task) {
        String[] formats = {"", "flash=1", "push=1", "hlr=1", "bin=1", "bin=2", "ping=1"};
        String[] result = {};

        String textMessage = smsMessage.toFormMessage(task);

        try {
            result = SmscSendCmd("send", "cost=3&phones=" + URLEncoder.encode(task.getPhone(), charset)
                    + "&mes=" + URLEncoder.encode(textMessage, charset)
                    + "&translit=" + translit + "&id=" + id + (format > 0 ? "&" + formats[format] : "")
                    + (sender == "" ? "" : "&sender=" + URLEncoder.encode(sender, charset))
                    + (time == "" ? "" : "&time=" + URLEncoder.encode(time, charset))
                    + (query == "" ? "" : "&" + query));
        } catch (UnsupportedEncodingException e) {

        }

        if (debug) {
            if (Integer.parseInt(result[1]) > 0) {
                System.out.println("Message successfully sent. ID: " + result[0] +
                        ", all SMS: " + result[1] +
                        ", price: " + result[2] +
                        ", balance: " + result[3]);
            } else {
                System.out.print("Error №" + Math.abs(Integer.parseInt(result[1])));
                System.out.println(Integer.parseInt(result[0]) > 0 ? (", ID: " + result[0]) : "");
            }
        }
    }

    public String[] getSmsCost(String phones, String message, int translit, int format, String sender, String query) {
        String[] formats = {"", "flash=1", "push=1", "hlr=1", "bin=1", "bin=2", "ping=1"};
        String[] result = {};

        try {
            result = SmscSendCmd("send", "cost=1&phones=" + URLEncoder.encode(phones, charset)
                    + "&mes=" + URLEncoder.encode(message, charset)
                    + "&translit=" + translit + (format > 0 ? "&" + formats[format] : "")
                    + (sender == "" ? "" : "&sender=" + URLEncoder.encode(sender, charset))
                    + (query == "" ? "" : "&" + query));
        } catch (UnsupportedEncodingException e) {

        }
        // (cost, cnt) или (0, -error)

        if (debug) {
            if (Integer.parseInt(result[1]) > 0) {
                System.out.println("Price for send: " + result[0] + ", All SMS: " + result[1]);
            } else {
                System.out.print("Error №" + Math.abs(Integer.parseInt(result[1])));
            }
        }

        return result;
    }

    public String[] getStatus(int id, String phone, int all) {
        String[] result = {};
        String tmp;

        try {
            result = SmscSendCmd("status", "phone=" + URLEncoder.encode(phone, charset) + "&id=" + id + "&all=" + all);

            if (debug) {
                if (result[1] != "" && Integer.parseInt(result[1]) >= 0) {
                    java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(Integer.parseInt(result[1]));
                    System.out.println("Status SMS = " + result[0]);
                } else
                    System.out.println("Error №" + Math.abs(Integer.parseInt(result[1])));
            }

            if (all == 1 && result.length > 9 && (result.length < 14 || result[14] != "HLR")) {
                tmp = implode(result, ",");
                result = tmp.split(",", 9);
            }
        } catch (UnsupportedEncodingException e) {

        }

        return result;
    }

    public String getBalance() {
        String[] result = {};

        result = SmscSendCmd("balance", ""); // (balance) или (0, -error)

        if (debug) {
            if (result.length == 1)
                System.out.println("Account on balance: " + result[0]);
            else
                System.out.println("Error №" + Math.abs(Integer.parseInt(result[1])));
        }

        return result.length == 2 ? "" : result[0];
    }

    private String[] SmscSendCmd(String cmd, String arg) {
        String ret = ",";

        try {
            String url = (https ? "https" : "http") + "://smsc.ru/sys/" + cmd + ".php?login=" + URLEncoder.encode(login, charset)
                    + "&psw=" + URLEncoder.encode(password, charset)
                    + "&fmt=1&charset=" + charset + "&" + arg;

            int i = 0;
            do {
                if (i > 0)
                    Thread.sleep(2000);
                ret = SmscReadUrl(url);
            }
            while (ret == "" && ++i < 3);
        } catch (UnsupportedEncodingException e) {

        } catch (InterruptedException e) {

        }

        return ret.split(",");
    }

    private String SmscReadUrl(String url) {
        String line = "";
        String realUrl = url;
        String[] param = {};
        boolean isPost = (post || url.length() > 2000);

        if (isPost) {
            param = url.split("\\?", 2);
            realUrl = param[0];
        }

        try {
            URL u = new URL(realUrl);
            InputStream is;

            if (isPost) {
                URLConnection conn = u.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream(), charset);
                os.write(param[1]);
                os.flush();
                os.close();
                System.out.println("post");
                is = conn.getInputStream();
            } else {
                is = u.openStream();
            }

            InputStreamReader reader = new InputStreamReader(is, charset);

            int ch;
            while ((ch = reader.read()) != -1) {
                line += (char) ch;
            }

            reader.close();
        } catch (MalformedURLException e) { // Неверно урл, протокол...

        } catch (IOException e) {

        }

        return line;
    }

    private static String implode(String[] array, String delim) {
        String out = "";

        for (int i = 0; i < array.length; i++) {
            if (i != 0)
                out += delim;
            out += array[i];
        }

        return out;
    }
}
