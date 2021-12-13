package nts.uk.ctx.at.shared.infra.entity.monthlyattendanceitemused;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nts.uk.ctx.at.shared.dom.monthlyattditem.FormCanUsedForTime;
import nts.uk.ctx.at.shared.dom.monthlyattditem.MonthlyAttendanceItemUsed;
import nts.uk.shr.infra.data.entity.UkJpaEntity;

@Data
@Entity
@Table(name = "KFNCT_ATD_ID_RPT_MON")
@EqualsAndHashCode(callSuper = true)
public class KfnctAtdIdRptMon extends UkJpaEntity implements MonthlyAttendanceItemUsed.MementoGetter, MonthlyAttendanceItemUsed.MementoSetter, Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private KfnctAtdIdRptMonPK kfnctAtdIdRptMonPK;
	
	@Column(name = "EXCLUS_VER")
	private String exclusVer;
	
	@Column(name = "CONTRACT_CD")
	private String contractCd;
	
	@Column(name = "WORK_ATTENDANCE")
	private boolean workAttendance;
	
	@Column(name = "WORK_MONTHLY")
	private boolean workMonthly;
	
	@Column(name = "WORK_YEARLY_ATD")
	private boolean workYearlyAtd;
	
	@Column(name = "WORK_PERIOD")
	private boolean workPeriod;
	
	@Column(name = "ATD_WORK_ATTENDANCE")
	private boolean atdWorkAttendance;
	
	@Column(name = "ATD_WORK_YEARLY")
	private boolean atdWorkYearly;

	@Column(name = "WORK_YEARLY_36")
	private boolean workYearly36;
	
	
	@Override
	protected Object getKey() {
		return this.kfnctAtdIdRptMonPK;
	}
	
	@Override
	public void setCompanyId(String companyId) {
		if (this.kfnctAtdIdRptMonPK == null) {
			this.kfnctAtdIdRptMonPK = new KfnctAtdIdRptMonPK();
		}
		this.kfnctAtdIdRptMonPK.setCompanyId(companyId);
	}

	@Override
	public void setAttendanceItemId(int attendanceItemId) {
		if (this.kfnctAtdIdRptMonPK == null) {
			this.kfnctAtdIdRptMonPK = new KfnctAtdIdRptMonPK();
		}
		this.kfnctAtdIdRptMonPK.setAttendanceItemId(attendanceItemId);
	}

	@Override
	public void setFormCanUsedForTimes(List<FormCanUsedForTime> forms) {
		this.setWorkAttendance(false);
		this.setWorkMonthly(false);
		this.setWorkYearlyAtd(false);
		this.setWorkPeriod(false);
		this.setAtdWorkAttendance(false);
		this.setAtdWorkYearly(false);
		this.setWorkYearly36(false);
		for (FormCanUsedForTime form : forms) {
			switch (form) {
				case ATTENDANCE_BOOK:
					this.setWorkAttendance(true);
					break;
				case MONTHLY_WORK_SCHEDULE:
					this.setWorkMonthly(true);
					break;
				case ANNUAL_WORK_SCHEDULE:
					this.setWorkYearlyAtd(true);
					break;
				case OPTIONAL_PERIOD_SCHEDULE:
					this.setWorkPeriod(true);
					break;
				case ANNUAL_WORK_LEDGER:
					this.setAtdWorkAttendance(true);
					break;
				case WORKBOOK:
					this.setAtdWorkYearly(true);
					break;
				case ANNUAL_ROSTER_36_AGREEMENT:
					this.setWorkYearly36(true);
					break;
				default:
					break;
			}
		}
	}

	@Override
	public String getCompanyId() {
		return this.kfnctAtdIdRptMonPK.getCompanyId();
	}

	@Override
	public int getAttendanceItemId() {
		return this.kfnctAtdIdRptMonPK.getAttendanceItemId();
	}

	@Override
	public List<FormCanUsedForTime> getFormCanUsedForTimes() {
		List<FormCanUsedForTime> canUsedForTimes = new ArrayList<FormCanUsedForTime>();
		if (this.workAttendance) {
			canUsedForTimes.add(FormCanUsedForTime.ATTENDANCE_BOOK);
		}

		if (this.workMonthly) {
			canUsedForTimes.add(FormCanUsedForTime.MONTHLY_WORK_SCHEDULE);
		}

		if (this.workYearlyAtd) {
			canUsedForTimes.add(FormCanUsedForTime.ANNUAL_WORK_SCHEDULE);
		}

		if (this.workPeriod) {
			canUsedForTimes.add(FormCanUsedForTime.OPTIONAL_PERIOD_SCHEDULE);
		}

		if (this.atdWorkAttendance) {
			canUsedForTimes.add(FormCanUsedForTime.ANNUAL_WORK_LEDGER);
		}

		if (this.atdWorkYearly) {
			canUsedForTimes.add(FormCanUsedForTime.WORKBOOK);
		}

		if (this.workYearly36) {
			canUsedForTimes.add(FormCanUsedForTime.ANNUAL_ROSTER_36_AGREEMENT);
		}

		return canUsedForTimes;
	}
	
	

}