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
import utilities.ObjContainer;

@WebServlet("/GetDettaglioAstaCs")
public class GetDettaglioAstaCs extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public GetDettaglioAstaCs() {
		super();
	}

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");

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
		Integer idVincitore = null;
		Integer idAsta = null;
		ObjContainer objContainer= new ObjContainer();
		
		try {
			idAsta = Integer.parseInt(request.getParameter("idAsta"));
		} catch (NumberFormatException | NullPointerException e) {
			  e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}

		
		UserDao userDao = new UserDao(connection);
		User user = (User) session.getAttribute("user");
		User vincitoreAsta = null;
		AsteDao astaDao = new AsteDao(connection);
		Asta detAstaCs = null;

		OffertaDao offertaDao = new OffertaDao(connection);
		List<Offerta> offerte = null;
		Timestamp currTime = null;

		try {
			detAstaCs = astaDao.findAstaEarticoloCsByIdAsta(idAsta);
			offerte = offertaDao.findTutteOfferteByIdAsta(idAsta);
		    
			if (detAstaCs == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
				return;
				
			}
			
				idVincitore = detAstaCs.getVincitoreAsta();
				System.out.println(idVincitore);

				vincitoreAsta = userDao.findUserById(idVincitore);
				System.out.println(vincitoreAsta.getIndirizzo());
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			response.getWriter().println("Not possible to recover asta");
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
			return;
		}
		currTime = new Timestamp(System.currentTimeMillis());
		
		objContainer.setFirstObj(detAstaCs);
		objContainer.setSecondObj(vincitoreAsta);

		Gson gson = new GsonBuilder()
				   .setDateFormat("yyyy-MM-dd hh:mm").create();
		
		String json = gson.toJson(objContainer);
		System.out.println("creato json");

		response.setContentType("application/json");		
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
		System.out.println("mandato  json");
		System.out.println(vincitoreAsta.getName());
		System.out.println(vincitoreAsta.getIndirizzo());
			

		
		
		
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
