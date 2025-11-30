package com.subscription.core.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginEvent {
    private String userId;
    private String email;
    private ZonedDateTime loginTime;
    private String deviceInfo;
}
