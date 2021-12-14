package nts.uk.ctx.exio.dom.input.canonicalize.existing;

import java.util.List;

import nts.uk.ctx.exio.dom.input.ExecutionContext;

public interface ExternalImportExistingRepository {
	
	void setup(ExecutionContext context);
	
	void save(ExecutionContext context, AnyRecordToChange record);

	List<AnyRecordToChange> findAllChanges(ExecutionContext context);

	List<AnyRecordToChange> findAllChangesWhere(ExecutionContext context, int keyItemNo, String keyValue);
	
	void save(ExecutionContext context, AnyRecordToDelete record);
	
	List<AnyRecordToDelete> findAllDeletes(ExecutionContext context);

	List<AnyRecordToDelete> findAllDeletesWhere(ExecutionContext context, int keyItemNo, String keyValue);
}
