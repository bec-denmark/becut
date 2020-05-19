package dk.bec.unittest.becut.debugscript.model;

public class ProgramTerminationBreakpoint implements DebugEntity {
	
	private Perform perform;
	private String blockName;

	public ProgramTerminationBreakpoint(Perform perform, String blockName) {
		this.perform = perform;
		this.blockName = blockName;
	}
	@Override
	public String generate() {
		String result = "";
		String performBlock = perform.generate();
		if (!performBlock.isEmpty()) {
			result = "           AT EXIT " + blockName + "\n" + perform.generate();
		}
		return result;
	}

}
