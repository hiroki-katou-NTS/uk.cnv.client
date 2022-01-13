package nts.uk.ctx.at.record.infra.entity.jobmanagement.favoritetask.manhourrecorditem;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourrecorditem.ManHourRecordItem;
import nts.uk.ctx.at.shared.dom.personallaborcondition.UseAtr;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 
 * @author tutt
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KRCMT_MAN_HR_ITEM")
public class KrcmtManHrItem extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KrcmtManHrItemPk pk;

	@Column(name = "DISPLAY_FORMAT")
	public int displayFormat;

	@Column(name = "MAN_HR_ITEM_NAME")
	public String manHrItemName;

	@Override
	protected Object getKey() {
		return this.pk;
	}

	public KrcmtManHrItem(ManHourRecordItem domain) {
		String cId = AppContexts.user().companyId();
		this.pk = new KrcmtManHrItemPk(cId, domain.getItemId());
		this.displayFormat = domain.getUseAtr().value;
		this.manHrItemName = domain.getName();
	}

	public ManHourRecordItem toDomain() {
		return new ManHourRecordItem(this.pk.manHrItemId, this.manHrItemName, UseAtr.valueOf(this.displayFormat));
	}
}
