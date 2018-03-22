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

    public ImageRequestQueueListener() {
    }

    //TODO: understand this
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
            LOGGER.info("Polling SQS for messages.");
            List<Message> messages = null;

            if (sqsService != null) {
                messages = sqsService.getMessages();

                if (CollectionUtils.isEmpty(messages)) {
                    try {
                        LOGGER.info("ImageRequestQueueListener thread sleeping for time={}ms.", ServiceConstants.SLEEP_TIME_20S);
                        Thread.sleep(ServiceConstants.SLEEP_TIME_20S);
                    } catch (InterruptedException e) {
                        LOGGER.error(e.getMessage());
                    }
                } else {
                    String messageBody = messages.get(0).getBody();
                    LOGGER.info("Message body={}", messageBody);

                    // Fetches job record from MongoDB.
                    Job job = jobDao.getJob(messageBody);

                    // Execute bash script to recognize image.
                    String result = bashExecuterService.recognizeImage(job.getUrl());
                    LOGGER.info("Result computed for jobId={}, result={}", job.getId(), result);
                    job.setResult(result);

                    if(StringUtils.isEmpty(result)) {
                        job.setStatus(JobStatus.FAILED);
                    }
                    else {
                        job.setStatus(JobStatus.COMPLETE);
                    }

                    job.setCompletedDateTime(DateTime.now(DateTimeZone.UTC));

                    // Updates job record in MongoDB.
                    jobDao.updateJob(job);

                    String resultString = "[" + job.getInputFilename() + "," + result.split("\\(score")[0] + "]";
                    uploadService.uploadResultToS3(resultString);

                    // Deletes message from the queue.
                    String messageReceiptHandle = messages.get(0).getReceiptHandle();
                    sqsService.deleteMessage(messageReceiptHandle);

                    try {
                        LOGGER.info("ImageRequestQueueListener thread sleeping for time={}ms.", ServiceConstants.SLEEP_TIME_1S);
                        Thread.sleep(ServiceConstants.SLEEP_TIME_1S);
                    } catch (InterruptedException e) {
                        LOGGER.error(e.getMessage());
                    }
                }
            }
        }

    }

}
