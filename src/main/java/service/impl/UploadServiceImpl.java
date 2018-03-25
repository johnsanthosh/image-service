package service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import controller.ImageController;
import model.Job;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import service.UploadService;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Service
public class UploadServiceImpl implements UploadService {


    private final static Logger LOGGER = LoggerFactory.getLogger(UploadServiceImpl.class);

    AmazonS3 s3Client;

    @Value("${amazon.s3.bucket.name}")
    private String bucketName;


    @Value("${amazon.s3.bucket.job.folder.name}")
    private String jobFolder;

    @Value("${amazon.s3.bucket.output.file.name}")
    private String resultFile;

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
    public void uploadResultToS3(String fileContent) {
        try {

            StringBuilder existingFileContent = new StringBuilder(getFileContentsFromS3());
            existingFileContent.append(fileContent);

            File tempFile = File.createTempFile("temp", "temp");
            InputStream stream = new ByteArrayInputStream(existingFileContent.toString().getBytes(StandardCharsets.UTF_8));

            OutputStream outputStream = new FileOutputStream(tempFile);
            IOUtils.copy(stream, outputStream);
            outputStream.close();

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, resultFile, tempFile);
            s3Client.putObject(putObjectRequest);
            if(tempFile.exists()) {
                tempFile.delete();
            }

        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void createResultFile()
    {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("temp", "temp");
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, resultFile, tempFile);
            s3Client.putObject(putObjectRequest);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private boolean checkFileExistence() {
        ListObjectsV2Request listRequest = new ListObjectsV2Request().withBucketName(bucketName);
        ListObjectsV2Result listResult = s3Client.listObjectsV2(listRequest);
        for (S3ObjectSummary s3ObjectSummary : listResult.getObjectSummaries()) {
            if (s3ObjectSummary.getKey().equals(resultFile)) {
                return true;
            }
        }
        return false;
    }

    private String getFileContentsFromS3() {

        if(!checkFileExistence()) {
            createResultFile();
        }

        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, resultFile);
        S3Object s3Object = s3Client.getObject(getObjectRequest);

        StringBuilder contentsOfFile = new StringBuilder();
        BufferedReader bReader = new BufferedReader(new InputStreamReader(s3Object.getObjectContent()));
        String oneLine;
        try {
            while((oneLine = bReader.readLine()) != null) {
                contentsOfFile.append(oneLine);
                contentsOfFile.append(System.lineSeparator());
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        return contentsOfFile.toString();
    }

    @Override
    public void createJob(Job job) {

    }
}
