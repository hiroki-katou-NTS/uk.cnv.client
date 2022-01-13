package nts.uk.screen.at.app.kml002.H;

import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountForCompany;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.CriterionAmountForCompanyRepository;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.HandlingOfCriterionAmount;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.HandlingOfCriterionAmountByNo;
import nts.uk.ctx.at.aggregation.dom.schedulecounter.criterion.HandlingOfCriterionAmountRepository;
import nts.uk.shr.com.context.AppContexts;

// 
@Stateless
public class EstimatedCompanyInfoFinder {
	
	@Inject
	private CriterionAmountForCompanyRepository criterionAmountForCompanyRepository;
	
	@Inject
	private HandlingOfCriterionAmountRepository handlingOfCriterionAmountRepository;
	/**
	 * 目安金額情報を取得する
	 */
	public EsimatedInfoDto getEstimatedInfo() {
		String cid = AppContexts.user().companyId();
		
		EsimatedInfoDto esimatedInfoDto =new EsimatedInfoDto();
		// 会社の目安金額を取得する
		Optional<CriterionAmountForCompany> criOptional = criterionAmountForCompanyRepository.get(cid);
		
		// 目安金額の扱いを取得する
		Optional<HandlingOfCriterionAmount> hanOptional = handlingOfCriterionAmountRepository.get(cid);
		if(criOptional.isPresent()) {
			esimatedInfoDto.setMonths(criOptional.get()
						.getCriterionAmount()
						.getMonthly()
						.getList()
						.stream()
						.map(CriterionAmountByNoDto::setData)
						.collect(Collectors.toList()));
			esimatedInfoDto.setAnnuals(criOptional.get()
						.getCriterionAmount()
						.getYearly()
						.getList()
						.stream()
						.map(CriterionAmountByNoDto::setData)
						.collect(Collectors.toList()));
		} 	
		if(hanOptional.isPresent()) {			
			esimatedInfoDto.setListHandlingByNo(hanOptional.get()
						.getList()
						.stream()
						.map(HandlingOfCriterionAmountByNoDto::setData)
						.collect(Collectors.toList()));			
		} 
		return esimatedInfoDto;
	}
}
