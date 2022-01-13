package nts.uk.ctx.exio.dom.input.errors;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.Value;

/**
 * 単一レコードのエラー
 */
@Getter
@EqualsAndHashCode
@ToString
public class RecordError {

	int csvRowNo;
	Integer itemNo;
	String message;

	public RecordError(int csvRowNo, String message) {
		this.csvRowNo = csvRowNo;
		this.itemNo = null;
		this.message = message;
	}
	public RecordError(int csvRowNo, int itemNo, String message) {
		this.csvRowNo = csvRowNo;
		this.itemNo = itemNo;
		this.message = message;
	}

	public static RecordError record(int rowNo, String message) {
		return new RecordError(rowNo, message);
	}

	public static RecordError item(int rowNo, ItemError error) {
		return new RecordError(rowNo, error.getItemNo(), error.getMessage());
	}
}
