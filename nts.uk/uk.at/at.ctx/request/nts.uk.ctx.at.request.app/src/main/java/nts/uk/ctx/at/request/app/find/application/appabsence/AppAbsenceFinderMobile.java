package nts.uk.ctx.at.request.app.find.application.appabsence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.app.command.application.kdl035.HolidayWorkAssociationStart;
import nts.uk.ctx.at.request.app.command.application.kdl035.Kdl035InputData;
import nts.uk.ctx.at.request.app.command.application.kdl035.Kdl035OutputData;
import nts.uk.ctx.at.request.app.command.application.kdl036.HolidayAssociationStart;
import nts.uk.ctx.at.request.app.command.application.kdl036.Kdl036InputData;
import nts.uk.ctx.at.request.app.command.application.kdl036.Kdl036OutputData;
import nts.uk.ctx.at.request.app.find.application.appabsence.dto.AbsenceCheckRegisterDto;
import nts.uk.ctx.at.request.app.find.application.appabsence.dto.AppAbsenceStartInfoDto;
import nts.uk.ctx.at.request.app.find.application.appabsence.dto.AppForLeaveStartOutputDto;
import nts.uk.ctx.at.request.app.find.application.appabsence.dto.ChangeDateParamMobile;
import nts.uk.ctx.at.request.app.find.application.appabsence.dto.CheckInsertMobileParam;
import nts.uk.ctx.at.request.app.find.application.appabsence.dto.DetailInfoParam;
import nts.uk.ctx.at.request.app.find.application.appabsence.dto.MaxDaySpecHdDto;
import nts.uk.ctx.at.request.app.find.application.appabsence.dto.MaxHolidayDayParamMobile;
import nts.uk.ctx.at.request.app.find.application.appabsence.dto.SelectTypeHolidayParam;
import nts.uk.ctx.at.request.app.find.application.appabsence.dto.SelectWorkOutputDto;
import nts.uk.ctx.at.request.app.find.application.appabsence.dto.SelectWorkTimeHolidayParam;
import nts.uk.ctx.at.request.app.find.application.appabsence.dto.SelectWorkTypeHolidayParam;
import nts.uk.ctx.at.request.app.find.application.appabsence.dto.StartMobileParam;
import nts.uk.ctx.at.request.app.find.application.appabsence.dto.VacationCheckOutputDto;
import nts.uk.ctx.at.request.app.find.application.common.AppDispInfoWithDateDto;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.appabsence.ApplyForLeave;
import nts.uk.ctx.at.request.dom.application.appabsence.HolidayAppType;
import nts.uk.ctx.at.request.dom.application.appabsence.apptimedigest.TimeDigestApplication;
import nts.uk.ctx.at.request.dom.application.appabsence.service.AbsenceServiceProcess;
import nts.uk.ctx.at.request.dom.application.appabsence.service.AbsenceServiceProcessMobile;
import nts.uk.ctx.at.request.dom.application.appabsence.service.output.AbsenceCheckRegisterOutput;
import nts.uk.ctx.at.request.dom.application.appabsence.service.output.AppAbsenceStartInfoOutput;
import nts.uk.ctx.at.request.dom.application.appabsence.service.output.AppForLeaveStartOutput;
import nts.uk.ctx.at.request.dom.application.appabsence.service.output.VacationCheckOutput;
import nts.uk.ctx.at.request.dom.application.common.service.setting.CommonAlgorithm;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoWithDateOutput;
import nts.uk.ctx.at.shared.app.find.remainingnumber.paymana.PayoutSubofHDManagementDto;
import nts.uk.ctx.at.shared.app.find.remainingnumber.subhdmana.dto.LeaveComDayOffManaDto;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.specialholiday.specialholidayevent.service.MaxDaySpecHdOutput;
import nts.uk.ctx.at.shared.dom.specialholiday.specialholidayevent.service.SpecialHolidayEventAlgorithm;

@Stateless
public class AppAbsenceFinderMobile {
	public final static String DATE_FORMAT = "yyyy/MM/dd";
	
	@Inject
	private AbsenceServiceProcessMobile absenceServiceProcessMobile;
	
	@Inject
	private CommonAlgorithm commonAlgorithm;
	
	@Inject
	private AppAbsenceFinder appAbsenceFinder;
	
	@Inject
	private SpecialHolidayEventAlgorithm specHdEventAlg;
	
	@Inject
	private AbsenceServiceProcess absenceServiceProcess;
	
	@Inject
    private HolidayAssociationStart holidayAssociationStart;
	
	@Inject
	private HolidayWorkAssociationStart holidayWorkAssociationStart;
	
