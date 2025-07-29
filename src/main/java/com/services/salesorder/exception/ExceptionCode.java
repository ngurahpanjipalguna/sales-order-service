package com.services.salesorder.exception;

import lombok.Getter;

public enum ExceptionCode {
	OK("OK"),
	FAILED("Failed"),
	F_NV("Form not Valid"),
	CFG_R("Config is required"),
	AUTH_PROBLEM("Authentication Problem, common is token expired"),
	CFG_VALUE_NO_VALID("Config Value Not Valid"),
	JWT_PAYLOAD_NO_VALID("Jwt payload is not valid"),
	JWT_KEY_ID_NOT_FOUND("Jwt KeyId not found"),
	JWT_UNAUTHENTICATION("Jwt is not permit"),
	JWT_IS_EXPIRED("Jwt is expired"),
	JWT_SIG_NOT_VALID("Jwt signature not valid"),
	SERVICE_COMM_PROBLEM("Service communication problem"),
	//---------- HANYA MODIF DIBAGIAN BAWAH INI
	PRODUCT_NOT_FOUND("Category not found"),
	CUSTOMER_NOT_FOUND("Customer not found"),
	SALES_ORDER_NOT_FOUND("Sales order not found"),
	ORDER_ITEM_NOT_FOUND("Order item not found"),
	;

	@Getter
	private final String message;

	ExceptionCode(String message) {
		this.message = message;
	}
	
}
