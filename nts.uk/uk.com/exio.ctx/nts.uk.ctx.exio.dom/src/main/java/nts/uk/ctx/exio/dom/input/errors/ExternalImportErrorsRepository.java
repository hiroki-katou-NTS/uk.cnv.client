package nts.uk.ctx.exio.dom.input.errors;

import nts.uk.ctx.exio.dom.input.ExecutionContext;

public interface ExternalImportErrorsRepository {

	void cleanOldTables(ExecutionContext context);
	
	void setup(ExecutionContext context);

	void add(ExecutionContext context, ExternalImportError error);
	
	ExternalImportErrors find(ExecutionContext context, int startErrorNo, int size);
}
