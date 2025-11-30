package com.subscription.core.service;

import com.subscription.core.dto.UserAddressResponseDTO;
import com.subscription.core.dto.UserAddressUpsertDTO;
import com.subscription.core.dto.UserContactResponseDTO;
import com.subscription.core.dto.UserContactUpsertDTO;
import com.subscription.core.dto.UserProfileResponseDTO;
import com.subscription.core.dto.UserUpsertDTO;
import com.subscription.core.entity.User;
import com.subscription.core.entity.UserAddress;
import com.subscription.core.entity.UserContact;
import com.subscription.core.enums.ContactStatus;
import com.subscription.core.exception.ResourceNotFoundException;
import com.subscription.core.repository.UserAddressRepository;
import com.subscription.core.repository.UserContactRepository;
import com.subscription.core.repository.UserRepository;
import com.subscription.core.util.LambdaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final UserContactRepository userContactRepository;
    
    /**
     * Creates or updates a user based on the provided request.
     *
     * @param userRequest The user data to create or update
     * @return Success message indicating if user was created or updated
     */
    @Transactional
    public String upsertUser(UserUpsertDTO userRequest) {
        log.info("[f:upsertUser] Processing user upsert: {}", userRequest.getEmailId());
        
        Optional<User> existingUser = Optional.ofNullable(userRequest.getEmailId())
                .flatMap(userRepository::findByEmailId);
        
        User user = existingUser.map(u -> {
            updateUser(u, userRequest);
            return u;
        }).orElseGet(() -> createUser(userRequest));
        
        User savedUser = userRepository.save(user);
        
        if (!CollectionUtils.isEmpty(userRequest.getAddresses())) {
            processAddresses(savedUser.getUserId(), userRequest.getAddresses());
        }
        
        log.info("[f:upsertUser] User {} successfully with ID: {}", 
                existingUser.isPresent() ? "updated" : "created", savedUser.getUserId());
        return existingUser.isPresent() ? "User updated successfully" : "User created successfully";
    }

    private User createUser(UserUpsertDTO dto) {
        User user = new User();
        user.setUserName(dto.getUserName());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        user.setStatus(dto.getStatus());
        user.setPhoneNo(dto.getPhoneNo());
        user.setEmailId(dto.getEmailId());
        return user;
    }

    private void updateUser(User user, UserUpsertDTO userRequest) {
        LambdaUtil.updateIfNotNull(userRequest.getUserName(), user::setUserName);
        LambdaUtil.updateIfNotNull(userRequest.getPassword(), user::setPassword);
        LambdaUtil.updateIfNotNull(userRequest.getRole(), user::setRole);
        LambdaUtil.updateIfNotNull(userRequest.getStatus(), user::setStatus);
        LambdaUtil.updateIfNotNull(userRequest.getPhoneNo(), user::setPhoneNo);
        LambdaUtil.updateIfNotNull(userRequest.getEmailId(), user::setEmailId);
    }
    
    private void processAddresses(String userId, List<UserAddressUpsertDTO> addressDTOs) {
        addressDTOs.forEach(addressDTO -> {
            UserAddress address = upsertAddress(addressDTO, userId);
            UserAddress savedAddress = userAddressRepository.save(address);
            
            if (!CollectionUtils.isEmpty(addressDTO.getContacts())) {
                processContacts(userId, savedAddress.getUserAddressId(), addressDTO.getContacts());
            }
        });
    }
    
    private UserAddress upsertAddress(UserAddressUpsertDTO dto, String userId) {
        return Optional.ofNullable(dto.getUserAddressId())
                .flatMap(userAddressRepository::findByUserAddressId)
                .map(existing -> {
                    updateAddress(existing, dto, userId);
                    return existing;
                })
                .orElseGet(() -> createAddress(dto, userId));
    }
    
    private UserAddress createAddress(UserAddressUpsertDTO dto, String userId) {
        UserAddress address = new UserAddress();
        address.setUserId(userId);
        address.setAddressLine1(dto.getAddressLine1());
        address.setAddressLine2(dto.getAddressLine2());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setPostalCode(dto.getPostalCode());
        address.setCountry(dto.getCountry());
        address.setLatitude(dto.getLatitude());
        address.setLongitude(dto.getLongitude());
        address.setMicrodata(dto.getMicrodata());
        address.setZoneId(dto.getZoneId());
        address.setStatus(Objects.nonNull(dto.getStatus()) ? dto.getStatus() : UserAddress.AddressStatus.ACTIVE);
        address.setNoOfOrdersPlaced(Objects.nonNull(dto.getNoOfOrdersPlaced()) ? dto.getNoOfOrdersPlaced() : 0);
        return address;
    }
    
    private void updateAddress(UserAddress address, UserAddressUpsertDTO dto, String userId) {
        address.setUserId(userId);
        LambdaUtil.updateIfNotNull(dto.getAddressLine1(), address::setAddressLine1);
        LambdaUtil.updateIfNotNull(dto.getAddressLine2(), address::setAddressLine2);
        LambdaUtil.updateIfNotNull(dto.getCity(), address::setCity);
        LambdaUtil.updateIfNotNull(dto.getState(), address::setState);
        LambdaUtil.updateIfNotNull(dto.getPostalCode(), address::setPostalCode);
        LambdaUtil.updateIfNotNull(dto.getCountry(), address::setCountry);
        LambdaUtil.updateIfNotNull(dto.getLatitude(), address::setLatitude);
        LambdaUtil.updateIfNotNull(dto.getLongitude(), address::setLongitude);
        LambdaUtil.updateIfNotNull(dto.getMicrodata(), address::setMicrodata);
        LambdaUtil.updateIfNotNull(dto.getZoneId(), address::setZoneId);
        LambdaUtil.updateIfNotNull(dto.getStatus(), address::setStatus);
        LambdaUtil.updateIfNotNull(dto.getNoOfOrdersPlaced(), address::setNoOfOrdersPlaced);
    }
    
    private void processContacts(String userId, String userAddressId, List<UserContactUpsertDTO> contactDTOs) {
        contactDTOs.forEach(contactDTO -> {
            UserContact contact = upsertContact(contactDTO, userId, userAddressId);
            userContactRepository.save(contact);
        });
    }
    
    private UserContact upsertContact(UserContactUpsertDTO dto, String userId, String userAddressId) {
        return Optional.ofNullable(dto.getUserContactId())
                .flatMap(userContactRepository::findByUserContactId)
                .map(existing -> {
                    updateContact(existing, dto, userId, userAddressId);
                    return existing;
                })
                .orElseGet(() -> createContact(dto, userId, userAddressId));
    }
    
    private UserContact createContact(UserContactUpsertDTO dto, String userId, String userAddressId) {
        UserContact contact = new UserContact();
        contact.setUserId(userId);
        contact.setUserAddressId(userAddressId);
        contact.setPhoneNo(dto.getPhoneNo());
        contact.setEmailId(dto.getEmailId());
        contact.setOptForEmail(Objects.nonNull(dto.getOptForEmail()) ? dto.getOptForEmail() : false);
        contact.setOptForWhatsapp(Objects.nonNull(dto.getOptForWhatsapp()) ? dto.getOptForWhatsapp() : false);
        contact.setOptForSms(Objects.nonNull(dto.getOptForSms()) ? dto.getOptForSms() : false);
        contact.setStatus(Objects.nonNull(dto.getStatus()) ? dto.getStatus() : ContactStatus.ACTIVE);
        return contact;
    }
    
    private void updateContact(UserContact contact, UserContactUpsertDTO dto, String userId, String userAddressId) {
        contact.setUserId(userId);
        contact.setUserAddressId(userAddressId);
        LambdaUtil.updateIfNotNull(dto.getPhoneNo(), contact::setPhoneNo);
        LambdaUtil.updateIfNotNull(dto.getEmailId(), contact::setEmailId);
        LambdaUtil.updateIfNotNull(dto.getOptForEmail(), contact::setOptForEmail);
        LambdaUtil.updateIfNotNull(dto.getOptForWhatsapp(), contact::setOptForWhatsapp);
        LambdaUtil.updateIfNotNull(dto.getOptForSms(), contact::setOptForSms);
        LambdaUtil.updateIfNotNull(dto.getStatus(), contact::setStatus);
    }
    
    /**
     * Retrieves user profile with addresses and contacts.
     *
     * @param userId The user ID
     * @return User profile response DTO
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public UserProfileResponseDTO getUserProfile(String userId) {
        log.info("[f:getUserProfile] Retrieving profile for user: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        List<UserAddress> addresses = userAddressRepository.findByUserId(userId);
        List<UserAddressResponseDTO> addressDTOs = addresses.stream()
                .map(this::mapAddressToDTO)
                .collect(Collectors.toList());
        
        return UserProfileResponseDTO.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .role(user.getRole())
                .status(user.getStatus())
                .phoneNo(user.getPhoneNo())
                .emailId(user.getEmailId())
                .addresses(addressDTOs)
                .build();
    }
    
    private UserAddressResponseDTO mapAddressToDTO(UserAddress address) {
        List<UserContact> contacts = userContactRepository.findByUserAddressId(address.getUserAddressId());
        
        List<UserContactResponseDTO> contactDTOs = contacts.stream()
                .map(this::mapContactToDTO)
                .collect(Collectors.toList());
        
        return UserAddressResponseDTO.builder()
                .userAddressId(address.getUserAddressId())
                .userId(address.getUserId())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .microdata(address.getMicrodata())
                .zoneId(address.getZoneId())
                .status(address.getStatus())
                .noOfOrdersPlaced(address.getNoOfOrdersPlaced())
                .contacts(contactDTOs)
                .build();
    }
    
    private UserContactResponseDTO mapContactToDTO(UserContact contact) {
        return UserContactResponseDTO.builder()
                .userContactId(contact.getUserContactId())
                .userAddressId(contact.getUserAddressId())
                .userId(contact.getUserId())
                .phoneNo(contact.getPhoneNo())
                .emailId(contact.getEmailId())
                .optForEmail(contact.getOptForEmail())
                .optForWhatsapp(contact.getOptForWhatsapp())
                .optForSms(contact.getOptForSms())
                .status(contact.getStatus())
                .build();
    }
    
    /**
     * Updates an existing user address.
     *
     * @param addressId The address ID
     * @param dto The address update data
     * @return Success message
     * @throws ResourceNotFoundException if address not found
     */
    @Transactional
    public String updateAddress(String addressId, UserAddressUpsertDTO dto) {
        log.info("[f:updateAddress] Updating address: {}", addressId);
        
        UserAddress address = userAddressRepository.findByUserAddressId(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));
        
        updateAddress(address, dto, address.getUserId());
        userAddressRepository.save(address);
        
        log.info("[f:updateAddress] Address updated successfully: {}", addressId);
        return "Address updated successfully";
    }
    
    /**
     * Updates an existing user contact.
     *
     * @param contactId The contact ID
     * @param dto The contact update data
     * @return Success message
     * @throws ResourceNotFoundException if contact not found
     */
    @Transactional
    public String updateContact(String contactId, UserContactUpsertDTO dto) {
        log.info("[f:updateContact] Updating contact: {}", contactId);
        
        UserContact contact = userContactRepository.findByUserContactId(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with id: " + contactId));
        
        updateContact(contact, dto, contact.getUserId(), contact.getUserAddressId());
        userContactRepository.save(contact);
        
        log.info("[f:updateContact] Contact updated successfully: {}", contactId);
        return "Contact updated successfully";
    }
}
