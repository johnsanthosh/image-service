package constants;

public interface ServiceConstants {
    String PATH_SEPARATOR = "/";
    String AWS_AMI_ID = "amazon.ec2.ami.id";
    String AWS_ACCESS_KEY = "amazon.ec2.access.key.id";
    String AWS_SECRET_KEY = "amazon.ec2.secret.access.key";
    String SPRING_BOOT_STARTUP_SLAVE = "#!/usr/bin/env bash"
	    + "\n"+ "pip install awscli  --upgrade"
            + "\n" + "/home/ubuntu/.local/bin/aws configure set aws_access_key_id AKIAIJD7XM7766A3MIUA"
            + "\n" + "/home/ubuntu/.local/bin/aws configure set aws_secret_access_key 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"
            + "\n" + "sudo pip install --upgrade pip"
	    + "\n" + "sudo pip install numpy"
	    + "\n" + "source /home/ubuntu/tensorflow/bin/activate"
            + "\n" + "cd /home/ubuntu/image-service-slave"
            + "\n" + "java -jar target/image-service-slave-1.0-SNAPSHOT.jar";
}






