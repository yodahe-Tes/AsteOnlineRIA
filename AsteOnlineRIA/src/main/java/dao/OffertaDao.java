package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.sql.Timestamp;

import beans.Offerta;

public class OffertaDao {
	private Connection connection;

	public OffertaDao(Connection connection) {
		this.connection = connection;
	}

	public List<Offerta> findOfferteByIdAsta(int idasta) throws SQLException {
		List<Offerta> offerte = new ArrayList<Offerta>();

		String query = "SELECT idOfferta,ammontareOfferta,dataOraOfferta,idastaFk,idOfferente,username from (offerta LEFT OUTER JOIN user on idOfferente = UserId )LEFT OUTER JOIN asta on idastaFK = idAsta where idastaFK = ? and scadenza > CURRENT_TIME() ORDER BY dataOraOfferta DESC";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, idasta);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Offerta offerta = new Offerta();
					offerta.setIdOfferta(result.getInt("idOfferta"));
					offerta.setAmmontareOfferta(result.getInt("ammontareOfferta"));
					offerta.setDataOraOfferta(result.getTimestamp("dataOraOfferta"));
					offerta.setIdAsta(result.getInt("idastaFK"));
					offerta.setIdOfferente(result.getInt("idOfferente"));
					offerta.setNomeOfferente(result.getString("username"));
					offerte.add(offerta);
				}
			}
		}
		return offerte;
	}
	
	
public List<Offerta> findTutteOfferteByIdAsta(Integer idAsta) throws SQLException {  // per trovare tutte le offerte 
	List<Offerta> offerte = new ArrayList<Offerta>();

	String query = "SELECT idOfferta,ammontareOfferta,dataOraOfferta,idastaFk,idOfferente,username from offerta LEFT OUTER JOIN user on idOfferente = UserId where idastaFK = ?  ORDER BY dataOraOfferta DESC";
	try (PreparedStatement pstatement = connection.prepareStatement(query);) {
		pstatement.setInt(1, idAsta);
		try (ResultSet result = pstatement.executeQuery();) {
			while (result.next()) {
				Offerta offerta = new Offerta();
				offerta.setIdOfferta(result.getInt("idOfferta"));
				offerta.setAmmontareOfferta(result.getInt("ammontareOfferta"));
				offerta.setDataOraOfferta(result.getTimestamp("dataOraOfferta"));
				offerta.setIdAsta(result.getInt("idastaFK"));
				offerta.setIdOfferente(result.getInt("idOfferente"));
				offerta.setNomeOfferente(result.getString("username"));
				offerte.add(offerta);
			}
		}
	}
	return offerte;
}
	
	public Offerta findOffertaVincente(int idasta) throws SQLException {    // per trovare tutte le offerte non scadute
		Offerta offerta = null;

		String query = "SELECT idOfferta,max(ammontareOfferta),dataOraOfferta,idastaFk,idOfferente from offerta LEFT OUTER JOIN asta on idastaFK = idAsta where idastaFK = ? and scadenza > dataOraOfferta ORDER BY dataOraOfferta DESC";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, idasta);
			try (ResultSet result = pstatement.executeQuery();) {
				if(result.next()) {
					offerta = new Offerta();
					offerta.setIdOfferta(result.getInt("idOfferta"));
					offerta.setAmmontareOfferta(result.getInt("max(ammontareOfferta)"));
					offerta.setDataOraOfferta(result.getTimestamp("dataOraOfferta"));
					offerta.setIdAsta(result.getInt("idastaFK"));
					offerta.setIdOfferente(result.getInt("idOfferente"));	
					offerta.setNomeOfferente("nomeOfferente");
				}
			}
		}
		return offerta;
	}



	public void creaOfferta(Float ammontareOfferta, Timestamp dataOraOfferta,  int idAsta, int idOfferente)
			throws SQLException {
		String query1 = "INSERT into offerta (ammontareOfferta, dataOraOfferta,idastaFK,idOfferente) VALUES(?, ?, ?, ? );";
		String query2 = " update asta set prezzoAggiudica = ? WHERE idAsta = ? ; ";
		try (PreparedStatement pstatement = connection.prepareStatement(query1);) {
			pstatement.setFloat(1, ammontareOfferta);			
			pstatement.setTimestamp(2, dataOraOfferta);     /* TODO CONTROLLA COME SI FA*/
			pstatement.setInt(3, idAsta);
			pstatement.setInt(4, idOfferente);
			
			pstatement.executeUpdate();
		}
		try (PreparedStatement pstatement = connection.prepareStatement(query2);) {
			
			pstatement.setFloat(1, ammontareOfferta);			
			pstatement.setInt(2, idAsta);
			pstatement.executeUpdate();
		}
	}

	
}
