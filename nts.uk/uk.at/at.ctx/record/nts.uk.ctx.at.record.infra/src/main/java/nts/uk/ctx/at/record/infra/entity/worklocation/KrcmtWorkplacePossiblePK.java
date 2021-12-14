package nts.uk.ctx.at.record.infra.entity.worklocation;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * @author tutk
 *
 */
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class KrcmtWorkplacePossiblePK implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 契約コード */
	@NotNull
	@Column(name = "CONTRACT_CD")
	public String contractCode;

	/** 勤務場所コード */
	@NotNull
	@Column(name = "WK_LOCATION_CD")
	public String workLocationCD;
	
	/** 会社ID */
	@NotNull
	@Column(name = "CID")
	public String cid;

}
