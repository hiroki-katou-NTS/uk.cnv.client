package nts.uk.ctx.at.request.ac.record.remainingnumber.annualleave;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.pub.remainnumber.annualleave.AnnLeaveRemainNumberPub;
import nts.uk.ctx.at.record.pub.remainnumber.annualleave.export.AnnualLeaveGrantExport;
import nts.uk.ctx.at.record.pub.remainnumber.annualleave.export.AnnualLeaveManageInforExport;
import nts.uk.ctx.at.record.pub.remainnumber.annualleave.export.AnnualLeaveRemainingNumberExport;
import nts.uk.ctx.at.record.pub.remainnumber.annualleave.export.ReNumAnnLeaReferenceDateExport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.annualleave.AnnLeaveRemainNumberAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.annualleave.AnnualLeaveGrantImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.annualleave.AnnualLeaveManageInforImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.annualleave.AnnualLeaveRemainingNumberImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.annualleave.ReNumAnnLeaReferenceDateImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.remainingnumber.annualleave.ReNumAnnLeaveImport;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class AnnLeaveRemainNumberImpl implements AnnLeaveRemainNumberAdapter {

	@Inject
	AnnLeaveRemainNumberPub annLeavePub;
	
	@Inject
	private WorkTypeRepository workTypeRepository;
	
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @Override
    public ReNumAnnLeaveImport getReferDateAnnualLeaveRemain(String employeeID, GeneralDate date) {
        ReNumAnnLeaReferenceDateExport reNum = this.annLeavePub.getReferDateAnnualLeaveRemainNumber(employeeID, date);
        return new ReNumAnnLeaveImport(
                reNum.getRemainingDays(), 
                reNum.getRemainingTime());
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public ReNumAnnLeaReferenceDateImport getReferDateAnnualLeaveRemainNumber(String employeeID, GeneralDate date) {
		ReNumAnnLeaReferenceDateExport reNum = this.annLeavePub.getReferDateAnnualLeaveRemainNumber(employeeID, date);
		return new ReNumAnnLeaReferenceDateImport(
											mapNumber(reNum.getAnnualLeaveRemainNumberExport().getRemainNumberWithMinusExport()),
											mapLeaveGrants(reNum.getAnnualLeaveGrantExports()),
											mapManageInfors(reNum.getAnnualLeaveManageInforExports()));
	}

	private List<AnnualLeaveManageInforImport> mapManageInfors(
			List<AnnualLeaveManageInforExport> infors) {
		String companyID = AppContexts.user().companyId();
		if(infors == null){
			return	Collections.emptyList();
		}
		
		// đối ứng bug #109638: thêm hiển thị workType cho KDL020
		List<String> workTypeCDLst = infors.stream().map(x -> x.getWorkTypeCode()).collect(Collectors.toList());
		
		// request list 407
		Map<String , String> workTypeInfoMap = workTypeRepository.getCodeNameWorkType(companyID, workTypeCDLst);
		
		return	infors.stream().map(x->  new AnnualLeaveManageInforImport(
									x.getYmd(),
									x.getDaysUsedNo(),
									x.getUsedMinutes(),
									x.getScheduleRecordAtr(),
									x.getWorkTypeCode(),
									workTypeInfoMap.get(x.getWorkTypeCode())
								)).collect(Collectors.toList());
	}

	private List<AnnualLeaveGrantImport> mapLeaveGrants(List<AnnualLeaveGrantExport> grants) {
		if(grants== null){
			return	Collections.emptyList();
		}
		return grants.stream().map(x-> new AnnualLeaveGrantImport(
											x.getGrantDate(), 
											x.getGrantNumber(), 
											x.getDaysUsedNo(), 
											x.getUsedMinutes(), 
											x.getRemainDays(), 
											x.getRemainMinutes(), 
											x.getDeadline())).collect(Collectors.toList());
	}

	private AnnualLeaveRemainingNumberImport mapNumber(AnnualLeaveRemainingNumberExport number) {
		if (number == null) {
			return null;
		}
		
		return new AnnualLeaveRemainingNumberImport(number.getAnnualLeaveGrantPreDay(), 
				number.getAnnualLeaveGrantPreTime(), 
				number.getNumberOfRemainGrantPre(),
				number.getTimeAnnualLeaveGrantPre(), 
				number.getAnnualLeaveGrantPostDay(), 
				number.getAnnualLeaveGrantPostTime(),
				number.getNumberOfRemainGrantPost(), 
				number.getAnnualLeaveGrantPostTime());
	}

}
