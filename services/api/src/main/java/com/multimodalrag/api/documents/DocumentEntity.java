package com.multimodalrag.api.documents;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="documents")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DocumentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String storedFilename;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private long sizeBytes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status;

    @Column(length = 2000)
    private String errorMessage;

    @Column(nullable = false,updatable = false)
    private Instant createdAt;

    protected DocumentEntity(
            String originalFilename,
            String storedFilename,
            String contentType,
            long sizeBytes
    ){
        this.originalFilename=originalFilename;
        this.storedFilename=storedFilename;
        this.contentType=contentType;
        this.sizeBytes=sizeBytes;
        this.status=DocumentStatus.UPLOADED;
        this.createdAt=Instant.now();
    }
}
