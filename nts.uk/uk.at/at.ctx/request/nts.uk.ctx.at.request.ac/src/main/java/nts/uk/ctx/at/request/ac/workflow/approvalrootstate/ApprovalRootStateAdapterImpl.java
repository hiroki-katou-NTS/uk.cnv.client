package nts.uk.ctx.at.request.ac.workflow.approvalrootstate;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;

import nts.arc.enums.EnumAdaptor;
import nts.arc.error.BusinessException;
import nts.arc.time.GeneralDate;
import nts.arc.time.YearMonth;
import nts.arc.time.calendar.period.DatePeriod;
import nts.gul.collection.CollectionUtil;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.ApprovalRootStateAdapter;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.ApprovalStatusForEmployeeImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.ApproveRootStatusForEmpImPort;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.AgentPubImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.AppFrameInsImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.AppPhaseInsImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.AppRootInsImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.AppRootInsPeriodImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.AppRootSttMonthImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApprovalBehaviorAtrImport_New;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApprovalFormImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApprovalFrameImport_New;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApprovalPhaseStateImport_New;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApprovalRootContentImport_New;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApprovalRootStateImport_New;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApproveResultImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApproverApproveImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApproverApprovedImport_New;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApproverEmpImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApproverPersonImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApproverRemandImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApproverRepresenterImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApproverStateImport_New;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ApproverWithFlagImport_New;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.EmpPerformMonthParamAt;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.ErrorFlagImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.RepresenterInformationImport;
import nts.uk.ctx.at.request.dom.application.common.adapter.workflow.dto.Request533Import;
import nts.uk.ctx.sys.env.pub.maildestination.IMailDestinationPub;
import nts.uk.ctx.sys.env.pub.maildestination.MailDestination;
import nts.uk.ctx.sys.env.pub.maildestination.OutGoingMail;
import nts.uk.ctx.workflow.pub.agent.AgentPubExport;
import nts.uk.ctx.workflow.pub.agent.ApproverRepresenterExport;
import nts.uk.ctx.workflow.pub.resultrecord.ApproverApproveExport;
import nts.uk.ctx.workflow.pub.resultrecord.EmpPerformMonthParam;
import nts.uk.ctx.workflow.pub.resultrecord.IntermediateDataPub;
import nts.uk.ctx.workflow.pub.resultrecord.export.Request533Export;
import nts.uk.ctx.workflow.pub.service.ApprovalRootStatePub;
import nts.uk.ctx.workflow.pub.service.export.ApprovalBehaviorAtrExport;
import nts.uk.ctx.workflow.pub.service.export.ApprovalFormExport;
import nts.uk.ctx.workflow.pub.service.export.ApprovalFrameExport;
import nts.uk.ctx.workflow.pub.service.export.ApprovalPhaseStateExport;
import nts.uk.ctx.workflow.pub.service.export.ApprovalRootContentExport;
import nts.uk.ctx.workflow.pub.service.export.ApprovalRootStateExport;
import nts.uk.ctx.workflow.pub.service.export.ApproveResultExport;
import nts.uk.ctx.workflow.pub.service.export.ApproverApprovedExport;
import nts.uk.ctx.workflow.pub.service.export.ApproverPersonExportNew;
import nts.uk.ctx.workflow.pub.service.export.ApproverStateExport;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.time.calendar.date.ClosureDate;
/**
 * 
 * @author Doan Duy Hung
 *
 */
@RequestScoped
public class ApprovalRootStateAdapterImpl implements ApprovalRootStateAdapter {
	
	@Inject
	private ApprovalRootStatePub approvalRootStatePub;
	
	@Inject
	private IntermediateDataPub intermediateDataPub;
	
	@Inject
	private IMailDestinationPub iMailDestinationPub;
	