	public AppForLeaveStartOutputDto start(StartMobileParam param) {
		
		AppForLeaveStartOutput output = absenceServiceProcessMobile.start(
				param.getMode(),
				param.getCompanyId(),
				Optional.ofNullable(param.getEmployeeIdOp()),
				param.getDatesOp()
						.stream()
						.map(x -> GeneralDate.fromString(x, DATE_FORMAT))
						.collect(Collectors.toList()),
				Optional.ofNullable(param.getAppAbsenceStartInfoOutputOp())
						.flatMap(x -> Optional.of(x.toDomain(param.getCompanyId()))),
				Optional.ofNullable(param.getApplyForLeaveOp()).flatMap(x -> Optional.of(x.toDomain())),
				param.getAppDispInfoStartupOutput().toDomain()
				);
		return AppForLeaveStartOutputDto.fromDomain(output);
	}
	
	/**
	 * Refactor5 申請日を変更する
	 * UKDesign.UniversalK.就業.KAF_申請.KAFS06_休暇申請（スマホ）.A：休暇申請入力画面.アルゴリズム.申請日を変更する
	 * @param param
	 * @return
	 */
	public AppAbsenceStartInfoDto changeAppDate(ChangeDateParamMobile param) {
		// 申請表示情報(基準日関係あり)を取得する
		AppDispInfoWithDateOutput appDispInfoWithDateOutput = commonAlgorithm.getAppDispInfoWithDate(
				param.getCompanyId(),
				ApplicationType.ABSENCE_APPLICATION,
				Optional.ofNullable(param.getDates()).orElse(Collections.emptyList())
													 .stream()
													 .map(x -> GeneralDate.fromString(x, DATE_FORMAT))
													 .collect(Collectors.toList()),
				 param.getAppAbsenceStartInfoDto().getAppDispInfoStartupOutput().toDomain().getAppDispInfoNoDateOutput(),
				true,
				Optional.empty());
		AppAbsenceStartInfoOutput output = param.getAppAbsenceStartInfoDto().toDomain(param.getCompanyId());
		output.getAppDispInfoStartupOutput().setAppDispInfoWithDateOutput(appDispInfoWithDateOutput);
		// 申請日変更時処理
		return appAbsenceFinder.getChangeAppDate(
				param.getCompanyId(),
				param.getAppAbsenceStartInfoDto(),
				Optional.ofNullable(param.getDates()).orElse(Collections.emptyList()),
				param.getAppHolidayType(),
				AppDispInfoWithDateDto.fromDomain(appDispInfoWithDateOutput));
	}
	/**
	 * Refactor5 
	 * UKDesign.UniversalK.就業.KAF_申請.KAFS06_休暇申請（スマホ）.A：休暇申請入力画面.ユースケース
	 * @param param
	 * @return
	 */
	public MaxDaySpecHdDto getMaxDaySpecHd(MaxHolidayDayParamMobile param) {
		// 指定する特休枠の上限日数を取得する
		MaxDaySpecHdOutput output = specHdEventAlg.getMaxDaySpecHd(
				param.getCompanyId(),
				param.getSpecHdFrame(),
				param.getSpecHdEvent().toDomain(),
				Optional.ofNullable(param.getRelationCDOp()));
		return MaxDaySpecHdDto.fromDomain(output);
	}
	/**
	 * Refactor5 UKDesign.UniversalK.就業.KAF_申請.KAFS06_休暇申請（スマホ）.A：休暇申請入力画面.ユースケース
	 * @param param
	 * @return
	 */
	public AppAbsenceStartInfoDto selectTypeHoliday(SelectTypeHolidayParam param) {
		// 休暇種類変更時処理
		AppAbsenceStartInfoOutput appAbsenceStartInfoOutput = absenceServiceProcess.holidayTypeChangeProcess(
				param.getCompanyId(),
				param.getAppAbsenceStartInfoDto().toDomain(param.getCompanyId()),
				Optional.ofNullable(param.getDates()).orElse(Collections.emptyList()),
			    EnumAdaptor.valueOf(param.getHolidayAppType(), HolidayAppType.class));
		return AppAbsenceStartInfoDto.fromDomain(appAbsenceStartInfoOutput);
	}
	/**
	 * Refactor5
	 * UKDesign.UniversalK.就業.KAF_申請.KAFS06_休暇申請（スマホ）.A：休暇申請入力画面.ユースケース
	 * @param param
	 * @return
	 */
	public SelectWorkOutputDto selectWorkType(SelectWorkTypeHolidayParam param) {
		SelectWorkOutputDto output = new SelectWorkOutputDto();
		// 「KDLS02_勤務種類選択（スマホ）」を起動する From UI
		// 勤務種類変更時処理
		AppAbsenceStartInfoOutput appAbsenceStartInfoOutput = absenceServiceProcess.workTypeChangeProcess(
				param.getCompanyId(),
				param.getDates(),
				param.getAppAbsenceStartInfoOutput().toDomain(param.getCompanyId()),
				EnumAdaptor.valueOf(param.getHolidayAppType(), HolidayAppType.class),
				Optional.ofNullable(param.getWorkTypeCodeAfterOp()).flatMap(x -> Optional.of(x.getWorkTypeCode())));
		output.appAbsenceStartInfoDto = AppAbsenceStartInfoDto.fromDomain(appAbsenceStartInfoOutput);
		// 休暇紐付管理をチェックする
		VacationCheckOutput vacationCheckOutput = absenceServiceProcess.checkVacationTyingManage(
				Optional.ofNullable(param.getWorkTypeCodeBeforeOp()).map(x -> x.toDomain()).orElse(null),
				Optional.ofNullable(param.getWorkTypeCodeBeforeOp()).map(x -> x.toDomain()).orElse(null),
				appAbsenceStartInfoOutput.getLeaveComDayOffManas(),
				appAbsenceStartInfoOutput.getPayoutSubofHDManagements());
		output.vacationCheckOutputDto = VacationCheckOutputDto.fromDomain(vacationCheckOutput);
		
		return output;
	}
	/**
	 * Refactor5
	 * UKDesign.UniversalK.就業.KAF_申請.KAFS06_休暇申請（スマホ）.A：休暇申請入力画面.ユースケース
	 * @param param
	 * @return
	 */
	public AppAbsenceStartInfoDto selectWorkTime(SelectWorkTimeHolidayParam param) {
		// 「KDLS01_就業時間帯選択（スマホ）」を起動する from UI
		// 就業時間帯変更時処理
		AppAbsenceStartInfoOutput output = absenceServiceProcess.workTimesChangeProcess(
				param.getCompanyId(),
				param.getAppAbsenceStartInfoDto().toDomain(param.getCompanyId()),
				param.getWorkTypeCode(),
				Optional.ofNullable(param.getWorkTimeCodeOp()));
		// 指定する勤務種類に必要な休暇時間を算出する
		AttendanceTime attendanceTime= absenceServiceProcess.calculateTimeRequired(
				param.getEmployeeId(),
				Optional.ofNullable(param.getDatesOp())
					.flatMap(x -> CollectionUtil.isEmpty(x) ? Optional.empty() : Optional.of(GeneralDate.fromString(x.get(0), DATE_FORMAT))),
				Optional.ofNullable(param.getWorkTypeCode()),
				Optional.ofNullable(param.getWorkTimeCodeOp()),
				Optional.empty(),
				Optional.empty(),
				Optional.empty());
		// 返ってきた「必要時間」を「休暇申請起動時の表示情報」にセットする
		output.setRequiredVacationTimeOptional(Optional.ofNullable(attendanceTime));
		
		return AppAbsenceStartInfoDto.fromDomain(output);
	}
	
