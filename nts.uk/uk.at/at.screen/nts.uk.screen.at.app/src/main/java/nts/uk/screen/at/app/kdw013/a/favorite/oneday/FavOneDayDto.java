package nts.uk.screen.at.app.kdw013.a.favorite.oneday;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nts.uk.screen.at.app.kdw013.query.OneDayFavoriteSetDto;
import nts.uk.screen.at.app.kdw013.query.OneDayFavoriteTaskDisplayOrderDto;

@Getter
@NoArgsConstructor
@Setter
public class FavOneDayDto {
	// Get*(ログイン社員ID)
	// 1日お気に入り作業セット
	public List<OneDayFavoriteSetDto> oneDayFavSets;
	
	// 1日お気に入り作業の表示順
	public OneDayFavoriteTaskDisplayOrderDto oneDayFavTaskDisplayOrders;
}
