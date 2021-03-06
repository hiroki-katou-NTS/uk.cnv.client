package nts.uk.screen.at.app.kdw013.h;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.app.find.dailyperform.DailyRecordDto;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.ApprovalStatusActualResult;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.ConfirmStatusActualResult;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.ModeData;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.change.approval.ApprovalStatusActualDayChange;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.confirmationstatus.change.confirm.ConfirmStatusActualDayChange;
import nts.uk.ctx.at.shared.dom.dailyattdcal.dailyattendance.IntegrationOfDailyGetter;
import nts.uk.ctx.at.shared.dom.scherec.attendanceitem.converter.service.AttendanceItemConvertFactory;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.DailyRecordToAttendanceItemConverter;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.converter.util.item.ItemValue;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.dailyattendancework.IntegrationOfDaily;
import nts.uk.screen.at.app.dailymodify.command.DailyModifyRCommandFacade;
import nts.uk.screen.at.app.dailyperformance.correction.dto.ApprovalConfirmCache;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPItemParent;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPItemValue;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DataResultAfterIU;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DateRange;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.context.LoginUserContext;

/**
 * @author thanhpv
 * @part UKDesign.UniversalK.??????.KDW_????????????.KDW013_????????????.H?????????????????????.???????????????OCD
 */
@Stateless
public class CreateAchievementRegistrationParam {
    
    @Inject 
    private ApprovalStatusActualDayChange approvalStatusActualDayChange;
    
    @Inject
    private ConfirmStatusActualDayChange confirmStatusActualDayChange;
    
    @Inject
    private DailyModifyRCommandFacade dailyModifyRCommandFacade;
    
    @Inject 
    private IntegrationOfDailyGetter integrationOfDailyGetter;
    
    @Inject
    private AttendanceItemConvertFactory attendanceItemConvertFactory;
    
    /**
     * @name ???????????????????????????
     */
    public DataResultAfterIU registerAchievements(String empTarget, GeneralDate targetDate, List<ItemValue> items){
    	
    	//call ScreenQuery ??????????????????????????????????????????
    	// ch??a c?? m?? t??? param truy???n v??o.
    	DPItemParent DPItemParent = this.create(empTarget, targetDate, items);
    	
    	//Call ?????????????????????????????????
    	//QA: 120067 -  ??ang h???i anh thanhNX - Anh thanhNX tr??? l???i l?? h??m DailyModifyRCommandFacade.insertItemDomain()
    	//V?? param ???????????????????????????"Mode s???a qu?? kh??? " l?? ??ang thi???t k??? n??n v???n ch??a c?? source code.
		return dailyModifyRCommandFacade.insertItemDomain(DPItemParent);
    }
    
    //????????????????????????????????????
    public List<ItemValue> getIntegrationOfDaily(String empTarget, GeneralDate targetDate, List<Integer> items){
    	// 1:get()
    	List<IntegrationOfDaily> integrationOfDailys = integrationOfDailyGetter.getIntegrationOfDaily(empTarget, new DatePeriod(targetDate, targetDate));
    	Optional<IntegrationOfDaily> integrationOfDaily = integrationOfDailys.stream().filter(c->c.getYmd().equals(targetDate)).findFirst();
    	
    	// 2:<call> ItemValue???????????????
    	DailyRecordToAttendanceItemConverter dailyRecordToAttendanceItemConverter = attendanceItemConvertFactory.createDailyConverter();
    	dailyRecordToAttendanceItemConverter.setData(integrationOfDaily.get());															
    	return dailyRecordToAttendanceItemConverter.convert(items);
    }
    
    /**
     * @name ??????????????????????????????????????????
     * @param targetEmployee ????????????
     * @param targetDate ?????????
     * @param items ???????????????List<itemvalue>
     * @param integrationOfDaily ????????????(Work)
     * @return
     */
    public DPItemParent create(String empTarget, GeneralDate targetDate, List<ItemValue> items){
    	LoginUserContext loginUserContext = AppContexts.user();
    	
    	//DPItemValue???????????????
    	List<DPItemValue> itemValues = items.stream().map(i -> new DPItemValue(empTarget, targetDate, i)).collect(Collectors.toList());
    	
    	//[No.585]?????????????????????????????????????????????NEW???
    	List<ApprovalStatusActualResult> lstApproval = approvalStatusActualDayChange.processApprovalStatus(loginUserContext.companyId(), loginUserContext.employeeId(), Arrays.asList(empTarget), Optional.of(new DatePeriod(targetDate, targetDate)), Optional.empty(), ModeData.NORMAL.value);
    	
    	//[No.584]?????????????????????????????????????????????NEW???
    	List<ConfirmStatusActualResult> lstConfirm = confirmStatusActualDayChange.processConfirmStatus(loginUserContext.companyId(), loginUserContext.employeeId(), Arrays.asList(empTarget), Optional.of(new DatePeriod(targetDate, targetDate)), Optional.empty());
    	
    	//approvalConfirmCache???????????????
    	ApprovalConfirmCache approvalConfirmCache = new ApprovalConfirmCache(loginUserContext.employeeId(), Arrays.asList(empTarget), new DatePeriod(targetDate, targetDate), 0, lstConfirm, lstApproval);
    	
    	//???????????????(Work)??????????????????
    	List<IntegrationOfDaily> integrationOfDailys = integrationOfDailyGetter.getIntegrationOfDaily(empTarget, new DatePeriod(targetDate, targetDate));
    	Optional<IntegrationOfDaily> integrationOfDaily = integrationOfDailys.stream().filter(c->c.getYmd().equals(targetDate)).findFirst();
    	
    	//DPItemParent???????????????
    	return new DPItemParent(
    			0, 
    			empTarget,
    			itemValues, 
    			new ArrayList<>(), 
    			new ArrayList<>(), 
    			new DateRange(targetDate, targetDate), 
    			null, 
    			null, 
    			Arrays.asList(DailyRecordDto.from(integrationOfDaily.get()).clone()), 
    			Arrays.asList(DailyRecordDto.from(integrationOfDaily.get()).clone()), 
    			Arrays.asList(DailyRecordDto.from(integrationOfDaily.get()).clone()), 
    			false, 
    			new ArrayList<>(), 
    			new HashMap<>(), 
    			new ArrayList<>(), 
    			new ArrayList<>(), 
    			false, 
    			new ArrayList<>(), 
    			false, 
    			false, 
    			true, 
    			approvalConfirmCache, 
    			Optional.empty(), 
    			null,
    			null);
    }
}
