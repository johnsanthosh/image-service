package listener;

import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.QueueAttributeName;
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
import java.util.Map;

@Component
public class Ec2Instantiator implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(Ec2Instantiator.class);

    private Ec2InstancePoolManager poolManager;

    private SqsService sqsService;

    public Ec2Instantiator() {
    }

    @Autowired
    public Ec2Instantiator(SqsService sqsService, Ec2InstancePoolManager poolManager) {
        this.sqsService = sqsService;
        this.poolManager = poolManager;
    }

    @Override
    public void run() {
        while (true) {
            LOGGER.info("Polling SQS for queue size.");
            Map<String, String> attributeMap = null;

            if (sqsService != null) {
                attributeMap = sqsService.getQueueAttributes();

                if (!CollectionUtils.isEmpty(attributeMap)) {
                    Long messageCount = Long.parseLong(attributeMap.get(QueueAttributeName.ApproximateNumberOfMessages.toString()));
                    LOGGER.info("Retrieving queue attributes from SQS. ApproximateNumberOfMessages={}", messageCount);
                    long instancesCreated = poolManager.getPool().getCreatedCount();
                    long extraInstancesNeeded = messageCount - instancesCreated;
                    if (messageCount > 1 && extraInstancesNeeded > 0
                            && (extraInstancesNeeded + instancesCreated) <= ServiceConstants.MAX_EC2_INSTANCE_COUNT) {
                        createInstances((int) extraInstancesNeeded);
                    }

                    try {
                        LOGGER.info("Ec2Instantiator thread sleeping for time={}ms.", ServiceConstants.SLEEP_TIME_20S);
                        Thread.sleep(ServiceConstants.SLEEP_TIME_20S);
                    } catch (InterruptedException e) {
                        LOGGER.error(e.getMessage());
                    }
                }
            }
        }

    }

    public void createInstances(int count) {
        Ec2Instance ec2Instance = null;
        for (int i = 0; i < count; i++) {
            try {
                ec2Instance = (Ec2Instance) poolManager.getPool().borrowObject();
                Ec2Service ec2Service = new Ec2ServiceImpl(ec2Instance);
                LOGGER.info("EC2 Instance created, InstanceId={}", ec2Service.createInstance());
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("Creating EC2 Instance failed.");
            }
        }
    }
}
