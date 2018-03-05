package dao.impl;

import dao.JobDao;
import model.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import repository.JobRepository;

import java.util.List;

@Repository
public class JobDaoImpl implements JobDao {

    @Autowired
    JobRepository jobRepository;

    @Override
    public void createJob(Job job) {
        this.jobRepository.insert(job);
    }

    @Override
    public void updateJob(Job job) {
        jobRepository.save(job);
    }

    @Override
    public void deleteJob(String jobId) {

    }

    @Override
    public List<Job> getJobs() {
        return this.jobRepository.findAll();
    }

    @Override
    public Job getJob(String id) {
        return this.jobRepository.findOne(id);
    }
}
