package dk.bec.unittest.becut.debugscript.model;

import java.util.ArrayList;
import java.util.List;

import dk.bec.unittest.becut.debugscript.model.variable.Variable;

public class DebugScript implements DebugEntity {

	List<DebugEntity> entities;
	
	List<Variable> variableDeclarations = new ArrayList<Variable>();

	public DebugScript(List<DebugEntity> entities) {
		this.entities = entities;
	}

	public List<DebugEntity> getEntities() {
		return entities;
	}

	public void setEntities(List<DebugEntity> entities) {
		this.entities = entities;
	}

	public List<Variable> getVariableDeclarations() {
		return variableDeclarations;
	}

	public void setVariableDeclarations(List<Variable> variableDeclarations) {
		this.variableDeclarations = variableDeclarations;
	}

	@Override
	public String generate() {
		String script = "";
		for (Variable variable: variableDeclarations) {
			script = script + "\n" + variable.declaration();
		}
		for (DebugEntity entity : entities) {
			script = script + "\n" + entity.generate();
		}
		return script;
	}

}
