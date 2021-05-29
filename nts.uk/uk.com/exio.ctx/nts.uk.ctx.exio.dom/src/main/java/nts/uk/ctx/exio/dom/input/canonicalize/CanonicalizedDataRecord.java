package nts.uk.ctx.exio.dom.input.canonicalize;

import lombok.Value;
import nts.uk.ctx.exio.dom.input.DataItemList;
import nts.uk.ctx.exio.dom.input.ExecutionContext;

/**
 * 正準化データ1行分
 */
@Value
public class CanonicalizedDataRecord {

	ExecutionContext executionContext;

	/** 正準化したデータ */
	DataItemList itemsAfterCanonicalize;
	
	/** 正準化対象の正準化前データ */
	DataItemList itemsBeforeCanonicalize;
	
	/** 正準化対象ではないデータ */
	DataItemList itemsNotCanonicalize;
	
}
