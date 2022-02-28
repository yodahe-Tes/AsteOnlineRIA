package beans;


public enum AstaStatus {
	CLOSED(0),OPEN(1),SCADUTA(2) ;

	private final int value;

	AstaStatus(int value) {
		this.value = value;
	}

	public static AstaStatus getAstaStatusFromInt(int value) {
		switch (value) {
		
		case 0:
			return AstaStatus.CLOSED;
		case 1:
			return AstaStatus.OPEN;
		case 2:
			return AstaStatus.SCADUTA;	
		}
		return null;
	}

	public int getValue() {
		return value;
	}

}
