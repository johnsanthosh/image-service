package service.impl;

import org.springframework.stereotype.Service;
import service.BashExecuterService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Service
public class BashExecuterServiceImpl implements BashExecuterService {
    @Override
    public String recognizeImage(String imageUrl) {
        ProcessBuilder pb = new ProcessBuilder("/home/ubuntu/recognize_image.sh",
                imageUrl);
        String result = null;
        try {
            Process p = pb.start();
            result = loadStream(p.getInputStream());
            p.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String loadStream(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null)
            sb.append(line);
        return sb.toString();
    }
}
