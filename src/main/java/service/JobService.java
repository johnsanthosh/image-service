package service;

import model.Job;
import org.springframework.web.multipart.MultipartFile;

public interface JobService {
    Job createJob(String url, MultipartFile file);
}
