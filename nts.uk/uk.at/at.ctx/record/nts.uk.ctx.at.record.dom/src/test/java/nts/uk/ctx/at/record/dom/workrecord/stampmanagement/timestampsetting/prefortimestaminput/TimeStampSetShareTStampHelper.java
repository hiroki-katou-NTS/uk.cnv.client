package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nts.uk.ctx.at.shared.dom.common.color.ColorCode;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * 
 * @author chungnt
 *
 */

public class TimeStampSetShareTStampHelper {

	public static StampSetCommunal getDefault() {
		
		List<StampPageLayout> lstStampPageLayout = new ArrayList<>();
		List<ButtonSettings> lstButtonSet = new ArrayList<>();
		
		lstButtonSet.add(new ButtonSettings(new ButtonPositionNo(1),
				new ButtonDisSet(new ButtonNameSet(new ColorCode("DUMMY"), new ButtonName("DUMMY")), new ColorCode("DUMMY")),
				new ButtonType(ReservationArt.RESERVATION, Optional.empty()),
				NotUseAtr.USE,
				AudioType.NONE,
				Optional.of(SupportWplSet.SELECT_AT_THE_TIME_OF_STAMPING)));
		
		lstButtonSet.add(new ButtonSettings(new ButtonPositionNo(3),
				new ButtonDisSet(new ButtonNameSet(new ColorCode("DUMMY"), new ButtonName("DUMMY")), new ColorCode("DUMMY")),
				new ButtonType(ReservationArt.RESERVATION, Optional.empty()),
				NotUseAtr.USE,
				AudioType.NONE,
				Optional.of(SupportWplSet.SELECT_AT_THE_TIME_OF_STAMPING)));
		
		lstButtonSet.add(new ButtonSettings(new ButtonPositionNo(4),
				new ButtonDisSet(new ButtonNameSet(new ColorCode("DUMMY"), new ButtonName("DUMMY")), new ColorCode("DUMMY")),
				new ButtonType(ReservationArt.RESERVATION, Optional.empty()),
				NotUseAtr.USE,
				AudioType.NONE,
				Optional.of(SupportWplSet.SELECT_AT_THE_TIME_OF_STAMPING)));
		
		
		lstStampPageLayout.add(new StampPageLayout(new PageNo(1),
				new StampPageName("DUMMY"),
				new StampPageComment(new PageComment("DUMMY"), new ColorCode("DUMMY")),
				ButtonLayoutType.LARGE_2_SMALL_4,
				lstButtonSet));
		
		DisplaySettingsStampScreen displaySetStampScreen = new DisplaySettingsStampScreen(new CorrectionInterval(1),
				new SettingDateTimeColorOfStampScreen(new ColorCode("DUMMY")),
				new ResultDisplayTime(1));
		
		StampSetCommunal setShareTStamp = new StampSetCommunal(
				"000-0000000001", 
				displaySetStampScreen, 
				lstStampPageLayout, 
				true, 
				false, 
				true, 
				Optional.empty());
		
		return setShareTStamp;
	}
	
public static StampSetCommunal get_list_empty() {
		
		List<StampPageLayout> lstStampPageLayout = new ArrayList<>();
		
		DisplaySettingsStampScreen displaySetStampScreen = new DisplaySettingsStampScreen(new CorrectionInterval(1),
				new SettingDateTimeColorOfStampScreen(new ColorCode("DUMMY")),
				new ResultDisplayTime(1));
		
		StampSetCommunal setShareTStamp = new StampSetCommunal(
				"000-0000000001", 
				displaySetStampScreen, 
				lstStampPageLayout, 
				true, 
				false, 
				true, 
				Optional.empty());
		
		return setShareTStamp;
	}
	
}
