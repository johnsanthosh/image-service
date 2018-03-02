package service;

import model.Job;

public interface SqsService {
    void insertToQueue(String jobId);
}
