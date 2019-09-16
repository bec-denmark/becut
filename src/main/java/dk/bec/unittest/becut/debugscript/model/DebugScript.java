package dk.bec.unittest.becut.debugscript.model;

import java.util.List;

public class DebugScript implements DebugEntity {

	List<DebugEntity> entities;

	public DebugScript(List<DebugEntity> entities) {
		this.entities = entities;
	}

	public List<DebugEntity> getEntities() {
		return entities;
	}

	public void setEntities(List<DebugEntity> entities) {
		this.entities = entities;
	}

	@Override
	public String generate() {
		String script = "";
		for (DebugEntity entity : entities) {
			script = script + "\n" + entity.generate();
		}
		return script;
	}

}
