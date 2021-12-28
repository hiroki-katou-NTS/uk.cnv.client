package nts.uk.ctx.at.record.dom.jobmanagement.displayformat;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.arc.layer.dom.AggregateRoot;

/**
 * AR:工数入力表示フォーマット
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.工数入力表示フォーマット.工数入力表示フォーマット
 * @author tutt
 *
 */
@Getter
@AllArgsConstructor
public class ManHrInputDisplayFormat extends AggregateRoot{
	
	/** 実績欄表示項目一覧*/
	private List<RecordColumnDisplayItem> recordColumnDisplayItems;
	
	/** 実績入力ダイアログ表示項目一覧 */
	private List<DisplayAttItem> displayAttItems;
	
	/** 作業内容入力ダイアログ表示項目一覧*/
	private List<DisplayManHrRecordItem> displayManHrRecordItems;

}
