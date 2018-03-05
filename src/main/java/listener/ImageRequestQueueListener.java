package listener;

import com.amazonaws.services.sqs.model.Message;
import constants.ServiceConstants;
import model.Ec2Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pool.Ec2InstancePoolManager;
import service.Ec2Service;
import service.SqsService;
import service.impl.Ec2ServiceImpl;

import java.util.List;

@Component
public class ImageRequestQueueListener implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(ImageRequestQueueListener.class);

    private Ec2InstancePoolManager poolManager;

    private SqsService sqsService;

    public ImageRequestQueueListener() {
    }

    @Autowired
    public ImageRequestQueueListener(SqsService sqsService, Ec2InstancePoolManager poolManager) {
        this.sqsService = sqsService;
        this.poolManager = poolManager;
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
                        LOGGER.info("ImageRequestQueueListener thread sleeping for time={}ms.", ServiceConstants.LISTENER_SLEEP_TIME);
                        Thread.sleep(ServiceConstants.LISTENER_SLEEP_TIME);
                    } catch (InterruptedException e) {
                        LOGGER.error(e.getMessage());
                    }
                } else {
                    String messageReceiptHandle = messages.get(0).getReceiptHandle();
                    messages.forEach(message -> LOGGER.info("Message body={}", message.getBody()));
                    // TODO Add logic to invoke BatchExecutorService.
                    sqsService.deleteMessage(messageReceiptHandle);
                    try {
                        LOGGER.info("ImageRequestQueueListener thread sleeping for time={}ms.", ServiceConstants.LISTENER_SLEEP_TIME);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        LOGGER.error(e.getMessage());
                    }
                }
            }
        }

    }

}
