package nts.uk.screen.at.app.kdw013.a;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.screen.at.app.kdw013.command.DeleteFavoriteCommand;
import nts.uk.screen.at.app.kdw013.command.DeleteFavoriteCommandHandler;

/**
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.A:工数入力.メニュー別OCD.お気に入り作業を削除する
 * @author tutt
 *
 */
@Stateless
public class DeleteTaskSet {
	
	@Inject
	private DeleteFavoriteCommandHandler handler;
		
	public void deleteTaskSet(DeleteFavoriteCommand command) {
		handler.handle(command);
	}
}
