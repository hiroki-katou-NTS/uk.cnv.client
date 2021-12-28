package nts.uk.ctx.at.record.infra.entity.jobmanagement.manhourinput;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourinput.AttendanceByTimezoneDeletion;
import nts.uk.ctx.at.record.dom.jobmanagement.manhourinput.AttendanceDeletionStatusEnum;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.timesheet.ouen.SupportFrameNo;
import nts.uk.shr.infra.data.entity.ContractCompanyUkJpaEntity;

/**
 * 時間帯別勤怠の削除
 * 
 * @author tutt
 *
 */
@NoArgsConstructor
@Entity
@Table(name = "KRCDT_DAY_SUP_DELETE")
public class KrcdtDaySupDelete extends ContractCompanyUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KrcdtDaySupDeletePk pk;

	// 削除状態
	@Column(name = "STATUS")
	public int status;

	@Override
	protected Object getKey() {
		return this.pk;
	}

	public KrcdtDaySupDelete(KrcdtDaySupDeletePk pk, int status) {
		super();
		this.pk = pk;
		this.status = status;
	}

	public static KrcdtDaySupDelete toEntity(String sId, GeneralDate ymd, AttendanceByTimezoneDeletion deletion) {
		return new KrcdtDaySupDelete(new KrcdtDaySupDeletePk(sId, ymd, deletion.getSupportFrameNo().v()),
				deletion.getDeletionStatus().value);
	}

	public AttendanceByTimezoneDeletion toAttendance() {
		return new AttendanceByTimezoneDeletion(new SupportFrameNo(this.pk.supTaskNo),
				AttendanceDeletionStatusEnum.of(this.status));
	}

}
