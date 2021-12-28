package nts.uk.ctx.exio.dom.input.workspace;

import lombok.RequiredArgsConstructor;
import nts.uk.ctx.exio.dom.input.ExecutionContext;

@RequiredArgsConstructor
public class WorkspaceTableName {
	
	private final ExecutionContext context;

	public String asRevised() {
		return create("REVI_");
	}
	
	public String asCanonicalized() {
		return create("CANO_");
	}

	private String create(String type) {
		return TemporaryTable.createTableName(context, type);
	}
}
