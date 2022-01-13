package nts.uk.ctx.at.shared.app.command.scherec.monthlyattendanceitem;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.aggr.pastmonth.AggregatePastMonthResult;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeOfManagePeriod;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.agreement.AgreementTimeOfManagePeriodRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.AttendanceTimeOfMonthlyRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.anyitem.AnyItemOfMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.monthly.anyitem.AnyItemOfMonthlyRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.weekly.AttendanceTimeOfWeekly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattdcal.weekly.AttendanceTimeOfWeeklyRepository;

/**
 * 過去月集計結果を登録する
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.shared(勤務予定、勤務実績).月の勤怠計算.月別実績.App.過去月集計結果を登録する
 * @author tutk
 *
 */
@Stateless
public class RegisterPastMonthTotalResult {
	
	@Inject
	private AnyItemOfMonthlyRepository anyItemOfMonthlyRepository;
	
	@Inject
	private AgreementTimeOfManagePeriodRepository agreementTimeOfManagePeriodRepository;
	
	@Inject
	private AttendanceTimeOfMonthlyRepository attendanceTimeOfMonthlyRepository;
	
	@Inject
	private AttendanceTimeOfWeeklyRepository attendanceTimeOfWeeklyRepository;
	
	public void register(List<AggregatePastMonthResult> listAggregatePastMonthResult) {
		for(AggregatePastMonthResult aggregatePastMonthResult : listAggregatePastMonthResult) {
			//1:2:3:
			List<AnyItemOfMonthly> monthlyAnyItem =  aggregatePastMonthResult.getMonthlyAnyItem();
			anyItemOfMonthlyRepository.persistAndUpdate(monthlyAnyItem);
			
			//4:5:6:
			List<AgreementTimeOfManagePeriod> agreementTimes = aggregatePastMonthResult.getAgreementTime();
			for(AgreementTimeOfManagePeriod agreementTime : agreementTimes) {
				agreementTimeOfManagePeriodRepository.persistAndUpdate(agreementTime);
			}
			
			//7:8:9:
			attendanceTimeOfMonthlyRepository.persistAndUpdate(aggregatePastMonthResult.getMonthlyAttdTime(), Optional.empty());
			
			//10:11:12:
			List<AttendanceTimeOfWeekly> weeklyAttdTimes = aggregatePastMonthResult.getWeeklyAttdTime();
			for(AttendanceTimeOfWeekly weeklyAttdTime : weeklyAttdTimes) {
				attendanceTimeOfWeeklyRepository.persistAndUpdate(weeklyAttdTime);
			}
			
		}
	}

}
