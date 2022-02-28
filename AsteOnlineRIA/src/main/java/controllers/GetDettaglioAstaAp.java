package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
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
import connection.ConnectionHandler;
import dao.AsteDao;
import dao.OffertaDao;
import dao.UserDao;

@WebServlet("/GetDettaglioAstaAp")
public class GetDettaglioAstaAp extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GetDettaglioAstaAp() {
		super();
	}

	public void init() throws ServletException {
		

		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
					// If the user is not logged in (not present in session) redirect to the login
			HttpSession session = request.getSession();
			
		    if (session.isNew() || session.getAttribute("user") == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.setStatus(403);
			response.setHeader("Location", loginpath);
			System.out.print("You have to log in");
			return;
		}

		// get and check params
		Integer idAsta = null;
		
		try {
			idAsta = Integer.parseInt(request.getParameter("idAsta"));
		} catch (NumberFormatException | NullPointerException e) {
			 // e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}

		
		UserDao userDao = new UserDao(connection);
		AsteDao astaDao = new AsteDao(connection);
		Asta detAsta = null;


		try {
			detAsta = astaDao.findAstaEarticoloByIdAsta(idAsta);
		    
			if (detAsta == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Non esiste questa asta");
				return;
				
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			response.getWriter().println("Not possible to recover asta");
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			return;
		}


		Gson gson = new GsonBuilder()
				   .setDateFormat("yyyy-MM-dd hh:mm").create();
		
		String json = gson.toJson(detAsta);

		response.setContentType("application/json");		
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
