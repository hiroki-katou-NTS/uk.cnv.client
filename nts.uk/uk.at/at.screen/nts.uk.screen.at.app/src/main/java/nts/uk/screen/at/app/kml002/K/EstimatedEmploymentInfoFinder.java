package nts.uk.screen.at.app.kml002.K;

import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;

import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountForEmployment;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountForEmploymentRepository;
import nts.uk.ctx.at.shared.dom.vacation.setting.compensatoryleave.EmploymentCode;
import nts.uk.screen.at.app.kml002.H.CriterionAmountByNoDto;

import javax.inject.Inject;


// 雇用の目安金額を取得する
@Stateless
public class EstimatedEmploymentInfoFinder {
	
	@Inject
	private CriterionAmountForEmploymentRepository criterionAmountForEmploymentRepository;
	
	public CriterionAmountForEmploymentDto getEstimatedInfo(String cid, String employmentCd) {
		
		Optional<CriterionAmountForEmployment> criOptional = criterionAmountForEmploymentRepository.get(cid, new EmploymentCode(employmentCd));
		
				
		return criOptional.map(x -> new CriterionAmountForEmploymentDto(
				x.getEmploymentCode().v(),
				x.getCriterionAmount()
				 .getMonthly()
				 .getList()
				 .stream()
				 .map(CriterionAmountByNoDto::setData)
				 .collect(Collectors.toList()),
				x.getCriterionAmount()
				 .getYearly()
				 .getList()
				 .stream()
				 .map(CriterionAmountByNoDto::setData)
				 .collect(Collectors.toList()) 
				)).orElse(null);
	}
}
