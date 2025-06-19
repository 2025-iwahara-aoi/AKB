package com.example.Morihara.controller;

import com.example.Morihara.controller.Form.CommentForm;
import com.example.Morihara.controller.Form.MessageForm;
import com.example.Morihara.controller.Form.ReadForm;
import com.example.Morihara.controller.Form.UserForm;
import com.example.Morihara.repository.MessageRepository;
import com.example.Morihara.repository.UserRepository;
import com.example.Morihara.service.CommentService;
import com.example.Morihara.service.MessageService;
import com.example.Morihara.service.ReadService;
import com.example.Morihara.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.text.ParseException;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    MessageService messageService;

    @Autowired
    CommentService commentService;

    @Autowired
    ReadService readService;

    @Autowired
    HttpSession session;

    /*
     * 投稿内容表示処理
     */

    @GetMapping("/home")
    public ModelAndView showTop(
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate,
            @RequestParam(name = "category", required = false) String category,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            Model model) throws ParseException {
        ModelAndView mav = new ModelAndView();

        session = request.getSession();

        List<MessageForm> messageList = messageService.findByMessages(startDate, endDate, category);  // 変数名を統一
        for (MessageForm message : messageList) {
            List<CommentForm> comments = commentService.findCommentsByMessageId(message.getId());
            //MessageFormにList<ReadForm>をいれないと既読数が獲得できない
            List<ReadForm> reads = readService.findReadByMessageId(message.getId());


            message.setCount(reads.size());
            for(int i = 0; i < reads.size(); i++ ){

                ReadForm read = reads.get(i);
                UserForm userForm = userService.findById(read.getUserId());
                read.setAccount(userForm.getAccount());
            }
            message.setReads(reads);
            message.setComments(comments);

            if(reads.isEmpty()) {
                message.setReads(null);
            }
        }



        mav.setViewName("/home");
        mav.addObject("messages", messageList);
        model.addAttribute("start", startDate);
        model.addAttribute("end", endDate);
        model.addAttribute("category", category);
        // コメントを別に渡す必要はないです。messagesに含まれているので不要
        //コメント投稿に対するバリデーション表示でいる。
        mav.addObject("commentForm", new CommentForm());
        return mav;


    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // セッションを取得
        if (session != null) {
            session.invalidate(); // セッションを破棄
        }
        return "redirect:/"; // ログアウト成功後のリダイレクト
    }


    @DeleteMapping("/message/delete/{id}")
    public String deleteMessage(@PathVariable("id") int id) {
        //投稿をテーブルに格納
        messageService.deleteMessage(id);
        return "redirect:/home";
    }

//    @PostMapping("/task/updateStatus")
//    public String updateStatus(@RequestParam Long id,@RequestParam int status){
//        reportService.updateStatus(id, status);
//        return "redirect:/";
//    }
}
