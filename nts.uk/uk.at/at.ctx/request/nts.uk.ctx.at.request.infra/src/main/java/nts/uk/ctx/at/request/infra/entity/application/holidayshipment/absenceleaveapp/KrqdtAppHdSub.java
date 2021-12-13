package nts.uk.ctx.at.request.infra.entity.application.holidayshipment.absenceleaveapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.commons.lang3.BooleanUtils;

import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.holidayshipment.TypeApplicationHolidays;
import nts.uk.ctx.at.request.dom.application.holidayshipment.absenceleaveapp.AbsenceLeaveApp;
import nts.uk.ctx.at.request.infra.entity.application.holidayshipment.KrqdtAppRecAbsPK;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.common.TimeZoneWithWorkNo;
import nts.uk.ctx.at.shared.dom.worktime.predset.WorkNo;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

/**
 * 振休申請
 * @author ThanhPV
 */
@Entity
@Table(name = "KRQDT_APP_HD_SUB")
@NoArgsConstructor
public class KrqdtAppHdSub extends ContractUkJpaEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	public KrqdtAppRecAbsPK pK;
	
	@Column(name = "WORK_TYPE_CD")
	public String workTypeCd;
	
	@Column(name = "WORK_CHANGE_ATR")
	public boolean workChangeAtr;
	
	@Column(name = "WORK_TIME_CD")
	public String workTimeCd;
	
	@Column(name = "WORK_TIME_START1")
	public Integer workTimeStart1;
	
	@Column(name = "WORK_TIME_END1")
	public Integer workTimeEnd1;
	
	@Column(name = "WORK_TIME_START2")
	public Integer workTimeStart2;
	
	@Column(name = "WORK_TIME_END2")
	public Integer workTimeEnd2;
	
	@Column(name = "HDSUB_DATE")
	public GeneralDate hdsubDate;

	@Override
	protected Object getKey() {
		return pK;
	}
	
	@PrePersist
    private void setInsertingContractInfo() {
		this.contractCd = AppContexts.user().contractCode();
	}
	
	@PreUpdate
    private void setUpdateContractInfo() {
		this.contractCd = AppContexts.user().contractCode();
	}
	
	public KrqdtAppHdSub(AbsenceLeaveApp domain) {
		super();
		this.pK = new KrqdtAppRecAbsPK(AppContexts.user().companyId(), domain.getAppID());
		this.workTypeCd = domain.getWorkInformation().getWorkTypeCode().v();
		this.workChangeAtr = BooleanUtils.toBoolean(domain.getWorkChangeUse().value);
		if(domain.getWorkInformation().getWorkTimeCodeNotNull().isPresent()) {
			this.workTimeCd = domain.getWorkInformation().getWorkTimeCodeNotNull().get().v();
			this.workTimeStart1 = domain.getWorkTime(new WorkNo(1)).map(c-> c.getTimeZone().getStartTime().v()).orElse(null);
			this.workTimeEnd1 = domain.getWorkTime(new WorkNo(1)).map(c-> c.getTimeZone().getEndTime().v()).orElse(null);
			this.workTimeStart2 = domain.getWorkTime(new WorkNo(2)).isPresent() ? domain.getWorkTime(new WorkNo(2)).get().getTimeZone().getStartTime().v():null;
			this.workTimeEnd2 = domain.getWorkTime(new WorkNo(2)).isPresent() ? domain.getWorkTime(new WorkNo(2)).get().getTimeZone().getEndTime().v():null;
		}
		this.hdsubDate = domain.getChangeSourceHoliday().orElse(null);
	}
	
	public AbsenceLeaveApp toDomain(Application application) {
		return new AbsenceLeaveApp(
				this.getWorkingHours(),
				new WorkInformation(this.workTypeCd, this.workTimeCd), 
				NotUseAtr.valueOf(this.workChangeAtr),
				Optional.ofNullable(this.hdsubDate),
				TypeApplicationHolidays.Abs,
				application);
	}
	
	private List<TimeZoneWithWorkNo> getWorkingHours() {
		List<TimeZoneWithWorkNo> result = new ArrayList<>(); 
		if(this.workTimeStart1 != null && this.workTimeEnd1 != null) {
			result.add(new TimeZoneWithWorkNo(1, this.workTimeStart1, this.workTimeEnd1));
		}
		if(this.workTimeStart2 != null && this.workTimeEnd2 != null) {
			result.add(new TimeZoneWithWorkNo(2, this.workTimeStart2, this.workTimeEnd2));
		}
		return result;
	}

}