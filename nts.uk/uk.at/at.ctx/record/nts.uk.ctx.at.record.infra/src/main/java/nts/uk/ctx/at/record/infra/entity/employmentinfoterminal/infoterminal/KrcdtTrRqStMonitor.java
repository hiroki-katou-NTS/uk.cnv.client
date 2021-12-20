package nts.uk.ctx.at.record.infra.entity.employmentinfoterminal.infoterminal;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.EmpInfoTerminalCode;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.ReqComStatusMonitoring;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

/**
 * 
 * @author dungbn
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KRCDT_TR_RQ_ST_MONITOR")
public class KrcdtTrRqStMonitor extends UkJpaEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KrcdtTrRqStMonitorPK pk;
	
	/**
	 * 通信中
	 */
	@Column(name = "CONNECTING_FLG")
	public boolean connecting;
	
	@Override
	protected Object getKey() {
		return pk;
	}
	
	public static KrcdtTrRqStMonitor toEntity(ReqComStatusMonitoring domain) {
		return new KrcdtTrRqStMonitor(
				new KrcdtTrRqStMonitorPK(domain.getContractCode().v(), domain.getTerminalCode().v()),
				domain.isConnecting());
	}
	
	public ReqComStatusMonitoring toDomain() {
		return new ReqComStatusMonitoring(
				new ContractCode(this.pk.contractCode),
				new EmpInfoTerminalCode(String.valueOf(this.pk.timeRecordCode)),
				this.connecting);
	}

}
