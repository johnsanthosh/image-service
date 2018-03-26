package service;

import model.Job;
import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
    void uploadFile(String fileName, MultipartFile multipartFile);

    void createJob(Job job);

    void uploadResultToS3(String fileContent);

    void putResultAsKeyValuePairs(String fileContent);
}
