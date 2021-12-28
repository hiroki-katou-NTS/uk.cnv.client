package nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.common.amount.AttendanceAmountDaily;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.premiumtime.PremiumTime;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.ExtraTimeItemNo;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.WorkingHoursUnitPrice;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 勤務予定の割増時間 UKDesign.データベース.ER図.就業.勤務予定.勤務予定.勤務予定
 * 
 * @author HieuLt
 *
 */
@Entity
@NoArgsConstructor
@Table(name = "KSCDT_SCH_PREMIUM")
@Getter
public class KscdtSchPremium extends ContractUkJpaEntity {

	@EmbeddedId
	public KscdtSchPremiumPK pk;
	/** 会社ID **/
	@Column(name = "CID")
	public String cid;

	/** 割増時間 **/
	@Column(name = "PREMIUM_TIME")
	public int premiumTime;
	
	/** 割増時間 **/
	@Column(name = "PREMIUM_TIME_AMOUNT")
	public int premiumTimeAmount;
	
	/** 割増時間 **/
	@Column(name = "PREMIUM_TIME_UNIT_COST")
	public int premiumTimeUnitCost;

	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumns({ @PrimaryKeyJoinColumn(name = "SID", referencedColumnName = "SID"),
			@PrimaryKeyJoinColumn(name = "YMD", referencedColumnName = "YMD") })
	public KscdtSchTime kscdtSchTime;

	@Override
	protected Object getKey() {
		return this.pk;
	}

	public KscdtSchPremium(KscdtSchPremiumPK pk, String cid, int premiumTime, int premiumTimeAmount,
			int premiumTimeUnitCost) {
		super();
		this.pk = pk;
		this.cid = cid;
		this.premiumTime = premiumTime;
		this.premiumTimeAmount = premiumTimeAmount;
		this.premiumTimeUnitCost = premiumTimeUnitCost;
	}


	public static KscdtSchPremium toEntity(PremiumTime premiumTime, String sid, GeneralDate ymd) {
		return new KscdtSchPremium(new KscdtSchPremiumPK(sid, ymd, premiumTime.getPremiumTimeNo().value),
				AppContexts.user().companyId(), premiumTime.getPremitumTime().v(), premiumTime.getPremiumAmount().v(),
				premiumTime.getUnitPrice().v());
	}
	//勤務予定．勤怠時間．勤務時間．割増時間．割増時間
	public List<PremiumTime> toDomain(List<KscdtSchPremium> premiums){
		List<PremiumTime> result = new ArrayList<>();
		if(!premiums.isEmpty()) {
		premiums.stream().forEach( x ->{
			PremiumTime time = new PremiumTime(ExtraTimeItemNo.valueOf(x.getPk().getFrameNo()), new AttendanceTime(x.getPremiumTime()),
					new AttendanceAmountDaily(x.premiumTimeAmount), new WorkingHoursUnitPrice(x.premiumTimeUnitCost));
			result.add(time);
		});
		}
		return result;
	}

}
