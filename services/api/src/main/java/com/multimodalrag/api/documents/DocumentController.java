package com.multimodalrag.api.documents;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {
    private final DocumentService documentService;

    public DocumentController(DocumentService documentService){
        this.documentService=documentService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentResponse uploadDocument(
            @RequestParam("file")MultipartFile file
            )throws IOException {
        DocumentEntity document=documentService.uploadDocument(file);
        return DocumentResponse.from(document);
    }
    @GetMapping
    public List<DocumentResponse> getAllDocuments(){
        return documentService.getAllDocuments()
                .stream()
                .map(DocumentResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public DocumentResponse getDocumentById(@PathVariable UUID id){
        try{
            DocumentEntity document=documentService.getDocumentById(id);

            return DocumentResponse.from(document);
        }catch (IllegalArgumentException exception){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage()
            );
        }
    }

    public record DocumentResponse(
            UUID id,
            String originalFilename,
            String contentType,
            long sizeBytes,
            DocumentStatus status,
            String errorMessage,
            java.time.Instant createdAt
    ){
        public static DocumentResponse from (DocumentEntity document){
            return new DocumentResponse(
                    document.getId(),
                    document.getOriginalFilename(),
                    document.getContentType(),
                    document.getSizeBytes(),
                    document.getStatus(),
                    document.getErrorMessage(),
                    document.getCreatedAt()
            );
        }
    }
}
