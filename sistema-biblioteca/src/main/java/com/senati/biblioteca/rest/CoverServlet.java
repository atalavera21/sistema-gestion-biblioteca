package com.senati.biblioteca.rest;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import javax.imageio.ImageIO;

@WebServlet("/cover/*")
public class CoverServlet extends HttpServlet {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 420;
    private static final int PADDING = 24;

    private static final String[][] CATEGORY_PALETTES = {
        {"#1B3A4B", "#0F2837"}, {"#2D6A4F", "#1B4332"}, {"#1B6B93", "#0F4C6B"},
        {"#6B3FA0", "#4A2878"}, {"#A42834", "#7A1C26"}, {"#B8860B", "#8B6508"},
        {"#3F6B5C", "#2A4D41"}, {"#5C3F6B", "#3E2A4D"}, {"#8B4513", "#5C2D0D"},
        {"#2C5282", "#1A3A5C"}, {"#6B5C3F", "#4D412A"}, {"#3F6B8A", "#2A4D6B"},
        {"#4A8A6B", "#305C4A"}, {"#6B4A8A", "#4A305C"}, {"#8A6B4A", "#5C4A30"}
    };

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("image/png");
        response.setHeader("Cache-Control", "public, max-age=86400");

        int catIndex = 0;
        String title = "Libro";
        String author = "";

        if (pathInfo != null && pathInfo.length() > 1) {
            String[] parts = pathInfo.substring(1).split("/", 3);
            try { catIndex = Integer.parseInt(parts[0]) % CATEGORY_PALETTES.length; } catch (Exception ignored) {}
            if (parts.length > 1) {
                title = URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
            }
            if (parts.length > 2) {
                author = URLDecoder.decode(parts[2], StandardCharsets.UTF_8);
            }
        }

        String[] palette = CATEGORY_PALETTES[catIndex];
        Color c1 = Color.decode(palette[0]);
        Color c2 = Color.decode(palette[1]);

        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Fondo con gradiente
        GradientPaint gradient = new GradientPaint(0, 0, c1, WIDTH, HEIGHT, c2);
        g.setPaint(gradient);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Elementos decorativos sutiles
        g.setColor(new Color(255, 255, 255, 15));
        g.setStroke(new BasicStroke(1f));
        for (int i = 0; i < 6; i++) {
            int y = 50 + i * 70;
            g.drawLine(0, y, WIDTH, y);
        }

        // Círculo decorativo
        g.setColor(new Color(255, 255, 255, 8));
        int circleR = 180;
        g.fillOval(WIDTH / 2 - circleR / 2, HEIGHT / 2 - circleR / 2 - 20, circleR, circleR);

        // Ícono de libro (emoji o símbolo centrado arriba)
        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        g.setColor(new Color(255, 255, 255, 80));
        String icon = "📖";
        FontMetrics fmi = g.getFontMetrics();
        int iconW = fmi.stringWidth(icon);
        g.drawString(icon, (WIDTH - iconW) / 2, 100);

        // Título (wrapping)
        g.setColor(Color.WHITE);
        Font titleFont = new Font("Inter", Font.BOLD, 22);
        g.setFont(titleFont);

        int maxWidth = WIDTH - PADDING * 2;
        int y = 170;
        String[] words = title.split(" ");
        StringBuilder line = new StringBuilder();
        FontMetrics fm = g.getFontMetrics();

        for (String word : words) {
            String test = line.length() == 0 ? word : line + " " + word;
            if (fm.stringWidth(test) > maxWidth) {
                if (line.length() > 0) {
                    int lineW = fm.stringWidth(line.toString());
                    g.drawString(line.toString(), (WIDTH - lineW) / 2, y);
                    y += fm.getHeight() + 4;
                }
                line = new StringBuilder(word);
            } else {
                if (line.length() > 0) line.append(" ");
                line.append(word);
            }
        }
        if (line.length() > 0) {
            int lineW = fm.stringWidth(line.toString());
            g.drawString(line.toString(), (WIDTH - lineW) / 2, y);
            y += fm.getHeight() + 8;
        }

        // Autor
        if (!author.isEmpty()) {
            g.setFont(new Font("Inter", Font.PLAIN, 13));
            g.setColor(new Color(255, 255, 255, 180));
            fm = g.getFontMetrics();
            int authW = fm.stringWidth(author);
            g.drawString(author, (WIDTH - authW) / 2, y);
        }

        // Línea decorativa inferior
        g.setColor(new Color(255, 255, 255, 40));
        int decorY = HEIGHT - 60;
        g.setStroke(new BasicStroke(3f));
        g.drawLine(WIDTH / 2 - 40, decorY, WIDTH / 2 + 40, decorY);

        // "SENATI" abajo
        g.setFont(new Font("Playfair Display", Font.BOLD, 11));
        g.setColor(new Color(255, 255, 255, 50));
        fm = g.getFontMetrics();
        String brand = "SENATI BIBLIOTECA";
        int brandW = fm.stringWidth(brand);
        g.drawString(brand, (WIDTH - brandW) / 2, HEIGHT - 20);

        g.dispose();

        try (OutputStream out = response.getOutputStream()) {
            ImageIO.write(img, "PNG", out);
        }
    }
}
