package nts.uk.screen.at.app.kdw013.g;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.screen.at.app.kdw013.command.RegisterFavoriteForOneDayCommand;
import nts.uk.screen.at.app.kdw013.command.RegisterFavoriteForOneDayCommandHandler;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.G:1日作業セット登録.メニュー別OCD.1日作業セットを新規追加する
 * 
 * @author tutt
 *
 */
@Stateless
public class AddOneDayNewFavoriteTaskSet {
	
	@Inject
	private RegisterFavoriteForOneDayCommandHandler handler;

	public void addOneDayNewFavoriteTaskSet(RegisterFavoriteForOneDayCommand command) {
		
		handler.handle(command);
	}

}
