package nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.applicationtypesetting;

import java.util.Optional;

import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.applicationsetting.BeforeAddCheckMethod;
import nts.uk.shr.com.time.AttendanceClock;

/**
 * refactor 4
 * UKDesign.ドメインモデル."NittsuSystem.UniversalK".就業.contexts.申請承認.設定.会社別.申請承認設定.申請設定.申請種類別設定.残業申請事前の受付制限
 * @author Doan Duy Hung
 *
 */
@Getter
public class OTAppBeforeAccepRestric {
	
	/**
	 * チェック方法
	 */
	private BeforeAddCheckMethod methodCheck;
	
	/**
	 * 日数
	 */
	private AppAcceptLimitDay dateBeforehandRestrictions;
	
	/**
	 * 利用する
	 */
	private boolean toUse;
	
	/**
	 * 時刻（早出残業）
	 */
	private Optional<AttendanceClock> opEarlyOvertime;
	
	/**
	 * 時刻（通常残業）
	 */
	private Optional<AttendanceClock> opNormalOvertime;
	
	/**
	 * 時刻（早出残業・通常残業）
	 */
	private Optional<AttendanceClock> opEarlyNormalOvertime;

	/**
	 * 時刻（複数回残業）
	 */
	private Optional<AttendanceClock> opMultipleOvertime;
	
	public OTAppBeforeAccepRestric(BeforeAddCheckMethod methodCheck, 
			AppAcceptLimitDay dateBeforehandRestrictions, boolean toUse, Optional<AttendanceClock> opEarlyOvertime,
			Optional<AttendanceClock> opNormalOvertime, Optional<AttendanceClock> opEarlyNormalOvertime, Optional<AttendanceClock> opMultipleOvertime) {
		this.methodCheck = methodCheck;
		this.dateBeforehandRestrictions = dateBeforehandRestrictions;
		this.toUse = toUse;
		this.opEarlyOvertime = opEarlyOvertime;
		this.opNormalOvertime = opNormalOvertime;
		this.opEarlyNormalOvertime = opEarlyNormalOvertime;
		this.opMultipleOvertime = opMultipleOvertime;
	}
	
	public static OTAppBeforeAccepRestric createNew(int methodCheck, int dateBeforehandRestrictions, boolean toUse, 
			Integer opEarlyOvertime, Integer opNormalOvertime, Integer opEarlyNormalOvertime, Integer opMultipleOvertime) {
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
