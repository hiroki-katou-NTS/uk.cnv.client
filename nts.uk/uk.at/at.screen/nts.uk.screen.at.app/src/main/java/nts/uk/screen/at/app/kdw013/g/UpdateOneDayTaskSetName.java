package nts.uk.screen.at.app.kdw013.g;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.screen.at.app.kdw013.command.UpdateOneDayFavNameCommand;
import nts.uk.screen.at.app.kdw013.command.UpdateOneDayFavNameCommandHandler;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.G:1日作業セット登録.メニュー別OCD.1日作業セット名称を変更する
 * @author tutt
 *
 */
@Stateless
public class UpdateOneDayTaskSetName {
	
	@Inject
	private UpdateOneDayFavNameCommandHandler handler;
	
	public void updateOneDayTaskSetName(UpdateOneDayFavNameCommand command) {
		
		handler.handle(command);
	}

}