	@Override
	public Map<String,List<ApprovalPhaseStateImport_New>> getApprovalRootContents(List<String> appIDs, String companyID) {
		Map<String,List<ApprovalPhaseStateImport_New>> approvalPhaseImport_NewMap = new LinkedHashMap<>();
		Map<String,List<ApprovalPhaseStateExport>> approvalRootContentExports = approvalRootStatePub.getApprovalRoots(appIDs, companyID);
		for(Map.Entry<String,List<ApprovalPhaseStateExport>> approvalRootContentExport : approvalRootContentExports.entrySet()){
					
			List<ApprovalPhaseStateImport_New> appRootContentImport_News = fromExport(approvalRootContentExport.getValue(), Optional.empty());
			approvalPhaseImport_NewMap.put(approvalRootContentExport.getKey(), appRootContentImport_News);
		}
		return approvalPhaseImport_NewMap;
	}
	
	public MailDestinationCache createMailDestinationCache(String companyID) {

		return new MailDestinationCache(
				sid -> this.iMailDestinationPub.getEmpEmailAddress(companyID, Arrays.asList(sid), 6));
	}
	
	@Override
	public ApprovalRootContentImport_New getApprovalRootContent(
			String companyID, String employeeID, Integer appTypeValue, GeneralDate appDate, String appID, Boolean isCreate,
			MailDestinationCache mailDestCache) {
		ApprovalRootContentExport approvalRootContentExport = approvalRootStatePub.getApprovalRoot(companyID, employeeID, appTypeValue, appDate, appID, isCreate);
		
		return new ApprovalRootContentImport_New(
					new ApprovalRootStateImport_New(
							approvalRootContentExport.getApprovalRootState().getRootStateID(),
							fromExport(approvalRootContentExport.getApprovalRootState().getListApprovalPhaseState(), Optional.of(mailDestCache)),
							approvalRootContentExport.getApprovalRootState().getApprovalRecordDate(),
							approvalRootContentExport.getApprovalRootState().getRootType(),
							approvalRootContentExport.getApprovalRootState().getEmployeeID()),
					EnumAdaptor.valueOf(approvalRootContentExport.getErrorFlag().value, ErrorFlagImport.class));
	}

	@Override
	public void insertByAppType(String companyID, String employeeID, Integer appTypeValue, GeneralDate appDate, String appID, GeneralDate baseDate) {

		approvalRootStatePub.insertAppRootType(companyID, employeeID, appTypeValue, appDate, appID, 0, baseDate);

	}
	
	@Override
	public void insertFromCache(String companyID, String appID, GeneralDate date, String employeeID,
			List<ApprovalPhaseStateImport_New> listApprovalPhaseState) {
		List<ApprovalPhaseStateExport> convertLst = listApprovalPhaseState.stream()
				.map(x -> new ApprovalPhaseStateExport(
						x.getPhaseOrder(), 
						EnumAdaptor.valueOf(x.getApprovalAtr().value, ApprovalBehaviorAtrExport.class), 
						EnumAdaptor.valueOf(x.getApprovalForm().value, ApprovalFormExport.class), 
						x.getListApprovalFrame().stream().map(y -> new ApprovalFrameExport(
								y.getFrameOrder(), 
								y.getListApprover().stream().map(z -> new ApproverStateExport(
										z.getApproverID(), 
										EnumAdaptor.valueOf(z.getApprovalAtr().value, ApprovalBehaviorAtrExport.class), 
										z.getAgentID(), 
										z.getApproverName(), 
										z.getAgentName(),
										z.getRepresenterID(), 
										z.getRepresenterName(), 
										z.getApprovalDate(), 
										z.getApprovalReason(),
										z.getApproverInListOrder()))
								.collect(Collectors.toList()), 
								y.getConfirmAtr(), 
								y.getAppDate()))
						.collect(Collectors.toList())))
				.collect(Collectors.toList());
		approvalRootStatePub.insertFromCache(companyID, appID, date, employeeID, convertLst);
	}

	@Override
	public List<String> getNextApprovalPhaseStateMailList(String rootStateID, Integer approvalPhaseStateNumber) {

		return approvalRootStatePub.getNextApprovalPhaseStateMailList(rootStateID, approvalPhaseStateNumber);

	}

