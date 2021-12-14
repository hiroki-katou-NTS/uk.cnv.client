package nts.uk.file.at.app.export.dailyschedule.totalsum;

import java.text.DecimalFormat;

import org.apache.commons.lang3.math.NumberUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.gul.text.StringUtil;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ValueType;

/**
 * 合算値
 * @author HoangNDH
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TotalValue {
	
	/** The Constant STRING. */
	public static final int STRING = 4;
	
	// 勤怠項目ID
	private int attendanceId;
	// 値
	private String value;
	
	private int valueType;
	
	private String unit = "";
	
	/**
	 * Value.
	 *
	 * @param <T> the generic type
	 * @return the t
	 */
	@SuppressWarnings("unchecked")
	public <T> T value() {
		if(StringUtil.isNullOrEmpty(value, false)){
			return null;
		}
		ValueType valueType = EnumAdaptor.valueOf(this.valueType, ValueType.class);
		if (valueType == ValueType.ATTR)
			return (T) this.value;
		if (valueType.isInteger()) {
			return (T) new Integer(this.value);
		}
		if (valueType.isBoolean()) {
			return (T) new Boolean(this.value);
		}
		if (valueType.isDate()) {
			return (T) GeneralDate.fromString(this.value, "yyyyMMdd");
		}
		if (valueType.isDouble()) {
			return (T) new Double(this.value);
		}
		if (valueType.isString()) {
			return (T) this.value;
		}
		throw new RuntimeException("invalid type: " + this.valueType);
	}
	
	public <T extends Number> void addValue(T value, ValueType valueType) {
		if (value == null) {
			return;
		}
		if (valueType.isInteger()) {
			this.value = String.valueOf(NumberUtils.toInt(this.value, 0) + value.intValue());
		} else if (valueType.isDouble()) {
			this.value = String.valueOf(NumberUtils.toDouble(this.value, 0) + value.doubleValue());
		}
	}
	
	public String formatValue() {
		String result = null;
		if (StringUtil.isNullOrEmpty(this.value, true)) {
			return this.value;
		}
		switch(EnumAdaptor.valueOf(this.valueType, ValueType.class)) {
		case AMOUNT:
			DecimalFormat formatDouble = new DecimalFormat("###,###,###.#");
			result = formatDouble.format(Double.valueOf(this.value));
			break;
		case AMOUNT_NUM:
			DecimalFormat formatInt = new DecimalFormat("###,###,###");
			result = formatInt.format(Integer.valueOf(this.value));
			break;
		default:
			result = this.value;
		}
		return result.concat(this.unit);
	}
}
