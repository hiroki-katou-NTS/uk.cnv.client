package nts.uk.ctx.exio.infra.repository.input.workspace;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import lombok.val;
import nts.arc.layer.infra.data.JpaRepository;
import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.workspace.ExternalImportWorkspaceRepository;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class JpaExternalImportWorkspaceRepository extends JpaRepository implements ExternalImportWorkspaceRepository {
	
	@Override
	public void setup(Require require, ExecutionContext context) {
		
		WorkspaceSql.cleanOldTables(require, context, jdbcProxy());
		
		val workspace = WorkspaceSql.create(require, context, jdbcProxy());
		
		// 編集済み一時テーブル
		workspace.createTableRevised();
		
		// 正準化済み一時テーブル
		workspace.createTableCanonicalized();
	}
}
