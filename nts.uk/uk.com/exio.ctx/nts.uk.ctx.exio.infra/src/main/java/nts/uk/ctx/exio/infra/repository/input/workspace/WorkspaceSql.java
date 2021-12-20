package nts.uk.ctx.exio.infra.repository.input.workspace;

import static java.util.stream.Collectors.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import nts.arc.layer.infra.data.database.DatabaseProduct;
import nts.arc.layer.infra.data.jdbc.JdbcProxy;
import nts.arc.layer.infra.data.jdbc.NtsResultSet.NtsResultRecord;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.uk.ctx.exio.dom.input.DataItem;
import nts.uk.ctx.exio.dom.input.DataItemList;
import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.canonicalize.result.CanonicalItem;
import nts.uk.ctx.exio.dom.input.canonicalize.result.CanonicalizedDataRecord;
import nts.uk.ctx.exio.dom.input.domain.ImportingDomain;
import nts.uk.ctx.exio.dom.input.setting.assembly.RevisedDataRecord;
import nts.uk.ctx.exio.dom.input.workspace.ExternalImportWorkspaceRepository.Require;
import nts.uk.ctx.exio.dom.input.workspace.TemporaryTable;
import nts.uk.ctx.exio.dom.input.workspace.WorkspaceTableName;
import nts.uk.ctx.exio.dom.input.workspace.builder.CreateTableBuilder;
import nts.uk.ctx.exio.dom.input.workspace.datatype.DataType;
import nts.uk.ctx.exio.dom.input.workspace.datatype.DataTypeConfiguration;
import nts.uk.ctx.exio.dom.input.workspace.domain.DomainWorkspace;
import nts.uk.ctx.exio.dom.input.workspace.item.WorkspaceItem;
import nts.uk.shr.com.company.CompanyId;

/**
 * ワークスペースの一時テーブルを扱うクラス
 * 現状はSQLServerにしか対応できていないので、後でPostgreSQL対応が必要。
 * その際、できるだけTemporaryTableクラスに処理を移譲すること。
 */
@RequiredArgsConstructor
public class WorkspaceSql {

	private final ExecutionContext context;
	private final ImportingDomain domain;
	private final DomainWorkspace workspace;
	private final JdbcProxy jdbcProxy;
	private final DatabaseProduct database;	//this.database().product();

	static final Column ROW_NO = new Column("ROW_NO", new DataTypeConfiguration(DataType.INT, 10,0), "rowno");
	static final Column CONTRACT_CD = new Column("CONTRACT_CD", new DataTypeConfiguration(DataType.STRING, 12,0), "contract");
	static final Column CID = new Column("CID", new DataTypeConfiguration(DataType.STRING, 17,0), "cid");

	public static WorkspaceSql create(Require require, ExecutionContext context, JdbcProxy jdbcProxy, DatabaseProduct database) {

		val domain = require.getImportingDomain(context.getDomainId());
		val workspace = require.getDomainWorkspace(context.getDomainId());
		
		return new WorkspaceSql(context, domain, workspace, jdbcProxy, database);
	}
	
	/**
	 * 古い一時テーブルをすべて削除する
	 * @param require
	 * @param context
	 * @param jdbcProxy
	 */
	public static void cleanOldTables(Require require, ExecutionContext context, JdbcProxy jdbcProxy) {
		val domain = require.getImportingDomain(context.getDomainId());		
		val tableName = tableName(context, domain);
		TemporaryTable.dropTable(jdbcProxy, tableName.asRevised());
		TemporaryTable.dropTable(jdbcProxy, tableName.asCanonicalized());
	}
	
	/**
	 * 編集済み用のCREATE TABLEを実行する
	 * @return
	 */
	public void createTableRevised() {
		createTable(tableName().asRevised());
	}

	/**
	 * 正準化済み用のCREATE TABLEを実行する
	 * @return
	 */
	public void createTableCanonicalized() {
		createTable(tableName().asCanonicalized());
	}

	private void createTable(String tableName) {
		TemporaryTable.createTable(jdbcProxy, database, tableName, b -> {
			// 正準化時にうまれる項目を主キーに指定できない（編集時にはNULLである）ので、一旦ROW_NOを固定で主キーとする
			// 必要なら主キーではなくインデックスにすることを検討する
			b = b.columnPK(ROW_NO.name, ROW_NO.type)
					.column(CONTRACT_CD.name, CONTRACT_CD.type)
					.column(CID.name, CID.type);
			for (WorkspaceItem item : workspace.getItemsPk()){
				b = b.column(item.getName(), item.getDataTypeConfig());
			}
			for (WorkspaceItem item : workspace.getItemsNotPk()){
				b = b.column(item.getName(), item.getDataTypeConfig());
			}
		});
	}
	
	static class CommonColumns {
		static final List<String> LIST = Arrays.asList(ROW_NO.paramName, CONTRACT_CD.paramName, CID.paramName);
		
		static String sqlParams() {
			return LIST.stream()
					.map(paramName -> "@" + paramName)
					.collect(Collectors.joining(","));
		}
		
		static void setParams(NtsStatement statement, int rowNo, ExecutionContext context) {
			
			statement.paramInt(ROW_NO.paramName, rowNo);
			
			String companyId = context.getCompanyId();
			String contractCode = CompanyId.getContractCodeOf(companyId);
			statement.paramString(CONTRACT_CD.paramName, contractCode);
			statement.paramString(CID.paramName, companyId);
		}
	}
	
	@Value
	static class Column {
		String name;
		DataTypeConfiguration type;
		String paramName;
	}

