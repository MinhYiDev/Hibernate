package com.javaweb.controllerAdvice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.javaweb.model.ErrorResponseDTO;

@ControllerAdvice
public class ControllerAdvisor {

	@ExceptionHandler(ArithmeticException.class)
	public ResponseEntity<Object> handllerArithmeticException(ArithmeticException ex, WebRequest ws) {
//		err:[loi toan hoc
		ErrorResponseDTO err = new ErrorResponseDTO();

		err.setError(ex.getMessage());

		List<String> detail = new ArrayList<String>();
		detail.add("Loi toan hoc ne");
		err.setDetails(detail);

		return new ResponseEntity<>(err, HttpStatus.BAD_GATEWAY);
	}
}
