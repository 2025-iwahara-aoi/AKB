package com.example.Morihara.controller.Form;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ReadForm {

    private int id;
    private int userId;
    private int messageId;
    private int status;
    private Date readTime;

    private String account;

}
