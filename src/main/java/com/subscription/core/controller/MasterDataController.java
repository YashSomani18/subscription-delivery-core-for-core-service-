package com.subscription.core.controller;

import com.subscription.core.dto.CategoryUpsertDTO;
import com.subscription.core.dto.DiscountTypeUpsertDTO;
import com.subscription.core.dto.ProductUpsertDTO;
import com.subscription.core.dto.SlotUpsertDTO;
import com.subscription.core.dto.ZoneUpsertDTO;
import com.subscription.core.dto.WarehouseUpsertDTO;
import com.subscription.core.dto.WarehouseZoneUpsertDTO;
import com.subscription.core.dto.UserAddressUpsertDTO;
import com.subscription.core.dto.UserContactUpsertDTO;
import com.subscription.core.service.MasterDataService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/master-data")
@RequiredArgsConstructor
@Slf4j
public class MasterDataController {

    private final MasterDataService masterDataService;

    // --- Category Admin APIs ---

    /**
     * Creates or updates categories in bulk.
     *
     * @param requests The list of category data to create or update
     * @return Response containing success message
     */
    @PostMapping("/category/upsert")
    public ResponseEntity<String> upsertCategory(@RequestBody List<CategoryUpsertDTO> requests) {
        log.info("[f:upsertCategory] Processing bulk request for {} categories", requests.size());
        return ResponseEntity.ok(masterDataService.upsertCategories(requests));
    }

    // --- Product Admin APIs ---

    /**
     * Creates or updates products in bulk.
     *
     * @param requests The list of product data to create or update
     * @return Response containing success message
     */
    @PostMapping("/product/upsert")
    public ResponseEntity<String> upsertProduct(@RequestBody List<ProductUpsertDTO> requests) {
        log.info("[f:upsertProduct] Processing bulk request for {} products", requests.size());
        return ResponseEntity.ok(masterDataService.upsertProducts(requests));
    }

    // --- DiscountType Admin APIs ---

    /**
     * Creates or updates discount types in bulk.
     *
     * @param requests The list of discount type data to create or update
     * @return Response containing success message
     */
    @PostMapping("/discount-type/upsert")
    public ResponseEntity<String> upsertDiscountType(@RequestBody List<DiscountTypeUpsertDTO> requests) {
        log.info("[f:upsertDiscountType] Processing bulk request for {} discount types", requests.size());
        return ResponseEntity.ok(masterDataService.upsertDiscountTypes(requests));
    }

    // --- Slot Admin APIs ---

    /**
     * Creates or updates slots in bulk.
     *
     * @param requests The list of slot data to create or update
     * @return Response containing success message
     */
    @PostMapping("/slot/upsert")
    public ResponseEntity<String> upsertSlot(@RequestBody List<SlotUpsertDTO> requests) {
        log.info("[f:upsertSlot] Processing bulk request for {} slots", requests.size());
        return ResponseEntity.ok(masterDataService.upsertSlots(requests));
    }

    // --- Zone Admin APIs ---

    /**
     * Creates or updates zones in bulk.
     *
     * @param requests The list of zone data to create or update
     * @return Response containing success message
     */
    @PostMapping("/zone/upsert")
    public ResponseEntity<String> upsertZone(@RequestBody List<ZoneUpsertDTO> requests) {
        log.info("[f:upsertZone] Processing bulk request for {} zones", requests.size());
        return ResponseEntity.ok(masterDataService.upsertZones(requests));
    }

    // --- Warehouse Admin APIs ---

    /**
     * Creates or updates warehouses in bulk.
     *
     * @param requests The list of warehouse data to create or update
     * @return Response containing success message
     */
    @PostMapping("/warehouse/upsert")
    public ResponseEntity<String> upsertWarehouse(@RequestBody List<WarehouseUpsertDTO> requests) {
        log.info("[f:upsertWarehouse] Processing bulk request for {} warehouses", requests.size());
        return ResponseEntity.ok(masterDataService.upsertWarehouses(requests));
    }

    // --- WarehouseZone Admin APIs ---

    /**
     * Creates or updates warehouse-zone mappings in bulk.
     *
     * @param requests The list of warehouse-zone mapping data to create or update
     * @return Response containing success message
     */
    @PostMapping("/warehouse-zone/upsert")
    public ResponseEntity<String> upsertWarehouseZone(@RequestBody List<WarehouseZoneUpsertDTO> requests) {
        log.info("[f:upsertWarehouseZone] Processing bulk request for {} warehouse-zone mappings", requests.size());
        return ResponseEntity.ok(masterDataService.upsertWarehouseZones(requests));
    }

    // --- UserAddress Admin APIs ---

    /**
     * Creates or updates user addresses in bulk.
     *
     * @param requests The list of user address data to create or update
     * @return Response containing success message
     */
    @PostMapping("/user-address/upsert")
    public ResponseEntity<String> upsertUserAddress(@RequestBody List<UserAddressUpsertDTO> requests) {
        log.info("[f:upsertUserAddress] Processing bulk request for {} user addresses", requests.size());
        return ResponseEntity.ok(masterDataService.upsertUserAddresses(requests));
    }

    // --- UserContact Admin APIs ---

    /**
     * Creates or updates user contacts in bulk.
     *
     * @param requests The list of user contact data to create or update
     * @return Response containing success message
     */
    @PostMapping("/user-contact/upsert")
    public ResponseEntity<String> upsertUserContact(@RequestBody List<UserContactUpsertDTO> requests) {
        log.info("[f:upsertUserContact] Processing bulk request for {} user contacts", requests.size());
        return ResponseEntity.ok(masterDataService.upsertUserContacts(requests));
    }

}
