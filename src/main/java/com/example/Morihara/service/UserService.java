package com.example.Morihara.service;

import com.example.Morihara.controller.Form.UserForm;
import com.example.Morihara.repository.UserRepository;
import com.example.Morihara.repository.entity.User;
import com.example.Morihara.util.TimeUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    //アカウント重複チェック用
    public boolean AccountDuB(String account) {
        List<User> results = userRepository.findByAccount(account);
        return !results.isEmpty();
    }


    //支店と部署の組み合わせチェック。
    public boolean BranchDepartmentComb(Integer branchId, Integer departmentId){
        //有効な組み合わせを力技解決
        Map<Integer, List<Integer>> Comb = new HashMap<>();
        Comb.put(1, List.of(1, 2));//本社、
        Comb.put(2, List.of(3, 4));//A支社,
        Comb.put(3, List.of(3, 4));
        Comb.put(4, List.of(3, 4));

        List<Integer> a = Comb.get(branchId);
        return a != null && a.contains(departmentId);

    }

    public List<UserForm>  findByAllUser(){
        List<User> userList = userRepository.findAllByOrderByIdAsc();
        List<UserForm> users = setUserForm(userList);
        return users;

    }

    public List<User> findUserById(int id){
        return userRepository.findByIdWithDepartmentAndBranch(id);
    }

    public UserForm findByAccount(String account){

        List<User> results = userRepository.findByAccount(account);
        List<UserForm> users = setUserForm(results);
        if(users.isEmpty()){
            return null;
        }
        return users.get(0);
    }


    private List<UserForm> setUserForm(List<User> results) {
        List<UserForm> users = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            UserForm user = new UserForm();
            User result = results.get(i);
            user.setId(result.getId());
            user.setAccount(result.getAccount());
            user.setName(result.getName());
            user.setPassword(result.getPassword());
            user.setBranchId(result.getBranchId());
            user.setDepartmentId(result.getDepartmentId());
            user.setIsStopped(result.getIsStopped());
            user.setBranch(result.getBranch());
            user.setDepartment(result.getDepartment());
            user.setCreatedDate(result.getCreatedDate());
            user.setUpdatedDate(result.getUpdatedDate());
            user.setLastLogin(result.getLastLogin());
            user.setReTimeText(TimeUtils.ReTime(result.getLastLogin()));
            users.add(user);
        }
        return users;
    }

    /*
     * レコード追加
     */
    public void saveUser(UserForm reqUser) {
        User saveUser = setUserEntity(reqUser);
        userRepository.save(saveUser);
    }
    /*
     * リクエストから取得した情報をEntityに設定
     */
    private User setUserEntity(UserForm reqUser) {
        String encodedPassword = passwordEncoder.encode(reqUser.getPassword());

        User report = new User();
        report.setId(reqUser.getId());
        report.setAccount(reqUser.getAccount());
        report.setPassword(encodedPassword);
        report.setName(reqUser.getName());
        report.setBranchId(reqUser.getBranchId());
        report.setDepartmentId(reqUser.getDepartmentId());
        report.setIsStopped(reqUser.getIsStopped());
        report.setLastLogin(reqUser.getLastLogin());
        return report;
    }

    @Transactional
    public void updateStatus(Integer id, int status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("指定されたidが見つかりません: ID=" + id));
        user.setIsStopped(status);
        user.setUpdatedDate(new Date());
        userRepository.save(user);
    }

    public void deleteUser(Integer id){

        userRepository.deleteById(id);
    }

    public List<User> findByIdWithDepartmentAndBranch(int id){
        return userRepository.findByIdWithDepartmentAndBranch(id);
    }

    public UserForm findById(int id){

        List<User> user = userRepository.findById(id);

        //URLパラメーターから存在しないidで情報をDBから探しに行ったときにnullで返す
        if(user == null){
            return null;
        }

        List<UserForm> userForm = setUserForm(user);

        return userForm.get(0);
    }

    @Transactional
    public void updateLastLogin(Integer userId) {
        Optional<User> opt = userRepository.findById(userId);
        if(opt.isPresent()) {
            User u = opt.get();
            u.setLastLogin(LocalDateTime.now());
            userRepository.save(u);
        }
    }


}
