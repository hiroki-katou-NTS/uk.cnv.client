package nts.uk.ctx.pr.file.app.core.socialinsurnoticreset;

import nts.arc.error.BusinessException;
import nts.arc.layer.app.file.export.ExportService;
import nts.arc.layer.app.file.export.ExportServiceContext;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.pr.core.dom.socialinsurance.socialinsuranceoffice.SocialInsuranceOffice;
import nts.uk.ctx.pr.core.dom.socialinsurance.socialinsuranceoffice.SocialInsuranceOfficeRepository;
import nts.uk.ctx.pr.core.dom.socialinsurance.socialinsuranceoffice.SocialInsurancePrefectureInformation;
import nts.uk.ctx.pr.core.dom.socialinsurance.socialinsuranceoffice.SocialInsurancePrefectureInformationRepository;
import nts.uk.ctx.pr.report.dom.printconfig.socinsurnoticreset.*;
import nts.uk.ctx.pr.shared.dom.socialinsurance.employeesociainsur.empbenepenpeninfor.EmpWelfarePenInsQualiInforRepository;
import nts.uk.ctx.pr.shared.dom.socialinsurance.employeesociainsur.emphealinsurbeneinfo.EmplHealInsurQualifiInforRepository;
import nts.uk.shr.com.context.AppContexts;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

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
	private SocialInsuranceOfficeRepository socialInsuranceOfficeRepository;

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
		SocialInsurNotiCreateSet domain = new SocialInsurNotiCreateSet(cid, userId,
				socialInsurNotiCreateSet.getOfficeInformation(),
				socialInsurNotiCreateSet.getBusinessArrSymbol(),
				socialInsurNotiCreateSet.getOutputOrder(),
				socialInsurNotiCreateSet.getPrintPersonNumber(),
				socialInsurNotiCreateSet.getSubmittedName(),
				socialInsurNotiCreateSet.getInsuredNumber(),
				socialInsurNotiCreateSet.getFdNumber(),
				socialInsurNotiCreateSet.getTextPersonNumber(),
				socialInsurNotiCreateSet.getLineFeedCode(),
				socialInsurNotiCreateSet.getOutputFormat()
		);
		socialInsNotifiCreateSetRegis(domain);
		if(end.before(start)) {
			throw new BusinessException("Msg_812");
		}
		if(domain.getInsuredNumber() == InsurPersonNumDivision.DO_NOT_OUPUT) {
			throw new BusinessException("MsgQ_174", "QSI013_21");
		}
		if(domain.getOfficeInformation() == BusinessDivision.OUTPUT_COMPANY_NAME) {
			throw new BusinessException("MsgQ_174", "QSI013_23");
		}
		if(!domain.getFdNumber().isPresent()) {
			throw new BusinessException("MsgQ_5", "QSI013_32");
		}
        boolean empWelfarePenInsQualiInfor = empWelfarePenInsQualiInforRepository.checkEmpWelfarePenInsQualiInfor(end, empIds);
        boolean emplHealInsurQualifiInfor= emplHealInsurQualifiInforRepository.checkEmplHealInsurQualifiInfor(end, empIds);
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
			List<InsLossDataExport> welfPenInsLoss = socialInsurNotiCreateSetEx.getWelfPenInsLoss(empIds, cid, start, end);
			List<SocialInsuranceOffice> socialInsuranceOffice =  socialInsuranceOfficeRepository.findByCid(cid);
			CompanyInfor company = socialInsurNotiCreateSetEx.getCompanyInfor(cid);
			notificationOfLossInsCSVFileGenerator.generate(exportServiceContext.getGeneratorContext(),
					new LossNotificationInformation(healthInsLoss, welfPenInsLoss, socialInsuranceOffice, socialInsurNotiCreateSet, exportServiceContext.getQuery().getReference(), company, infor));
        }

        /*if(domain.getOutputFormat().get() == OutputFormatClass.HEAL_INSUR_ASSO) {
			List<SocialInsurancePrefectureInformation> infor  = socialInsuranceInfor.findByHistory();
			List<SocialInsuranceOffice> socialInsuranceOffice =  socialInsuranceOfficeRepository.findByCid(cid);
			CompanyInfor company = socialInsurNotiCreateSetEx.getCompanyInfor(cid);
			List<InsLossDataExport> healthInsAssociationData = socialInsurNotiCreateSetEx.getHealthInsAssociation(empIds, cid, start, end);
			notificationOfLossInsCSVFileGenerator.generate(exportServiceContext.getGeneratorContext(),
					new LossNotificationInformation(null, null, socialInsuranceOffice, socialInsurNotiCreateSet, exportServiceContext.getQuery().getReference(), company, infor));
		}*/

		if(domain.getOutputFormat().get() == OutputFormatClass.THE_WELF_PEN) {
			List<SocialInsurancePrefectureInformation> infor  = socialInsuranceInfor.findByHistory();
			List<SocialInsuranceOffice> socialInsuranceOffice =  socialInsuranceOfficeRepository.findByCid(cid);
			CompanyInfor company = socialInsurNotiCreateSetEx.getCompanyInfor(cid);
			List<PensFundSubmissData> healthInsAssociationData = socialInsurNotiCreateSetEx.getHealthInsAssociation(empIds, cid, start, end);
			notificationOfLossInsCSVFileGenerator.generate(exportServiceContext.getGeneratorContext(),
					new LossNotificationInformation(null, null, socialInsuranceOffice, socialInsurNotiCreateSet, exportServiceContext.getQuery().getReference(), company, infor));
		}

    }

	//社会保険届作成設定登録処理
	private void socialInsNotifiCreateSetRegis(SocialInsurNotiCreateSet domain){
		socialInsurNotiCrSetRepository.update(domain);
	}

}
