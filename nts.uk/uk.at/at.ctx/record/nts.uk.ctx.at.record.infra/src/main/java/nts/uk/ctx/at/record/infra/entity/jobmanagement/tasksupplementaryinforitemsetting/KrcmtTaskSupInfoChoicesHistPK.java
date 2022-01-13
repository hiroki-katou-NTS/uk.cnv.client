package nts.uk.ctx.at.record.infra.entity.jobmanagement.tasksupplementaryinforitemsetting;

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
public class KrcmtTaskSupInfoChoicesHistPK implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "MAN_HR_ITEM_ID")
	public int manHrItemId;
	
	@Column(name = "HIST_ID")
	public String histId;
}
