package com.example.Morihara.controller.Form;

import com.example.Morihara.service.MessageService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.List;

@Getter
@Setter

public class MessageForm {
    private int id;

    @NotBlank(message = "件名を入力してください")
    @Pattern(regexp = "^(?!　*$).*$", message = "件名を入力してください")
    @Size(max = 30, message = "件名は30文字以内で入力してください")
    private  String title;

    @NotBlank(message = "本文を入力してください")
    @Pattern(regexp = "^(?!　*$).*$", message = "本文を入力してください")
    @Size(max = 1000, message = "本文は1000文字以内で入力してください")
    //@Pattern(regexp = "^[あ-んー]*$", message = "ひらがなで入力してください")

    private  String text;


    private  String str;


    @NotBlank(message = "カテゴリを入力してください")
    @Pattern(regexp = "^(?!　*$).*$", message = "カテゴリを入力してください")
    @Size(max = 10, message = "カテゴリは10文字以内で入力してください")
    private String category;

    private int userId;

    private Date createdDate;
    private Date updatedDate;

    private List<CommentForm> comments;

    private List<ReadForm> reads;

    private  int count;


}
