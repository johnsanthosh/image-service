package constants;

public interface ServiceConstants {
    String PATH_SEPARATOR = "/";
    String AWS_AMI_ID = "amazon.ec2.ami.id";
    String AWS_ACCESS_KEY = "amazon.ec2.access.key.id";
    String AWS_SECRET_KEY = "amazon.ec2.secret.access.key";
    String SPRING_BOOT_STARTUP_SLAVE = "#!/usr/bin/env bash"
            + "\n" + "sudo su ubuntu"
            + "\n" + "cd /home/ubuntu/image-service-slave"
            + "\n" + "java -jar target/image-service-slave-1.0-SNAPSHOT.jar";
}






