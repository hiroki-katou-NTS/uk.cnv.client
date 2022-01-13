package nts.uk.ctx.sys.auth.infra.entity.wkpmanager;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.sys.auth.dom.wkpmanager.WorkplaceManager;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.shr.infra.data.entity.ContractUkJpaEntity;

@NoArgsConstructor
@Entity
@Table(name = "SACMT_WKP_MANAGER")
public class SacmtWkpManager extends ContractUkJpaEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@EmbeddedId
	public SacmtWorkplaceManagerPK  kacmtWorkplaceManagerPK;
	
	@Basic(optional = false)
	@Column(name = "SID")
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

	public SacmtWkpManager(SacmtWorkplaceManagerPK kacmtWorkplaceManagerPK, String employeeId, String workplaceId,
			GeneralDate startDate, GeneralDate endDate) {
		super();
		this.kacmtWorkplaceManagerPK = kacmtWorkplaceManagerPK;
		this.employeeId = employeeId;
		this.workplaceId = workplaceId;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	/**
	 * Convert domain to entity
	 * @param domain
	 * @return SacmtWkpManager object
	 */
	public static SacmtWkpManager toEntity(WorkplaceManager domain) {
		return new SacmtWkpManager(
					new SacmtWorkplaceManagerPK(domain.getWorkplaceManagerId()),
					domain.getEmployeeId(),
					domain.getWorkplaceId(),
					domain.getHistoryPeriod().start(),
					domain.getHistoryPeriod().end()
				);
	}
	/**
	 * Convert entity to domain
	 * @return WorkplaceManager object
	 */
	public WorkplaceManager toDomain() {
		return new WorkplaceManager(
				this.kacmtWorkplaceManagerPK.workplaceManagerId,
				this.workplaceId,
				this.employeeId,
				new DatePeriod(this.startDate, this.endDate)
				);
	}
}
