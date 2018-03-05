package service.impl;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.*;
import model.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import service.SqsService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SqsServiceImpl implements SqsService {

    private final static Logger LOGGER = LoggerFactory.getLogger(SqsServiceImpl.class);

    @Value("${amazon.sqs.queue.name}")
    private String queueName;

    @Value("${amazon.sqs.queue.message.group.id}")
    private String groupId;

    private AmazonSQS amazonSQS;

    SqsServiceImpl() {
        this.amazonSQS = AmazonSQSClientBuilder.defaultClient();
    }

    @Override
    public void insertToQueue(String jobId) {
        String queueUrl = amazonSQS.getQueueUrl(this.queueName).getQueueUrl();
        SendMessageRequest sendMessageRequest = new SendMessageRequest(queueUrl, jobId);
        sendMessageRequest.setMessageGroupId(groupId);
        amazonSQS.sendMessage(sendMessageRequest);
        LOGGER.info("Inserting message={} into SQS.", jobId);
    }

    @Override
    public List<Message> getMessages() {
        String queueUrl = amazonSQS.getQueueUrl(this.queueName).getQueueUrl();
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
        List<Message> messages = amazonSQS.receiveMessage(receiveMessageRequest).getMessages();
        return messages;
    }

    @Override
    public void deleteMessage(String messageReceiptHandle) {
        String queueUrl = amazonSQS.getQueueUrl(this.queueName).getQueueUrl();
        amazonSQS.deleteMessage(new DeleteMessageRequest(queueUrl, messageReceiptHandle));
        LOGGER.info("Deleting message with handle={} from SQS.", messageReceiptHandle);
    }

    @Override
    public Map<String, String> getQueueAttributes() {
        String queueUrl = amazonSQS.getQueueUrl(this.queueName).getQueueUrl();
        List<String> attributes = Stream.of(QueueAttributeName.ApproximateNumberOfMessages.toString())
                .collect(Collectors.toList());
        GetQueueAttributesRequest getQueueAttributesRequest = new GetQueueAttributesRequest(queueUrl, attributes);
        GetQueueAttributesResult result = amazonSQS.getQueueAttributes(getQueueAttributesRequest);
        return result.getAttributes();
    }
}
