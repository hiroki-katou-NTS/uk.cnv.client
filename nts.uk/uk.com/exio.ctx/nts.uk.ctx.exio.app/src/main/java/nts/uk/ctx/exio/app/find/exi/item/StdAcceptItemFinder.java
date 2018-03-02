package nts.uk.ctx.exio.app.find.exi.item;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import nts.uk.ctx.exio.dom.exi.item.StdAcceptItemRepository;
import nts.uk.shr.com.context.AppContexts;

@Stateless
/**
 * 受入項目（定型）
 */
public class StdAcceptItemFinder {

	@Inject
	private StdAcceptItemRepository finder;

	public List<StdAcceptItemDto> getAllStdAcceptItem() {
		return finder.getAllStdAcceptItem().stream().map(item -> StdAcceptItemDto.fromDomain(item))
				.collect(Collectors.toList());
	}

	public List<StdAcceptItemDto> getStdAcceptItem(int systemType, String conditionSetCd) {
		String companyId = AppContexts.user().companyId();
		return finder.getStdAcceptItem(companyId, systemType, conditionSetCd).stream()
				.map(item -> StdAcceptItemDto.fromDomain(item)).collect(Collectors.toList());
	}
}
