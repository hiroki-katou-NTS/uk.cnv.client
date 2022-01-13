package nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.rsvleamanager.rsvimport;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RsvLeaCriterialDate {
	/** 積立年休残数 */
	private RsvLeaveInfoImport reserveLeaveInfo;
	/** 積立年休付与残数データ */
	//積立年休付与情報
	private List<RsvLeaGrantRemainingImport> grantRemainingList;
	/** 暫定積立年休管理データ */
	//積立年休管理情報
	private List<TmpRsvLeaveMngImport> tmpManageList;
	/** 積立年休残日数 */
	private Double remainingDays;
}
