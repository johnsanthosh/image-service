package listener;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.QueueAttributeName;
import constants.ServiceConstants;
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
import service.Ec2Service;
import service.SqsService;
import service.impl.Ec2ServiceImpl;

import java.util.List;
import java.util.Map;
import java.util.Stack;

@Component
public class Ec2Instantiator implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(Ec2Instantiator.class);

    private Ec2InstancePoolManager poolManager;

    private SqsService sqsService;

    private static Stack<Ec2Instance> ec2InstanceStack;

    @Value("${amazon.sqs.request.queue.name}")
    private String requestQueueName;

    @Value("${amazon.sqs.request.queue.message.group.id}")
    private String requestQueueGroupId;

    @Value("${amazon.sqs.instance.shutdown.queue.name}")
    private String instanceShutdownQueueName;

    @Value("${amazon.sqs.instance.shutdown.queue.message.group.id}")
    private String instanceShutdownQueueGroupId;

    @Value("${amazon.ec2.instance.max.count}")
    private int ec2MaxInstanceCount;

    @Value("${sleep.time.max}")
    private int maxSleepTime;

    @Value("${sleep.time.min}")
    private int minSleepTime;


    public Ec2Instantiator() {
    }

    @Autowired
    public Ec2Instantiator(SqsService sqsService, Ec2InstancePoolManager poolManager) {
        this.sqsService = sqsService;
        this.poolManager = poolManager;
        ec2InstanceStack = new Stack<Ec2Instance>();
    }

    @Override
    public void run() {
        while (true) {
            if (sqsService != null) {

                // Poll request queue attributes.
                pollRequestQueue();

                // Poll instance shutdown queue.
                pollInstanceShutdownQueue();

            }
        }

    }

    private void createInstances(int count) {
        Ec2Instance ec2Instance = null;
        for (int i = 0; i < count; i++) {
            try {
                ec2Instance = (Ec2Instance) poolManager.getPool().borrowObject();
                ec2InstanceStack.push(ec2Instance);
                Ec2Service ec2Service = new Ec2ServiceImpl(ec2Instance);
                LOGGER.info("Ec2Instantiator : EC2 Instance created, InstanceId={}",
                        ec2Service.createInstance());
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("Ec2Instantiator : Creating EC2 Instance failed. error={}", e.getMessage());
            }
        }
    }

    private void pollRequestQueue() {
        Map<String, String> requestQueueAttributeMap = null;
        LOGGER.info("Ec2Instantiator : Polling request SQS for queue size.");
        requestQueueAttributeMap = sqsService.getQueueAttributes(this.requestQueueName);

        if (!CollectionUtils.isEmpty(requestQueueAttributeMap)) {
            Long messageCount = Long.parseLong(requestQueueAttributeMap
                    .get(QueueAttributeName.ApproximateNumberOfMessages.toString()));
            LOGGER.info("Ec2Instantiator : Retrieving request queue attributes from SQS. messageCount={}"
                    , messageCount);
            long instancesCreated = poolManager.getPool().getCreatedCount() - poolManager.getPool().getDestroyedCount();
            LOGGER.info("EC2Instantiator : running instances={}.", instancesCreated);
            long extraInstancesNeeded = messageCount - instancesCreated;
            if (messageCount > 1 && extraInstancesNeeded > 0
                    && (extraInstancesNeeded + instancesCreated) <= this.ec2MaxInstanceCount) {
                createInstances((int) extraInstancesNeeded);
                LOGGER.info("Ec2Instantiator : Running instances={} after instance creation."
                        , poolManager.getPool().getCreatedCount() - poolManager.getPool().getDestroyedCount());
            }

            try {
                LOGGER.info("Ec2Instantiator : thread (request queue) sleeping for time={}ms.", this.maxSleepTime);
                Thread.sleep(this.maxSleepTime);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    private void pollInstanceShutdownQueue() {
        List<Message> messages = null;

        LOGGER.info("Ec2Instantiator : Polling instance-shutdown SQS for queue size.");
        messages = sqsService.getMessages(this.instanceShutdownQueueName);

        if (CollectionUtils.isEmpty(messages)) {
            try {
                LOGGER.info("Ec2Instantiator : thread (instance-shutdown) sleeping for time={}ms.",
                        this.maxSleepTime);
                Thread.sleep(this.maxSleepTime);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage());
            }
        } else {
            String messageBody = messages.get(0).getBody();
            LOGGER.info("Ec2Instantiator : Instance Shutdown SQS Message body={}", messageBody);

            // Deletes message from the queue.
            String messageReceiptHandle = messages.get(0).getReceiptHandle();
            sqsService.deleteMessage(messageReceiptHandle, this.instanceShutdownQueueName);

            if (!ec2InstanceStack.isEmpty()) {
                try {
                    poolManager.getPool().invalidateObject(ec2InstanceStack.pop());
                    LOGGER.info("Ec2Instantiator : instance invalidated from the pool. Running instances={}."
                            , poolManager.getPool().getCreatedCount() - poolManager.getPool().getDestroyedCount());

                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
            } else {
                LOGGER.info("Ec2Instantiator : ec2InstanceStack is empty.");
            }

            try {
                LOGGER.info("Ec2Instantiator : thread (instance-shutdown) sleeping for time={}ms.",
                        this.minSleepTime);
                Thread.sleep(this.minSleepTime);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }
}
