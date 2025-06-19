package com.example.Morihara.repository;

import com.example.Morihara.repository.entity.Comment;
import com.example.Morihara.repository.entity.Read;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadRepository extends JpaRepository<Read, Integer> {
    List<Read> findByMessageId(int messageId);

    List<Read> findByUserId(int userId);
}
