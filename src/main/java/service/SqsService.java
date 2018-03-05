package service;

import com.amazonaws.services.sqs.model.Message;
import model.Job;

import java.util.List;
import java.util.Map;

public interface SqsService {
    void insertToQueue(String jobId);

    List<Message> getMessages();

    void deleteMessage(String messageReceiptHandle);

    Map<String, String> getQueueAttributes();
}
