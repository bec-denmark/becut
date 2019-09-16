package dk.bec.unittest.becut;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Constants {

	private Constants() {}

	public static final List<String> IBMHostVariableMemoryAllocationPrograms = Collections .unmodifiableList(
			Arrays.asList("DSNHADDR", "DSNHADD2"));

	public static final List<String> IBMSQLPrograms = Collections .unmodifiableList(
			Arrays.asList("DSNHLI"));
}
