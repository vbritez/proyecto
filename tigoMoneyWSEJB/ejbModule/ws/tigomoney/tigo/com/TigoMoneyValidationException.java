package ws.tigomoney.tigo.com;


public class TigoMoneyValidationException extends Exception {
	
	private static final long serialVersionUID = 7911196506589550715L;
	private String code = "";
    private String description = "";
    private String transactionId = "";
    
    public TigoMoneyValidationException(Throwable t) {
        super(t);
    }  

	public TigoMoneyValidationException(String code, String description,
			String transactionId) {
		super();
		this.code = code;
		this.description = description;
		this.transactionId = transactionId;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

    
}
