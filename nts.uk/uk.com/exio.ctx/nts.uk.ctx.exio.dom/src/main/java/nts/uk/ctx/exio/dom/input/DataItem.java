package nts.uk.ctx.exio.dom.input;

import java.math.BigDecimal;

import lombok.Value;
import nts.arc.time.GeneralDate;

/**
 * 1項目分のデータ
 */
@Value
public class DataItem {

	/** 受入項目NO */
	int itemNo;
	
	/** 値 */
	Object value;
	
	/**
	 * null
	 * @param itemNo 
	 * @return
	 */
	public static DataItem of(int itemNo) {
		return new DataItem(itemNo, null);
	}
	
	/**
	 * 文字型
	 * @param itemNo
	 * @param value
	 * @return
	 */
	public static DataItem of(int itemNo, String value) {
		return new DataItem(itemNo, value);
	}
	
	/**
	 * 整数型
	 * @param itemNo
	 * @param value
	 * @return
	 */
	public static DataItem of(int itemNo, long value) {
		return new DataItem(itemNo, value);
	}
	
	/**
	 * 実数型
	 * @param itemNo
	 * @param value
	 * @return
	 */
	public static DataItem of(int itemNo, BigDecimal value) {
		return new DataItem(itemNo, value);
	}
	
	/**
	 * 日付型
	 * @param itemNo
	 * @param value
	 * @return
	 */
	public static DataItem of(int itemNo, GeneralDate value) {
		return new DataItem(itemNo, value);
	}

	/**
	 * bool型
	 * @param itemNo
	 * @param value
	 * @return
	 */
	public static DataItem of(int itemNo, boolean value) {
		return new DataItem(itemNo, value);
	}
	
	public String getString() {
		return value != null ? (String) value : null;
	}
	
	public Long getInt() {
		return value != null ? (long) value : null;
	}
	
	public BigDecimal getReal() {
		return value != null ? (BigDecimal) value : null;
	}
	
	public GeneralDate getDate() {
		return value != null ? (GeneralDate) value : null;
	}
	
	public boolean isNull() {
		return value == null;
	}

	public boolean isEmpty() {
		return isNull() || value.toString().isEmpty();
		
	}
}
