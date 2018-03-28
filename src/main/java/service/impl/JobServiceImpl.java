package service.impl;

import constants.ServiceConstants;
import controller.ImageController;
import dao.JobDao;
import model.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import service.JobService;

import java.util.Arrays;
import java.util.UUID;

@Service
public class JobServiceImpl implements JobService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ImageController.class);

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
            job.setInputFilename(Arrays.stream(url.split("/"))
                    .reduce((first, second) -> second)
                    .orElse(null));
        } else if (file != null) {
            job.setInputFilename(file.getOriginalFilename());
            job.setFilePath(getImageFolder() + ServiceConstants.PATH_SEPARATOR + job.getSubmitDateTime()
                    + file.getOriginalFilename());
            job.setUrl(getS3BaseUrl() + ServiceConstants.PATH_SEPARATOR + getBucketName()
                    + ServiceConstants.PATH_SEPARATOR + job.getFilePath());
        }
        return job;
    }


    public String getJobResult(String jobId)  {
       boolean jobInProgress=true;
       Job updatedJob=null;
       while(jobInProgress) {
           updatedJob = jobDao.getJob(jobId);

           try {
               Thread.sleep(1000);
           }
           catch (Exception e)
           {
               e.printStackTrace();
           }
           LOGGER.info("updated job"+updatedJob.toString());
           System.out.println(updatedJob.getStatus());
           if(updatedJob!=null && updatedJob.getResult()!=null && updatedJob.getStatus().equals("completed"))
           {
               jobInProgress=false;
           }
       }
       return updatedJob.getResult();
   }

   @Autowired
    JobDao jobDao;



}
