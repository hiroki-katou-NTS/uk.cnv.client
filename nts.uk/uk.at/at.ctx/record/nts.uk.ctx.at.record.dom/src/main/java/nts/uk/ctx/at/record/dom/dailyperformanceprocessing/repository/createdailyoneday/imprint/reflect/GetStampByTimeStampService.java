package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyoneday.imprint.reflect;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.val;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCard;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockAtr;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.DayAttr;
import nts.uk.shr.com.time.TimeWithDayAttr;

/** 打刻時刻と該当する打刻を取得する */
public class GetStampByTimeStampService {

	public static Optional<Stamp> get(Require require, String sid, GeneralDate ymd, boolean startAtr, TimeWithDayAttr stampTime) {
		if (stampTime == null) {
			return Optional.empty();
		}
		
		/** 打刻時刻と年月日から打刻の日時を計算する */
		val stampDate = ymd.addDays(getDayToAdd(stampTime));
		val timeToGet = GeneralDateTime.ymdhms(stampDate.year(), stampDate.month(), stampDate.day(), 0, 0, 0)
					.addMinutes(stampTime.getDayTime());
		
		/** Requireで「社員の打刻カード」を取得する */
		val stampCards = require.stampCard(AppContexts.user().contractCode(), sid);
		
		/** 時刻変更区分を取得する */
		val timeChangeAtr = startAtr ? ChangeClockAtr.GOING_TO_WORK : ChangeClockAtr.WORKING_OUT;
		
		/** Require「打刻」を取得する*/
		return require.stamp(stampCards.stream().map(c -> c.getStampNumber().v()).collect(Collectors.toList()), 
									AppContexts.user().companyId(), timeToGet, timeToGet)
							.stream().filter(c -> c.getType().getChangeClockArt() == timeChangeAtr).findFirst();
	}
	
	private static int getDayToAdd(TimeWithDayAttr time) {
		if (time.getDayDivision() == DayAttr.TWO_DAY_EARLIER) {
			return -2;
		}
		
		return time.getDayDivision().value - 1;
		
	}
	
	public static interface Require {
		
		List<StampCard> stampCard(String contractCode, String sid);

		List<Stamp> stamp(List<String> listCard,String companyId, GeneralDateTime startDate, GeneralDateTime endDate);
	}
}
