package org.example;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    private static final String CONNECTION_STRING = "DefaultEndpointsProtocol=https;AccountName=sensor1data;AccountKey=zin++eomthOe501JF2P7VJefVr646GhbuCMbqaMyMfdl59eH6n3fwIbGKzuzbnfyg61aRqE1cjsv+ASt5YbUjQ==;EndpointSuffix=core.windows.net" ;
    private static final String CONTAINER_NAME = "onlinesensordata";
    private static final String BLOB_NAME = "0_161b469607c84a04a037505b8ebeaca3_1.json";

    public static void main(String[] args) {
        downloadBlob(CONNECTION_STRING, CONTAINER_NAME, BLOB_NAME);
    }

    private static void downloadBlob(String connectionString, String containerName, String blobName) {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
        try {
            BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);

            File downloadFile = Paths.get(blobName).toFile();
            BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
            OutputStream outputStream = new FileOutputStream(downloadFile);

            try {
                blobClient.download(outputStream);
                System.out.println("Blob downloaded successfully.");

                // Parse the JSON content using GSON
                Gson gson = new Gson();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(downloadFile)))) {
                    JsonElement jsonElement = gson.fromJson(reader, JsonElement.class);
                    if (jsonElement.isJsonObject()) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        JsonArray jsonArray = jsonObject.getAsJsonArray("data");

                        // Output the parsed data
                        System.out.println("Parsed data:");
                        for (JsonElement item : jsonArray) {
                            System.out.println(item.toString());
                        }
                    } else {
                        System.out.println("Invalid JSON format");
                    }
                }
            } finally {
                outputStream.close();
            }
        } catch (IOException e) {
            // Log the exception using a logging framework
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, "Error downloading blob", e);
        }
    }
}