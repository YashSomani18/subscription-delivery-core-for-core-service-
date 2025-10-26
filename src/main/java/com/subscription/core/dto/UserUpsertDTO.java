package com.subscription.core.dto;

import com.subscription.core.enums.UserStatus;
import lombok.Data;

@Data
public class UserUpsertDTO {
    private String userName;
    private String password;
    private UserStatus status;
    private String phoneNo;
    private String emailId;
}
