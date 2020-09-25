package nts.uk.ctx.at.schedule.dom.schedule.alarm.bansamedayholiday;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.val;
import mockit.integration.junit4.JMockit;
import nts.arc.testing.assertion.NtsAssert;
import nts.gul.text.IdentifierUtil;
import nts.uk.ctx.at.schedule.dom.shift.basicworkregister.ClassificationCode;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrgIdenInfor;
import nts.uk.ctx.at.shared.dom.workrule.organizationmanagement.workplace.TargetOrganizationUnit;

@RunWith(JMockit.class)
public class BanHolidayTogetherTest {
	
	@Test
	public void getters() {
		BanHolidayTogether banHdTogether = BanHolidayTogether.create(
				TargetOrgIdenInfor.creatIdentifiWorkplace("DUMMY"),
				new BanHolidayTogetherCode("0001"), 
				new BanHolidayTogetherName("禁止グループA"),
				Optional.empty(),
				new MinNumberEmployeeTogether(1),
				BanHolidayTogetherHelper.creatEmpsCanNotSameHolidays(2)
				);
		
		NtsAssert.invokeGetters(banHdTogether);
	}
	

	/**
	 * inv1: 同日の休日取得を禁止する社員.size() > 1	
	 * ケース: 同日の休日取得を禁止する社員がemptyです -> Msg_1885
	 */
	@Test
	public void check_inv1_emptyList() {		
		NtsAssert.businessException("Msg_1885", ()-> {
			BanHolidayTogether.create(
					TargetOrgIdenInfor.creatIdentifiWorkplace("DUMMY"),
					new BanHolidayTogetherCode("0001"), 
					new BanHolidayTogetherName("会社の禁止グループ"),
					BanHolidayTogetherHelper.creatCalendarReferenceCompany(),
					new MinNumberEmployeeTogether(3),
					Collections.emptyList()
					);
		});
	}
	
	/**
	 * inv1: 同日の休日取得を禁止する社員.size() > 1	
	 * ケース: 同日の休日取得を禁止する社員のsize() = 1 -> Msg_1885
	 */
	@Test
	public void check_inv1_sizeEquals1() {
		NtsAssert.businessException("Msg_1885", ()-> {
			BanHolidayTogether.create(
					TargetOrgIdenInfor.creatIdentifiWorkplace("DUMMY"),
					new BanHolidayTogetherCode("0001"), 
					new BanHolidayTogetherName("会社の禁止グループ"),
					BanHolidayTogetherHelper.creatCalendarReferenceCompany(),
					new MinNumberEmployeeTogether(3),
					BanHolidayTogetherHelper.creatEmpsCanNotSameHolidays(1)
					);
		});
	}
	
	/**
	 * inv2: 最低限出勤すべき人数 <= 同日の休日取得を禁止する社員.size()
	 * ケース1: 同日の休日取得を禁止する社員.size() == 最低限出勤すべき人数 -> create success
	 * ケース2: 同日の休日取得を禁止する社員.size() < 最低限出勤すべき人数  ->Msg_1886
	*/
	@Test
	public void check_inv2_empsCanNotSameHolidaysBeforeMinNumberOfEmployeeToWork() {
        /** ケース: 同日の休日取得を禁止する社員.size() == 最低限出勤すべき人数  */
		BanHolidayTogether.create(
				TargetOrgIdenInfor.creatIdentifiWorkplace("DUMMY"),
				new BanHolidayTogetherCode("0001"), 
				new BanHolidayTogetherName("会社の禁止グループ"),
				BanHolidayTogetherHelper.creatCalendarReferenceCompany(),
				new MinNumberEmployeeTogether(5),
				BanHolidayTogetherHelper.creatEmpsCanNotSameHolidays(5)
				);
		
		 /** ケース: 同日の休日取得を禁止する社員.size() < 最低限出勤すべき人数  */
		NtsAssert.businessException("Msg_1886", ()-> {
			BanHolidayTogether.create(
					TargetOrgIdenInfor.creatIdentifiWorkplace("DUMMY"),
					new BanHolidayTogetherCode("0001"), 
					new BanHolidayTogetherName("会社の禁止グループ"),
					BanHolidayTogetherHelper.creatCalendarReferenceCompany(),
					new MinNumberEmployeeTogether(6),
					BanHolidayTogetherHelper.creatEmpsCanNotSameHolidays(5)
					);
		});
		
	}
	

