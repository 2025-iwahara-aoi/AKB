package com.example.Morihara.service;

import com.example.Morihara.controller.Form.MessageForm;
import com.example.Morihara.controller.Form.UserForm;
import com.example.Morihara.repository.MessageRepository;
import com.example.Morihara.repository.entity.Message;
import com.example.Morihara.repository.entity.User;
import com.example.Morihara.util.TimeUtils;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    @Autowired
    MessageRepository messageRepository;

    public void saveMessage(MessageForm reqMessage) {
        Message saveMessage = setMessageEntity(reqMessage);
        messageRepository.save(saveMessage);
    }
    private Message setMessageEntity(MessageForm reqMessage) {
        Message message = new Message();
        message.setId(reqMessage.getId());
        message.setTitle(reqMessage.getTitle());
        message.setText(reqMessage.getText());
        message.setCategory(reqMessage.getCategory());
        message.setUserId(reqMessage.getUserId());
        message.setCreatedDate(reqMessage.getCreatedDate());
        message.setUpdatedDate(reqMessage.getUpdatedDate());
        return message;
    }
    public List<MessageForm> findByMessages(String startDate, String endDate, String category) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String StrStartDate = "2020-01-01 00:00:00";
        String StrEndDate = "2100-12-31 23:59:59";

        if (!StringUtils.isBlank(startDate)) {
            StrStartDate = startDate + " 00:00:00";
        }
        if (!StringUtils.isBlank(endDate)) {
            StrEndDate = endDate + " 23:59:59";
        }

        Date StrDate = df.parse(StrStartDate);
        Date EndDate = df.parse(StrEndDate);

        if(StringUtils.isBlank(category)){
            List<Message> results = messageRepository.findByCreatedDateBetweenOrderByCreatedDateDesc(StrDate, EndDate);
            return setMessageForm(results);
        } else {
            List<Message> results = messageRepository.findByCreatedDateBetweenAndCategoryContainingOrderByCreatedDateDesc(StrDate, EndDate, category);
            return setMessageForm(results);
        }


    }
    private List<MessageForm> setMessageForm(List<Message> results) {
        List<MessageForm> messages = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            MessageForm message = new MessageForm();
            Message result = results.get(i);
            message.setId(result.getId());
            message.setTitle(result.getTitle());
            message.setText(result.getText());
            message.setCategory(result.getCategory());
            message.setUserId(result.getUserId());
            message.setCreatedDate(result.getCreatedDate());
            message.setUpdatedDate(result.getUpdatedDate());
            message.setReTimeText(TimeUtils.ReTime(result.getCreatedDate()));
            messages.add(message);
        }
        return messages;
    }
    public void deleteMessage(Integer id){
        messageRepository.deleteById(id);
    }
}
