package nts.uk.ctx.at.record.infra.entity.byperiod.verticaltotal.worktime;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.infra.entity.byperiod.KrcdtAnpAttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonth;
import nts.uk.ctx.at.shared.dom.scherec.byperiod.AttendanceTimeOfAnyPeriodKey;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.ExtraTimeItemNo;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceAmountMonth;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.verticaltotal.worktime.premiumtime.AggregatePremiumTime;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 集計割増時間
 * @author shuichu_ishida
 */
@Entity
@Table(name = "KRCDT_ANP_AGGR_PREM_TIME")
@NoArgsConstructor
@AllArgsConstructor
public class KrcdtAnpAggrPremTime extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/** プライマリキー */
	@EmbeddedId
	public KrcdtAnpAggrPremTimePK PK;
	
	/** 割増時間 */
	@Column(name = "PREMIUM_TIME")
	public int premiumTime;
	/** 割増金額 */
	@Column(name = "PREMIUM_AMOUNT")
	public long premiumAmount;

	/** マッチング：任意期間別実績の勤怠時間 */
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "SID", referencedColumnName = "SID", insertable = false, updatable = false),
		@JoinColumn(name = "FRAME_CODE", referencedColumnName = "FRAME_CODE", insertable = false, updatable = false)
	})
	public KrcdtAnpAttendanceTime krcdtAnpAttendanceTime;
	
	/**
	 * キー取得
	 */
	@Override
	protected Object getKey() {		
		return this.PK;
	}
	
	/**
	 * ドメインに変換
	 * @return 集計割増時間
	 */
	public AggregatePremiumTime toDomain(){
		
		return AggregatePremiumTime.of(
				ExtraTimeItemNo.valueOf(this.PK.premiumTimeItemNo),
				new AttendanceTimeMonth(this.premiumTime),
				new AttendanceAmountMonth(this.premiumAmount));
	}
	
	/**
	 * ドメインから変換　（for Insert）
	 * @param key キー値：任意期間別実績の勤怠時間
	 * @param domain 集計割増時間
	 */
	public void fromDomainForPersist(AttendanceTimeOfAnyPeriodKey key, AggregatePremiumTime domain){
		
		this.PK = new KrcdtAnpAggrPremTimePK(
				key.getEmployeeId(),
				key.getAnyAggrFrameCode().v(),
				domain.getPremiumTimeItemNo().value);
		this.fromDomainForUpdate(domain);
	}
	
	/**
	 * ドメインから変換　(for Update)
	 * @param domain 集計割増時間
	 */
	public void fromDomainForUpdate(AggregatePremiumTime domain){
		
		this.premiumTime = domain.getTime().v();
		this.premiumAmount = domain.getAmount().v();
	}
}
