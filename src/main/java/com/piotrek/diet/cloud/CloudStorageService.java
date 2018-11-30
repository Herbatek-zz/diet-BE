package com.piotrek.diet.cloud;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CloudStorageService {

    final private CloudStorageAccount cloudStorageAccount;

    public String uploadImageBlob(String containerName, String imageName, MultipartFile image) {
        String url = null;
        try {
            final CloudBlobClient blobClient = cloudStorageAccount.createCloudBlobClient();
            final CloudBlobContainer container = blobClient.getContainerReference(containerName);
            final CloudBlockBlob blob = container.getBlockBlobReference(imageName);
            blob.getProperties().setContentType("image/jpeg");
            blob.uploadFromByteArray(image.getBytes(), 0, image.getBytes().length);
            url = blob.getUri().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

}
