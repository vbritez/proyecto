package com.tigo.cs.servlet;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tigo.cs.facade.GlobalParameterFacade;

/**
 * 
 * @author Miguel Maciel
 * @version $Id$
 */

@WebServlet("/markImage")
public class MarkImage extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = -7746864310642706498L;

    /**
     * @throws IOException
     * 
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //
        ByteArrayOutputStream imgOutputStream = new ByteArrayOutputStream();
        byte[] captchaBytes = null; // imageBytes
        String sImgType = "png";

        String txt = request.getParameter("cliente");
        if (txt == null) {
            txt = "";
        }
        Boolean inRoute = false;
        if (Boolean.parseBoolean(request.getParameter("inRoute"))) {
            inRoute = true;
        }

        int width = (txt.length() * 10);
        int height = 30;

        try {

            GlobalParameterFacade globalParameterFacade = GlobalParameterFacade.getLocalInstance(GlobalParameterFacade.class);
            String[] parameter = null;

            Color color = null;
            if (inRoute) {
                String colorParameter = globalParameterFacade.findByCode("map.roadmap.inroute.color");
                parameter = colorParameter.split(",");

            } else {
                String colorParameter = globalParameterFacade.findByCode("map.roadmap.offroute.color");
                parameter = colorParameter.split(",");
            }
            color = new Color(Integer.parseInt(parameter[0]),
                    Integer.parseInt(parameter[1]),
                    Integer.parseInt(parameter[2]));

            BufferedImage bufImage = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = bufImage.createGraphics();

            // Fill background
            // g2d.setColor(Color.LIGHT_GRAY);
            // g2d.fillRect(0, 0, width, height / 2);

            /*
             * texto
             */
            // g2d.setColor(new Color(62, 114, 170));
            // if (Integer.parseInt(request.getParameter("zoom")) >= 15) {
            g2d.setColor(color);
            g2d.setFont(new Font("Courier New", Font.BOLD, 12));
            g2d.drawString(txt, 15, 12);

            /*
             * borde
             */
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawRect(10, 0, width - 11, height / 2);
            // }
            /*
             * mark
             */
            g2d.setColor(color);
            g2d.fillOval(width / 2, 15, 10, 10);
            // g2d.setColor(Color.WHITE);
            // g2d.fillOval(3, 18, 4, 4);

            g2d.dispose();

            ImageIO.write(bufImage, sImgType, imgOutputStream);
            captchaBytes = imgOutputStream.toByteArray();
        } catch (Exception e) {
        }
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/"
            + (sImgType.equalsIgnoreCase("png") ? "png" : "jpeg"));
        ServletOutputStream outStream = response.getOutputStream();
        outStream.write(captchaBytes);
        outStream.flush();
        outStream.close();

    }
}
