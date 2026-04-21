package com.bintang.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

@Controller
public class CaptchaController {

    @GetMapping("/captcha-image")
    public void getCaptcha(HttpSession session, HttpServletResponse response) throws IOException {
        int width = 180;
        int height = 50;
        
        // Create Image with Transparency
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        
        // Background - Transparent
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, width, height);
        g.setComposite(AlphaComposite.SrcOver);
        
        // Font
        g.setFont(new Font("Arial", Font.BOLD, 28));
        
        // Generate Random Code
        String captchaCode = generateRandomCode(5);
        session.setAttribute("CAPTCHA_CODE", captchaCode);
        
        // Draw Text with slight distortion
        Random rand = new Random();
        for (int i = 0; i < captchaCode.length(); i++) {
            g.setColor(new Color(rand.nextInt(100), rand.nextInt(100), rand.nextInt(100)));
            int x = 20 + i * 25;
            int y = 35 + (rand.nextInt(10) - 5);
            g.drawString(String.valueOf(captchaCode.charAt(i)), x, y);
        }
        
        // Add Noise Lines
        g.setColor(new Color(200, 200, 200));
        for (int i = 0; i < 6; i++) {
            g.drawLine(rand.nextInt(width), rand.nextInt(height), rand.nextInt(width), rand.nextInt(height));
        }
        
        g.dispose();
        
        // Disable caching
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        
        // Response
        response.setContentType("image/png");
        ImageIO.write(image, "png", response.getOutputStream());
        
        System.out.println("Generated CAPTCHA: " + captchaCode + " for session: " + session.getId());
    }
    
    private String generateRandomCode(int length) {
        String chars = "ACEFHJKLMNPRTUVWXY34679"; // Ultra-clear character set
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
