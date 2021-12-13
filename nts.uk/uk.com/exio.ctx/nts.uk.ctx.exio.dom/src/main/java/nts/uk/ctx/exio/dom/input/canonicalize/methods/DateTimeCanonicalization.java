package nts.uk.ctx.exio.dom.input.canonicalize.methods;

import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.val;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.exio.dom.input.canonicalize.result.CanonicalItem;
import nts.uk.ctx.exio.dom.input.canonicalize.result.IntermediateResult;
import nts.uk.ctx.exio.dom.input.setting.assembly.RevisedDataRecord;
import nts.uk.ctx.exio.dom.input.workspace.domain.DomainWorkspace;

/**
 * 年月日と時刻を日時に正準化する
 * CSVの1項目で日時を表現しようとすると仕様が複雑化するので、日時項目は年月日と時刻に分けて受け入れる想定
 */
@Value
@AllArgsConstructor
public class DateTimeCanonicalization {

	/** 年月日の項目No */
	final int itemNoDate;
	
	/** 時刻の項目No */
	final int itemNoTime;
	
	/** 日時の項目No */
	final int itemNoDateTime;
	
	public DateTimeCanonicalization(DomainWorkspace workspace) {
		itemNoDate = workspace.getItemByName("年月日").getItemNo();
		itemNoTime = workspace.getItemByName("時分").getItemNo();
		itemNoDateTime = workspace.getItemByName("日時").getItemNo();
	}
	
	/**
	 * 渡された編集済みデータを正準化する
	 * @param require
	 * @param revisedData
	 * @return
	 */
	public IntermediateResult canonicalize(
			CanonicalizationMethodRequire require,
			RevisedDataRecord revisedData) {
		
		val date = revisedData.getItemByNo(itemNoDate).get().getDate();
		int time = (int) (long) revisedData.getItemByNo(itemNoTime).get().getInt();
		
		int hour = time / 60;
		int minute = time % 60;
		
		val datetime = GeneralDateTime.ymdhms(date.year(), date.month(), date.day(), hour, minute, 0);
		val item = CanonicalItem.of(itemNoDateTime, datetime);
		
		return IntermediateResult.create(revisedData).addCanonicalized(item);
	}
}
