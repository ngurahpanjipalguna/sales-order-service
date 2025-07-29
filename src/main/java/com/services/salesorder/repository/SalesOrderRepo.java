package com.services.salesorder.repository;

import com.services.salesorder.exception.DataNotFoundException;
import com.services.salesorder.exception.ExceptionCode;
import com.services.salesorder.exception.FormatException;
import com.services.salesorder.exception.ServiceBaseException;
import com.services.salesorder.model.body.SalesOrderCreationBody;
import com.services.salesorder.model.body.SalesOrderUpdateBody;
import com.services.salesorder.model.entity.SalesOrder;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class SalesOrderRepo implements PanacheRepository<SalesOrder> {

    @Inject
    Validator validator;

    public SalesOrder create(SalesOrderCreationBody body) {
        Objects.requireNonNull(body);
        FormatException.validateObject(body, validator);
        var entity = body.mapToEntity();
        persist(entity);
        return entity;
    }

    public List<SalesOrder> findByCustomerName(String customerName, String sortField) {
        // 1. Normalisasi input: hilangkan spasi berlebih dan lowercase
        String normalizedSearch = customerName.trim()
                .replaceAll("\\s+", " ")
                .toLowerCase();

        // 2. Bangun query dengan LIKE yang proper
        String query = "LOWER(TRIM(customerName)) LIKE LOWER(?1)";

        // 3. Tambahkan sorting jika diperlukan
        if (sortField != null && !sortField.isEmpty()) {
            query += " ORDER BY " + sortField;
        }

        // 4. Gunakan wildcard % untuk pencarian partial
        return find(query, "%" + normalizedSearch + "%")
                .list();
    }

    public SalesOrder update(SalesOrderUpdateBody body, long id) {
        Objects.requireNonNull(body);
        FormatException.validateObject(body, validator);

        var entity = findByIdAndTenant(id, body.tenantId())
                .orElseThrow(() -> new DataNotFoundException(ExceptionCode.SALES_ORDER_NOT_FOUND));

        body.updateEntity(entity);
        return entity;
    }

    public Optional<SalesOrder> findByIdAndTenant(long id, long tenantId) {
        return find("id = ?1 AND tenantId = ?2", id, tenantId).firstResultOptional();
    }

    @Transactional
    public void deleteByIdAndTenant(long id, long tenantId) {
        var entity = findByIdAndTenant(id, tenantId)
                .orElseThrow(() -> new DataNotFoundException(ExceptionCode.SALES_ORDER_NOT_FOUND));
        delete(entity);
    }

    @Transactional
    public void updateTotal(Long orderId, double newTotal) {
        try {
            var so = (SalesOrder) findById(orderId);
            so.setTotalAmount(newTotal);
        } catch(Exception exc){
            throw (new ServiceBaseException(ExceptionCode.SALES_ORDER_NOT_FOUND, "Error on finding sales order ID=" + orderId));
        }
        // Panache auto-flushes on transaction commit
    }
}
