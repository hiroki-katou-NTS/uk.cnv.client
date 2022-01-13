package nts.uk.screen.at.app.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;

import nts.uk.ctx.at.request.app.find.application.applicationlist.ListOfAppTypesDto;
import nts.uk.ctx.at.request.dom.application.applist.service.ListOfAppTypes;
import nts.uk.ctx.at.request.dom.application.applist.service.content.AppContentService;
import nts.uk.ctx.at.request.dom.application.applist.service.content.AppTypeMapProgramID;
import nts.uk.ctx.sys.portal.pub.standardmenu.StandardMenuNameExport;
import nts.uk.ctx.sys.portal.pub.standardmenu.StandardMenuNameQuery;
import nts.uk.ctx.sys.portal.pub.standardmenu.StandardMenuPub;
import nts.uk.shr.com.context.AppContexts;

/**
 * refactor 4
 * @author Doan Duy Hung
 *
 */
@Stateless
public class ApplicationScreenFinder {
	
	@Inject
	private AppContentService appContentService;
	
	@Inject
	private StandardMenuPub standardMenuPub;
	
	/**
	 * refactor 4
	 * UKDesign.UniversalK.就業.KAF_申請.CMM045_申請一覧・承認一覧.A:申請一覧画面ver4.アルゴリズム.申請一覧の申請名称を取得する.申請一覧の申請名称を取得する
	 * @return
	 */
	public List<ListOfAppTypesDto> getAppNameInAppList() {
		String companyID = AppContexts.user().companyId();
		List<ListOfAppTypes> result = new ArrayList<>();
		// アルゴリズム「申請一覧申請種類のプログラムID」を実行する
		List<AppTypeMapProgramID> appTypeMapProgramIDLst = appContentService.getListProgramIDOfAppType();
		for(AppTypeMapProgramID item : appTypeMapProgramIDLst) {
			result.add(new ListOfAppTypes(
					item.getAppType(), 
					Strings.EMPTY, 
					false, 
					Optional.of(item.getProgramID()), 
					item.getApplicationTypeDisplay() == null ? Optional.empty() : Optional.of(item.getApplicationTypeDisplay()), 
					Optional.of("A")));
		}
		// アルゴリズム「メニューの表示名を取得する」を実行する
		List<StandardMenuNameQuery> param = result.stream().map(x -> {
			String programId = x.getOpProgramID().orElse("");
			String screenId = x.getOpString().orElse("");
			Optional<String> queryString = Optional.empty();
			if(x.getOpApplicationTypeDisplay().isPresent()) {
				switch (x.getOpApplicationTypeDisplay().get()) {
				case EARLY_OVERTIME:
					queryString = Optional.of("overworkatr=0");
					break;
				case NORMAL_OVERTIME:
					queryString = Optional.of("overworkatr=1");
					break;
				case EARLY_NORMAL_OVERTIME:
					queryString = Optional.of("overworkatr=2");
					break;
				case OVERTIME_MULTIPLE_TIME:
					queryString = Optional.of("overworkatr=3");
					break;
				case STAMP_ADDITIONAL:
					screenId = "A";
					break;
				case STAMP_ONLINE_RECORD:
					screenId = "B";
					break;
				default:
					break;
				}
			}
			return new StandardMenuNameQuery(programId, screenId, queryString);
			}).collect(Collectors.toList());
		List<StandardMenuNameExport> standardMenuNameExportLst = standardMenuPub.getMenuDisplayName(companyID, param);
		for(ListOfAppTypes item : result) {
			Optional<StandardMenuNameExport> opStandardMenuNameExport = standardMenuNameExportLst.stream().filter(x -> {
				String programId = item.getOpProgramID().orElse("");
				String screenId = item.getOpString().orElse("");
				Optional<String> queryString = Optional.empty();
				if(item.getOpApplicationTypeDisplay().isPresent()) {
					switch (item.getOpApplicationTypeDisplay().get()) {
					case EARLY_OVERTIME:
						queryString = Optional.of("overworkatr=0");
						break;
					case NORMAL_OVERTIME:
						queryString = Optional.of("overworkatr=1");
						break;
					case EARLY_NORMAL_OVERTIME:
						queryString = Optional.of("overworkatr=2");
						break;
					case OVERTIME_MULTIPLE_TIME:
						queryString = Optional.of("overworkatr=3");
						break;
					case STAMP_ADDITIONAL:
						screenId = "A";
						break;
					case STAMP_ONLINE_RECORD:
						screenId = "B";
						break;
					default:
						break;
					}
				}
				boolean condition = x.getProgramId().equals(programId) && x.getScreenId().equals(screenId);
				if(queryString.isPresent()) {
					condition = condition && queryString.get().equals(x.getQueryString());
				}
				return condition;
			}).findAny();
			if(opStandardMenuNameExport.isPresent()) {
				item.setAppName(opStandardMenuNameExport.get().getDisplayName());
			}
		}
		return result.stream().map(x -> ListOfAppTypesDto.fromDomain(x)).collect(Collectors.toList());
	}
}
