package service.impl;

import listener.Ec2Instantiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import service.BashExecuterService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Service
public class BashExecuterServiceImpl implements BashExecuterService {
    private final static Logger LOGGER = LoggerFactory.getLogger(BashExecuterService.class);

    @Override
    public String recognizeImage(String imageUrl) {
        ProcessBuilder pb = new ProcessBuilder("/home/ubuntu/recognize_image.sh",
                imageUrl);
        String result = null;
        String error = null;
        try {
            Process p = pb.start();
            result = loadStream(p.getInputStream());
            error = loadStream(p.getErrorStream());
            p.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (StringUtils.isEmpty(error)) {
            LOGGER.info("Result computed, result={}", result);
        } else {
            LOGGER.info("Result computation failed with error={}", error);
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