	@Override
	public ApproveResultImport doApprove(String rootStateID, String employeeID, String memo) {
		ApproveResultExport approveResultExport = approvalRootStatePub.doApprove(rootStateID, employeeID, memo);
		ApproveResultImport approveResultImport = new ApproveResultImport(
				approveResultExport.getApprovalPhaseNumber(), 
				new ApprovalRootStateImport_New(
						approveResultExport.getApprovalRootState().getRootStateID(), 
						approveResultExport.getApprovalRootState().getListApprovalPhaseState().stream().map(x -> new ApprovalPhaseStateImport_New(
								x.getPhaseOrder(), 
								EnumAdaptor.valueOf(x.getApprovalAtr().value, ApprovalBehaviorAtrImport_New.class), 
								EnumAdaptor.valueOf(x.getApprovalForm().value, ApprovalFormImport.class), 
								x.getListApprovalFrame().stream().map(y -> new ApprovalFrameImport_New(
										y.getFrameOrder(), 
										y.getListApprover().stream().map(z -> new ApproverStateImport_New(
												z.getApproverID(), 
												EnumAdaptor.valueOf(z.getApprovalAtr().value, ApprovalBehaviorAtrImport_New.class), 
												z.getAgentID(), 
												z.getApproverName(), 
												z.getAgentName(), 
												z.getRepresenterID(), 
												z.getRepresenterName(), 
												z.getApprovalDate(), 
												z.getApprovalReason(), 
												"", 
												"", 
												"", 
												z.getApproverInListOrder())).collect(Collectors.toList()), 
										y.getConfirmAtr(), 
										y.getAppDate())
								).collect(Collectors.toList()))
						).collect(Collectors.toList()), 
						approveResultExport.getApprovalRootState().getApprovalRecordDate(), 
						approveResultExport.getApprovalRootState().getRootType(), 
						approveResultExport.getApprovalRootState().getEmployeeID()));
		return approveResultImport;
	}

	@Override
	public Boolean isApproveAllComplete(ApprovalRootStateImport_New approvalRootState) {
		ApprovalRootStateExport approvalRootStateExport = new ApprovalRootStateExport(
				approvalRootState.getRootStateID(), 
				approvalRootState.getRootType(), 
				approvalRootState.getDate(), 
				approvalRootState.getEmployeeID(), 
				approvalRootState.getListApprovalPhaseState().stream().map(x -> new ApprovalPhaseStateExport(
						x.getPhaseOrder(), 
						EnumAdaptor.valueOf(x.getApprovalAtr().value, ApprovalBehaviorAtrExport.class), 
						EnumAdaptor.valueOf(x.getApprovalForm().value, ApprovalFormExport.class), 
						x.getListApprovalFrame().stream().map(y -> new ApprovalFrameExport(
								y.getFrameOrder(), 
								y.getListApprover().stream().map(z -> new ApproverStateExport(
										z.getApproverID(), 
										EnumAdaptor.valueOf(z.getApprovalAtr().value, ApprovalBehaviorAtrExport.class), 
										z.getAgentID(), 
										z.getApproverName(), 
										z.getAgentName(), 
										z.getRepresenterID(), 
										z.getRepresenterName(), 
										z.getApprovalDate(), 
										z.getApprovalReason(), 
										z.getApproverInListOrder())).collect(Collectors.toList()), 
								y.getConfirmAtr(), 
								y.getAppDate())
						).collect(Collectors.toList()))
				).collect(Collectors.toList()));

		return approvalRootStatePub.isApproveAllComplete(approvalRootStateExport);

	}

	@Override
	public void doReleaseAllAtOnce(String companyID, String rootStateID) {

		approvalRootStatePub.doReleaseAllAtOnce(companyID, rootStateID, 0);

	}

	@Override
	public ApproverApprovedImport_New getApproverApproved(String rootStateID) {
		ApproverApprovedExport approverApprovedExport = approvalRootStatePub.getApproverApproved(rootStateID, 0);
		return new ApproverApprovedImport_New(
				approverApprovedExport.getListApproverWithFlagOutput().stream()
					.map(x -> new ApproverWithFlagImport_New(x.getEmployeeID(), x.getAgentFlag())).collect(Collectors.toList()), 
				approverApprovedExport.getListApprover());

	}

	@Override
	public AgentPubImport getApprovalAgencyInformation(String companyID, List<String> approver) {
		// TODO Auto-generated method stub
		return convertAgentPubImport(approvalRootStatePub.getApprovalAgentInfor(companyID, approver));
	}
	
