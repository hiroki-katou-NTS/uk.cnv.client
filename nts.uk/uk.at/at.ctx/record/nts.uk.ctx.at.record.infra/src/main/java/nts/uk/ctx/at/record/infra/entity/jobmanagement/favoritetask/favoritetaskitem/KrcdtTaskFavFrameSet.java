package nts.uk.ctx.at.record.infra.entity.jobmanagement.favoritetask.favoritetaskitem;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskItem;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskName;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.TaskContent;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.work.WorkCode;
import nts.uk.shr.infra.data.entity.ContractCompanyUkJpaEntity;

/**
 * @name お気に入り作業項目 FavoriteTaskItem
 * @author tutt
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "KRCDT_TASK_FAV_FRAME_SET")
public class KrcdtTaskFavFrameSet extends ContractCompanyUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "FAV_ID")
	public String favId;

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

	@Column(name = "FAV_NAME")
	public String favName;

	@Column(name = "SID")
	public String sId;

	@Override
	protected Object getKey() {
		return this.favId;
	}
	
	public KrcdtTaskFavFrameSet (FavoriteTaskItem domain) {
		this.favId = domain.getFavoriteId();
		this.sId = domain.getEmployeeId();
		this.favName = domain.getTaskName().v();
		this.taskCd1 = domain.getFavoriteContents().stream().filter(f -> f.getItemId() == 4).findAny().map(m -> m.getTaskCode().v().isEmpty() ? null : m.getTaskCode().v()).orElse(null);
		this.taskCd2 = domain.getFavoriteContents().stream().filter(f -> f.getItemId() == 5).findAny().map(m -> m.getTaskCode().v().isEmpty() ? null : m.getTaskCode().v()).orElse(null);
		this.taskCd3 = domain.getFavoriteContents().stream().filter(f -> f.getItemId() == 6).findAny().map(m -> m.getTaskCode().v().isEmpty() ? null : m.getTaskCode().v()).orElse(null);
		this.taskCd4 = domain.getFavoriteContents().stream().filter(f -> f.getItemId() == 7).findAny().map(m -> m.getTaskCode().v().isEmpty() ? null : m.getTaskCode().v()).orElse(null);
		this.taskCd5 = domain.getFavoriteContents().stream().filter(f -> f.getItemId() == 8).findAny().map(m -> m.getTaskCode().v().isEmpty() ? null : m.getTaskCode().v()).orElse(null);
	}
	
	public FavoriteTaskItem toDomain() {
		List<TaskContent> favoriteContents = new ArrayList<>();
		
		favoriteContents.add(new TaskContent(4, new WorkCode(this.taskCd1)));
		favoriteContents.add(new TaskContent(5, new WorkCode(this.taskCd2)));
		favoriteContents.add(new TaskContent(6, new WorkCode(this.taskCd3)));
		favoriteContents.add(new TaskContent(7, new WorkCode(this.taskCd4)));
		favoriteContents.add(new TaskContent(8, new WorkCode(this.taskCd5)));
		
		return new FavoriteTaskItem(this.sId, this.favId, new FavoriteTaskName(this.favName), favoriteContents);
	}

}
