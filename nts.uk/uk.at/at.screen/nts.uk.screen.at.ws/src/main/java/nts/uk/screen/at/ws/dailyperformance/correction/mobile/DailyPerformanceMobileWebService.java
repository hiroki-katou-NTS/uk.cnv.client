package nts.uk.screen.at.ws.dailyperformance.correction.mobile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import lombok.val;
import nts.arc.web.session.HttpSubSession;
import nts.uk.ctx.at.function.app.find.dailyperformanceformat.DailyPerformanceMobileCodeFinder;
import nts.uk.ctx.at.function.app.find.dailyperformanceformat.dto.DailyPerformanceCodeDto;
import nts.uk.ctx.at.record.app.find.dailyperform.DailyRecordDto;
import nts.uk.ctx.at.record.app.find.monthly.root.MonthlyRecordWorkDto;
import nts.uk.screen.at.app.dailymodify.mobile.DailyModifyMobileCommandFacade;
import nts.uk.screen.at.app.dailymodify.mobile.dto.DPMobileAdUpParam;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ApprovalConfirmCache;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPAttendanceItem;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPCorrectionInitParam;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPItemCheckBox;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DailyPerformanceCorrectionDto;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DataResultAfterIU;
import nts.uk.screen.at.app.dailyperformance.correction.dto.cache.DPCorrectionStateParam;
import nts.uk.screen.at.app.dailyperformance.correction.mobile.InitScreenMob;
import nts.uk.screen.at.app.dailyperformance.correction.mobile.UpdateConfirmAllMob;

@Path("screen/at/correctionofdailyperformance")
@Produces("application/json")
public class DailyPerformanceMobileWebService {

	@Inject
	private HttpSubSession session;

	@Inject
	private DailyModifyMobileCommandFacade dailyModifyMobiCommandFacade;
	
	@Inject
	private InitScreenMob initScreenMob;
	
	@Inject
	private UpdateConfirmAllMob updateConfirmAllMob;
	
	@Inject
	private DailyPerformanceMobileCodeFinder dailyPerformanceMobileCodeFinder;

	@POST
	@Path("addUpMobile")
	@SuppressWarnings("unchecked")
	public DataResultAfterIU addAndUpdate(DPMobileAdUpParam dataParent) {
		val domain = session.getAttribute("domainEdits");
		List<DailyRecordDto> dailyEdits = new ArrayList<>();
		if (domain == null) {
			dailyEdits = cloneListDto((List<DailyRecordDto>) session.getAttribute("domainOlds"));
		} else {
			dailyEdits = (List<DailyRecordDto>) domain;
		}
		dataParent.setDailyEdits(dailyEdits);
		dataParent.setDailyOlds((List<DailyRecordDto>) session.getAttribute("domainOlds"));
		dataParent.setDailyOldForLog((List<DailyRecordDto>) session.getAttribute("domainOldForLog"));
		dataParent.setLstAttendanceItem((Map<Integer, DPAttendanceItem>) session.getAttribute("itemIdRCs"));
		dataParent.setApprovalConfirmCache((ApprovalConfirmCache) session.getAttribute("approvalConfirm"));
		dataParent.setStateParam((DPCorrectionStateParam)session.getAttribute("dpStateParam"));
		Object objectCacheMonth = session.getAttribute("domainMonths");
		Optional<MonthlyRecordWorkDto> domainMonthOpt = objectCacheMonth == null ? Optional.empty()
				: (Optional<MonthlyRecordWorkDto>) objectCacheMonth;
		dataParent.setDomainMonthOpt(domainMonthOpt);
		DataResultAfterIU dataResultAfterIU = dailyModifyMobiCommandFacade.insertItemDomain(dataParent);
		// TODO: set cache month
		if (dataResultAfterIU.getDomainMonthOpt().isPresent()) {
			session.setAttribute("domainMonths", dataResultAfterIU.getDomainMonthOpt());
		}
		return dataResultAfterIU;
	}

	@POST
	@Path("resetCacheDomain")
	public void resetCacheDomain() {
		session.setAttribute("domainEdits", null);
		return ;
	}
	
	@POST
	@Path("initMOB")
	public DailyPerformanceCorrectionDto initScreen(DPCorrectionInitParam param) throws InterruptedException{
		param.dpStateParam = (DPCorrectionStateParam)session.getAttribute("dpStateParam");
		DailyPerformanceCorrectionDto dtoResult = this.initScreenMob.initMOB(param);
		session.setAttribute("dpStateParam", dtoResult.getStateParam());
		if (dtoResult.getErrorInfomation() != 0 || !dtoResult.getErrors().isEmpty()) {
			return dtoResult;
		}
		session.setAttribute("domainOlds", dtoResult.getDomainOld());		
		//add
		session.setAttribute("domainOldForLog", cloneListDto(dtoResult.getDomainOld()));
		session.setAttribute("domainEdits", null);
		session.setAttribute("itemIdRCs", dtoResult.getLstControlDisplayItem() == null ? null : dtoResult.getLstControlDisplayItem().getMapDPAttendance());
		session.setAttribute("dataSource", dtoResult.getLstData());
		session.setAttribute("closureId", dtoResult.getClosureId());
		session.setAttribute("resultReturn", null);
		session.setAttribute("approvalConfirm", dtoResult.getApprovalConfirmCache());
		dtoResult.setApprovalConfirmCache(null);
		dtoResult.setLstCellState(dtoResult.getMapCellState().values().stream().collect(Collectors.toList()));
		dtoResult.setMapCellState(null);
		removeSession();
		dtoResult.setDomainOld(Collections.emptyList());
		return dtoResult;
	}
	
	@POST
	@Path("confirmAll")
	@SuppressWarnings("unchecked")
	public void confirmAll(List<DPItemCheckBox> dataCheckSign) throws InterruptedException{
		List<DailyRecordDto> dailyRecordDtos = (List<DailyRecordDto>) session.getAttribute("domainOlds");
		updateConfirmAllMob.confirmAll(dataCheckSign, dailyRecordDtos);
		return;
	}
	
	private List<DailyRecordDto> cloneListDto(List<DailyRecordDto> dtos) {
		if (dtos == null)
			return new ArrayList<>();
		return dtos.stream().map(x -> x.clone()).collect(Collectors.toList());
	}
	
	private void removeSession() {
		session.setAttribute("lstSidDateErrorCalc", Collections.emptyList());
		session.setAttribute("errorAllCalc", false);
	}
	
	@POST
	@Path("getFormatList")
	public List<DailyPerformanceCodeDto> getAuthorityDailyFormat() {
		return this.dailyPerformanceMobileCodeFinder.findAll();
	}

}