	private AgentPubImport convertAgentPubImport(AgentPubExport agentPubExport) {
		return new AgentPubImport(
				agentPubExport.getListApproverAndRepresenterSID().stream()
				.map(x -> this.covertApproverImport(x)).collect(Collectors.toList()),
				agentPubExport.getListRepresenterSID(),
				agentPubExport.isFlag()
				);
		
	}
	
	private ApproverRepresenterImport covertApproverImport(ApproverRepresenterExport approverRepresenterExport) {
		return new  ApproverRepresenterImport(
				approverRepresenterExport.getApprover(),
				new RepresenterInformationImport(approverRepresenterExport.getRepresenter().getValue()) 
				);
	}

	@Override
	public List<String> getMailNotifierList(String companyID, String rootStateID) {
		return approvalRootStatePub.getMailNotifierList(companyID, rootStateID, 0);

	}

	@Override
	public void deleteApprovalRootState(String rootStateID) {
		approvalRootStatePub.deleteApprovalRootState(rootStateID, 0);
	}

	@Override
	public Boolean doRelease(String companyID, String rootStateID, String employeeID) {
		return approvalRootStatePub.doRelease(companyID, rootStateID, employeeID, 0);
	}

	@Override
	public Boolean doDeny(String rootStateID, String employeeID, String memo) {
		return approvalRootStatePub.doDeny(rootStateID, employeeID, memo);
	}

	@Override
	public Boolean judgmentTargetPersonIsApprover(String companyID, String rootStateID, String employeeID) {
		return approvalRootStatePub.judgmentTargetPersonIsApprover(companyID, rootStateID, employeeID, 0);
	}

	@Override
	public ApproverPersonImport judgmentTargetPersonCanApprove(String companyID, String rootStateID,
			String employeeID) {
		ApproverPersonExportNew approverPersonExport = approvalRootStatePub.judgmentTargetPersonCanApprove(companyID, rootStateID, employeeID, 0);
		return new ApproverPersonImport(
				approverPersonExport.getAuthorFlag(), 
				EnumAdaptor.valueOf(approverPersonExport.getApprovalAtr().value, ApprovalBehaviorAtrImport_New.class), 
				approverPersonExport.getExpirationAgentFlag());
	}

	@Override
	public List<String> doRemandForApprover(String companyID, String rootStateID, Integer order) {
		return approvalRootStatePub.doRemandForApprover(companyID, rootStateID, order, 0);
	}

	@Override
	public void doRemandForApplicant(String companyID, String rootStateID) {
		approvalRootStatePub.doRemandForApplicant(companyID, rootStateID, 0);
	}
	// request list 113
	@Override
	public List<ApproveRootStatusForEmpImPort> getApprovalByEmplAndDate(GeneralDate startDate, GeneralDate endDate,
			String employeeID, String companyID, Integer rootType) throws BusinessException {
		return intermediateDataPub.getAppRootStatusByEmpPeriod(employeeID, new DatePeriod(startDate, endDate), rootType)
				.getAppRootStateStatusLst()
				.stream().map(x -> new ApproveRootStatusForEmpImPort(
						x.getEmployeeID(), 
						x.getDate(), 
						EnumAdaptor.valueOf(x.getDailyConfirmAtr(),ApprovalStatusForEmployeeImport.class))).collect(Collectors.toList());
	}
	@Override
	public List<String> getApproverFromPhase(String appID) {
//		return approvalRootStatePub.getApproverFromPhase(appID);
		return null;
	}
	@Override
	public List<ApproverRemandImport> getListApproverRemand(String appID) {
		return approvalRootStatePub.getListApproverRemand(appID).stream()
								.map(c-> new ApproverRemandImport(c.getPhaseOrder(), c.getSID(), c.isAgent()))
								.collect(Collectors.toList());
	}
	@Override
	public Boolean isApproveApprovalPhaseStateComplete(String companyID, String rootStateID, Integer phaseNumber) {
		return approvalRootStatePub.isApproveApprovalPhaseStateComplete(companyID, rootStateID, phaseNumber);
	}
	@Override
	public List<ApproveRootStatusForEmpImPort> getAppRootStatusByEmpPeriodMonth(String employeeID, DatePeriod period) {
		return Collections.emptyList();
		/*return intermediateDataPub.getAppRootStatusByEmpPeriodMonth(employeeID, period)
				.stream().map(x -> new ApproveRootStatusForEmpImPort(
						x.getEmployeeID(), 
						x.getDate(), 
						EnumAdaptor.valueOf(x.getDailyConfirmAtr(),ApprovalStatusForEmployeeImport.class))).collect(Collectors.toList());*/
	}

