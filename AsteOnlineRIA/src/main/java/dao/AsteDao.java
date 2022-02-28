package dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import beans.Asta;
import beans.AstaStatus;

public class AsteDao {
	private Connection connection;

	public AsteDao(Connection connection) {
		this.connection = connection;
	}

	public List<Asta> findAsteChiuseBySellerId(int userId) throws SQLException {   // Non serve piu , E stato sostituito
		List<Asta> aste = new ArrayList<Asta>();

		String query = "SELECT * from asta where sellerId = ? AND status = ? ORDER BY scadenza DESC";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, userId);
			pstatement.setInt(2, AstaStatus.CLOSED.getValue());

			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Asta asta = new Asta();
					asta.setIdAsta(result.getInt("idAsta"));
					asta.setprezzoIniziale(result.getFloat("prezzoIniziale"));
					asta.setrialzoMinimo(result.getFloat("rialzoMinimo"));
					asta.setStatus(result.getInt("status"));
					asta.setScadenza(result.getTimestamp("scadenza"));
					asta.setSellerId(result.getInt("sellerId"));
					
					
					aste.add(asta);
				}
			}
		}
		return aste;
	}
	
	public List<Asta> findAsteAperteBySellerId(int userId) throws SQLException {     // Non serve piu , E stato sostituito
		List<Asta> aste = new ArrayList<Asta>();

		String query = "SELECT * from asta where sellerId = ?  ORDER BY scadenza DESC";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, userId);
		//	pstatement.setInt(2, AstaStatus.OPEN.getValue());
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Asta asta = new Asta();
					
					asta.setIdAsta(result.getInt("idAsta"));
					asta.setprezzoIniziale(result.getFloat("prezzoIniziale"));
					asta.setrialzoMinimo(result.getFloat("rialzoMinimo"));
					asta.setStatus(result.getInt("status"));
					asta.setScadenza(result.getTimestamp("scadenza"));
					asta.setSellerId(result.getInt("sellerId"));
					aste.add(asta);
				}
			}
		}
		return aste;
	}
	
	public List<Asta> findAsteEarticoloAperteBySellerId(int userId,Timestamp tempoLogin) throws SQLException {
		List<Asta> dettAstArtcliAprt = new ArrayList<Asta>();

		String query ="SELECT idasta, sellerId, status, codice ,scadenza , nome, max(ammontareOfferta), CONCAT(ROUND(GREATEST(TIMESTAMPDIFF(MINUTE,?,scadenza)/1440,0),0),'d:',ROUND(GREATEST(TIMESTAMPDIFF(MINUTE,?,scadenza)%1440/60,0),0),'h:',GREATEST(TIMESTAMPDIFF(MINUTE,?,scadenza)%1440%60,0),'m') as tempoMancante FROM asta LEFT OUTER JOIN offerta ON idAsta = offerta.idastaFK where sellerId = ? AND status = ? GROUP BY idAsta ORDER BY scadenza ASC";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setTimestamp(1,tempoLogin );
			pstatement.setTimestamp(2,tempoLogin );
			pstatement.setTimestamp(3,tempoLogin );
			pstatement.setInt(4, userId);
			pstatement.setInt(5, AstaStatus.OPEN.getValue());

			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Asta dettAstArt = new Asta();
					dettAstArt.setIdAsta(result.getInt("idAsta"));
					dettAstArt.setSellerId(result.getInt("sellerId"));
					dettAstArt.setCodice(result.getString("codice"));
					dettAstArt.setNome(result.getString("nome"));
					dettAstArt.setPrezzoAggiudica(result.getInt("max(ammontareOfferta)"));
					dettAstArt.setStatus(result.getInt("status"));
					dettAstArt.setScadenza(result.getTimestamp("scadenza"));
					dettAstArt.setTempoMancante(result.getString("tempoMancante"));
					dettAstArtcliAprt.add(dettAstArt);
				}
			}
		}
		return dettAstArtcliAprt;
	}
	
	public List<Asta> findAsteEarticoloChiuseBySellerId(int userId,Timestamp tempoLogin) throws SQLException {
		List<Asta> dettAstArtcliCs = new ArrayList<Asta>();

		String query = "SELECT idAsta, sellerId,status,prezzoAggiudica,descrizione, codice , nome, scadenza ,CONCAT(ROUND(TIMESTAMPDIFF(MINUTE,?,scadenza)/1440,0),'d:',ROUND(TIMESTAMPDIFF(MINUTE,?,scadenza)%1440/60,0),'h:',TIMESTAMPDIFF(MINUTE,?,scadenza)%1440%60,'m') as tempoMancante FROM asta   LEFT OUTER JOIN offerta ON idAsta = offerta.idastaFK where sellerId = ? AND status = ? GROUP BY idAsta ORDER BY scadenza ASC";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setTimestamp(1,tempoLogin );
			pstatement.setTimestamp(2,tempoLogin );
			pstatement.setTimestamp(3,tempoLogin );
			pstatement.setInt(4, userId);
			pstatement.setInt(5, AstaStatus.CLOSED.getValue());

			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Asta dettAstArt = new Asta();
					dettAstArt.setIdAsta(result.getInt("idAsta"));
					dettAstArt.setCodice(result.getString("codice"));
					dettAstArt.setNome(result.getString("nome"));System.out.println(dettAstArt.getNome());
					dettAstArt.setPrezzoAggiudica(result.getInt("prezzoAggiudica"));
					dettAstArt.setSellerId(result.getInt("sellerId"));
					dettAstArt.setDescrizione(result.getString("descrizione"));
					dettAstArt.setStatus(result.getInt("status"));
					dettAstArt.setScadenza(result.getTimestamp("scadenza"));					
					dettAstArt.setTempoMancante(result.getString("tempoMancante"));
					dettAstArtcliCs.add(dettAstArt);
								}
			}
		}
		return dettAstArtcliCs;
	}
	

	
	
	public Asta findAstaById(int idAsta) throws SQLException {
		Asta asta = null;

		String query = "SELECT * FROM asta WHERE idAsta = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, idAsta);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					asta = new Asta();
					asta.setIdAsta(result.getInt("idAsta"));
					asta.setprezzoIniziale(result.getFloat("prezzoIniziale"));
					asta.setrialzoMinimo(result.getFloat("rialzoMinimo"));
					asta.setStatus(result.getInt("status"));
					asta.setScadenza(result.getTimestamp("scadenza"));
					asta.setSellerId(result.getInt("sellerId"));
				}
			}
		}
		return asta;
	}
	
	public Asta findAstaEarticoloByIdAsta(int idAsta) throws SQLException {
		Asta dettAst = new Asta();

		String query ="SELECT idAsta,  codice ,nome , descrizione, image,prezzoIniziale , prezzoAggiudica, rialzoMinimo , scadenza,status, sellerId  FROM asta where idAsta = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, idAsta);

			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					dettAst.setIdAsta(result.getInt("idAsta"));System.out.println(dettAst.getIdAsta());
					dettAst.setCodice(result.getString("codice"));
					dettAst.setNome(result.getString("nome"));
					dettAst.setDescrizione(result.getString("descrizione"));
					byte[] imgData = result.getBytes("image");
					String encodedImg=Base64.getEncoder().encodeToString(imgData);
					dettAst.setImage(encodedImg);
					dettAst.setprezzoIniziale(result.getFloat("prezzoIniziale"));
					dettAst.setPrezzoAggiudica(result.getInt("prezzoAggiudica"));
					dettAst.setrialzoMinimo(result.getFloat("rialzoMinimo"));
					dettAst.setScadenza(result.getTimestamp("scadenza"));
					dettAst.setStatus(result.getInt("status"));
					dettAst.setSellerId(result.getInt("sellerId"));
				}
			}
		}
		return dettAst;
	}
	
	
	public Asta findAstaEarticoloCsByIdAsta(int idAsta) throws SQLException {
		Asta dettAstCs = new Asta();

		String query ="SELECT idAsta, status, sellerId, image,codice , nome,  scadenza, username ,prezzoAggiudica, indirizzo,vincitoreAsta FROM asta LEFT OUTER JOIN user on userId = vincitoreAsta where idAsta = ? and status = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, idAsta);
			pstatement.setInt(2, AstaStatus.CLOSED.getValue());

			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					dettAstCs.setIdAsta(result.getInt("idAsta"));System.out.println(dettAstCs.getIdAsta());
					dettAstCs.setCodice(result.getString("codice"));
					dettAstCs.setNome(result.getString("nome"));
					dettAstCs.setSellerId(result.getInt("sellerId"));
					byte[] imgData = result.getBytes("image");
					String encodedImg=Base64.getEncoder().encodeToString(imgData);
					dettAstCs.setImage(encodedImg);
					dettAstCs.setStatus(result.getInt("status"));
					dettAstCs.setScadenza(result.getTimestamp("scadenza"));
					dettAstCs.setVincitoreAsta(result.getInt("vincitoreAsta"));
					dettAstCs.setPrezzoAggiudica(result.getFloat("prezzoAggiudica"));
		
				}
			}
		}
		return dettAstCs;
	}

	
	public List<Asta> trovaAsteBySearchword(String parola, int userId) throws SQLException {
		List<Asta> dettAstArtcliAprt = new ArrayList<Asta>();

		String query ="SELECT idAsta, sellerId, status,codice , nome, scadenza FROM asta where (INSTR(nome, ?) > 0  or INSTR(descrizione, ?) > 0) AND sellerId != ? AND status = ? AND scadenza> CURRENT_TIME() GROUP BY idAsta ORDER BY scadenza DESC";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1,parola);
			pstatement.setString(2,parola);
			pstatement.setInt(3,userId);
			System.out.println(parola);
			pstatement.setInt(4, AstaStatus.OPEN.getValue());

			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Asta dettAstArt = new Asta();
					dettAstArt.setIdAsta(result.getInt("idAsta"));
					dettAstArt.setSellerId(result.getInt("sellerId"));
					dettAstArt.setCodice(result.getString("codice"));
					dettAstArt.setNome(result.getString("nome"));
					dettAstArt.setStatus(result.getInt("status"));
					dettAstArt.setScadenza(result.getTimestamp("scadenza"));
					dettAstArtcliAprt.add(dettAstArt);
				}
			}
		}
		return dettAstArtcliAprt;
	}

	public List<Asta> findAsteEarticoloChiuseByBuyerId(int userId) throws SQLException{
		List<Asta> dettAstArtcliCs = new ArrayList<Asta>();

		String query = "SELECT idAsta, status, codice , descrizione, nome, max(ammontareOfferta), scadenza FROM asta  LEFT OUTER JOIN offerta ON idasta = offerta.idastaFK where vincitoreAsta = ? AND status = ? GROUP BY idAsta ORDER BY scadenza DESC";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, userId);
			pstatement.setInt(2, AstaStatus.CLOSED.getValue());

			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Asta dettAstArt = new Asta();
					dettAstArt.setIdAsta(result.getInt("idAsta"));
					dettAstArt.setCodice(result.getString("codice"));
					dettAstArt.setNome(result.getString("nome"));System.out.println(dettAstArt.getNome());
					dettAstArt.setDescrizione(result.getString("descrizione"));
					dettAstArt.setPrezzoAggiudica(result.getInt("max(ammontareOfferta)"));
					dettAstArt.setStatus(result.getInt("status"));
					dettAstArt.setScadenza(result.getTimestamp("scadenza"));
					dettAstArtcliCs.add(dettAstArt);
					
				}
			}
		}
		return dettAstArtcliCs;
	}



	public void changeAstaStatus(int idAsta, int vincitoreAsta,float prezzoAggiudica,  AstaStatus astaStatus) throws SQLException {
		String query = "UPDATE asta SET status = ? , vincitoreAsta = ? , prezzoAggiudica = ? WHERE idAsta = ? ";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, astaStatus.getValue());
			pstatement.setInt(2, vincitoreAsta);
			pstatement.setFloat(3, prezzoAggiudica);
			pstatement.setInt(4,idAsta);
			pstatement.executeUpdate();
		}
	}

	public void creaAsta(String codice, String nome,String descrizione,InputStream imageStream,float prezzoIniziale, float rialzoMinimo,Date scadenza,int sellerId )
			throws SQLException {

		String query = "INSERT into asta (codice,nome,descrizione,image,prezzoIniziale,prezzoAggiudica, rialzoMinimo,scadenza,sellerId,status) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setString(1, codice);	
			pstatement.setString(2, nome);	
			pstatement.setString(3, descrizione);	
			pstatement.setBlob(4, imageStream);
			pstatement.setFloat(5, prezzoIniziale);
			pstatement.setFloat(6, prezzoIniziale);
			pstatement.setFloat(7, rialzoMinimo);
			pstatement.setTimestamp(8, new java.sql.Timestamp(scadenza.getTime()));     /* TODO CONTROLLA COME SI FA*/
			pstatement.setInt(9, sellerId);	
			pstatement.setInt(10, AstaStatus.OPEN.getValue());
			pstatement.executeUpdate();
		}
	}
}
