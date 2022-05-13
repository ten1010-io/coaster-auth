package io.ten1010.coaster.auth.web;

import io.ten1010.coaster.auth.common.LogUtil;
import io.ten1010.coaster.auth.common.PropertyUserVisibleException;
import io.ten1010.coaster.auth.common.UserVisibleException;
import io.ten1010.coaster.auth.web.exception.ResourceConflictException;
import io.ten1010.coaster.auth.web.exception.ResourceNotFoundException;
import io.ten1010.coaster.auth.web.exceptionadapter.DomainModelPropertyExceptionAdapterManager;
import io.ten1010.coaster.auth.web.meta.v1.Status;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Slf4j
public class ResponseEntityExceptionHandlerImpl extends ResponseEntityExceptionHandler {

    private static final String LOG_TEMPLATE = "Processing request [{}], exception [{}] occurred\n{}";

    private static void logAsDebug(final Exception ex, final WebRequest request) {
        log.debug(
                LOG_TEMPLATE,
                parseRequestDescription(request),
                ex.getClass().getSimpleName(),
                LogUtil.logMessage(ex, true));
    }

    private static void logAsError(final Exception ex, final WebRequest request) {
        log.error(
                LOG_TEMPLATE,
                parseRequestDescription(request),
                ex.getClass().getSimpleName(),
                LogUtil.logMessage(ex, true));
    }

    private static String parseRequestDescription(final WebRequest request) {
        if (request instanceof ServletWebRequest) {
            ServletWebRequest casted = (ServletWebRequest) request;
            HttpServletRequest servletRequest = casted.getRequest();

            return String.format(
                    "method=%s;uri=%s;client=%s",
                    casted.getHttpMethod(),
                    servletRequest.getRequestURI(),
                    servletRequest.getRemoteAddr());
        }

        return request.getDescription(true);
    }

    @Value
    public static class PropertyExceptionDetail {

        private String object;
        private String propertyPath;
        private String message;

    }

    @Autowired
    private DomainModelPropertyExceptionAdapterManager adapter;

    // 400...

    @ExceptionHandler({UserVisibleException.class})
    public ResponseEntity<Object> handle(final UserVisibleException ex, final WebRequest request) {
        return handleNormalException(ex, request, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler({PropertyUserVisibleException.class})
    public ResponseEntity<Object> handle(final PropertyUserVisibleException ex, final WebRequest request) {
        PropertyUserVisibleException exception = ex;
        if (this.adapter.isTarget(ex)) {
            exception = this.adapter.adapt(ex);
        }
        PropertyExceptionDetail detail = new PropertyExceptionDetail(exception.getObject(), exception.getPropertyPath(), exception.getMessage());
        return handleNormalException(exception, request, HttpStatus.BAD_REQUEST, detail);
    }

    // 401

//    @ExceptionHandler({NotAuthenticatedException.class})
//    public ResponseEntity<Object> handleNotAuthenticatedException(final NotAuthenticatedException ex, final WebRequest request) {
//        NotAuthenticatedExceptionDetail detail = new NotAuthenticatedExceptionDetail(ex);
//        return commonExceptionHandler(ex, request, HttpStatus.UNAUTHORIZED, detail, null);
//    }
//
//    @ExceptionHandler({AuthenticationException.class})
//    public ResponseEntity<Object> handleAuthenticationException(final AuthenticationException ex, final WebRequest request) {
//        NotAuthenticatedExceptionDetail detail = new NotAuthenticatedExceptionDetail(ex);
//        return commonExceptionHandler(ex, request, HttpStatus.UNAUTHORIZED, detail, NotAuthenticatedException.class.getSimpleName());
//    }

    // 403

//    @ExceptionHandler({AccessDeniedException.class})
//    public ResponseEntity<Object> handleAccessDeniedException(final AccessDeniedException ex, final WebRequest request) {
//        AccessDeniedExceptionDetail detail = new AccessDeniedExceptionDetail(ex);
//        return commonExceptionHandler(ex, request, HttpStatus.FORBIDDEN, detail, null);
//    }

    // 404...

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<Object> handle(final ResourceNotFoundException ex, final WebRequest request) {
        return handleNormalException(ex, request, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // 409...

//    @ExceptionHandler({GlobalResourceAlreadyExistException.class})
//    public ResponseEntity<Object> handleGlobalResourceAlreadyExistException(final GlobalResourceAlreadyExistException ex, final WebRequest request) {
//        String exceptionName = ex.getClass().getSimpleName();
//        log.debug("Processing request [{}] : resource [{}] with name [{}] already exist",
//                request.getDescription(true), ex.getResourceClass().getSimpleName(), ex.getResourceName());
//        Status status = Status.withDefaultApiAndKind()
//                .code(HttpStatus.CONFLICT.value())
//                .type(exceptionName)
//                .detail(null);
//
//        return new ResponseEntity<>(status, HttpStatus.CONFLICT);
//    }

//    @ExceptionHandler({GroupResourceAlreadyExistException.class})
//    public ResponseEntity<Object> handleGroupResourceAlreadyExistException(final GroupResourceAlreadyExistException ex, final WebRequest request) {
//        String exceptionName = ex.getClass().getSimpleName();
//        log.debug("Processing request [{}] : resource [{}] with group [{}], name [{}] already exist",
//                request.getDescription(true), ex.getResourceClass().getSimpleName(), ex.getGroupName(),
//                ex.getResourceName());
//        Status status = Status.withDefaultApiAndKind()
//                .code(HttpStatus.CONFLICT.value())
//                .type(exceptionName)
//                .detail(null);
//
//        return new ResponseEntity<>(status, HttpStatus.CONFLICT);
//    }

    @ExceptionHandler({ResourceConflictException.class})
    public ResponseEntity<Object> handle(final ResourceConflictException ex, final WebRequest request) {
        return handleNormalException(ex, request, HttpStatus.CONFLICT, ex.getMessage());
    }

    // 500
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAbnormalException(final Exception ex, final WebRequest request) {
        logAsError(ex, request);
        Status status = Status.builder()
                .type("InternalServerError")
                .detail(null)
                .build();

        return new ResponseEntity<>(status, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Non handlers...

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex,
            HttpHeaders headers,
            HttpStatus httpStatus,
            WebRequest request) {
        return handleNormalException(ex, request, HttpStatus.NOT_FOUND, null);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            @Nullable Object body,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        return handleNormalException(ex, request, status, null);
    }

    private ResponseEntity<Object> handleNormalException(
            final Exception ex,
            final WebRequest request,
            HttpStatus httpStatus,
            @Nullable Object detail) {
        logAsDebug(ex, request);
        Status status = Status.builder()
                .type(ex.getClass().getSimpleName())
                .detail(detail)
                .build();

        return new ResponseEntity<>(status, httpStatus);
    }

}
