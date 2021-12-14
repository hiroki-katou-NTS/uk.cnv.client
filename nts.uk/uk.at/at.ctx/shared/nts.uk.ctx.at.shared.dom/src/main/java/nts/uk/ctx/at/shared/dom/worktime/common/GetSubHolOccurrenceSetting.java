package nts.uk.ctx.at.shared.dom.worktime.common;

import java.util.Optional;

import lombok.val;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.CompensatoryLeaveComSetting;
import nts.uk.ctx.at.shared.dom.worktime.WorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;

/**
 * @author thanh_nx
 *
 *         代休設定を取得する
 */
public class GetSubHolOccurrenceSetting {

	public static Optional<SubHolTransferSet> process(Require require, String cid, Optional<String> workTimeCode,
			CompensatoryOccurrenceDivision originAtr) {
		
		//	$代休管理設定
		val comLeavSet = require.findCompensatoryLeaveComSet(cid);
		//	if($代休管理設定.is not Present())
		if (comLeavSet == null)
			return Optional.empty();
		if (originAtr.equals(CompensatoryOccurrenceDivision.FromOverTime) && !comLeavSet.isManagedTime()) {
			return Optional.empty();
		}

		Optional<WorkTimezoneCommonSet> commonset = Optional.empty();
		if (workTimeCode.isPresent()) {
			Optional<WorkTimeSetting> workTimeSet = require.getWorkTime(cid, workTimeCode.get());
			commonset = workTimeSet.map(x -> {
				WorkSetting workSetting = x.getWorkSetting(require);
				if (workSetting instanceof FlowWorkSetting) {
					return ((FlowWorkSetting) workSetting).getCommonSetting();
				}
				
				if (workSetting instanceof FixedWorkSetting) {
					return ((FixedWorkSetting) workSetting).getCommonSetting();
				}
				
				return ((FlexWorkSetting)workSetting).getCommonSetting();
			});
		}

		if (commonset.isPresent() && commonset.get().getSubHolTimeSet().stream()
				.filter(x -> x.getOriginAtr() == originAtr).findFirst().map(x -> x.getSubHolTimeSet().isUseDivision())
				.orElse(false)) {
			return commonset.get().getSubHolTimeSet().stream()
					.filter(x -> x.getOriginAtr() == originAtr).map(x -> x.getSubHolTimeSet()).findFirst();
		}
		
		SubHolTransferSet result = comLeavSet.getCompensatoryOccurrenceSetting().stream()
				.filter(x -> x.getOccurrenceType().value == originAtr.value).map(x -> x.getTransferSetting())
				.findFirst().orElse(null);
		return (result != null && result.isUseDivision()) ? Optional.of(result) : Optional.empty();
	}

	public static interface Require extends WorkTimeSetting.Require{

		//WorkTimeSettingRepository.findByCode
		public Optional<WorkTimeSetting> getWorkTime(String cid, String workTimeCode);
		
		// CompensLeaveComSetRepository
		CompensatoryLeaveComSetting findCompensatoryLeaveComSet(String companyId);
	}
}
