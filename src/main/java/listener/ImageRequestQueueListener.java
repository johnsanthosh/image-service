package listener;

import com.amazonaws.services.sqs.model.Message;
import constants.ServiceConstants;
import dao.JobDao;
import model.Ec2Instance;
import model.Job;
import model.JobStatus;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import pool.Ec2InstancePoolManager;
import service.BashExecuterService;
import service.Ec2Service;
import service.SqsService;
import service.UploadService;
import service.impl.Ec2ServiceImpl;

import java.util.List;

@Component
public class ImageRequestQueueListener implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(ImageRequestQueueListener.class);

    private Ec2InstancePoolManager poolManager;

    private SqsService sqsService;

    private BashExecuterService bashExecuterService;

    private JobDao jobDao;

    private UploadService uploadService;

    @Value("${amazon.sqs.request.queue.name}")
    private String requestQueueName;

    @Value("${amazon.sqs.request.queue.message.group.id}")
    private String requestQueueGroupId;

    @Value("${sleep.time.max}")
    private int maxSleepTime;

    @Value("${sleep.time.min}")
    private int minSleepTime;

    public ImageRequestQueueListener() {
    }

    @Autowired
    public ImageRequestQueueListener(SqsService sqsService, Ec2InstancePoolManager poolManager,
                                     BashExecuterService bashExecuterService, JobDao jobDao, UploadService uploadService) {
        this.sqsService = sqsService;
        this.poolManager = poolManager;
        this.bashExecuterService = bashExecuterService;
        this.jobDao = jobDao;
        this.uploadService = uploadService;
    }

    @Override
    public void run() {
        while (true) {

            if (sqsService != null) {
                pollRequestQueue();
            }
        }

    }

    private void pollRequestQueue() {
        LOGGER.info("ImageRequestQueueListener : Polling request SQS for messages.");
        List<Message> messages = null;

        messages = sqsService.getMessages(this.requestQueueName);

        if (CollectionUtils.isEmpty(messages)) {
            try {
                LOGGER.info("ImageRequestQueueListener : thread sleeping for time={}ms.", this.maxSleepTime);
                Thread.sleep(this.maxSleepTime);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage());
            }
        } else {
            String messageBody = messages.get(0).getBody();
            LOGGER.info("ImageRequestQueueListener : Request queue Message body={}", messageBody);

            // Fetches job record from MongoDB.
            Job job = jobDao.getJob(messageBody);
            String result = "";

            if (job == null) {
                LOGGER.info("ImageRequestQueueListener : Unable to retrieve job with id={} record from Mongo,", messageBody);
            } else {
                // Execute bash script to recognize image.
                result = bashExecuterService.recognizeImage(job.getUrl());

                if (StringUtils.isEmpty(result)) {
                    LOGGER.info("ImageRequestQueueListener : Result computation for jobId={} failed.", messageBody);
                    job.setResult(result);
                    job.setStatus(JobStatus.FAILED);
                } else {
                    LOGGER.info("ImageRequestQueueListener : Result computed for jobId={}, result={}", job.getId(), result);
                    job.setCompletedDateTime(DateTime.now(DateTimeZone.UTC));
                    job.setStatus(JobStatus.COMPLETE);

                    // Deletes message from the queue on successful result computation.
                    String messageReceiptHandle = messages.get(0).getReceiptHandle();
                    sqsService.deleteMessage(messageReceiptHandle, this.requestQueueName);

                    String resultString = "[" + job.getInputFilename() + "," + result.split("\\(score")[0] + "]";

                    //Appends to a file (actually replaces the file for every request). Doesn't ensure correctness of concurrent requests.
                    uploadService.uploadResultToS3(resultString);
                    //Writes as key value pairs to a different bucket. Key = resultString and content also is resultString here.
                    uploadService.putResultAsKeyValuePairs(resultString);
                }

                // Updates job record in MongoDB.
                jobDao.updateJob(job);
            }

            try {
                LOGGER.info("ImageRequestQueueListener : thread sleeping for time={}ms.", this.minSleepTime);
                Thread.sleep(this.minSleepTime);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

}