	@Override
	public Map<String,List<ApprovalPhaseStateImport_New>> getApprovalRootContentCMM045(String companyID, String approverID, 
			List<String> lstAgent, DatePeriod period, boolean unapprovalStatus, boolean approvalStatus, boolean denialStatus, 
			boolean agentApprovalStatus, boolean remandStatus, boolean cancelStatus) {
		Map<String,List<ApprovalPhaseStateImport_New>> approvalPhaseImport_NewMap = new LinkedHashMap<>();
		Map<String,List<ApprovalPhaseStateExport>> approvalRootContentExports = approvalRootStatePub.getApprovalRootCMM045(companyID, approverID, lstAgent, period,
				unapprovalStatus, approvalStatus, denialStatus, agentApprovalStatus, remandStatus, cancelStatus);
		for(Map.Entry<String,List<ApprovalPhaseStateExport>> approvalRootContentExport : approvalRootContentExports.entrySet()){
					
			List<ApprovalPhaseStateImport_New> appRootContentImport_News = fromExport(approvalRootContentExport.getValue(), Optional.empty());
            approvalPhaseImport_NewMap.put(approvalRootContentExport.getKey(), appRootContentImport_News);
        }
        return approvalPhaseImport_NewMap;
}

	@Override
	public List<ApprovalPhaseStateImport_New> getApprovalDetail(String appID) {
		String companyID = AppContexts.user().companyId();
		List<ApprovalPhaseStateExport> listApprovalPhaseState = approvalRootStatePub.getApprovalDetail(appID);
		MailDestinationCache mailDestCache = createMailDestinationCache(companyID);
		return fromExport(listApprovalPhaseState, Optional.of(mailDestCache));
	}
	
	private List<ApprovalPhaseStateImport_New> fromExport(List<ApprovalPhaseStateExport> listApprovalPhaseState, Optional<MailDestinationCache> opMailDestCache) {
		return listApprovalPhaseState.stream()
		.map(x -> {
			return new ApprovalPhaseStateImport_New(
					x.getPhaseOrder(), 
					EnumAdaptor.valueOf(x.getApprovalAtr().value, ApprovalBehaviorAtrImport_New.class),
					EnumAdaptor.valueOf(x.getApprovalForm().value, ApprovalFormImport.class),
					x.getListApprovalFrame().stream()
					.map(y -> {
						return new ApprovalFrameImport_New(
								y.getFrameOrder(), 
								y.getListApprover().stream().map(z -> {
									String approverMail = "";
									String agentMail = "";
									String representerMail = "";
									if(opMailDestCache.isPresent()) {
										MailDestinationCache mailDestCache = opMailDestCache.get();
										List<MailDestination> approverDest = mailDestCache.get(z.getApproverID());
										if(!CollectionUtil.isEmpty(approverDest)){
											List<OutGoingMail> approverOuts = approverDest.get(0).getOutGoingMails();
											if(!CollectionUtil.isEmpty(approverOuts)){
												approverMail = Strings.isNotBlank(approverOuts.get(0).getEmailAddress())
														? approverOuts.get(0).getEmailAddress() : "";
											}
										}
										if(Strings.isNotBlank(z.getAgentID())){
											List<MailDestination> agentDest = mailDestCache.get(z.getAgentID());
											if(!CollectionUtil.isEmpty(agentDest)){
												List<OutGoingMail> agentOuts = agentDest.get(0).getOutGoingMails();
												if(!CollectionUtil.isEmpty(agentOuts)){
													agentMail = Strings.isNotBlank(agentOuts.get(0).getEmailAddress())
															? agentOuts.get(0).getEmailAddress() : "";
												}
											}
										}
										if(Strings.isNotBlank(z.getRepresenterID())){
											List<MailDestination> representerDest = mailDestCache.get(z.getRepresenterID());
											if(!CollectionUtil.isEmpty(representerDest)){
												List<OutGoingMail> representerOuts = representerDest.get(0).getOutGoingMails();
												if(!CollectionUtil.isEmpty(representerOuts)){
													representerMail = Strings.isNotBlank(representerOuts.get(0).getEmailAddress())
															? representerOuts.get(0).getEmailAddress() : "";
												}
											}
										}
									}
									return new ApproverStateImport_New(
											z.getApproverID(), 
											EnumAdaptor.valueOf(z.getApprovalAtr().value, ApprovalBehaviorAtrImport_New.class),
											z.getAgentID(),
											z.getApproverName(), 
											z.getAgentName(),
											z.getRepresenterID(),
											z.getRepresenterName(),
											z.getApprovalDate(),
											z.getApprovalReason(),
											approverMail,
											agentMail,
											representerMail,
											z.getApproverInListOrder());
									}).collect(Collectors.toList()), 
								y.getConfirmAtr(),
								y.getAppDate());
					}).collect(Collectors.toList()));
		}).collect(Collectors.toList());
	}

