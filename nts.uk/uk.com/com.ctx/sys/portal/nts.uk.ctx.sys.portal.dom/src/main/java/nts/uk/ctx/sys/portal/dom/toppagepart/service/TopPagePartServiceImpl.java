package nts.uk.ctx.sys.portal.dom.toppagepart.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.arc.enums.EnumConstant;
import nts.uk.ctx.sys.portal.dom.enums.TopPagePartType;
import nts.uk.ctx.sys.portal.dom.enums.UseDivision;
import nts.uk.ctx.sys.portal.dom.flowmenu.FlowMenuRepository;
import nts.uk.ctx.sys.portal.dom.layout.PGType;
import nts.uk.ctx.sys.portal.dom.mypage.setting.MyPageSetting;
import nts.uk.ctx.sys.portal.dom.mypage.setting.MyPageSettingRepository;
import nts.uk.ctx.sys.portal.dom.mypage.setting.TopPagePartUseSetting;
import nts.uk.ctx.sys.portal.dom.placement.service.PlacementService;
import nts.uk.ctx.sys.portal.dom.toppagepart.TopPagePart;
import nts.uk.ctx.sys.portal.dom.toppagepart.TopPagePartRepository;

/**
 * @author HieuLT
 */
@Stateless
public class TopPagePartServiceImpl implements TopPagePartService{

	@Inject
	private TopPagePartRepository topPagePartRepository;
	
	@Inject
	private FlowMenuRepository flowMenuRepository;
	
	@Inject
	private PlacementService placementService;
	
	@Inject
	private MyPageSettingRepository	myPageSettingRepository;
	
	@Override
	public boolean isExist(String companyID, String code, int type) {
		Optional<TopPagePart> topPage = topPagePartRepository.findByCodeAndType(companyID, code, type);
		return topPage.isPresent();
	}

	@Override
	public void deleteTopPagePart(String companyID, String topPagePartID) {
		Optional<TopPagePart> checkTopPagePart = topPagePartRepository.find(topPagePartID);
		if (checkTopPagePart.isPresent()) {
			topPagePartRepository.remove(companyID, topPagePartID);
			myPageSettingRepository.removeTopPagePartUseSettingById(companyID, topPagePartID);
			placementService.deletePlacementByTopPagePart(companyID, topPagePartID);
		}
	}

	@Override
	public List<EnumConstant> getAllActiveTopPagePartType(String companyID, PGType pgType) {
		if (pgType == PGType.TOPPAGE) {
			return getTopPagePartType(companyID);
		}
		else if (pgType == PGType.TITLEMENU) {
			return getTitleMenuPartType(companyID);
		}
		else if (pgType == PGType.MYPAGE) {
			return getMyPagePartType(companyID);
		}
		return null;
	}

	@Override
	public List<TopPagePart> getAllActiveTopPagePart(String companyID, PGType pgType) {
		List<EnumConstant> activeTopPagePartTypes = getAllActiveTopPagePartType(companyID, pgType);
		List<Integer> activeTopPagePartTypeIDs = activeTopPagePartTypes.stream().map(c -> c.getValue()).collect(Collectors.toList());
		
		List<TopPagePart> listTopPagePart = new ArrayList<TopPagePart>();
		if (pgType == PGType.TOPPAGE) {
			listTopPagePart = topPagePartRepository.findByTypes(companyID, activeTopPagePartTypeIDs);
		}
		else if (pgType == PGType.TITLEMENU) {
			listTopPagePart = topPagePartRepository.findByTypes(companyID, activeTopPagePartTypeIDs);
		}
		else if (pgType == PGType.MYPAGE) {
			List<String> activeTopPagePartIDs = getMyPageActivePartIDs(companyID);
			listTopPagePart = topPagePartRepository.findByTypesAndIDs(companyID, activeTopPagePartTypeIDs, activeTopPagePartIDs);
		}
		
		List<TopPagePart> result =  new ArrayList<TopPagePart>();
		// Get list FlowMenu
		val listFlowMenu = listTopPagePart.stream().filter(c -> c.isFlowMenu()).collect(Collectors.toList());
		result.addAll(flowMenuRepository.findByCodes(companyID, listFlowMenu.stream().map(c -> c.getToppagePartID()).collect(Collectors.toList())));
		
		return result;
	}
	
	/**
	 * Get all TopPage's active TopPagePartType
	 * @return List EnumConstant of TopPagePartType
	 */
	private List<EnumConstant> getTopPagePartType(String companyID) {
		return EnumAdaptor.convertToValueNameList(TopPagePartType.class);
	}
	
	/**
	 * Get all TitleMenu's active TopPagePartType
	 * @return List EnumConstant of TopPagePartType
	 */
	private List<EnumConstant> getTitleMenuPartType(String companyID) {
		List<EnumConstant> listTopPagePartType = EnumAdaptor.convertToValueNameList(TopPagePartType.class);
		List<Integer> checkingTopPagePartTypeValues = new ArrayList<Integer>();
		checkingTopPagePartTypeValues.add(TopPagePartType.FlowMenu.value);
		checkingTopPagePartTypeValues.add(TopPagePartType.ExternalUrl.value);
		return listTopPagePartType.stream().filter(o -> checkingTopPagePartTypeValues.contains(o.getValue())).collect(Collectors.toList());
	} 

	/**
	 * Get all MyPage's active TopPagePartType
	 * @return List EnumConstant of TopPagePartType
	 */
	private List<EnumConstant> getMyPagePartType(String companyID) {
		List<EnumConstant> listTopPagePartType = EnumAdaptor.convertToValueNameList(TopPagePartType.class);
		List<Integer> checkingTopPagePartTypeValues = new ArrayList<Integer>();
		Optional<MyPageSetting> checkMyPageSetting = myPageSettingRepository.findByCompanyId(companyID);
		if (checkMyPageSetting.isPresent()) {
			MyPageSetting myPageSetting = checkMyPageSetting.get();
			if (myPageSetting.useMyPage()) {
				if (myPageSetting.useStandarWidget())
					checkingTopPagePartTypeValues.add(TopPagePartType.StandardWidget.value);
				if (myPageSetting.useOptionalWidget())
					checkingTopPagePartTypeValues.add(TopPagePartType.OptionalWidget.value);
				if (myPageSetting.useDashboard())
					checkingTopPagePartTypeValues.add(TopPagePartType.DashBoard.value);
				if (myPageSetting.useFlowMenu())
					checkingTopPagePartTypeValues.add(TopPagePartType.FlowMenu.value);
				if (myPageSetting.isAllowExternalUrlPermission())
					checkingTopPagePartTypeValues.add(TopPagePartType.ExternalUrl.value);
			}
		}
		return (listTopPagePartType.stream().filter(o -> checkingTopPagePartTypeValues.contains(o.getValue())).collect(Collectors.toList()));
	}
	
	/**
	 * Get all MyPage's active TopPagePart
	 * @return List EnumConstant of TopPagePartType
	 */
	private List<String> getMyPageActivePartIDs(String companyID) {
		List<String> activeTopPagePartIDs = new ArrayList<String>(); 
		List<TopPagePartUseSetting> topPagePartSettings = myPageSettingRepository.findTopPagePartUseSettingByCompanyId(companyID);
		for (TopPagePartUseSetting topPagePartUseSetting : topPagePartSettings) {
			if (topPagePartUseSetting.getUseDivision() == UseDivision.Use)
				activeTopPagePartIDs.add(topPagePartUseSetting.getTopPagePartId());
		}
		return activeTopPagePartIDs;
	}
}