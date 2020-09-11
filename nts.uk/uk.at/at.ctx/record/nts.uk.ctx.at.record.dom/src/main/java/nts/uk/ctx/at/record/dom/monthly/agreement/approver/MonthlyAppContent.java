package nts.uk.ctx.at.record.dom.monthly.agreement.approver;

import lombok.Getter;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.record.dom.monthly.agreement.monthlyresult.SpecialProvisionsOfAgreement;
import nts.uk.ctx.at.shared.dom.monthlyattdcal.agreementresult.AgreementOneMonthTime;
import org.eclipse.persistence.internal.xr.ValueObject;

/**
 * 月間の申請内容
 * @author khai.dh
 */
@Getter
public class MonthlyAppContent extends ValueObject {
	// 対象者
	private String applicant;

	// 年月度
	private YearMonth ym;

	// エラー時間
	private AgreementOneMonthTime errTime;

	// アラーム時間
	private AgreementOneMonthTime alarmTime;

	// 申請理由 : 36協定申請理由 TODO ???
	// private AppReason36Agr reason;
	private SpecialProvisionsOfAgreement reason;
}
