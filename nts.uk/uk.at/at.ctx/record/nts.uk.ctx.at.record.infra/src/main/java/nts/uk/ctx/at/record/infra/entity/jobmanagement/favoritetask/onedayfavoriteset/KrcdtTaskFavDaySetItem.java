package nts.uk.ctx.at.record.infra.entity.jobmanagement.favoritetask.onedayfavoriteset;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import nts.uk.shr.infra.data.entity.ContractCompanyUkJpaEntity;

/**
 * 
 * @author tutt
 * 1日お気に入り作業セット詳細
 */
@NoArgsConstructor
@Entity
@Table(name = "KRCDT_TASK_FAV_DAY_SET_ITEM")
public class KrcdtTaskFavDaySetItem extends ContractCompanyUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KrcdtTaskFavDaySetItemPk pk;
	
	@Column(name = "TASK_CD1")
	public String taskCd1;

	@Column(name = "TASK_CD2")
	public String taskCd2;

	@Column(name = "TASK_CD3")
	public String taskCd3;

	@Column(name = "TASK_CD4")
	public String taskCd4;

	@Column(name = "TASK_CD5")
	public String taskCd5;
	
	@Column(name = "TASK_TIME")
	public Integer taskTime;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumns({ @PrimaryKeyJoinColumn(name = "FAV_ID", referencedColumnName = "FAV_ID"),
		@PrimaryKeyJoinColumn(name = "START_CLOCK", referencedColumnName = "START_CLOCK") })
	public KrcdtTaskFavDaySetTs krcdtTaskFavDaySetTs;

	@Override
	protected Object getKey() {
		return this.pk;
	}

	public KrcdtTaskFavDaySetItem(KrcdtTaskFavDaySetItemPk krcdtTaskFavDaySetItemPk, String taskCd1, String taskCd2, String taskCd3, String taskCd4, String taskCd5, Integer taskTime) {
		super();
		this.pk = krcdtTaskFavDaySetItemPk;
		this.taskCd1 = taskCd1;
		this.taskCd2 = taskCd2;
		this.taskCd3 = taskCd3;
		this.taskCd4 = taskCd4;
		this.taskCd5 = taskCd5;
		this.taskTime = taskTime;
	}
	
}
