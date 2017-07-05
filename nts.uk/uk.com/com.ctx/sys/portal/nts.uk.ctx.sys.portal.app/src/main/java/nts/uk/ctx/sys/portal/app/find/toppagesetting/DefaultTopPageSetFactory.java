package nts.uk.ctx.sys.portal.app.find.toppagesetting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.gul.text.StringUtil;
import nts.uk.ctx.sys.portal.app.find.flowmenu.FlowMenuDto;
import nts.uk.ctx.sys.portal.app.find.flowmenu.FlowMenuFinder;
import nts.uk.ctx.sys.portal.app.find.mypage.setting.MyPageSettingDto;
import nts.uk.ctx.sys.portal.app.find.mypage.setting.MyPageSettingFinder;
import nts.uk.ctx.sys.portal.app.find.placement.PlacementDto;
import nts.uk.ctx.sys.portal.app.find.placement.PlacementPartDto;
import nts.uk.ctx.sys.portal.app.find.toppage.TopPageDto;
import nts.uk.ctx.sys.portal.app.find.toppage.TopPageFinder;
import nts.uk.ctx.sys.portal.dom.layout.Layout;
import nts.uk.ctx.sys.portal.dom.mypage.MyPage;
import nts.uk.ctx.sys.portal.dom.mypage.MyPageRepository;
import nts.uk.ctx.sys.portal.dom.placement.Placement;
import nts.uk.ctx.sys.portal.dom.placement.PlacementRepository;
import nts.uk.ctx.sys.portal.dom.placement.externalurl.ExternalUrl;
import nts.uk.ctx.sys.portal.dom.standardmenu.StandardMenu;
import nts.uk.ctx.sys.portal.dom.standardmenu.StandardMenuRepository;
import nts.uk.ctx.sys.portal.dom.toppagepart.TopPagePart;
import nts.uk.ctx.sys.portal.dom.toppagepart.service.TopPagePartService;
import nts.uk.ctx.sys.portal.dom.toppagesetting.TopPageJobSet;
import nts.uk.ctx.sys.portal.dom.toppagesetting.TopPageJobSetRepository;
import nts.uk.ctx.sys.portal.dom.toppagesetting.TopPagePersonSet;
import nts.uk.ctx.sys.portal.dom.toppagesetting.TopPagePersonSetRepository;
import nts.uk.ctx.sys.portal.dom.toppagesetting.TopPageSelfSetRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author hoatt
 *
 */
@Stateless
public class DefaultTopPageSetFactory implements TopPageSetFactory {

	@Inject
	private TopPagePartService topPagePartService;
	@Inject
	private FlowMenuFinder flowmenu;
	@Inject
	private TopPageSelfSettingFinder topPageSelfSet;
	@Inject
	private TopPagePersonSetRepository topPagePerson;
	@Inject
	private StandardMenuRepository standardMenu;
	@Inject
	private TopPageFinder toppageFinder;
	@Inject
	private MyPageSettingFinder myPageSetFinder;
	@Inject
	private TopPageSelfSetRepository toppageRepository;
	@Inject
	private PlacementRepository placementRepository;
	@Inject
	private MyPageRepository mypage;
	@Inject
	private TopPageJobSetRepository topPageJobSet;
	//companyId
	String companyId = AppContexts.user().companyId();
	//employeeId
	String employeeId = AppContexts.user().employeeId();
	
	/**
	 * check = true (hien thi top page truoc)
	 * check = false (hien thi my page truoc)
	 */
	Boolean check = false;
	
