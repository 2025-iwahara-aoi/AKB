package com.example.Morihara.controller;

import com.example.Morihara.controller.Form.UserForm;
import com.example.Morihara.repository.entity.User;
import com.example.Morihara.service.UserService;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
public class UserEditController {
    @Autowired
    UserService userService;
    @Autowired
    HttpSession session;

    /*("/userEdit/id={id}")の（id=）をつけずに｛"/userEdit/{id}"｝にしてしまうと
    　 if(!StringUtils.isBlank(strId) && strId.matches("^[0-9]*$"))が機能しなくなる
     */
    @GetMapping("/userEdit/id={id}")
    public ModelAndView newContent(
            @PathVariable("id") String strId,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) throws IOException {
        ModelAndView mav = new ModelAndView();

        //URLパターンチェック
        UserForm user = null;
        if(!StringUtils.isBlank(strId) && strId.matches("^[0-9]*$")) {
            user = userService.findById(Integer.parseInt(strId));
            // 準備した空のFormを保管
            mav.addObject("formModel", user);
        }
        if(user == null) {
            redirectAttributes.addFlashAttribute("errorMessageForm", "不正なパラメータが入力されました");
            return new ModelAndView("redirect:/management");
        }

        //ログインユーザ情報チェック
        session = request.getSession();
        UserForm sessionUser = (UserForm) session.getAttribute("user");

        //FilterConfig及びLoginFilterの機能の代替
        //"userEdit/{id}"の{id}の部分がFilterConfigで記載方法がない
        if(sessionUser == null){
            session.setAttribute("errorMessageForm", "ログインしてください");
            return new ModelAndView("redirect:/");
        }

        List<User> users = userService.findByIdWithDepartmentAndBranch(sessionUser.getId());
        User userInfoForm =  users.get(0);

        if(userInfoForm.getDepartment().getId() != 1 && userInfoForm.getDepartmentId() != 1){
            redirectAttributes.addFlashAttribute("errorMessageForm", "不正なパラメータが入力されました");
            return new ModelAndView("redirect:/management");
        }
        // 画面遷移先を指定
        mav.setViewName("/userEdit");
        // 準備した空のFormを保管
        mav.addObject("branchOptions", getBranchOptions());
        mav.addObject("departmentOptions", getDepartmentOptions());
        // mav.addObject("errorMessageForm", errorMessages);
        return mav;
    }

    // コントローラー内に選択肢を返すメソッド プルダウンで使用
    private Map<Integer, String> getBranchOptions() {
        Map<Integer, String> options = new LinkedHashMap<>();
        options.put(1, "本社");
        options.put(2, "A支社");
        options.put(3, "B支社");
        options.put(4, "C支社");
        return options;
    }

    private Map<Integer, String> getDepartmentOptions() {
        Map<Integer, String> options = new LinkedHashMap<>();
        options.put(1, "総務人事部");
        options.put(2, "情報管理部");
        options.put(3, "営業部");
        options.put(4, "技術部");
        return options;
    }

    @PostMapping("/update")
    public String updateUser(
            @Validated(UserForm.EditGroup.class)
            @Valid @ModelAttribute("formModel") UserForm userForm,
            BindingResult result,
            Model model){
        // パスワード確認チェック
        if (!result.hasFieldErrors("passwordConfirm") &&
                !userForm.getPassword().equals(userForm.getPasswordConfirm())) {
            result.rejectValue("passwordConfirm", null, "パスワードとパスワード確認が一致しません");
        }
        UserForm userPass = userService.findByAccount(userForm.getAccount());
        // アカウント重複チェック
        if (userPass.getId() != userForm.getId()) {
            result.rejectValue("account", "duplicate", "アカウントが重複しています");
        }

        // 支社と部署の組み合わせチェック
        if (!userService.BranchDepartmentComb(userForm.getBranchId(), userForm.getDepartmentId())) {
            result.rejectValue("branchId", "mismatch","支社と部署の組み合わせが不正です");
        }

        if(userForm.getPassword().matches("^[a-zA-Z]+$") && (userForm.getPassword().length() >= 6 && userForm.getPassword().length()<= 20)){
            userService.saveUser(userForm);
            return "redirect:/management";
        }



        if (!userForm.getPassword().isBlank() &&!userForm.getPassword().matches("^[a-zA-Z]+$")){
            result.rejectValue("password", "duplicate","パスワードは半角かつ6文字以上20文字以内で入力してください");
        }

        if((!userForm.getPassword().isBlank() && userForm.getPassword().length() < 6) || userForm.getPassword().length() > 20){
            result.rejectValue("password", "duplicate","パスワードは6文字以上20文字以内で入力してください");
        }

        if (result.hasErrors()) {
            model.addAttribute("branchOptions", getBranchOptions());
            model.addAttribute("departmentOptions", getDepartmentOptions());
            return "userEdit"; // フォワードで遷移
        }

    userForm.setPassword(userPass.getPassword());

        userService.saveUser(userForm);
        return "redirect:/management";
    }
}
