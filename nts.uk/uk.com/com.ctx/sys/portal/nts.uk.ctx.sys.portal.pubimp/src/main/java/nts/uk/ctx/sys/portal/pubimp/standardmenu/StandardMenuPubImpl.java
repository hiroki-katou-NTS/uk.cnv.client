package nts.uk.ctx.sys.portal.pubimp.standardmenu;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.sys.portal.dom.standardmenu.StandardMenu;
import nts.uk.ctx.sys.portal.dom.standardmenu.StandardMenuRepository;
import nts.uk.ctx.sys.portal.pub.standardmenu.StandardMenuNameExport;
import nts.uk.ctx.sys.portal.pub.standardmenu.StandardMenuNameQuery;
import nts.uk.ctx.sys.portal.pub.standardmenu.StandardMenuPub;

@Stateless
public class StandardMenuPubImpl implements StandardMenuPub {

	@Inject
	private StandardMenuRepository standardMenuRepo;

	@Override
	public List<StandardMenuNameExport> getMenuDisplayName(String companyId, List<StandardMenuNameQuery> query) {
		List<StandardMenuNameExport> displayNames = new ArrayList<StandardMenuNameExport>();
		query.stream().forEach(menu -> {
			if (menu.getQueryString().isPresent()) {
				this.standardMenuRepo.getMenuDisplayNameHasQuery(
						companyId,
						menu.getProgramId(),
						menu.getQueryString().get(), 
						menu.getScreenId()).ifPresent(result -> {
							
							displayNames.add(toExport(result));
							
						});
			} else {
				this.standardMenuRepo.getMenuDisplayNameNoQuery(
						companyId, 
						menu.getProgramId(), 
						menu.getScreenId())
						.ifPresent(result -> {
							
							displayNames.add(toExport(result));
						});
			}

		});

		return displayNames;
	}

	@Override
	public List<StandardMenuNameExport> getMenus(String companyId, int system) {
		return standardMenuRepo.findAll1(companyId).stream()
				.filter(x -> x.getSystem().value == system)
				.map(this::toExport).collect(Collectors.toList());
	}

	private StandardMenuNameExport toExport(StandardMenu domain) {
		return new StandardMenuNameExport(
				domain.getProgramId(), 
				domain.getScreenId(), 
				domain.getQueryString(),
				domain.getDisplayName() != null ? domain.getDisplayName().v() : "",
				domain.getUrl(),
				domain.getCode().v(),
				domain.getSystem().value,
				domain.getClassification().value
		);
	}
}
