package nts.uk.screen.at.app.kdw013.a.favorite.task;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.screen.at.app.kdw013.query.FavoriteTaskDisplayOrderDto;
import nts.uk.screen.at.app.kdw013.query.FavoriteTaskItemDto;

@Getter
@NoArgsConstructor
@Setter
public class FavTaskDto {
	// お気に入り作業項目
		public List<FavoriteTaskItemDto> favTaskItems;
		
		// お気に入り作業の表示順
		public FavoriteTaskDisplayOrderDto favTaskDisplayOrders;
}
