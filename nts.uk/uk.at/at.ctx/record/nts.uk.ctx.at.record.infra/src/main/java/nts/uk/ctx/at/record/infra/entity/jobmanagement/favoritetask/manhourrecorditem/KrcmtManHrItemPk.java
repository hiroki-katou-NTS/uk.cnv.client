package nts.uk.ctx.at.record.infra.entity.jobmanagement.favoritetask.manhourrecorditem;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 
 * @author tutt
 *
 */
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class KrcmtManHrItemPk implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "CID")
	public String cId;
	
	@Column(name = "MAN_HR_ITEM_ID")
	public int manHrItemId;
}
