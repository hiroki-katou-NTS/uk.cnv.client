package nts.uk.ctx.at.schedule.infra.entity.schedule.task.taskpalette;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class KscmtTaskPaletteDetailPk {
	
	/** 会社ID*/
	@Column(name = "CID")
	public String companyId;
	
	/** 対象組織の単位 */
	@Column(name = "TARGET_UNIT")
	public Integer targetUnit;

	/** 対象組織の単位に応じたID*/
	@Column(name = "TARGET_ID")
	public String targetId;

	/** ページ*/
	@Column(name = "PAGE")
	public Integer page;

	/** 位置番号*/
	@Column(name = "POSITION")
	public Integer position;
}
