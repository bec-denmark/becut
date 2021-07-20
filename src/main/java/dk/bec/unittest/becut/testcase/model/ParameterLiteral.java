package dk.bec.unittest.becut.testcase.model;

import java.util.concurrent.atomic.AtomicLong;

import dk.bec.unittest.becut.compilelist.model.DataType;

public class ParameterLiteral extends Parameter {
	
	private static final AtomicLong count = new AtomicLong(1);
	
	public ParameterLiteral(String parameterName) {
		
		name = "LITERAL" + count.getAndIncrement() + "_" + parameterName;
		
		if (parameterName.startsWith("\")") || parameterName.startsWith("'")) {
			dataType = DataType.PIC;
		}
		else {
			dataType = DataType.PIC_NUMERIC;
		}
		
		value = parameterName;
				
	}
	
	@Override
	public String getGuiName() {
		// TODO Auto-generated method stub
		return value;
	}

	@Override
	public String guiString() {
		return "Literal";
	}
	
	public boolean matches(String s) {
		return value.equals(s);
	}
}
