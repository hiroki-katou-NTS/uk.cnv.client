package nts.uk.ctx.at.record.dom.divergence.time.history;

import java.util.Date;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.dailyperformanceformat.businesstype.BusinessTypeOfEmployee;
import nts.uk.ctx.at.record.dom.dailyperformanceformat.businesstype.repository.BusinessTypeEmpService;
import nts.uk.ctx.at.record.dom.divergence.time.JudgmentResult;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.history.DateHistoryItem;

/**
 * The Class CompanyDivergenceReferenceTimeServiceIml.
 */
public class CompanyDivergenceReferenceTimeServiceIml implements CompanyDivergenceReferenceTimeService {

	/** The div reference time usage unit repo. */
	@Inject
	DivergenceReferenceTimeUsageUnitRepository divReferenceTimeUsageUnitRepo;

	/** The work type divergence ref time hist repo. */
	@Inject
	WorkTypeDivergenceReferenceTimeHistoryRepository workTypeDivergenceRefTimeHistRepo;

	/** The work type div ref time repo. */
	@Inject
	WorkTypeDivergenceReferenceTimeRepository workTypeDivRefTimeRepo;

	/** The type em service. */
	@Inject
	BusinessTypeEmpService typeEmService;

	/** The com div ref time hist repo. */
	@Inject
	CompanyDivergenceReferenceTimeHistoryRepository comDivRefTimeHistRepo;

	/** The com div ref time repo. */
	@Inject
	CompanyDivergenceReferenceTimeRepository comDivRefTimeRepo;

