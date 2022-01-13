package nts.uk.ctx.at.schedule.infra.entity.schedule.task.taskpalette;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 	作業パレット
 * 
 * @author quytb
 *
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "KSCMT_TASK_PALETTE")
public class KscmtTaskPalette extends ContractUkJpaEntity implements Serializable {
	
	private static final long serialVersionUID = -1905672797988767426L;

	@EmbeddedId
	public KscmtTaskPalettePk kscmtTaskPalettePk;
	
	/** 名称*/
	@Column(name = "PAGE_NAME")
	public String pageName;
	
	/** 備考*/
	@Column(name = "NOTE")
	public String remark;

	@Override
	protected Object getKey() {
		return this.kscmtTaskPalettePk;
	}
}
