# ImageService
 The objective of this project was to develop a highly scalable RESTful service based on a distributed architecture, that leverages various Amazon Web Services (AWS) and executed a given deep learning model for image recognition. The image recognition service was deployed on Amazon EC2 and auto scaled by spawning up to 19 slave executor EC2 instances based on the number of requests it receives. The service also scaled down when the number of incoming requests decreased and managed the termination of idle slave instances. The application was tested for up to a total of 1000 requests per second. The service was designed to keep Quality of Service (QOS) at its core ensuring that not even a single request was left unprocessed and steps were taken to include meaningful error messages in the result, in case of any unexpected event that could have interrupted the availability of any of the dependent services.
 
#### Group Members:
 - Sai Pragna Etikyala 
 - Srinath Ganesan 
 - John Santhosh 
 - Akshay Muraleedharan Nair Santhy

#### Requirements:

>  - JDK 1.8+
>  - Maven
>  - AMI `ami-c3a7b1a3`  region: `us-west-1`
>  - AWS Account.
>  - Amazon SQS.
>  - Amazon S3 bucket.
>  - MongoDB running on port `27017`.
>  - Tensorflow deeplearning model setup in the EC2 instance
>  - `recognize-image.sh` available in `/home/ubuntu/`
>  - ports (publically accessible) 8080, 27017
## S3 Buckets
File Bucket name : `cloud-computing-file-bucket`
Result Bucket name : `cloud-computing-result-bucket`

Folders : `images`    `jobs`
## MongoDB
Should be accessible on `http://54.215.241.86:27017/`
Database : `images`
Collection : `jobs`

## SQS
Request Queue : `image-recognition-request-queue`  
Shutdown Queue : `instance-shutdown-queue`  

 #### Dependencies:  

> [pom.xml](https://github.co/johnsanthosh/image-service/blob/master/pom.xml)

## Steps to setup

 1.  Update the application.properties file.
 
EC2 properties. 

`amazon.ec2.ami.id=`  `ami-c3a7b1a31`

`amazon.ec2.access.key.id=`  

`amazon.ec2.secret.access.key=`

S3 properties. 

`amazon.s3.base.url`=`https://s3-us-west-1.amazonaws.com`  

`amazon.s3.bucket.name`=`cloud-computing-file-bucket`  

`amazon.s3.bucket.image.folder.name`=`images`  

`amazon.s3.bucket.job.folder.name`=`jobs ` 

`amazon.s3.bucket.output.file.name`=`result.txt` 
 
`amazon.s3.result.bucket.name`=`cloud-computing-result-bucket`

SQS properties.

`amazon.sqs.request.queue.name`=`image-request-queue`  

`amazon.sqs.request.queue.message.group.id`=`image-recognition-request-queue`  

`amazon.sqs.instance.shutdown.queue.name`=`instance-shutdown-queue`  

`amazon.sqs.instance.shutdown.queue.message.group.id`=`instance-shutdown-queue` 

MongoDB.  

`spring.data.mongodb.host`=`localhost`  

`spring.data.mongodb.port`=`27017`  

`spring.data.mongodb.database`=`images`

Application Settings.  

`sleep.time.min`=`1000`  

`sleep.time.max`=`5000 ` 

`sleep.time.shutdown`=`5000`  

`amazon.ec2.instance.max.count`=`18`

 3. Navigate to image-service.
 4.  Run	**`mvn clean install`**
 5.  Run **`mvn spring-boot:run`**
 6.  Go to `http://54.215.241.86:8080/` to ensure that the application is running. Should display `Image Service is running.`
 

> Spins up the spring-boot application on `http://54.215.241.86:8080/`
> Master Instance (Web-Tier) IP Address : **`54.215.241.86`**

## Rest Endpoints

 1. GET : **`http://54.215.241.86:8080/`**
 

> response : Image Service is running.

 2. GET : **`http://54.215.241.86:8080/cloudimagerecognition/jobs`**

> Response: All the job records in MongoDB.

 
 3. GET : **`http://54.215.241.86:8080/cloudimagerecognition?input=some_valid_image_url`**

> `some_valid_image_url`=`http://visa.lab.asu.edu/cifar-10/0_cat.png`
> Response : `bobsled, bobsleigh, bob`

Sample Mongo Document : 

`{
	"_id": "69b87af8-c082-4f09-ba52-c367f90efde5",
	"_class": "model.Job",
	"url": "http://visa.lab.asu.edu/cifar-10/0_cat.png",
	"result": "bobsled, bobsleigh, bob (score = 0.06877)",
	"inputFilename": "0_cat.png",
	"status": "COMPLETE",
	"submitDateTime": ISODate("2018-03-29T14:42:09.557Z"),
	"completedDateTime": ISODate("2018-03-29T14:43:14.747Z"),
	"error": "2018-03-29 14:43:13.065865: I tensorflow/core/platform/cpu_feature_guard.cc:137] Your CPU supports instructions that this TensorFlow binary was not compiled to use: SSE4.1 SSE4.2 AVX AVX2 FMA2018-03-29 14:43:13.515949: W tensorflow/core/framework/op_def_util.cc:343] Op BatchNormWithGlobalNormalization is deprecated. It will cease to work in GraphDef version 9. Use tf.nn.batch_normalization()."
}
`

Bash Scripts :

**recognize_image.sh**
`#!/bin/bash
python /home/ubuntu/tensorflow/models/tutorials/image/imagenet/classify_image.py --image_file $1 --num_top_predictions 1`

**shutdown.sh**
`#!/bin/bash
shutdown -h now`

Remote Slave Instance Startup Script :

`#!/usr/bin/env bash"
pip install awscli  --upgrade
/home/ubuntu/.local/bin/aws configure set aws_access_key_id AKIAIJD7XM7766A3MIUA
/home/ubuntu/.local/bin/aws configure set aws_secret_access_key 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
sudo pip install --upgrade pip
sudo pip install numpy
source /home/ubuntu/tensorflow/bin/activate
cd /home/ubuntu/image-service-slave
java -jar target/image-service-slave-1.0-SNAPSHOT.jar`


 
    

 


