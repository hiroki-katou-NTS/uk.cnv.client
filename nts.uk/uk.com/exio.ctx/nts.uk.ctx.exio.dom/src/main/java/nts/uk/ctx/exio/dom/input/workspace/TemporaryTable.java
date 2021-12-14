package nts.uk.ctx.exio.dom.input.workspace;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.val;
import nts.arc.layer.infra.data.database.DatabaseProduct;
import nts.arc.layer.infra.data.jdbc.JdbcProxy;
import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.workspace.datatype.DataTypeConfiguration;

/**
 * 外部受入の一時テーブルを扱う
 */
public class TemporaryTable {
	
	private static final String PREFIX = "外部受入_";
	
	public static String createTableName(ExecutionContext context, String name) {
		return PREFIX + name + "_" + context.getCompanyId().replace("-", "");
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
		
		val builder = new CreateTableBuilder(tableName);
		buildProcess.accept(builder);
		String sql = builder.buildSql(database);
		
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
	
	@RequiredArgsConstructor
	public static class CreateTableBuilder {
		
		private final String tableName;
		
		private final List<Column> columns = new ArrayList<>();
		
		private final List<String> pkColumnNames = new ArrayList<>();
		
		public CreateTableBuilder columnPK(String name, DataTypeConfiguration type) {
			columns.add(new Column(name, false, type));
			pkColumnNames.add(name);
			return this;
		}
		
		public CreateTableBuilder column(String name, DataTypeConfiguration type) {
			columns.add(new Column(name, true, type));
			return this;
		}
		
		public String buildSql(DatabaseProduct database) {
			
			val sql = new StringBuilder();
			
			sql.append("create table ").append(tableName).append(" (");
			
			for (val column : columns) {
				column.sql(database, sql);
				sql.append(",");
			}

			primaryKey(database, sql);
			
			sql.append(");");
			
			return sql.toString();
		}
		
		
		private void primaryKey(DatabaseProduct database, StringBuilder sql) {

			String pkName = "PK_" + tableName;
			
			String keys = pkColumnNames.stream()
					.collect(Collectors.joining(", "));
			
			sql.append("constraint ")
				.append(pkName)
				.append(" primary key nonclustered (")
				.append(keys)
				.append(")");
		}
		
		/**
		 * 主キーなら非NULL、そうでなければNULL可とする
		 */
		@Value
		private static class Column {
			
			String name;
			boolean isNullable;
			DataTypeConfiguration type;
			
			void sql(DatabaseProduct database, StringBuilder sql) {

				sql.append(name).append(" ");
				
				dataType(database, sql);

				if (isNullable) {
					sql.append(" null ");
				} else {
					sql.append(" not null ");
				}
			}

			void dataType(DatabaseProduct database, StringBuilder sql) {
				
				switch (type.getType()) {
				case DATE:
				case DATETIME:
					sql.append("datetime2");
					break;
				case STRING:
					sql.append("nvarchar(").append(type.getLength()).append(")");
					break;
				case INT:
					sql.append("decimal(").append(type.getLength()).append(")");
					break;
				case REAL:
					sql.append("decimal(").append(type.getLength()).append(",").append(type.getScale()).append(")");
					break;
				case AUTONUMBER:
					sql.append("int IDENTITY(1,1)");
					break;
				default:
					throw new RuntimeException("error: " + type);
				}
			}
		}
	}
}
