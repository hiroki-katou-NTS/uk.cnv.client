package nts.uk.screen.at.app.query.kbt002.b.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import nts.uk.ctx.at.function.app.find.alarm.AlarmPatternSettingDto;
import nts.uk.ctx.at.function.app.find.indexreconstruction.IndexReorgCateDto;
import nts.uk.ctx.at.record.app.find.resultsperiod.optionalaggregationperiod.OptionalAggrPeriodDto;
import nts.uk.query.app.exi.condset.dto.StdAcceptCondSetDto;
import nts.uk.query.app.exo.condset.dto.StdOutputCondSetDto;

/**
 * The class Master info dto.
 *
 * @author nws-minhnb
 */
@Data
@AllArgsConstructor
public class MasterInfoDto {

	/** 任意集計期間 */
	private List<OptionalAggrPeriodDto> aggrPeriodList;

	/** アラームリストパターン設定 */
	private List<AlarmPatternSettingDto> alarmPatternSettingList;

	/** 出力条件設定（定型） */
	private List<StdOutputCondSetDto> stdOutputCondSetList;

	/** 受入条件設定（定型） */
	private List<StdAcceptCondSetDto> stdAcceptCondSetList;

	/** データ保存のパターン設定 */
	private List<?> dataStoragePatternSetList;

	/** データ削除のパターン設定 */
	private List<?> dataDelPatternSetList;

	/** インデックス再構成カテゴリ */
	private List<IndexReorgCateDto> indexReorgCateList;

}
