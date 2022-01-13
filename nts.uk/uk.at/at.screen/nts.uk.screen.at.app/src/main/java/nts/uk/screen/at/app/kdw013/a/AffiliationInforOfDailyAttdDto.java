package nts.uk.screen.at.app.kdw013.a;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.affiliationinfor.AffiliationInforOfDailyAttd;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AffiliationInforOfDailyAttdDto {
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

	public static AffiliationInforOfDailyAttdDto fromDomain(AffiliationInforOfDailyAttd domain) {
		return new AffiliationInforOfDailyAttdDto(domain.getEmploymentCode().v(), 
				domain.getJobTitleID(),
				domain.getWplID(),
				domain.getClsCode().v(),
				domain.getBusinessTypeCode().map(x-> x.v()).orElse(null),
				domain.getBonusPaySettingCode().map(x -> x.v()).orElse(null));
	}
}
