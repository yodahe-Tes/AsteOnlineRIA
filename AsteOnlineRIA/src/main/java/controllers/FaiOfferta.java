package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import beans.Asta;
import beans.User;
import connection.ConnectionHandler;
import dao.AsteDao;
import dao.OffertaDao;

@WebServlet("/FaiOfferta")
public class FaiOfferta extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private Connection connection = null;

	public FaiOfferta() {
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

		// Get and parse all parameters from request
		boolean isBadRequest = false;
		Float ammontareOfferta= null;
		Float offertaPreced = null;
		Float rialzoMinimo = null;
		Timestamp dataOraOfferta = null;
		Integer idAsta = null;
		Integer idOfferente = null;
		User user = (User) session.getAttribute("user");
		AsteDao asteDao = new AsteDao(connection);
		Asta asta = null;
		
		
		
		try {                                                              
			ammontareOfferta = Float.parseFloat(request.getParameter("ammontareOffertaArticolo"));
			System.out.println("ammontare offerta is: "+ammontareOfferta);
			dataOraOfferta = new Timestamp(System.currentTimeMillis());;
			System.out.println("dataOra offerta is: "+dataOraOfferta);
			idAsta = Integer.parseInt(request.getParameter("idAstaArticolo"));
			System.out.println("id Asta is: "+idAsta);
			idOfferente = user.getUserId();
			System.out.println("id offerente is: "+idOfferente);
			isBadRequest = ammontareOfferta <= 0 ;
			//|| (ammontareOfferta - max(ammontareOfferta)) < rialzoMinimo ;	
			
			
		} catch (NumberFormatException | NullPointerException e) {
			isBadRequest = true;
			e.printStackTrace();
		}
		if (isBadRequest) {
			
			response.getWriter().println("Inserisci valori validi");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			
			return;
		}
			
		
		
		
		try {
			asta = asteDao.findAstaEarticoloByIdAsta(idAsta);
			offertaPreced = asta.getPrezzoAggiudica();
			rialzoMinimo = asta.getrialzoMinimo();
			
			
			if (asta.getSellerId() == user.getUserId() ) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("Non puoi fare l'offerta sulla tua asta");
				return;
			}
			
			if (dataOraOfferta.after(asta.getScadenza()) ) {
			
			response.getWriter().println("Non puoi fare offerte, l'asta e scaduta");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			
			return;
		    }
			
			System.out.println("ammontare offerta is: "+ammontareOfferta);
			System.out.println("offertaPrec  is: "+offertaPreced);
			System.out.println("rialzo min is: "+rialzoMinimo);

			isBadRequest = (ammontareOfferta - offertaPreced) < rialzoMinimo ;

		} catch (SQLException e1) {
			isBadRequest = true;
			//e1.printStackTrace();
		}
		System.out.println("offertaPrec  is: "+offertaPreced);
		System.out.println("rialzo min is: "+rialzoMinimo);
		if (isBadRequest) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("offerta minore del rialzo Minimo");
			return;
		}
		
		OffertaDao offertaDao = new OffertaDao(connection);
		
		try {
			offertaDao.creaOfferta(ammontareOfferta, dataOraOfferta, idAsta, idOfferente);
		} catch (SQLException e) {
			//e.printStackTrace();
			response.getWriter().println("Non è possible creare l'Offerta");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		
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