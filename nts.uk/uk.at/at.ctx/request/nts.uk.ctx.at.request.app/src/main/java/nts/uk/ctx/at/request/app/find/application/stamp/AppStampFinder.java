package nts.uk.ctx.at.request.app.find.application.stamp;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import nts.arc.enums.EnumAdaptor;
import nts.arc.time.GeneralDate;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.app.find.application.ApplicationDto;
import nts.uk.ctx.at.request.app.find.application.stamp.dto.AppStampOutputDto;
import nts.uk.ctx.at.request.dom.application.AppReason;
import nts.uk.ctx.at.request.dom.application.Application;
import nts.uk.ctx.at.request.dom.application.ApplicationDate;
import nts.uk.ctx.at.request.dom.application.ApplicationType;
import nts.uk.ctx.at.request.dom.application.PrePostAtr;
import nts.uk.ctx.at.request.dom.application.ReasonForReversion;
import nts.uk.ctx.at.request.dom.application.common.service.detailscreen.init.DetailAppCommonSetService;
import nts.uk.ctx.at.request.dom.application.common.service.newscreen.output.ConfirmMsgOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.AchievementDetail;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ActualContentDisplay;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.StampRecordOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.TrackRecordAtr;
import nts.uk.ctx.at.request.dom.application.common.service.setting.CommonAlgorithm;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoWithDateOutput;
import nts.uk.ctx.at.request.dom.application.stamp.AppCommonDomainService;
import nts.uk.ctx.at.request.dom.application.stamp.StampRequestMode;
import nts.uk.ctx.at.request.dom.application.stamp.output.AppStampOutput;
import nts.uk.ctx.at.request.dom.application.stamp.output.ErrorStampInfo;
import nts.uk.ctx.at.request.dom.setting.company.appreasonstandard.AppStandardReasonCode;
/**
 * 
 * @author hoangnd
 *
 */
@Stateless
public class AppStampFinder {
	
	//refactor4
	public static final String PATTERN_DATE = "yyyy/MM/dd";
	
	@Inject
	private AppCommonDomainService appCommonStampDomainService;
	
	@Inject
	private DetailAppCommonSetService appCommonSetService;
	
	@Inject
	private CommonAlgorithm commonAlgorithm;
	
	
	
	
	
//	Refactor4	
	public AppStampOutputDto getDataCommon(StartAppStampParam startParam) {
		AppStampOutput appStampOutput = appCommonStampDomainService.getDataCommon(startParam.getCompanyId(),
				!StringUtils.isBlank(startParam.getDate())
						? Optional.of(GeneralDate.fromString(startParam.getDate(), PATTERN_DATE))
						: Optional.empty(),
				startParam.getAppDispInfoStartupDto().toDomain(), startParam.getRecoderFlag());
		return AppStampOutputDto.fromDomain(appStampOutput);
		
	}
	
	public List<ConfirmMsgOutput> checkBeforeRegister(BeforeRegisterOrUpdateParam beforeRegisterParam) {
		String pattern2 = "yyyy/MM/dd";
		ApplicationDto applicationDto = beforeRegisterParam.getApplicationDto();
		Application application = Application.createFromNew(
				EnumAdaptor.valueOf(applicationDto.getPrePostAtr(), PrePostAtr.class),
				applicationDto.getEmployeeID(),
				EnumAdaptor.valueOf(applicationDto.getAppType(), ApplicationType.class),
				new ApplicationDate(GeneralDate.fromString(applicationDto.getAppDate(), pattern2)),
				applicationDto.getEnteredPerson(),
				applicationDto.getOpStampRequestMode() == null ? Optional.empty() : Optional.of(EnumAdaptor.valueOf(applicationDto.getOpStampRequestMode(), StampRequestMode.class)),
				applicationDto.getOpReversionReason() == null ? Optional.empty() : Optional.of(new ReasonForReversion(applicationDto.getOpReversionReason())),
				StringUtils.isBlank(applicationDto.getOpAppStartDate()) ? Optional.empty() : Optional.of(new ApplicationDate(GeneralDate.fromString(applicationDto.getOpAppStartDate(), pattern2))),
				StringUtils.isBlank(applicationDto.getOpAppEndDate()) ? Optional.empty() : Optional.of(new ApplicationDate(GeneralDate.fromString(applicationDto.getOpAppEndDate(), pattern2))),
				applicationDto.getOpAppReason() == null ? Optional.empty() : Optional.of(new AppReason(applicationDto.getOpAppReason())),
				applicationDto.getOpAppStandardReasonCD() == null ? Optional.empty() : Optional.of(new AppStandardReasonCode(applicationDto.getOpAppStandardReasonCD())));
		AppStampOutput as = beforeRegisterParam.getAppStampOutputDto().toDomain();
		as.getAppStampOptional().ifPresent(x -> {
			x.setPrePostAtr(application.getPrePostAtr());
		});
		return appCommonStampDomainService.checkBeforeRegister(
				beforeRegisterParam.getCompanyId(),
				beforeRegisterParam.getAgentAtr(),
				application,
				as);
	}
	
