package nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.annualholiday;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import nts.uk.ctx.at.function.dom.alarm.checkcondition.annualholiday.AlarmCheckConAgr;
import nts.uk.ctx.at.function.infra.entity.alarm.checkcondition.KfnmtAlarmCheckConditionCategory;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@NoArgsConstructor
@Entity
@Table(name = "KFNMT_ALST_CHK_HDPAID_OBL")
public class KfnmtAlCheckConAg extends ContractUkJpaEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KfnmtAlCheckConAgPK pk;
	
	/**
	 * 期間按分使用区分
	 */
	@Column(name = "DIVIDE_ATR")
	public int distByPeriod;
	
	/**
	 * 表示するメッセージ
	 */
	@Basic
	@Column(name = "MESSAGE_DISP")
	public String displayMessage;
	
	/**
	 * 年休使用義務日数
	 */
	@Basic
	@Column(name = "UNDER_LIMIT_DAY")
	public int usageObliDay;

	@Override
	protected Object getKey() {
		return this.pk;
	}
	
	@OneToOne
	@JoinColumns({ @JoinColumn(name = "CID", referencedColumnName = "CID", insertable = false, updatable = false),
			@JoinColumn(name = "CATEGORY", referencedColumnName = "CATEGORY", insertable = false, updatable = false),
			@JoinColumn(name = "CD", referencedColumnName = "CD", insertable = false, updatable = false) })
	public KfnmtAlarmCheckConditionCategory condition;
	
	public KfnmtAlCheckConAg(KfnmtAlCheckConAgPK pk, int distByPeriod, String displayMessage, int usageObliDay) {
		super();
		this.pk = pk;
		this.distByPeriod = distByPeriod;
		this.displayMessage = displayMessage;
		this.usageObliDay = usageObliDay;
	}
	
	public static KfnmtAlCheckConAg toEntity(String companyId, String code,
			int category, AlarmCheckConAgr domain) {
		return new KfnmtAlCheckConAg(new KfnmtAlCheckConAgPK(companyId, category, code),
				domain.isDistByPeriod() ? 1 : 0, domain.getDisplayMessage().isPresent() ? domain.getDisplayMessage().get().v() : null, 
				domain.getUsageObliDay().v());
	}

	public AlarmCheckConAgr toDomain() {
		return new AlarmCheckConAgr(distByPeriod == 1, displayMessage, usageObliDay);
	}

}
