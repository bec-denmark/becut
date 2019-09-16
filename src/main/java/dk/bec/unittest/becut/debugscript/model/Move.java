package dk.bec.unittest.becut.debugscript.model;

import dk.bec.unittest.becut.testcase.model.Parameter;

public class Move implements Statement {

	private String from;
	private String to;
	private Boolean is88 = Boolean.FALSE;

	public Move(Parameter parameter) {
		switch (parameter.getDataType()) {
		case PIC:
			this.from = "'" + parameter.getValue() + "'";
			break;
		case PIC_NUMERIC:
			this.from = parameter.getValue();
			break;
		case BINARY:
			this.from = parameter.getValue();
			break;
		case CEE_Entry:
			this.from = parameter.getValue();
			break;
		case EIGHTYEIGHT:
			this.from = parameter.getValue();
			is88 = Boolean.TRUE;
			break;
		case GROUP:
			this.from = parameter.getValue();
			break;
		case INDEX:
			this.from = parameter.getValue();
			break;
		case NONE:
			this.from = parameter.getValue();
			break;
		case PACKED_DECIMAL:
			this.from = parameter.getValue();
			break;
		case POINTER:
			this.from = parameter.getValue();
			break;
		case UNKNOWN:
			this.from = parameter.getValue();
			break;
		default:
			this.from = parameter.getValue();
			break;
		}
		this.to = parameter.getName();
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	@Override
	public String generate() {
		if (is88) {
			return "       SET " + to + " TO " + from + ";"; 
		}
		return "       MOVE " + from + " TO " + to + ";";
	}

}
