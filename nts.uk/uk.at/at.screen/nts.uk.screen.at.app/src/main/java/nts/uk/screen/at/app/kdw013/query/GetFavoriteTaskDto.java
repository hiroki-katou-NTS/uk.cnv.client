package nts.uk.screen.at.app.kdw013.query;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author tutt
 *
 */
@Getter
@NoArgsConstructor
@Setter
public class GetFavoriteTaskDto {
	
	// Get*(ログイン社員ID)
	// 1日お気に入り作業セット
	public List<OneDayFavoriteSetDto> oneDayFavSets;
	
	// 1日お気に入り作業の表示順
	public OneDayFavoriteTaskDisplayOrderDto oneDayFavTaskDisplayOrders;
	
	// お気に入り作業項目
	public List<FavoriteTaskItemDto> favTaskItems;
	
	// お気に入り作業の表示順
	public FavoriteTaskDisplayOrderDto favTaskDisplayOrders;

}
