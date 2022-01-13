package nts.uk.ctx.at.record.infra.entity.executionstatusmanage.optionalperiodprocess;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 任意期間集計エラーメッセージ情報
 * @author phongtq
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KRCDT_ANP_PERIOD_ERR")
public class KrcdtAnpPeriodErr extends ContractUkJpaEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	public KrcmtAggrPeriodInforPK krcmtAggrPeriodInforPK;

	@Column(name = "PROCESS_DAY")
	public GeneralDate processDay;
	
	@Column(name = "ERROR_MESS")
	public String errorMess;
	
	@Override
	protected Object getKey() {
		// TODO Auto-generated method stub
		return krcmtAggrPeriodInforPK;
	}
}
