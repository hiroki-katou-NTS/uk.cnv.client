package nts.uk.screen.at.app.kdw013.a;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.bonuspay.primitives.BonusPaySettingCode;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.affiliationinfor.AffiliationInforOfDailyAttd;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.affiliationinfor.ClassificationCode;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.EmploymentCode;
import nts.uk.ctx.at.shared.dom.workrule.businesstype.BusinessTypeCode;

@AllArgsConstructor
@Getter
public class AffiliationInforOfDailyAttdCommand {
	// 雇用コード
	private String employmentCode;

	// 職位ID
	private String jobTitleID;

	// 職場ID
	private String wplID;

	// 分類コード
	private String clsCode;

	// 勤務種別コード
	private String businessTypeCode;

	// 加給コード : optional
	private String bonusPaySettingCode;

	public AffiliationInforOfDailyAttd toDomain() {

		return new AffiliationInforOfDailyAttd(
				new EmploymentCode(this.getEmploymentCode()), 
				this.getJobTitleID(), 
				this.getWplID(), 
				new ClassificationCode(this.getClsCode()),
				Optional.ofNullable(new BusinessTypeCode(this.getBusinessTypeCode())),
				Optional.ofNullable(new BonusPaySettingCode(this.getBonusPaySettingCode()))
				);
	}
}
