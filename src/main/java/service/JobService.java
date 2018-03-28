package service;

import jdk.nashorn.internal.scripts.JO;
import model.Job;
import org.springframework.web.multipart.MultipartFile;

public interface JobService {
    Job createJob(String url, MultipartFile file);
    String getJobResult(String jobId);
}
