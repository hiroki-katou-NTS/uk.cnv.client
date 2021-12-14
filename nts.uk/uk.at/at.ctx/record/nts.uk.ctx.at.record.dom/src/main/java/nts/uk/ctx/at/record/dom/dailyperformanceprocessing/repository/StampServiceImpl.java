package nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.app.cache.CacheCarrier;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.dailyperformanceprocessing.repository.createdailyoneday.EmbossingExecutionFlag;
import nts.uk.ctx.at.record.dom.stamp.card.StampCardItem;
import nts.uk.ctx.at.record.dom.stamp.card.StampCardtemRepository;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCard;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCardRepository;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampDakokuRepository;
import nts.uk.ctx.at.record.dom.workrecord.workperfor.dailymonthlyprocessing.ErrMessageInfoRepository;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.dailyperformanceprocessing.ErrMessageResource;
import nts.uk.ctx.at.shared.dom.dailyperformanceprocessing.repository.RecreateFlag;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.algorithmdailyper.StampReflectRangeOutput;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.workinfomation.algorithmdailyper.TimeZoneOutput;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrMessageContent;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.ErrMessageInfo;
import nts.uk.ctx.at.shared.dom.workrecord.workperfor.dailymonthlyprocessing.enums.ExecutionContent;
import nts.uk.shr.com.i18n.TextResource;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class StampServiceImpl implements StampDomainService {

	@Inject
	private ErrMessageInfoRepository errRepo;
	@Inject
	private StampCardtemRepository stampCardRepo;
	
	@Inject
	private StampDakokuRepository stampDakokuRepository;
	
	@Inject
	private StampCardRepository stampCardRepository;

	//打刻を取得する
    public List<Stamp> handleData(StampReflectRangeOutput s,
            String empCalAndSumExecLogID, GeneralDate date, String employeeId, String companyId,RecreateFlag recreateFlag) {
		if (s.getLstStampReflectTimezone().isEmpty()) {
			ErrMessageInfo employmentErrMes = new ErrMessageInfo(employeeId, empCalAndSumExecLogID,
					new ErrMessageResource("009"), EnumAdaptor.valueOf(0, ExecutionContent.class), date,
					new ErrMessageContent(TextResource.localize("Msg_466")));
			errRepo.add(employmentErrMes);
			return null;
		}
		List<StampCardItem> lstStampCardItem = this.stampCardRepo.findByEmployeeID(employeeId);
		ArrayList<String> stampNumber = new ArrayList<String>();
		if (lstStampCardItem != null) {
			int stampCardSize = lstStampCardItem.size();
			if (stampCardSize > 0) {
				if (stampCardSize > 10) {
					stampCardSize = 10;
				}
				for (int i = 0; i < stampCardSize; i++) {
					stampNumber.add(lstStampCardItem.get(i).getCardNumber().v());
				}

			}
		}

		List<Stamp> lstStampOutput = new ArrayList<>();

		if (stampNumber != null && !stampNumber.isEmpty()) {

			List<Stamp> listStamp = stampDakokuRepository.getByListCard(stampNumber);
            if (recreateFlag != RecreateFlag.CREATE_DAILY) {
            	for(Stamp x :listStamp){
					int attendanceClock = x.getStampDateTime().clockHourMinute().v();
					TimeZoneOutput stampRange = s.getStampRange();
					if (x.getStampDateTime().year()==date.year()&& x.getStampDateTime().month() == date.month() && x.getStampDateTime().day() == date.day()
							&& attendanceClock >= stampRange.getStart().v().intValue()
							&& attendanceClock <= stampRange.getEnd().v().intValue()
							&& x.getImprintReflectionStatus().isReflectedCategory() == false) {//打刻．反映済み区分　=　false
						lstStampOutput.add(x);
					}
					lstStampOutput.addAll(findStempItemNext(listStamp, date.addDays(1), stampRange, x.getImprintReflectionStatus().isReflectedCategory()));
				}
            	listStamp = listStamp.stream().collect(Collectors.toSet()).stream().collect(Collectors.toList());
            	listStamp.sort(Comparator.comparing(Stamp::getStampDateTime));

				return lstStampOutput;
			}
             
			for(Stamp x : listStamp) {
				int attendanceClock = x.getStampDateTime().clockHourMinute().v();
				TimeZoneOutput stampRange = s.getStampRange();
				if (x.getStampDateTime().year()==date.year()&& x.getStampDateTime().month() == date.month() && x.getStampDateTime().day() == date.day()
						&& attendanceClock >= stampRange.getStart().v().intValue()
						&& attendanceClock <= stampRange.getEnd().v().intValue()) {
					lstStampOutput.add(x);
				}
				lstStampOutput.addAll(findStempItemNext(listStamp, date.addDays(1), stampRange, x.getImprintReflectionStatus().isReflectedCategory()));
			};
			lstStampOutput = lstStampOutput.stream().collect(Collectors.toSet()).stream().collect(Collectors.toList());
			lstStampOutput.sort(Comparator.comparing(Stamp::getStampDateTime));
		}
		return lstStampOutput;
	}

	private List<Stamp> findStempItemNext(List<Stamp> lstStamp, GeneralDate tomorow, TimeZoneOutput stampRange, boolean reflectedAtr){
		List<Stamp> lstStampResult = lstStamp.stream().filter(x -> x.getStampDateTime().year() == tomorow.year()
				&& x.getStampDateTime().month() == tomorow.month() && x.getStampDateTime().day() == tomorow.day() 
				&& x.getStampDateTime().clockHourMinute().v() >= 0
				&& x.getStampDateTime().clockHourMinute().v() <= stampRange.getStart().v().intValue()
				&& (reflectedAtr == true)).map(x ->{
					x.setAttendanceTime(new AttendanceTime(x.getStampDateTime().clockHourMinute().v() + 1440));
					return x;
				}).collect(Collectors.toList());
		
		return lstStampResult;
	}

	@Override
	public List<Stamp> handleDataNew(StampReflectRangeOutput s, GeneralDate date,
			String employeeId, String companyId, EmbossingExecutionFlag flag,CacheCarrier carrier) {
		List<Stamp> listStampHasReflected = carrier.get("listStamp");
		List<Stamp> listStamp = new ArrayList<>();
		// ドメインモデル「打刻カード」を取得する
		List<StampCard> lstStampCard = stampCardRepository.getListStampCard(employeeId);
		if(lstStampCard.isEmpty()) {
			return new ArrayList<>();
		}
		int timeStart = s.getStampRange().getStart() !=null?s.getStampRange().getStart().v():0;
		int timeEnd = s.getStampRange().getEnd()!=null?s.getStampRange().getEnd().v():0;
		GeneralDateTime start = GeneralDateTime.ymdhms(date.year(), date.month(), date.day(), 0, 0, 0)
				.addMinutes(timeStart);

		GeneralDateTime end = GeneralDateTime.ymdhms(date.year(), date.month(), date.day(), 0, 0, 0)
				.addMinutes(timeEnd);
		// ドメインモデル「打刻」を取得する (Lấy dữ liệu)
		if (flag == EmbossingExecutionFlag.ALL) {
			listStamp = stampDakokuRepository.getByDateTimeperiod(lstStampCard.stream().map(c->c.getStampNumber().v()).collect(Collectors.toList()), start, end);
		} else {
			listStamp = stampDakokuRepository.getByDateTimeperiod(lstStampCard.stream().map(c->c.getStampNumber().v()).collect(Collectors.toList()), start, end)
					.stream()
					.filter(c -> !c.getImprintReflectionStatus().isReflectedCategory()).collect(Collectors.toList());
		}
		listStamp.stream().sorted(Comparator.comparing(Stamp::getStampDateTime)).collect(Collectors.toList());
		List<Stamp> listRemove = new ArrayList<>();
		for(Stamp stamp :listStamp) {
			for(Stamp stampRf :listStampHasReflected) {
				if(stamp.getStampDateTime().equals(stampRf.getStampDateTime()) && 
						stampRf.getType().getChangeClockArt()== stamp.getType().getChangeClockArt()
						&& stampRf.getCardNumber().equals(stamp.getCardNumber())
						&& stampRf.getImprintReflectionStatus().isReflectedCategory()) {
					listRemove.add(stamp);
				}
			}
		}
		listStamp.removeAll(listRemove);
		return listStamp;
	}
}
