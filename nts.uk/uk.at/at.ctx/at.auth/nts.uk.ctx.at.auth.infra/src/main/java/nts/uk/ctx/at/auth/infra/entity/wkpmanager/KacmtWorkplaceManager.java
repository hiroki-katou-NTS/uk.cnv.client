package nts.uk.ctx.at.auth.infra.entity.wkpmanager;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.auth.dom.wkpmanager.WorkplaceManager;
import nts.uk.shr.com.time.calendar.period.DatePeriod;
import nts.uk.shr.infra.data.entity.UkJpaEntity;



@NoArgsConstructor
@Entity
@Table(name = "KACMT_WORKPLACE_MANAGER")
public class KacmtWorkplaceManager extends UkJpaEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@EmbeddedId
	public KacmtWorkplaceManagerPK  kacmtWorkplaceManagerPK;
	
	@Basic(optional = false)
	@Column(name = "EMP_ID")
	public String employeeId;
	
	@Basic(optional = false)
	@Column(name = "WKP_ID")
	public String workplaceId;
	
	@Basic(optional = false)
	@Column(name = "START_DATE")
	public GeneralDate startDate;
	
	@Basic(optional = false)
	@Column(name = "END_DATE")
	public GeneralDate endDate;
	
	@Override
	protected Object getKey() {
		return this.kacmtWorkplaceManagerPK;
	}

	public KacmtWorkplaceManager(KacmtWorkplaceManagerPK kacmtWorkplaceManagerPK, String employeeId, String workplaceId,
			GeneralDate startDate, GeneralDate endDate) {
		super();
		this.kacmtWorkplaceManagerPK = kacmtWorkplaceManagerPK;
		this.employeeId = employeeId;
		this.workplaceId = workplaceId;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public static KacmtWorkplaceManager toEntity(WorkplaceManager domain) {
		return new  KacmtWorkplaceManager(
					new KacmtWorkplaceManagerPK(domain.getWorkplaceManagerId()),
					domain.getEmployeeId(),
					domain.getWorkplaceId(),
					domain.getHistoryPeriod().start(),
					domain.getHistoryPeriod().end()
				);
	}
	
	public WorkplaceManager toDomain() {
		return new WorkplaceManager(
				this.kacmtWorkplaceManagerPK.workplaceManagerId,
				this.employeeId,
				this.workplaceId,
				new DatePeriod(this.startDate, this.endDate)
				);
	}



}
