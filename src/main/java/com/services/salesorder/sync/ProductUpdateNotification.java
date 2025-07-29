package com.services.salesorder.sync;

public record ProductUpdateNotification(
        Long productId,
        String name,
        String category,
        Float salesPrice
) {}