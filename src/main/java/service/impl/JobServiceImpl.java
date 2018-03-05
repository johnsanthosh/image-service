package service.impl;

import constants.ServiceConstants;
import model.Job;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import service.JobService;

import java.util.UUID;

@Service
public class JobServiceImpl implements JobService {

    @Value("${amazon.s3.bucket.image.folder.name}")
    private String imageFolder;

    @Value("${amazon.s3.bucket.name}")
    private String bucketName;

    @Value("${amazon.s3.base.url}")
    private String s3BaseUrl;


    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getS3BaseUrl() {
        return s3BaseUrl;
    }

    public void setS3BaseUrl(String s3BaseUrl) {
        this.s3BaseUrl = s3BaseUrl;
    }

    public String getImageFolder() {
        return imageFolder;
    }

    public void setImageFolder(String imageFolder) {
        this.imageFolder = imageFolder;
    }

    @Override
    public Job createJob(String url, MultipartFile file) {
        Job job = new Job();
        if (!StringUtils.isEmpty(url)) {
            job.setUrl(url);
        }

        if (file != null) {
            job.setInputFilename(file.getOriginalFilename());
            job.setFilePath(getImageFolder() + ServiceConstants.PATH_SEPARATOR + job.getSubmitDateTime()
                    + file.getOriginalFilename());
            job.setUrl(getS3BaseUrl() + ServiceConstants.PATH_SEPARATOR + getBucketName()
                    + ServiceConstants.PATH_SEPARATOR + job.getFilePath());
        }
        return job;
    }
}
