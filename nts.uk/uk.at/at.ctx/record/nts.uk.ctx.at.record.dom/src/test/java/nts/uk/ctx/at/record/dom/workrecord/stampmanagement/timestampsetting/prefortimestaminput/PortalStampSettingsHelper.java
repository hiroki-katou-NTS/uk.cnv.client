package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.shared.dom.common.color.ColorCode;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.shr.com.enumcommon.NotUseAtr;

public class PortalStampSettingsHelper {

	public static PortalStampSettings getDefault() {
		List<ButtonSettings> lstBS = new ArrayList<ButtonSettings>();
		
		StampType stampType = new StampType(
				true, 
				EnumAdaptor.valueOf(0, GoingOutReason.class), 
				EnumAdaptor.valueOf(0, SetPreClockArt.class), 
				EnumAdaptor.valueOf(1, ChangeClockAtr.class),
				EnumAdaptor.valueOf(0, ChangeCalArt.class));

		lstBS.add(new ButtonSettings(new ButtonPositionNo(1),
				NotUseAtr.NOT_USE,
				new ButtonDisSet(new ButtonNameSet(new ColorCode("DUMMY"), new ButtonName("DUMMY")),
						new ColorCode("DUMMY")),
				stampType,
				AudioType.GOOD_JOB,
				Optional.of(SupportWplSet.USE_THE_STAMPED_WORKPLACE),
				Optional.of(AssignmentMethod.SELECT_AT_THE_TIME_OF_STAMPING)));
		
		lstBS.add(new ButtonSettings(new ButtonPositionNo(2),
				NotUseAtr.NOT_USE,
				new ButtonDisSet(new ButtonNameSet(new ColorCode("DUMMY"), new ButtonName("DUMMY")),
						new ColorCode("DUMMY")),
				stampType,
				AudioType.GOOD_JOB,
				Optional.of(SupportWplSet.USE_THE_STAMPED_WORKPLACE),
				Optional.of(AssignmentMethod.SELECT_AT_THE_TIME_OF_STAMPING)));

		return new PortalStampSettings("000-0000000001",
				new DisplaySettingsStampScreen(new CorrectionInterval(1),
						new SettingDateTimeColorOfStampScreen(new ColorCode("DUMMY")),
						new ResultDisplayTime(1)),
				lstBS, true, true, true, true);
	}
	
	public static Optional<ButtonSettings> getOptButtonSettings(){
		StampType stampType = new StampType(
				true, 
				EnumAdaptor.valueOf(0, GoingOutReason.class), 
				EnumAdaptor.valueOf(0, SetPreClockArt.class), 
				EnumAdaptor.valueOf(1, ChangeClockAtr.class),
				EnumAdaptor.valueOf(0, ChangeCalArt.class));
		
		ButtonSettings buttonSettings = new ButtonSettings(new ButtonPositionNo(1),
				NotUseAtr.NOT_USE,
				new ButtonDisSet(new ButtonNameSet(new ColorCode("DUMMY"), new ButtonName("DUMMY")),
						new ColorCode("DUMMY")),
				stampType,
				AudioType.GOOD_JOB,
				Optional.of(SupportWplSet.USE_THE_STAMPED_WORKPLACE),
				Optional.of(AssignmentMethod.SELECT_AT_THE_TIME_OF_STAMPING));
		
		return Optional.of(buttonSettings);
	}
}
