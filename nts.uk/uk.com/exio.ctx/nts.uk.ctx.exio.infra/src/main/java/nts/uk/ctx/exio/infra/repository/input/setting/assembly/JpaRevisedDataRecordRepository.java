package nts.uk.ctx.exio.infra.repository.input.setting.assembly;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.setting.assembly.RevisedDataRecord;
import nts.uk.ctx.exio.dom.input.setting.assembly.RevisedDataRecordRepository;
import nts.uk.ctx.exio.dom.input.workspace.ExternalImportWorkspaceRepository.Require;
import nts.uk.ctx.exio.infra.repository.input.workspace.WorkspaceSql;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class JpaRevisedDataRecordRepository extends JpaRepository implements RevisedDataRecordRepository {

	@Override
	public void save(Require require, ExecutionContext context, RevisedDataRecord record) {
		
		WorkspaceSql.create(require, context, jdbcProxy()).insert(record);
	}

	@Override
	public int getMaxRowNumber(Require require, ExecutionContext context) {
		
		return WorkspaceSql.create(require, context, jdbcProxy())
				.getMaxRowNumberOfRevisedData();
	}

	@Override
	public List<String> getStrings(Require require, ExecutionContext context, int itemNo) {
		
		val workspace = require.getDomainWorkspace(context.getDomainId());
		String columnName = workspace.getItem(itemNo).get().getName();
		
		return WorkspaceSql.create(require, context, jdbcProxy())
				.getStringsOfRevisedData(columnName);
	}

	@Override
	public Optional<RevisedDataRecord> findByRowNo(Require require, ExecutionContext context, int rowNo) {
		
		return WorkspaceSql.create(require, context, jdbcProxy())
				.findRevisedByRowNo(rowNo);
	}

	@Override
	public List<RevisedDataRecord> findAll(Require require, ExecutionContext context) {
		return WorkspaceSql.create(require, context, jdbcProxy()).findAllRevised();
	}

	@Override
	public List<RevisedDataRecord> findByCriteria(
			Require require, ExecutionContext context, int criteriaItemNo, String criteriaValue) {
		
		return WorkspaceSql.create(require, context, jdbcProxy())
				.findRevisedWhere(criteriaItemNo, criteriaValue);
	}
}
