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
 * @part UKDesign.UniversalK.就業.KDW_日別実績.KDW013_工数入力.H：実績内容確認.メニュー別OCD
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
     * @name 実績内容を登録する
     */
    public DataResultAfterIU registerAchievements(String empTarget, GeneralDate targetDate, List<ItemValue> items){
    	
    	//call ScreenQuery 実績登録パラメータを作成する
    	// chưa có mô tả param truyền vào.
    	DPItemParent DPItemParent = this.create(empTarget, targetDate, items);
    	
    	//Call 修正した実績を登録する
    	//QA: 120067 -  đang hỏi anh thanhNX - Anh thanhNX trả lời là hàm DailyModifyRCommandFacade.insertItemDomain()
    	//Vì param 「過去修正モード」"Mode sửa quá khứ " là đang thiết kế nên vẫn chưa có source code.
		return dailyModifyRCommandFacade.insertItemDomain(DPItemParent);
    }
    
    //日別実績データを取得する
    public List<ItemValue> getIntegrationOfDaily(String empTarget, GeneralDate targetDate, List<Integer> items){
    	// 1:get()
    	List<IntegrationOfDaily> integrationOfDailys = integrationOfDailyGetter.getIntegrationOfDaily(empTarget, new DatePeriod(targetDate, targetDate));
    	Optional<IntegrationOfDaily> integrationOfDaily = integrationOfDailys.stream().filter(c->c.getYmd().equals(targetDate)).findFirst();
    	
    	// 2:<call> ItemValueに変換する
    	DailyRecordToAttendanceItemConverter dailyRecordToAttendanceItemConverter = attendanceItemConvertFactory.createDailyConverter();
    	dailyRecordToAttendanceItemConverter.setData(integrationOfDaily.get());															
    	return dailyRecordToAttendanceItemConverter.convert(items);
    }
    
    /**
     * @name 実績登録パラメータを作成する
     * @param targetEmployee 対象社員
     * @param targetDate 対象日
     * @param items 修正内容：List<itemvalue>
     * @param integrationOfDaily 日別実績(Work)
     * @return
     */
    public DPItemParent create(String empTarget, GeneralDate targetDate, List<ItemValue> items){
    	LoginUserContext loginUserContext = AppContexts.user();
    	
    	//DPItemValueを作成する
    	List<DPItemValue> itemValues = items.stream().map(i -> new DPItemValue(empTarget, targetDate, i)).collect(Collectors.toList());
    	
    	//[No.585]日の実績の承認状況を取得する（NEW）
    	List<ApprovalStatusActualResult> lstApproval = approvalStatusActualDayChange.processApprovalStatus(loginUserContext.companyId(), loginUserContext.employeeId(), Arrays.asList(empTarget), Optional.of(new DatePeriod(targetDate, targetDate)), Optional.empty(), ModeData.NORMAL.value);
    	
    	//[No.584]日の実績の確認状況を取得する（NEW）
    	List<ConfirmStatusActualResult> lstConfirm = confirmStatusActualDayChange.processConfirmStatus(loginUserContext.companyId(), loginUserContext.employeeId(), Arrays.asList(empTarget), Optional.of(new DatePeriod(targetDate, targetDate)), Optional.empty());
    	
    	//approvalConfirmCacheを作成する
    	ApprovalConfirmCache approvalConfirmCache = new ApprovalConfirmCache(loginUserContext.employeeId(), Arrays.asList(empTarget), new DatePeriod(targetDate, targetDate), 0, lstConfirm, lstApproval);
    	
    	//「日別勤怠(Work)」を取得する
    	List<IntegrationOfDaily> integrationOfDailys = integrationOfDailyGetter.getIntegrationOfDaily(empTarget, new DatePeriod(targetDate, targetDate));
    	Optional<IntegrationOfDaily> integrationOfDaily = integrationOfDailys.stream().filter(c->c.getYmd().equals(targetDate)).findFirst();
    	
    	//DPItemParentを作成する
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
