package com.example.Morihara.controller;

import com.example.Morihara.controller.Form.ReadForm;
import com.example.Morihara.service.ReadService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ReadController {
    @Autowired
    HttpSession session;
    @Autowired
    ReadService readService;

    @PostMapping("/read")
    public String read (ReadForm read, RedirectAttributes redirectAttributes) {
        // 既読重複チェック
        if ((!readService.UserIdDuB(read.getUserId(), read.getMessageId())) && (!readService.MessageIdDuB(read.getMessageId(), read.getUserId()))) {
            return "redirect:/home";
        }
        readService.saveReadForm(read);
        return "redirect:/home";
    }
}

