package com.multimodalrag.api.documents;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.time.Instant;

@RestControllerAdvice
public class DocumentExceptionHandler {
    public record ErrorResponse(
            Instant timestamp,
            int status,
            String error,
            String message
    ){}
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse>handleBadRequest(
            IllegalArgumentException e
    ){
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                e.getMessage()
        );
    }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse>handleMaxUploadSize(
           MaxUploadSizeExceededException e
    ){
        return buildResponse(
                HttpStatus.PAYLOAD_TOO_LARGE,
                "The uploaded file exceeds the allowed size limit."
        );
    }
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(
            IOException exception
    ) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An error occurred while storing or reading the uploaded file."
        );
    }
    private ResponseEntity<ErrorResponse>buildResponse(
            HttpStatus status,
            String message
    ){
        ErrorResponse response=new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message
        );
        return ResponseEntity.status(status).body(response);
    }
    
}
