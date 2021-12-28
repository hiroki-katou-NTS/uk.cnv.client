package nts.uk.ctx.at.record.infra.entity.jobmanagement.manhourinputdisplayformat;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.jobmanagement.displayformat.DisplayAttItem;
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
@Table(name = "KRCMT_MAN_HR_FORMAT_DIALOG")
public class KrcmtManHrFormatDialog extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KrcmtManHrFormatDialogPk pk;

	@Column(name = "DISPORDER")
	public int disOrder;

	@Override
	protected Object getKey() {
		return this.pk;
	}

	public KrcmtManHrFormatDialog toEntity(DisplayAttItem domain) {
		return new KrcmtManHrFormatDialog(
				new KrcmtManHrFormatDialogPk(domain.getAttendanceItemId(), AppContexts.user().companyId()),
				domain.getOrder());
	}

	public DisplayAttItem toDomain() {
		return new DisplayAttItem(this.pk.attItemId, this.disOrder);
	}
}
