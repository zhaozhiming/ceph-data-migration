package com.github.zzm.migration.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class RadosGatewayS3Client {
    private AmazonS3 connect;

    public RadosGatewayS3Client(String accessKey, String secretKey, String hostname) {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        connect= new AmazonS3Client(credentials);
        connect.setEndpoint(hostname);
    }

    public List<Bucket> listBuckets() {
        return connect.listBuckets();
    }

    public Bucket createBucket(String bucketName) {
        return connect.createBucket(bucketName);
    }

    public ObjectListing listObjects(String bucketName) {
        return connect.listObjects(bucketName);
    }

    public PutObjectResult putObject(File file, Bucket bucket) throws FileNotFoundException {
        return connect.putObject(
                bucket.getName(), file.getName(), new FileInputStream(file), new ObjectMetadata());
    }

    public S3Object getObject(String bucketName, String objectName) {
        return connect.getObject(bucketName, objectName);
    }

    public ObjectMetadata downloadObject(String bucketName, String objectName, File downloadFile) {
        return connect.getObject(new GetObjectRequest(bucketName, objectName), downloadFile);
    }

    public void deleteObject(String bucketName, String fileName) {
        connect.deleteObject(bucketName, fileName);
    }

    public boolean isBucketExist(String bucketName) {
        List<Bucket> buckets = listBuckets();
        for (Bucket bucket : buckets) {
            if (bucketName.equals(bucket.getName())) {
                return true;
            }
        }
        return false;
    }
}
