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
import beans.User;
import dao.AsteDao;
import connection.ConnectionHandler;

	@WebServlet("/GetAsteAperteVendo")
	public class GetAsteAperteVendo extends HttpServlet {
		private static final long serialVersionUID = 1L;
		private Connection connection = null;

		public GetAsteAperteVendo() {
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
			HttpSession s = request.getSession();
			long time = request.getSession().getCreationTime();
			Timestamp loginTime = new Timestamp(time);
			user = (User) s.getAttribute("user");
			AsteDao asteDao = new AsteDao(connection);
			List<Asta> dettAstArtcliAprt = null;

			try {
				dettAstArtcliAprt = asteDao.findAsteEarticoloAperteBySellerId(user.getUserId(),loginTime);

			} catch (SQLException e) {
				 //throw new ServletException(e);
				response.getWriter().println("Failure in seller's database extraction");
				response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			}
			
			Gson gson = new GsonBuilder()
					   .setDateFormat("yyyy-MM-dd hh:mm").create();
			
			String json = gson.toJson(dettAstArtcliAprt);
			System.out.println("creato json");
	
			response.setContentType("application/json");		
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
			System.out.println("mandato  json");	
		}

		public void destroy() {
			try {
				ConnectionHandler.closeConnection(connection);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
		

		
		

			