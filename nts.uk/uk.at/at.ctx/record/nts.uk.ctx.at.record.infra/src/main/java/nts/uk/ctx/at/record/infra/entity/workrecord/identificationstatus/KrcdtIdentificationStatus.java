package nts.uk.ctx.at.record.infra.entity.workrecord.identificationstatus;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

/**
 * 
 * @author nampt
 * 本人確認
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KRCDT_IDENTIFICATION_STA")
public class KrcdtIdentificationStatus extends UkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KrcdtIdentificationStatusPK krcdtIdentificationStatusPK;

	@Column(name = "PROCESSING_YMD")
	public GeneralDate processingYmd;
	
	@Column(name = "INDENTIFICATION_YMD")
	public GeneralDate indentificationYmd;

	@Override
	protected Object getKey() {
		return this.krcdtIdentificationStatusPK;
	}
}
