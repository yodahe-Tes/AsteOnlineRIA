package controllers;
import beans.Offerta;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import beans.Asta;
import beans.AstaStatus;
import beans.User;

import connection.ConnectionHandler;
import dao.AsteDao;
import dao.OffertaDao;

@WebServlet("/ChiudiAsta")
public class ChiudiAsta extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public ChiudiAsta() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
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
		Integer idVincitoreAsta = null;
		Float prezzoAggiudica = null;
		OffertaDao offertaDao = new OffertaDao(connection);
		Offerta offerta = null;
		User user = (User) session.getAttribute("user");
		AsteDao astaDao = new AsteDao(connection);
		
		try {
			idAsta = Integer.parseInt(request.getParameter("idAstaDaChiudere"));
		} catch (NumberFormatException | NullPointerException e) {
			  e.printStackTrace();
			  response.getWriter().println("Incorrect param values");
			  response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		try {
			offerta = offertaDao.findOffertaVincente(idAsta);

			idVincitoreAsta = offerta.getIdOfferente();
			prezzoAggiudica = offerta.getAmmontareOfferta();
		
		} catch (SQLException  e) {
			 e.printStackTrace();
			 response.getWriter().println("Non esiste questa asta");
			  response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			  return;
		}
		

		try {
			// Check that only the user who created the bid closes it
			Asta asta = astaDao.findAstaById(idAsta);
			
			if (asta == null) {
				response.getWriter().println("Non esiste questa asta");
				  response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			
			if (asta.getSellerId() != user.getUserId()) {
				System.out.println(asta.getSellerId());
				System.out.println(user.getUserId());
				
				response.getWriter().println("Non hai l'autorizzazione per chiudere questa asta");
				  response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
			if (asta.getScadenza().after(new Timestamp(System.currentTimeMillis()))) {
				System.out.println(asta.getSellerId());
				System.out.println(user.getUserId());
				
				response.getWriter().println("Non puoi chiudere , l'asta non e ancora scaduta");
				  response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			astaDao.changeAstaStatus(idAsta,idVincitoreAsta,prezzoAggiudica, AstaStatus.CLOSED);
			
		} catch (SQLException e) {
			 e.printStackTrace();
			 response.getWriter().println("asta non chiudibile , problemi server side");
			  response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		// Return view
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
