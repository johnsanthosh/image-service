package controller;

import model.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import service.JobService;
import service.UploadService;

import java.net.URI;

@RestController()
@RequestMapping(value = "/image-service")
public class ImageController {

    private final static Logger LOGGER = LoggerFactory.getLogger(ImageController.class);

    private JobService jobService;
    private UploadService uploadService;

    @Autowired
    public ImageController(JobService jobService, UploadService uploadService) {
        this.jobService = jobService;
        this.uploadService = uploadService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/")
    String home() {
        return "Image Service is running.";
    }

    @RequestMapping(value = {"/images"},
            method = RequestMethod.POST,
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void create(
            @RequestPart(required = false, name = "url") String url,
            @RequestParam(required = false, name = "image") MultipartFile file) throws Exception {

        LOGGER.info("Create job requested");

        //delegate to uploadService for processing and validation
        Job response = jobService.createJob(url, file);
        uploadService.uploadFile(response.getFileName(), file);

        LOGGER.info("Create job successful");
    }


}
