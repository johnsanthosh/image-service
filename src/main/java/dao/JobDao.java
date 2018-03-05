package dao;

import model.Job;

import java.util.List;

public interface JobDao {
    void createJob(Job job);
    void updateJob(Job job);
    void deleteJob(String jobId);
    List<Job> getJobs();
    Job getJob(String id);
}
