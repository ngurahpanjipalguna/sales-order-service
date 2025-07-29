package com.services.salesorder.repository;

import com.services.salesorder.exception.DataNotFoundException;
import com.services.salesorder.exception.ExceptionCode;
import com.services.salesorder.exception.FormatException;
import com.services.salesorder.model.body.OrderItemCreationBody;
import com.services.salesorder.model.body.OrderItemUpdateBody;
import com.services.salesorder.model.entity.OrderItem;
import com.services.salesorder.model.entity.SalesOrder;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class OrderItemRepo implements PanacheRepository<OrderItem> {

    @Inject
    Validator validator;

    @Inject
    SalesOrderRepo orderRepo;

    @Inject
    SalesOrderRepo salesOrderRepo;

    public OrderItem create(OrderItemCreationBody body, Long salesOrderId) {
        Objects.requireNonNull(body);
        FormatException.validateObject(body, validator);
        SalesOrder salesOrder =  null;
        salesOrder = getSalesOrderById(salesOrderId, body.tenantId());
        var entity = body.mapToEntity(salesOrder);
        persist(entity);
        calculateTotalItem(salesOrderId, body.tenantId());
        return entity;
    }

    public OrderItem update(OrderItemUpdateBody body, long id) {
        Objects.requireNonNull(body);
        FormatException.validateObject(body, validator);

        var entity = findByIdAndTenant(id, body.tenantId())
                .orElseThrow(() -> new DataNotFoundException(ExceptionCode.ORDER_ITEM_NOT_FOUND));

        body.updateEntity(entity);
        calculateTotalItem(entity.getSalesOrder().getOrderId(), body.tenantId());
        return entity;
    }

    public Optional<OrderItem> findByIdAndTenant(long id, long tenantId) {
        return find("id = ?1 AND tenantId = ?2", id, tenantId).firstResultOptional();
    }

    @Transactional
    public void deleteByIdAndTenant(long id, long tenantId) {
        var entity = findByIdAndTenant(id, tenantId)
                .orElseThrow(() -> new DataNotFoundException(ExceptionCode.ORDER_ITEM_NOT_FOUND));
        delete(entity);
        calculateTotalItem(entity.getSalesOrder().getOrderId(),tenantId);
    }

    public List<OrderItem> findBySalesOrderIdAndTenant(Long salesOrderId, long tenantId) {
        return find("salesOrder.orderId = ?1 AND tenantId = ?2", salesOrderId, tenantId).list();
    }

    public List<OrderItem> listAll(long tenantId) {
        return find("tenantId = ?1", tenantId).list();
    }

    public SalesOrder getSalesOrderById(Long id, Long tenantId) {
        SalesOrderRepo salesOrderRepo = new SalesOrderRepo();
        return salesOrderRepo.findByIdAndTenant(id, tenantId)
                .orElseThrow(() -> new DataNotFoundException(ExceptionCode.SALES_ORDER_NOT_FOUND));
    }


    public void calculateTotalItem(long salesOrderId, long tenantId) {
        // find the item first so we know orderId

        // then recalc and save on the order:
        var total = findBySalesOrderIdAndTenant( salesOrderId,  tenantId)
                .stream()
                .mapToDouble(i->i.getSalesQuantity()*i.getSalesPrice())
                .sum();

        orderRepo.updateTotal(salesOrderId, total);
    }

    public List<OrderItem> findByProductAndDraftStatus(Long productId, String draftStatus) {
        // Panache will traverse the salesOrder.status field
        return find("productId = ?1 and salesOrder.status = ?2", productId, draftStatus).list();
    }
}
