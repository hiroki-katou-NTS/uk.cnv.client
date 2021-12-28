package nts.uk.screen.at.app.kdw013.a;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.screen.at.app.kdw013.command.DeleteFavoriteForOneDayCommand;
import nts.uk.screen.at.app.kdw013.command.DeleteFavoriteForOneDayCommandHandler;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.A:工数入力.メニュー別OCD.1日作業セットを削除する
 * @author tutt
 *
 */
@Stateless
public class DeleteOneDayTaskSet {
	
	@Inject
	private DeleteFavoriteForOneDayCommandHandler handler;
		
	public void deleteOneDayTaskSet(DeleteFavoriteForOneDayCommand command) {
		handler.handle(command);
	}

}
