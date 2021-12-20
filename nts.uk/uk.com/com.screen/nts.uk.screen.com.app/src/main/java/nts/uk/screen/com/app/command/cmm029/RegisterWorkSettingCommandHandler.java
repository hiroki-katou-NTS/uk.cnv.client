package nts.uk.screen.com.app.command.cmm029;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import nts.arc.enums.EnumAdaptor;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.shared.dom.common.CompanyId;
import nts.uk.ctx.at.shared.dom.entranceexit.ManageEntryExit;
import nts.uk.ctx.at.shared.dom.entranceexit.ManageEntryExitGetMemento;
import nts.uk.ctx.at.shared.dom.entranceexit.ManageEntryExitRepository;
import nts.uk.ctx.at.shared.dom.personallaborcondition.UseAtr;
import nts.uk.ctx.at.shared.dom.workmanagementmultiple.UseATR;
import nts.uk.ctx.at.shared.dom.workmanagementmultiple.WorkManagementMultiple;
import nts.uk.ctx.at.shared.dom.workmanagementmultiple.WorkManagementMultipleRepository;
import nts.uk.ctx.at.shared.dom.workrule.deformed.AggDeformedLaborSetting;
import nts.uk.ctx.at.shared.dom.workrule.deformed.AggDeformedLaborSettingRepository;
import nts.uk.ctx.at.shared.dom.workrule.workform.FlexWorkMntSetRepository;
import nts.uk.ctx.at.shared.dom.workrule.workform.FlexWorkSet;
import nts.uk.ctx.at.shared.dom.workrule.workuse.TemporaryWorkUseManage;
import nts.uk.ctx.at.shared.dom.workrule.workuse.TemporaryWorkUseManageRepository;
import nts.uk.screen.com.app.find.cmm029.DisplayDataDto;
import nts.uk.shr.com.context.AppContexts;
import nts.uk.shr.com.enumcommon.NotUseAtr;

/**
 * UKDesign.UniversalK.共通.CMM_マスタメンテナンス.CMM029_機能の選択.A : 機能の選択.メニュー別OCD.勤務の設定機能を登録する.勤務の設定機能を登録する
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class RegisterWorkSettingCommandHandler extends CommandHandler<RegisterFunctionSettingCommand> {

	private static final int SYSTEM_TYPE = 1;

	@Inject
	private FlexWorkMntSetRepository flexWorkSettingRepository;

	@Inject
	private AggDeformedLaborSettingRepository aggDeformedLaborSettingRepository;

	@Inject
	private WorkManagementMultipleRepository workManagementMultipleRepository;

	@Inject
	private TemporaryWorkUseManageRepository tempWorkUseManageRepository;

	@Inject
	private ManageEntryExitRepository manageEntryExitRepository;

	@Override
	protected void handle(CommandHandlerContext<RegisterFunctionSettingCommand> context) {
		String cid = AppContexts.user().companyId();
		List<DisplayDataDto> datas = context.getCommand().getDatas();
		this.findDisplayData(datas, "CMM029_15").ifPresent(data -> {
			// 1. 取得する(会社ID＝Input. 会社ID)
			Optional<FlexWorkSet> optFlexWorkSet = this.flexWorkSettingRepository.find(cid);
			// 1.1. <call>
			UseAtr useAtr = UseAtr.valueOf(data.isUseAtr() ? 1 : 0);
			FlexWorkSet domain = new FlexWorkSet(new CompanyId(cid), useAtr);
			if (optFlexWorkSet.isPresent()) {
				this.flexWorkSettingRepository.update(domain);
			} else {
				this.flexWorkSettingRepository.add(domain);
			}
		});

		this.findDisplayData(datas, "CMM029_16").ifPresent(data -> {
			// 2. 取得する(会社ID＝Input. 会社ID)
			Optional<AggDeformedLaborSetting> optLaborSetting = this.aggDeformedLaborSettingRepository.findByCid(cid);
			// 2.1. <call>
			UseAtr useAtr = UseAtr.valueOf(data.isUseAtr() ? 1 : 0);
			AggDeformedLaborSetting domain = new AggDeformedLaborSetting(new CompanyId(cid), useAtr);
			if (optLaborSetting.isPresent()) {
				this.aggDeformedLaborSettingRepository.update(domain);
			} else {
				this.aggDeformedLaborSettingRepository.insert(domain);
			}
		});

		this.findDisplayData(datas, "CMM029_17").ifPresent(data -> {
			// 3. 取得する(会社ID＝Input. 会社ID)
			Optional<WorkManagementMultiple> optManageMulti = this.workManagementMultipleRepository.findByCode(cid);
			// 3.1. <call>
			UseATR useAtr = EnumAdaptor.valueOf(data.isUseAtr() ? 1 : 0, UseATR.class);
			WorkManagementMultiple domain = new WorkManagementMultiple(cid, useAtr);
			if (optManageMulti.isPresent()) {
				this.workManagementMultipleRepository.update(domain);
			} else {
				this.workManagementMultipleRepository.insert(domain);
			}
		});

		this.findDisplayData(datas, "CMM029_18").ifPresent(data -> {
			// 4. 取得する(会社ID＝Input. 会社ID)
			Optional<TemporaryWorkUseManage> optTempWorkUse = this.tempWorkUseManageRepository.findByCid(cid);
			// 4.1. <call>
			TemporaryWorkUseManage domain = TemporaryWorkUseManage.createFromJavaType(cid, data.isUseAtr() ? 1 : 0);
			if (optTempWorkUse.isPresent()) {
				this.tempWorkUseManageRepository.update(domain);
			} else {
				this.tempWorkUseManageRepository.insert(domain);
			}
		});

		this.findDisplayData(datas, "CMM029_19").ifPresent(data -> {
			// 5. 取得する(会社ID＝Input. 会社ID)
			Optional<ManageEntryExit> optEntryExit = this.manageEntryExitRepository.findByID(cid);
			// 5.1. <call>
			ManageEntryExit domain = new ManageEntryExit(new ManageEntryExitGetMemento() {

				@Override
				public NotUseAtr getUseCls() {
					return NotUseAtr.valueOf(data.isUseAtr());
				}

				@Override
				public String getCompanyID() {
					return cid;
				}
			});
			if (optEntryExit.isPresent()) {
				this.manageEntryExitRepository.update(domain);
			} else {
				this.manageEntryExitRepository.add(domain);
			}
		});
	}

	private Optional<DisplayDataDto> findDisplayData(List<DisplayDataDto> datas, String programId) {
		return datas.stream().filter(data -> data.getProgramId().equals(programId) && data.getSystem() == SYSTEM_TYPE)
				.findFirst();
	}
}
