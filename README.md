# ImageService
#### Requirements:

>  - JDK 1.8+
>  - Maven

 #### Dependencies:  

> [pom.xml](https://github.com/johnsanthosh/image-service/blob/master/pom.xml)

## Steps to setup

 1.  Navigate to image-service.

 2.  Run	**`mvn clean install`**
 3.  Run **`mvn spring-boot:run`**
 

> Spins up the spring-boot application on `http://localhost:8080/`

## Rest Endpoints

 1. GET : **`http://localhost:8080/image-service/`**
 

> response : Image Service is running.

 2. POST : **`http://localhost:8080/image-service/images`**

> request header : content-type:multipart/form-data 
> 
> url : some-url.com *(Optional)*
> 
> image : *attach file here
## S3 Buckets
 - *images*
 - *jobs*
## MongoDB

Database : `images`

Collection : `jobs`

Sample Document : 
`{"_id":"b1d82861-6105-400b-9bf3-44971c184097",
"_class":"model.Job","url":"https://s3-us-west-2.amazonaws.com/images/2018-03-02T06:59:03.343ZJesus14.jpg",
"inputFilename":"Jesus14.jpg",
"filePath":"images/2018-03-02T06:59:03.343ZJesus14.jpg",
"status":"ACCEPTED",
"submitDateTime":"2018-03-02T06:59:03.343Z"}`


 
    

 


