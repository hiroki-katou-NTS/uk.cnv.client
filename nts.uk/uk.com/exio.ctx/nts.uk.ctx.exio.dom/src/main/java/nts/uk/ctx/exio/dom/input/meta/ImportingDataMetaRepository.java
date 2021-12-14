package nts.uk.ctx.exio.dom.input.meta;

import nts.uk.ctx.exio.dom.input.ExecutionContext;

public interface ImportingDataMetaRepository {
	
	void setup(ExecutionContext context);
	
	void save(ExecutionContext context, ImportingDataMeta meta);

	ImportingDataMeta find(ExecutionContext context);
}
