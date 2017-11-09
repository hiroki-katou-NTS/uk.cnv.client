package nts.uk.ctx.bs.employee.infra.entity.temporaryAbsence;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.shr.infra.data.entity.UkJpaEntity;
@AllArgsConstructor
@Entity
@Table(name = "BSYMT_TEMPORARY_ABSENCE")
public class BsymtTemporaryAbsence extends UkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/** The BsymtTemporaryAbsencePK. */
	@EmbeddedId
	public BsymtTemporaryAbsencePK bsymtTemporaryAbsencePK;

	@Basic(optional = false)
	@Column(name="SID")
	public String sid;

	@Basic(optional = false)
	@Column(name = "START_DATE")
	public GeneralDate startDate;

	@Basic(optional = false)
	@Column(name = "END_DATE")
	public GeneralDate endDate;

	@Basic(optional = false)
	@Column(name = "LEAVE_HOLIDAY_ATR")
	public int leaveHolidayAtr;

	@Basic(optional = false)
	@Column(name = "REASON")
	public String reason;

	@Basic(optional = false)
	@Column(name = "FAMILY_MEMBER_ID")
	public String familyMemberId;

	@Basic(optional = false)
	@Column(name = "BIRTHDAY")
	public GeneralDate birthday;

	@Basic(optional = false)
	@Column(name = "MULTIPLE")
	public int multiple;

	@Override
	protected Object getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	public BsymtTemporaryAbsence() {
		super();
	}

}
