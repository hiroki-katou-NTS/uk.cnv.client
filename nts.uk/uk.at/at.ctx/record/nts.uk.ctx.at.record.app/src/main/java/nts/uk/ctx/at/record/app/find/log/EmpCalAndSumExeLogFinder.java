package nts.uk.ctx.at.record.app.find.log;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.record.app.find.log.dto.EmpCalAndSumExeLogDto;
import nts.uk.ctx.at.record.app.find.log.dto.InputEmpCalAndSum;
import nts.uk.ctx.at.record.app.find.log.dto.InputEmpCalAndSumByDate;
import nts.uk.ctx.at.record.dom.workrecord.log.EmpCalAndSumExeLogRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class EmpCalAndSumExeLogFinder {
	
	@Inject
	private EmpCalAndSumExeLogRepository empCalAndSumExeLogRepo;
	
	 
	/**
	 * get All EmpCalAndSumExeLog
	 * @return list EmpCalAndSumExeLog
	 */
	public List<EmpCalAndSumExeLogDto> getAllEmpCalAndSumExeLog(){
		String companyID = AppContexts.user().companyId();
		List<EmpCalAndSumExeLogDto> data = empCalAndSumExeLogRepo
				.getAllEmpCalAndSumExeLog(companyID)
				.stream()
				.map(c -> EmpCalAndSumExeLogDto.fromDomain(c))
				.collect(Collectors.toList());
		if(data.isEmpty())
			return Collections.emptyList();
		return data;
	}
	/**
	 * get All EmpCalAndSumExeLog by id
	 * @param inputEmpCalAndSum
	 * @return
	 */
	public EmpCalAndSumExeLogDto getEmpCalAndSumExeLogById(InputEmpCalAndSum inputEmpCalAndSum){
		String companyID = AppContexts.user().companyId();
		 String employeeID = AppContexts.user().employeeId();
		Optional<EmpCalAndSumExeLogDto> data = empCalAndSumExeLogRepo
				.getEmpCalAndSumExeLogByID(companyID, inputEmpCalAndSum.getEmpCalAndSumExecLogID(), inputEmpCalAndSum.getOperationCaseID(), employeeID)
				.map(c->EmpCalAndSumExeLogDto.fromDomain(c));
			if(data.isPresent())
				return data.get();
		return null;
		
	}
	
	/**
	 * get list EmpCalAndSumExeLogDto by empCalAndSumExecLogID
	 * @param empCalAndSumExecLogID
	 * @return
	 */
	public List<EmpCalAndSumExeLogDto> getByEmpCalAndSumExecLogID(String empCalAndSumExecLogID){
		List<EmpCalAndSumExeLogDto> data = empCalAndSumExeLogRepo.getByEmpCalAndSumExecLogID(empCalAndSumExecLogID).stream()
				.map(c -> EmpCalAndSumExeLogDto.fromDomain(c))
				.collect(Collectors.toList());
		if(data.isEmpty())
			return Collections.emptyList();
		return data;
	}

	public List<EmpCalAndSumExeLogDto> getEmpCalAndSumExeLogByDate(InputEmpCalAndSumByDate inputEmpCalAndSumByDate){
		String companyID = AppContexts.user().companyId();
		List<EmpCalAndSumExeLogDto> data = empCalAndSumExeLogRepo
				.getAllEmpCalAndSumExeLogByDate(companyID, inputEmpCalAndSumByDate.getStartDate(), inputEmpCalAndSumByDate.getEndDate())
				.stream()
				.map(c->EmpCalAndSumExeLogDto.fromDomain(c))
				.collect(Collectors.toList());;
			if(data.isEmpty())
				return Collections.emptyList();
		return data;
	}

}
