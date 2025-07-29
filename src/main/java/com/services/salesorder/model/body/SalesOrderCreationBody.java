// SalesOrderCreationBody.java
package com.services.salesorder.model.body;

import com.services.salesorder.model.entity.OrderItem;
import com.services.salesorder.model.entity.SalesOrder;
import io.smallrye.common.constraint.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.With;

import java.util.List;

public record SalesOrderCreationBody(
        @NotNull Long customerId,
        @NotBlank String customerName,
        double totalAmount,
        String status,
        List<OrderItem> orderItems,
        @With @NotNull Long tenantId
) {
    public SalesOrder mapToEntity() {
        var entity = new SalesOrder();
        entity.setCustomerId(customerId);
        entity.setCustomerName(customerName);
        entity.setTotalAmount(totalAmount);
        entity.setStatus(status);
        entity.setOrderItems(orderItems);
        entity.setTenantId(tenantId);
        return entity;
    }
}
