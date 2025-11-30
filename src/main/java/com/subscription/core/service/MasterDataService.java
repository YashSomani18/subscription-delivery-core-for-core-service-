package com.subscription.core.service;

import com.subscription.core.dto.CategoryUpsertDTO;
import com.subscription.core.dto.DiscountTypeUpsertDTO;
import com.subscription.core.dto.ProductUpsertDTO;
import com.subscription.core.dto.SlotUpsertDTO;
import com.subscription.core.dto.ZoneUpsertDTO;
import com.subscription.core.dto.WarehouseUpsertDTO;
import com.subscription.core.dto.WarehouseZoneUpsertDTO;
import com.subscription.core.dto.UserAddressUpsertDTO;
import com.subscription.core.dto.UserContactUpsertDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MasterDataService {

    private final CategoryService categoryService;
    private final ProductService productService;
    private final DiscountTypeService discountTypeService;
    private final SlotService slotService;
    private final ZoneService zoneService;
    private final WarehouseService warehouseService;
    private final WarehouseZoneService warehouseZoneService;
    private final UserAddressService userAddressService;
    private final UserContactService userContactService;

    @Transactional
    public String upsertCategories(List<CategoryUpsertDTO> requests) {
        // Using forEach as requested to process one by one
        requests.forEach(categoryService::upsertCategory);
        return "Bulk upsert for categories completed successfully";
    }

    @Transactional
    public String upsertProducts(List<ProductUpsertDTO> requests) {
        requests.forEach(productService::upsertProduct);
        return "Bulk upsert for products completed successfully";
    }

    @Transactional
    public String upsertDiscountTypes(List<DiscountTypeUpsertDTO> requests) {
        requests.forEach(discountTypeService::upsertDiscountType);
        return "Bulk upsert for discount types completed successfully";
    }

    @Transactional
    public String upsertSlots(List<SlotUpsertDTO> requests) {
        requests.forEach(slotService::upsertSlot);
        return "Bulk upsert for slots completed successfully";
    }

    @Transactional
    public String upsertZones(List<ZoneUpsertDTO> requests) {
        requests.forEach(zoneService::upsertZone);
        return "Bulk upsert for zones completed successfully";
    }

    @Transactional
    public String upsertWarehouses(List<WarehouseUpsertDTO> requests) {
        requests.forEach(warehouseService::upsertWarehouse);
        return "Bulk upsert for warehouses completed successfully";
    }

    @Transactional
    public String upsertWarehouseZones(List<WarehouseZoneUpsertDTO> requests) {
        requests.forEach(warehouseZoneService::upsertWarehouseZone);
        return "Bulk upsert for warehouse zones completed successfully";
    }

    @Transactional
    public String upsertUserAddresses(List<UserAddressUpsertDTO> requests) {
        requests.forEach(userAddressService::upsertUserAddress);
        return "Bulk upsert for user addresses completed successfully";
    }

    @Transactional
    public String upsertUserContacts(List<UserContactUpsertDTO> requests) {
        requests.forEach(userContactService::upsertUserContact);
        return "Bulk upsert for user contacts completed successfully";
    }

    @Transactional(readOnly = true)
    public com.subscription.core.dto.CategorySearchResponseDTO searchCategories(
            com.subscription.core.dto.CategorySearchDTO dto) {
        return categoryService.searchCategories(dto);
    }

    @Transactional(readOnly = true)
    public com.subscription.core.dto.ProductSearchResponseDTO searchProducts(
            com.subscription.core.dto.ProductSearchDTO dto) {
        return productService.searchProducts(dto);
    }

    @Transactional(readOnly = true)
    public com.subscription.core.dto.DiscountTypeSearchResponseDTO searchDiscountTypes(
            com.subscription.core.dto.DiscountTypeSearchDTO dto) {
        return discountTypeService.searchDiscountTypes(dto);
    }

    @Transactional(readOnly = true)
    public com.subscription.core.dto.SlotSearchResponseDTO searchSlots(com.subscription.core.dto.SlotSearchDTO dto) {
        return slotService.searchSlots(dto);
    }

    @Transactional(readOnly = true)
    public com.subscription.core.dto.ZoneSearchResponseDTO searchZones(com.subscription.core.dto.ZoneSearchDTO dto) {
        return zoneService.searchZones(dto);
    }
}
