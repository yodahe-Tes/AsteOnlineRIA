package beans;

import java.sql.Timestamp;

public class Asta {

	private int idAsta;
	
	
	private float prezzoIniziale;
	private float rialzoMinimo;
	private AstaStatus status;
	private int sellerId;
	private int vincitoreAsta;
	private float prezzoAggiudica;
	private String codice;
	private String nome;
	private String descrizione;
	private String image;
	private Timestamp scadenza;
	private String tempoMancante; 
	
	
	public void setIdAsta(int idAsta) {
		this.idAsta = idAsta;
	}
	
	public int getIdAsta() {
		return idAsta;
	}
	
	public void setStatus(AstaStatus status) {
		this.status = status;
	}

	public void setStatus(int value) {
		this.status = AstaStatus.getAstaStatusFromInt(value);
	}

	public void setScadenza(Timestamp scadenza) {
		this.scadenza = scadenza;
	}
	      
	public Timestamp getScadenza() {
		return scadenza;
	}


	public void setprezzoIniziale(float f ) {
		this.prezzoIniziale = f;
	}

	
	public float getprezzoIniziale() {
		return prezzoIniziale;
	}

	public void setrialzoMinimo(float rialzoMinimo) {
		this.rialzoMinimo = rialzoMinimo;
	}
	
	public float getrialzoMinimo() {
		return rialzoMinimo;
	}

	public AstaStatus getStatus() {
		return status;
	}

	public boolean isOpen() {
		return status == AstaStatus.OPEN;
	}

	public boolean isClosed() {
		return status == AstaStatus.CLOSED;
	}

	public int getSellerId() {
		return sellerId;
	}

	public void setSellerId(int sellerId) {
		this.sellerId = sellerId;
	}

	public int getVincitoreAsta() {
		return vincitoreAsta;
	}

	public void setVincitoreAsta(int vincitoreAsta) {
		this.vincitoreAsta = vincitoreAsta;
	}

	public float getPrezzoAggiudica() {
		return prezzoAggiudica;
	}

	public void setPrezzoAggiudica(float prezzoAggiudica) {
		this.prezzoAggiudica = prezzoAggiudica;
		
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getTempoMancante() {
		return tempoMancante;
	}

	public void setTempoMancante(String tempoMancante) {
		this.tempoMancante = tempoMancante;
	}

	
	
}
