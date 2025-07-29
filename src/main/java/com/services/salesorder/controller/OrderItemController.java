package com.services.salesorder.controller;

import com.services.salesorder.core.util.CommonStatic;
import com.services.salesorder.core.util.ResponseSalesOrder;
import com.services.salesorder.exception.DataNotFoundException;
import com.services.salesorder.model.body.OrderItemCreationBody;
import com.services.salesorder.model.body.OrderItemUpdateBody;
import com.services.salesorder.model.dto.OrderItemResponse;
import com.services.salesorder.model.entity.OrderItem;
import com.services.salesorder.repository.OrderItemRepo;
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
public class OrderItemController {

    @Inject
    OrderItemRepo orderItemRepo;

    @POST
    @Path("/protect/order-item/{salesOrderId}")
    @Transactional
    public ResponseSalesOrder createOrderItem(@Context SecurityContext sc, OrderItemCreationBody body, long salesOrderId) {
        Long tenantID = CommonStatic.tenantID;
        var entity = orderItemRepo.create(body.withTenantId(tenantID), salesOrderId);
        return new ResponseSalesOrder(entity.getOrderDetailId(),"",0);
    }

    @PUT
    @Path("/protect/order-item/{id}")
    @Transactional
    public ResponseSalesOrder updateOrderItem(@Context SecurityContext sc, OrderItemUpdateBody body, @RestPath Long id) {
        Long tenantID = CommonStatic.tenantID;
        var entity = orderItemRepo.update(body.withTenantId(tenantID), id);
        return new ResponseSalesOrder(entity.getOrderDetailId(),"",0);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/protect/order-item/get/{id}")
    public Response getOrderItem(@Context SecurityContext sc, @RestPath("id") Long id) {
        Long tenantID = CommonStatic.tenantID;
        return orderItemRepo.findByIdAndTenant(id, tenantID)
                .map(Response::ok)
                .orElse(Response.status(Response.Status.NOT_FOUND))
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/protect/order-item/list/by-order/{orderId}")
    public Response listOrderItemsByOrder(@Context SecurityContext sc, @RestPath("orderId") Long orderId) {
        Long tenantID = CommonStatic.tenantID;
        List<OrderItem> items = orderItemRepo.findBySalesOrderIdAndTenant( orderId, tenantID);
        return items.isEmpty()
                ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.ok(items).build();
    }


    @GET
    @Path("/protect/order-item/list/by-order/with-order-id/{salesOrderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrderItemsByOrder(@Context SecurityContext sc,
                                         @RestPath Long salesOrderId) {
        long tenantId = CommonStatic.tenantID; // or from JWT
        List<OrderItem> entities = orderItemRepo.findBySalesOrderIdAndTenant(salesOrderId, tenantId);

        List<OrderItemResponse> responseList = entities.stream()
                .map(OrderItemResponse::fromEntity)
                .toList();

        return Response.ok(responseList).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/protect/order-item/list/all")
    public Response listOrderItems(@Context SecurityContext sc) {
        Long tenantID = CommonStatic.tenantID;
        List<OrderItem> items = orderItemRepo.listAll(tenantID);
        return items.isEmpty()
                ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.ok(items).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/protect/order-item/list/all/with-order-id")
    public Response listOrderItemsWithOrderId(@Context SecurityContext sc) {
        Long tenantID = CommonStatic.tenantID;
        List<OrderItem> entities = orderItemRepo.listAll(tenantID);

        List<OrderItemResponse> responseList = entities.stream()
                .map(OrderItemResponse::fromEntity)
                .toList();

        return Response.ok(responseList).build();
    }

    @DELETE
    @Path("/protect/order-item/{id}")
    @Transactional
    public Response deleteOrderItem(@Context SecurityContext sc, @RestPath Long id) {
        Long tenantID = CommonStatic.tenantID;

        try {
            orderItemRepo.deleteByIdAndTenant(id, tenantID);
            return Response.ok("Order item deleted successfully.").build();
        } catch (DataNotFoundException ex) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Order item not found.").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to delete order item.").build();
        }
    }

}
