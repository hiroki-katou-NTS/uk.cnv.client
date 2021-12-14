package nts.uk.ctx.at.request.ac.schedule.schedule.basicschedule;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.request.dom.application.common.adapter.schedule.schedule.basicschedule.BasicScheduleConfirmImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.schedule.schedule.basicschedule.BasicScheduleConfirmImport.ConfirmedAtrImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.schedule.schedule.basicschedule.BreakTimeSheetImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.schedule.schedule.basicschedule.ScBasicScheduleAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.schedule.schedule.basicschedule.ScBasicScheduleImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.schedule.schedule.basicschedule.ShortWorkingTimeSheetImport;
import nts.uk.ctx.at.schedule.pub.schedule.basicschedule.ScWorkScheduleExport_New;
import nts.uk.ctx.at.schedule.pub.schedule.workschedule.WorkSchedulePub;
/**
 * 
 * @author Doan Duy Hung
 *
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ScBasicScheduleAdapterImpl implements ScBasicScheduleAdapter {
	
	@Inject
	private WorkSchedulePub workSchedulePub;

//	private ScBasicScheduleImport_Old convertTo(ScBasicScheduleExport x) {
//		return new ScBasicScheduleImport_Old(
//				x.getEmployeeId(), 
//				x.getDate(), 
//				x.getWorkTypeCode(), 
//				x.getWorkTimeCode(), 
//				x.getWorkScheduleTimeZones().stream().map(y -> new WorkScheduleTimeZoneImport(
//						y.getScheduleCnt(), 
//						y.getScheduleStartClock(), 
//						y.getScheduleEndClock(), 
//						y.getBounceAtr()))
//				.collect(Collectors.toList()));
//	}

	@Override
	public ScBasicScheduleImport findByIDRefactor(String employeeID, GeneralDate date) {
		ScWorkScheduleExport_New scWorkScheduleExport = workSchedulePub.findByIdNewV2(employeeID, date).orElse(null);
		return fromExport(scWorkScheduleExport);
	}
	
	private ScBasicScheduleImport fromExport(ScWorkScheduleExport_New scWorkScheduleExport) {
		if (scWorkScheduleExport == null) return null;
		return new ScBasicScheduleImport(
				scWorkScheduleExport.getEmployeeId(), 
				scWorkScheduleExport.getDate(), 
				scWorkScheduleExport.getWorkTypeCode(), 
				scWorkScheduleExport.getWorkTimeCode(), 
				scWorkScheduleExport.getScheduleStartClock1(), 
				scWorkScheduleExport.getScheduleEndClock1(), 
				scWorkScheduleExport.getScheduleStartClock2(), 
				scWorkScheduleExport.getScheduleEndClock2(), 
				scWorkScheduleExport.getChildTime(), 
				scWorkScheduleExport.getListShortWorkingTimeSheetExport().stream().map(x -> new ShortWorkingTimeSheetImport(
						x.getShortWorkTimeFrameNo(), 
						x.getChildCareAttr(), 
						x.getStartTime(), 
						x.getEndTime())).collect(Collectors.toList()), 
				scWorkScheduleExport.getListBreakTimeSheetExports().stream().map(x -> new BreakTimeSheetImport(
				        x.getBreakFrameNo(), 
				        x.getStartTime(), 
				        x.getEndTime(), 
				        x.getBreakTime())).collect(Collectors.toList()));
	}

	@Override
	public List<BasicScheduleConfirmImport> findConfirmById(List<String> employeeID, DatePeriod date) {
		return workSchedulePub
				.findConfirmById(employeeID, date).stream().map(x -> new BasicScheduleConfirmImport(x.getEmployeeId(),
						x.getDate(), ConfirmedAtrImport.valueOf(x.getConfirmedAtr().value)))
				.collect(Collectors.toList());
	}

}
