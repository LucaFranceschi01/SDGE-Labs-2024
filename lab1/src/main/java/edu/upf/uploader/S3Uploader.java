package edu.upf.uploader;

import java.io.File;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class S3Uploader implements Uploader {
    private final String bucketName, prefix;
    private final AWSCredentials credentials;

    public S3Uploader(String bucketName, String prefix) {
        this.bucketName = bucketName;
        this.prefix = prefix;

        EnvironmentVariableCredentialsProvider credentialsProvider = new EnvironmentVariableCredentialsProvider();
        this.credentials = credentialsProvider.getCredentials();
    }

    public S3Uploader(String bucketName, String prefix, String profile) throws IllegalArgumentException{
        this.bucketName = bucketName;
        this.prefix = prefix;

        ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider(profile);
        this.credentials = credentialsProvider.getCredentials();
    }

    @Override
    public void upload(List<String> files) {
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                            .withCredentials(new AWSStaticCredentialsProvider(credentials))
                            .build();

        String key;
        File file;
        PutObjectRequest request;
        String[] splitted_name;
        
        for (String name : files) {
            splitted_name = name.split("/");
            key = this.prefix + '/' + splitted_name[splitted_name.length-1];
            file = new File(name);
            request = new PutObjectRequest(this.bucketName, key, file);
            s3Client.putObject(request);
        }
    }
}
