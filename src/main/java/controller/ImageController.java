package controller;

import dao.JobDao;
import listener.Ec2Instantiator;
import listener.ImageRequestQueueListener;
import model.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pool.Ec2InstancePoolManager;
import service.JobService;
import service.SqsService;
import service.UploadService;

import java.util.List;

@CrossOrigin("http://localhost:4200")
@RestController()
@RequestMapping(value = "/image-service")
public class ImageController {

    private final static Logger LOGGER = LoggerFactory.getLogger(ImageController.class);

    private JobService jobService;
    private UploadService uploadService;
    private SqsService sqsService;
    private JobDao jobDao;
    private Ec2InstancePoolManager poolManager;
    private ImageRequestQueueListener imageRequestQueueListener;
    private Ec2Instantiator ec2Instantiator;

    @Value("${amazon.sqs.request.queue.name}")
    private String requestQueueName;

    @Value("${amazon.sqs.request.queue.message.group.id}")
    private String requestQueueGroupId;

    @Autowired
    public ImageController(JobService jobService, UploadService uploadService, SqsService sqsService,
                           JobDao jobDao, Ec2InstancePoolManager poolManager, ImageRequestQueueListener imageRequestQueueListener,
                           Ec2Instantiator ec2Instantiator) {
        this.jobService = jobService;
        this.uploadService = uploadService;
        this.sqsService = sqsService;
        this.jobDao = jobDao;
        this.poolManager = poolManager;
        this.imageRequestQueueListener = imageRequestQueueListener;
        this.ec2Instantiator = ec2Instantiator;

        Thread ec2InstantiatorThread = new Thread(ec2Instantiator);
        ec2InstantiatorThread.start();
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

        jobDao.createJob(job);
        uploadService.uploadFile(job.getFilePath(), file);
        sqsService.insertToQueue(job.getId(), this.requestQueueName);


        LOGGER.info("Create job successful with jobId={}", job.getId());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/images")
    List<Job> getJobs() {
        return jobDao.getJobs();
    }


}
