package com.example.xmlserver.service;

import com.example.xmlserver.db.Database;
import com.example.xmlserver.util.BadWordsService;
import com.messaging.schema.*;
import java.util.Calendar;

public class MessageProcessor {
    public static String process(String xml) {
        try {
            MessageDocument doc = MessageDocument.Factory.parse(xml);
            MessageType message = doc.getMessage();
            String text = null;
            String user = null;

            if (message.isSetRequest()) {
                text = message.getRequest().getText();
                user = message.getRequest().getUser();
            }

            boolean bad = BadWordsService.containsBadWords(text);
            int code = bad ? 1 : 0;
            String reason = bad
                    ? "used inappropriate language"
                    : "success";

            Database.save(user, text, code);
            return buildResponse(code, reason);
        } catch (Exception e) {
            return buildResponse(1, "invalid xml");
        }
    }

    private static String buildResponse(int code, String reason) {

        MessageDocument doc = MessageDocument.Factory.newInstance();
        MessageType message = doc.addNewMessage();
        HeaderType header = message.addNewHeader();
        header.setTime(Calendar.getInstance());
        ResponseType response = message.addNewResponse();
        StatusType status = response.addNewStatus();
        status.setCode(code);
        status.setReason(reason);
        return doc.xmlText();
    }
}