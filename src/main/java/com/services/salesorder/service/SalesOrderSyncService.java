package com.services.salesorder.service;

import com.services.salesorder.core.util.SalesOrderStatus;
import com.services.salesorder.repository.OrderItemRepo;
import com.services.salesorder.repository.SalesOrderRepo;
import com.services.salesorder.sync.ProductUpdateNotification;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;


import java.util.*;
import java.util.stream.*;

@ApplicationScoped
public class SalesOrderSyncService {

    @Inject
    OrderItemRepo itemRepo;
    @Inject
    SalesOrderRepo orderRepo;

    @Transactional
    public void handleProductUpdate(ProductUpdateNotification notif) {
        // 1) find all DRAFT items for this product
        var draft   = SalesOrderStatus.SALES_ORDER_STATUS_DRAFT;
        var items   = itemRepo.findByProductAndDraftStatus(notif.productId(), draft);

        // 2) update each line—collect which orders changed
        Set<Long> changedOrders = new HashSet<>();
        for (var item : items) {
            item.setProductName(notif.name());
            item.setProductCategory(notif.category());
            item.setSalesPrice(notif.salesPrice());
            changedOrders.add(item.getSalesOrder().getOrderId());
        }

        // 3) for each affected order, recalc total
        for (var orderId : changedOrders) {
            double total = itemRepo.find("salesOrder.orderId = ?1", orderId)
                    .stream()
                    .mapToDouble(i -> i.getSalesQuantity() * i.getSalesPrice())
                    .sum();
            orderRepo.findById(orderId)
                    .setTotalAmount(total);
        }
    }
}
