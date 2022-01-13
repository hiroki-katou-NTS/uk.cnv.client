package nts.uk.ctx.at.shared.infra.entity.worktime.filtercondition;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class KshmtWtSearchConditionPK implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 会社ID
	 */
	@NotNull
	@Column(name = "CID")
	public String cid;
	
	/**
	 * 絞り込み条件NO
	 */
	@NotNull
	@Column(name = "NO")
	public int no;
}
