package com.multimodalrag.api.documents;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class DocumentStorageService {
    private final Path storageDirectory;
    public DocumentStorageService(
            @Value("${app.storage.directory:storage/documents}")
            String storageDirectory
    )throws IOException {
        this.storageDirectory= Paths.get(storageDirectory)
                .toAbsolutePath()
                .normalize();

        Files.createDirectories(this.storageDirectory);
    }

    public String store(MultipartFile file) throws IOException {
        String storedFilename= UUID.randomUUID()+".pdf";
        Path destination=storageDirectory
                .resolve(storedFilename)
                .normalize();
        if(!destination.getParent().equals(storageDirectory)){
            throw new IOException("Invalid storage path");
        }
        Files.copy(
                file.getInputStream(),
                destination,
                StandardCopyOption.REPLACE_EXISTING
        );

        return storedFilename;
    }
    public void delete(String storedfilename)throws IOException{
        Path filePath=storageDirectory
                .resolve(storedfilename)
                .normalize();
        if (!filePath.getParent().equals(storageDirectory)){
            throw new IOException("Invalid storage path");
        }
        Files.deleteIfExists(filePath);
    }

}
