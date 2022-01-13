package nts.uk.ctx.at.record.infra.entity.jobmanagement.manhourinputdisplayformat;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.jobmanagement.displayformat.DisplayManHrRecordItem;
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
@Table(name = "KRCMT_MAN_HR_FORMAT_TASK")
public class KrcmtManHrFormatDialogTask extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KrcmtManHrFormatDialogTaskPk pk;

	@Column(name = "DISPORDER")
	public int dispOrder;

	@Override
	protected Object getKey() {
		return this.pk;
	}

	public KrcmtManHrFormatDialogTask toEntity(DisplayManHrRecordItem domain) {
		return new KrcmtManHrFormatDialogTask(
				new KrcmtManHrFormatDialogTaskPk(domain.getItemId(), AppContexts.user().companyId()),
				domain.getOrder());
	}

	public DisplayManHrRecordItem toDomain() {
		return new DisplayManHrRecordItem(this.pk.manHrItemId, this.dispOrder);
	}
}
