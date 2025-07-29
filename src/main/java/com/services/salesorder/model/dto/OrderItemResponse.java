package com.services.salesorder.model.dto;

import com.services.salesorder.model.entity.OrderItem;
import lombok.Data;

@Data
public class OrderItemResponse {
    private Long orderDetailId;
    private Long salesOrderId;
    private Long productId;
    private String productName;
    private String productCategory;
    private Float salesQuantity;
    private float salesPrice;
    private Long tenantId;

    public static OrderItemResponse fromEntity(OrderItem entity) {
        OrderItemResponse dto = new OrderItemResponse();
        dto.setOrderDetailId(entity.getOrderDetailId());
        dto.setSalesOrderId(entity.getSalesOrder().getOrderId()); // ✅ includes only orderId
        dto.setProductId(entity.getProductId());
        dto.setProductName(entity.getProductName());
        dto.setProductCategory(entity.getProductCategory());
        dto.setSalesQuantity(entity.getSalesQuantity());
        dto.setSalesPrice(entity.getSalesPrice());
        dto.setTenantId(entity.getTenantId());
        return dto;
    }
}
