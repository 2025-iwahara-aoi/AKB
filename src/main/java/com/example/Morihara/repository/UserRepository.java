package com.example.Morihara.repository;

import com.example.Morihara.controller.Form.UserForm;
import com.example.Morihara.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findByAccountAndPassword(String account, String password);
    @Query("""
            SELECT u FROM User u
            JOIN FETCH u.department
            JOIN FETCH u.branch
            WHERE u.id = :id
            """)
    List<User> findByIdWithDepartmentAndBranch(@Param("id") int id);

    User findById(int id);

    Optional<User> findByAccount(String account);
}
