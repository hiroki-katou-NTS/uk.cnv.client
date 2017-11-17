package nts.uk.ctx.at.request.dom.application.overtime.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationRepository;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;
import nts.uk.ctx.at.request.dom.application.UseAtr;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.EmployeeRequestAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.bs.dto.SWkpHistImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.schedule.shift.businesscalendar.specificdate.WpSpecificDateSettingAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.schedule.shift.businesscalendar.specificdate.dto.WpSpecificDateSettingImport;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.output.AppCommonSettingOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.OtherCommonAlgorithm;
import nts.uk.ctx.at.request.dom.application.overtime.AppOverTime;
import nts.uk.ctx.at.request.dom.application.overtime.OverTimeAtr;
import nts.uk.ctx.at.request.dom.application.overtime.OverTimeInput;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeInputRepository;
import nts.uk.ctx.at.request.dom.application.overtime.OvertimeRepository;
import nts.uk.ctx.at.request.dom.overtimeinstruct.OverTimeInstruct;
import nts.uk.ctx.at.request.dom.overtimeinstruct.OvertimeInstructRepository;
import nts.uk.ctx.at.request.dom.setting.applicationreason.ApplicationReason;
import nts.uk.ctx.at.request.dom.setting.applicationreason.ApplicationReasonRepository;
import nts.uk.ctx.at.request.dom.setting.company.applicationapprovalsetting.overtimerestappcommon.OvertimeRestAppCommonSetting;
import nts.uk.ctx.at.request.dom.setting.company.divergencereason.DivergenceReason;
import nts.uk.ctx.at.request.dom.setting.company.divergencereason.DivergenceReasonRepository;
import nts.uk.ctx.at.request.dom.setting.request.application.applicationsetting.ApplicationSetting;
import nts.uk.ctx.at.request.dom.setting.request.application.applicationsetting.ApplicationSettingRepository;
import nts.uk.ctx.at.request.dom.setting.request.application.apptypediscretesetting.AppTypeDiscreteSetting;
import nts.uk.ctx.at.request.dom.setting.request.application.apptypediscretesetting.AppTypeDiscreteSettingRepository;
import nts.uk.ctx.at.request.dom.setting.request.gobackdirectlycommon.primitive.AppDisplayAtr;
import nts.uk.ctx.at.request.dom.setting.request.gobackdirectlycommon.primitive.InitValueAtr;
import nts.uk.ctx.at.request.dom.setting.requestofeach.AtWorkAtr;
import nts.uk.ctx.at.request.dom.setting.requestofeach.DisplayFlg;
import nts.uk.ctx.at.request.dom.setting.requestofeach.RequestAppDetailSetting;
import nts.uk.ctx.at.shared.dom.bonuspay.primitives.WorkingTimesheetCode;
import nts.uk.ctx.at.shared.dom.bonuspay.primitives.WorkplaceId;
import nts.uk.ctx.at.shared.dom.bonuspay.repository.BPSettingRepository;
import nts.uk.ctx.at.shared.dom.bonuspay.repository.BPTimeItemRepository;
import nts.uk.ctx.at.shared.dom.bonuspay.repository.CPBonusPaySettingRepository;
import nts.uk.ctx.at.shared.dom.bonuspay.repository.PSBonusPaySettingRepository;
import nts.uk.ctx.at.shared.dom.bonuspay.repository.WPBonusPaySettingRepository;
import nts.uk.ctx.at.shared.dom.bonuspay.repository.WTBonusPaySettingRepository;
import nts.uk.ctx.at.shared.dom.bonuspay.setting.BonusPaySetting;
import nts.uk.ctx.at.shared.dom.bonuspay.setting.CompanyBonusPaySetting;
import nts.uk.ctx.at.shared.dom.bonuspay.setting.PersonalBonusPaySetting;
import nts.uk.ctx.at.shared.dom.bonuspay.setting.WorkingTimesheetBonusPaySetting;
import nts.uk.ctx.at.shared.dom.bonuspay.setting.WorkplaceBonusPaySetting;
import nts.uk.ctx.at.shared.dom.bonuspay.timeitem.BonusPayTimeItem;
import nts.uk.ctx.at.shared.dom.employmentrule.hourlate.breaktime.breaktimeframe.BreaktimeFrame;
import nts.uk.ctx.at.shared.dom.employmentrule.hourlate.breaktime.breaktimeframe.BreaktimeFrameRepository;
import nts.uk.ctx.at.shared.dom.employmentrule.hourlate.overtime.overtimeframe.OvertimeFrame;
import nts.uk.ctx.at.shared.dom.employmentrule.hourlate.overtime.overtimeframe.OvertimeFrameRepository;
import nts.uk.ctx.at.shared.dom.worktimeset_old.WorkTimeSet;
import nts.uk.ctx.at.shared.dom.worktimeset_old.WorkTimeSetRepository;

