package nts.uk.ctx.exio.infra.repository.input.canonicalize.existing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import nts.arc.layer.infra.data.jdbc.JdbcProxy;
import nts.gul.collection.CollectionUtil;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.AnyRecordToChange;
import nts.uk.ctx.exio.dom.input.canonicalize.existing.StringifiedValue;
import nts.uk.ctx.exio.dom.input.workspace.TemporaryTable;

/**
 * AnyRecordToChange用一時テーブルのレイアウト
 */
@RequiredArgsConstructor
public class LayoutAnyRecordToChange {
	
	private final JdbcProxy jdbcProxy;
	private final ExecutionContext context;
	
	private static final String TABLE_NAME = "ANY_RECORD_TO_CHANGE";
	private static final int ATR_PRIMARY_KEYS = 1;
	private static final int ATR_CHANGES = 2;
	
	private String tableName() {
		return TemporaryTable.createTableName(context, TABLE_NAME);
	}
	
	public void createTable() {
		
		TemporaryTable.dropTable(jdbcProxy, tableName());
		
		String sql = "create table " + tableName() + "("
				+ " ID char(36) not null,"
				+ " ITEM_NO decimal(5) not null,"
				+ " ATR decimal(1) not null,"
				+ " VALUE varchar(1000) null"
				+ ");";
		
		jdbcProxy.query(sql).execute();
	}
	
	public void insert(AnyRecordToChange record) {
		
		String id = IdentifierUtil.randomUniqueId();

		insert(tableName(), id, record.getPrimaryKeys(), ATR_PRIMARY_KEYS);
		insert(tableName(), id, record.getChanges(), ATR_CHANGES);
	}
	
	private void insert(String tableName, String id, Map<Integer, StringifiedValue> map, int atr) {
		
		map.forEach((itemNo, value) -> {
			String sql = "insert into " + tableName + " values ("
					+ "'" + id + "'"
					+ ", " + itemNo
					+ ", " + atr
					+ ", '" + value.asString() + "'"
					+ ")";
			
			jdbcProxy.query(sql).execute();
		});
	}
	
	public List<AnyRecordToChange> findAll() {
		
		String sql 	= " select *"
					+ " from " + tableName();
		
		List<Entity> allEntitys = this.jdbcProxy.query(sql)
				.getList(rec -> {
					return new Entity(
						rec.getString("ID"), 
						rec.getInt("ITEM_NO"), 
						rec.getInt("ATR"), 
						rec.getString("VALUE"));
				});
		
		return assembleChanges(allEntitys);
	}
	
	public List<AnyRecordToChange> findAllWhere(int keyItemNo, String keyValue){
		
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
					.getList(rec -> {
						return new Entity(
								rec.getString("ID"), 
								rec.getInt("ITEM_NO"), 
								rec.getInt("ATR"), 
								rec.getString("VALUE"));
					}));
		});
		
		return assembleChanges(allEntitys);
	}
	
	private List<AnyRecordToChange> assembleChanges(List<Entity> allEntitys){
		val entitysMap = allEntitys
					.stream()
					.collect(Collectors.groupingBy(Entity::getId));
		val result = new ArrayList<AnyRecordToChange>();
		for(List<Entity> entitys : entitysMap.values()) {
			result.add(toDomain(entitys));
		}
		return result;
	}
	
	
	private AnyRecordToChange toDomain(List<Entity> entitys){
		val result = AnyRecordToChange.create(context);
		
		entitys.forEach(entity -> {
			if(entity.atr == 1) {
				result.addKey(entity.itemNo, StringifiedValue.of(entity.value));
			}else {
				result.addChange(entity.itemNo, StringifiedValue.of(entity.value));
			}
		});
		return result;
	}
	
	@AllArgsConstructor
	@Getter
	private class Entity{
		private final String id;
		private final int itemNo;
		private final int atr;
		private final String value;
	}
}
