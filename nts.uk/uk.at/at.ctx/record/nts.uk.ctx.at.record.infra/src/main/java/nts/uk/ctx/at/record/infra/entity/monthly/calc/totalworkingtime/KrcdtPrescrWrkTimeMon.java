package nts.uk.ctx.at.record.infra.entity.monthly.calc.totalworkingtime;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.infra.entity.monthly.KrcdtMonAttendanceTime;
import nts.uk.ctx.at.record.infra.entity.monthly.KrcdtMonAttendanceTimePK;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

/**
 * 月別実績の所定労働時間
 * @author shuichu_ishida
 */
@Entity
@Table(name = "KRCDT_PRESCR_WRK_TIME_MON")
@NoArgsConstructor
@AllArgsConstructor
public class KrcdtPrescrWrkTimeMon extends UkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/** プライマリキー */
	@EmbeddedId
	public KrcdtMonAttendanceTimePK PK;
	
	/** 計画所定労働時間 */
	@Column(name = "SCHE_PRESCRIBED_WT")
	public int schedulePrescribedWorkingTime;
	
	/** 実績所定労働時間 */
	@Column(name = "RECD_PRESCRIBED_WT")
	public int recordPrescribedWorkingTime;

	/** マッチング：月別実績の勤怠時間 */
	@OneToOne
	@JoinColumns({
		@JoinColumn(name = "SID", referencedColumnName="KRCDT_MON_ATTENDANCE_TIME.SID", insertable = false, updatable = false),
		@JoinColumn(name = "START_YMD", referencedColumnName="KRCDT_MON_ATTENDANCE_TIME.START_YMD", insertable = false, updatable = false),
		@JoinColumn(name = "END_YMD", referencedColumnName="KRCDT_MON_ATTENDANCE_TIME.END_YMD", insertable = false, updatable = false)
	})
	public KrcdtMonAttendanceTime krcdtMonAttendanceTime;
	
	/**
	 * キー取得
	 */
	@Override
	protected Object getKey() {		
		return this.PK;
	}
}
