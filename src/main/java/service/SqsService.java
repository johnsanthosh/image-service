package service;

import com.amazonaws.services.sqs.model.Message;
import model.Job;

import java.util.List;
import java.util.Map;

public interface SqsService {
    void insertToQueue(String jobId, String queueName, String groupId);

    List<Message> getMessages(String queueName);

    void deleteMessage(String messageReceiptHandle, String queueName);

    Map<String, String> getQueueAttributes(String queueName);
}
