package nts.uk.ctx.at.record.infra.entity.daily.timegroup;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.daily.ouen.SupportFrameNo;
import nts.uk.ctx.at.shared.dom.common.time.TimeSpanForCalc;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * @name 日別実績の作業時間帯グループ
 * @author tutt
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KSRDT_TASK_TS_GROUP")
public class KsrdtTaskTsGroup extends ContractUkJpaEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KsrdtTaskTsGroupPk pk;

	@Column(name = "CID")
	public String cid;

	@Column(name = "END_CLOCK")
	public int endClock;

	@Override
	protected Object getKey() {
		return this.pk;
	}

	public KsrdtTaskTsGroup(String sId, GeneralDate date, TimeSpanForCalc caltimeSpan, SupportFrameNo sn) {
		this.pk = new KsrdtTaskTsGroupPk(sId, date, caltimeSpan.start(), sn.v());
		this.cid = AppContexts.user().companyId();
		this.endClock = caltimeSpan.end();
	}
}
