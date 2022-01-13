package nts.uk.screen.at.app.kdw013.query;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset.OneDayFavoriteTaskDisplayOrder;

/**
 * 
 * @author tutt
 *
 */
@Getter
@NoArgsConstructor
public class OneDayFavoriteTaskDisplayOrderDto {
	
	private String sId;
	
	private List<FavoriteDisplayOrderDto> displayOrders;

	public OneDayFavoriteTaskDisplayOrderDto(OneDayFavoriteTaskDisplayOrder domain) {
		this.sId = domain.getSId();
		this.displayOrders = domain.getDisplayOrders().stream().map(m -> new FavoriteDisplayOrderDto(m)).collect(Collectors.toList());
	}
	
}
