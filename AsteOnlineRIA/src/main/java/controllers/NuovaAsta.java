package controllers;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringEscapeUtils;

import beans.User;
import dao.AsteDao;
import connection.ConnectionHandler;

@WebServlet("/NuovaAsta")
@MultipartConfig
public class NuovaAsta extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Connection connection = null;

	public NuovaAsta() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	private Date getMeNow() {
		return new Date(System.currentTimeMillis());
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
		String codice = null;
		String nome = null;
		String descrizione = null;
		Float prezzoIniziale = null;
		Float rialzoMinimo = null;
		Date scadenza = null;
		Part imagePart = request.getPart("image");
		InputStream imageStream = null;
		String mimeType = null;
		
		if (imagePart != null) {
			imageStream = imagePart.getInputStream();
			String filename = imagePart.getSubmittedFileName();
			mimeType = getServletContext().getMimeType(filename);			
		}
		
		if (imageStream == null || (imageStream.available()==0) || !mimeType.startsWith("image/")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Inserisca un immagine in formato jpg");
			return;
		}
		
		try {
			
			codice = RandomStringUtils.randomAlphanumeric(8);
			nome = StringEscapeUtils.escapeJava(request.getParameter("nome"));
			descrizione = StringEscapeUtils.escapeJava(request.getParameter("descrizione"));
			prezzoIniziale = Float.parseFloat(request.getParameter("prezzoIniziale"));
			rialzoMinimo = Float.parseFloat(request.getParameter("rialzoMinimo"));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
			scadenza = (Date) sdf.parse(request.getParameter("scadenza"));
			isBadRequest = prezzoIniziale <= 0 || descrizione.isEmpty() || rialzoMinimo <= 0||getMeNow().after(scadenza);
					//
		} catch (NumberFormatException | NullPointerException | ParseException e) {
			isBadRequest = true;
			e.printStackTrace();
		}
		if (isBadRequest) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Valori inseriti non corretti , controlli di non aver inserito una data passata , o di non aver inserito lettere nella casella per i valori numerici dei prezzi.");
			return;
		}

		// Crea Asta in Database
		User user = (User) session.getAttribute("user");
		AsteDao asteDAO = new AsteDao(connection);
		try {
			asteDAO.creaAsta( codice,nome,descrizione,imageStream,prezzoIniziale,rialzoMinimo,scadenza, user.getUserId());
		} catch (SQLException e) {
			//e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Non è possibile creare l'asta");
			return;
		}
		System.out.println("ho creato l'asta");
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
	