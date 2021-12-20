package nts.uk.ctx.at.request.infra.entity.application.holidayshipment.compltleavesimmana;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.apache.commons.lang3.BooleanUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.request.dom.application.holidayshipment.compltleavesimmng.AppHdsubRec;
import nts.uk.ctx.at.request.dom.application.holidayshipment.compltleavesimmng.SyncState;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 振休振出同時申請管理
 * 
 * @author ThanhPV
 */
@Entity
@Table(name = "KRQDT_APP_HDSUB_REC")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KrqdtAppHdsubRec extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private KrqdtAppHdsubRecPK pk;

	@Basic(optional = false)
	@Column(name = "SYNCING")
	private boolean syncing;

	@Override
	protected Object getKey() {
		return pk;
	}
	
	@PrePersist
    private void setInsertingContractInfo() {
		this.contractCd = AppContexts.user().contractCode();
	}

	public KrqdtAppHdsubRec(AppHdsubRec domain) {
		super();
		this.pk = new KrqdtAppHdsubRecPK(AppContexts.user().companyId(), domain.getRecAppID(), domain.getAbsenceLeaveAppID());
		this.syncing = BooleanUtils.toBoolean(domain.getSyncing().value);
	}
	
	public AppHdsubRec toDomain() {
		return new AppHdsubRec(
				this.pk.getRecAppID(), 
				this.pk.getAbsenceLeaveAppID(), 
				EnumAdaptor.valueOf(BooleanUtils.toInteger(this.syncing), SyncState.class));
	}
	
}
