package nts.uk.ctx.exio.infra.repository.input.canonicalize.existing;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import nts.arc.layer.infra.data.jdbc.JdbcProxy;
import nts.arc.layer.infra.data.jdbc.NtsResultSet;
import nts.gul.collection.CollectionUtil;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.AnyRecordToDelete;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.StringifiedValue;
import nts.uk.ctx.exio.dom.input.workspace.TemporaryTable;

/**
 * AnyRecordToDelete用一時テーブルのレイアウト
 */
@RequiredArgsConstructor
public class LayoutAnyRecordToDelete {
	
	private final JdbcProxy jdbcProxy;
	private final ExecutionContext context;
	
	private static final String TABLE_NAME = "ANY_RECORD_TO_DELETE";
	
	private String tableName() {
		return TemporaryTable.createTableName(context, TABLE_NAME);
	}

	public void dropTable() {
		TemporaryTable.dropTable(jdbcProxy, tableName());
	}
	
	public void createTable() {
		
		String tableName = tableName();
		jdbcProxy.query(Entity.sqlCreateTable(tableName)).execute();
	}
	
	public void insert(AnyRecordToDelete record) {
		
		Entity.sqlsInsert(record, tableName()).forEach(sql -> {
			jdbcProxy.query(sql).execute();
		});
	}
	
	public List<AnyRecordToDelete> findAll() {
		
		String sql 	= " select *"
					+ " from " + tableName();
		
		List<Entity> allEntitys = this.jdbcProxy.query(sql)
				.getList(Entity::of);
		
		return assembleDeletes(allEntitys);
	}
	
	public List<AnyRecordToDelete> findAllWhere(int keyItemNo, String keyValue){
		
		String sqlByKey = " select *"
					+ " from " + tableName()
					+ " where ITEM_NO = @itemNO"
					+ " and VALUE = @value";
		
		List<String> ids = this.jdbcProxy.query(sqlByKey)
				.paramInt("itemNO", keyItemNo)
				.paramString("value", keyValue)
				.getList(rec -> {
					return rec.getString("ID");
				});
		
		String sqlByID = " select *"
				+ " from " + tableName()
				+ " where ID in @ids";
		
		List<Entity> allEntitys = new ArrayList<>();
		CollectionUtil.split(ids, 1000, subIdList -> {
			allEntitys.addAll(
				this.jdbcProxy.query(sqlByID)
					.paramString("ids", subIdList)
					.getList(Entity::of));
		});
		
		return assembleDeletes(allEntitys);
	}
	
	private List<AnyRecordToDelete> assembleDeletes(List<Entity> allEntitys){
		val entitysMap = allEntitys
					.stream()
					.collect(Collectors.groupingBy(Entity::getId));
		val result = new ArrayList<AnyRecordToDelete>();
		for(List<Entity> entitys : entitysMap.values()) {
			result.add(Entity.toDomain(context, entitys));
		}
		return result;
	}
	
	@AllArgsConstructor
	@Getter
	private static class Entity{
		private final String id;
		private final String targetName;
		private final int itemNo;
		private final String value;
		
		static Entity of(NtsResultSet.NtsResultRecord rec) {
			return new Entity(
					rec.getString("ID"), 
					rec.getString("TARGET_NAME"), 
					rec.getInt("ITEM_NO"), 
					rec.getString("VALUE"));
		}
		
		static String sqlCreateTable(String tableName) {
			return "create table " + tableName + "("
					+ " ID char(36) not null,"
					+ " TARGET_NAME varchar(100) null,"
					+ " ITEM_NO decimal(5) not null,"
					+ " VALUE varchar(1000) null"
					+ ");";
		}
		
		String sqlInsert(String tableName) {
			return "insert into " + tableName + " values ("
					+ "'" + id + "'"
					+ ", '" + targetName + "'"
					+ ", " + itemNo
					+ ", '" + value + "'"
					+ ")";
		}
		
		static List<String> sqlsInsert(AnyRecordToDelete record, String tableName) {
			
			String id = IdentifierUtil.randomUniqueId();
			String targetName = record.getTargetName() != null ? record.getTargetName() : "";
			
			return record.getPrimaryKeys().stream()
					.map(key -> new Entity(id, targetName, key.getItemNo(), key.getValue().asString())
						.sqlInsert(tableName))
					.collect(toList());
		}
		
		static AnyRecordToDelete toDomain(ExecutionContext context, List<Entity> entities) {
			
			String targetName = entities.get(0).targetName;
			val result = AnyRecordToDelete.create(context, targetName);
			
			entities.forEach(entity -> {
				result.addKey(entity.itemNo, StringifiedValue.of(entity.value));
			});
			
			return result;
		}
	}
}
