package service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import model.Job;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import service.UploadService;

import java.io.File;
import java.io.IOException;

@Service
public class UploadServiceImpl implements UploadService {

    AmazonS3 s3Client;

    @Value("${amazon.s3.bucket.name}")
    private String bucketName;

    @Value("${amazon.s3.bucket.image.folder.name}")
    private String imageFolder;

    @Value("${amazon.s3.bucket.job.folder.name}")
    private String jobFolder;

    public UploadServiceImpl() {
        this.s3Client = new AmazonS3Client();
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getImageFolder() {
        return imageFolder;
    }

    public void setImageFolder(String imageFolder) {
        this.imageFolder = imageFolder;
    }

    public String getJobFolder() {
        return jobFolder;
    }

    public void setJobFolder(String jobFolder) {
        this.jobFolder = jobFolder;
    }

    public AmazonS3 getS3Client() {
        return s3Client;
    }

    public void setS3Client(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }


    @Override
    public void uploadFile(String fileName, MultipartFile multipartFile) {
        try {
            s3Client.putObject(new PutObjectRequest(bucketName, getImageFolder() + '/' + fileName, multipartFile.getInputStream(), new ObjectMetadata()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createJob(Job job) {

    }
}
