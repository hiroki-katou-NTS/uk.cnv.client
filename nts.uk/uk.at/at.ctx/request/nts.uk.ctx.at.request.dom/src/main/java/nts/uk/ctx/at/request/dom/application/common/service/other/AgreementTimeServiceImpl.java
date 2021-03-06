package nts.uk.ctx.at.request.dom.application.common.service.other;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.EmployeeRequestAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.SEmpHistImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.agreement.AgreementTimeAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.agreement.AgreementTimeImport_Old;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.agreement.AgreementTimeOfManagePeriod;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.standardtime.AgreementMonthSettingAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.record.standardtime.AgreementMonthSettingOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.AgreeOverTimeOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.OverTimeWorkHoursOutput;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.ScheRecAtr;
import nts.uk.ctx.at.shared.dom.workrule.closure.Closure;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmployment;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureEmploymentRepository;
import nts.uk.ctx.at.shared.dom.workrule.closure.ClosureRepository;

@Stateless
public class AgreementTimeServiceImpl implements AgreementTimeService {
	
	@Inject
	private AgreementTimeAdapter agreementTimeAdapter;
	
	@Inject
	private EmployeeRequestAdapter employeeAdaptor;
	
	@Inject
	private ClosureRepository closureRepository;
	
	@Inject
	private ClosureEmploymentRepository closureEmploymentRepository;
	
	@Inject
	private AgreementMonthSettingAdapter agreementMonthSettingAdapter;

	@Override
	public AgreeOverTimeOutput getAgreementTime(String companyID, String employeeID) {
		// ?????????????????????????????????
		SEmpHistImport empHistImport = employeeAdaptor.getEmpHist(companyID, employeeID, GeneralDate.today());
		if(empHistImport==null || empHistImport.getEmploymentCode()==null){
			throw new BusinessException("Msg_426");
		}
		String employmentCD = empHistImport.getEmploymentCode();
		// ??????ID???????????????
		Optional<ClosureEmployment> closureEmployment = closureEmploymentRepository.findByEmploymentCD(companyID, employmentCD);
		if(!closureEmployment.isPresent()){
			throw new BusinessException("Msg_1134");
		}
		Optional<Closure> opClosure = closureRepository.findById(companyID, closureEmployment.get().getClosureId());
		if(!opClosure.isPresent()){
			throw new RuntimeException("khong co closure");
		}
		Closure closure = opClosure.get();
		
		// ??????????????????????????????
		YearMonth currentMonth = closure.getClosureMonth().getProcessingYm();
		// ??????=??????????????????.AddMonths(1)
		YearMonth nextMonth = currentMonth.addMonths(1);
		
		AgreementTimeImport_Old detailCurrentMonth = null;
		AgreementTimeImport_Old detailNextMonth = null;
		
		// 36?????????????????????
		List<AgreementTimeImport_Old> detailCurrentMonthLst = agreementTimeAdapter.getAgreementTime(companyID, Arrays.asList(employeeID), currentMonth, closure.getClosureId());
		if(!CollectionUtil.isEmpty(detailCurrentMonthLst)){
			detailCurrentMonth = detailCurrentMonthLst.get(0);
		}
		// 36?????????????????????
		List<AgreementTimeImport_Old> detailNextMonthLst = agreementTimeAdapter.getAgreementTime(companyID, Arrays.asList(employeeID), nextMonth, closure.getClosureId());
		if(!CollectionUtil.isEmpty(detailNextMonthLst)){
			detailNextMonth = detailNextMonthLst.get(0);
		}
		
		return new AgreeOverTimeOutput(detailCurrentMonth, detailNextMonth, currentMonth, nextMonth);
	}

	@Override
	public OverTimeWorkHoursOutput getOverTimeWorkHoursOutput(String companyId, String employeeId) {
		// ?????????????????????????????????
		SEmpHistImport empHistImport = employeeAdaptor.getEmpHist(companyId, employeeId, GeneralDate.today());
		if(empHistImport==null || empHistImport.getEmploymentCode()==null) {
			throw new BusinessException("Msg_426");
		}
		String employmentCD = empHistImport.getEmploymentCode();
		
		
		// ??????ID???????????????
		Optional<ClosureEmployment> closureEmployment = closureEmploymentRepository.findByEmploymentCD(companyId, employmentCD);
		if (!closureEmployment.isPresent()) {
			throw new BusinessException("Msg_1134");
		}
		Optional<Closure> opClosure = closureRepository.findById(companyId, closureEmployment.get().getClosureId());
		if (!opClosure.isPresent()) {
			throw new RuntimeException("khong co closure");
		}
		Closure closure = opClosure.get();
		
		// ??????????????????????????????
		YearMonth currentMonth = closure.getClosureMonth().getProcessingYm();
		// ??????=??????????????????.AddMonths(1)
		YearMonth nextMonth = currentMonth.addMonths(1);
		
		// ???NO.333???36?????????????????????
		AgreementTimeOfManagePeriod currentTimeMonth = agreementTimeAdapter.getAgreementTimeOfManagePeriod(
				employeeId,
				currentMonth,
				Collections.emptyList()	,
				GeneralDate.today(),
				ScheRecAtr.SCHEDULE);
		// [NO.708]?????????????????????????????????????????????????????????????????????
		AgreementMonthSettingOutput currentMonthOutput = agreementMonthSettingAdapter.getData(employeeId, currentMonth);
		// ???NO.333???36?????????????????????
		AgreementTimeOfManagePeriod nextTimeMonth = agreementTimeAdapter.getAgreementTimeOfManagePeriod(
				employeeId,
				nextMonth,
				Collections.emptyList()	,
				GeneralDate.today(),
				ScheRecAtr.SCHEDULE);
		// [NO.708]?????????????????????????????????????????????????????????????????????
		AgreementMonthSettingOutput nextMonthOutput = agreementMonthSettingAdapter.getData(employeeId, nextMonth);
		
		return OverTimeWorkHoursOutput.builder()
				.isCurrentMonth(currentMonthOutput.getIsExist())
				.currentTimeMonth(currentTimeMonth)
				.currentMonth(currentMonth)
				.isNextMonth(nextMonthOutput.getIsExist())
				.nextTimeMonth(nextTimeMonth)
				.nextMonth(nextMonth)
				.build();
	}

}
