package service;

import model.ImageRecognitionResult;
import model.Job;

public interface BashExecuterService {
    ImageRecognitionResult recognizeImage(String imageUrl);
}
