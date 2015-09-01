package com.gongpingjia.carplay.common.exception;

import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.exceptions.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.NestedServletException;

import com.gongpingjia.carplay.common.domain.ResponseDo;

/**
 * 统一异常处理
 * 
 * @author licheng
 *
 */
@ControllerAdvice
public class CommonExceptionHandler extends ResponseEntityExceptionHandler {

	private static Logger LOG = LoggerFactory.getLogger(CommonExceptionHandler.class);

	/**
	 * 处理业务异常
	 * 
	 * @param ex
	 *            异常对象
	 * @param request
	 *            请求参数
	 * @return 返回处理结果
	 */
	@ExceptionHandler(ApiException.class)
	public final ResponseEntity<?> handleApiException(ApiException ex, WebRequest request) {
		LOG.warn("Handle ApiException, it is business logic exception");
		LOG.error(ex.getMessage(), ex);
		ResponseDo response = ResponseDo.buildFailureResponse("输入参数有误");
		return handleExceptionInternal(ex, response, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	/**
	 * 处理数据库返回映射操作异常，一般为返回数据映射问题
	 * 
	 * @param ex
	 *            异常对象
	 * @param request
	 *            请求参数
	 * @return 返回处理结果
	 */
	@ExceptionHandler(NestedServletException.class)
	public final ResponseEntity<?> handleNestedServletException(NestedServletException ex, WebRequest request) {
		LOG.warn("Handle NestedServletException, it is database mapping exception");
		LOG.error(ex.getMessage(), ex);
		ResponseDo response = ResponseDo.buildFailureResponse("系统错误");
		HttpHeaders headers = new HttpHeaders();
		return handleExceptionInternal(ex, response, headers, HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	@ExceptionHandler(PersistenceException.class)
	public final ResponseEntity<?> handlePersistenceException(PersistenceException ex, WebRequest request) {
		LOG.warn("Handle NestedServletException, it is database mapping exception");
		LOG.error(ex.getMessage(), ex);
		ResponseDo response = ResponseDo.buildFailureResponse("系统错误");
		HttpHeaders headers = new HttpHeaders();
		return handleExceptionInternal(ex, response, headers, HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	@ExceptionHandler(BindingException.class)
	public final ResponseEntity<?> handleBindingException(BindingException ex, WebRequest request) {
		LOG.warn("Handle BindingException, it is database mapping exception");
		LOG.error(ex.getMessage(), ex);
		ResponseDo response = ResponseDo.buildFailureResponse("系统错误");
		HttpHeaders headers = new HttpHeaders();
		return handleExceptionInternal(ex, response, headers, HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	@ExceptionHandler(RuntimeException.class)
	public final ResponseEntity<?> handleRuntimeException(RuntimeException ex, WebRequest request) {
		LOG.warn("Handle RuntimeException, it is database mapping exception");
		LOG.error(ex.getMessage(), ex);
		ResponseDo response = ResponseDo.buildFailureResponse("系统错误");
		HttpHeaders headers = new HttpHeaders();
		return handleExceptionInternal(ex, response, headers, HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	/**
	 * 处理Exception类型异常，终极捕获异常
	 * 
	 * @param ex
	 *            异常对象
	 * @param request
	 *            请求参数
	 * @return 返回处理结果
	 */
	@ExceptionHandler(Exception.class)
	public final ResponseEntity<?> handleFinalException(Exception ex, WebRequest request) {
		LOG.warn("Handle Exception, it is internal server exception, exception type:{}", ex.getClass());
		LOG.error(ex.getMessage(), ex);
		ResponseDo response = ResponseDo.buildFailureResponse("系统错误");
		return handleExceptionInternal(ex, response, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	/**
	 * 处理Throwable类型异常，终极捕获异常
	 * 
	 * @param throwable
	 *            异常对象
	 * @param request
	 *            请求参数
	 * @return 返回处理结果
	 */
	@ExceptionHandler(Throwable.class)
	public final ResponseEntity<?> handleThrowable(Throwable throwable, WebRequest request) {
		LOG.warn("Handle Throwable, it is internal server exception");
		LOG.error(throwable.getMessage(), throwable);
		ResponseDo response = ResponseDo.buildFailureResponse("系统错误");
		Exception ex = new Exception(throwable);
		return handleExceptionInternal(ex, response, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	}
}
