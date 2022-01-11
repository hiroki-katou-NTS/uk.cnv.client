package nts.uk.ctx.at.shared.dom.specialholiday.export;

import java.math.BigDecimal;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.GrantRemainRegisterType;
import nts.uk.ctx.at.shared.dom.remainingnumber.base.LeaveExpirationStatus;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveGrantDayNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveNumberInfo;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedNumber;
import nts.uk.ctx.at.shared.dom.remainingnumber.common.empinfo.grantremainingdata.daynumber.LeaveUsedPercent;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.empinfo.grantremainingdata.SpecialLeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialleave.service.ConditionFlg;
import nts.uk.ctx.at.shared.dom.yearholidaygrant.GrantNum;

/**
 * 次回特別休暇付与
 * @author masaaki_jinno
 *
 */
@Getter
@Setter
@AllArgsConstructor
public class NextSpecialLeaveGrant {

	/** 付与年月日 */
	private GeneralDate grantDate;
	/** 付与日数 */
	private LeaveGrantDayNumber grantDays;
	/** 付与回数 */
	private GrantNum times;
	/** 期限日 */
	private GeneralDate deadLine;
	
	/**
	 * エラーフラグ
	 */
	private Optional<ConditionFlg> errorFlg;
	
	/**
	 * コンストラクタ
	 */
	public NextSpecialLeaveGrant(){
		this.grantDate = GeneralDate.today();
		this.grantDays = new LeaveGrantDayNumber(0.0);
		this.times = new GrantNum(0);
		this.deadLine = GeneralDate.max();
		this.errorFlg = Optional.empty();
	}
	
	public NextSpecialLeaveGrant(GeneralDate grantDate, LeaveGrantDayNumber grantDays, GrantNum times,
			GeneralDate deadLine) {
		this.grantDate = grantDate;
		this.grantDays = grantDays;
		this.times = times;
		this.deadLine = deadLine;
		this.errorFlg = Optional.empty();
	}
	
	/**
	 * 特別休暇付与残数データを作成
	 * @param employeeId
	 * @param code
	 * @return
	 */
	public SpecialLeaveGrantRemainingData toSpecialLeaveGrantRemainingData(String employeeId, int code){
		return new SpecialLeaveGrantRemainingData(
				employeeId,
				this.getGrantDate(),
				this.getDeadLine(),
				LeaveExpirationStatus.AVAILABLE,
				GrantRemainRegisterType.MONTH_CLOSE,
				toLeaveNumberInfo(),
				code
				);
		
	}
	/**
	 * 休暇数情報作成
	 * @return
	 */
	private LeaveNumberInfo toLeaveNumberInfo() {
		return new LeaveNumberInfo(this.grantDays.toLeaveGrantNumber(), new LeaveUsedNumber(),
				this.grantDays.toLeaveRemainingNumber(), new LeaveUsedPercent(new BigDecimal(0)));
	}
}
