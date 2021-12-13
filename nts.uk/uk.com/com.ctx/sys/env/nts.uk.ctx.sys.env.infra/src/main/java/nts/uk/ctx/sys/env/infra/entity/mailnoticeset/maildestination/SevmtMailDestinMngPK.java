package nts.uk.ctx.sys.env.infra.entity.mailnoticeset.maildestination;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class SevmtMailDestinMngPK implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 会社ID
	 */
	@NotNull
	@Column(name = "CID")
	public String cid;
	
	/**
	 * 機能ID
	 */
	@NotNull
	@Column(name = "FUNCTION_ID")
	public int functionId;
}
