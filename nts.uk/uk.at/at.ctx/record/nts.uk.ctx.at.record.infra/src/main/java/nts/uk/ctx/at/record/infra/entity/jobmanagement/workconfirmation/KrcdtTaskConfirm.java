package nts.uk.ctx.at.record.infra.entity.jobmanagement.workconfirmation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.jobmanagement.workconfirmation.ConfirmationWorkResults;
import nts.uk.ctx.at.record.dom.jobmanagement.workconfirmation.Confirmer;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * @author thanhpv
 * @name 作業実績の確認 ConfirmationWorkResults
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KRCDT_TASK_CONFIRM")
public class KrcdtTaskConfirm extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KrcdtTaskConfirmPK pk;

	@Column(name = "CID")
	public String cid;

	@Column(name = "CONFIRM_SID1")
	public String confirmSid1;
	
	@Column(name = "CONFIRM_DATE_TIME1")
	public GeneralDateTime confirmDateTime1;
	
	@Column(name = "CONFIRM_SID2")
	public String confirmSid2;
	
	@Column(name = "CONFIRM_DATE_TIME2")
	public GeneralDateTime confirmDateTime2;
	
	@Column(name = "CONFIRM_SID3")
	public String confirmSid3;
	
	@Column(name = "CONFIRM_DATE_TIME3")
	public GeneralDateTime confirmDateTime3;
	
	@Column(name = "CONFIRM_SID4")
	public String confirmSid4;
	
	@Column(name = "CONFIRM_DATE_TIME4")
	public GeneralDateTime confirmDateTime4;
	
	@Column(name = "CONFIRM_SID5")
	public String confirmSid5;
	
	@Column(name = "CONFIRM_DATE_TIME5")
	public GeneralDateTime confirmDateTime5;

	@Override
	protected Object getKey() {
		return this.pk;
	}
	
	public KrcdtTaskConfirm(ConfirmationWorkResults domain) {
		this.cid = AppContexts.user().companyId();
		this.pk = new KrcdtTaskConfirmPK(domain.getTargetSID(), domain.getTargetYMD());
		if(domain.getConfirmers().size() > 0) {
			this.confirmSid1 = domain.getConfirmers().get(0).getConfirmSID();
			this.confirmDateTime1 = domain.getConfirmers().get(0).getConfirmDateTime();
		}
		if(domain.getConfirmers().size() > 1) {
			this.confirmSid2 = domain.getConfirmers().get(1).getConfirmSID();
			this.confirmDateTime2 = domain.getConfirmers().get(1).getConfirmDateTime();
		}
		if(domain.getConfirmers().size() > 2) {
			this.confirmSid3 = domain.getConfirmers().get(2).getConfirmSID();
			this.confirmDateTime3 = domain.getConfirmers().get(2).getConfirmDateTime();
		}
		if(domain.getConfirmers().size() > 3) {
			this.confirmSid4 = domain.getConfirmers().get(3).getConfirmSID();
			this.confirmDateTime4 = domain.getConfirmers().get(3).getConfirmDateTime();
		}
		if(domain.getConfirmers().size() > 4) {
			this.confirmSid5 = domain.getConfirmers().get(4).getConfirmSID();
			this.confirmDateTime5 = domain.getConfirmers().get(4).getConfirmDateTime();
		}
	}

	public ConfirmationWorkResults toDomain() {
		List<Confirmer> confirmers = new ArrayList<Confirmer>();
		if(this.confirmSid1 != null && this.confirmDateTime1 != null)
			confirmers.add(new Confirmer(this.confirmSid1, this.confirmDateTime1));
		if(this.confirmSid2 != null && this.confirmDateTime2 != null)
			confirmers.add(new Confirmer(this.confirmSid2, this.confirmDateTime2));
		if(this.confirmSid3 != null && this.confirmDateTime3 != null)
			confirmers.add(new Confirmer(this.confirmSid3, this.confirmDateTime3));
		if(this.confirmSid4 != null && this.confirmDateTime4 != null)
			confirmers.add(new Confirmer(this.confirmSid4, this.confirmDateTime4));
		if(this.confirmSid5 != null && this.confirmDateTime5 != null)
			confirmers.add(new Confirmer(this.confirmSid5, this.confirmDateTime5));
		return new ConfirmationWorkResults(this.pk.targetSid, this.pk.targetYMD, confirmers);
	}
}