	@Override
	public void insertApp(String appID, GeneralDate appDate, String employeeID, List<ApprovalPhaseStateImport_New> listApprovalPhaseState) {
		List<ApprovalPhaseStateExport> approvalPhaseStateExportLst = listApprovalPhaseState.stream()
				.map(x -> new ApprovalPhaseStateExport(
						x.getPhaseOrder(), 
						EnumAdaptor.valueOf(x.getApprovalAtr().value, ApprovalBehaviorAtrExport.class), 
						EnumAdaptor.valueOf(x.getApprovalForm().value, ApprovalFormExport.class), 
						x.getListApprovalFrame().stream().map(y -> new ApprovalFrameExport(
								y.getFrameOrder(), 
								y.getListApprover().stream().map(z -> new ApproverStateExport(
										z.getApproverID(), 
										EnumAdaptor.valueOf(z.getApprovalAtr().value, ApprovalBehaviorAtrExport.class), 
										z.getAgentID(), 
										z.getApproverName(), 
										z.getAgentName(),
										z.getRepresenterID(), 
										z.getRepresenterName(), 
										z.getApprovalDate(), 
										z.getApprovalReason(), 
										z.getApproverInListOrder())
								).collect(Collectors.toList()), 
								y.getConfirmAtr(), 
								appDate))
						.collect(Collectors.toList())
				)).collect(Collectors.toList());
		approvalRootStatePub.insertApp(appID, appDate, employeeID, approvalPhaseStateExportLst);
	}

	@Override
	public Map<String, List<ApprovalPhaseStateImport_New>> getApprovalPhaseByID(List<String> appIDLst) {
		Map<String,List<ApprovalPhaseStateImport_New>> approvalPhaseImport_NewMap = new LinkedHashMap<>();
		Map<String,List<ApprovalPhaseStateExport>> approvalRootContentExports = approvalRootStatePub.getApprovalPhaseByID(appIDLst);
		for(Map.Entry<String,List<ApprovalPhaseStateExport>> approvalRootContentExport : approvalRootContentExports.entrySet()){
					
			List<ApprovalPhaseStateImport_New> appRootContentImport_News = fromExport(approvalRootContentExport.getValue(), Optional.empty());
			approvalPhaseImport_NewMap.put(approvalRootContentExport.getKey(), appRootContentImport_News);
		}
		return approvalPhaseImport_NewMap;
	}

	@Override
	public Request533Import getAppRootStatusByEmpsMonth(List<EmpPerformMonthParamAt> empPerformMonthParamLst) {
		Request533Export request533Export = intermediateDataPub.getAppRootStatusByEmpsMonth(empPerformMonthParamLst.stream()
				.map(x -> new EmpPerformMonthParam(
						x.getYearMonth(), 
						x.getClosureID(), 
						x.getClosureDate(), 
						x.getBaseDate(), 
						x.getEmployeeID())
				).collect(Collectors.toList()));
		return new Request533Import(
				request533Export.getAppRootSttMonthExportLst().stream()
					.map(x -> new AppRootSttMonthImport(
							x.getEmployeeID(), 
							x.getDailyConfirmAtr(), 
							x.getYearMonth(), 
							x.getClosureID(), 
							x.getClosureDate()))
					.collect(Collectors.toList()), 
				request533Export.isErrorFlg(), 
				request533Export.getErrorMsgID(), 
				request533Export.getErrorEmpLst());
	}

