package nts.uk.screen.at.app.query.kdp.kdp003.a;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.time.calendar.period.DatePeriod;
import nts.uk.ctx.sys.portal.app.query.notice.MessageNoticeDto;
import nts.uk.ctx.sys.portal.dom.notice.DestinationClassification;
import nts.uk.ctx.sys.portal.dom.notice.MessageNotice;
import nts.uk.ctx.sys.portal.dom.notice.MessageNoticeRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.UniversalK.就業.KDP_打刻.KDP003_打刻入力(氏名選択).A:打刻入力(氏名選択).メニュー別OCD.打刻入力(共有)でお知らせメッセージを取得する
 * 
 * @author tutt
 *
 */
@Stateless
public class GetStampInputSetting {

	@Inject
	private MessageNoticeRepository msgNoticeRepo;

	/**
	 * 
	 * @param period 期間
	 * @param wkpIds <List>職場ID
	 * @return Map<お知らせメッセージ>(List)
	 */
	public GetStampInputSettingDto get(DatePeriod period, List<String> wkpIds) {
		List<MessageNoticeDto> messageNoticeDtos = new ArrayList<>();
		String cid = AppContexts.user().companyId();

		// 1. [※ノートに記述]: 職場IDListからメッセージを取得する(期間、<List>職場ID):お知らせメッセージ
		List<MessageNotice> messageNotices = msgNoticeRepo.getMsgFromWpIdList(period, wkpIds, cid);
		
		messageNotices.stream().filter(f -> f.getTargetInformation().getDestination().value != 0).collect(Collectors.toList());
		
		messageNotices.addAll(msgNoticeRepo.getMsgInDestinationCategoryAndCid(period, DestinationClassification.ALL, cid));

		if (!messageNotices.isEmpty()) {
			messageNoticeDtos = messageNotices.stream().map(m -> MessageNoticeDto.toDto(m))
					.collect(Collectors.toList());
		}

		return new GetStampInputSettingDto(messageNoticeDtos);
	}

}
