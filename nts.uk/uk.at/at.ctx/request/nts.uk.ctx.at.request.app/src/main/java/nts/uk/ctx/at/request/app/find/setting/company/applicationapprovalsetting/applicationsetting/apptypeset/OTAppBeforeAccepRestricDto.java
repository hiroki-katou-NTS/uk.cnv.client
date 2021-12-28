package nts.uk.ctx.at.request.app.find.setting.company.applicationapprovalsetting.applicationsetting.apptypeset;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.BeforeAddCheckMethod;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.AppAcceptLimitDay;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting.OTAppBeforeAccepRestric;
import nts.uk.shr.com.time.AttendanceClock;

/**
 * refactor 4
 * @author Doan Duy Hung
 *
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class OTAppBeforeAccepRestricDto {
	/**
	 * チェック方法
	 */
	private int methodCheck;
	
	/**
	 * 日数
	 */
	private int dateBeforehandRestrictions;
	
	/**
	 * 利用する
	 */
	private boolean toUse;
	
	/**
	 * 時刻（早出残業）
	 */
	private Integer opEarlyOvertime;
	
	/**
	 * 時刻（通常残業）
	 */
	private Integer opNormalOvertime;
	
	/**
	 * 時刻（早出残業・通常残業）
	 */
	private Integer opEarlyNormalOvertime;

	/**
	 * 時刻（複数回残業）
	 */
	private Integer opMultipleOvertime;
	
	public static OTAppBeforeAccepRestricDto fromDomain(OTAppBeforeAccepRestric otAppBeforeAccepRestric) {
		return new OTAppBeforeAccepRestricDto(
				otAppBeforeAccepRestric.getMethodCheck().value, 
				otAppBeforeAccepRestric.getDateBeforehandRestrictions().value, 
				otAppBeforeAccepRestric.isToUse(), 
				otAppBeforeAccepRestric.getOpEarlyOvertime().map(x -> x.v()).orElse(null), 
				otAppBeforeAccepRestric.getOpNormalOvertime().map(x -> x.v()).orElse(null), 
				otAppBeforeAccepRestric.getOpEarlyNormalOvertime().map(x -> x.v()).orElse(null),
				otAppBeforeAccepRestric.getOpMultipleOvertime().map(x -> x.v()).orElse(null)
		);
	}
	
	public OTAppBeforeAccepRestric toDomain() {
		return new OTAppBeforeAccepRestric(
				EnumAdaptor.valueOf(methodCheck, BeforeAddCheckMethod.class), 
				EnumAdaptor.valueOf(dateBeforehandRestrictions, AppAcceptLimitDay.class), 
				toUse, 
				opEarlyOvertime == null ? Optional.empty() : Optional.of(new AttendanceClock(opEarlyOvertime)), 
				opNormalOvertime == null ? Optional.empty() : Optional.of(new AttendanceClock(opNormalOvertime)), 
				opEarlyNormalOvertime == null ? Optional.empty() : Optional.of(new AttendanceClock(opEarlyNormalOvertime)),
				opMultipleOvertime == null ? Optional.empty() : Optional.of(new AttendanceClock(opMultipleOvertime))
		);
	}
}
