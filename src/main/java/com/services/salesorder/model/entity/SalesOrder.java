package com.services.salesorder.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "sales_orders")
@Data
public class SalesOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sales_order_seq")
    @SequenceGenerator(name = "sales_order_seq", sequenceName = "sales_order_seq", allocationSize = 1)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "total_amount")
    private double totalAmount;

    @Column(name = "status")
    private String status;

    @NotNull
    @Column(name = "xk_tenant_id")
    private Long tenantId;

    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, orphanRemoval = true , fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<OrderItem> orderItems;
}