	/* (non-Javadoc)
	 * @see nts.uk.ctx.at.record.dom.divergence.time.history.CompanyDivergenceReferenceTimeService#CheckDivergenceTime(java.lang.String, java.lang.String, nts.arc.time.GeneralDate, int, nts.uk.ctx.at.shared.dom.common.time.AttendanceTime)
	 */
	@Override
	public JudgmentResultDetermineRefTime CheckDivergenceTime(String userId, String companyId, GeneralDate processDate,
			int divergenceTimeNo, AttendanceTime DivergenceTimeOccurred) {

		JudgmentResultDetermineRefTime judgmentResultDetermineRefTime = new JudgmentResultDetermineRefTime();

		//get DivergenceReferenceTimeUsageUnit
		Optional<DivergenceReferenceTimeUsageUnit> optionalDivReferenceTimeUsageUnit = divReferenceTimeUsageUnitRepo
				.findByCompanyId(companyId);
		if (optionalDivReferenceTimeUsageUnit.isPresent()
				&& optionalDivReferenceTimeUsageUnit.get().isWorkTypeUseSet()) {

			DetermineReferenceTime determineRefTime = new DetermineReferenceTime();
			determineRefTime.setReferenceTime(ReferenceTime.WORKTYPE);
			JudgmentResult result = JudgmentResult.ERROR;
			
			//get BusinessTypeOfEmployee
			BusinessTypeOfEmployee typeofEmployee = typeEmService.getData(userId, processDate).get(0);

			//get workTypeDivRefTimeHist
			WorkTypeDivergenceReferenceTimeHistory workTypeDivRefTimeHist = workTypeDivergenceRefTimeHistRepo
					.findByKey(typeofEmployee.getHistoryId());

			//get DateHistoryItem
			DateHistoryItem dateHistItem = workTypeDivRefTimeHist.getHistoryItems().stream()
					.filter(item -> item.start().before(processDate) && item.end().after(processDate)).findFirst()
					.get();

			//check DateHistoryItem is null
			if (dateHistItem != null) {
				//get optionalWorkTypeDivRefTime
				Optional<WorkTypeDivergenceReferenceTime> optionalWorkTypeDivRefTime = workTypeDivRefTimeRepo.findByKey(
						typeofEmployee.getHistoryId(), workTypeDivRefTimeHist.getWorkTypeCode(), divergenceTimeNo);
				
				if (optionalWorkTypeDivRefTime.isPresent()) {
					WorkTypeDivergenceReferenceTime workTypeDivRefTime = optionalWorkTypeDivRefTime.get();
					
					//check getNotUseAtr
					if (workTypeDivRefTime.getNotUseAtr() == NotUseAtr.USE) {

						//check AlarmTime
						if (workTypeDivRefTime.getDivergenceReferenceTimeValue().get().getAlarmTime().get()
								.greaterThan(DivergenceTimeOccurred)
								|| workTypeDivRefTime.getDivergenceReferenceTimeValue().get().getAlarmTime()
										.get() == new DivergenceReferenceTime(0)) {
							//set judgment result
							result = JudgmentResult.NORMAL;
						} else {
							//set judgment result
							result = JudgmentResult.ERROR;
							//set determineRefTime.Threshold
							determineRefTime.setThreshold(
									workTypeDivRefTime.getDivergenceReferenceTimeValue().get().getAlarmTime().get());
						}
						
						//check ErrorTime
						if (workTypeDivRefTime.getDivergenceReferenceTimeValue().get().getErrorTime().get()
								.greaterThan(DivergenceTimeOccurred)
								|| workTypeDivRefTime.getDivergenceReferenceTimeValue().get().getErrorTime()
										.get() == new DivergenceReferenceTime(0)) {
							//set judgment result
							result = JudgmentResult.NORMAL;
						} else {
							//set judgment result
							result = JudgmentResult.ERROR;
							//set determineRefTime.Threshold
							determineRefTime.setThreshold(
									workTypeDivRefTime.getDivergenceReferenceTimeValue().get().getErrorTime().get());
						}

					} else {
						//set judgment result
						result = JudgmentResult.NORMAL;
					}
				}

			} else {
				//get companyDivergenceReferenceTimeHistory
				CompanyDivergenceReferenceTimeHistory companyDivergenceReferenceTimeHistory = comDivRefTimeHistRepo
						.findByHistId(typeofEmployee.getHistoryId());
				//get dateHistItemcom
				DateHistoryItem dateHistItemcom = companyDivergenceReferenceTimeHistory.getHistoryItems().stream()
						.filter(item -> item.start().before(processDate) && item.end().after(processDate)).findFirst()
						.get();
				//check dateHistItemcom
				if (dateHistItemcom != null) {
					//get CompanyDivergenceReferenceTime
					Optional<CompanyDivergenceReferenceTime> optionalComDivRefTime = comDivRefTimeRepo
							.findByKey(typeofEmployee.getHistoryId(), divergenceTimeNo);

					if (optionalComDivRefTime.isPresent()) {
						CompanyDivergenceReferenceTime companyDivergenceReferenceTime = optionalComDivRefTime.get();
						//check NotUseAtr
						if (companyDivergenceReferenceTime.getNotUseAtr() == NotUseAtr.USE) {
							//check AlarmTime
							if (companyDivergenceReferenceTime.getDivergenceReferenceTimeValue().get().getAlarmTime()
									.get().greaterThan(DivergenceTimeOccurred)
									|| companyDivergenceReferenceTime.getDivergenceReferenceTimeValue().get()
											.getAlarmTime().get() == new DivergenceReferenceTime(0)) {
								result = JudgmentResult.NORMAL;
							} else {
								result = JudgmentResult.ERROR;
								determineRefTime.setThreshold(companyDivergenceReferenceTime
										.getDivergenceReferenceTimeValue().get().getAlarmTime().get());
							}
							//check ErrorTime
							if (companyDivergenceReferenceTime.getDivergenceReferenceTimeValue().get().getErrorTime()
									.get().greaterThan(DivergenceTimeOccurred)
									|| companyDivergenceReferenceTime.getDivergenceReferenceTimeValue().get()
											.getErrorTime().get() == new DivergenceReferenceTime(0)) {
								//set Judgment result
								result = JudgmentResult.NORMAL;
							} else {
								//set Judgment result
								result = JudgmentResult.ERROR;
								//set determineRefTime.Threshold
								determineRefTime.setThreshold(companyDivergenceReferenceTime
										.getDivergenceReferenceTimeValue().get().getErrorTime().get());
							}

						} else {
							//set Judgment result
							result = JudgmentResult.NORMAL;
						}
					}
				}

			}
			//set judgmentResultDetermineRefTime
			judgmentResultDetermineRefTime.setJudgmentResult(result);
			judgmentResultDetermineRefTime.setDetermineReafTime(determineRefTime);
			
		}
		if (!optionalDivReferenceTimeUsageUnit.isPresent()
				|| !optionalDivReferenceTimeUsageUnit.get().isWorkTypeUseSet()) {
			// Incase false or domain is not exist
		}
		return new JudgmentResultDetermineRefTime();
	}

}
