package com.example.Morihara.service;

import com.example.Morihara.controller.Form.ReadForm;
import com.example.Morihara.repository.ReadRepository;
import com.example.Morihara.repository.entity.Read;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReadService {
    @Autowired
    ReadRepository readRepository;

    public void saveReadForm (ReadForm reqReadForm){
        Read saveRead = setReadEntity(reqReadForm);
        readRepository.save(saveRead);
    }

    private Read setReadEntity(ReadForm reqReadForm) {
        Read read = new Read();
        read.setId(reqReadForm.getId());
        read.setUserId(reqReadForm.getUserId());
        read.setMessageId(reqReadForm.getMessageId());
        read.setStatus(1);

        return read;
    }
    //HomeController
    public List<ReadForm> findReadByMessageId(int messageId){
        List<Read> results = readRepository.findByMessageId(messageId);
        return setReadForm(results);
    }
    private List<ReadForm> setReadForm(List<Read> results) {
        List<ReadForm> reads = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            ReadForm read = new ReadForm();
            Read result = results.get(i);

            read.setId(result.getId());
            read.setUserId(result.getUserId());
            read.setMessageId(result.getMessageId());
            read.setStatus(result.getStatus());
            read.setReadTime(result.getReadTime());

            reads.add(read);
        }
        return reads;
    }

    public  boolean UserIdDuB(int userId, int messageId) {
        List<Read> results = readRepository.findByUserId(userId);
        if (results.isEmpty()) {
            return true;
        }

        for (int i = 0; i < results.size(); i++){
            Read read = results.get(i);
            if(messageId == read.getMessageId()) {
                return false;
            }
        }
        return true;
    }
    public boolean MessageIdDuB(int messageId, int userId) {
        List<Read> results = readRepository.findByMessageId(messageId);
        if (results.isEmpty()) {
            return true;
        }

        for (int i = 0; i < results.size(); i++){
            Read read = results.get(i);
            if(userId == read.getUserId()) {
                return false;
            }
        }
        return true;
    }
}
