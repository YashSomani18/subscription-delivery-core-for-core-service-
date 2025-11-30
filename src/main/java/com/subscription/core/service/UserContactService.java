package com.subscription.core.service;

import com.subscription.core.dto.UserContactUpsertDTO;
import com.subscription.core.entity.UserContact;
import com.subscription.core.repository.UserContactRepository;
import com.subscription.core.util.LambdaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserContactService {

    private final UserContactRepository userContactRepository;

    public String upsertUserContact(UserContactUpsertDTO dto) {
        log.info("[f:upsertUserContact] Processing user contact: {}", dto.getUserContactId());

        boolean isUpdate = Optional.ofNullable(dto.getUserContactId())
                .map(userContactRepository::findById)
                .orElse(Optional.empty())
                .isPresent();

        UserContact userContact = Optional.ofNullable(dto.getUserContactId())
                .map(userContactRepository::findById)
                .orElse(Optional.empty())
                .map(existing -> {
                    updateUserContact(existing, dto);
                    return existing;
                })
                .orElseGet(() -> createUserContact(dto));

        userContactRepository.save(userContact);
        log.info("[f:upsertUserContact] User contact {} successfully",
                isUpdate ? "updated" : "created");

        return isUpdate ? "User contact updated successfully" : "User contact created successfully";
    }

    private UserContact createUserContact(UserContactUpsertDTO dto) {
        return UserContact.builder()
                .phoneNo(dto.getPhoneNo())
                .emailId(dto.getEmailId())
                .optForEmail(dto.getOptForEmail())
                .optForWhatsapp(dto.getOptForWhatsapp())
                .optForSms(dto.getOptForSms())
                .status(dto.getStatus())
                .build();
    }

    private void updateUserContact(UserContact userContact, UserContactUpsertDTO dto) {
        LambdaUtil.updateIfNotNull(dto.getPhoneNo(), userContact::setPhoneNo);
        LambdaUtil.updateIfNotNull(dto.getEmailId(), userContact::setEmailId);
        LambdaUtil.updateIfNotNull(dto.getOptForEmail(), userContact::setOptForEmail);
        LambdaUtil.updateIfNotNull(dto.getOptForWhatsapp(), userContact::setOptForWhatsapp);
        LambdaUtil.updateIfNotNull(dto.getOptForSms(), userContact::setOptForSms);
        LambdaUtil.updateIfNotNull(dto.getStatus(), userContact::setStatus);
    }
}
