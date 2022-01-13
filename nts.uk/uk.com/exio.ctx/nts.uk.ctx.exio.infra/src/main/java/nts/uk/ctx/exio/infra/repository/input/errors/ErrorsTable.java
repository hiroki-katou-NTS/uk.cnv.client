package nts.uk.ctx.exio.infra.repository.input.errors;

import static nts.uk.ctx.exio.dom.input.workspace.datatype.DataTypeConfiguration.*;

import java.util.List;

import lombok.RequiredArgsConstructor;
import nts.arc.layer.infra.data.database.DatabaseProduct;
import nts.arc.layer.infra.data.jdbc.JdbcProxy;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomainId;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportError;
import nts.uk.ctx.exio.dom.input.workspace.TemporaryTable;

@RequiredArgsConstructor
public class ErrorsTable {

//	private final ExecutionContext context;
	private final String companyId;
	private final DatabaseProduct database;
	private final JdbcProxy jdbcProxy;
	
	private static final String ERROR_NO = "ERROR_NO";
	private static final String ROW_NO = "ROW_NO";
	private static final String DOMAIN_ID = "DOMAIN_ID";
	private static final String ITEM_NO = "ITEM_NO";
	private static final String MESSAGE = "MESSAGE";

	public void dropTable() {
		String tableName = tableName();
		TemporaryTable.dropTable(jdbcProxy, tableName);
	}
	
	public void createTable() {
		
		String tableName = tableName();
		
		createTable(tableName);
	}
	
	private void createTable(String tableName) {
		
		TemporaryTable.createTable(jdbcProxy, database, tableName, b -> b
				.columnPK(ERROR_NO, autonumber())
				.column(ROW_NO, integer(10))
				.column(DOMAIN_ID, integer(3))
				.column(ITEM_NO, integer(4))
				.column(MESSAGE, text(1000)));
	}
	
	public void insert(ExternalImportError error) {
		
		String sql = "insert into " + tableName()
			+ " (" + ROW_NO + "," + DOMAIN_ID + ", " + ITEM_NO + ", " + MESSAGE + ") "
			+ " VALUES (@p1, @p2, @p3, @p4)";
		
		this.jdbcProxy.query(sql)
			.paramInt("p1", error.getCsvRowNo())
			.paramInt("p2", error.getDomainId() != null ? error.getDomainId().value : null)
			.paramInt("p3", error.getItemNo())
			.paramString("p4", error.getMessage())
			.execute();
	}
	
	public List<ExternalImportError> select(int startErrorNo, int size) {
		
		String sql = "select * from " + tableName()
			+ " where " + ERROR_NO + " between @p1 and @p2";
		
		return this.jdbcProxy.query(sql)
				.paramInt("p1", startErrorNo)
				.paramInt("p2", startErrorNo + size)
				.getList(rec -> new ExternalImportError(
						rec.getInt(ROW_NO),
						rec.getEnum(DOMAIN_ID, ImportingDomainId.class),
						rec.getInt(ITEM_NO),
						rec.getString(MESSAGE)));
	}
	
	private String tableName() {
		return TemporaryTable.createErrorTableName(companyId, "ERROR");
	}
}