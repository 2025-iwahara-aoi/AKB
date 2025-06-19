package com.example.Morihara.controller;

import com.example.Morihara.controller.Form.CommentForm;
import com.example.Morihara.controller.Form.MessageForm;
import com.example.Morihara.controller.Form.UserForm;
import com.example.Morihara.service.CommentService;
import com.example.Morihara.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

@Controller
public class CommentController {
    @Autowired
    CommentService commentService;

    @Autowired
    MessageService messageService;
    @Autowired
    HttpSession session;

//    @GetMapping("/comment")
//    public ModelAndView newContent(HttpServletRequest request, HttpServletResponse response) {
//        session = request.getSession();
//        UserForm user = (UserForm) session.getAttribute("user");
//        ModelAndView mav = new ModelAndView();
//        CommentForm commentForm = new CommentForm();
//        mav.setViewName("/comment");
//        mav.addObject("commentInfo", commentForm);
//        return mav;
//    }

    @PostMapping("/addComment")
    public ModelAndView addComment(
            @Valid @ModelAttribute CommentForm commentForm,
            BindingResult result,
            HttpServletRequest request,
            Model model

    ) throws ParseException {
        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView();

            // homeで必要な投稿一覧を取得
            List<MessageForm> messageList = messageService.findByMessages(null, null, null);
            for (MessageForm message : messageList) {
                List<CommentForm> comments = commentService.findCommentsByMessageId(message.getId());
                message.setComments(comments);
            }

            mav.addObject("messages", messageList);
            model.addAttribute("commentForm", commentForm);
            mav.setViewName("home");
            model.addAttribute("errorMessageId", commentForm.getMessageId()); // どのメッセージのコメントかを記録
            return mav;
        }

        // セッションからログインユーザーを取得
        UserForm user = (UserForm) session.getAttribute("user");
        if (user == null) {
            return new ModelAndView("redirect:/home");
        }

//        // homeで必要な投稿一覧を取得
//        List<MessageForm> messageList = messageService.findByMessages(null, null, null);
//        for (MessageForm message : messageList) {
//            List<CommentForm> comments = commentService.findCommentsByMessageId(message.getId());
//            message.setComments(comments);
//        }
//        //NGワードの条件式
//        ModelAndView mav = new ModelAndView("home");
//        String str = commentForm.getText();
//
//        List<String> ngWords = Arrays.asList("死", "殺", "バカ");
//
//        //「：」は、ngWordsの中身を一つずつ取り出して、wordに入れる処理
//        for (String word : ngWords) {
//            if (str.contains(word)) {
//                result.rejectValue("text", "errorMessage", "不正なワードが含まれています");
//                mav.addObject("commentInfo", commentForm);
//                mav.addObject("messages", messageList);
//                mav.addObject("formModel", user);
//                return mav;
//            }
//        }

        // 正しい userId をセット
        commentForm.setUserId(user.getId());

        commentService.saveComment(commentForm);
        return new ModelAndView("redirect:/home");
    }

    @DeleteMapping("/comment/delete/{id}")
    public String deleteComment(@PathVariable("id") int id) {
        commentService.deleteComment(id);
        return "redirect:/home";
    }
}