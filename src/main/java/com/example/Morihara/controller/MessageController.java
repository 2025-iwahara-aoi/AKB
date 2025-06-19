package com.example.Morihara.controller;

import com.example.Morihara.controller.Form.MessageForm;
import com.example.Morihara.controller.Form.UserForm;
import com.example.Morihara.repository.entity.User;
import com.example.Morihara.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

@Controller
public class MessageController {
    @Autowired
    MessageService messageService;
    @Autowired
    HttpSession session;

    @GetMapping("/message")
    public ModelAndView newContent
            (HttpServletRequest request, HttpServletResponse response,
             RedirectAttributes redirectAttributes) {
        session = request.getSession();
        // セッションからユーザーオブジェクトを取得
        UserForm user = (UserForm) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessageForm", "ログインしてください");
            return new ModelAndView("redirect:/");
        }
        ModelAndView mav = new ModelAndView();
        // form用の空のentityを準備
        MessageForm messageForm = new MessageForm();
        // 画面遷移先を指定
        mav.setViewName("/message");
        mav.addObject("formModel", user);
        // 準備した空のFormを保管
        mav.addObject("messageInfo", messageForm);
        // mav.addObject("errorMessageForm", errorMessages);
        return mav;

    }

    @PostMapping("/addMessage")
    public ModelAndView addContent(
            HttpServletRequest request, HttpServletResponse response,
            @Valid
            @ModelAttribute("messageInfo") MessageForm messageForm,
            BindingResult result,
            Model model
    ) throws ParseException {
        session = request.getSession();
        UserForm user = (UserForm) session.getAttribute("user"); // セッションから再取得

        //NGワードの条件式
        List<String> ngWords = Arrays.asList("死", "殺", "バカ","よこすか");

        //「：」は、ngWordsの中身を一つずつ取り出して、wordに入れる処理
        for (String word : ngWords) {
            if (messageForm.getText().contains(word)) {
                result.rejectValue("text", "errorMessage", "本文にNGワードが含まれています");
            }
            if (messageForm.getTitle().contains(word)) {
                result.rejectValue("title", "errorMessage", "件名にNGワードが含まれています");
            }
            if (messageForm.getCategory().contains(word)) {
                result.rejectValue("category", "errorMessage", "カテゴリにNGワードが含まれています");
            }
        }
        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView("message");
            mav.addObject("messageInfo", messageForm);
            mav.addObject("formModel", user);
            // errorsはバインディング済みなので自動的にビューへ渡る
            return mav;
        }

        messageForm.setUserId(user.getId());
        // 投稿をテーブルに格納
        messageService.saveMessage(messageForm);
        // rootへリダイレクト
        return new ModelAndView("redirect:/home");
    }
}
