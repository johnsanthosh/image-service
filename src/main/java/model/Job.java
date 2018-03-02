package model;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.UUID;

@Document(collection = "jobs")
public class Job implements Serializable {
    protected String id;
    protected String url;
    protected String result;
    protected String inputFilename;
    protected String filePath;
    protected JobStatus status;
    protected DateTime submitDateTime;
    protected DateTime completedDateTime;

    public Job() {
        this.id = UUID.randomUUID().toString();
        this.submitDateTime = DateTime.now(DateTimeZone.UTC);
        this.status = JobStatus.ACCEPTED;
    }

    public String getInputFilename() {
        return inputFilename;
    }

    public void setInputFilename(String inputFilename) {
        this.inputFilename = inputFilename;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public DateTime getSubmitDateTime() {
        return submitDateTime;
    }

    public void setSubmitDateTime(DateTime submitDateTime) {
        this.submitDateTime = submitDateTime;
    }

    public DateTime getCompletedDateTime() {
        return completedDateTime;
    }

    public void setCompletedDateTime(DateTime completedDateTime) {
        this.completedDateTime = completedDateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }
}
