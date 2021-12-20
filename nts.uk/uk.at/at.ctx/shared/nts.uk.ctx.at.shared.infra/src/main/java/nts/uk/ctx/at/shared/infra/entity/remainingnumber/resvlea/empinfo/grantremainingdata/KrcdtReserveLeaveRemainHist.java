package nts.uk.ctx.at.shared.infra.entity.remainingnumber.resvlea.empinfo.grantremainingdata;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.BooleanUtils;

import lombok.NoArgsConstructor;
import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.uk.ctx.at.shared.dom.remainingnumber.reserveleave.empinfo.grantremainingdata.ReserveLeaveGrantRemainHistoryData;
import nts.uk.ctx.at.shared.dom.remainingnumber.reserveleave.empinfo.grantremainingdata.ReserveLeaveGrantRemainingData;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureId;
import nts.uk.shr.com.time.calendar.date.ClosureDate;
import nts.uk.shr.infra.data.entity.ContractCompanyUkJpaEntity;

/**
 *
 * @author HungTT - 積立年休付与残数履歴データ
 *
 */

@NoArgsConstructor
@Entity
@Table(name = "KRCDT_HDSTK_REM_HIST")
public class KrcdtReserveLeaveRemainHist extends ContractCompanyUkJpaEntity implements Serializable{

	@EmbeddedId
	public KrcdtReserveLeaveRemainHistPK  krcdtReserveLeaveRemainHistPK;

	@Column(name = "DEADLINE")
	public GeneralDate deadline;

	@Column(name = "EXP_STATUS")
	public int expStatus;

	@Column(name = "REGISTER_TYPE")
	public int registerType;

	@Column(name = "GRANT_DAYS")
	public double grantDays;

	@Column(name = "USED_DAYS")
	public double usedDays;

	@Column(name = "OVER_LIMIT_DAYS")
	@Basic(optional = true)
	public Double overLimitDays;

	@Column(name = "REMAINING_DAYS")
	public double remainingDays;

	public static KrcdtReserveLeaveRemainHist fromDomain(ReserveLeaveGrantRemainHistoryData domain) {
		return new KrcdtReserveLeaveRemainHist(
				domain.getEmployeeId(),
				domain.getYearMonth().v(),
				domain.getClosureId().value,
				domain.getClosureDate().getClosureDay().v(),
				domain.getClosureDate().getLastDayOfMonth() ? 1 : 0,
				domain.getGrantDate(),
				domain.getDeadline(), domain.getExpirationStatus().value,
				domain.getRegisterType().value,
				domain.getDetails().getGrantNumber().getDays().v(),
				domain.getDetails().getUsedNumber().getDays().v(),
				domain.getDetails().getUsedNumber().getLeaveOverLimitNumber().isPresent()
						? domain.getDetails().getUsedNumber().getLeaveOverLimitNumber().get().numberOverDays.v() : null,
				domain.getDetails().getRemainingNumber().getDays().v());
	}

	public ReserveLeaveGrantRemainHistoryData toDomain() {
		
		val data = ReserveLeaveGrantRemainingData.createFromJavaType("", this.krcdtReserveLeaveRemainHistPK.sid, this.krcdtReserveLeaveRemainHistPK.grantDate, 
				deadline, expStatus, registerType, grantDays, usedDays, overLimitDays, grantDays);
		
		return new ReserveLeaveGrantRemainHistoryData(data, new YearMonth(this.krcdtReserveLeaveRemainHistPK.yearMonth),
				EnumAdaptor.valueOf(this.krcdtReserveLeaveRemainHistPK.closureId, ClosureId.class),
				new ClosureDate(this.krcdtReserveLeaveRemainHistPK.closeDay, this.krcdtReserveLeaveRemainHistPK.isLastDay));
	}

	public KrcdtReserveLeaveRemainHist(String sid, Integer yearMonth, Integer closureId, Integer closeDay,
			int isLastDay, GeneralDate grantDate,
			GeneralDate deadline, int expStatus, int registerType, double grantDays, double usedDays,
			Double overLimitDays, double remainingDays) {
		super();
		this.krcdtReserveLeaveRemainHistPK = new KrcdtReserveLeaveRemainHistPK(sid, yearMonth, closureId, closeDay, BooleanUtils.toBoolean(isLastDay), grantDate);
		this.deadline = deadline;
		this.expStatus = expStatus;
		this.registerType = registerType;
		this.grantDays = grantDays;
		this.usedDays = usedDays;
		this.overLimitDays = overLimitDays;
		this.remainingDays = remainingDays;

	}

	private static final long serialVersionUID = 1L;

	@Override
	protected Object getKey() {
		return this.krcdtReserveLeaveRemainHistPK;
	}

}