	/**
	 * hien thi my page
	 */
	@Override
	public LayoutForMyPageDto buildLayoutDto(Layout layout, List<Placement> placements) {
		MyPageSettingDto myPage = myPageSetFinder.findByCompanyId(companyId);
		List<PlacementDto> placementDtos = buildPlacementDto(layout, placements, myPage);
		List<FlowMenuPlusDto> flowmenuNew = new ArrayList<FlowMenuPlusDto>();
		List<PlacementDto> placementNew = new ArrayList<PlacementDto>();
		for (PlacementDto placementDto : placementDtos) {
			if(placementDto.getPlacementPartDto().getExternalUrl() == null && placementDto.getPlacementPartDto().getType().intValue() == 2){
				FlowMenuDto flowMenu = flowmenu.getFlowMenu(placementDto.getPlacementPartDto().getTopPagePartID());
				if(flowMenu != null){
					FlowMenuPlusDto fMenu = new FlowMenuPlusDto(flowMenu.getToppagePartID(),
							flowMenu.getTopPageCode(),
							flowMenu.getTopPageName(),
							flowMenu.getType(),
							flowMenu.getWidthSize(),
							flowMenu.getHeightSize(),
							flowMenu.getFileID(),
							flowMenu.getFileName(),
							flowMenu.getDefClassAtr(),
							placementDto.getRow(),
							placementDto.getColumn());
					flowmenuNew.add(fMenu);
				}
				continue;
			}else{
				placementNew.add(placementDto);
			}
		}
		return new LayoutForMyPageDto(employeeId, layout.getLayoutID(), layout.getPgType().value, flowmenuNew, placementNew);
	}
	//build placement my page
	private List<PlacementDto> buildPlacementDto(Layout layout, List<Placement> placements, MyPageSettingDto myPage) {
		List<TopPagePart> activeTopPageParts = topPagePartService.getAllActiveTopPagePart(layout.getCompanyID(), layout.getPgType());
		List<PlacementDto> placementDtos = new ArrayList<PlacementDto>();
		for (Placement placement : placements) {
			if (placement.isExternalUrl()) {
				if (myPage.getExternalUrlPermission().intValue() == 1) {
					ExternalUrl externalUrl = placement.getExternalUrl().get();
					placementDtos.add(new PlacementDto(placement.getPlacementID(), placement.getLayoutID(),
							placement.getColumn().v(), placement.getRow().v(), fromExternalUrl(externalUrl)));
				}
				continue;
			}
			Optional<TopPagePart> topPagePart = activeTopPageParts.stream()
												.filter(c -> c.getToppagePartID().equals(placement.getToppagePartID()))
												.findFirst();
			if (topPagePart.isPresent()) {
					placementDtos.add(new PlacementDto(placement.getPlacementID(), placement.getLayoutID(),
							placement.getColumn().v(), placement.getRow().v(), fromTopPagePart(topPagePart.get())));
			}
		}
		return placementDtos;
	}

	private PlacementPartDto fromTopPagePart(TopPagePart topPagePart) {
		return new PlacementPartDto(topPagePart.getWidth().v(), topPagePart.getHeight().v(),
				topPagePart.getToppagePartID(), topPagePart.getCode().v(), topPagePart.getName().v(),
				topPagePart.getType().value, null);
	}

	private PlacementPartDto fromExternalUrl(ExternalUrl externalUrl) {
		return new PlacementPartDto(externalUrl.getWidth().v(), externalUrl.getHeight().v(), null, null, null, null,externalUrl.getUrl().v());
	}
	
