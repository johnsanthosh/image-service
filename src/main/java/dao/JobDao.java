package dao;

import model.Job;

public interface JobDao {
    void createJob(Job job);
    void updateJob(Job job);
    void deleteJob(String jobId);
}
