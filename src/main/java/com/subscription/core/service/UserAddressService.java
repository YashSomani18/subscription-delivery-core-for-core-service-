package com.subscription.core.service;

import com.subscription.core.dto.UserAddressUpsertDTO;
import com.subscription.core.entity.UserAddress;
import com.subscription.core.repository.UserAddressRepository;
import com.subscription.core.util.LambdaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAddressService {

    private final UserAddressRepository userAddressRepository;

    public String upsertUserAddress(UserAddressUpsertDTO dto) {
        log.info("[f:upsertUserAddress] Processing user address: {}", dto.getUserAddressId());

        boolean isUpdate = Optional.ofNullable(dto.getUserAddressId())
                .map(userAddressRepository::findById)
                .orElse(Optional.empty())
                .isPresent();

        UserAddress userAddress = Optional.ofNullable(dto.getUserAddressId())
                .map(userAddressRepository::findById)
                .orElse(Optional.empty())
                .map(existing -> {
                    updateUserAddress(existing, dto);
                    return existing;
                })
                .orElseGet(() -> createUserAddress(dto));

        userAddressRepository.save(userAddress);
        log.info("[f:upsertUserAddress] User address {} successfully",
                isUpdate ? "updated" : "created");

        return isUpdate ? "User address updated successfully" : "User address created successfully";
    }

    private UserAddress createUserAddress(UserAddressUpsertDTO dto) {
        return UserAddress.builder()
                .addressLine1(dto.getAddressLine1())
                .addressLine2(dto.getAddressLine2())
                .city(dto.getCity())
                .state(dto.getState())
                .postalCode(dto.getPostalCode())
                .country(dto.getCountry())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .microdata(dto.getMicrodata())
                .zoneId(dto.getZoneId())
                .status(dto.getStatus())
                .noOfOrdersPlaced(dto.getNoOfOrdersPlaced() != null ? dto.getNoOfOrdersPlaced() : 0)
                .build();
    }

    private void updateUserAddress(UserAddress userAddress, UserAddressUpsertDTO dto) {
        LambdaUtil.updateIfNotNull(dto.getAddressLine1(), userAddress::setAddressLine1);
        LambdaUtil.updateIfNotNull(dto.getAddressLine2(), userAddress::setAddressLine2);
        LambdaUtil.updateIfNotNull(dto.getCity(), userAddress::setCity);
        LambdaUtil.updateIfNotNull(dto.getState(), userAddress::setState);
        LambdaUtil.updateIfNotNull(dto.getPostalCode(), userAddress::setPostalCode);
        LambdaUtil.updateIfNotNull(dto.getCountry(), userAddress::setCountry);
        LambdaUtil.updateIfNotNull(dto.getLatitude(), userAddress::setLatitude);
        LambdaUtil.updateIfNotNull(dto.getLongitude(), userAddress::setLongitude);
        LambdaUtil.updateIfNotNull(dto.getMicrodata(), userAddress::setMicrodata);
        LambdaUtil.updateIfNotNull(dto.getZoneId(), userAddress::setZoneId);
        LambdaUtil.updateIfNotNull(dto.getStatus(), userAddress::setStatus);
        LambdaUtil.updateIfNotNull(dto.getNoOfOrdersPlaced(), userAddress::setNoOfOrdersPlaced);
    }
}
