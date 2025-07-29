// OrderItemUpdateBody.java
package com.services.salesorder.model.body;

import com.services.salesorder.model.entity.OrderItem;
import io.smallrye.common.constraint.NotNull;
import lombok.With;

import java.util.Objects;
import java.util.Optional;

public record OrderItemUpdateBody(
        Long productId,
        String productName,
        String productCategory,
        Float salesQuantity,
        Float salesPrice,
        @With @NotNull Long tenantId
) {
    public void updateEntity(OrderItem entity) {
        Objects.requireNonNull(entity);
        entity.setProductId(Optional.ofNullable(productId).orElse(entity.getProductId()));
        entity.setProductName(Optional.ofNullable(productName).orElse(entity.getProductName()));
        entity.setProductCategory(Optional.ofNullable(productCategory).orElse(entity.getProductCategory()));
        entity.setSalesQuantity(Optional.ofNullable(salesQuantity).orElse(entity.getSalesQuantity()));
        entity.setSalesPrice(Optional.ofNullable(salesPrice).orElse(entity.getSalesPrice()));
    }
}