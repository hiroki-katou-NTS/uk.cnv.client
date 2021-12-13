package nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.receive;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.receive.StampReceptionData.StampDataBuilder;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.AuthcMethod;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeCalArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.ChangeClockAtr;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.SetPreClockArt;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.timestampsetting.prefortimestaminput.StampType;
import nts.uk.ctx.at.shared.dom.common.WorkplaceId;
import nts.uk.ctx.at.shared.dom.scherec.dailyattdcal.dailyattendance.common.timestamp.WorkLocationCD;
import nts.uk.ctx.at.shared.dom.workrule.goingout.GoingOutReason;
import nts.uk.shr.com.enumcommon.NotUseAtr;
import nts.uk.shr.com.net.Ipv4Address;

/**
 * @author ThanhNX
 *
 *         打刻受信データTest
 */
public class StampReceptionDataTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void getOverTimeHours() {
		StampReceptionData dataNR = new StampDataBuilder("1", "A", "1", "O", "200303", "01").time("0101")
				.overTimeHours("").midnightTime("1201").build();

		assertEquals("", dataNR.getOverTimeHours());
	}

	@Test
	public void getMidnightTime() {
		StampReceptionData dataNR = new StampDataBuilder("1", "A", "1", "O", "200303", "01").time("0101")
				.overTimeHours("1101").midnightTime(" ").build();

		assertEquals("", dataNR.getMidnightTime());
	}

	@Test
	public void convertAuthcMethod() {
		StampReceptionData dataNR = new StampDataBuilder("1", "B", "1", "O", "200303", "01").time("0101")
				.overTimeHours("1101").midnightTime(" ").build();
		assertEquals(AuthcMethod.EXTERNAL_AUTHC, dataNR.convertAuthcMethod());

		StampReceptionData dataNRC = new StampDataBuilder("1", "C", "1", "O", "200303", "01").time("0101")
				.overTimeHours("1101").midnightTime(" ").build();
		assertEquals(AuthcMethod.IC_CARD_AUTHC, dataNRC.convertAuthcMethod());

		StampReceptionData dataNRD = new StampDataBuilder("1", "D", "1", "O", "200303", "01").time("0101")
				.overTimeHours("1101").midnightTime(" ").build();
		assertEquals(AuthcMethod.VEIN_AUTHC, dataNRD.convertAuthcMethod());

	}

	@Test
	public void convertChangeCalArt() {

		StampReceptionData dataNRA = new StampDataBuilder("1", "B", "1", "D", "200303", "01").time("0101")
				.overTimeHours("1101").midnightTime(" ").build();
		assertEquals(ChangeCalArt.FIX, dataNRA.convertChangeCalArt());

		dataNRA = new StampDataBuilder("1", "B", "1", "L", "200303", "01").time("0101").overTimeHours("1101")
				.midnightTime(" ").build();
		assertEquals(ChangeCalArt.FIX, dataNRA.convertChangeCalArt());

		dataNRA = new StampDataBuilder("1", "B", "1", "5", "200303", "01").time("0101").overTimeHours("1101")
				.midnightTime(" ").build();
		assertEquals(ChangeCalArt.FIX, dataNRA.convertChangeCalArt());

		dataNRA = new StampDataBuilder("1", "B", "1", "J", "200303", "01").time("0101").overTimeHours("1101")
				.midnightTime(" ").build();
		assertEquals(ChangeCalArt.OVER_TIME, dataNRA.convertChangeCalArt());

		dataNRA = new StampDataBuilder("1", "B", "1", "S", "200303", "01").time("0101").overTimeHours("1101")
				.midnightTime(" ").build();
		assertEquals(ChangeCalArt.EARLY_APPEARANCE, dataNRA.convertChangeCalArt());

		dataNRA = new StampDataBuilder("1", "B", "1", "8", "200303", "01").time("0101").overTimeHours("1101")
				.midnightTime(" ").build();
		assertEquals(ChangeCalArt.EARLY_APPEARANCE, dataNRA.convertChangeCalArt());

		dataNRA = new StampDataBuilder("1", "B", "1", "U", "200303", "01").time("0101").overTimeHours("1101")
				.midnightTime(" ").build();
		assertEquals(ChangeCalArt.BRARK, dataNRA.convertChangeCalArt());

		dataNRA = new StampDataBuilder("1", "B", "1", "6", "200303", "01").time("0101").overTimeHours("1101")
				.midnightTime(" ").build();
		assertEquals(ChangeCalArt.BRARK, dataNRA.convertChangeCalArt());

	}

	@Test
	public void convertChangeClockArt() {

//		EmpInfoTerminal empInfoTerminal = createEmpInfoTer(NotUseAtr.NOT_USE);
//		StampReceptionData dataNRA = new StampDataBuilder("1", "B", "1", "A", "200303", "01").time("0101")
//				.overTimeHours("1101").midnightTime(" ").build();
//		assertEquals(ChangeClockArt.GOING_TO_WORK, dataNRA.convertChangeClockArt(empInfoTerminal));
//
//		dataNRA = new StampDataBuilder("1", "B", "1", "B", "200303", "01").time("0101").overTimeHours("1101")
//				.midnightTime(" ").build();
//		assertEquals(ChangeClockArt.GOING_TO_WORK, dataNRA.convertChangeClockArt(empInfoTerminal));
//
//		empInfoTerminal = createEmpInfoTer(NotUseAtr.USE);
//		dataNRA = new StampDataBuilder("1", "B", "1", "D", "200303", "01").time("0101").overTimeHours("1101")
//				.midnightTime(" ").build();
//		assertEquals(ChangeClockArt.OVER_TIME, dataNRA.convertChangeClockArt(empInfoTerminal));
//
//		dataNRA = new StampDataBuilder("1", "B", "1", "S", "200303", "01").time("0101").overTimeHours("1101")
//				.midnightTime(" ").build();
//		assertEquals(ChangeClockArt.GOING_TO_WORK, dataNRA.convertChangeClockArt(empInfoTerminal));
//
//		dataNRA = new StampDataBuilder("1", "B", "1", "U", "200303", "01").time("0101").overTimeHours("1101")
//				.midnightTime(" ").build();
//		assertEquals(ChangeClockArt.GOING_TO_WORK, dataNRA.convertChangeClockArt(empInfoTerminal));
//
//		dataNRA = new StampDataBuilder("1", "B", "1", "G", "200303", "01").time("0101").overTimeHours("1101")
//				.midnightTime(" ").build();
//		assertEquals(ChangeClockArt.OVER_TIME, dataNRA.convertChangeClockArt(empInfoTerminal));
//
//		dataNRA = new StampDataBuilder("1", "B", "1", "H", "200303", "01").time("0101").overTimeHours("1101")
//				.midnightTime(" ").build();
//		assertEquals(ChangeClockArt.OVER_TIME, dataNRA.convertChangeClockArt(empInfoTerminal));
//
//		dataNRA = new StampDataBuilder("1", "B", "1", "J", "200303", "01").time("0101").overTimeHours("1101")
//				.midnightTime(" ").build();
//		assertEquals(ChangeClockArt.OVER_TIME, dataNRA.convertChangeClockArt(empInfoTerminal));
//
//		empInfoTerminal = createEmpInfoTer(NotUseAtr.NOT_USE);
//		dataNRA = new StampDataBuilder("1", "B", "1", "L", "200303", "01").time("0101").overTimeHours("1101")
//				.midnightTime(" ").build();
//		assertEquals(ChangeClockArt.WORKING_OUT, dataNRA.convertChangeClockArt(empInfoTerminal));
//
//		empInfoTerminal = createEmpInfoTer(NotUseAtr.USE);
//		dataNRA = new StampDataBuilder("1", "B", "1", "O", "200303", "01").time("0101").overTimeHours("1101")
//				.midnightTime(" ").build();
//		assertEquals(ChangeClockArt.START_OF_SUPPORT, dataNRA.convertChangeClockArt(empInfoTerminal));
//
//		empInfoTerminal = createEmpInfoTer(NotUseAtr.NOT_USE);
//		dataNRA = new StampDataBuilder("1", "B", "1", "O", "200303", "01").time("0101").overTimeHours("1101")
//				.midnightTime(" ").build();
//		assertEquals(ChangeClockArt.GO_OUT, dataNRA.convertChangeClockArt(empInfoTerminal));
//
//		empInfoTerminal = createEmpInfoTer(NotUseAtr.USE);
//		dataNRA = new StampDataBuilder("1", "B", "1", "Q", "200303", "01").time("0101").overTimeHours("1101")
//				.midnightTime(" ").build();
//		assertEquals(ChangeClockArt.END_OF_SUPPORT, dataNRA.convertChangeClockArt(empInfoTerminal));
//
//		empInfoTerminal = createEmpInfoTer(NotUseAtr.NOT_USE);
//		dataNRA = new StampDataBuilder("1", "B", "1", "Q", "200303", "01").time("0101").overTimeHours("1101")
//				.midnightTime(" ").build();
//		assertEquals(ChangeClockArt.RETURN, dataNRA.convertChangeClockArt(empInfoTerminal));
//
//		dataNRA = new StampDataBuilder("1", "B", "1", "0", "200303", "01").time("0101").overTimeHours("1101")
//				.midnightTime(" ").build();
//		assertEquals(ChangeClockArt.TEMPORARY_WORK, dataNRA.convertChangeClockArt(empInfoTerminal));
//
//		dataNRA = new StampDataBuilder("1", "B", "1", "1", "200303", "01").time("0101").overTimeHours("1101")
//				.midnightTime(" ").build();
//		assertEquals(ChangeClockArt.START_OF_SUPPORT, dataNRA.convertChangeClockArt(empInfoTerminal));
//
//		dataNRA = new StampDataBuilder("1", "B", "1", "2", "200303", "01").time("0101").overTimeHours("1101")
//				.midnightTime(" ").build();
//		assertEquals(ChangeClockArt.END_OF_SUPPORT, dataNRA.convertChangeClockArt(empInfoTerminal));
//
//		dataNRA = new StampDataBuilder("1", "B", "1", "3", "200303", "01").time("0101").overTimeHours("1101")
//				.midnightTime(" ").build();
//		assertEquals(ChangeClockArt.SUPPORT, dataNRA.convertChangeClockArt(empInfoTerminal));
//
//		dataNRA = new StampDataBuilder("1", "B", "1", "4", "200303", "01").time("0101").overTimeHours("1101")
//				.midnightTime(" ").build();
//		assertEquals(ChangeClockArt.SUPPORT, dataNRA.convertChangeClockArt(empInfoTerminal));
//
//		dataNRA = new StampDataBuilder("1", "B", "1", "5", "200303", "01").time("0101").overTimeHours("1101")
//				.midnightTime(" ").build();
//		assertEquals(ChangeClockArt.SUPPORT, dataNRA.convertChangeClockArt(empInfoTerminal));
//
//		dataNRA = new StampDataBuilder("1", "B", "1", "6", "200303", "01").time("0101").overTimeHours("1101")
//				.midnightTime(" ").build();
//		assertEquals(ChangeClockArt.START_OF_SUPPORT, dataNRA.convertChangeClockArt(empInfoTerminal));
//
//		dataNRA = new StampDataBuilder("1", "B", "1", "7", "200303", "01").time("0101").overTimeHours("1101")
//				.midnightTime(" ").build();
//		assertEquals(ChangeClockArt.TEMPORARY_SUPPORT_WORK, dataNRA.convertChangeClockArt(empInfoTerminal));
//
//		dataNRA = new StampDataBuilder("1", "B", "1", "8", "200303", "01").time("0101").overTimeHours("1101")
//				.midnightTime(" ").build();
//		assertEquals(ChangeClockArt.START_OF_SUPPORT, dataNRA.convertChangeClockArt(empInfoTerminal));
//
//		dataNRA = new StampDataBuilder("1", "B", "1", "9", "200303", "01").time("0101").overTimeHours("1101")
//				.midnightTime(" ").build();
//		assertEquals(ChangeClockArt.TEMPORARY_LEAVING, dataNRA.convertChangeClockArt(empInfoTerminal));


	}

	@Test
	public void createStampType() {

	}

}
