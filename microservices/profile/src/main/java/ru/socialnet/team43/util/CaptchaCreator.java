package ru.socialnet.team43.util;

import cn.apiclub.captcha.Captcha;
import cn.apiclub.captcha.gimpy.FishEyeGimpyRenderer;
import cn.apiclub.captcha.noise.StraightLineNoiseProducer;
import cn.apiclub.captcha.text.producer.DefaultTextProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.socialnet.team43.dto.CaptchaDto;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Component
@Slf4j
public class CaptchaCreator {

    @Value("${captcha.length}")
    private int captchaLength;

    @Value("${captcha.symbols}")
    private String captchaSymbols;

    private final Map<String, String> captchaList = new HashMap<>();

    public CaptchaDto createCaptcha() {
    Captcha captcha = new Captcha.Builder(200, 50)
            .addText(new DefaultTextProducer(captchaLength, captchaSymbols.toCharArray()))
            .addNoise(new StraightLineNoiseProducer())
            .gimp(new FishEyeGimpyRenderer())
            .build();
    try {
        String secret = UUID.randomUUID().toString();
        String captchaCode = "data:image/png;base64," +
                DatatypeConverter.printBase64Binary(imageToByteArray(captcha.getImage()));
        CaptchaDto captchaDto = CaptchaDto.builder()
                .image(captchaCode)
                .secret(secret)
                .build();
        captchaList.put(secret, captcha.getAnswer());
        log.info("New captcha added in captchaList");
        return captchaDto;
    } catch (
    IOException e) {
        log.error(e.toString());
        e.printStackTrace();
    }
        return null;
}

    private static byte[] imageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", stream);
        return stream.toByteArray();
    }

    public Map<String, String> getCaptchaList() {
        return captchaList;
    }
}
