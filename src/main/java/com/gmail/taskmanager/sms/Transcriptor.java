package com.gmail.taskmanager.sms;

import com.ibm.icu.text.Transliterator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Transcriptor {
    @Value("${transcriptor.languages}")
    private String languages;

    public String getTranslit(String text) {
        Transliterator toLatinTrans = Transliterator.getInstance(languages);
        return toLatinTrans.transliterate(text);
    }
}
