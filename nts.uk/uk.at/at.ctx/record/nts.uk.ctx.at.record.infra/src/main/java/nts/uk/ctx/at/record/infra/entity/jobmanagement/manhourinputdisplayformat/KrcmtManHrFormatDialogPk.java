package nts.uk.ctx.at.record.infra.entity.jobmanagement.manhourinputdisplayformat;

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
public class KrcmtManHrFormatDialogPk implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column(name = "ATTENDANCE_ITEM_ID")
	public int attItemId;
	
	@Column(name = "CID")
	public String cId;
}
