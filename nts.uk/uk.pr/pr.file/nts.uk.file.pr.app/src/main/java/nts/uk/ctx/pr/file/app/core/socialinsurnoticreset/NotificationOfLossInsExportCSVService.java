package nts.uk.ctx.pr.file.app.core.socialinsurnoticreset;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.file.export.ExportService;
import nts.arc.layer.app.file.export.ExportServiceContext;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.pr.core.dom.socialinsurance.socialinsuranceoffice.SocialInsuranceOfficeRepository;
import nts.uk.ctx.pr.core.dom.socialinsurance.socialinsuranceoffice.SocialInsurancePrefectureInformation;
import nts.uk.ctx.pr.core.dom.socialinsurance.socialinsuranceoffice.SocialInsurancePrefectureInformationRepository;
import nts.uk.ctx.pr.report.dom.printconfig.socinsurnoticreset.*;
import nts.uk.ctx.pr.shared.dom.socialinsurance.employeesociainsur.empbenepenpeninfor.EmpWelfarePenInsQualiInforRepository;
import nts.uk.ctx.pr.shared.dom.socialinsurance.employeesociainsur.empcomworkstlinfor.CorWorkFormInfoRepository;
import nts.uk.ctx.pr.shared.dom.socialinsurance.employeesociainsur.emphealinsurbeneinfo.EmplHealInsurQualifiInforRepository;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.swing.text.html.Option;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
public class NotificationOfLossInsExportCSVService extends ExportService<NotificationOfLossInsExportQuery> {

	@Inject
	private SocialInsurNotiCrSetRepository socialInsurNotiCrSetRepository;

	@Inject
	private EmpWelfarePenInsQualiInforRepository empWelfarePenInsQualiInforRepository;

	@Inject
	private EmplHealInsurQualifiInforRepository emplHealInsurQualifiInforRepository;

	@Inject
	private NotificationOfLossInsCSVFileGenerator notificationOfLossInsCSVFileGenerator;

	@Inject
	private NotificationOfLossInsExRepository socialInsurNotiCreateSetEx;

	@Inject
	private SocialInsurancePrefectureInformationRepository socialInsuranceInfor;


