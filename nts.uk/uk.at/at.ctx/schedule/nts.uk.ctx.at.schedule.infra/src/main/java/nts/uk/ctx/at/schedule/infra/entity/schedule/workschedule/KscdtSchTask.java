package nts.uk.ctx.at.schedule.infra.entity.schedule.workschedule;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.schedule.dom.schedule.task.taskschedule.TaskScheduleDetail;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@Entity
@NoArgsConstructor
@Table(name="KSCDT_SCH_TASK")
@Getter
public class KscdtSchTask extends ContractUkJpaEntity {
	
	@EmbeddedId
	public KscdtSchTaskPK pk;
	
	/** 作業コード **/
	@Column(name = "TASK_CODE")
	public String taskCode;
	
	/** 開始時刻 **/
	@Column(name = "START_CLOCK")
	public int startClock;
	
	/** 終了時刻 **/
	@Column(name = "END_CLOCK")
	public int endClock;
	
	@Column(name = "CID")
	public String cid;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@PrimaryKeyJoinColumns({ @PrimaryKeyJoinColumn(name = "SID", referencedColumnName = "SID"),
			@PrimaryKeyJoinColumn(name = "YMD", referencedColumnName = "YMD") })
	public KscdtSchTime kscdtSchTime;

	@Override
	protected Object getKey() {
		return pk;
	}
	
	public static KscdtSchTask toEntity(String sid, GeneralDate ymd, String cid, TaskScheduleDetail taskDetail, int index) {
		KscdtSchTask schTask = new KscdtSchTask(new KscdtSchTaskPK(sid, ymd, index), taskDetail.getTaskCode().v(),
				taskDetail.getTimeSpan().getStart().v().intValue(), taskDetail.getTimeSpan().getEnd().v().intValue(), cid);
		return schTask;
	}

	public KscdtSchTask(KscdtSchTaskPK pk, String taskCode, int startClock, int endClock, String cid) {
		super();
		this.pk = pk;
		this.taskCode = taskCode;
		this.startClock = startClock;
		this.endClock = endClock;
		this.cid = cid;
	}
}
