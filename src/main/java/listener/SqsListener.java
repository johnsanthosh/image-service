package listener;

import com.amazonaws.services.sqs.model.Message;
import constants.ServiceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import service.SqsService;

import java.util.List;

public class SqsListener implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(SqsListener.class);

    private SqsService sqsService;

    public SqsListener() {
    }

    public SqsListener(SqsService sqsService) {
        this.sqsService = sqsService;
    }

    @Override
    public void run() {
        while (true) {
            LOGGER.info("Polling SQS.");
            List<Message> messages = null;

            if (sqsService != null) {
                messages = sqsService.getMessages();

                if (CollectionUtils.isEmpty(messages)) {
                    try {
                        LOGGER.info("Listener thread sleeping for time={}ms.", ServiceConstants.LISTENER_SLEEP_TIME);
                        Thread.sleep(ServiceConstants.LISTENER_SLEEP_TIME);
                    } catch (InterruptedException e) {
                        LOGGER.error(e.getMessage());
                    }
                } else {
                    String messageReceiptHandle = messages.get(0).getReceiptHandle();
                    messages.forEach(message -> LOGGER.info("Message body={}", message.getBody()));
                    // Add logic for executing python code here.
                    sqsService.deleteMessage(messageReceiptHandle);
                }
            }
        }

    }
}
