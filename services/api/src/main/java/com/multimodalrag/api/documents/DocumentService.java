package com.multimodalrag.api.documents;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {
    private static final long MAX_FILE_SIZE=25*1024*1024;

    private final DocumentRepository documentRepository;
    private final DocumentStorageService documentStorageService;

    public DocumentService(
            DocumentRepository documentRepository,
            DocumentStorageService documentStorageService
    ){
        this.documentRepository=documentRepository;
        this.documentStorageService=documentStorageService;
    }
    @Transactional
    public DocumentEntity uploadDocument(MultipartFile file)throws IOException {
        validatePdf(file);

        String storedFilename= documentStorageService.store(file);

        try{
            DocumentEntity document=new DocumentEntity(

                    file.getOriginalFilename(),
                    storedFilename,
                    "application/pdf",
                    file.getSize()
            );
            return documentRepository.save(document);
        } catch (RuntimeException e) {
            documentStorageService.delete(storedFilename);
            throw e;
        }
    }
    @Transactional(readOnly=true)
    public List<DocumentEntity> getAllDocuments(){
        return documentRepository.findAll();
    }
    @Transactional(readOnly=true)
    public DocumentEntity getDocumentById(UUID id){
        return documentRepository.findById(id)
                .orElseThrow(()->
                        new IllegalArgumentException(
                                "Document not found: "+id
                        ));
    }
    private void validatePdf(MultipartFile file) throws IOException{

        if(file==null || file.isEmpty()){
            throw new IllegalArgumentException(
                    "Please upload a non-empty PDF file."
            );
        }
        if(file.getSize() > MAX_FILE_SIZE){
            throw new IllegalArgumentException(
                    "File size must not exceed 25 MB."
            );
        }
        String originalFilename=file.getOriginalFilename();

        if(originalFilename==null || !originalFilename.toLowerCase()
                .endsWith(".pdf")){
            throw new IllegalArgumentException(
                    "Only PDF files are allowed."
            );
        }
        byte[] header=new byte[5];
        try(InputStream inputStream=file.getInputStream()){
            int byteRead=inputStream.read(header);

            if(byteRead!=5 || header[0]!='%' ||
             header[1]!='P'||header[2]!='D'||header[3]!='F'||header[4]!='-'){
                throw new IllegalArgumentException("The uploaded file does not have a valid PDF header.");
            }
        }
    }
}
