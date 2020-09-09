package dk.bec.unittest.becut.recorder.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import dk.bec.unittest.becut.Tuples;
import dk.bec.unittest.becut.Tuples.Tuple2;
import dk.bec.unittest.becut.recorder.DebugToolLogParser;

public class SessionCallPart {
	private Map<Tuple2<Integer, String>, SessionRecord> records = new HashMap<>();

	public List<SessionRecord> getRecords() {
		return new ArrayList<>(records.values());
	}
	
	public SessionRecord getSessionRecord(Integer level, String name) {
		Tuple2<Integer, String> t = Tuples.of(level, name);
		return records.get(t);
	}
	
	//level compileUnit:>name
	//example:
	//03 RDZDB2:>FIRSTNME-LEN  of 02 RDZDB2:>FIRSTNME  of 01 RDZDB2:>EMPRECORD  = +00000 
	private static Pattern p = Pattern.compile("(\\d+)\\s+(.+):>(.+)");
	private static Pattern pGreedy = Pattern.compile(".*(\\d+\\s+[A-Z0-9-]+:>[A-Z0-9-]+)");
	
	public static String[] split(String line) {
		if(line.indexOf('=') != -1) {
			return line.split(" = ");
		}
		Matcher m = pGreedy.matcher(line);
		if(m.find()) {
			int i = m.end(1);
			return new String[]{
					line.substring(0, i),
					line.substring(i, line.length()),
				};
		}
		return new String[0];
	}
	
	public SessionCallPart(List<String> callBlock) {
		List<String> lines = sanitize(callBlock);
		lines
			.stream()
			.forEach(line -> {
				String[] as = split(line);
				assert as.length == 2 : "Couldn't find value part in '" + line + '"';
				String path = as[0];
				String value = as[1];
				LinkedList<String> stack = new LinkedList<>(Arrays.asList(path.split(" of ")));
				SessionRecord parent = null;
				while(!stack.isEmpty()) {
					String rec = stack.pollLast();
					Matcher m = p.matcher(rec);
					if(m.find()) {
						Integer level = Integer.valueOf(m.group(1));
						String compileUnit = m.group(2);
						String name = m.group(3).trim();
						Tuple2<Integer, String> key = Tuples.of(level,  name);
						if(parent == null) {
							records.computeIfAbsent(key, k -> new SessionRecord(k._1(), compileUnit, k._2(), null));
							SessionRecord sr = records.get(key);
							if(stack.peek() == null) {
								sr.setValue(value);
							}
							parent = sr;
						} else {
							SessionRecord p = parent;
							records.computeIfAbsent(key, k -> new SessionRecord(k._1(), compileUnit, k._2(), p));
							SessionRecord sr = records.get(key);
							if(stack.peek() == null) {
								sr.setValue(value);
							}
							parent.getChildren().add(sr);
							parent = sr;
						}
					}
				}
			});
	}

	private static Pattern sub = Pattern.compile("^SUB\\(\\d+\\) .*");
	public static List<String> sanitize(List<String> list) {
		LinkedList<String> lines = new LinkedList<>();
		list.forEach(line -> {
			line = DebugToolLogParser.removeProlog(line);
			//TODO 
			if(sub.matcher(line).matches()) {
				return;
			}
			if (!lines.isEmpty()) {
				if (lines.peekLast().matches("\\d+ .*") && !line.matches("\\d+ .*")) {
					lines.add(lines.pollLast() + line);
				} else {
					lines.add(line);
				}
			} else {
				lines.add(line);
			}
		});
		//example:
		//"      * FD CALLER:>NUMBER-LIST  = OPEN INPUT, FILE STATUS: 00           \n" + 
		//"      * 01 CALLER:>NUMBERS-RECORD  =                                    " +
		//filter the first line as it is not a record but a file descriptor
		return lines
				.stream()
				.filter(s -> Character.isDigit(s.charAt(0))).collect(Collectors.toList());
	}
}
