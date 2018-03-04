package service;

import com.amazonaws.services.ec2.AmazonEC2;

public interface Ec2Service {
    String createInstance();

    String startInstance();

    void stopInstance();

    void terminateInstance();

}
