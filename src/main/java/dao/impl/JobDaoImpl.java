package dao.impl;

import dao.JobDao;
import model.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import repository.JobRepository;

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

    }

    @Override
    public void deleteJob(String jobId) {

    }
}
