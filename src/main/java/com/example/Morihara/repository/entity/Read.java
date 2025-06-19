package com.example.Morihara.repository.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "reads")
@Data

@Getter
@Setter
public class Read {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private int userId;

    @Column
    private int messageId;

    @Column
    private int status;

    @Column(insertable = false, updatable = false)
    private Date readTime;
}
