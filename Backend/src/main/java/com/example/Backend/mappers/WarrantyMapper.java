package com.example.Backend.mappers;

import com.example.Backend.controllers.WarrantyController;
import com.example.Backend.dtos.warranty.WarrantyResponse;
import com.example.Backend.models.Warranty;
import org.springframework.stereotype.Component;

@Component
public class WarrantyMapper {

    public WarrantyResponse toResponse(Warranty warranty) {
        if (warranty == null) {
            return null;
        }

        return WarrantyResponse.builder()
                .id(warranty.getId())
                .customerId(warranty.getCustomer().getId())
                .customerName(warranty.getCustomer().getFullName())
                .orderItemId(warranty.getOrderItem().getId())
                .serialUnitId(warranty.getSerialUnit() != null ? warranty.getSerialUnit().getId() : null)
                .imei(warranty.getSerialUnit() != null ? warranty.getSerialUnit().getImei() : null)
                .warrantyCode(warranty.getWarrantyCode())
                .startDate(warranty.getStartDate())
                .endDate(warranty.getEndDate())
                .months(warranty.getMonths())
                .status(warranty.getStatus())
                .qrImageUrl(warranty.getQrImageUrl())
                .notes(warranty.getNotes())
                .isActive(warranty.isActive())
                .isExpired(warranty.isExpired())
                .daysRemaining(warranty.getDaysRemaining())
                .createdAt(warranty.getCreatedAt())
                .updatedAt(warranty.getUpdatedAt())
                .build();
    }
}
