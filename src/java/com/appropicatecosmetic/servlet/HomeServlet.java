/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.appropicatecosmetic.servlet;

import com.appropicatecosmetic.dao.UserDAO;
import com.appropicatecosmetic.dao.XmlDAO;
import com.appropicatecosmetic.entity.TblUser;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 *
 * @author Admin
 */
public class HomeServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession();
            String userId = (String) session.getAttribute("USERID");
            if (userId == null) {
                userId = UserDAO.getInstance().checkDefaultUser().getUserId();
            } else {
                TblUser user = UserDAO.getInstance().getUserById(userId);
                if (user.getTblConcernCollection().isEmpty() && user.getTblSkinTypeCollection().isEmpty()) {
                    userId = UserDAO.getInstance().checkDefaultUser().getUserId();
                }
            }
            XmlDAO xmlDAO = new XmlDAO();
            String concernList = xmlDAO.getListConcern();
            String skintypeList = xmlDAO.getListSkinType();
            String categoryList = xmlDAO.getListCategory();
            String result = xmlDAO.getHomeRecommend(userId);
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            
            Document doc = db.parse(new InputSource(new StringReader(result)));
            Document docConcern = db.parse(new InputSource(new StringReader(concernList)));
            Document docSkintype = db.parse(new InputSource(new StringReader(skintypeList)));
            Document docCategory = db.parse(new InputSource(new StringReader(categoryList)));
            
            session.setAttribute("HOMERECOMMENDDOC", doc);
            session.setAttribute("CONCERNSLIST", docConcern);
            session.setAttribute("SKINTYPELIST", docSkintype);
            session.setAttribute("CATEGORYLIST", docCategory);
            request.getRequestDispatcher("index.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
