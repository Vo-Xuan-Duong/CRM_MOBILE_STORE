package com.example.Backend.mappers;

import com.example.Backend.dtos.serial.SerialUnitResponse;
import com.example.Backend.models.SerialUnit;
import org.springframework.stereotype.Component;

@Component
public class SerialUnitMapper {

    public SerialUnitResponse toResponse(SerialUnit serialUnit) {
        if (serialUnit == null) {
            return null;
        }

        return SerialUnitResponse.builder()
                .id(serialUnit.getId())
                .skuId(serialUnit.getSku().getId())
                .skuName(serialUnit.getSku().getModel().getName() + " " +
                        (serialUnit.getSku().getVariantName() != null ? serialUnit.getSku().getVariantName() : ""))
                .brandName(serialUnit.getSku().getModel().getBrand().getName())
                .imei(serialUnit.getImei())
                .serialNumber(serialUnit.getSerialNumber())
                .status(serialUnit.getStatus())
                .purchaseDate(serialUnit.getPurchaseDate())
                .notes(serialUnit.getNotes())
                .createdAt(serialUnit.getCreatedAt())
                .updatedAt(serialUnit.getUpdatedAt())
                .build();
    }
}