@Stateless
public class OvertimePreProcessImpl implements IOvertimePreProcess{
	
	final String DATE_FORMAT = "yyyy/MM/dd";
	@Inject
	private OvertimeInstructRepository overtimeInstructRepository;
	@Inject
	private ApplicationSettingRepository applicationSettingRepository;
	@Inject
	private AppTypeDiscreteSettingRepository discreteRepo;
	@Inject
	private OtherCommonAlgorithm otherCommonAlgorithm;
	@Inject
	private EmployeeRequestAdapter employeeAdapter;
	@Inject
	private ApplicationReasonRepository applicationReasonRepository;
	@Inject
	private DivergenceReasonRepository diReasonRepository;
	@Inject
	private ApplicationRepository applicationRepository;
	
	@Inject
	private OvertimeRepository overtimeRepository;
	
	@Inject
	private OvertimeInputRepository overtimeInputRepository;
	@Inject
	private WpSpecificDateSettingAdapter wpSpecificDateSettingAdapter;
	@Inject
	private WTBonusPaySettingRepository wTBonusPaySettingRepository;
	@Inject
	private PSBonusPaySettingRepository pSBonusPaySettingRepository;
	@Inject
	private WPBonusPaySettingRepository wPBonusPaySettingRepository;
	@Inject
	private CPBonusPaySettingRepository cPBonusPaySettingRepository;
	@Inject
	private BPSettingRepository bPSettingRepository;
	@Inject
	private OvertimeFrameRepository overtimeFrameRepository;
	@Inject
	private BPTimeItemRepository bPTimeItemRepository;
	@Inject
	private BreaktimeFrameRepository breaktimeFrameRep;
	@Inject
	private WorkTimeSetRepository workTimeSetRepository;
	
	
	@Override
	public OvertimeInstructInfomation getOvertimeInstruct(AppCommonSettingOutput appCommonSettingOutput,String appDate,String employeeID) {
		OvertimeInstructInfomation overtimeInstructInformation = new OvertimeInstructInfomation();
		if(appCommonSettingOutput != null){
			int useAtr = appCommonSettingOutput.requestOfEachCommon.getRequestAppDetailSettings().get(0).getInstructionUseSetting().getInstructionUseDivision().value;
			if(useAtr == UseAtr.USE.value){
				if(appDate != null){
					overtimeInstructInformation.setDisplayOvertimeInstructInforFlg(true);
					OverTimeInstruct overtimeInstruct = overtimeInstructRepository.getOvertimeInstruct(GeneralDate.fromString(appDate, DATE_FORMAT), employeeID);
					if(overtimeInstruct != null){
						overtimeInstructInformation.setOvertimeInstructInfomation(overtimeInstruct.getInstructDate().toString() 
								+" "+ convert(overtimeInstruct.getStartClock().v())
								+"~"+ convert(overtimeInstruct.getEndClock().v()) 
								+" "+ employeeAdapter.getEmployeeName(overtimeInstruct.getTargetPerson())
								+" ("+employeeAdapter.getEmployeeName(overtimeInstruct.getInstructor()) + ")");
					}else{
						overtimeInstructInformation.setOvertimeInstructInfomation(GeneralDate.fromString(appDate, DATE_FORMAT) + "の残業指示はありません。");
					}
				}
			}else{
				overtimeInstructInformation.setDisplayOvertimeInstructInforFlg(false);
			}
		}
		return overtimeInstructInformation;
	}

