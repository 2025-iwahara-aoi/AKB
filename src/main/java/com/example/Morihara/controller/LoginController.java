package com.example.Morihara.controller;

import com.example.Morihara.controller.Form.UserForm;
import com.example.Morihara.repository.entity.User;
import com.example.Morihara.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class LoginController {
    @Autowired
    UserService userService;
    @Autowired
    HttpSession session;
    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping
    public ModelAndView login() {

        ModelAndView mav = new ModelAndView();
        UserForm userForm = new UserForm();
        // ログインフィルターで書いたエラー文をここで受け取る。
        String errorMessage = (String) session.getAttribute("errorMessageForm");
        //一度だけ表示させるコード
        session.removeAttribute("errorMessageForm");
        // 画面遷移先を指定
        mav.setViewName("login");
        // 準備した空のFormを保管
        mav.addObject("formModel", userForm);
        mav.addObject("errorMessageForm", errorMessage);
        return mav;
    }

  @PostMapping("/loading")
  //@Validated(LoginGroup.class)←これが、Fromでバリデーションしてるグループを使うって意味
    public ModelAndView addContent(@Validated(UserForm.LoginGroup.class)
            @Valid @ModelAttribute("formModel") UserForm userForm,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model){
        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView("/login");
            mav.addObject("formModel", userForm);
            return mav;
        }
      // 投稿をテーブルに格納
      UserForm userInfo = userService.findByAccount(userForm.getAccount());

        //DBから取得した情報がnullの時またはアカウントが停止中の時にエラーを表示させる
      if (userInfo == null || userInfo.getIsStopped() == 1 || !passwordEncoder.matches(userForm.getPassword(), userInfo.getPassword())) {
          //フラッシュメッセージをセット
          model.addAttribute("errorMessageForm", "ログインに失敗しました");
          model.addAttribute("formModel", userForm);
          return new ModelAndView("/login");
        }
        session.setAttribute("user", userInfo);
        // rootへリダイレクト
        return new ModelAndView("redirect:/home");
    }
}
