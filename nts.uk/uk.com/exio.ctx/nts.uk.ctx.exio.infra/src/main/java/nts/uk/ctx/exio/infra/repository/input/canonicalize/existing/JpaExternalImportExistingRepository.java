package nts.uk.ctx.exio.infra.repository.input.canonicalize.existing;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.AnyRecordToChange;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.AnyRecordToDelete;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.ExternalImportExistingRepository;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class JpaExternalImportExistingRepository extends JpaRepository implements ExternalImportExistingRepository {
	
	@Override
	public void setup(ExecutionContext context) {
		new LayoutAnyRecordToChange(jdbcProxy(), context).createTable();
		new LayoutAnyRecordToDelete(jdbcProxy(), context).createTable();
	}
	
	@Override
	public void save(ExecutionContext context, AnyRecordToChange record) {
		new LayoutAnyRecordToChange(jdbcProxy(), context).insert(record);
	}
	
	@Override
	public List<AnyRecordToChange> findAllChanges(ExecutionContext context) {
		return new LayoutAnyRecordToChange(jdbcProxy(), context).findAll();
	}
	
	@Override
	public List<AnyRecordToChange> findAllChangesWhere(ExecutionContext context, int keyItemNo, String keyValue) {
		return new LayoutAnyRecordToChange(jdbcProxy(), context).findAllWhere(keyItemNo, keyValue);
	}
	
	@Override
	public void save(ExecutionContext context, AnyRecordToDelete record) {
		new LayoutAnyRecordToDelete(jdbcProxy(), context).insert(record);
	}
	
	@Override
	public List<AnyRecordToDelete> findAllDeletes(ExecutionContext context) {
		return new LayoutAnyRecordToDelete(jdbcProxy(), context).findAll();
	}
	
	@Override
	public List<AnyRecordToDelete> findAllDeletesWhere(ExecutionContext context, int keyItemNo, String keyValue) {
		return new LayoutAnyRecordToDelete(jdbcProxy(), context).findAllWhere(keyItemNo, keyValue);
	}
}
