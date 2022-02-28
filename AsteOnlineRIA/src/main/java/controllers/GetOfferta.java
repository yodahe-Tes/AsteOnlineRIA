package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import beans.Asta;
import beans.Offerta;
import beans.User;
import dao.AsteDao;
import dao.OffertaDao;
import connection.ConnectionHandler;
import org.json.simple.JSONObject;

	@WebServlet("/GetOfferta")
	public class GetOfferta extends HttpServlet {
		private static final long serialVersionUID = 1L;
		private Connection connection = null;

		public GetOfferta() {
			super();
		}

		public void init() throws ServletException {
		
			connection = ConnectionHandler.getConnection(getServletContext());
		}

		protected void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			
			HttpSession session = request.getSession();
			if (session.isNew() || session.getAttribute("user") == null) {
				String loginpath = getServletContext().getContextPath() + "/index.html";
				response.setStatus(403);
				response.setHeader("Location", loginpath);
				System.out.print("You have to log in");
				return;
			}
			
			User user = null;
			Integer idAsta = null;
			user = (User) session.getAttribute("user");
			OffertaDao offertaDao = new OffertaDao(connection);
			List<Offerta> offerte = null;
			Timestamp currTime = null;
			try {
				idAsta = Integer.parseInt(request.getParameter("idAsta"));
				System.out.println("idAsta :"+ idAsta);
			} catch (NumberFormatException | NullPointerException e) {
				  e.printStackTrace();
				  response.getWriter().println("numero idAsta Sbagliato");
				  response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
					return;
			}
			try { 
				
				offerte = offertaDao.findTutteOfferteByIdAsta(idAsta);
				
			} catch (SQLException e) {
				 //throw new ServletException(e);
				 response.getWriter().println("Non sono state trovate le offerte");
				 response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
					}
			currTime = new Timestamp(System.currentTimeMillis());
			

			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd hh:mm").create();
			
			String json = gson.toJson(offerte);
			
			response.setContentType("application/json");
			response.setCharacterEncoding("utf-8");
			response.getWriter().write(json.toString());
			
		}

		public void destroy() {
			try {
				ConnectionHandler.closeConnection(connection);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
		
		