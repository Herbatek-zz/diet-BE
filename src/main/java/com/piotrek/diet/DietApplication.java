package com.piotrek.diet;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileOutputStream;

@SpringBootApplication
public class DietApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(DietApplication.class, args);
    }

    @Autowired
    private CloudStorageAccount cloudStorageAccount;

    final String containerName = "diet-images";

    public void run(String... var1) {
//        readImageBlob(containerName);
        uploadImageBlob(containerName);
    }

    private void uploadImageBlob(String containerName) {
        try {
            // Create a blob client.
            final CloudBlobClient blobClient = cloudStorageAccount.createCloudBlobClient();
            // Get a reference to a container. (Name must be lower case.)
            final CloudBlobContainer container = blobClient.getContainerReference(containerName);
            // Get a blob reference for a text file.
            CloudBlockBlob blob = container.getBlockBlobReference("kot.jpeg");
            // Upload some text into the blob.
//            blob.uploadText("Działam w chmurze!");
            blob.getProperties().setContentType("image/jpeg");
            blob.uploadFromFile("C:\\Users\\Piotrek\\Desktop\\a.jpeg");

            String etag = blob.getProperties().getEtag();
            System.out.println(etag);
            System.out.println(blob.getUri());
        } catch (Exception e) {
            // Output the stack trace.
            e.printStackTrace();
        }
    }

    private void readImageBlob(String containerName) {
        try {
            final CloudBlobClient blobClient = cloudStorageAccount.createCloudBlobClient();
            final CloudBlobContainer container = blobClient.getContainerReference(containerName);
//            CloudBlockBlob blob = container.getBlockBlobReference("kot.jpeg");
            CloudBlob blob = container.getBlobReferenceFromServer("kot.jpeg");
            blob.download(new FileOutputStream("C:\\Users\\Piotrek\\Desktop\\kot.jpeg"));

        } catch (Exception e) {
            // Output the stack trace.
            e.printStackTrace();
        }
    }
}

