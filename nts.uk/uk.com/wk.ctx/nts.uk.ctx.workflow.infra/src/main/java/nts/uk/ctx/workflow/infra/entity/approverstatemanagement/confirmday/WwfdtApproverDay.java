package nts.uk.ctx.workflow.infra.entity.approverstatemanagement.confirmday;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.workflow.dom.approverstatemanagement.ApproverState;
import nts.uk.shr.infra.data.entity.UkJpaEntity;
/**
 * 
 * @author Doan Duy Hung
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="WWFDT_APPROVER_DAY")
@Builder
public class WwfdtApproverDay extends UkJpaEntity {
	
	@EmbeddedId
	public WwfdpApproverDayPK wwfdpApproverDayPK;
	
	@Column(name="CID")
	public String companyID;
	
	@Column(name="APPROVAL_RECORD_DATE")
	public GeneralDate recordDate;
	
	@ManyToOne
	@PrimaryKeyJoinColumns({
		@PrimaryKeyJoinColumn(name="ROOT_STATE_ID",referencedColumnName="ROOT_STATE_ID"),
		@PrimaryKeyJoinColumn(name="PHASE_ORDER",referencedColumnName="PHASE_ORDER"),
		@PrimaryKeyJoinColumn(name="FRAME_ORDER",referencedColumnName="FRAME_ORDER")
	})
	private WwfdtApprovalFrameDay wwfdtApprovalFrameDay;

	@Override
	protected Object getKey() {
		return wwfdpApproverDayPK;
	}
	
	public static WwfdtApproverDay fromDomain(String companyID, GeneralDate date, ApproverState approverState){
		return WwfdtApproverDay.builder()
				.wwfdpApproverDayPK(
						new WwfdpApproverDayPK(
								approverState.getRootStateID(), 
								approverState.getPhaseOrder(), 
								approverState.getFrameOrder(), 
								approverState.getApproverID()))
				.companyID(companyID)
				.recordDate(date)
				.build();
	}
	
	public ApproverState toDomain(){
		return ApproverState.builder()
				.rootStateID(this.wwfdpApproverDayPK.rootStateID)
				.phaseOrder(this.wwfdpApproverDayPK.phaseOrder)
				.frameOrder(this.wwfdpApproverDayPK.frameOrder)
				.approverID(this.wwfdpApproverDayPK.approverID)
				.build();
	}
	
}