	@Override
	protected void handle(ExportServiceContext<NotificationOfLossInsExportQuery> exportServiceContext) {
		String userId = AppContexts.user().userId();
		String cid = AppContexts.user().companyId();
		NotificationOfLossInsExport socialInsurNotiCreateSet = exportServiceContext.getQuery().getSocialInsurNotiCreateSet();
        List<String> empIds = exportServiceContext.getQuery().getEmpIds();
        GeneralDate start = exportServiceContext.getQuery().getStartDate();
        GeneralDate end = exportServiceContext.getQuery().getEndDate();
		SocialInsurNotiCreateSet domain = new SocialInsurNotiCreateSet(userId,cid,
				socialInsurNotiCreateSet.getOfficeInformation(),
				socialInsurNotiCreateSet.getBusinessArrSymbol(),
				socialInsurNotiCreateSet.getOutputOrder(),
				socialInsurNotiCreateSet.getPrintPersonNumber(),
				socialInsurNotiCreateSet.getSubmittedName(),
				socialInsurNotiCreateSet.getInsuredNumber(),
				socialInsurNotiCreateSet.getFdNumber(),
				socialInsurNotiCreateSet.getTextPersonNumber(),
                socialInsurNotiCreateSet.getOutputFormat(),
				socialInsurNotiCreateSet.getLineFeedCode()
		);
		socialInsNotifiCreateSetRegis(domain);
		if(end.before(start)) {
			throw new BusinessException("Msg_812");
		}
		if(domain.getInsuredNumber() == InsurPersonNumDivision.DO_NOT_OUPUT) {
			throw new BusinessException("MsgQ_174", "QSI013_A222_24");
		}
		if(domain.getOfficeInformation() == BusinessDivision.DO_NOT_OUTPUT || domain.getOfficeInformation() == BusinessDivision.DO_NOT_OUTPUT_BUSINESS) {
			throw new BusinessException("MsgQ_174", "QSI013_A222_30");
		}
		if(!domain.getFdNumber().isPresent()) {
			throw new BusinessException("MsgQ_5", "QSI013_A222_53");
		}
        boolean empWelfarePenInsQualiInfor = empWelfarePenInsQualiInforRepository.checkEmpWelfarePenInsQualiInforEnd(start, end, empIds);
        boolean emplHealInsurQualifiInfor= emplHealInsurQualifiInforRepository.checkEmplHealInsurQualifiInforEndDate(start, end, empIds);
		if(domain.getOutputFormat().get() == OutputFormatClass.PEN_OFFICE && empWelfarePenInsQualiInfor && emplHealInsurQualifiInfor) {
		    throw new BusinessException("Msg_37");
		}
		if(domain.getOutputFormat().get() == OutputFormatClass.HEAL_INSUR_ASSO && emplHealInsurQualifiInfor) {
		    throw new BusinessException("Msg_37");
        }
        if(domain.getOutputFormat().get() == OutputFormatClass.THE_WELF_PEN && empWelfarePenInsQualiInfor) {
            throw new BusinessException("Msg_37");
        }
        if(domain.getOutputFormat().get() == OutputFormatClass.PEN_OFFICE){
			List<SocialInsurancePrefectureInformation> infor  = socialInsuranceInfor.findByHistory();
			List<InsLossDataExport> healthInsLoss = socialInsurNotiCreateSetEx.getHealthInsLoss(empIds, cid, start, end);
			if(healthInsLoss.isEmpty()) {
				throw new BusinessException("Msg_37");
			}
			healthInsLoss.forEach( item -> {
						if (!item.getEndDate().isEmpty() && item.getEndDate().equals(item.getEndDate2())) {
							item.setCaInsurance2(null);
							item.setCause2(null);
							item.setNumRecoved2(null);
							item.setOther2(null);
							item.setOtherReason2(null);
						}
			});
			CompanyInfor company = socialInsurNotiCreateSetEx.getCompanyInfor(cid);
			healthInsLoss = this.order(domain.getOutputOrder(), healthInsLoss);
			notificationOfLossInsCSVFileGenerator.generate(exportServiceContext.getGeneratorContext(),
					new LossNotificationInformation(healthInsLoss, null,null, domain, exportServiceContext.getQuery().getReference(), company, infor));
        }

        if(domain.getOutputFormat().get() == OutputFormatClass.HEAL_INSUR_ASSO) {
			List<SocialInsurancePrefectureInformation> infor  = socialInsuranceInfor.findByHistory();
			CompanyInfor company = socialInsurNotiCreateSetEx.getCompanyInfor(cid);
			List<InsLossDataExport> healthInsAssociationData = socialInsurNotiCreateSetEx.getHealthInsLoss(empIds, cid, start, end);
			if(healthInsAssociationData.isEmpty()) {
				throw new BusinessException("Msg_37");
			}
			healthInsAssociationData = this.order(domain.getOutputOrder(), healthInsAssociationData);
			notificationOfLossInsCSVFileGenerator.generate(exportServiceContext.getGeneratorContext(),
					new LossNotificationInformation(healthInsAssociationData, null, null, domain, exportServiceContext.getQuery().getReference(), company, infor));
		}

		if(domain.getOutputFormat().get() == OutputFormatClass.THE_WELF_PEN) {
			List<SocialInsurancePrefectureInformation> infor  = socialInsuranceInfor.findByHistory();
			CompanyInfor company = socialInsurNotiCreateSetEx.getCompanyInfor(cid);
			List<PensFundSubmissData> healthInsAssociationData = socialInsurNotiCreateSetEx.getHealthInsAssociation(empIds, cid, start, end);
			if(healthInsAssociationData.isEmpty()) {
				throw new BusinessException("Msg_37");
			}
            if(domain.getOutputOrder() == SocialInsurOutOrder.EMPLOYEE_KANA_ORDER) {
                healthInsAssociationData = healthInsAssociationData.stream().sorted(Comparator.comparing(PensFundSubmissData::getPersonNameKana)).collect(Collectors.toList());
            }
            if(domain.getOutputOrder() == SocialInsurOutOrder.HEAL_INSUR_NUMBER_ORDER) {
                healthInsAssociationData = healthInsAssociationData.stream().sorted(Comparator.comparing(PensFundSubmissData::getHealInsNumber)).collect(Collectors.toList());
            }
            if(domain.getOutputOrder() == SocialInsurOutOrder.WELF_AREPEN_NUMBER_ORDER) {
                healthInsAssociationData = healthInsAssociationData.stream().sorted(Comparator.comparing(PensFundSubmissData::getWelNumber)).collect(Collectors.toList());
            }
            if(domain.getOutputOrder() == SocialInsurOutOrder.HEAL_INSUR_NUMBER_UNION_ORDER) {
                healthInsAssociationData = healthInsAssociationData.stream().sorted(Comparator.comparing(PensFundSubmissData::getHealInsUnionNumber)).collect(Collectors.toList());
            }
            if(domain.getOutputOrder() == SocialInsurOutOrder.ORDER_BY_FUND) {
                healthInsAssociationData = healthInsAssociationData.stream().sorted(Comparator.comparing(PensFundSubmissData::getMemberNumber)).collect(Collectors.toList());
            }
			notificationOfLossInsCSVFileGenerator.generate(exportServiceContext.getGeneratorContext(),
					new LossNotificationInformation(null, null, healthInsAssociationData,domain, exportServiceContext.getQuery().getReference(), company, infor));
		}

    }

