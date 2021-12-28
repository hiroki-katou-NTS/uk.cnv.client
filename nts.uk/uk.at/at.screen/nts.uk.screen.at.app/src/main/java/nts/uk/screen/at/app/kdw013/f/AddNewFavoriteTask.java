package nts.uk.screen.at.app.kdw013.f;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.screen.at.app.kdw013.command.RegisterFavoriteCommand;
import nts.uk.screen.at.app.kdw013.command.RegisterFavoriteCommandHandler;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.F:作業お気に入り登録.メニュー別OCD.作業お気に入りを新規追加する
 * @author tutt
 *
 */
@Stateless
public class AddNewFavoriteTask {
	
	@Inject
	private RegisterFavoriteCommandHandler handler;
	
	public void addNewFavoriteTask(RegisterFavoriteCommand command) {
		
		handler.handle(command);
	}

}
