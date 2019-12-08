package com.gmail.taskmanager.sms;

import com.gmail.taskmanager.dto.TaskToNotifyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

@Service
public class SMSCSender implements SmsSender {
    private static final String SMSC_LOGIN = "taskManager";     // логин клиента
    private static final String SMSC_PASSWORD = "cek9sxyHXKekFQYS";  // пароль или MD5-хеш пароля в нижнем регистре
    private static final boolean SMSC_HTTPS = false;         // использовать HTTPS протокол
    private static final String SMSC_CHARSET = "utf-8";       // кодировка сообщения: koi8-r, windows-1251 или utf-8 (по умолчанию)
    private static final boolean SMSC_DEBUG = true;         // флаг отладки(default = false)
    private static final boolean SMSC_POST = false;         // Использовать метод POST
    private int translit = 0; //1 - переклад в translit
    private String time = "";
    private String id = "";
    private int format = 0;
    private String sender = "";
    private String query = "";
    @Autowired
    private Transcriptor transcriptor;

    public SMSCSender() {
    }

    public void sendSms(TaskToNotifyDTO task) {
        String[] formats = {"", "flash=1", "push=1", "hlr=1", "bin=1", "bin=2", "ping=1"};
        String[] result = {};
        String textMessage = "Shanovnyi " + transcriptor.getTranslit(task.getName()) + "!" + '\n' +
                "Ne zabudte: " + transcriptor.getTranslit(task.getDescription()) + '\n' +
                "Chas zaversheniya:" + task.getDateFinish() + '\n' +
                "Task Manager";

        try {
            result = SmscSendCmd("send", "cost=3&phones=" + URLEncoder.encode(task.getPhone(), SMSC_CHARSET)
                    + "&mes=" + URLEncoder.encode(textMessage, SMSC_CHARSET)
                    + "&translit=" + translit + "&id=" + id + (format > 0 ? "&" + formats[format] : "")
                    + (sender == "" ? "" : "&sender=" + URLEncoder.encode(sender, SMSC_CHARSET))
                    + (time == "" ? "" : "&time=" + URLEncoder.encode(time, SMSC_CHARSET))
                    + (query == "" ? "" : "&" + query));
        } catch (UnsupportedEncodingException e) {

        }

        if (SMSC_DEBUG) {
            if (Integer.parseInt(result[1]) > 0) {
                System.out.println("Повідомлення відправлено успешно. ID: " + result[0] +
                        ", всього SMS: " + result[1] +
                        ", вартість: " + result[2] +
                        ", баланс: " + result[3]);
            } else {
                System.out.print("Помилка №" + Math.abs(Integer.parseInt(result[1])));
                System.out.println(Integer.parseInt(result[0]) > 0 ? (", ID: " + result[0]) : "");
            }
        }
    }

    public String[] getSmsCost(String phones, String message, int translit, int format, String sender, String query) {
        String[] formats = {"", "flash=1", "push=1", "hlr=1", "bin=1", "bin=2", "ping=1"};
        String[] result = {};

        try {
            result = SmscSendCmd("send", "cost=1&phones=" + URLEncoder.encode(phones, SMSC_CHARSET)
                    + "&mes=" + URLEncoder.encode(message, SMSC_CHARSET)
                    + "&translit=" + translit + (format > 0 ? "&" + formats[format] : "")
                    + (sender == "" ? "" : "&sender=" + URLEncoder.encode(sender, SMSC_CHARSET))
                    + (query == "" ? "" : "&" + query));
        } catch (UnsupportedEncodingException e) {

        }
        // (cost, cnt) или (0, -error)

        if (SMSC_DEBUG) {
            if (Integer.parseInt(result[1]) > 0) {
                System.out.println("Вартість розсилки: " + result[0] + ", Всього SMS: " + result[1]);
            } else {
                System.out.print("Помилка №" + Math.abs(Integer.parseInt(result[1])));
            }
        }

        return result;
    }

    public String[] getStatus(int id, String phone, int all) {
        String[] result = {};
        String tmp;

        try {
            result = SmscSendCmd("status", "phone=" + URLEncoder.encode(phone, SMSC_CHARSET) + "&id=" + id + "&all=" + all);

            if (SMSC_DEBUG) {
                if (result[1] != "" && Integer.parseInt(result[1]) >= 0) {
                    java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(Integer.parseInt(result[1]));
                    System.out.println("Статус SMS = " + result[0]);
                } else
                    System.out.println("Помилка №" + Math.abs(Integer.parseInt(result[1])));
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

        if (SMSC_DEBUG) {
            if (result.length == 1)
                System.out.println("Сума на рахунку: " + result[0]);
            else
                System.out.println("Помилка №" + Math.abs(Integer.parseInt(result[1])));
        }

        return result.length == 2 ? "" : result[0];
    }

    private String[] SmscSendCmd(String cmd, String arg) {
        String ret = ",";

        try {
            String url = (SMSC_HTTPS ? "https" : "http") + "://smsc.ru/sys/" + cmd + ".php?login=" + URLEncoder.encode(SMSC_LOGIN, SMSC_CHARSET)
                    + "&psw=" + URLEncoder.encode(SMSC_PASSWORD, SMSC_CHARSET)
                    + "&fmt=1&charset=" + SMSC_CHARSET + "&" + arg;

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
        boolean isPost = (SMSC_POST || url.length() > 2000);

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
                OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream(), SMSC_CHARSET);
                os.write(param[1]);
                os.flush();
                os.close();
                System.out.println("post");
                is = conn.getInputStream();
            } else {
                is = u.openStream();
            }

            InputStreamReader reader = new InputStreamReader(is, SMSC_CHARSET);

            int ch;
            while ((ch = reader.read()) != -1) {
                line += (char) ch;
            }

            reader.close();
        } catch (MalformedURLException e) {

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
