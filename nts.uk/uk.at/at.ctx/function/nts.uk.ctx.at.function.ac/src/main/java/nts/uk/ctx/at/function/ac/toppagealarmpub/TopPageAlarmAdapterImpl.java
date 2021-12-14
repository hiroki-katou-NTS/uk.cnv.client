package nts.uk.ctx.at.function.ac.toppagealarmpub;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.function.dom.adapter.toppagealarmpub.DeleteInfoAlarmImport;
import nts.uk.ctx.at.function.dom.adapter.toppagealarmpub.TopPageAlarmImport;
import nts.uk.ctx.at.function.dom.adapter.toppagealarmpub.TopPageAlarmAdapter;
import nts.uk.ctx.sys.portal.pub.toppagealarm.AlarmClassification;
import nts.uk.ctx.sys.portal.pub.toppagealarm.DeleteInfoExport;
import nts.uk.ctx.sys.portal.pub.toppagealarm.DisplayEmpClassfication;
import nts.uk.ctx.sys.portal.pub.toppagealarm.ToppageAlarmExport;
import nts.uk.ctx.sys.portal.pub.toppagealarm.ToppageAlarmPub;

@Stateless
public class TopPageAlarmAdapterImpl implements TopPageAlarmAdapter {
	
	@Inject
	private ToppageAlarmPub toppageAlarmPub;
	
	@Override
	public void create(String companyId, List<TopPageAlarmImport> alarmInfos, Optional<DeleteInfoAlarmImport> delInfoOpt) {
		// set トップアラーム
		List<ToppageAlarmExport> topPageAlarmExports = alarmInfos.stream().map(x -> toTopPageAlarmExport(x)).collect(Collectors.toList());
		
		// set 削除の情報
		Optional<DeleteInfoExport> delInformationOpt = Optional.empty();
		if (delInfoOpt.isPresent()) {
			DeleteInfoAlarmImport delInfo = delInfoOpt.get();
			DeleteInfoExport delInformation = new DeleteInfoExport();
			delInformation.setAlarmClassification(EnumAdaptor.valueOf(delInfo.getAlarmClassification(), AlarmClassification.class));
			delInformation.setSids(delInfo.getSids());
			delInformation.setDisplayEmpClassfication(EnumAdaptor.valueOf(delInfo.getDisplayAtr(), DisplayEmpClassfication.class));
			delInformation.setPatternCode(delInfo.getPatternCode());
			delInformationOpt = Optional.of(delInformation);
		}
		
		// create
		toppageAlarmPub.create(companyId, topPageAlarmExports, delInformationOpt);
	}
	
	/**
	 * Convert TopPageAlarmImport to TopPageAlarmExport
	 * @param alarmInfo TopPageAlarmImport
	 * @return ToppageAlarmExport
	 */
	private ToppageAlarmExport toTopPageAlarmExport(TopPageAlarmImport alarmInfo) {
		ToppageAlarmExport alarmInformation = new ToppageAlarmExport();
		alarmInformation.setAlarmClassification(EnumAdaptor.valueOf(alarmInfo.getAlarmClassification(), AlarmClassification.class));
		alarmInformation.setOccurrenceDateTime(alarmInfo.getOccurrenceDateTime());
		alarmInformation.setDisplaySId(alarmInfo.getDisplaySId());
		alarmInformation.setDisplayEmpClassfication(EnumAdaptor.valueOf(alarmInfo.getDisplayAtr(), DisplayEmpClassfication.class));
		alarmInformation.setSubSids(alarmInfo.getSubEmployeeIds()); //#116503
		alarmInformation.setPatternCode(alarmInfo.getPatternCode());
		alarmInformation.setPatternName(alarmInfo.getPatternName());
		alarmInformation.setLinkUrl(alarmInfo.getLinkUrl());
		alarmInformation.setDisplayMessage(alarmInfo.getDisplayMessage());
		alarmInformation.setNoErrEmployeeIds(alarmInfo.getNoErrEmployeeIds());
		
		return alarmInformation;
	}

}
