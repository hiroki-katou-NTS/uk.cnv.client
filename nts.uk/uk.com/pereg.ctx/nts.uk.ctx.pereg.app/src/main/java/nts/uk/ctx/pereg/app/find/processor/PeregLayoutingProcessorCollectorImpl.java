package nts.uk.ctx.pereg.app.find.processor;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.util.TypeLiteral;

import find.person.contact.PersonContactDto;
import find.person.info.PersonDto;
import nts.uk.ctx.at.record.app.find.dailyperformanceformat.businesstype.BusinessTypeDto;
import nts.uk.ctx.at.record.app.find.stamp.card.stampcard.PeregStampCardDto;
import nts.uk.ctx.at.shared.app.find.dailyattdcal.empunitpricehistory.EmployeeUnitPriceDto;
import nts.uk.ctx.at.shared.app.find.remainingnumber.annualleave.AnnualLeaveDto;
import nts.uk.ctx.at.shared.app.find.remainingnumber.empinfo.basicinfo.Specialleave10informationDto;
import nts.uk.ctx.at.shared.app.find.remainingnumber.empinfo.basicinfo.Specialleave11informationDto;
import nts.uk.ctx.at.shared.app.find.remainingnumber.empinfo.basicinfo.Specialleave12informationDto;
import nts.uk.ctx.at.shared.app.find.remainingnumber.empinfo.basicinfo.Specialleave13informationDto;
import nts.uk.ctx.at.shared.app.find.remainingnumber.empinfo.basicinfo.Specialleave14informationDto;
import nts.uk.ctx.at.shared.app.find.remainingnumber.empinfo.basicinfo.Specialleave15informationDto;
import nts.uk.ctx.at.shared.app.find.remainingnumber.empinfo.basicinfo.Specialleave16informationDto;
import nts.uk.ctx.at.shared.app.find.remainingnumber.empinfo.basicinfo.Specialleave17informationDto;
import nts.uk.ctx.at.shared.app.find.remainingnumber.empinfo.basicinfo.Specialleave18informationDto;
import nts.uk.ctx.at.shared.app.find.remainingnumber.empinfo.basicinfo.Specialleave19informationDto;
import nts.uk.ctx.at.shared.app.find.remainingnumber.empinfo.basicinfo.Specialleave1InformationDto;
import nts.uk.ctx.at.shared.app.find.remainingnumber.empinfo.basicinfo.Specialleave20informationDto;
import nts.uk.ctx.at.shared.app.find.remainingnumber.empinfo.basicinfo.Specialleave2informationDto;
import nts.uk.ctx.at.shared.app.find.remainingnumber.empinfo.basicinfo.Specialleave3informationDto;
import nts.uk.ctx.at.shared.app.find.remainingnumber.empinfo.basicinfo.Specialleave4informationDto;
import nts.uk.ctx.at.shared.app.find.remainingnumber.empinfo.basicinfo.Specialleave5informationDto;
import nts.uk.ctx.at.shared.app.find.remainingnumber.empinfo.basicinfo.Specialleave6informationDto;
import nts.uk.ctx.at.shared.app.find.remainingnumber.empinfo.basicinfo.Specialleave7informationDto;
import nts.uk.ctx.at.shared.app.find.remainingnumber.empinfo.basicinfo.Specialleave8informationDto;
import nts.uk.ctx.at.shared.app.find.remainingnumber.empinfo.basicinfo.Specialleave9informationDto;
import nts.uk.ctx.at.shared.app.find.remainingnumber.nursingcareleave.CareLeaveInfoDto;
import nts.uk.ctx.at.shared.app.find.remainingnumber.otherhdinfo.OtherHolidayInfoDto;
import nts.uk.ctx.at.shared.app.find.remainingnumber.specialleavegrant.finder.SpecialLeaveGrantDto1;
import nts.uk.ctx.at.shared.app.find.remainingnumber.specialleavegrant.finder.SpecialLeaveGrantDto10;
import nts.uk.ctx.at.shared.app.find.remainingnumber.specialleavegrant.finder.SpecialLeaveGrantDto11;
import nts.uk.ctx.at.shared.app.find.remainingnumber.specialleavegrant.finder.SpecialLeaveGrantDto12;
import nts.uk.ctx.at.shared.app.find.remainingnumber.specialleavegrant.finder.SpecialLeaveGrantDto13;
import nts.uk.ctx.at.shared.app.find.remainingnumber.specialleavegrant.finder.SpecialLeaveGrantDto14;
import nts.uk.ctx.at.shared.app.find.remainingnumber.specialleavegrant.finder.SpecialLeaveGrantDto15;
import nts.uk.ctx.at.shared.app.find.remainingnumber.specialleavegrant.finder.SpecialLeaveGrantDto16;
import nts.uk.ctx.at.shared.app.find.remainingnumber.specialleavegrant.finder.SpecialLeaveGrantDto17;
import nts.uk.ctx.at.shared.app.find.remainingnumber.specialleavegrant.finder.SpecialLeaveGrantDto18;
import nts.uk.ctx.at.shared.app.find.remainingnumber.specialleavegrant.finder.SpecialLeaveGrantDto19;
import nts.uk.ctx.at.shared.app.find.remainingnumber.specialleavegrant.finder.SpecialLeaveGrantDto2;
import nts.uk.ctx.at.shared.app.find.remainingnumber.specialleavegrant.finder.SpecialLeaveGrantDto20;
import nts.uk.ctx.at.shared.app.find.remainingnumber.specialleavegrant.finder.SpecialLeaveGrantDto3;
import nts.uk.ctx.at.shared.app.find.remainingnumber.specialleavegrant.finder.SpecialLeaveGrantDto4;
import nts.uk.ctx.at.shared.app.find.remainingnumber.specialleavegrant.finder.SpecialLeaveGrantDto5;
import nts.uk.ctx.at.shared.app.find.remainingnumber.specialleavegrant.finder.SpecialLeaveGrantDto6;
import nts.uk.ctx.at.shared.app.find.remainingnumber.specialleavegrant.finder.SpecialLeaveGrantDto7;
import nts.uk.ctx.at.shared.app.find.remainingnumber.specialleavegrant.finder.SpecialLeaveGrantDto8;
import nts.uk.ctx.at.shared.app.find.remainingnumber.specialleavegrant.finder.SpecialLeaveGrantDto9;
import nts.uk.ctx.at.shared.app.find.shortworktime.ShortWorkTimeDto;
import nts.uk.ctx.at.shared.app.find.workingcondition.WorkingCondition2Dto;
import nts.uk.ctx.at.shared.app.find.workingcondition.WorkingConditionDto;
import nts.uk.ctx.bs.employee.app.find.classification.affiliate.AffClassificationDto;
import nts.uk.ctx.bs.employee.app.find.department.affiliate.AffDeptHistDto;
import nts.uk.ctx.bs.employee.app.find.employee.contact.EmpInfoContactDto;
import nts.uk.ctx.bs.employee.app.find.employee.history.AffCompanyHistInfoDto;
import nts.uk.ctx.bs.employee.app.find.employee.mngdata.EmployeeDataMngInfoDto;
import nts.uk.ctx.bs.employee.app.find.jobtitle.affiliate.AffJobTitleDto;
import nts.uk.ctx.bs.employee.app.find.temporaryabsence.TempAbsHisItemDto;
import nts.uk.ctx.bs.employee.app.find.workplace.affiliate.AffWorlplaceHistItemDto;
import nts.uk.ctx.pereg.app.find.employment.history.EmploymentHistoryDto;
import nts.uk.ctx.pr.shared.app.find.socialinsurance.employeesociainsur.empcomofficehis.EmpCorpHealthOffHisDto;
import nts.uk.ctx.pr.shared.app.find.socialinsurance.employeesociainsur.emphealinsurbeneinfo.EmpHealInsQualifiInfoDto;
import nts.uk.ctx.pr.shared.app.find.socialinsurance.employeesociainsur.empsocialinsgradehis.EmpSocialInsGradeInforDto;
import nts.uk.ctx.sys.gateway.app.find.login.password.userpassword.PeregLoginPasswordDto;
import nts.uk.shr.pereg.app.find.PeregFinder;

