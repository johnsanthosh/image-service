package constants;

public interface ServiceConstants {
    String PATH_SEPARATOR = "/";
    int SLEEP_TIME_1S = 1000;
    int SLEEP_TIME_20S = 20000;
    String EC2_AMI_ID = "ami-065ccd7e";
    String AWS_ACCESS_KEY_ID = "AKIAJ23PYR3ZPWNMBJJA";
    String AWS_SECRET_ACCESS_KEY = "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";
    int MAX_EC2_INSTANCE_COUNT = 19;
    String SPRING_BOOT_STARTUP_SLAVE = "#!/usr/bin/env bash"
            +"\n"+ "pip install awscli  --upgrade --user"
            +"\n" + "/home/ubuntu/.local/bin/aws configure set aws_access_key_id AKIAJ23PYR3ZPWNMBJJA"
            +"\n" + "/home/ubuntu/.local/bin/aws configure set aws_secret_access_key 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"
            + "\n" + "sudo su ubuntu"
            + "\n" + "cd /home/ubuntu/image-service-slave"
            + "\n" + "mvn spring-boot:run";
}






