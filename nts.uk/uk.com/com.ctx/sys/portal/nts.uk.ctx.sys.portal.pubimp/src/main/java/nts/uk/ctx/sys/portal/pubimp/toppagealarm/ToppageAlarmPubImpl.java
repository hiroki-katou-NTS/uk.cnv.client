package nts.uk.ctx.sys.portal.pubimp.toppagealarm;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.sys.portal.dom.standardmenu.StandardMenuRepository;
import nts.uk.ctx.sys.portal.dom.toppagealarm.AlarmClassification;
import nts.uk.ctx.sys.portal.dom.toppagealarm.AlarmListPatternCode;
import nts.uk.ctx.sys.portal.dom.toppagealarm.DisplayAtr;
import nts.uk.ctx.sys.portal.dom.toppagealarm.DisplayMessage;
import nts.uk.ctx.sys.portal.dom.toppagealarm.LinkURL;
import nts.uk.ctx.sys.portal.dom.toppagealarm.ToppageAlarmDataRepository;
import nts.uk.ctx.sys.portal.dom.toppagealarm.service.createalarmdata.CreateAlarmDataDs;
import nts.uk.ctx.sys.portal.dom.toppagealarm.service.createalarmdata.DeleleteInfo;
import nts.uk.ctx.sys.portal.dom.toppagealarm.service.registeralarmdata.RegisterAlarmDataDefaultRequireImpl;
import nts.uk.ctx.sys.portal.dom.toppagealarm.service.registeralarmdata.ToppageAlarmParam;
import nts.uk.ctx.sys.portal.dom.toppagealarm.service.updatealarmdata.UpdateAlarmDataDefaultRequireImpl;
import nts.uk.ctx.sys.portal.dom.toppagealarm.service.updateautorunalarm.UpdateAutoRunAlarmDefaultRequireImpl;
import nts.uk.ctx.sys.portal.pub.toppagealarm.DeleteInfoExport;
import nts.uk.ctx.sys.portal.pub.toppagealarm.ToppageAlarmExport;
import nts.uk.ctx.sys.portal.pub.toppagealarm.ToppageAlarmPub;

@Stateless
public class ToppageAlarmPubImpl implements ToppageAlarmPub {
	
	@Inject
	private ToppageAlarmDataRepository toppageAlarmDataRepository;

	@Inject
	private StandardMenuRepository standardMenuRepository;
	
	@Override
	public void create(String cid, List<ToppageAlarmExport> alarmInfos, Optional<DeleteInfoExport> delInfor) {
		
		UpdateAutoRunAlarmDefaultRequireImpl rq1 = new UpdateAutoRunAlarmDefaultRequireImpl(toppageAlarmDataRepository);
		
		UpdateAlarmDataDefaultRequireImpl rq2 = new UpdateAlarmDataDefaultRequireImpl(toppageAlarmDataRepository);
		
		RegisterAlarmDataDefaultRequireImpl rq3 = new RegisterAlarmDataDefaultRequireImpl(toppageAlarmDataRepository, standardMenuRepository);
		
		List<ToppageAlarmParam> alarmInfoList = alarmInfos.stream()
				.map(mapper -> ToppageAlarmParam.builder()
						.alarmClassification(EnumAdaptor.valueOf(mapper.getAlarmClassification().value, AlarmClassification.class))
						.occurrenceDateTime(mapper.getOccurrenceDateTime())
						.displaySId(mapper.getDisplaySId())
						.displayEmpClassfication(EnumAdaptor.valueOf(mapper.getDisplayEmpClassfication().value, DisplayAtr.class))
						.subSids(mapper.getSubSids()) //#116503
						.noErrSids(mapper.getNoErrEmployeeIds())
						.patternCode(Optional.ofNullable(mapper.getPatternCode().map(i -> new AlarmListPatternCode(i)).orElse(null)))
						.linkUrl(Optional.ofNullable(mapper.getLinkUrl().map(i -> new LinkURL(i)).orElse(null)))
						.displayMessage(Optional.ofNullable(mapper.getDisplayMessage().map(i -> new DisplayMessage(i)).orElse(null)))
						.patternName(Optional.ofNullable(mapper.getPatternName().orElse(null)))
						.build())
				.collect(Collectors.toList());
		
		Optional<DeleleteInfo> delInfo = Optional.ofNullable(delInfor
				.map(mapper -> DeleleteInfo.builder()
						.alarmClassification(EnumAdaptor.valueOf(mapper.getAlarmClassification().value, AlarmClassification.class))
						.sids(mapper.getSids())
						.displayEmpClassfication(EnumAdaptor.valueOf(mapper.getDisplayEmpClassfication().value, DisplayAtr.class))
						.alarmListParttenCode(Optional.ofNullable(mapper.getPatternCode().orElse(null)))
						.build())
				.orElse(null));
		
		CreateAlarmDataDs.create(rq1, rq2, rq3, cid, alarmInfoList, delInfo);
	}
}
