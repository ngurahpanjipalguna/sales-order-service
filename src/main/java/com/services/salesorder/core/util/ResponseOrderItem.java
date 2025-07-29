 package com.services.salesorder.core.util;

public record ResponseOrderItem(Long orderItemId,
                                String message, // may add next dev
                                Integer responseCode) {
}