	@Override
	public DisplayPrePost getDisplayPrePost(String companyID, int uiType, String appDate) {
		Optional<ApplicationSetting> applicationSetting = applicationSettingRepository.getApplicationSettingByComID(companyID);
		DisplayPrePost result = new DisplayPrePost();
		if(applicationSetting.isPresent()){
			// if display then check What call UI?
			if(applicationSetting.get().getDisplayPrePostFlg().value == AppDisplayAtr.DISPLAY.value){
				result.setDisplayPrePostFlg(AppDisplayAtr.DISPLAY.value);
				/**
				 * check UI
				 * 0: メニューから起動 :menu
				 * other: 日別修正、トップページアラームから起動,残業指示から起動
				 */
				if(uiType == 0){
					Optional<AppTypeDiscreteSetting> discreteSetting = discreteRepo.getAppTypeDiscreteSettingByAppType(companyID, ApplicationType.OVER_TIME_APPLICATION.value);
					if(discreteSetting.isPresent()){
						result.setPrePostAtr(discreteSetting.get().getPrePostInitFlg().value);
					}
				}else{
					//事後申請として起動する(khoi dong cai xin sau len)
					result.setPrePostAtr(InitValueAtr.POST.value);
					
				}
			}else{
				//if not display
				result.setDisplayPrePostFlg(AppDisplayAtr.NOTDISPLAY.value);
				result.setPrePostAtr(this.otherCommonAlgorithm.preliminaryJudgmentProcessing(EnumAdaptor.valueOf(ApplicationType.OVER_TIME_APPLICATION.value,ApplicationType.class), GeneralDate.fromString(appDate, DATE_FORMAT)).value);
			}
		}
		return result;
	}

	@Override
	public void getWorkingHours(String companyID, String employeeID, String appDate,
			RequestAppDetailSetting requestAppDetailSetting) {
		if(requestAppDetailSetting != null){
			if(appDate != null){
				int atWorkAtr = requestAppDetailSetting.getAtworkTimeBeginDisFlg().value;
				if(atWorkAtr == AtWorkAtr.DISPLAY.value){
					// team anh lương
				}
			}
		}
	}

	@Override
	public boolean getRestTime(RequestAppDetailSetting requestAppDetailSetting) {
		if(requestAppDetailSetting != null){
			if(requestAppDetailSetting.getBreakInputFieldDisFlg().value == DisplayFlg.DISPLAY.value){
				return true;
			}else{
				return false;
			}
		}
		return false;
	}

	@Override
	public List<OvertimeFrame> getOvertimeHours(int overtimeAtr,String companyID) {
		List<OvertimeFrame> overtimeFrames = new ArrayList<>();
		//早出残業の場合
		if(overtimeAtr == OverTimeAtr.PREOVERTIME.value){
			overtimeFrames = this.overtimeFrameRepository.getOvertimeFrameByCID(companyID,UseAtr.USE.value);
		}
		//通常残業の場合
		if(overtimeAtr == OverTimeAtr.REGULAROVERTIME.value){
			overtimeFrames = this.overtimeFrameRepository.getOvertimeFrameByCID(companyID,UseAtr.USE.value);
		}
		//早出残業・通常残業の場合
		if(overtimeAtr == OverTimeAtr.ALL.value){
			overtimeFrames = this.overtimeFrameRepository.getOvertimeFrameByCID(companyID,UseAtr.USE.value);
		}
		return overtimeFrames;
	}
	@Override
	public List<BreaktimeFrame> getBreaktimeFrame(String companyID) {
		
		return this.breaktimeFrameRep.getBreaktimeFrameByCID(companyID, UseAtr.USE.value);
	}
	