	//社会保険届作成設定登録処理
	private void socialInsNotifiCreateSetRegis(SocialInsurNotiCreateSet domain){
		socialInsurNotiCrSetRepository.update(domain);
	}

	private List<InsLossDataExport> order(SocialInsurOutOrder order, List<InsLossDataExport> healthInsLoss){
		if(order == SocialInsurOutOrder.EMPLOYEE_KANA_ORDER) {
			return healthInsLoss.stream().sorted(Comparator.comparing(InsLossDataExport::getOfficeCd).thenComparing(InsLossDataExport::getPersonNameKana).thenComparing(InsLossDataExport::getEmpCd)).collect(Collectors.toList());
		}
		if(order == SocialInsurOutOrder.HEAL_INSUR_NUMBER_ORDER) {
			return healthInsLoss.stream().sorted(Comparator.comparing(InsLossDataExport::getOfficeCd).thenComparing(InsLossDataExport::getHealInsNumber)).collect(Collectors.toList());
		}
		if(order == SocialInsurOutOrder.WELF_AREPEN_NUMBER_ORDER) {
			return healthInsLoss.stream().sorted(Comparator.comparing(InsLossDataExport::getOfficeCd).thenComparing(InsLossDataExport::getWelfPenNumber)).collect(Collectors.toList());
		}
		if(order == SocialInsurOutOrder.HEAL_INSUR_NUMBER_UNION_ORDER) {
			return healthInsLoss.stream().sorted(Comparator.comparing(InsLossDataExport::getOfficeCd).thenComparing(InsLossDataExport::getHealInsUnionNumber)).collect(Collectors.toList());
		}
		if(order == SocialInsurOutOrder.ORDER_BY_FUND) {
			return healthInsLoss.stream().sorted(Comparator.comparing(InsLossDataExport::getOfficeCd).thenComparing(InsLossDataExport::getMemberNumber)).collect(Collectors.toList());
		}
		if(order == SocialInsurOutOrder.INSURED_PER_NUMBER_ORDER) {
			return healthInsLoss.stream().sorted(Comparator.comparing(InsLossDataExport::getOfficeCd).thenComparing(InsLossDataExport::getInsPerCls)).collect(Collectors.toList());
		}
		return healthInsLoss;
	}

}
