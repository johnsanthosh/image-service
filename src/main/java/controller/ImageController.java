package controller;

import dao.JobDao;
import model.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import service.JobService;
import service.SqsService;
import service.UploadService;

import java.net.URI;

@RestController()
@RequestMapping(value = "/image-service")
public class ImageController {

    private final static Logger LOGGER = LoggerFactory.getLogger(ImageController.class);

    private JobService jobService;
    private UploadService uploadService;
    private SqsService sqsService;
    private JobDao jobDao;

    @Autowired
    public ImageController(JobService jobService, UploadService uploadService, SqsService sqsService, JobDao jobDao) {
        this.jobService = jobService;
        this.uploadService = uploadService;
        this.sqsService = sqsService;
        this.jobDao = jobDao;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/")
    String home() {
        return "Image Service is running.";
    }

    @RequestMapping(value = {"/images"},
            method = RequestMethod.POST,
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void create(
            @RequestPart(required = false, name = "url") String url,
            @RequestParam(required = false, name = "image") MultipartFile file) throws Exception {

        Job job = jobService.createJob(url, file);
        LOGGER.info("Create job requested with jobId={}", job.getId());

        uploadService.uploadFile(job.getFilePath(), file);
        sqsService.insertToQueue(job.getId());
        jobDao.createJob(job);

        LOGGER.info("Create job successful with jobId={}", job.getId());
    }


}
