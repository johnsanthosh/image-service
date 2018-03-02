package service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import controller.ImageController;
import model.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import service.UploadService;

import java.io.File;
import java.io.IOException;

@Service
public class UploadServiceImpl implements UploadService {


    private final static Logger LOGGER = LoggerFactory.getLogger(UploadServiceImpl.class);

    AmazonS3 s3Client;

    @Value("${amazon.s3.bucket.name}")
    private String bucketName;


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
    public void uploadFile(String filePath, MultipartFile multipartFile) {
        try {
            s3Client.putObject(new PutObjectRequest(bucketName, filePath, multipartFile.getInputStream(), new ObjectMetadata()));
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void createJob(Job job) {

    }
}
