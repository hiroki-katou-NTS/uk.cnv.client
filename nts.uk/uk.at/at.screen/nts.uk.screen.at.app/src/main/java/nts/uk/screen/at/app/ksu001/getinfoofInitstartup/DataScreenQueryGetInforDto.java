package nts.uk.screen.at.app.ksu001.getinfoofInitstartup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.DisplayInfoOrganization;

/**
 * @author laitv
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DataScreenQueryGetInforDto {
	public GeneralDate startDate; // ・期間 A3_1_2
	public GeneralDate endDate;   // ・期間 A3_1_4
	public TargetOrgIdenInforDto targetOrgIdenInfor; // 対象組織識別情報
	public DisplayInfoOrganization displayInforOrganization; // 組織の表示情報
	public GeneralDate scheduleModifyStartDate;  // 修正可能開始日
	public Boolean usePublicAtr; // 公開を利用するか
	public Boolean useWorkAvailabilityAtr; // 勤務希望を利用するか
}
