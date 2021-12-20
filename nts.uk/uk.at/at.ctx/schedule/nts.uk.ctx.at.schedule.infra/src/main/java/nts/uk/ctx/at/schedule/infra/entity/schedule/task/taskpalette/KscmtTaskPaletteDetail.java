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
 * 作業パレット-詳細
 * 
 * @author quytb
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "KSCMT_TASK_PALETTE_DETAIL")
public class KscmtTaskPaletteDetail extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 4367917866104929209L;
	
	@EmbeddedId
	public KscmtTaskPaletteDetailPk kscmtTaskPaletteDetailPk;
	
	/** 作業コード */
	@Column(name = "TASK_CODE")
	public String taskCode;

	@Override
	protected Object getKey() {
		return this.kscmtTaskPaletteDetailPk;
	}
	
	public String getTargetId() {
		return this.kscmtTaskPaletteDetailPk.targetId;
	}
}
