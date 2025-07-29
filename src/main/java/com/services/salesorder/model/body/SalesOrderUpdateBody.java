// SalesOrderUpdateBody.java
package com.services.salesorder.model.body;

import com.services.salesorder.model.entity.OrderItem;
import com.services.salesorder.model.entity.SalesOrder;
import io.smallrye.common.constraint.NotNull;
import lombok.With;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record SalesOrderUpdateBody(
        Long customerId,
        String customerName,
        Double totalAmount,
        String status,
        List<OrderItem> orderItems,
        @With @NotNull Long tenantId
) {
    public void updateEntity(SalesOrder entity) {
        Objects.requireNonNull(entity);
        entity.setCustomerId(Optional.ofNullable(customerId).orElse(entity.getCustomerId()));
        entity.setCustomerName(Optional.ofNullable(customerName).orElse(entity.getCustomerName()));
        entity.setTotalAmount(Optional.ofNullable(totalAmount).orElse(entity.getTotalAmount()));
        entity.setStatus(Optional.ofNullable(status).orElse(entity.getStatus()));
        entity.setOrderItems(Optional.ofNullable(orderItems).orElse(entity.getOrderItems()));
    }
}