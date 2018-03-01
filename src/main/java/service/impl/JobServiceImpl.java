package service.impl;

import model.Job;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import service.JobService;

import java.util.UUID;

@Service
public class JobServiceImpl implements JobService {
    @Override
    public Job createJob(String url, MultipartFile file) {
        Job job = new Job();
        if (!StringUtils.isEmpty(url)) {
            job.setUrl(url);
        }

        if (file != null) {
            job.setInputFilename(file.getOriginalFilename());
            job.setFileName(job.getSubmitDateTime() + file.getOriginalFilename());
        }
        return job;
    }
}
