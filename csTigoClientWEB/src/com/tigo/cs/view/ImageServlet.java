package com.tigo.cs.view;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tigo.cs.domain.Image;
import com.tigo.cs.facade.NewFacade;

/**
 * Servlet implementation class ImageServlet
 */
@WebServlet("/ImageServlet")
public class ImageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ImageServlet() {
        super();
    }
    
    
    @EJB  
    private NewFacade newFacade;  
  
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)  
            throws ServletException, IOException {  
   
        String idString = request.getParameter("id");
        String type = request.getParameter("type");
  
        if (idString == null || idString.isEmpty() || type == null || type.isEmpty()) {  
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.  
            return;  
        }  
  
        Long id = Long.parseLong(idString.trim());  
  
        List<Image> imageList = newFacade.findImagesByNewCod(id);
  
        if (imageList == null) {  
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.  
            return;  
        }  
  
        ServletOutputStream out = null;  
  
        try {  
            if (imageList.size() > 0){
                response.reset();  
      
                // It works ok without setting any of these...  
                //response.setContentType(image.getContentType());  
                //response.setHeader("Content-Length", String.valueOf(image.getLength()));  
                //response.setHeader("Content-Disposition", "inline; filename=\"" + image.getName() + "\"");  
                //response.setContentType("image/bmp");  
                //response.setContentType("image/x-jpeg");  
      
                out = response.getOutputStream();  
                Image image = imageList.get(0);
  
                if (image.getSmallImageByt() != null && image.getSmallImageByt().length != 0) {  
                    if (type.equals("small"))
                        out.write(image.getSmallImageByt());  
                    else
                        out.write(image.getBigImageByt());
                }  
            }
        } catch (IOException e) {  
            e.printStackTrace(); 
        } finally {  
            close(out);  
        }  
  
    }  
    
    private static void close(Closeable resource) {  
        if (resource != null) {  
            try {  
                resource.close();  
            } catch (IOException e) {  
                e.printStackTrace(); 
            }  
        }  
    } 

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    processRequest(request, response); 
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    processRequest(request, response); 
	}

}
