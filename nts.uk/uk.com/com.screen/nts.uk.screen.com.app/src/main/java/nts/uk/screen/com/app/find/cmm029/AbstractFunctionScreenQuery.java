package nts.uk.screen.com.app.find.cmm029;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import nts.uk.ctx.sys.portal.dom.standardmenu.StandardMenu;
import nts.uk.screen.com.app.find.cmm029.DisplayDataDto.DisplayDataDtoBuilder;
import nts.uk.shr.com.i18n.TextResource;

public abstract class AbstractFunctionScreenQuery {

	protected static final int SYSTEM_TYPE = 1;
	protected static final String MESSAGE_ID = "Msg_2318";

	public List<DisplayDataDto> findData(String cid, List<StandardMenu> standardMenus) {
		return Stream
				.of(Arrays.asList(this.getMainDisplayData(standardMenus)), this.getFunctionSettings(cid, standardMenus))
				.flatMap(Collection::stream).collect(Collectors.toList());
	}

	protected DisplayDataDto.DisplayDataDtoBuilder findFromStandardMenu(List<StandardMenu> standardMenus,
			String programId, String screenId) {
		// 1. 絞り込み()
		Optional<StandardMenu> optStandardMenu = standardMenus.stream()
				.filter(data -> data.getSystem().value == SYSTEM_TYPE
						&& data.getProgramId().equals(TextResource.localize(programId))
						&& data.getScreenId().equals(TextResource.localize(screenId)))
				.findFirst();
		// 1.1. 作成()
		DisplayDataDtoBuilder builder = DisplayDataDto.builder().system(SYSTEM_TYPE).programId(programId).useAtr(true);
		if (optStandardMenu.isPresent()) {
			return builder.url(optStandardMenu.get().getUrl());
		} else {
			return builder.errorMessage(MESSAGE_ID);
		}
	}

	protected String getText(String textId) {
		return TextResource.localize(textId);
	}

	protected abstract DisplayDataDto getMainDisplayData(List<StandardMenu> standardMenus);

	protected abstract List<DisplayDataDto> getFunctionSettings(String cid, List<StandardMenu> standardMenus);
}
