package nts.uk.ctx.at.request.dom.application;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.logging.log4j.util.Strings;

import nts.uk.ctx.at.request.dom.application.appabsence.ApplyForLeaveRepository;
import nts.uk.ctx.at.request.dom.application.businesstrip.BusinessTrip;
import nts.uk.ctx.at.request.dom.application.businesstrip.BusinessTripRepository;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.ApprovalRootStateAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApprovalPhaseStateImport_New;
import nts.uk.ctx.at.request.dom.application.gobackdirectly.GoBackDirectlyRepository;
import nts.uk.ctx.at.request.dom.application.holidayshipment.absenceleaveapp.AbsenceLeaveAppRepository;
import nts.uk.ctx.at.request.dom.application.holidayshipment.compltleavesimmng.AppHdsubRecRepository;
import nts.uk.ctx.at.request.dom.application.holidayshipment.recruitmentapp.RecruitmentAppRepository;
import nts.uk.ctx.at.request.dom.application.holidayworktime.AppHolidayWorkRepository;
import nts.uk.ctx.at.request.dom.application.lateleaveearly.ArrivedLateLeaveEarlyRepository;
import nts.uk.ctx.at.request.dom.application.optional.OptionalItemApplication;
import nts.uk.ctx.at.request.dom.application.optional.OptionalItemApplicationRepository;
import nts.uk.ctx.at.request.dom.application.overtime.AppOverTimeRepository;
import nts.uk.ctx.at.request.dom.application.stamp.AppRecordImageRepository;
import nts.uk.ctx.at.request.dom.application.stamp.AppStampRepository;
import nts.uk.ctx.at.request.dom.application.stamp.StampRequestMode;
import nts.uk.ctx.at.request.dom.application.timeleaveapplication.TimeLeaveApplicationRepository;
import nts.uk.ctx.at.request.dom.application.workchange.AppWorkChangeRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 *
 * @author Doan Duy Hung
 *
 */
@Stateless
@Transactional
public class ApplicationApprovalImpl implements ApplicationApprovalService {

	@Inject
	private ApplicationRepository applicationRepository;

	@Inject
	private ApprovalRootStateAdapter approvalRootStateAdapter;

	@Inject
	private AppStampRepository appStampRepository;

	@Inject
	private AppRecordImageRepository appRecordImageRepository;

	@Inject
	private AppOverTimeRepository overtimeRepository;

	@Inject
	private GoBackDirectlyRepository goBackDirectlyRepository;

	@Inject
	private AppWorkChangeRepository workChangeRepository;

	@Inject
	private ArrivedLateLeaveEarlyRepository lateOrLeaveEarlyRepository;

	@Inject
	private AppHolidayWorkRepository appHolidayWorkRepository;

	@Inject
	private AbsenceLeaveAppRepository absRepo;

	@Inject
	private RecruitmentAppRepository recRepo;

	@Inject
	private ApplyForLeaveRepository appAbsenceRepository;

	@Inject
    private BusinessTripRepository businessTripRepo;

	@Inject
	private OptionalItemApplicationRepository optionalItemApplicationRepo;
	
	@Inject
	private AppHdsubRecRepository appHdsubRecRepository;

	@Inject
	private TimeLeaveApplicationRepository timeLeaveAppRepo;

	@Override
	public void delete(String appID, ApplicationType appType, Optional<StampRequestMode> stampRequestMode, Optional<HdsubRecLinkData> hdSubRecLink) {
		String companyID = AppContexts.user().companyId();
		String linkAppID = "";
		switch (appType) {
		case STAMP_APPLICATION:
			if (stampRequestMode.isPresent()) {
				if (stampRequestMode.get() == StampRequestMode.STAMP_ADDITIONAL) {
					appStampRepository.delete(companyID, appID);
				} else {
					appRecordImageRepository.delete(companyID, appID);
				}
			}
			break;
		case OVER_TIME_APPLICATION:
			overtimeRepository.remove(companyID, appID);
			break;
		case GO_RETURN_DIRECTLY_APPLICATION:
			goBackDirectlyRepository.delete(companyID, appID);
			break;
		case WORK_CHANGE_APPLICATION:
			workChangeRepository.remove(companyID, appID);
			break;
		case EARLY_LEAVE_CANCEL_APPLICATION:
			lateOrLeaveEarlyRepository.remove(companyID, appID);
			break;
		case HOLIDAY_WORK_APPLICATION:
			appHolidayWorkRepository.delete(companyID, appID);
			break;
		case COMPLEMENT_LEAVE_APPLICATION:
//			Optional<AppHdsubRec> appHdsubRec = appHdsubRecRepository.findByAppId(appID);
//			if(appHdsubRec.isPresent()) {
//				linkAppID = Arrays.asList(appHdsubRec.get().getRecAppID(), appHdsubRec.get().getAbsenceLeaveAppID())
//						.stream().filter(x -> !x.equals(appID)).findAny().orElse(null);
//				absRepo.remove(appHdsubRec.get().getAbsenceLeaveAppID());
//				recRepo.remove(appHdsubRec.get().getRecAppID());
//				appHdsubRecRepository.remove(appHdsubRec.get().getAbsenceLeaveAppID(), appHdsubRec.get().getRecAppID());
//			}else {
//				absRepo.remove(appID);
//				recRepo.remove(appID);
//			}
		    if (hdSubRecLink.isPresent()) {
		        linkAppID = Arrays.asList(hdSubRecLink.get().absId, hdSubRecLink.get().recId)
                      .stream().filter(x -> !x.equals(appID)).findAny().orElse(null);
		        absRepo.remove(hdSubRecLink.get().absId);
		        recRepo.remove(hdSubRecLink.get().recId);
		        appHdsubRecRepository.remove(hdSubRecLink.get().absId, hdSubRecLink.get().recId);
		    } else {
		        absRepo.remove(appID);
		        recRepo.remove(appID);
		    }
			break;
		case ABSENCE_APPLICATION:
			appAbsenceRepository.delete(companyID, appID);
			break;
		case BUSINESS_TRIP_APPLICATION:
		    Optional<BusinessTrip> businessTrip = businessTripRepo.findByAppId(companyID, appID);
		    if (businessTrip.isPresent()) {
		        businessTripRepo.remove(businessTrip.get());
		    }
		    break;
        case OPTIONAL_ITEM_APPLICATION:
            Optional<OptionalItemApplication> opItemApp = optionalItemApplicationRepo.getByAppId(companyID, appID);
            if (opItemApp.isPresent()) {
            	opItemApp.get().setAppID(appID);
                optionalItemApplicationRepo.remove(opItemApp.get());
            }
            break;
		case ANNUAL_HOLIDAY_APPLICATION:
			timeLeaveAppRepo.findById(companyID, appID).ifPresent(domain -> {
				timeLeaveAppRepo.remove(domain);
			});
			break;
		default:
			break;
		}
		applicationRepository.remove(appID);
		approvalRootStateAdapter.deleteApprovalRootState(appID);
		if(Strings.isNotBlank(linkAppID)) {
			applicationRepository.remove(linkAppID);
			approvalRootStateAdapter.deleteApprovalRootState(linkAppID);
		}

	}

	@Override
	public void insertApp(Application application, List<ApprovalPhaseStateImport_New> listApprovalPhaseState) {
		applicationRepository.insert(application);
		approvalRootStateAdapter.insertApp(
				application.getAppID(),
				application.getAppDate().getApplicationDate(),
				application.getEmployeeID(),
				listApprovalPhaseState);
	}

}
