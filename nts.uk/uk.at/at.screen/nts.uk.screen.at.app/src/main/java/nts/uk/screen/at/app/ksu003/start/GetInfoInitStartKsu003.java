package nts.uk.screen.at.app.ksu003.start;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.schedule.dom.displaysetting.DisplaySettingByDateForCmpRepository;
import nts.uk.ctx.at.schedule.dom.displaysetting.DisplaySettingByDateForCompany;
import nts.uk.ctx.at.schedule.dom.displaysetting.DisplaySettingByDateForOrgRepository;
import nts.uk.ctx.at.schedule.dom.displaysetting.DisplaySettingByDateForOrganization;
import nts.uk.ctx.at.schedule.dom.displaysetting.GetDisplaySettingByDateService;
import nts.uk.ctx.at.schedule.dom.displaysetting.functioncontrol.ScheFunctionControl;
import nts.uk.ctx.at.schedule.dom.displaysetting.functioncontrol.ScheFunctionControlRepository;
import nts.uk.ctx.at.shared.dom.workmanagementmultiple.WorkManagementMultiple;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.WorkplaceInfo;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.WorkplaceGroupAdapter;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.adapter.WorkplaceGroupImport;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.AffWorkplaceAdapter;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.WorkplaceExportServiceAdapter;
import nts.uk.screen.at.app.ksu003.start.dto.DisplayInfoOrganizationDto;
import nts.uk.screen.at.app.ksu003.start.dto.DisplaySettingByDateDto;
import nts.uk.screen.at.app.ksu003.start.dto.GetInfoInitStartKsu003Dto;
import nts.uk.screen.at.app.ksu003.start.dto.ScheFunctionControlDto;
import nts.uk.screen.at.app.ksu003.start.dto.WorkManageMultiDto;
import nts.uk.screen.at.app.query.kcp013.GetAllWorkingHoursQuery;
import nts.uk.shr.com.context.AppContexts;
/**
 * 初期起動の情報取得
 * UKDesign.UniversalK.就業.KSU_スケジュール.KSU003_個人スケジュール修正(日付別).A：個人スケジュール修正(日付別).メニュー別OCD
 * @author phongtq
 *
 */
@Stateless
public class GetInfoInitStartKsu003 {
	
	@Inject
	private DisplaySettingByDateForOrgRepository orgRepository; // 組織別スケジュール修正日付別の表示設定Repository.get (対象組織識別情報)
	
	@Inject
	private DisplaySettingByDateForCmpRepository cmpRepository; // 会社別スケジュール修正日付別の表示設定Repository.get (会社ID)	
	
	@Inject
	private WorkplaceGroupAdapter groupAdapter;

	@Inject
	private WorkplaceExportServiceAdapter serviceAdapter;

	@Inject
	private AffWorkplaceAdapter wplAdapter;
	
	@Inject
	private GetAllWorkingHoursQuery hoursQuery;
	
	@Inject
	private ScheFunctionControlRepository controlRepository;
	
	public GetInfoInitStartKsu003Dto getData(TargetOrgIdenInfor targetOrg) {
		
		String cid = AppContexts.user().companyId();
		
		// 1 .取得する(Require, 対象組織識別情報) : スケジュール修正日付別の表示設定
		GetDisplayRequireImpl requireImpl = new GetDisplayRequireImpl(orgRepository, cmpRepository);
		DisplaySettingByDateDto byDateDto = DisplaySettingByDateDto.convert(GetDisplaySettingByDateService.get(requireImpl, targetOrg));
		
		// 2 .組織の表示情報を取得する(Require, 年月日) : 組織の表示情報
		TargetOrgIdenInforImpl idenInforImpl = new TargetOrgIdenInforImpl(groupAdapter, serviceAdapter, wplAdapter);
		DisplayInfoOrganizationDto organizationDto = DisplayInfoOrganizationDto.convert(targetOrg.getDisplayInfor(idenInforImpl, GeneralDate.today()));
		
		// 3 .複数回勤務管理を取得する(会社ID) : 複数回勤務管理
		Optional<WorkManagementMultiple> manageMulti = hoursQuery.getUseDistinction();
		WorkManageMultiDto manageMultiDto = WorkManageMultiDto.convert(manageMulti);
		
		// 4 .時刻修正可能取得する(ログイン会社ID) : スケジュール修正の機能制御
		Optional<ScheFunctionControl> scheFunc = controlRepository.get(cid);
		ScheFunctionControlDto functionControlDto = ScheFunctionControlDto.convert(scheFunc);
		
		GetInfoInitStartKsu003Dto dto = new GetInfoInitStartKsu003Dto(byDateDto, organizationDto, manageMultiDto, functionControlDto);
		
		return dto;
	}
	
	@AllArgsConstructor
	private static class GetDisplayRequireImpl implements GetDisplaySettingByDateService.Require{
		
		@Inject
		private DisplaySettingByDateForOrgRepository orgRepository; // 組織別スケジュール修正日付別の表示設定Repository.get (対象組織識別情報)
		
		@Inject
		private DisplaySettingByDateForCmpRepository cmpRepository; // 会社別スケジュール修正日付別の表示設定Repository.get (会社ID)	
		
		@Override
		public Optional<DisplaySettingByDateForOrganization> getOrg(TargetOrgIdenInfor targetOrg) {
			String cid = AppContexts.user().companyId();
			Optional<DisplaySettingByDateForOrganization> displaySet =  orgRepository.get(cid, targetOrg);
			return displaySet;
		}

		@Override
		public Optional<DisplaySettingByDateForCompany> getCmp() {
			String cid = AppContexts.user().companyId();
			Optional<DisplaySettingByDateForCompany> displaySet = cmpRepository.get(cid);
			return displaySet;
		}
		
	}
	
	@AllArgsConstructor
	private static class TargetOrgIdenInforImpl implements TargetOrgIdenInfor.Require {
		@Inject
		private WorkplaceGroupAdapter groupAdapter;

		@Inject
		private WorkplaceExportServiceAdapter serviceAdapter;

		@Inject
		private AffWorkplaceAdapter wplAdapter;

		@Override
		public List<WorkplaceGroupImport> getSpecifyingWorkplaceGroupId(List<String> workplacegroupId) {
			return groupAdapter.getbySpecWorkplaceGroupID(workplacegroupId);
		}

		@Override
		public List<WorkplaceInfo> getWorkplaceInforFromWkpIds(List<String> listWorkplaceId, GeneralDate baseDate) {
			String companyId = AppContexts.user().companyId();
			List<WorkplaceInfo> workplaceInfos = serviceAdapter
					.getWorkplaceInforByWkpIds(companyId, listWorkplaceId, baseDate).stream()
					.map(mapper -> new WorkplaceInfo(mapper.getWorkplaceId(),
							Optional.ofNullable(mapper.getWorkplaceCode()),
							Optional.ofNullable(mapper.getWorkplaceName()),
							Optional.ofNullable(mapper.getWorkplaceExternalCode()),
							Optional.ofNullable(mapper.getWorkplaceGenericName()),
							Optional.ofNullable(mapper.getWorkplaceDisplayName()),
							Optional.ofNullable(mapper.getHierarchyCode())))
					.collect(Collectors.toList());
			return workplaceInfos;
		}

		@Override
		public List<String> getWKPID(String WKPGRPID) {
			String CID = AppContexts.user().companyId();
			return wplAdapter.getWKPID(CID, WKPGRPID);
		}
	}
	
}
