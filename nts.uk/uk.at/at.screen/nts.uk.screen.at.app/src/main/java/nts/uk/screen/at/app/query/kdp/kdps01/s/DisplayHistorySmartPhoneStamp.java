package nts.uk.screen.at.app.query.kdp.kdps01.s;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import nts.arc.time.GeneralDate;
import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCard;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCardRepository;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.Stamp;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampDakokuRepository;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.EmployeeStampInfo;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.GetListStampEmployeeService;
import nts.uk.screen.at.app.query.kdp.kdp001.a.EmployeeStampInfoDto;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author sonnlb
 * 
 *         UKDesign.UniversalK.就業.KDP_打刻.KDPS01_打刻入力(スマホ).S:打刻履歴(打刻一覧).メニュー別OCD.打刻入力(スマホ)の打刻履歴一覧を表示する
 *         打刻入力(スマホ)の打刻履歴一覧を表示する
 */
@Stateless
public class DisplayHistorySmartPhoneStamp {

	@Inject
	private StampCardRepository stampCardRepo;

	@Inject
	private StampDakokuRepository stampDakokuRepo;

	/**
	 * 【input】
	 * 
	 * 期間
	 * 
	 * 【output】
	 * 
	 * ドメイン：社員の打刻情報
	 */
	public List<EmployeeStampInfoDto> displayHistorySmartPhoneStampList(DatePeriod period) {

		List<EmployeeStampInfo> empDatas = new ArrayList<>();
		EmpStampDataRequiredImpl empStampDataR = new EmpStampDataRequiredImpl(stampCardRepo, stampDakokuRepo);
		List<GeneralDate> betweens = period.datesBetween();
		betweens.sort((d1, d2) -> d2.compareTo(d1));
		for (GeneralDate date : betweens) {

			Optional<EmployeeStampInfo> employeeStampData = GetListStampEmployeeService.get(empStampDataR,
					AppContexts.user().employeeId(), date);
			if (employeeStampData.isPresent()) {
				empDatas.add(employeeStampData.get());
			}
		}

		return empDatas.stream().map(x -> EmployeeStampInfoDto.fromDomain(x)).collect(Collectors.toList());
	}

	@AllArgsConstructor
	private class EmpStampDataRequiredImpl implements GetListStampEmployeeService.Require {

		@Inject
		protected StampCardRepository stampCardRepo;

		@Inject
		protected StampDakokuRepository stampDakokuRepo;

		@Override
		public List<StampCard> getListStampCard(String sid) {
			return stampCardRepo.getListStampCard(sid);
		}

		@Override
		public List<Stamp> getStamp(List<StampNumber> stampNumbers, GeneralDate date) {
			return stampDakokuRepo.get(AppContexts.user().contractCode(), stampNumbers, date);
		}

	}
}
