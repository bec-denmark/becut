package dk.bec.unittest.becut.compilelist.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class CompileOptions extends AbstractCompileListingSection {
	
	private Map<String, List<CompileOption>> options = new HashMap<String, List<CompileOption>>();
	
	public Map<String, List<CompileOption>> getOptions() {
		return options;
	}

	private static String optionSectionText = "^ \\w*$|^ \\w*\\(.*$";
	private static Pattern optionSectionTextPattern = Pattern.compile(optionSectionText);
	
	public CompileOptions(List<String> section) {
		this.setOriginalSource(section);
		String currentSection = "Unknown";
		for (String s: section) {
			if (optionSectionTextPattern.matcher(s).matches()) {
				currentSection = s.trim();
				ArrayList<CompileOption> ops = new ArrayList<CompileOption>();
				if (currentSection.contains("(")) {
					CompileOption co = new CompileOption(currentSection);
					ops.add(co);
				}
				options.put(currentSection, ops);
			} else {
				List<CompileOption> cos = options.getOrDefault(currentSection, new ArrayList<CompileOption>());
				cos.add(new CompileOption(s));
				options.put(currentSection, cos);
			}
		}
	}

	@Override
	public String toString() {
		return "CompileOptions [options=" + options + "]";
	}
}
