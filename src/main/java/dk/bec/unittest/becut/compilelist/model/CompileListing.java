package dk.bec.unittest.becut.compilelist.model;

import java.util.List;

import dk.bec.unittest.becut.compilelist.TreeUtil;

public class CompileListing extends AbstractCompileListingSection {

	private String programName;
	private InvocationParameters invocationParameters;
	private CompileOptions compileOptions;
	private SourceMapAndCrossReference sourceMapAndCrossReference;
	private DataNamesCrossReference dataNamesCrossReference;
	private ProceduresCrossReference proceduresCrossReference;
	private ProgramsCrossReference programsCrossReference;
	private DataDivisionMap dataDivisionMap;

	public CompileListing(InvocationParameters invocationParameters, CompileOptions compileOptions,
			SourceMapAndCrossReference sourceMapAndCrossReference, DataNamesCrossReference dataNamesCrossReference,
			ProceduresCrossReference proceduresCrossReference, ProgramsCrossReference programsCrossReference,
			DataDivisionMap dataDivisionMap, List<String> originalSource) {
		super();
		this.originalSource = originalSource;
		this.invocationParameters = invocationParameters;
		this.compileOptions = compileOptions;
		this.sourceMapAndCrossReference = sourceMapAndCrossReference;
		this.dataNamesCrossReference = dataNamesCrossReference;
		this.proceduresCrossReference = proceduresCrossReference;
		this.programsCrossReference = programsCrossReference;
		this.dataDivisionMap = dataDivisionMap;
		this.programName = TreeUtil.stripQuotes(TreeUtil.getFirst(sourceMapAndCrossReference.getAst(), "programName").getProgramText());
	}

	public InvocationParameters getInvocationParameters() {
		return invocationParameters;
	}

	public void setInvocationParameters(InvocationParameters invocationParameters) {
		this.invocationParameters = invocationParameters;
	}

	public CompileOptions getCompileOptions() {
		return compileOptions;
	}

	public void setCompileOptions(CompileOptions compileOptions) {
		this.compileOptions = compileOptions;
	}

	public SourceMapAndCrossReference getSourceMapAndCrossReference() {
		return sourceMapAndCrossReference;
	}

	public void setSourceMapAndCrossReference(SourceMapAndCrossReference sourceMapAndCrossReference) {
		this.sourceMapAndCrossReference = sourceMapAndCrossReference;
	}

	public DataNamesCrossReference getDataNamesCrossReference() {
		return dataNamesCrossReference;
	}

	public void setDataNamesCrossReference(DataNamesCrossReference dataNamesCrossReference) {
		this.dataNamesCrossReference = dataNamesCrossReference;
	}

	public ProceduresCrossReference getProceduresCrossReference() {
		return proceduresCrossReference;
	}

	public void setProceduresCrossReference(ProceduresCrossReference proceduresCrossReference) {
		this.proceduresCrossReference = proceduresCrossReference;
	}

	public ProgramsCrossReference getProgramsCrossReference() {
		return programsCrossReference;
	}

	public void setProgramsCrossReference(ProgramsCrossReference programsCrossReference) {
		this.programsCrossReference = programsCrossReference;
	}

	public DataDivisionMap getDataDivisionMap() {
		return dataDivisionMap;
	}

	public void setDataDivisionMap(DataDivisionMap dataDivisionMap) {
		this.dataDivisionMap = dataDivisionMap;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}
}