	/**
	 * 編集済み用のINSERT文を実行する
	 * @param record
	 * @return
	 */
	public void insert(RevisedDataRecord record) {
		insert(
				tableName().asRevised(),
				record.getRowNo(),
				itemNo -> record.getItemByNo(itemNo).map(DataItem::getValue).orElse(null));
	}
	
	/**
	 * 正準化済み用のINSERT文を実行する
	 * @param record
	 */
	public void insert(CanonicalizedDataRecord record) {
		insert(
				tableName().asCanonicalized(),
				record.getRowNo(),
				itemNo -> record.getItemByNo(itemNo).map(CanonicalItem::getValue).orElse(null));
	}
	
	private void insert(
			String tableName,
			int rowNo,
			Function<Integer, Object> itemValueGetter) {

		/*
		 * VALUES句の列順は、項目No順にテーブルが作られるという仕様を前提とする。
		 * ただし先頭はROW_NO, CONTRACT_CD, CID列で固定。
		 */
		String sql = Insert.createInsertSql(tableName, workspace);
		
		val statement = jdbcProxy.query(sql);
		
		CommonColumns.setParams(statement, rowNo, context);
		
		for (val workspaceItem : workspace.getAllItemsSortedByItemNo()) {
			val dataType = workspaceItem.getDataTypeConfig();
			Object itemValue = itemValueGetter.apply(workspaceItem.getItemNo());
			Insert.setParam(dataType, itemValue, statement, workspaceItem);
		}
		
		statement.execute();
	}

	static class Insert {

		static String createInsertSql(String tableName, DomainWorkspace workspace) {
			
			return new StringBuilder()
				.append("insert into ")
				.append(tableName)
				.append(" values (")
				.append(CommonColumns.sqlParams() + ",")
				.append(workspace.getAllItemsSortedByItemNo().stream()
						.map(item -> "@" + Insert.paramItem(item.getItemNo()))
						.collect(Collectors.joining(",")))
				.append(");")
				.toString();
		}

		static void setParam(
				DataTypeConfiguration dataType,
				Object value,
				NtsStatement statement,
				WorkspaceItem workspaceItem) {
			
			String param = paramItem(workspaceItem.getItemNo());
			
			try {
				dataType.getType().setParam(statement, param, value);
			} catch (Exception ex) {
				throw new RuntimeException("パラメータ設定に失敗：" + value + ", " + dataType + ", " + workspaceItem, ex);
			}
		}
		
		static String paramItem(int itemNo) {
			return "p" + itemNo;
		}
	}
	
	public int getMaxRowNumberOfRevisedData() {
		String sql = "select max(" + ROW_NO.name + ") from " + tableName().asRevised();
		return jdbcProxy.query(sql).getSingle(rec -> rec.getInt(1)).get();
	}
	
	public List<String> getStringsOfRevisedData(String columnName) {
		String sql = "select " + columnName + " from " + tableName().asRevised();
		return jdbcProxy.query(sql).getList(rec -> rec.getString(1)).stream().distinct().collect(Collectors.toList());
	}
	
	public Optional<RevisedDataRecord> findRevisedByRowNo(int rowNo) {
		String sql = "select * from " + tableName().asRevised()
				+ " where " + ROW_NO.name + " = " + rowNo;
		return jdbcProxy.query(sql).getSingle(rec -> toRevised(rec));
	}
	
	public List<RevisedDataRecord> findAllRevised() {
		String sql = "select * from " + tableName().asRevised();
		return jdbcProxy.query(sql).getList(rec -> toRevised(rec));
	}
	
	public List<RevisedDataRecord> findRevisedWhere(int itemNoCondition, String conditionString) {
		
		String columnName = workspace.getItem(itemNoCondition)
				.orElseThrow(() -> new RuntimeException("not found: " + itemNoCondition))
				.getName();
		
		String sql = "select * from " + tableName().asRevised()
				+ " where " + columnName + " = @p";
		
		return jdbcProxy.query(sql)
				.paramString("p", conditionString)
				.getList(rec -> toRevised(rec));
	}
	
	private RevisedDataRecord toRevised(NtsResultRecord record) {
		
		int rowNo = record.getInt(ROW_NO.name);
		
		val items = workspace.getAllItemsSortedByItemNo().stream()
				.map(wi -> toDataItem(record, wi))
				.collect(toList());
		
		return new RevisedDataRecord(rowNo, new DataItemList(items));
	}
	
	private static DataItem toDataItem(NtsResultRecord record, WorkspaceItem workspaceItem) {
		
		val dataType = workspaceItem.getDataTypeConfig();
		int itemNo = workspaceItem.getItemNo();
		String name = workspaceItem.getName();
		
		// nullの場合
		if(Objects.isNull(record.getObject(name))) {
			return DataItem.of(itemNo);
		}
		
		switch (dataType.getType()) {
		case INT:
			return DataItem.of(itemNo, record.getLong(name));
		case REAL:
			return DataItem.of(itemNo, record.getBigDecimal(name));
		case STRING:
			return DataItem.of(itemNo, record.getString(name));
		case DATE:
			return DataItem.of(itemNo, record.getGeneralDate(name));
		default:
			throw new RuntimeException("unknown: " + dataType.getType());
		}
	}
	
	public List<String> getAllEmployeeIdsOfCanonicalizedData() {
		
		// 社員IDのカラム名は固定で SID
		String sql = "select SID from " + tableName().asCanonicalized();
		
		return jdbcProxy.query(sql)
				.getList(rec -> rec.getString(1))
				.stream()
				.distinct()
				.collect(toList());
	}
	
	private WorkspaceTableName tableName() {
		return tableName(context, domain);
	}
	
	private static WorkspaceTableName tableName(ExecutionContext context, ImportingDomain domain) {
		return new WorkspaceTableName(context, domain.getName());
	}
}
