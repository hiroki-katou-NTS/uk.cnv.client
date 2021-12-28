package nts.uk.ctx.exio.dom.input.workspace.datatype;

import java.math.BigDecimal;

import lombok.RequiredArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.infra.data.jdbc.NtsStatement;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.exio.dom.input.importableitem.ItemType;

/**
 * データ型
 */
@RequiredArgsConstructor
public enum DataType {
	STRING		(0, (s, name, value) -> s.paramString(name, (String) value)),
	INT			(1, (s, name, value) -> s.paramLong(name, (Long) value)),
	REAL		(2, (s, name, value) -> s.paramDecimal(name, (BigDecimal) value)),
	DATE		(3, (s, name, value) -> s.paramDate(name, (GeneralDate) value)),
	DATETIME	(4, (s, name, value) -> s.paramDateTime(name, (GeneralDateTime) value)),
	AUTONUMBER	(5, (s, name, value) -> s.paramLong(name, (Long) value)),
	BOOLEAN		(6, (s, name, value) -> s.paramBoolean(name, (Boolean) value)),
	;

	public final int value;
	
	private final SetParam setParam;
	
	public static DataType valueOf(int value) {
		return EnumAdaptor.valueOf(value, DataType.class);
	}
	
	/**
	 * ItemTypeからの変換
	 * @param itemType
	 * @return
	 */
	public static DataType of(ItemType itemType) {
		
		// DATETIME,BOOLEANはItemTypeには無い
		
		switch (itemType) {
		case STRING:
			return STRING;
		case INT:
		case TIME_DURATION:
		case TIME_POINT:
			return INT;
		case REAL:
			return REAL;
		case DATE:
			return DATE;
		default:
			throw new RuntimeException("unknown: " + itemType);
		}
	}
	
	public void setParam(NtsStatement statement, String paramName, Object paramValue) {
		this.setParam.apply(statement, paramName, paramValue);
	}
	
	private static interface SetParam {
		void apply(NtsStatement statement, String paramName, Object paramValue);
	}
}
