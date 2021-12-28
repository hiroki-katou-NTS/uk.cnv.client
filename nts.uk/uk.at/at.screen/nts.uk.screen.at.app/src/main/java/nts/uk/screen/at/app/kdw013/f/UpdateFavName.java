package nts.uk.screen.at.app.kdw013.f;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.screen.at.app.kdw013.command.UpdateFavNameCommand;
import nts.uk.screen.at.app.kdw013.command.UpdateFavNameCommandHandler;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.F:作業お気に入り登録.メニュー別OCD.作業お気に入り名称を変更する
 * @author tutt
 *
 */
@Stateless
public class UpdateFavName {
	
	@Inject
	private UpdateFavNameCommandHandler handler;
	
	public void updateFavName(UpdateFavNameCommand command) {
		
		handler.handle(command);
	}

}
