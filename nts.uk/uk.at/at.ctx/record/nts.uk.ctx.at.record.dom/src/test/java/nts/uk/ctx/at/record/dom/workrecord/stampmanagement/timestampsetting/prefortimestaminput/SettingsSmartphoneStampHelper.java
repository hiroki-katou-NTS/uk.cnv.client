package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.settingforsmartphone.SettingsSmartphoneStamp;
import nts.uk.ctx.at.shared.dom.common.color.ColorCode;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * 
 * @author chungnt
 *
 */

public class SettingsSmartphoneStampHelper {

	public static SettingsSmartphoneStamp getSettingsSmartphoneStampDefault() {

		return new SettingsSmartphoneStamp("000-0000000001", new DisplaySettingsStampScreen(new CorrectionInterval(1), // dummy
				new SettingDateTimeColorOfStampScreen(new ColorCode("DUMMY")),
				new ResultDisplayTime(1)), // dummy
				new ArrayList<StampPageLayout>(), true, NotUseAtr.NOT_USE, NotUseAtr.NOT_USE);
	}

	public static SettingsSmartphoneStamp getSettingsSmartphoneStamp() {
		List<StampPageLayout> lstSPL = new ArrayList<>();
		List<ButtonSettings> lstBS = new ArrayList<>();
		StampType stampType = new StampType(
				true, 
				EnumAdaptor.valueOf(0, GoingOutReason.class), 
				EnumAdaptor.valueOf(0, SetPreClockArt.class), 
				EnumAdaptor.valueOf(1, ChangeClockAtr.class),
				EnumAdaptor.valueOf(0, ChangeCalArt.class));
		
		lstBS.add(new ButtonSettings(new ButtonPositionNo(1),
				NotUseAtr.NOT_USE,
				new ButtonDisSet(new ButtonNameSet(new ColorCode("DUMMY"), new ButtonName("DUMMY")), new ColorCode("DUMMY")),
				stampType,
				AudioType.GOOD_JOB,
				Optional.of(SupportWplSet.SELECT_AT_THE_TIME_OF_STAMPING),
				Optional.of(AssignmentMethod.SELECT_AT_THE_TIME_OF_STAMPING)));
		
		lstSPL.add(new StampPageLayout(new PageNo(1),
				new StampPageName("DUMMY"),
				new StampPageComment(new PageComment("DUMMY"), new ColorCode("DUMMY")),
				ButtonLayoutType.LARGE_2_SMALL_4,
				lstBS));
		
		return new SettingsSmartphoneStamp("000-0000000001", new DisplaySettingsStampScreen(new CorrectionInterval(1), // dummy
				new SettingDateTimeColorOfStampScreen(new ColorCode("DUMMY")),
				new ResultDisplayTime(1)), // dummy
				lstSPL, true, NotUseAtr.NOT_USE, NotUseAtr.NOT_USE);
	}
	
	public static Optional<ButtonSettings> getOPTButtonSeting(){
		StampType stampType = new StampType(
				true, 
				EnumAdaptor.valueOf(0, GoingOutReason.class), 
				EnumAdaptor.valueOf(0, SetPreClockArt.class), 
				EnumAdaptor.valueOf(1, ChangeClockAtr.class),
				EnumAdaptor.valueOf(0, ChangeCalArt.class));
		
		ButtonSettings bSettings = new ButtonSettings(new ButtonPositionNo(1),
				NotUseAtr.NOT_USE,
				new ButtonDisSet(new ButtonNameSet(new ColorCode("DUMMY"), new ButtonName("DUMMY")), new ColorCode("DUMMY")),
				stampType,
				AudioType.GOOD_JOB,
				Optional.of(SupportWplSet.SELECT_AT_THE_TIME_OF_STAMPING),
				Optional.of(AssignmentMethod.SELECT_AT_THE_TIME_OF_STAMPING));
		
		return Optional.ofNullable(bSettings);
	}

}
