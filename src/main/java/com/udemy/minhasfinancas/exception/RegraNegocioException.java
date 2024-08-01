package com.udemy.minhasfinancas.exception;

//Essa runtimeException é uma exceção de tempo de execução, não precisa ser tratada
public class RegraNegocioException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public RegraNegocioException(String msg) {
		super(msg);
	}
}
