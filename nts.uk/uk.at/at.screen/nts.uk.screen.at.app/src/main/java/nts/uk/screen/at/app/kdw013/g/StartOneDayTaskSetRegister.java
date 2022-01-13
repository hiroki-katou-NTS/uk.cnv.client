package nts.uk.screen.at.app.kdw013.g;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.screen.at.app.kdw013.query.GetFavTaskSetForOneDay;
import nts.uk.screen.at.app.kdw013.query.OneDayFavoriteSetDto;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.G:1日作業セット登録.メニュー別OCD.1日作業セット登録を起動する
 * @author tutt
 *
 */
@Stateless
public class StartOneDayTaskSetRegister {
	
	@Inject
	private GetFavTaskSetForOneDay getFavTaskSetForOneDay;
	
	public OneDayFavoriteSetDto startOneDayTaskSetRegister(String favId) {
		Optional<OneDayFavoriteSetDto> optSetDto =  getFavTaskSetForOneDay.get(favId);
		if(!optSetDto.isPresent()) {
			return new OneDayFavoriteSetDto();
		}
		
		return optSetDto.get();
	}

}
