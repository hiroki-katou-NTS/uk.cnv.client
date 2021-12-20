package nts.uk.ctx.at.shared.infra.entity.remainingnumber.annlea;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang3.BooleanUtils;

import lombok.NoArgsConstructor;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.empinfo.maxdata.AnnualLeaveMaxHistoryData;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 *
 * @author HungTT - 年休上限履歴データ
 *
 */
@NoArgsConstructor
@Entity
@Table(name = "KRCDT_HDPAID_MAX_HIST")
public class KrcdtAnnLeaMaxHist extends ContractUkJpaEntity {

	/** プライマリキー */
	@EmbeddedId
	public KrcdtAnnLeaMaxHistPK PK;

	//半日年休上限回数
	@Column(name = "MAX_TIMES")
	@Basic(optional = true)
	public Integer maxTimes;

	//半日年休使用回数
	@Column(name = "USED_TIMES")
	@Basic(optional = true)
	public Integer usedTimes;

	//半日年休残回数
	@Column(name = "REMAINING_TIMES")
	@Basic(optional = true)
	public Integer remainingTimes;

	//時間年休上限時間
	@Column(name = "MAX_MINUTES")
	@Basic(optional = true)
	public Integer maxMinutes;

	//時間年休使用時間
	@Column(name = "USED_MINUTES")
	@Basic(optional = true)
	public Integer usedMinutes;

	//時間年休残時間
	@Column(name = "REMAINING_MINUTES")
	@Basic(optional = true)
	public Integer remainingMinutes;

	@Override
	protected Object getKey() {
		return this.PK;
	}

	public KrcdtAnnLeaMaxHist(String sid, Integer maxTimes, Integer usedTimes, Integer remainingTimes,
			Integer maxMinutes, Integer usedMinutes, Integer remainingMinutes, int yearMonth, int closureId,
			Integer closeDay, Integer isLastDay) {
		super();
		this.maxTimes = maxTimes;
		this.usedTimes = usedTimes;
		this.remainingTimes = remainingTimes;
		this.maxMinutes = maxMinutes;
		this.usedMinutes = usedMinutes;
		this.remainingMinutes = remainingMinutes;
		this.PK = new KrcdtAnnLeaMaxHistPK(sid, yearMonth, closureId, closeDay, BooleanUtils.toBoolean(isLastDay));
	}

	public static KrcdtAnnLeaMaxHist fromDomain(AnnualLeaveMaxHistoryData domain) {
		return new KrcdtAnnLeaMaxHist(domain.getEmployeeId(),
				domain.getHalfdayAnnualLeaveMax().isPresent()
						? domain.getHalfdayAnnualLeaveMax().get().getMaxTimes().v() : null,
				domain.getHalfdayAnnualLeaveMax().isPresent()
						? domain.getHalfdayAnnualLeaveMax().get().getUsedTimes().v() : null,
				domain.getHalfdayAnnualLeaveMax().isPresent()
						? domain.getHalfdayAnnualLeaveMax().get().getRemainingTimes().v() : null,
				domain.getTimeAnnualLeaveMax().isPresent() ? domain.getTimeAnnualLeaveMax().get().getMaxMinutes().v()
						: null,
				domain.getTimeAnnualLeaveMax().isPresent() ? domain.getTimeAnnualLeaveMax().get().getUsedMinutes().v()
						: null,
				domain.getTimeAnnualLeaveMax().isPresent()
						? domain.getTimeAnnualLeaveMax().get().getRemainingMinutes().v() : null,
				domain.getYearMonth().v(), domain.getClosureId().value,
				domain.getClosureDate().getClosureDay().v().intValue(),
				domain.getClosureDate().getLastDayOfMonth() ? 1 : 0);
	}


}
