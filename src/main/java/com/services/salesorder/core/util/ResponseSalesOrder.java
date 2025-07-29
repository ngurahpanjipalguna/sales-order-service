package com.services.salesorder.core.util;

public record ResponseSalesOrder(Long salesOrderId,
         String message, // may add next dev
         Integer responseCode) {
}
