package nts.uk.ctx.at.record.infra.entity.weekly.verticaltotal.worktime;

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
import nts.uk.ctx.at.record.dom.monthly.verticaltotal.worktime.premiumtime.AggregatePremiumTime;
import nts.uk.ctx.at.record.dom.weekly.AttendanceTimeOfWeeklyKey;
import nts.uk.ctx.at.record.infra.entity.weekly.KrcdtWekAttendanceTime;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTimeMonth;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

/**
 * 集計割増時間
 * @author shuichu_ishida
 */
@Entity
@Table(name = "KRCDT_WEK_AGGR_PREM_TIME")
@NoArgsConstructor
@AllArgsConstructor
public class KrcdtWekAggrPremTime extends UkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/** プライマリキー */
	@EmbeddedId
	public KrcdtWekAggrPremTimePK PK;
	
	/** 割増時間 */
	@Column(name = "PREMIUM_TIME")
	public int premiumTime;

	/** マッチング：週別実績の勤怠時間 */
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "SID", referencedColumnName = "KRCDT_WEK_ATTENDANCE_TIME.SID", insertable = false, updatable = false),
		@JoinColumn(name = "YM", referencedColumnName = "KRCDT_WEK_ATTENDANCE_TIME.YM", insertable = false, updatable = false),
		@JoinColumn(name = "CLOSURE_ID", referencedColumnName = "KRCDT_WEK_ATTENDANCE_TIME.CLOSURE_ID", insertable = false, updatable = false),
		@JoinColumn(name = "CLOSURE_DAY", referencedColumnName = "KRCDT_WEK_ATTENDANCE_TIME.CLOSURE_DAY", insertable = false, updatable = false),
		@JoinColumn(name = "IS_LAST_DAY", referencedColumnName = "KRCDT_WEK_ATTENDANCE_TIME.IS_LAST_DAY", insertable = false, updatable = false),
		@JoinColumn(name = "WEEK_NO", referencedColumnName = "KRCDT_WEK_ATTENDANCE_TIME.WEEK_NO", insertable = false, updatable = false)
	})
	public KrcdtWekAttendanceTime krcdtWekAttendanceTime;
	
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
				this.PK.premiumTimeItemNo,
				new AttendanceTimeMonth(this.premiumTime));
	}
	
	/**
	 * ドメインから変換　（for Insert）
	 * @param key キー値：週別実績の勤怠時間
	 * @param domain 集計割増時間
	 */
	public void fromDomainForPersist(AttendanceTimeOfWeeklyKey key, AggregatePremiumTime domain){
		
		this.PK = new KrcdtWekAggrPremTimePK(
				key.getEmployeeId(),
				key.getYearMonth().v(),
				key.getClosureId().value,
				key.getClosureDate().getClosureDay().v(),
				(key.getClosureDate().getLastDayOfMonth() ? 1 : 0),
				key.getWeekNo(),
				domain.getPremiumTimeItemNo());
		this.fromDomainForUpdate(domain);
	}
	
	/**
	 * ドメインから変換　(for Update)
	 * @param domain 集計割増時間
	 */
	public void fromDomainForUpdate(AggregatePremiumTime domain){
		
		this.premiumTime = domain.getTime().v();
	}
}
