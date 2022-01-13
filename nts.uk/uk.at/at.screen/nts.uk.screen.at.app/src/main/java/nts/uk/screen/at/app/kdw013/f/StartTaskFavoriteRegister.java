package nts.uk.screen.at.app.kdw013.f;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.screen.at.app.kdw013.query.FavoriteTaskItemDto;
import nts.uk.screen.at.app.kdw013.query.GetFavoriteTaskItem;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.F:作業お気に入り登録.メニュー別OCD.作業お気に入り登録を起動する
 * @author tutt
 *
 */
@Stateless
public class StartTaskFavoriteRegister {
	
	@Inject
	private GetFavoriteTaskItem getFavoriteTaskItem;
	
	public FavoriteTaskItemDto startTaskFavRegister(String favId) {
		Optional<FavoriteTaskItemDto> optItemDto = getFavoriteTaskItem.getFavTaskItem(favId);
		
		if (!optItemDto.isPresent()) {
			return new FavoriteTaskItemDto();
		}
		return optItemDto.get();
		
	}

}
