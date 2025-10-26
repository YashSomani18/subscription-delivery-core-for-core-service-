package com.subscription.core.service;

import com.subscription.core.dto.UserUpsertDTO;
import com.subscription.core.entity.User;
import com.subscription.core.repository.UserRepository;
import com.subscription.core.util.LambdaUtil;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String upsertUser(UserUpsertDTO userRequest) {
        boolean isUpdate = Optional.ofNullable(userRequest.getEmailId())
            .map(userRepository::findByEmailId)
            .orElse(Optional.empty())
            .isPresent();
            
        User user = Optional.ofNullable(userRequest.getEmailId())
            .map(userRepository::findByEmailId)
            .orElse(Optional.empty())
            .map(existingUser -> {
                updateUser(existingUser, userRequest);
                return existingUser;
            })
            .orElse(createUser(userRequest));
        
        userRepository.save(user);
        return isUpdate ? "User updated successfully" : "User created successfully";
    }

    private User createUser(UserUpsertDTO dto) {
        User user = new User();
        user.setUserName(dto.getUserName());
        user.setPassword(dto.getPassword());
        user.setStatus(dto.getStatus());
        user.setPhoneNo(dto.getPhoneNo());
        user.setEmailId(dto.getEmailId());
        return user;
    }

    private void updateUser(User user, UserUpsertDTO userRequest) {
        LambdaUtil.updateIfNotNull(userRequest.getUserName(), user::setUserName);
        LambdaUtil.updateIfNotNull(userRequest.getPassword(), user::setPassword);
        LambdaUtil.updateIfNotNull(userRequest.getStatus(), user::setStatus);
        LambdaUtil.updateIfNotNull(userRequest.getPhoneNo(), user::setPhoneNo);
        LambdaUtil.updateIfNotNull(userRequest.getEmailId(), user::setEmailId);
    }
}
