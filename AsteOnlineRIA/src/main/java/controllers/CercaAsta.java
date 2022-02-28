package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import beans.Asta;
import beans.User;
import dao.AsteDao;
import connection.ConnectionHandler;

	@WebServlet("/CercaAsta")
	@MultipartConfig

	public class CercaAsta extends HttpServlet {
		private static final long serialVersionUID = 1L;
		private Connection connection = null;
	

		public CercaAsta() {
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
			String parola= null;
			boolean isBadRequest = false;
			
			user = (User) session.getAttribute("user");
			
			try {
			parola = StringEscapeUtils.escapeJava(request.getParameter("parola"));
			isBadRequest =  parola.isEmpty();

			} catch (  NullPointerException  e) {
				isBadRequest = true;
				e.printStackTrace();
			}
			if (isBadRequest) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("Inserisci parola per fare la ricerca");
				return;
			}
			
			List<Asta> dettAstAprt = null;
			AsteDao asteDao = new AsteDao(connection);
			
			
			try {
				dettAstAprt = asteDao.trovaAsteBySearchword(parola,user.getUserId());
				

			} catch (SQLException e) {
				 throw new ServletException(e);
				//response.getWriter().println("Non è possibile estrarre data dal database ");
				//response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
			
			
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd  hh:mm").create();
		
			String json = gson.toJson(dettAstAprt);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
					
		}
		
		protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
			doGet(request, response);
		}

		public void destroy() {
			try {
				ConnectionHandler.closeConnection(connection);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}		
	}
		