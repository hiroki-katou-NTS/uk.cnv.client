package nts.uk.ctx.exio.dom.input.errors;

import lombok.Value;

/**
 * 単一レコードのエラー
 */
@Value
public class RecordError {

	int csvRowNo;
	String message;
}
