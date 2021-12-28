package nts.uk.ctx.exio.dom.input.workspace;

import java.util.function.Consumer;

import lombok.val;
import nts.arc.layer.infra.data.database.DatabaseProduct;
import nts.arc.layer.infra.data.jdbc.JdbcProxy;
import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.workspace.builder.CreateTableBuilder;

/**
 * 外部受入の一時テーブルを扱う
 */
public class TemporaryTable {
	
	private static final String PREFIX = "外部受入_";
	
	public static String createTableName(ExecutionContext context, String name) {
		return PREFIX + context.getDomainId().value + "_"  + name + "_" + context.getCompanyId().replace("-", "");
	}

	public static String createErrorTableName(String companyId, String name) {
		return PREFIX + name + "_" + companyId.replace("-", "");
	}
	
	/**
	 * テーブルを作成する
	 * @param jdbcProxy
	 * @param database
	 * @param tableName
	 * @param buildProcess
	 */
	public static void createTable(
			JdbcProxy jdbcProxy,
			DatabaseProduct database,
			String tableName,
			Consumer<CreateTableBuilder> buildProcess) {
		
		val builder = CreateTableBuilder.newInstance(database, tableName);
		buildProcess.accept(builder);
		String sql = builder.buildSql();
		
		jdbcProxy.query(sql).execute();
	}
	
	/**
	 * テーブルを削除する（無くてもエラーにならない）
	 * @param jdbcProxy
	 * @param tableName
	 */
	public static void dropTable(JdbcProxy jdbcProxy, String tableName) {
		
		String sql = "drop table if exists " + tableName;

		jdbcProxy.query(sql).execute();
	}
}
