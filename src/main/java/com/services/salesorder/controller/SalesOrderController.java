package com.services.salesorder.controller;

import com.services.salesorder.core.util.CommonStatic;
import com.services.salesorder.core.util.ResponseSalesOrder;
import com.services.salesorder.exception.DataNotFoundException;
import com.services.salesorder.exception.ExceptionCode;
import com.services.salesorder.model.body.SalesOrderCreationBody;
import com.services.salesorder.model.body.SalesOrderUpdateBody;
import com.services.salesorder.model.entity.SalesOrder;
import com.services.salesorder.repository.SalesOrderRepo;
import com.services.salesorder.service.SalesOrderSyncService;
import com.services.salesorder.sync.ProductUpdateNotification;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.jboss.resteasy.reactive.RestPath;

import java.util.List;

@Path(CommonStatic.V1)
public class SalesOrderController {

    @Inject
    SalesOrderRepo salesOrderRepo;

    @POST
    @Path("/protect/sales-order")
    @Transactional
    public ResponseSalesOrder protectCreateSalesOrder(@Context SecurityContext sc, SalesOrderCreationBody body) {
        Long tenantID = CommonStatic.tenantID;
        var entity = salesOrderRepo.create(body.withTenantId(tenantID));
        return new ResponseSalesOrder(entity.getOrderId(), "", 0);
    }

    @PUT
    @Path("/protect/sales-order/{id}")
    @Transactional
    public ResponseSalesOrder protectUpdateSalesOrder(@Context SecurityContext sc, SalesOrderUpdateBody body, @RestPath Long id) {
        Long tenantID = CommonStatic.tenantID;
        var entity = salesOrderRepo.update(body.withTenantId(tenantID), id);
        return new ResponseSalesOrder(entity.getOrderId(), "", 0);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/protect/sales-order/get/{id}")
    public Response findSalesOrder(@Context SecurityContext sc, @RestPath("id") Long id) {
        Long tenantID = CommonStatic.tenantID;
        return salesOrderRepo.findByIdAndTenant(id, tenantID)
                .map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND))
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/protect/sales-order/list/all")
    public Response listAllSalesOrders(@Context SecurityContext sc) {
        List<SalesOrder> orders = salesOrderRepo.listAll(Sort.by("orderId").descending());
        return orders.isEmpty()
                ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.ok(orders).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/protect/sales-order/find/{customerName}")
    public Response listOrdersByCustomer(
            @Context SecurityContext sc,
            @PathParam("customerName") String customerName
    ) {
        List<SalesOrder> orders;

        if (customerName == null || customerName.trim().isEmpty()) {
            orders = salesOrderRepo.listAll(Sort.by("orderId").descending());
        } else {
            String normalizedSearch = customerName.trim().replaceAll("\\s+", " ");

            orders = salesOrderRepo.find(
                    "LOWER(TRIM(customerName)) LIKE LOWER(?1) ORDER BY orderId ASC",
                    "%" + normalizedSearch + "%"
            ).list();
        }

        return orders.isEmpty()
                ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.ok(orders).build();
    }

    @DELETE
    @Path("/protect/sales-order/{id}")
    @Transactional
    public Response deleteSalesOrder(@Context SecurityContext sc, @RestPath Long id) {
        Long tenantID = CommonStatic.tenantID;

        try {
            salesOrderRepo.deleteByIdAndTenant(id, tenantID);
            return Response.ok("Sales order deleted successfully.").build();
        } catch (DataNotFoundException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity("Sales order not found.").build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to delete sales order.").build();
        }
    }


    @Inject
    SalesOrderSyncService syncService;

    @POST
    @Path("/protect/sales-order/product-update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseSalesOrder onProductUpdate(ProductUpdateNotification notif) {
        ResponseSalesOrder responseSalesOrder = null;
        try {
            syncService.handleProductUpdate(notif);
            responseSalesOrder = new ResponseSalesOrder(0L,"Success item update on sales order DRAFT", ExceptionCode.OK.ordinal());
        }catch(Exception exc){
            responseSalesOrder = new ResponseSalesOrder(0L,"Failed update on sales order DRAFT", ExceptionCode.FAILED.ordinal());

        }
        return responseSalesOrder;
    }

    @GET
    @Path("/protect/sales-order/product/update/{id}/{name}/{category}/{price}")
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseSalesOrder onSalesOrderProductUpdate(
            @RestPath("id") Long id, @RestPath("name") String name, @RestPath("category") String category, @RestPath("price") Float price) {
        ResponseSalesOrder responseSalesOrder = null;
        try {
            ProductUpdateNotification notif= new ProductUpdateNotification(id, name,category, price);
            syncService.handleProductUpdate(notif);
            responseSalesOrder = new ResponseSalesOrder(0L,"Success item update on sales order DRAFT", ExceptionCode.OK.ordinal());
        }catch(Exception exc){
            responseSalesOrder = new ResponseSalesOrder(0L,"Failed update on sales order DRAFT", ExceptionCode.FAILED.ordinal());

        }
        return responseSalesOrder;
    }


}
