package nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import nts.arc.enums.EnumAdaptor;
import nts.arc.task.tran.AtomTask;
import nts.arc.testing.assertion.NtsAssert;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.AutoCreateStampCardNumberService;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCardCreateResult;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampMeans;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.domainservice.EnterStampFromPersonalStampService.Require;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.AssignmentMethod;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.AudioType;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonDisSet;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonLayoutType;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonName;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonNameSet;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonPositionNo;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ButtonSettings;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeCalArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockAtr;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.CorrectionInterval;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.DisplaySettingsStampScreen;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.PageComment;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.PageNo;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ResultDisplayTime;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.SetPreClockArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.SettingDateTimeColorOfStampScreen;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampButton;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampPageComment;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampPageLayout;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampPageName;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampSettingPerson;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampType;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.SupportWplSet;
import nts.uk.ctx.at.shared.dom.common.color.ColorCode;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * 
 * @author tutk
 *
 */
@RunWith(JMockit.class)
public class EnterStampFromPersonalStampServiceTest {
	
	@Injectable
	private Require require;

	/**
	 * require.getStampSet(); isPresent()
	 * result : BusinessException("Msg_1632");
	 */
	@Test
	public void test_stampSettingPerOpt_null() {
		String contractCode = "DUMMY";
		String employeeId = "DUMMY";
		GeneralDateTime dateTime = GeneralDateTime.now();
		StampButton stampButton = new StampButton(new PageNo(1), new ButtonPositionNo(1));
		
		new Expectations() {
			{
				require.getStampSet();
			}
		};
		
		NtsAssert.businessException("Msg_1632", () -> EnterStampFromPersonalStampService.create(require, "", contractCode, employeeId, dateTime, stampButton,
				null));
	}
	
	/**
	 * $ボタン詳細設定 = $個人利用の打刻設定.ボタン詳細設定を取得する(ページNO, ボタン位置NO)
	 * buttonSet : isPresent()
	 * result : BusinessException("Msg_1632");
	 */
	@Test
	public void test_buttonSet_null() {
		String contractCode = "DUMMY";
		String employeeId = "DUMMY";
		GeneralDateTime dateTime = GeneralDateTime.now();
		StampButton stampButton = new StampButton(new PageNo(1), new ButtonPositionNo(1));
		List<StampPageLayout> stampPageLayouts = new ArrayList<>();
		
		StampSettingPerson stampSettingPerson = new StampSettingPerson("DUMMY", 
				true, 
				new DisplaySettingsStampScreen(new CorrectionInterval(1),
						new SettingDateTimeColorOfStampScreen(new ColorCode("DUMMY")),
						new ResultDisplayTime(1)), 
				stampPageLayouts, 
				null);
		
		new Expectations() {
			{
				require.getStampSet();
				result = Optional.of(stampSettingPerson);
			}
		};
		
		NtsAssert.businessException("Msg_1632", () -> EnterStampFromPersonalStampService.create(require, "", contractCode, employeeId, dateTime, stampButton,
				null));
	}
	
	/**
	 * return CreateStampDataForEmployeesService.create(require, new
	 * ContractCode(contractCode), employeeId, null, stmapDateTime, relieve,
	 * buttonSet.get().getButtonType(), refActualResults, null);
	 */
	@Test
	public void test() {
		String contractCode = "DUMMY";
		String employeeId = "DUMMY";
		GeneralDateTime dateTime = GeneralDateTime.now();
		StampButton stampButton = new StampButton(new PageNo(1), new ButtonPositionNo(1));
		List<StampPageLayout> stampPageLayouts = new ArrayList<>();
		List<ButtonSettings> lstButtonSet = new ArrayList<>();
		StampType stampType = new StampType(
				true, 
				EnumAdaptor.valueOf(0, GoingOutReason.class), 
				EnumAdaptor.valueOf(0, SetPreClockArt.class),
				EnumAdaptor.valueOf(1, ChangeClockAtr.class),
				EnumAdaptor.valueOf(0, ChangeCalArt.class));

		lstButtonSet.add(new ButtonSettings(new ButtonPositionNo(1),
				NotUseAtr.USE,
				new ButtonDisSet(new ButtonNameSet(new ColorCode("DUMMY"), new ButtonName("DUMMY")), new ColorCode("DUMMY")),
				stampType,
				AudioType.NONE,
				Optional.of(SupportWplSet.USE_THE_STAMPED_WORKPLACE),
				Optional.of(AssignmentMethod.SELECT_AT_THE_TIME_OF_STAMPING)));
		
		lstButtonSet.add(new ButtonSettings(new ButtonPositionNo(1),
				NotUseAtr.USE,
				new ButtonDisSet(new ButtonNameSet(new ColorCode("DUMMY"), new ButtonName("DUMMY")), new ColorCode("DUMMY")),
				stampType,
				AudioType.NONE,
				Optional.of(SupportWplSet.USE_THE_STAMPED_WORKPLACE),
				Optional.of(AssignmentMethod.SELECT_AT_THE_TIME_OF_STAMPING)));
		
		lstButtonSet.add(new ButtonSettings(new ButtonPositionNo(1),
				NotUseAtr.USE,
				new ButtonDisSet(new ButtonNameSet(new ColorCode("DUMMY"), new ButtonName("DUMMY")), new ColorCode("DUMMY")),
				stampType,
				AudioType.NONE,
				Optional.of(SupportWplSet.USE_THE_STAMPED_WORKPLACE),
				Optional.of(AssignmentMethod.SELECT_AT_THE_TIME_OF_STAMPING)));
		
		stampPageLayouts.add(new StampPageLayout(new PageNo(1), new StampPageName("DUMMY"),
				new StampPageComment(new PageComment("DUMMY"), new ColorCode("DUMMY")), ButtonLayoutType.SMALL_8,
				lstButtonSet));
		
		StampSettingPerson stampSettingPerson = new StampSettingPerson("DUMMY", 
				true, 
				new DisplaySettingsStampScreen(new CorrectionInterval(1),
						new SettingDateTimeColorOfStampScreen(new ColorCode("DUMMY")),
						new ResultDisplayTime(1)), 
				stampPageLayouts, 
				null);
		
//		new Expectations() {
//			{
//				require.getStampSet();
//				result = Optional.of(stampSettingPerson);
//			}
//		};
		
		AtomTask atomTask = AtomTask.of(() -> {});// dummy
		StampCardCreateResult stampCardCreateResult = new StampCardCreateResult("1", Optional.of(atomTask));
		
//		new MockUp<AutoCreateStampCardNumberService>() {
//			@Mock
//			public Optional<StampCardCreateResult> create(AutoCreateStampCardNumberService.Require require,
//					String employeeID, StampMeans stampMeanss) {
//				return Optional.of(stampCardCreateResult);
//			}
//		};
		
//		TimeStampInputResult timeStampInputResult = EnterStampFromPersonalStampService.create(require, "", contractCode, employeeId, dateTime, stampButton,
//				null);
//		
//		assertThat(timeStampInputResult.at).isNotEmpty();
//		assertThat(timeStampInputResult.stampDataReflectResult.getAtomTask()).isNotNull();
	}
	
}

