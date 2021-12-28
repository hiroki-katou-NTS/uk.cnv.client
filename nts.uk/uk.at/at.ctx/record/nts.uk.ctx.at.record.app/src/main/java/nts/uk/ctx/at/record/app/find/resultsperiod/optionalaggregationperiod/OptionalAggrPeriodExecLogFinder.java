package nts.uk.ctx.at.record.app.find.resultsperiod.optionalaggregationperiod;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.record.dom.adapter.employee.EmployeeRecordAdapter;
import nts.uk.ctx.at.record.dom.adapter.employee.EmployeeRecordImport;
import nts.uk.ctx.at.record.dom.executionstatusmanage.optionalperiodprocess.AggrPeriodExcution;
import nts.uk.ctx.at.record.dom.executionstatusmanage.optionalperiodprocess.AggrPeriodExcutionRepository;
import nts.uk.ctx.at.record.dom.executionstatusmanage.optionalperiodprocess.AggrPeriodInfor;
import nts.uk.ctx.at.record.dom.executionstatusmanage.optionalperiodprocess.AggrPeriodInforRepository;
import nts.uk.ctx.at.record.dom.executionstatusmanage.optionalperiodprocess.AggrPeriodTarget;
import nts.uk.ctx.at.record.dom.executionstatusmanage.optionalperiodprocess.AggrPeriodTargetRepository;
import nts.uk.ctx.at.record.dom.resultsperiod.optionalaggregationperiod.AnyAggrPeriod;
import nts.uk.ctx.at.record.dom.resultsperiod.optionalaggregationperiod.AnyAggrPeriodRepository;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.i18n.TextResource;

/**
 * 
 * @author HungTT
 *
 */

@Stateless
public class OptionalAggrPeriodExecLogFinder {

	@Inject
	private AggrPeriodExcutionRepository logRepo;

	@Inject
	private AnyAggrPeriodRepository aggrPeriodRepo;

	@Inject
	private AggrPeriodTargetRepository targetRepo;

	@Inject
	private AggrPeriodInforRepository errorInfoRepo;

	@Inject
	private EmployeeRecordAdapter empAdapter;

	public List<OptionalAggrPeriodExecLogDto> findLog(GeneralDate start, GeneralDate end) {
		List<OptionalAggrPeriodExecLogDto> result = new ArrayList<>();
		String companyId = AppContexts.user().companyId();
		List<AggrPeriodExcution> listLog = logRepo.findExecutionPeriod(companyId, start, end);
		List<String> listEmployeeId = listLog.stream().map(l -> l.getExecutionEmpId()).collect(Collectors.toList());
		List<EmployeeRecordImport> lstEmpInfo = empAdapter.getPersonInfor(listEmployeeId);
		for (AggrPeriodExcution log : listLog) {
			Optional<AnyAggrPeriod> optAggr = aggrPeriodRepo.findOneByCompanyIdAndFrameCode(companyId, log.getAggrFrameCode().v());
			List<AggrPeriodTarget> listTarget = targetRepo.findAll(log.getAggrId());
			List<AggrPeriodInfor> listError = errorInfoRepo.findAll(log.getAggrId());
			EmployeeRecordImport empInfo = lstEmpInfo.stream().filter(e -> e.getEmployeeId().equals(log.getExecutionEmpId()))
					.findFirst()
					.orElse(null);
			if (empInfo == null) continue;
			if (optAggr.isPresent()) {
				AnyAggrPeriod aggr = optAggr.get();
				OptionalAggrPeriodExecLogDto dto = new OptionalAggrPeriodExecLogDto(log.getAggrId(),
						log.getAggrFrameCode().v(), aggr.getOptionalAggrName().v(), log.getStartDateTime(),
						empInfo.getEmployeeCode(), empInfo.getPname(), aggr.getPeriod().start(),
						aggr.getPeriod().end(), log.getExecutionStatus().get().name, listTarget.size(), listError.size());
				result.add(dto);
			} else {
				OptionalAggrPeriodExecLogDto dto = new OptionalAggrPeriodExecLogDto(log.getAggrId(),
						log.getAggrFrameCode().v(), TextResource.localize("Msg_1307"), log.getStartDateTime(),
						empInfo.getEmployeeCode(), empInfo.getPname(), null, null, log.getExecutionStatus().get().name,
						listTarget.size(), listError.size());
				result.add(dto);
			}
		}
		result.sort((OptionalAggrPeriodExecLogDto c1, OptionalAggrPeriodExecLogDto c2) -> c2.getExecutionDt()
				.compareTo(c1.getExecutionDt()));
		return result;
	}
	
	public AggrPeriodExcutionDto findAggr(String aggrFrameCode){
		String companyId = AppContexts.user().companyId();
		String executionEmpId = AppContexts.user().employeeId();
		Optional<AggrPeriodExcution> data = this.logRepo.findExecution(companyId, executionEmpId, aggrFrameCode);

		if (data.isPresent()) {
			return AggrPeriodExcutionDto.fromDomain(data.get());
		}

		return null;
		
	}
	
	public List<AggrPeriodExcutionDto> findStatus(String aggrFrameCode, int executionStatus){
		String companyId = AppContexts.user().companyId();
		return logRepo.findExecutionStatus(companyId, aggrFrameCode, executionStatus).stream().map(e -> {
			return convertToDbType(e);
		}).collect(Collectors.toList());
		
	}
	
	public List<AggrPeriodExcutionDto> findAll(String aggrFrameCode) {
		String companyId = AppContexts.user().companyId();
		return logRepo.findAggrCode(companyId, aggrFrameCode).stream().map(e -> {
			return convertToDbType(e);
		}).collect(Collectors.toList());
	}
	
	private AggrPeriodExcutionDto convertToDbType(AggrPeriodExcution excution) {
		AggrPeriodExcutionDto excutionDto = new AggrPeriodExcutionDto();
		excutionDto.setExecutionEmpId(excution.getExecutionEmpId());
		excutionDto.setAggrFrameCode(excution.getAggrFrameCode().v());
		excutionDto.setAggrId(excution.getAggrId());
		excutionDto.setStartDateTime(excution.getStartDateTime());
		excutionDto.setEndDateTime(excution.getEndDateTime());
		excutionDto.setExecutionAtr(excution.getExecutionAtr().value);
		excutionDto.setExecutionStatus(excution.getExecutionAtr().value);
		excutionDto.setPresenceOfError(excution.getPresenceOfError().value);
		return excutionDto;
	}
}