	@Override
	public List<ApprovalRootStateImport_New> getAppRootInstanceDayByEmpPeriod(List<String> employeeIDLst,
			DatePeriod period, Integer rootType) {
		return intermediateDataPub.getAppRootInstanceDayByEmpPeriod(employeeIDLst, period, rootType).stream()
			.map(x -> new ApprovalRootStateImport_New(
					x.getRootStateID(),
					fromExport(x.getListApprovalPhaseState(), Optional.empty()), 
					x.getApprovalRecordDate(),
					x.getRootType(),
					x.getEmployeeID()))
			.collect(Collectors.toList());
	}

	@Override
	public ApprovalRootStateImport_New getAppRootInstanceMonthByEmpPeriod(String employeeID, DatePeriod period, YearMonth yearMonth, Integer closureID,
			ClosureDate closureDate, GeneralDate baseDate) {
		ApprovalRootStateExport approvalRootStateExport = intermediateDataPub.getAppRootInstanceMonthByEmpPeriod(employeeID, period, yearMonth, closureID,
				closureDate, baseDate);
		if(approvalRootStateExport==null) {
			return null;
		}
		return new ApprovalRootStateImport_New(
				approvalRootStateExport.getRootStateID(),
				fromExport(approvalRootStateExport.getListApprovalPhaseState(), Optional.empty()), 
				approvalRootStateExport.getApprovalRecordDate(),
				approvalRootStateExport.getRootType(),
				approvalRootStateExport.getEmployeeID()); 
	}

	@Override
	public List<ApproverApproveImport> getApproverByDateLst(List<String> employeeIDLst, List<GeneralDate> dateLst,
			Integer rootType) {
		return intermediateDataPub.getApproverByDateLst(employeeIDLst, dateLst, rootType).stream()
				.map(x -> new ApproverApproveImport(
						x.getDate(), 
						x.getEmployeeID(), 
						x.getAuthorList().stream().map(y -> new ApproverEmpImport(
								y.getEmployeeID(), 
								y.getEmployeeCD(), 
								y.getEmployeeName())).collect(Collectors.toList())))
				.collect(Collectors.toList());
	}

	@Override
	public ApproverApproveImport getApproverByPeriodMonth(String employeeID, Integer closureID, YearMonth yearMonth,
			ClosureDate closureDate, GeneralDate date) {
		ApproverApproveExport approverApproveExport = intermediateDataPub.getApproverByPeriodMonth(employeeID, closureID, yearMonth, closureDate, date);
		return new ApproverApproveImport(
				approverApproveExport.getDate(), 
				approverApproveExport.getEmployeeID(), 
				approverApproveExport.getAuthorList().stream().map(y -> new ApproverEmpImport(
						y.getEmployeeID(), 
						y.getEmployeeCD(), 
						y.getEmployeeName())).collect(Collectors.toList()));
	}

	@Override
	public List<AppRootInsPeriodImport> getAppRootInstanceByEmpPeriod(List<String> employeeIDLst, DatePeriod period,
			Integer rootType) {
		return intermediateDataPub.getAppRootInstanceByEmpPeriod(employeeIDLst, period, rootType)
			.stream().map(x -> new AppRootInsPeriodImport(
					x.getEmployeeID(), 
					x.getAppRootInstanceLst().stream().map(y -> new AppRootInsImport(
								y.getRootID(), 
								y.getCompanyID(), 
								y.getEmployeeID(), 
								y.getDatePeriod(), 
								y.getRootType(), 
								y.getListAppPhase().stream().map(z -> new AppPhaseInsImport(
											z.getPhaseOrder(), 
											z.getApprovalForm(), 
											z.getListAppFrame().stream().map(t -> new AppFrameInsImport(
														t.getFrameOrder(), 
														t.isConfirmAtr(), 
														t.getListApprover())
											).collect(Collectors.toList()))
								).collect(Collectors.toList()))
					).collect(Collectors.toList()))
			).collect(Collectors.toList());
	}

}
