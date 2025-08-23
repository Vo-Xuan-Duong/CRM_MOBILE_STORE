package com.example.Backend.mappers;

import com.example.Backend.dtos.salesorder.SalesOrderItemResponse;
import com.example.Backend.models.SalesOrderItem;
import org.springframework.stereotype.Component;

@Component
public class SalesOrderItemMapper {

    public SalesOrderItemResponse toResponse(SalesOrderItem item) {
        if (item == null) {
            return null;
        }

        return SalesOrderItemResponse.builder()
                .id(item.getId())
                .skuId(item.getSku() != null ? item.getSku().getId() : null)
                .productName(item.getSku() != null && item.getSku().getModel() != null
                    ? item.getSku().getModel().getName() : null)
                .serialUnitId(item.getSerialUnit() != null ? item.getSerialUnit().getId() : null)
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .lineTotal(item.getLineTotal())
                .build();
    }
}
