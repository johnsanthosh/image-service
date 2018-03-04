package model;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import org.springframework.beans.factory.annotation.Value;

public class Ec2Instance {
    private int instanceNumber;
    private String awsInstanceId;
    private InstanceStatus instanceStatus;
    private AWSCredentials awsCredentials;
    private AmazonEC2 ec2Client;
    private String ec2AmiId;

    public Ec2Instance() {
    }

    public Ec2Instance(String awsAccessKeyId, String awsSecretAccessKey, String ec2AmiId) {
        this.awsCredentials = new BasicAWSCredentials(awsAccessKeyId, awsSecretAccessKey);
        this.ec2AmiId = ec2AmiId;
        buildAmazonEc2Client();
    }

    public int getInstanceNumber() {
        return instanceNumber;
    }

    public void setInstanceNumber(int instanceNumber) {
        this.instanceNumber = instanceNumber;
    }

    public String getAwsInstanceId() {
        return awsInstanceId;
    }

    public void setAwsInstanceId(String awsInstanceId) {
        this.awsInstanceId = awsInstanceId;
    }

    public InstanceStatus getInstanceStatus() {
        return instanceStatus;
    }

    public void setInstanceStatus(InstanceStatus instanceStatus) {
        this.instanceStatus = instanceStatus;
    }

    public AWSCredentials getAwsCredentials() {
        return awsCredentials;
    }

    public void setAwsCredentials(AWSCredentials awsCredentials) {
        this.awsCredentials = awsCredentials;
    }

    public AmazonEC2 getEc2Client() {
        return ec2Client;
    }

    public void setEc2Client(AmazonEC2 ec2Client) {
        this.ec2Client = ec2Client;
    }

    public String getEc2AmiId() {
        return ec2AmiId;
    }

    public void setEc2AmiId(String ec2AmiId) {
        this.ec2AmiId = ec2AmiId;
    }

    void buildAmazonEc2Client() {
        this.ec2Client = AmazonEC2ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(this.awsCredentials)).build();
    }
}
