package nts.uk.screen.at.app.kdw013.query;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskDisplayOrder;

/**
 * 
 * @author tutt
 *
 */
@Getter
@NoArgsConstructor
public class FavoriteTaskDisplayOrderDto {
	
	private String employeeId;
	
	private List<FavoriteDisplayOrderDto> displayOrders;
	
	public FavoriteTaskDisplayOrderDto(FavoriteTaskDisplayOrder domain) {
		this.employeeId = domain.getEmployeeId();
		this.displayOrders = domain.getDisplayOrders().stream().map(m -> new FavoriteDisplayOrderDto(m)).collect(Collectors.toList());
	}
	
}
