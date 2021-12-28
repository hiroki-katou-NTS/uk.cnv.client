package nts.uk.ctx.at.schedule.app.find.shift.estimate.aggregateset;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.schedule.app.find.budget.premium.dto.PremiumItemDto;
import nts.uk.ctx.at.schedule.dom.shift.estimate.aggregateset.AggregateSetting;
import nts.uk.ctx.at.schedule.dom.shift.estimate.aggregateset.AggregateSettingRepository;
import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.personcostcalc.premiumitem.PremiumItemRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * The Class AggregateSettingFinder.
 */
@Stateless
public class AggregateSettingFinder {
	
	/** The repository. */
	@Inject
	private AggregateSettingRepository repository;
	
	@Inject
	private PremiumItemRepository premiumItemRepository;
	
	/**
	 * Find data.
	 *
	 * @return the aggregate setting find dto
	 */
	public AggregateSettingFindDto findData(){
		//get company id
		String companyId = AppContexts.user().companyId();
		
		Optional<AggregateSetting> optAggredateSet = this.repository.findByCID(new CompanyId(companyId));
		if (optAggredateSet.isPresent()) {
			AggregateSettingFindDto dto = new AggregateSettingFindDto();
			AggregateSetting domain = optAggredateSet.get();
			domain.saveToMemento(dto);
			return dto;
		}
		return null;
	}
	
	public List<PremiumItemDto> findPremiumNo(){
		String companyId = AppContexts.user().companyId();
		return this.premiumItemRepository.findAllIsUse(companyId)
				.stream()
				.map(x -> new PremiumItemDto(
						companyId, 
						x.getDisplayNumber().value,
						x.getName().v(), 
						x.getUseAtr().value))
				.sorted(Comparator.comparing(PremiumItemDto::getDisplayNumber))
				.collect(Collectors.toList());
	}
}
