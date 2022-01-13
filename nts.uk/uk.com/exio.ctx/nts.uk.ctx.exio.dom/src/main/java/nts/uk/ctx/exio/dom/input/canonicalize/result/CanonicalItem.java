package nts.uk.ctx.exio.dom.input.canonicalize.result;

import java.math.BigDecimal;

import lombok.Value;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.exio.dom.input.DataItem;

/**
 * 正準化済みの項目
 */
@Value
public class CanonicalItem {

	/** 受入項目NO */
	int itemNo;
	
	/** 値 */
	Object value;
	
	public static CanonicalItem of(DataItem dataItem) {
		return new CanonicalItem(dataItem.getItemNo(), dataItem.getValue());
	}
	
	public static CanonicalItem nullValue(int itemNo) {
		return new CanonicalItem(itemNo, null);
	}

	/**
	 * 文字型
	 * @param itemNo
	 * @param value
	 * @return
	 */
	public static CanonicalItem of(int itemNo, String value) {
		return new CanonicalItem(itemNo, value);
	}
	
	/**
	 * 整数型
	 * @param itemNo
	 * @param value
	 * @return
	 */
	public static CanonicalItem of(int itemNo, long value) {
		return new CanonicalItem(itemNo, value);
	}
	
	/**
	 * 実数型
	 * @param itemNo
	 * @param value
	 * @return
	 */
	public static CanonicalItem of(int itemNo, BigDecimal value) {
		return new CanonicalItem(itemNo, value);
	}
	
	/**
	 * 日付型
	 * @param itemNo
	 * @param value
	 * @return
	 */
	public static CanonicalItem of(int itemNo, GeneralDate value) {
		return new CanonicalItem(itemNo, value);
	}

	/**
	 * 日時型
	 * @param itemNo
	 * @param value
	 * @return
	 */
	public static CanonicalItem of(int itemNo, GeneralDateTime value) {
		return new CanonicalItem(itemNo, value);
	}

	/**
	 * bool型
	 * @return
	 */
	public static CanonicalItem of(int itemNo, boolean value){
		return new CanonicalItem(itemNo, value);
	}
	

	public String getString() {
		return value != null ? (String) value : null;
	}
	
	public Long getInt() {
		return value != null ? (long) value : null;
	}
	
	public Integer getJavaInt() {
		return value != null ? (int) (long) value : null;
	}
	
	public BigDecimal getReal() {
		return value != null ? (BigDecimal) value : null;
	}
	
	public GeneralDate getDate() {
		return value != null ? (GeneralDate) value : null;
	}
	
	public GeneralDateTime getDateTime() {
		return value != null ? (GeneralDateTime) value : null;
	}

	public Boolean getBoolean(){ return value != null ? (boolean) value : null;}
	
	public boolean isNull() {
		return value == null;
	}

	public boolean isEmpty() {
		return isNull() || getString().isEmpty();
	}
}
