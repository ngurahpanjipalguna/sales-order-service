// OrderItemCreationBody.java
package com.services.salesorder.model.body;

import com.services.salesorder.model.entity.OrderItem;
import com.services.salesorder.model.entity.SalesOrder;
import io.smallrye.common.constraint.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.With;

public record OrderItemCreationBody(
        @NotNull Long productId,
        @NotBlank String productName,
        @NotBlank String productCategory,
        Float salesQuantity,
        float salesPrice,
        @With @NotNull Long tenantId
) {
    public OrderItem mapToEntity(SalesOrder parentOrder) {
        var entity = new OrderItem();
        entity.setSalesOrder(parentOrder);
        entity.setProductId(productId);
        entity.setProductName(productName);
        entity.setProductCategory(productCategory);
        entity.setSalesQuantity(salesQuantity);
        entity.setSalesPrice(salesPrice);
        entity.setTenantId(tenantId);
        return entity;
    }
}