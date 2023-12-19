package com.github.flombois.exceptions;

import jakarta.ws.rs.core.Response;
import org.hibernate.exception.ConstraintViolationException;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

/**
 * Exception mapper class to handle specific exceptions and map them to appropriate HTTP responses.
 */
public class ExceptionMapper {


    /**
     * Maps a {@link ConstraintViolationException} to a Bad Request response (HTTP status 400).
     *
     * @param constraintViolationException The constraint violation exception to be mapped.
     * @return A Bad Request response containing the error message from the exception.
     */
    @ServerExceptionMapper
    public Response mapConstraintViolationException(ConstraintViolationException constraintViolationException) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(constraintViolationException.getErrorMessage())
                .build();
    }
}
