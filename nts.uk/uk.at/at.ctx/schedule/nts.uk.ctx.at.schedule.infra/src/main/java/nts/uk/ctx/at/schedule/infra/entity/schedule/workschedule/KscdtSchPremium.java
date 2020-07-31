package nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 勤務予定の割増時間
 * UKDesign.データベース.ER図.就業.勤務予定.勤務予定.勤務予定
 * @author kingo
 *
 */
@Entity
@NoArgsConstructor
@Table(name="KSCDT_SCH_PREMIUM")
public class KscdtSchPremium extends ContractUkJpaEntity{
	
	@EmbeddedId
	public KscdtSchPremiumPK pk;
	/** 会社ID **/
	@Column(name = "CID")
	public String cid;
	
	/** 割増時間 **/
	@Column(name = "PREMIUM_TIME")
	public int premiumTime;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumns({ @PrimaryKeyJoinColumn(name = "CID", referencedColumnName = "CID"),
			@PrimaryKeyJoinColumn(name = "YMD", referencedColumnName = "YMD") })
	public KscdtSchTime kscdtSchTime;

	@Override
	protected Object getKey() {
		return this.pk;
	}

}