	@Override
	public List<BonusPayTimeItem> getBonusTime(String employeeID, Optional<OvertimeRestAppCommonSetting> overtimeRestAppCommonSet,
			String appDate,String companyID,SiftType siftType) {
		List<BonusPayTimeItem> result = new ArrayList<>();
		WpSpecificDateSettingImport wpSpecificDateSettingImport = new WpSpecificDateSettingImport(null,null);
		if(overtimeRestAppCommonSet.get().getBonusTimeDisplayAtr().value == UseAtr.USE.value){
			// アルゴリズム「社員所属職場履歴を取得」を実行する
			SWkpHistImport sWkpHistImport = employeeAdapter.getSWkpHistByEmployeeID(employeeID, GeneralDate.fromString(appDate, DATE_FORMAT));
			//アルゴリズム「職場の特定日設定を取得する」を実行する (hung lam)
			if(sWkpHistImport != null){
				wpSpecificDateSettingImport = this.wpSpecificDateSettingAdapter.workplaceSpecificDateSettingService(companyID, sWkpHistImport.getWorkplaceId(), GeneralDate.fromString(appDate, DATE_FORMAT));
			}
			if(wpSpecificDateSettingImport.getNumberList() != null){
				List<BonusPayTimeItem> bonusPayTimeItemSpecs = this.bPTimeItemRepository.getListSpecialBonusPayTimeItemInUse(companyID);
				for(BonusPayTimeItem bonusItem : bonusPayTimeItemSpecs){
					result.add(bonusItem);
				}
			}
			List<BonusPayTimeItem>  bonusPayTimeItems = this.bPTimeItemRepository.getListBonusPayTimeItemInUse(companyID);
			for(BonusPayTimeItem bonusItem : bonusPayTimeItems){
				result.add(bonusItem);
			}
		}
		return result;
	}

	@Override
	public List<ApplicationReason> getApplicationReasonType(String companyID, int appType, Optional<AppTypeDiscreteSetting> appTypeDiscreteSetting) {
		if(appTypeDiscreteSetting.get().getTypicalReasonDisplayFlg().value == AppDisplayAtr.DISPLAY.value){
			List<ApplicationReason> applicationReasons = applicationReasonRepository.getReasonByAppType(companyID,
					appType);
			return applicationReasons;
		}
		return null;
	}

	@Override
	public boolean displayAppReasonContentFlg(Optional<AppTypeDiscreteSetting> appTypeDiscreteSetting) {
		if(appTypeDiscreteSetting.get().getDisplayReasonFlg().value == AppDisplayAtr.DISPLAY.value){
			return true;
		}
		return false;
	}

	@Override
	public List<DivergenceReason> getDivergenceReasonForm(String companyID, int appType,
			Optional<OvertimeRestAppCommonSetting> overtimeRestAppCommonSet) {
		if(overtimeRestAppCommonSet.get().getDivergenceReasonFormAtr().value == UseAtr.USE.value){
			List<DivergenceReason> divergenceReasons = diReasonRepository.getDivergenceReason(companyID, ApplicationType.OVER_TIME_APPLICATION.value);
			return divergenceReasons;
		}
		return null;
	}

	@Override
	public boolean displayDivergenceReasonInput(Optional<OvertimeRestAppCommonSetting> overtimeRestAppCommonSet) {
		if(overtimeRestAppCommonSet.get().getDivergenceReasonInputAtr().value == UseAtr.USE.value){
			return true;
		}
		return false;
	}

