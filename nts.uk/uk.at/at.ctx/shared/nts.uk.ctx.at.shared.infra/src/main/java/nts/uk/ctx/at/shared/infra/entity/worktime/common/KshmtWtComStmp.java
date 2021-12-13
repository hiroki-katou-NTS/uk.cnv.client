package nts.uk.ctx.at.shared.infra.entity.worktime.common;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@Getter
@Setter
@Entity
@Table(name = "KSHMT_WT_COM_STMP")
public class KshmtWtComStmp extends ContractUkJpaEntity implements Serializable{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The kshmt wt com stmp PK. */
	@EmbeddedId
	protected KshmtWtComStmpPK kshmtWtComStmpPK;

	/** The piority atr attendance. */
	@Column(name = "PIORITY_ATR_ATTENDANCE")
	private boolean piorityAtrAttendance;

	/** The piority atr leave. */
	@Column(name = "PIORITY_ATR_LEAVE")
	private boolean piorityAtrLeave;

	/** The piority atr attendance gate. */
	@Column(name = "PIORITY_ATR_ATTENDANCE_GATE")
	private boolean piorityAtrAttendanceGate;

	/** The piority atr leave gate. */
	@Column(name = "PIORITY_ATR_LEAVE_GATE")
	private boolean piorityAtrLeaveGate;

	/** The piority atr log out. */
	@Column(name = "PIORITY_ATR_LOGON")
	private boolean piorityAtrLogOn;

	/** The piority atr log off. */
	@Column(name = "PIORITY_ATR_LOGOFF")
	private boolean piorityAtrLogOff;

	/** The attendance minute later*/
	@Column(name = "ATTENDANCE_MINUTE_LATER")
	private int attendanceMinuteLater;

	/** The leave minute ago*/
	@Column(name = "LEAVE_MINUTE_AGO")
	private int leaveMinuteAgo;

	/** The front rear atr attendance */
	@Column(name = "FRONT_REAR_ATR_ATTENDANCE")
	private boolean frontRearAtrAttendance;

	/** The rounding time unit attendance */
	@Column(name = "ROUNDING_TIME_UNIT_ATTENDANCE")
	private int roundingTimeUnitAttendance;

	/** The front rear atr leave */
	@Column(name = "FRONT_REAR_ATR_LEAVE")
	private boolean frontRearAtrLeave;

	/** The rounding time unit leave */
	@Column(name = "ROUNDING_TIME_UNIT_LEAVE")
	private int roundingTimeUnitLeave;

	/** The front rear atr goout */
	@Column(name = "FRONT_REAR_ATR_GOOUT")
	private boolean frontRearAtrGoout;

	/** The rounding time unit goout */
	@Column(name = "ROUNDING_TIME_UNIT_GOOUT")
	private int roundingTimeUnitGoout;

	/** The front rear atr turnback */
	@Column(name = "FRONT_REAR_ATR_TURNBACK")
	private boolean frontRearAtrTurnback;

	/** The rounding time unit turnback */
	@Column(name = "ROUNDING_TIME_UNIT_TURNBACK")
	private int roundingTimeUnitTurnback;


	/**
	 * Instantiates a new kshmt wt com stmp.
	 */
	public KshmtWtComStmp() {
		super();
	}

	/**
	 *
	 * @param kshmtWtComStmpPK
	 */
	public KshmtWtComStmp(KshmtWtComStmpPK kshmtWtComStmpPK) {
		super();
		this.kshmtWtComStmpPK = kshmtWtComStmpPK;
	}


	/*
	 *
	 *
	 *(非 Javadoc)
	 * @see nts.arc.layer.infra.data.entity.JpaEntity#getKey()
	 */
	@Override
	protected Object getKey() {
		return this.kshmtWtComStmpPK;
	}
}
