package dk.bec.unittest.becut.compilelist.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ProgramsCrossReference extends AbstractCompileListingSection {
	
	private List<ProgramReference> programReferences = new ArrayList<ProgramReference>();

	private static Pattern refPattern = Pattern.compile("^EXTERNAL\\s+\\w{1,8}[ .]*(\\d+).*", Pattern.DOTALL);
	
	public ProgramsCrossReference(List<String> lines) {
		originalSource = lines;
		
		String programType = "";
		String programName = "";
		List<Integer> references = new ArrayList<>();
		
		for (String line: lines) {
			String l = line.trim();
			if (refPattern.matcher(l).matches()) {
				String[] parts = l.split("\\s+");
				if (parts.length > 0) {
					if (parts[0].matches("\\d+")) {
						ProgramReference lastSeenReference = programReferences.get(programReferences.size()-1);
						for (int i = 0; i < parts.length; i++) {
							if (parts[i].matches("\\d+")) {
								references.add(Integer.parseInt(parts[i]));
							}
						}
						lastSeenReference.getReferences().addAll(references);
					}
					else {
						programType = parts[0];
						programName = parts[1];
						for (int i = 2; i < parts.length; i++) {
							if (parts[i].matches("\\d+")) {
								references.add(Integer.parseInt(parts[i]));
							}
						}
						programReferences.add(new ProgramReference(programType, programName, references));
					}
				}
				references = new ArrayList<>();
			}
		}
	}
	
	public List<ProgramReference> getProgramReferences() {
		return programReferences;
	}

	public void setProgramReferences(List<ProgramReference> programReferences) {
		this.programReferences = programReferences;
	}

}
