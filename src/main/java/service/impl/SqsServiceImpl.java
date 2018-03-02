package service.impl;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import model.Job;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import service.SqsService;

@Service
public class SqsServiceImpl implements SqsService {
    @Value("${amazon.sqs.queue.name}")
    private String queueName;

    @Value("${amazon.sqs.queue.message.group.id}")
    private String groupId;

    AmazonSQS amazonSQS;

    SqsServiceImpl() {
        this.amazonSQS = AmazonSQSClientBuilder.defaultClient();
    }

    @Override
    public void insertToQueue(String jobId) {
        String queueUrl = amazonSQS.getQueueUrl(queueName).getQueueUrl();
        SendMessageRequest sendMessageRequest = new SendMessageRequest(queueUrl, jobId);
        sendMessageRequest.setMessageGroupId(groupId);
        amazonSQS.sendMessage(sendMessageRequest);
    }
}
