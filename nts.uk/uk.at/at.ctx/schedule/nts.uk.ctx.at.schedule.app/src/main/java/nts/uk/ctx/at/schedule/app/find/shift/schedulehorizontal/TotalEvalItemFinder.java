package nts.uk.ctx.at.schedule.app.find.shift.schedulehorizontal;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.error.BusinessException;
import nts.uk.ctx.at.schedule.dom.shift.schedulehorizontal.TotalEvalItem;
import nts.uk.ctx.at.schedule.dom.shift.schedulehorizontal.repository.HoriTotalCategoryRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
public class TotalEvalItemFinder {
	@Inject
	private HoriTotalCategoryRepository horiRep;
	/**
	 * convert from domain to dto
	 * @param yearServicePerSet
	 * @return
	 */
	private TotalEvalItemDto fromDomain(TotalEvalItem totalEvalItem){
		TotalEvalItemDto totalEvalItemDto = new TotalEvalItemDto();
		totalEvalItemDto.setTotalItemNo(totalEvalItem.getTotalItemNo().v());
		totalEvalItemDto.setTotalItemName(totalEvalItem.getTotalItemName().v());
		return totalEvalItemDto;
	}
	
	public List<TotalEvalItemDto> finder(){
		String companyId = AppContexts.user().companyId();
		List<TotalEvalItemDto> totalEvalItemDtos = horiRep.findAllItem(companyId)
				.stream()
				.map(c -> fromDomain(c))
				.collect(Collectors.toList());
		if(totalEvalItemDtos == null){
			throw new BusinessException("Msg_458");
		}
		
		return totalEvalItemDtos;
	}
}
