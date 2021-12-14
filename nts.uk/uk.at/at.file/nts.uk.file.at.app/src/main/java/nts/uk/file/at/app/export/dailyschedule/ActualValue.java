package nts.uk.file.at.app.export.dailyschedule;

import java.text.DecimalFormat;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;
import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.gul.text.StringUtil;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ValueType;

/**
 * 実績値.
 *
 * @author HoangNDH
 */
@Getter
@Setter
public class ActualValue {
	/** The Constant STRING. */
	public static final int STRING = 4;
	
	// 勤怠項目ID
	private int attendanceId;
	
	// 値
	private String value;
	
	/** The value type. */
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
		if(value == null){
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

	public ActualValue() {
		super();
	}

	public ActualValue(int attendanceId, String value, int valueType) {
		super();
		this.attendanceId = attendanceId;
		this.value = value;
		this.valueType = valueType;
		this.setUnit(EnumAdaptor.valueOf(valueType, ValueType.class));
	}
	
	public void setUnit(ValueType valueType) {
		switch (valueType) {
		case AMOUNT:
		case AMOUNT_NUM:
			this.unit = "円"; break;
		case COUNT:
		case COUNT_WITH_DECIMAL:
			this.unit = "回"; break;
		case DAYS:
			this.unit = "日"; break;
		default: break;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object val) {
		if (val instanceof ActualValue) {
			ActualValue actualValue = (ActualValue) val;
			return this.attendanceId == actualValue.attendanceId && StringUtils.equals(this.value, actualValue.value) && this.valueType == actualValue.valueType;
		}
		return false;
	}
	
}
