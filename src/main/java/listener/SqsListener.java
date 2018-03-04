package listener;

import com.amazonaws.services.sqs.model.Message;
import constants.ServiceConstants;
import model.Ec2Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import pool.Ec2InstancePoolManager;
import service.Ec2Service;
import service.SqsService;
import service.impl.Ec2ServiceImpl;

import java.util.List;

public class SqsListener implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(SqsListener.class);

    private Ec2InstancePoolManager poolManager;

    private SqsService sqsService;


    public SqsListener() {
    }

    public SqsListener(SqsService sqsService, Ec2InstancePoolManager poolManager) {
        this.sqsService = sqsService;
        this.poolManager = poolManager;
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
                    // TODO Add logic to check sqs size and create instances based on that.
                    try {
                        createInstances();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    sqsService.deleteMessage(messageReceiptHandle);
                }
            }
        }

    }

    public void createInstances() throws Exception {
        // TODO Modify
        Ec2Instance ec2Instance1 = (Ec2Instance) poolManager.getPool().borrowObject();
        Ec2Service ec2Service1 = new Ec2ServiceImpl(ec2Instance1);
        LOGGER.info("EC2 Instance created, InstanceId={}", ec2Service1.createInstance());
    }
}
