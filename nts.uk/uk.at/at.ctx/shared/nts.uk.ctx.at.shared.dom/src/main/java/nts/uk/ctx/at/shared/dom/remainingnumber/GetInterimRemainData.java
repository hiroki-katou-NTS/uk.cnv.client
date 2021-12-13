package nts.uk.ctx.at.shared.dom.remainingnumber;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.shared.dom.holidaymanagement.publicholiday.interimdata.TempPublicHolidayManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimAbsMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.absencerecruitment.interim.InterimRecMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.DailyInterimRemainMngData;
import nts.uk.ctx.at.shared.dom.remainingnumber.annualleave.interim.TempAnnualLeaveMngs;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimBreakMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimDayOffMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.interimremain.InterimRemain;
import nts.uk.ctx.at.shared.dom.remainingnumber.nursingcareleavemanagement.care.interimdata.TempCareManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.nursingcareleavemanagement.childcare.interimdata.TempChildCareManagement;
import nts.uk.ctx.at.shared.dom.remainingnumber.reserveleave.interim.TmpResereLeaveMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.specialholidaymng.interim.InterimSpecialHolidayMng;

/**
 * 期間内の暫定管理データを取得する
 * @author hayata_maekawa
 *
 */
public class GetInterimRemainData {

    public static List<DailyInterimRemainMngData> getInterimRemainData(Require require,String employeeId, DatePeriod period){
        
        List<InterimRemain> interimRemainList= new ArrayList<>();
        
        List<TempAnnualLeaveMngs> annualLeaveList = require.tmpAnnualHolidayMng(employeeId, period);
        interimRemainList.addAll(annualLeaveList);
        
        List<TmpResereLeaveMng> resereLeaveList = require.tmpResereLeaveMng(employeeId, period);
        interimRemainList.addAll(resereLeaveList);
        
        List<InterimSpecialHolidayMng> specialHolidayList =require.interimSpecialHolidayMng(employeeId, period);
        interimRemainList.addAll(specialHolidayList);
        
        List<InterimDayOffMng> dayOffList = require.getDayOffBySidPeriod(employeeId, period);
        interimRemainList.addAll(dayOffList);
        
        List<InterimBreakMng> breakList = require.getBreakBySidPeriod(employeeId, period);
        interimRemainList.addAll(breakList);
        
        List<InterimRecMng> recList = require.getRecBySidDatePeriod(employeeId, period);
        interimRemainList.addAll(recList);
        
        List<InterimAbsMng> absList = require.getAbsBySidDatePeriod(employeeId, period);
        interimRemainList.addAll(absList);
        
        List<TempChildCareManagement> childCareList = require.findChildCareByPeriodOrderByYmd(employeeId, period);
        interimRemainList.addAll(childCareList);
        
        List<TempCareManagement> careList = require.findCareByPeriodOrderByYmd(employeeId, period);
        interimRemainList.addAll(careList);
        
        List<TempPublicHolidayManagement> publicHolidayList = require.tempPublicHolidayManagement(employeeId, period);
        interimRemainList.addAll(publicHolidayList);
        
        return period.stream().map(x ->new DailyInterimRemainMngData(x,
                absList.stream().filter(c->c.getYmd().equals(x)).findFirst(),
                interimRemainList.stream().filter(c->c.getYmd().equals(x)).collect(Collectors.toList()),
                recList.stream().filter(c->c.getYmd().equals(x)).findFirst(),
                dayOffList.stream().filter(c->c.getYmd().equals(x)).collect(Collectors.toList()),
                annualLeaveList.stream().filter(c->c.getYmd().equals(x)).collect(Collectors.toList()),
                resereLeaveList.stream().filter(c->c.getYmd().equals(x)).findFirst(),
                breakList.stream().filter(c->c.getYmd().equals(x)).findFirst(),
                specialHolidayList.stream().filter(c->c.getYmd().equals(x)).collect(Collectors.toList()),
                childCareList.stream().filter(c->c.getYmd().equals(x)).collect(Collectors.toList()),
                careList.stream().filter(c->c.getYmd().equals(x)).collect(Collectors.toList()),
                publicHolidayList.stream().filter(c->c.getYmd().equals(x)).collect(Collectors.toList())
                )).collect(Collectors.toList());
    }
    

    public static interface Require{
        
        List<TempAnnualLeaveMngs> tmpAnnualHolidayMng(String sid, DatePeriod period);
        
        List<TmpResereLeaveMng> tmpResereLeaveMng(String sid, DatePeriod period);
        
        List<InterimSpecialHolidayMng> interimSpecialHolidayMng(String sId , DatePeriod period);
        
        List<InterimDayOffMng> getDayOffBySidPeriod(String sid, DatePeriod period);
        
        List<InterimBreakMng> getBreakBySidPeriod(String sid, DatePeriod period);
        
        List<InterimRecMng> getRecBySidDatePeriod(String sid, DatePeriod period);
        
        List<InterimAbsMng> getAbsBySidDatePeriod(String sid, DatePeriod period);
        
        List<TempChildCareManagement> findChildCareByPeriodOrderByYmd(String employeeId, DatePeriod period);
        
        List<TempCareManagement> findCareByPeriodOrderByYmd(String employeeId, DatePeriod period);

        List<TempPublicHolidayManagement> tempPublicHolidayManagement(String sid, DatePeriod period);
        
    }
}