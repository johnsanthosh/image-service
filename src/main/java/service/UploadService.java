package service;

import model.Job;
import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
    void uploadFile(String fileName, MultipartFile multipartFile);

    void createJob(Job job);
}
