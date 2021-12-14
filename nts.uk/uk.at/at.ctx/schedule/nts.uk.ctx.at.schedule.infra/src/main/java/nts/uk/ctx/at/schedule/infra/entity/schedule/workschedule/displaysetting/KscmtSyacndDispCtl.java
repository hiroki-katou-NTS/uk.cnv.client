package nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule.displaysetting;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.schedule.dom.workschedule.displaysetting.DisplayControlPersonalCondition;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 
 * 個人条件の表示制御 entity
 *
 */
@AllArgsConstructor
@Entity
@NoArgsConstructor
@Table(name = "KSCMT_SYACND_DISPCTL")
public class KscmtSyacndDispCtl extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KscmtSyacndDispCtlPK pk;

	/** 表示区分 **/
	@Column(name = "DISP_ATR")
	public int dispAtr;

	/** 表示記号 **/
	@Column(name = "SYNAME")
	public String syname;

	@Override
	protected Object getKey() {
		return this.pk;
	}

	// @OneToMany(targetEntity = KscmtSyacndDispCtlQua.class, mappedBy =
	// "kscmtSyacndDispCtl", cascade = CascadeType.ALL, orphanRemoval = true,
	// fetch = FetchType.LAZY)
	// @JoinTable(name = "KSCMT_SYACND_DISPCTL_QUA")
	// public List<KscmtSyacndDispCtlQua> ctlQuas;

	public static List<KscmtSyacndDispCtl> toEntities(DisplayControlPersonalCondition dom) {
		String qualificationMark = dom.getOtpWorkscheQualifi().isPresent()
				? dom.getOtpWorkscheQualifi().get().getQualificationMark().v() : null;
		return dom.getListConditionDisplayControl().stream().map(i -> {
			KscmtSyacndDispCtlPK dispCtlPK = new KscmtSyacndDispCtlPK(dom.getCompanyID(), i.getConditionATR().value);
			return new KscmtSyacndDispCtl(dispCtlPK, i.getDisplayCategory().value, qualificationMark);

		}).collect(Collectors.toList());
	}
}