	/**
	 * hien thi top page
	 */
	@Override
	public LayoutForTopPageDto buildLayoutTopPage(Layout layout, List<Placement> placements) {
		List<PlacementDto> placementDtos = buildPlacementTopPage(layout, placements);
		List<FlowMenuPlusDto> flowmenuNew = new ArrayList<FlowMenuPlusDto>();
		List<PlacementDto> placementNew = new ArrayList<PlacementDto>();
		for (PlacementDto placementDto : placementDtos) {
			if(placementDto.getPlacementPartDto().getExternalUrl()== null && placementDto.getPlacementPartDto().getType().intValue() == 2){
					FlowMenuDto flowMenu = flowmenu.getFlowMenu(placementDto.getPlacementPartDto().getTopPagePartID());
					if(flowMenu != null){
						FlowMenuPlusDto fMenu = new FlowMenuPlusDto(flowMenu.getToppagePartID(),
								flowMenu.getTopPageCode(),
								flowMenu.getTopPageName(),
								flowMenu.getType(),
								flowMenu.getWidthSize(),
								flowMenu.getHeightSize(),
								flowMenu.getFileID(),
								flowMenu.getFileName(),
								flowMenu.getDefClassAtr(),
								placementDto.getRow(),
								placementDto.getColumn());
						flowmenuNew.add(fMenu);
					}
					continue;
			}else{
				placementNew.add(placementDto);
			}
		}
		return new LayoutForTopPageDto(layout.getCompanyID(), layout.getLayoutID(), layout.getPgType().value, flowmenuNew,placementNew,null);
	}
	//build placement my page
	private List<PlacementDto> buildPlacementTopPage(Layout layout, List<Placement> placements) {
		List<TopPagePart> activeTopPageParts = topPagePartService.getAllActiveTopPagePart(layout.getCompanyID(), layout.getPgType());
		List<PlacementDto> placementDtos = new ArrayList<PlacementDto>();
		for (Placement placement : placements) {
			if (placement.isExternalUrl()) {
				ExternalUrl externalUrl = placement.getExternalUrl().get();
				placementDtos.add(new PlacementDto(placement.getPlacementID(), placement.getLayoutID(),
						placement.getColumn().v(), placement.getRow().v(), fromExternalUrl(externalUrl)));
				continue;
			}
			TopPagePart topPagePart = activeTopPageParts.stream()
										.filter(c -> c.getToppagePartID()
										.equals(placement.getToppagePartID())).findFirst().orElse(null);
			if (topPagePart != null) {
				placementDtos.add(new PlacementDto(placement.getPlacementID(), placement.getLayoutID(),
							placement.getColumn().v(), placement.getRow().v(), fromTopPagePart(topPagePart)));
			}
		}
		return placementDtos;
	}
	/**
	 * Lay theo chuc vu
	 */
	@Override
	public LayoutAllDto getTopPageForPosition(JobPositionDto jobPosition, TopPageJobSet topPageJob) {
		TopPageSelfSettingDto tpSelfSet = topPageSelfSet.getTopPageSelfSet();
		LayoutForTopPageDto layoutTopPage = null;
		//check my page: use or not use
		Boolean checkMyPage = checkMyPageSet();
		//check top page: setting or not setting
		Boolean checkTopPage = checkTopPageSet();
		if(topPageJob.getPersonPermissionSet().value == 1 && tpSelfSet != null){//check topPageJob: setting or not setting
			//display top page self set (本人トップページ設定)-C
			check = true;
			layoutTopPage = getTopPageByCode(companyId,tpSelfSet.getCode(), 0, 8, check);
			if (!checkMyPage) {//not use my page
				return new LayoutAllDto(null, layoutTopPage, check, checkMyPage, checkTopPage);
			}
			LayoutForMyPageDto layoutMypage = findLayoutMyPage();
			return new LayoutAllDto(layoutMypage, layoutTopPage, check, checkMyPage, checkTopPage);
		}
		//topPageJob: setting
		Optional<TopPagePersonSet> tpPerson = topPagePerson.getbyCode(companyId, employeeId);
		String code = topPageJob.getLoginMenuCode().toString();
		if(tpPerson.isPresent() && !StringUtil.isNullOrEmpty(code, true)){//login menu code & top page person
			//display top page person set (個人別トップページ設定)-B
			check = true;
			layoutTopPage = getTopPageByCode(companyId, tpPerson.get().getTopMenuCode().toString(), tpPerson.get().getLoginSystem().value, tpPerson.get().getMenuClassification().value, check);
			MyPageSettingDto myPage = myPageSetFinder.findByCompanyId(companyId);
			if (myPage.getUseMyPage().intValue() == 0) {//khong su dung my page
				return new LayoutAllDto(null, layoutTopPage, check, false, checkTopPage);
			}
			LayoutForMyPageDto layoutMypage = findLayoutMyPage();
			return new LayoutAllDto(layoutMypage, layoutTopPage, check, true, checkTopPage);
		}
		//not top page person or not login menu code
		if(StringUtil.isNullOrEmpty(code, true)){//login menu code: empty
			//display my page
			check = false;
			layoutTopPage = null;
			if (!checkMyPage) {//not use my page
				return new LayoutAllDto(null, layoutTopPage, check, checkMyPage, checkTopPage);
			}
			//use my page
			LayoutForMyPageDto layoutMypage = findLayoutMyPage();
			return new LayoutAllDto(layoutMypage, layoutTopPage, check, checkMyPage, checkTopPage);
		}
		////display top page job title set (職位別トップページ設定)-A
		check = true;
		layoutTopPage = getTopPageByCode(companyId, topPageJob.getTopMenuCode().toString(), topPageJob.getLoginSystem().value, topPageJob.getMenuClassification().value, check);
		if (!checkMyPage) {//not use my page
			return new LayoutAllDto(null, layoutTopPage, check, checkMyPage, checkTopPage);
		}
		LayoutForMyPageDto layoutMypage = findLayoutMyPage();
		return new LayoutAllDto(layoutMypage, layoutTopPage, check, checkMyPage, checkTopPage);
	}

