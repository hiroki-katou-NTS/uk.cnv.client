package nts.uk.screen.at.app.kml002.K;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountForEmployment;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountForEmploymentRepository;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.HandlingOfCriterionAmount;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.HandlingOfCriterionAmountRepository;
import nts.uk.ctx.bs.employee.app.find.employment.dto.EmploymentDto;
import nts.uk.ctx.bs.employee.dom.employment.Employment;
import nts.uk.ctx.bs.employee.dom.employment.EmploymentRepository;
import nts.uk.screen.at.app.kml002.H.HandlingOfCriterionAmountByNoDto;
import nts.uk.shr.com.context.AppContexts;


// 初期情報を取得する
@Stateless
public class EmploymentInfoFinder {
	
	@Inject
	private EmploymentRepository employmentRepository;
	
	@Inject
	private HandlingOfCriterionAmountRepository handlingOfCriterionAmountRepository;
	
	@Inject
	private CriterionAmountForEmploymentRepository criterionAmountForEmploymentRepository;
	
	public InitEmploymentInfoDto getInitInfo() {
		
		String cid = AppContexts.user().companyId();
		
		// 1: call
		List<Employment> employments = employmentRepository.findAll(cid);
		
		// 2: 目安金額の扱いを取得する 
		Optional<HandlingOfCriterionAmount> hanOptional = handlingOfCriterionAmountRepository.get(cid);
		
		// 設定した雇用の目安金額を取得する
		List<CriterionAmountForEmployment> criterionAmountForEmployments = Collections.emptyList();
		if (!CollectionUtil.isEmpty(employments)) {
			criterionAmountForEmployments = criterionAmountForEmploymentRepository.getAll(cid);
		}
		return new InitEmploymentInfoDto(
				criterionAmountForEmployments
					.stream()
					.map(x -> x.getEmploymentCode().v())
					.collect(Collectors.toList()),
				employments
					.stream()
					.map(x -> new EmploymentDto(x.getEmploymentCode().v(), x.getEmploymentName().v()))
					.collect(Collectors.toList()),
				hanOptional
					.map(x -> x.getList())
					.orElse(Collections.emptyList())
					.stream()
					.map(HandlingOfCriterionAmountByNoDto::setData)
					.collect(Collectors.toList()));
	}
}