	@Override
	public AppOverTime getPreApplication(String employeeId,
			Optional<OvertimeRestAppCommonSetting> overtimeRestAppCommonSet, String appDate, int prePostAtr) {
		AppOverTime result = new AppOverTime();
		if(prePostAtr == InitValueAtr.POST.value){
			Application applicationOvertime = new Application();
			if(overtimeRestAppCommonSet.get().getPreDisplayAtr().value == UseAtr.USE.value){
				Optional<Application> application = this.applicationRepository.getApp(employeeId,  GeneralDate.fromString(appDate, DATE_FORMAT), PrePostAtr.PREDICT.value, ApplicationType.OVER_TIME_APPLICATION.value);
				if(application.isPresent()){
					applicationOvertime.setApplicationDate(application.get().getApplicationDate());
					Optional<AppOverTime> appOvertime = this.overtimeRepository.getAppOvertime(application.get().getCompanyID(), application.get().getApplicationID());
					if(appOvertime.isPresent()){
						result.setWorkTypeCode(appOvertime.get().getWorkTypeCode());
						result.setSiftCode(appOvertime.get().getSiftCode());
						result.setWorkClockFrom1(appOvertime.get().getWorkClockFrom1());
						result.setWorkClockTo1(appOvertime.get().getWorkClockTo1());
						result.setWorkClockFrom2(appOvertime.get().getWorkClockFrom2());
						result.setWorkClockTo2(appOvertime.get().getWorkClockTo2());
						
						List<OverTimeInput> overtimeInputs = overtimeInputRepository.getOvertimeInput(appOvertime.get().getCompanyID(), appOvertime.get().getAppID());
						result.setOverTimeInput(overtimeInputs);
						result.setOverTimeShiftNight(appOvertime.get().getOverTimeShiftNight());
						result.setFlexExessTime(appOvertime.get().getFlexExessTime());
						result.setApplication(application.get());
						result.setAppID(appOvertime.get().getAppID());
					}
				}
			}
		}
		return result;
	}

	@Override
	public void getResultContentActual(int prePostAtr,String siftCode,String companyID) {
		// TODO Auto-generated method stub
		if(PrePostAtr.POSTERIOR.value == prePostAtr){
			Optional<WorkTimeSet> workTimeSet = workTimeSetRepository.findByCode(companyID, siftCode);
			if(workTimeSet.isPresent()){
				
				
			}
		}
	}
	private String convert(int minute){
		String hourminute ="";
		if(minute == -1){
			return null;
		}else if (minute == 0) {
			hourminute = "00:00";
		}else{
			int hour = minute/60;
			int minutes = minute%60;
			hourminute = (hour < 10 ? ("0" + hour) : hour ) + ":"+ (minutes < 10 ? ("0" + minutes) : minutes);
		}
		return hourminute;
	}

	@Override
	public Optional<BonusPaySetting> getBonusPaySetting(String employeeID, String siftCode, String companyID,
			SWkpHistImport sWkpHistImport) {
		Optional<BonusPaySetting> bonusPaySetting = Optional.empty();
		Optional<WorkingTimesheetBonusPaySetting> workingTimesheetBonusPaySetting = Optional.empty();
		
			workingTimesheetBonusPaySetting = this.wTBonusPaySettingRepository.getWTBPSetting(companyID, new WorkingTimesheetCode(siftCode));
		
		if(!workingTimesheetBonusPaySetting.isPresent()){
			Optional<PersonalBonusPaySetting> personalBonusPaySetting =this.pSBonusPaySettingRepository.getPersonalBonusPaySetting(employeeID);
			
			if(!personalBonusPaySetting.isPresent()){
				Optional<WorkplaceBonusPaySetting> workplaceBonusPaySetting = this.wPBonusPaySettingRepository.getWPBPSetting(new WorkplaceId(sWkpHistImport.getWorkplaceId()));
				if(!workplaceBonusPaySetting.isPresent()){
					Optional<CompanyBonusPaySetting> companyBonusPaySetting = this.cPBonusPaySettingRepository.getSetting(companyID);
					if(!companyBonusPaySetting.isPresent()){
						return bonusPaySetting;
					}else{
						bonusPaySetting = bPSettingRepository.getBonusPaySetting(companyID, companyBonusPaySetting.get().getBonusPaySettingCode());
					}
				}else{
					bonusPaySetting = bPSettingRepository.getBonusPaySetting(companyID, workplaceBonusPaySetting.get().getBonusPaySettingCode());
				}
			}else{
				bonusPaySetting = bPSettingRepository.getBonusPaySetting(companyID, personalBonusPaySetting.get().getBonusPaySettingCode());
			}
		}else{
			bonusPaySetting = bPSettingRepository.getBonusPaySetting(companyID, workingTimesheetBonusPaySetting.get().getBonusPaySettingCode());
		}
		return null;
	}
	
}
