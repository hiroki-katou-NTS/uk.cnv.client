package nts.uk.screen.at.app.kdw013.query;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteDisplayOrder;

/**
 * 
 * @author tutt
 *
 */
@Getter
@NoArgsConstructor
public class FavoriteDisplayOrderDto {
	
	private String favId;
	
	private int order;

	public FavoriteDisplayOrderDto(FavoriteDisplayOrder domain) {
		this.favId = domain.getFavId();
		this.order = domain.getOrder();
	}
	
}
