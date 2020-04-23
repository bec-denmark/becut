package dk.bec.unittest.becut.ui.model;

public class ValidationResult {

	private Boolean isSuccess;
	private String invalidText;

	public ValidationResult(Boolean isSuccess) {
		this.isSuccess = isSuccess;
		invalidText = "";
	}

	public ValidationResult(Boolean isSuccess, String invalidText) {
		this.isSuccess = isSuccess;
		this.invalidText = invalidText;
	}

	public Boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getInvalidText() {
		return invalidText;
	}

	public void setInvalidText(String invalidText) {
		this.invalidText = invalidText;
	}

}
