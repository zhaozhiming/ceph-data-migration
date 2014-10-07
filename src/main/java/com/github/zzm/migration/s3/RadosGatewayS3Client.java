package com.github.zzm.migration.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class RadosGatewayS3Client {
    private AmazonS3 connect;

    public RadosGatewayS3Client(String accessKey, String secretKey, String hostname) {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        connect = new AmazonS3Client(credentials);
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

    public PutObjectResult putObject(String bucketName, String fileName,
                                     InputStream inputStream, ObjectMetadata objectMetadata)
            throws FileNotFoundException {
        return connect.putObject(
                bucketName, fileName, inputStream, objectMetadata);
    }

    public S3Object getObject(String bucketName, String objectName) {
        return connect.getObject(bucketName, objectName);
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