@Stateless
@SuppressWarnings("serial")
public class PeregLayoutingProcessorCollectorImpl implements PeregFinderProcessorCollector {


	/** ctg single finder */
	private static final List<TypeLiteral<?>> FINDER_CTG_SINGLE_HANDLER_CLASSES = Arrays.asList(
			// CS00001  社員データ管理
			new TypeLiteral<PeregFinder<EmployeeDataMngInfoDto>>() {
			},
			// CS00002 個人基本情報
			new TypeLiteral<PeregFinder<PersonDto>>() {
			},
			// CS00003 所属会社履歴
			new TypeLiteral<PeregFinder<AffCompanyHistInfoDto>>() {
			},
			// CS00004 分類１
			new TypeLiteral<PeregFinder<AffClassificationDto>>() {
			},
			// CS00014 雇用
			new TypeLiteral<PeregFinder<EmploymentHistoryDto>>() {
			},
			// CS00015 部門本務	
			new TypeLiteral<PeregFinder<AffDeptHistDto>>() {
			},
			// CS00016 職位本務
			new TypeLiteral<PeregFinder<AffJobTitleDto>>() {
			},
			// CS00017 職場
			new TypeLiteral<PeregFinder<AffWorlplaceHistItemDto>>() {
			},
			// CS00018 休職休業
			new TypeLiteral<PeregFinder<TempAbsHisItemDto>>() {
			},
			// CS00019 短時間勤務
			new TypeLiteral<PeregFinder<ShortWorkTimeDto>>() {
			},
			// CS00020 労働条件
			new TypeLiteral<PeregFinder<WorkingConditionDto>>() {
			},
			// CS00021 勤務種別
			new TypeLiteral<PeregFinder<BusinessTypeDto>>() {
			},
			// CS00022 個人連絡先
			new TypeLiteral<PeregFinder<PersonContactDto>>() {
			},
			// CS00023 社員連絡先
			new TypeLiteral<PeregFinder<EmpInfoContactDto>>() {
			},
			// CS00024 年休情報
			new TypeLiteral<PeregFinder<AnnualLeaveDto>>() {
			},
			// CS00025 特別休暇１情報
			new TypeLiteral<PeregFinder<Specialleave1InformationDto>>() {
			},
			// CS00026 特別休暇２情報
			new TypeLiteral<PeregFinder<Specialleave2informationDto>>() {
			},
			// CS00027 特別休暇３情報
			new TypeLiteral<PeregFinder<Specialleave3informationDto>>() {
			},
			// CS00028 特別休暇４情報
			new TypeLiteral<PeregFinder<Specialleave4informationDto>>() {
			},
			// CS00029 特別休暇５情報
			new TypeLiteral<PeregFinder<Specialleave5informationDto>>() {
			},
			// CS00030 特別休暇６情報
			new TypeLiteral<PeregFinder<Specialleave6informationDto>>() {
			},
			// CS00031 特別休暇７情報
			new TypeLiteral<PeregFinder<Specialleave7informationDto>>() {
			},
			// CS00032 特別休暇８情報
			new TypeLiteral<PeregFinder<Specialleave8informationDto>>() {
			},
			// CS00033 特別休暇９情報
			new TypeLiteral<PeregFinder<Specialleave9informationDto>>() {
			},
			// CS00034 特別休暇１０情報
			new TypeLiteral<PeregFinder<Specialleave10informationDto>>() {
			},
			// CS00035 その他休暇情報
			new TypeLiteral<PeregFinder<OtherHolidayInfoDto>>() {
			},
			// CS00036 子の看護・介護休暇管理
			new TypeLiteral<PeregFinder<CareLeaveInfoDto>>() {
			},
			// CS00049 特別休暇１１情報
			new TypeLiteral<PeregFinder<Specialleave11informationDto>>() {
			},
			// CS00050 特別休暇１２情報
			new TypeLiteral<PeregFinder<Specialleave12informationDto>>() {
			},
			// CS00051 特別休暇１３情報
			new TypeLiteral<PeregFinder<Specialleave13informationDto>>() {
			},
			// CS00052 特別休暇１４情報
			new TypeLiteral<PeregFinder<Specialleave14informationDto>>() {
			},
			// CS00053 特別休暇１５情報
			new TypeLiteral<PeregFinder<Specialleave15informationDto>>() {
			},
			// CS00054 特別休暇１６情報
			new TypeLiteral<PeregFinder<Specialleave16informationDto>>() {
			},
			// CS00055 特別休暇１７情報
			new TypeLiteral<PeregFinder<Specialleave17informationDto>>() {
			},
			// CS00056 特別休暇１８情報
			new TypeLiteral<PeregFinder<Specialleave18informationDto>>() {
			},
			// CS00057 特別休暇１９情報
			new TypeLiteral<PeregFinder<Specialleave19informationDto>>() {
			},
			// CS00058 特別休暇２０情報
			new TypeLiteral<PeregFinder<Specialleave20informationDto>>() {
			},
			// CS00039  特別休暇１付与残数
			new TypeLiteral<PeregFinder<SpecialLeaveGrantDto1>>() {
			},
			// CS00040  特別休暇2付与残数
			new TypeLiteral<PeregFinder<SpecialLeaveGrantDto2>>() {
			},
			// CS00041  特別休暇3付与残数
			new TypeLiteral<PeregFinder<SpecialLeaveGrantDto3>>() {
			},
			// CS00042  特別休暇4付与残数
			new TypeLiteral<PeregFinder<SpecialLeaveGrantDto4>>() {
			},
			// CS00043  特別休暇5付与残数
			new TypeLiteral<PeregFinder<SpecialLeaveGrantDto5>>() {
			},
			// CS00044  特別休暇6付与残数
			new TypeLiteral<PeregFinder<SpecialLeaveGrantDto6>>() {
			},
			// CS00045  特別休暇7付与残数
			new TypeLiteral<PeregFinder<SpecialLeaveGrantDto7>>() {
			},
			// CS00046  特別休暇8付与残数
			new TypeLiteral<PeregFinder<SpecialLeaveGrantDto8>>() {
			},
			// CS00047  特別休暇9付与残数
			new TypeLiteral<PeregFinder<SpecialLeaveGrantDto9>>() {
			},
			// CS00048  特別休暇１0付与残数
			new TypeLiteral<PeregFinder<SpecialLeaveGrantDto10>>() {
			},
			// CS00059 特別休暇１１付与残数
			new TypeLiteral<PeregFinder<SpecialLeaveGrantDto11>>() {
			},
			// CS00060  特別休暇12付与残数
			new TypeLiteral<PeregFinder<SpecialLeaveGrantDto12>>() {
			},
			// CS00061  特別休暇13付与残数
			new TypeLiteral<PeregFinder<SpecialLeaveGrantDto13>>() {
			},
			// CS00062  特別休暇14付与残数
			new TypeLiteral<PeregFinder<SpecialLeaveGrantDto14>>() {
			},
			// CS00063  特別休暇15付与残数
			new TypeLiteral<PeregFinder<SpecialLeaveGrantDto15>>() {
			},
			// CS00064  特別休暇16付与残数
			new TypeLiteral<PeregFinder<SpecialLeaveGrantDto16>>() {
			},
			// CS00065  特別休暇17付与残数
			new TypeLiteral<PeregFinder<SpecialLeaveGrantDto17>>() {
			},
			// CS00066  特別休暇18付与残数
			new TypeLiteral<PeregFinder<SpecialLeaveGrantDto18>>() {
			},
			// CS00067  特別休暇19付与残数
			new TypeLiteral<PeregFinder<SpecialLeaveGrantDto19>>() {
			},
			// CS00068  特別休暇20付与残数
			new TypeLiteral<PeregFinder<SpecialLeaveGrantDto20>>() {
			},
			// CS00069 打刻カード番号
			new TypeLiteral<PeregFinder<PeregStampCardDto>>() {
			},
			// CS00070  労働条件２
			new TypeLiteral<PeregFinder<WorkingCondition2Dto>>() {
			},
			//CS00075
			new TypeLiteral<PeregFinder<EmpCorpHealthOffHisDto>>() {
			},
			// CS00082
			new TypeLiteral<PeregFinder<EmpHealInsQualifiInfoDto>>() {
			},
			// CS00092
			new TypeLiteral<PeregFinder<EmpSocialInsGradeInforDto>>() {
			},
			// CS00100 パスワード
			new TypeLiteral<PeregFinder<PeregLoginPasswordDto>>() {
			},
			// CS00097 単価
			new TypeLiteral<PeregFinder<EmployeeUnitPriceDto>>() {
			}
	);

	@Override
	public Set<PeregFinder<?>> peregFinderCollect() {
		return FINDER_CTG_SINGLE_HANDLER_CLASSES.stream().map(type -> CDI.current().select(type).get())
				.map(obj -> (PeregFinder<?>) obj).collect(Collectors.toSet());
	}

}