	/**
	 * Lay khong theo chuc vu
	 */
	@Override
	public LayoutAllDto getTopPageNotPosition() {
		//get data from domain person set (個人別トップページ設定)
		Optional<TopPagePersonSet> tpPerson = topPagePerson.getbyCode(companyId, employeeId);
		LayoutForTopPageDto layoutToppage = null;
		//check my page: use or not use
		Boolean checkMyPage = checkMyPageSet();
		//check top page: setting or not setting
		Boolean checkTopPage = checkTopPageSet();
		if(tpPerson.isPresent()){//co tpPerson
			String code = tpPerson.get().getLoginMenuCode().toString();
			if(!StringUtil.isNullOrEmpty(code,true)){//co login menu code
				//display top page person set (個人別トップページ設定)-B
				check = true;
				layoutToppage = getTopPageByCode(companyId,tpPerson.get().getTopMenuCode().toString(),tpPerson.get().getLoginSystem().value,tpPerson.get().getMenuClassification().value,check);
				if (!checkMyPage) {//not use my page
					return new LayoutAllDto(null, layoutToppage, true, checkMyPage, checkTopPage);
				}
				LayoutForMyPageDto layoutMypage = findLayoutMyPage();
				return new LayoutAllDto(layoutMypage, layoutToppage, check, checkMyPage, checkTopPage);
			}
		}
		check = false;
		//display only my page
		if (!checkMyPage) {//not use my page
			return new LayoutAllDto(null, null, true, checkMyPage, checkTopPage);
		}
		LayoutForMyPageDto layoutMypage = findLayoutMyPage();
		return new LayoutAllDto(layoutMypage, null, false, checkMyPage, checkTopPage);
	}

	/**
	 * check is top page or standard menu
	 */
	@Override
	public LayoutForTopPageDto getTopPageByCode(String companyId, String code, int system, int classification, boolean check) {
		if(classification == 8){//topPage
			TopPageDto topPage = toppageFinder.findByCode(companyId, code, "0");
			Optional<Layout> layout = toppageRepository.find(topPage.getLayoutId(), 2);
			if (layout.isPresent()) {
				List<Placement> placements = placementRepository.findByLayout(topPage.getLayoutId());
				return buildLayoutTopPage(layout.get(), placements);
			}
			return null;
		}
		//standard menu
		Optional<StandardMenu> sMenu = standardMenu.getStandardMenubyCode(companyId, code, system, classification);
		if(sMenu.isPresent()){
			List<FlowMenuPlusDto> flowMenu = null;
			List<PlacementDto> placements = null;
			return new LayoutForTopPageDto(companyId, "", 0, flowMenu, placements, sMenu.get().getUrl());
		}
		return null;
	}
	/**
	 * check my page: setting or not setting
	 */
	@Override
	public Boolean checkMyPageSet(){
		MyPageSettingDto myPage = myPageSetFinder.findByCompanyId(companyId);
		if (myPage!=null && myPage.getUseMyPage().intValue() == 1) {//co su dung my page
			return true;
		}
		return false;
	}
	/**
	 * check top page: use or not use
	 * @return
	 */
	@Override
	public Boolean checkTopPageSet() {
		List<String> lst = new ArrayList<>();
		//lay job position
		JobPositionDto jobPosition = topPageSelfSet.getJobPosition(AppContexts.user().employeeId());
		if(jobPosition != null){
			lst.add(jobPosition.getJobId());
			//lay top page job title set
			TopPageJobSet tpJobSet = topPageJobSet.findByListJobId(companyId, lst).get(0);
			if(tpJobSet != null && tpJobSet.getPersonPermissionSet().value==1){//co job title va duoc setting
				return true;
			}
		}
		return false;
	}
	/**
	 * find lay out my page
	 * @return
	 */
	@Override
	public LayoutForMyPageDto findLayoutMyPage() {
		LayoutForMyPageDto layoutMypage = null;
		MyPage mPage = mypage.getMyPage(employeeId);
		if(mPage == null){//register my page
			String layoutId = UUID.randomUUID().toString();
			MyPage mypageNew = new MyPage(employeeId,layoutId);
			mypage.addMyPage(mypageNew);
			layoutMypage = new LayoutForMyPageDto(employeeId,layoutId, 2, null, null);
			return layoutMypage;
		}
		Optional<Layout> layout = toppageRepository.find(mPage.getLayoutId(),0);
		if(layout.isPresent()){
			List<Placement> placements = placementRepository.findByLayout(mPage.getLayoutId());
			if(!placements.isEmpty()){
				layoutMypage =buildLayoutDto(layout.get(),placements) ;
			}
		}else{
			layoutMypage = new LayoutForMyPageDto(employeeId,mPage.getLayoutId(),2,null,null);
		}
		return layoutMypage;
	}

}