	/**
	 * 作成する 組織の単位 = 職場, 稼働日の参照先 = empty
	 * 
	 */
	@Test
	public void create_banSameDayHolidayNotReference_success() {
		val empsCanNotSameHolidays = BanHolidayTogetherHelper.creatEmpsCanNotSameHolidays(2);
		val banHdComEmpty = BanHolidayTogether.create(
				TargetOrgIdenInfor.creatIdentifiWorkplace("DUMMY"),
				new BanHolidayTogetherCode("001"),
				new BanHolidayTogetherName("禁止グループ会社A"), 
				Optional.empty(),
				new MinNumberEmployeeTogether(1),
				empsCanNotSameHolidays);
		
		assertThat(banHdComEmpty.getTargetOrg().getUnit()).isEqualTo(TargetOrganizationUnit.WORKPLACE);
		assertThat(banHdComEmpty.getBanHolidayTogetherCode().v()).isEqualTo("001");
		assertThat(banHdComEmpty.getBanHolidayTogetherName().v()).isEqualTo("禁止グループ会社A");
		assertThat(banHdComEmpty.getWorkDayReference()).isEmpty();
		assertThat(banHdComEmpty.getMinOfWorkingEmpTogether().v()).isEqualTo(1);
		assertThat(banHdComEmpty.getEmpsCanNotSameHolidays()).containsExactlyInAnyOrderElementsOf(empsCanNotSameHolidays);
	}
	
	/**
	 * 作成する 稼働日の参照先 = 会社を参照する
	 * 
	 */
	@Test
	public void create_banSameDayHolidayCompany_success() {
		val banHdCom = BanHolidayTogether.create(
				TargetOrgIdenInfor.creatIdentifiWorkplace("DUMMY"),
				new BanHolidayTogetherCode("0001"),
				new BanHolidayTogetherName("禁止グループ会社A"),
				BanHolidayTogetherHelper.creatCalendarReferenceCompany(), 
				new MinNumberEmployeeTogether(1),
				BanHolidayTogetherHelper.creatEmpsCanNotSameHolidays(2));
		
		assertThat(banHdCom.getWorkDayReference()).isNotEmpty();
		assertThat(banHdCom.getWorkDayReference().get().getBusinessDaysCalendarType()).isEqualTo(BusinessDaysCalendarType.COMPANY);
	}
	
	/**
	 * 作成する 稼働日の参照先 = 職場カレンダーを参照する
	 * 
	 */
	@Test
	public void create_banSameDayHolidayWorkplace_success() {
		val banHdWorkplace = BanHolidayTogether.create(
				TargetOrgIdenInfor.creatIdentifiWorkplace("DUMMY"),
				new BanHolidayTogetherCode("0001"),
				new BanHolidayTogetherName("禁止グループ職場カレンダー"),
				Optional.ofNullable(new ReferenceCalendarWorkplace(IdentifierUtil.randomUniqueId())),
				new MinNumberEmployeeTogether(1),
				BanHolidayTogetherHelper.creatEmpsCanNotSameHolidays(2));

		assertThat(banHdWorkplace.getWorkDayReference()).isNotEmpty();
		assertThat(banHdWorkplace.getWorkDayReference().get().getBusinessDaysCalendarType()).isEqualTo(BusinessDaysCalendarType.WORKPLACE);

	}
	
	/**
	 * 作成する 稼働日の参照先 = 分類カレンダーを参照する
	 * 
	 */
	@Test
	public void create_banHolidayTogetherClassification_success() {
		val banHolidayClass = BanHolidayTogether.create(
				TargetOrgIdenInfor.creatIdentifiWorkplace("DUMMY"),
				new BanHolidayTogetherCode("0001"), 
				new BanHolidayTogetherName("禁止グループ分類カレンダー"),
				Optional.of(new ReferenceCalendarClass(new ClassificationCode("0009"))),
				new MinNumberEmployeeTogether(1), 
				BanHolidayTogetherHelper.creatEmpsCanNotSameHolidays(2));
		
		assertThat(banHolidayClass.getWorkDayReference()).isNotEmpty();
		assertThat(banHolidayClass.getWorkDayReference().get().getBusinessDaysCalendarType()).isEqualTo(BusinessDaysCalendarType.CLASSSICATION);

	}
	

}
