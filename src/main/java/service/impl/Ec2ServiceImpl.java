package service.impl;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import model.Ec2Instance;
import service.Ec2Service;

import java.util.Arrays;

public class Ec2ServiceImpl implements Ec2Service {

    Ec2Instance ec2Instance;

    public Ec2ServiceImpl() {
    }

    public Ec2ServiceImpl(Ec2Instance ec2Instance) {
        this.ec2Instance = ec2Instance;
    }


    @Override
    public String createInstance() {
        RunInstancesRequest runInstancesRequest = new RunInstancesRequest(ec2Instance.getEc2AmiId(), 1, 1);
        runInstancesRequest.setInstanceType("t2.micro");
        runInstancesRequest.setKeyName("cloud-computing");
        runInstancesRequest.setSecurityGroups(Arrays.asList("cloud-computing"));
        RunInstancesResult runResponse = ec2Instance.getEc2Client().runInstances(runInstancesRequest);
        String instanceId = runResponse.getReservation().getInstances().get(0).getInstanceId();
        ec2Instance.setAwsInstanceId(instanceId);
        return instanceId;
    }

    @Override
    public String startInstance() {
        return null;
    }

    @Override
    public void stopInstance() {

    }

    @Override
    public void terminateInstance() {

    }
}
