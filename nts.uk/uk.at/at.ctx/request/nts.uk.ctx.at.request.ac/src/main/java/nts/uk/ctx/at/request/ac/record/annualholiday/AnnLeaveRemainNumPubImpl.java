package nts.uk.ctx.at.request.ac.record.annualholiday;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.pub.remainnumber.annualleave.AnnLeaveRemainNumberPub;
import nts.uk.ctx.at.request.dom.application.annualholiday.AnnLeaveRemainAdapter;
import nts.uk.ctx.at.request.dom.application.annualholiday.AnnualLeaveGrantExport;
import nts.uk.ctx.at.request.dom.application.annualholiday.AnnualLeaveInfoExport;
import nts.uk.ctx.at.request.dom.application.annualholiday.AnnualLeaveMaxDataExport;
import nts.uk.ctx.at.request.dom.application.annualholiday.AnnualLeaveRemainingNumberExport;
import nts.uk.ctx.at.request.dom.application.annualholiday.ReNumAnnLeaReferenceDateExport;

@Stateless
public class AnnLeaveRemainNumPubImpl implements AnnLeaveRemainAdapter{
	
	@Inject
	private AnnLeaveRemainNumberPub pub;
	
	@Override
	public ReNumAnnLeaReferenceDateExport getReferDateAnnualLeaveRemainNumber(String employeeID, GeneralDate date) {
		val result = pub.getReferDateAnnualLeaveRemainNumber(employeeID, date);
		AnnualLeaveRemainingNumberExport remainNumberNoMinusExport = new AnnualLeaveRemainingNumberExport( 
				result.getAnnualLeaveRemainNumberExport().getRemainNumberNoMinusExport().getAnnualLeaveGrantPreTime(), 
				result.getAnnualLeaveRemainNumberExport().getRemainNumberNoMinusExport().getAnnualLeaveGrantPostTime());
		
		AnnualLeaveMaxDataExport annualLeaveMaxDataExport = new AnnualLeaveMaxDataExport(result.getAnnualLeaveRemainNumberExport().
				getAnnualLeaveMaxDataExport().getTimeAnnualLeaveMaxremainingMinutes());
				
		AnnualLeaveInfoExport annualLeaveRemainNumberExport = new AnnualLeaveInfoExport(
				result.getAnnualLeaveRemainNumberExport().getYmd(), 
				remainNumberNoMinusExport,
				annualLeaveMaxDataExport);
		
		List<AnnualLeaveGrantExport> annualLeaveGrantExports = result.getAnnualLeaveGrantExports().stream()
				.map(x -> {
					return new AnnualLeaveGrantExport(x.getLeaveID(), x.getEmployeeId(), x.getGrantDate(), 
							x.getDeadline(), x.getExpirationStatus(), x.getRegisterType(), x.getGrantNumber(), 
							x.getGrantNumberMinutes(), x.getDaysUsedNo(), x.getUsedMinutes(), x.getStowageDays(), 
							x.getLeaveOverLimitNumberOverDays(), x.getLeaveOverLimitNumberOverTimes(), 
							x.getRemainDays(), x.getRemainMinutes());
				}).collect(Collectors.toList());
		
		ReNumAnnLeaReferenceDateExport export = new ReNumAnnLeaReferenceDateExport(
				annualLeaveRemainNumberExport, 
				annualLeaveGrantExports, 
				result.getRemainingDays(), 
				result.getRemainingTime());
		return export;
	}

}
