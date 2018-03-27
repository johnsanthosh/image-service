package model;

public class ImageRecognitionResult {
    private String result;

    private String error;

    public ImageRecognitionResult() {

    }

    public ImageRecognitionResult(String result, String error) {
        this.result = result;
        this.error = error;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
