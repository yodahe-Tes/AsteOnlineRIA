package beans;

import java.sql.Timestamp;

public class Offerta {
	
	private int idOfferta;
	private float ammontareOfferta;
	private Timestamp dataOraofferta;
	private int idAsta;
	private int idOfferente;
	private String nomeOfferente;
	
	public int getIdOfferta() {
		return idOfferta;
	}
	public void setIdOfferta(int idOfferta) {
		this.idOfferta = idOfferta;
	}
	public float getAmmontareOfferta() {
		return ammontareOfferta;
	}
	public void setAmmontareOfferta(float ammontareOfferta) {
		this.ammontareOfferta = ammontareOfferta;
	}
	public Timestamp getDataOraOfferta() {
		return dataOraofferta;
	}
	public void setDataOraOfferta(Timestamp dataOraOfferta) {
		this.dataOraofferta = dataOraOfferta;
	}
	public int getIdAsta() {
		return idAsta;
	}
	public void setIdAsta(int idAsta) {
		this.idAsta = idAsta;
	}
	public int getIdOfferente() {
		return idOfferente;
	}
	public void setIdOfferente(int idOfferente) {
		this.idOfferente = idOfferente;
	}
	
	public String getNomeOfferente() {
		return nomeOfferente;
	}
	public void setNomeOfferente(String nomeOfferente) {
		this.nomeOfferente = nomeOfferente;
	}
	

}
