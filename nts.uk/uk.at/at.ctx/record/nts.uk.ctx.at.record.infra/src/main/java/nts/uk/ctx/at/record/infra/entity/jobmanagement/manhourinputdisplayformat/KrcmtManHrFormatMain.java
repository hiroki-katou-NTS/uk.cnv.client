package nts.uk.ctx.at.record.infra.entity.jobmanagement.manhourinputdisplayformat;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.jobmanagement.displayformat.RecordColumnDispName;
import nts.uk.ctx.at.record.dom.jobmanagement.displayformat.RecordColumnDisplayItem;
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
@Table(name = "KRCMT_MAN_HR_FORMAT_MAIN")
public class KrcmtManHrFormatMain extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KrcmtManHrFormatMainPk pk;

	@Column(name = "NAME")
	public String name;

	@Column(name = "DISPORDER")
	public int dispOrder;

	@Override
	protected Object getKey() {
		return this.pk;
	}

	public KrcmtManHrFormatMain toEntity(RecordColumnDisplayItem domain) {
		return new KrcmtManHrFormatMain(
				new KrcmtManHrFormatMainPk(AppContexts.user().companyId(), domain.getAttendanceItemId()),
				domain.getDisplayName().v(), domain.getOrder());
	}

	public RecordColumnDisplayItem toDomain() {
		return new RecordColumnDisplayItem(this.dispOrder, this.pk.attItemId, new RecordColumnDispName(this.name));
	}

}
