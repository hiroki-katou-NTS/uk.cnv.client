package nts.uk.ctx.at.record.app.find.monthly.nursingandchildnursing.acquirenursingandchildnursing;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.ctx.at.shared.dom.remainingnumber.nursingcareleavemanagement.info.NursingCareLeaveRemainingInfo;
import nts.uk.ctx.at.shared.dom.vacation.setting.nursingleave.NursingLeaveSetting;

/**
 * 
 * @author tutk
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AcquireNursingAndChildNursingDto {
	
	/**
	 * 介護看護休暇設定
	 */
	private Optional<NursingLeaveSetting> nursingLeaveSetting;
	
	/**
	 * 管理区分
	 */
	private boolean managementSection;
	
	/**
	 * 子の看護・介護休暇基本情報
	 */
	private Optional<NursingCareLeaveRemainingInfo> nursingCareLeaveRemainingInfo;
}