	public List<ConfirmMsgOutput> checkBeforeUpdate(BeforeRegisterOrUpdateParam beforeRegisterParam) {
		ApplicationDto applicationDto = beforeRegisterParam.getApplicationDto();
		Application application =applicationDto.toDomain();
		AppStampOutput as = beforeRegisterParam.getAppStampOutputDto().toDomain();
		as.getAppStampOptional().ifPresent(x -> {
			x.setPrePostAtr(application.getPrePostAtr());
		});
		
		 return appCommonStampDomainService.checkBeforeUpdate(
				beforeRegisterParam.getCompanyId(),
				beforeRegisterParam.getAgentAtr(),
				application,
				as);
	}
	
	public AppStampOutputDto getDataDetailCommon(DetailAppStampParam detailAppStampParam) {
		
//		14-1.詳細画面起動前申請共通設定を取得する
		//lay tu man 000
		 AppDispInfoStartupOutput appDispInfoStartupOutput = 
				 appCommonSetService.getCommonSetBeforeDetail(detailAppStampParam.getCompanyId(), detailAppStampParam.getAppId());
		 
		AppStampOutput appStampOutput = appCommonStampDomainService.getDataDetailCommon(
				detailAppStampParam.getCompanyId(),
				detailAppStampParam.getAppId(),
				appDispInfoStartupOutput,
				detailAppStampParam.getRecoderFlag());
		
		return AppStampOutputDto.fromDomain(appStampOutput);
	}
	public AppStampOutputDto changeDateAppStamp(ChangeDateParamMobile changeDateParam) {
		AppStampOutput  appStampOutput = changeDateParam.getAppStampOutputDto().toDomain();
		List<GeneralDate> dates = Collections.emptyList();
		if (!CollectionUtil.isEmpty(changeDateParam.getDate())) {
		    dates = changeDateParam.getDate().stream().map(x -> GeneralDate.fromString(x, PATTERN_DATE)).collect(Collectors.toList());
		}
		
//		申請表示情報(基準日関係あり)を取得する
		AppDispInfoWithDateOutput appDispInfoWithDateOutput = commonAlgorithm.getAppDispInfoWithDate(
		        changeDateParam.getCompanyId(), 
		        ApplicationType.STAMP_APPLICATION, 
		        dates, 
		        appStampOutput.getAppDispInfoStartupOutput().getAppDispInfoNoDateOutput(), 
		        true, 
		        Optional.empty());
		appStampOutput.getAppDispInfoStartupOutput().setAppDispInfoWithDateOutput(appDispInfoWithDateOutput);
		
		if(!changeDateParam.isRecorderFlag()) {
//		実績の打刻のチェック
		    StampRecordOutput stampRecordOutput = null;
		    Optional<String> workTypeCd = Optional.empty();
		    Optional<List<ActualContentDisplay>> listActualContentDisplay = appStampOutput.getAppDispInfoStartupOutput().getAppDispInfoWithDateOutput().getOpActualContentDisplayLst();
		    if (listActualContentDisplay.isPresent()) {
		        if (!CollectionUtil.isEmpty(listActualContentDisplay.get())) {
		            ActualContentDisplay actualContentDisplay = listActualContentDisplay.get().get(0);
		            Optional<AchievementDetail> opAchievementDetail = actualContentDisplay.getOpAchievementDetail();
		            if (opAchievementDetail.isPresent()) {
                        stampRecordOutput = opAchievementDetail.get().getStampRecordOutput();
                        if (opAchievementDetail.get().getTrackRecordAtr().equals(TrackRecordAtr.DAILY_RESULTS)) {
                            workTypeCd = Optional.ofNullable(opAchievementDetail.get().getWorkTypeCD());
                        }
                    }
		        }
		    }
		    List<ErrorStampInfo> listErrorStampInfo = appCommonStampDomainService.getErrorStampList(stampRecordOutput, workTypeCd);
		    appStampOutput.setErrorListOptional(Optional.ofNullable(listErrorStampInfo));
		}
		
		return AppStampOutputDto.fromDomain(appStampOutput);
	}
	
	
	
	
	
}
