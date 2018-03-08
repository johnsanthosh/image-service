# ImageService
#### Requirements:

>  - JDK 1.8+
>  - Maven
>  - Amazon EC2 `ami-07303b67` (public) region: `us-west-1a`
>  - AWS Account.
>  - Amazon SQS (FIFO).
>  - Amazon S3 bucket.
>  - MongoDB.

 #### Dependencies:  

> [pom.xml](https://github.com/johnsanthosh/image-service/blob/master/pom.xml)

## Steps to setup

 1.  Update the application.properties file.
 

> EC2 properties. 
`amazon.ec2.ami.id=`  
`amazon.ec2.access.key.id=`  
`amazon.ec2.secret.access.key=`
S3 properties. 
`amazon.s3.base.url=https://s3-us-west-2.amazonaws.com `
`amazon.s3.bucket.name=image-service-file-bucket`  
`amazon.s3.bucket.image.folder.name=images `
`amazon.s3.bucket.job.folder.name=jobs`  
SQS properties.
`amazon.sqs.queue.name=image-request-queue.fifo`  
`amazon.sqs.queue.message.group.id=image-recognition-request-queue`  
MongoDB.  
`spring.data.mongodb.host=localhost`  
`spring.data.mongodb.port=27017`  
`spring.data.mongodb.database=images`

 3. Navigate to image-service.
 4.  Run	**`mvn clean install`**
 5.  Run **`mvn spring-boot:run`**
 

> Spins up the spring-boot application on `http://localhost:8080/`

## Rest Endpoints

 1. GET : **`http://localhost:8080/image-service/`**
 

> response : Image Service is running.

 2. GET : **`http://localhost:8080/image-service/images`**
 
 3. POST : **`http://localhost:8080/image-service/images`**

> request header : content-type:multipart/form-data 
> 
> url : some-url.com *(Optional)*
> 
> image : *attach file here
## S3 Buckets
Bucket name : `image-service-file-bucket`
Folders : `images`    `jobs`
## MongoDB

Database : `images`
Collection : `jobs`

Sample Mongo Document : 
`{"_id":"b1d82861-6105-400b-9bf3-44971c184097",
"_class":"model.Job","url":"https://s3-us-west-2.amazonaws.com/images/2018-03-02T06:59:03.343ZJesus14.jpg",
"inputFilename":"Jesus14.jpg",
"filePath":"images/2018-03-02T06:59:03.343ZJesus14.jpg",
"status":"ACCEPTED",
"submitDateTime":"2018-03-02T06:59:03.343Z"}`

Bash Scripts :
**recognize_image.sh**
`#!/bin/bash
python /home/ubuntu/tensorflow/models/tutorials/image/imagenet/classify_image.py --image_file $1 --num_top_predictions 1`
**shutdown.sh**
`#!/bin/bash
shutdown -h now`


 
    

 