	public AbsenceCheckRegisterDto checkBeforeInsert(CheckInsertMobileParam param) {
		AbsenceCheckRegisterOutput output;
		ApplyForLeave applyForLeave = param.getApplyForLeave().toDomain();
		Application application;
		
		if (applyForLeave.getVacationInfo().getHolidayApplicationType() == HolidayAppType.DIGESTION_TIME) {
			if (!applyForLeave.getReflectFreeTimeApp().getTimeDegestion().isPresent()) {
				applyForLeave.getReflectFreeTimeApp().setTimeDegestion(Optional.of(new TimeDigestApplication(
						new AttendanceTime(0), 
						new AttendanceTime(0), 
						new AttendanceTime(0), 
						new AttendanceTime(0), 
						new AttendanceTime(0), 
						new AttendanceTime(0),
			            Optional.empty()
						)));
				
			}
		}
		
		
		// INPUT．「画面モード」をチェックする
		if (param.getMode()) {
			application = param.getApplication().toDomain();
			applyForLeave.setApplication(application);
			
			// 休出代休関連付けダイアログ起動
            Kdl036OutputData kdl036output = holidayAssociationStart.init(new Kdl036InputData(
                    application.getEmployeeID(), 
                    GeneralDate.fromString(param.getApplication().getAppDate(), "yyyy/MM/dd"), 
                    GeneralDate.fromString(param.getApplication().getAppDate(), "yyyy/MM/dd"), 
                    param.getAppAbsenceStartInfoDto().getWorkTypeLst().stream()
                        .filter(x -> x.getWorkTypeCode()
                        .equals(param.getApplyForLeave().getReflectFreeTimeApp().getWorkInfo().getWorkType())).findFirst().get().getWorkAtr() == 0 ? 1 : 0, 
                    1,
                    param.getAppAbsenceStartInfoDto().getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput().getOpActualContentDisplayLst(), 
                    new ArrayList<LeaveComDayOffManaDto>()));
            
            
            // 振休振休関連付けダイアログ起動
            Kdl035OutputData kdl035output = holidayWorkAssociationStart.init(new Kdl035InputData(
                    application.getEmployeeID(), 
                    GeneralDate.fromString(param.getApplication().getAppDate(), "yyyy/MM/dd"), 
                    GeneralDate.fromString(param.getApplication().getAppDate(), "yyyy/MM/dd"), 
                    param.getAppAbsenceStartInfoDto().getWorkTypeLst().stream()
                        .filter(x -> x.getWorkTypeCode()
                        .equals(param.getApplyForLeave().getReflectFreeTimeApp().getWorkInfo().getWorkType())).findFirst().get().getWorkAtr() == 0 ? 1 : 0, 
                    1,
                    param.getAppAbsenceStartInfoDto().getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput().getOpActualContentDisplayLst(), 
                    new ArrayList<PayoutSubofHDManagementDto>()));
            
			// 登録前のエラーチェック処理
			output = absenceServiceProcess.checkBeforeRegister(
					param.getCompanyId(),
					param.getAppAbsenceStartInfoDto().toDomain(param.getCompanyId()),
					applyForLeave,
					false, 
                    kdl036output.getHolidayWorkInfoList().isEmpty(), 
                    kdl035output.getSubstituteWorkInfoList().isEmpty());
			
		} else {
			application = param.getApplicationUpdate().toDomain(param.getAppAbsenceStartInfoDto().getAppDispInfoStartupOutput().getAppDetailScreenInfo().getApplication());;
			applyForLeave.setApplication(application);
			// 休出代休関連付けダイアログ起動
	        Kdl036OutputData kdl036output = holidayAssociationStart.init(new Kdl036InputData(
	                application.getEmployeeID(), 
	                GeneralDate.fromString(param.getAppAbsenceStartInfoDto().getAppDispInfoStartupOutput().getAppDetailScreenInfo().getApplication().getAppDate(), "yyyy/MM/dd"), 
	                GeneralDate.fromString(param.getAppAbsenceStartInfoDto().getAppDispInfoStartupOutput().getAppDetailScreenInfo().getApplication().getAppDate(), "yyyy/MM/dd"), 
	                param.getAppAbsenceStartInfoDto().getWorkTypeLst().stream()
	                    .filter(x -> x.getWorkTypeCode()
	                    .equals(param.getApplyForLeave().getReflectFreeTimeApp().getWorkInfo().getWorkType())).findFirst().get().getWorkAtr() == 0 ? 1 : 0, 
	                1,
	                param.getAppAbsenceStartInfoDto().getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput().getOpActualContentDisplayLst(), 
	                new ArrayList<LeaveComDayOffManaDto>()));
	        
	        
	        // 振休振休関連付けダイアログ起動
	        Kdl035OutputData kdl035output = holidayWorkAssociationStart.init(new Kdl035InputData(
                    application.getEmployeeID(), 
                    GeneralDate.fromString(param.getAppAbsenceStartInfoDto().getAppDispInfoStartupOutput().getAppDetailScreenInfo().getApplication().getAppDate(), "yyyy/MM/dd"), 
                    GeneralDate.fromString(param.getAppAbsenceStartInfoDto().getAppDispInfoStartupOutput().getAppDetailScreenInfo().getApplication().getAppDate(), "yyyy/MM/dd"), 
                    param.getAppAbsenceStartInfoDto().getWorkTypeLst().stream()
                        .filter(x -> x.getWorkTypeCode()
                        .equals(param.getApplyForLeave().getReflectFreeTimeApp().getWorkInfo().getWorkType())).findFirst().get().getWorkAtr() == 0 ? 1 : 0, 
                    1,
                    param.getAppAbsenceStartInfoDto().getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput().getOpActualContentDisplayLst(), 
                    new ArrayList<PayoutSubofHDManagementDto>()));
			
			// 更新前のエラーチェック処理
			output = absenceServiceProcess.checkBeforeUpdate(
					param.getCompanyId(),
					param.getAppAbsenceStartInfoDto().toDomain(param.getCompanyId()),
					applyForLeave,
					false, 
					kdl036output.getHolidayWorkInfoList().isEmpty(), 
					kdl035output.getSubstituteWorkInfoList().isEmpty());			
		}
		// 返ってきた内容をOUTPUTとして返す	
		return AbsenceCheckRegisterDto.fromDomain(output);
	}
	/**
	 * Refactor5
	 * UKDesign.UniversalK.就業.KAF_申請.KAFS06_休暇申請（スマホ）.B：休暇申請確認画面.アルゴリズム.休暇申請確認画面を起動する
	 * @param param
	 * @return
	 */
	public AppForLeaveStartOutputDto getDetailInfo(DetailInfoParam param) {
		return AppForLeaveStartOutputDto.fromDomain(absenceServiceProcess.getAppForLeaveStartB(
				param.getCompanyId(),
				param.getAppId(),
				param.getAppDispInfoStartup().toDomain()));
	}
}
