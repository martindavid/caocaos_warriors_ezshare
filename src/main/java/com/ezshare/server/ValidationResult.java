package com.ezshare.server;

public class ValidationResult {
	
	public String message;
	public Boolean isValid;
	
	public ValidationResult(Boolean isValid, String message) {
		this.isValid = isValid;
		this.message = message;
	}
}
