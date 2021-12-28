package nts.uk.ctx.at.shared.dom.remainingnumber.reserveleave.empinfo.grantremainingdata;

import java.math.BigDecimal;

import lombok.Getter;
import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.GrantRemainRegisterType;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.LeaveExpirationStatus;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.LeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveNumberInfo;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveRemainingNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedNumber;

@Getter
// domain name CS00038: 積立年休付与残数データ
public class ReserveLeaveGrantRemainingData extends LeaveGrantRemainingData {

	/**
	 * ファクトリー
	 * @param leavID ID
	 * @param employeeId 社員ID
	 * @param grantDate 付与日
	 * @param deadline 期限日
	 * @param expirationStatus 期限切れ状態
	 * @param registerType 登録種別
	 * @param details 明細
	 * @return 休暇付与残数データ　
	 */
	public static ReserveLeaveGrantRemainingData of(
			LeaveGrantRemainingData remaingData) {

		return new ReserveLeaveGrantRemainingData(
				remaingData.getLeaveID(),
				remaingData.getEmployeeId(),
				remaingData.getGrantDate(),
				remaingData.getDeadline(),
				remaingData.getExpirationStatus(),
				remaingData.getRegisterType(),
				remaingData.getDetails()
				);
	}

	public static ReserveLeaveGrantRemainingData createFromJavaType(String id, String employeeId, GeneralDate grantDate,
			GeneralDate deadline, int expirationStatus, int registerType, double grantDays, double usedDays,
			Double overLimitDays, double remainDays) {

		return new ReserveLeaveGrantRemainingData(
				id,
				employeeId,
				grantDate,
				deadline,
				EnumAdaptor.valueOf(expirationStatus, LeaveExpirationStatus.class),
				EnumAdaptor.valueOf(registerType, GrantRemainRegisterType.class),
				new ReserveLeaveNumberInfo(grantDays, usedDays, overLimitDays, remainDays)
				);
	}

	/**
	 * 積立年休を指定日数消化する
	 * @param usedDays 積立年休使用日数
	 * @param isForcibly 強制的に消化するか
	 * @return 積立年休使用残
	 */
	// 2018.7.15 add shuichi_ishida
	public double digest(double usedDays, boolean isForcibly){

		// 「積立年休使用日数」を所得
		if (usedDays <= 0.0) 
			return 0.0;


		double remainingNumber = this.details.getRemainingNumber().getDays().v();
		if (isDigestionable(usedDays,isForcibly)){

			// 積立年休残数から減算
			double newRemain = remainingNumber - usedDays;
			this.details.setRemainingNumber(new LeaveRemainingNumber(newRemain, 0));

			// 積立年休使用数に加算
//			this.details.addDaysToUsedNumber(remainingDays);
			this.details.getUsedNumber().add(new LeaveUsedNumber(usedDays, 0) );

			// 積立年休使用残を0にする
			return 0.0;
		}
		else {
			//積立年休使用残から減算
			double remainingDays = usedDays - remainingNumber; 

			// 積立年休使用数に加算
			// this.details.addDaysToUsedNumber(remainingNumber);
			this.details.getUsedNumber().add(new LeaveUsedNumber(remainingDays, 0) );

			// 積立年休残数を0にする
			this.details.setRemainingNumber(new LeaveRemainingNumber());
			
			return remainingDays;
		}
	}

	private boolean isDigestionable(double usedDays, boolean isForcibly) {
		if (isForcibly)
			return true;

		return this.details.getRemainingNumber().getDays().greaterThanOrEqualTo(usedDays);
	}

	public static void validate(ReserveLeaveGrantRemainingData domain) {
		if ((domain.getDetails().getGrantNumber() != null) || (domain.getDetails().getUsedNumber().getDays() != null)
				|| (domain.getDetails().getRemainingNumber() != null)) {
			if (domain.getGrantDate() == null || domain.getDeadline() == null) {
				if (domain.getGrantDate() == null) {
					throw new BusinessException("Msg_925", "付与日");
				}
				if (domain.getDeadline() == null) {
					throw new BusinessException("Msg_925", "期限日");
				}
			}
		}
		// 付与日＞使用期限の場合はエラー #Msg_1023
		if (domain.getGrantDate().compareTo(domain.getDeadline()) > 0) {
			throw new BusinessException("Msg_1023");
		}
	}

	// hàm validate cho domain để khi add command hay ghi log cho domain thì sẽ xem xét xem có được add hay không
	public static boolean validate(GeneralDate grantDate, GeneralDate deadlineDate,
			BigDecimal grantDays, BigDecimal usedDays, BigDecimal remainDays , String grantDateItemName, String  deadlineDateItemName) {
		boolean isNull = validate(grantDate, deadlineDate, grantDays, usedDays, remainDays);
		if(isNull == false) return false;
		if (grantDays != null || usedDays != null || remainDays != null) {
			if (deadlineDate == null || grantDate == null) {
				if (grantDate == null) {
					throw new BusinessException("Msg_925", grantDateItemName == null ? "付与日" : grantDateItemName);
				}
				if (deadlineDate == null) {
					throw new BusinessException("Msg_925", deadlineDateItemName == null ? "期限日" : deadlineDateItemName);
				}
			}
		}
		if (grantDate == null && deadlineDate != null) {
			throw new BusinessException("Msg_925", grantDateItemName == null ? "付与日" : grantDateItemName);
		}
		if (deadlineDate == null && grantDate != null) {
			throw new BusinessException("Msg_925", deadlineDateItemName == null ? "期限日" : deadlineDateItemName);
		}
		if (grantDate != null && deadlineDate != null) {
			// 付与日＞使用期限の場合はエラー #Msg_1023
			if (grantDate.compareTo(deadlineDate) > 0) {
				throw new BusinessException("Msg_1023");
			}
		}
		return true;
	}
	public static boolean validate(GeneralDate grantDate, GeneralDate deadlineDate, BigDecimal grantDays, BigDecimal usedDays, BigDecimal remainDays ) {
		if (grantDate == null && deadlineDate == null && grantDays == null && usedDays == null && remainDays == null)
			return false;
		return true;
	}

	/**
	 * コンストラクタ
	 * @param leaveID
	 * @param employeeId
	 * @param grantDate
	 * @param deadline
	 * @param expirationStatus
	 * @param grantRemainRegisterType
	 * @param details
	 */
	public ReserveLeaveGrantRemainingData(String leaveID,String employeeId, GeneralDate grantDate, GeneralDate deadline,
			LeaveExpirationStatus expirationStatus, GrantRemainRegisterType grantRemainRegisterType,LeaveNumberInfo details){
		super(leaveID, employeeId, grantDate, deadline, expirationStatus,grantRemainRegisterType,details);
	}
	
	/**
	 * コンストラクタ
	 * @param employeeId
	 * @param grantDate
	 * @param deadline
	 * @param expirationStatus
	 * @param grantRemainRegisterType
	 * @param details
	 */
	public ReserveLeaveGrantRemainingData(String employeeId, GeneralDate grantDate, GeneralDate deadline,
			LeaveExpirationStatus expirationStatus, GrantRemainRegisterType grantRemainRegisterType,LeaveNumberInfo details){
		super(IdentifierUtil.randomUniqueId(), employeeId, grantDate, deadline, expirationStatus,grantRemainRegisterType,details);
	}

}
