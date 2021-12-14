package nts.uk.ctx.at.shared.app.command.workrule.shiftmaster;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nts.arc.error.BusinessException;
import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.task.tran.AtomTask;
import nts.uk.ctx.at.shared.dom.WorkInformation;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.BasicScheduleService;
import nts.uk.ctx.at.shared.dom.schedule.basicschedule.SetupType;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.MakeShiftMasterService;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMaster;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterCode;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterImportCode;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.ShiftMasterRepository;
import nts.uk.ctx.at.shared.dom.workrule.shiftmaster.UpdateShiftMasterService;
import nts.uk.ctx.at.shared.dom.worktime.common.WorkTimeCode;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.fixedset.FixedWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flexset.FlexWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSetting;
import nts.uk.ctx.at.shared.dom.worktime.flowset.FlowWorkSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.predset.PredetemineTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSetting;
import nts.uk.ctx.at.shared.dom.worktime.worktimeset.WorkTimeSettingRepository;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeCode;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeRepository;
import nts.uk.shr.com.context.AppContexts;

/**
 * @author anhdt 職場で使うシフトマスタを登録する
 */
@Stateless
public class RegisterShiftMasterCommandHandler extends CommandHandler<RegisterShiftMasterCommand> {

	@Inject
	private ShiftMasterRepository shiftMasterRepo;

	@Inject
	private BasicScheduleService basicScheduleService;

	@Inject
	private WorkTypeRepository workTypeRepo;

	@Inject
	private WorkTimeSettingRepository workTimeSettingRepository;

	@Inject
    private FixedWorkSettingRepository fixedWorkSettingRepository;
	
	@Inject
    private FlowWorkSettingRepository flowWorkSettingRepository;
	
	@Inject
    private FlexWorkSettingRepository flexWorkSettingRepository;
	
	@Inject
    private PredetemineTimeSettingRepository predetemineTimeSettingRepository;


	@Override
	protected void handle(CommandHandlerContext<RegisterShiftMasterCommand> context) {
		String companyId = AppContexts.user().companyId();
		RegisterShiftMasterCommand cmd = context.getCommand();

		Optional<ShiftMaster> existed = shiftMasterRepo.getByShiftMaterCd(companyId,
				new ShiftMasterCode(cmd.getShiftMasterCode()).v());
		if (cmd.getNewMode() && existed.isPresent()) {
			throw new BusinessException("Msg_3");
		}
		//TODO 取込コードを追加
		MakeShiftMasterService.Require createRequired = new MakeShiftMasterRequireImpl(shiftMasterRepo, basicScheduleService, workTypeRepo, workTimeSettingRepository, fixedWorkSettingRepository, flowWorkSettingRepository, flexWorkSettingRepository, predetemineTimeSettingRepository);
		UpdateShiftMasterService.Require updateRequired = new UpdateShiftMasterRequireImpl(shiftMasterRepo, basicScheduleService, workTypeRepo, workTimeSettingRepository, fixedWorkSettingRepository, flowWorkSettingRepository, flexWorkSettingRepository, predetemineTimeSettingRepository);
		ShiftMaster dom = cmd.toDomain();
		dom.checkError(createRequired);

		AtomTask persist;

		if (cmd.getNewMode()) {
			persist = MakeShiftMasterService.makeShiftMaster(createRequired
							, companyId
							, new ShiftMasterCode(cmd.getShiftMasterCode())
							, new WorkTypeCode(cmd.getWorkTypeCd())
							, Optional.ofNullable( cmd.getWorkTimeSetCd().equals("") ? null : new WorkTimeCode(cmd.getWorkTimeSetCd()) )
							, dom.getDisplayInfor()
							, dom.getImportCode()
						);
		} else {
			persist = UpdateShiftMasterService.update(updateRequired
							, new ShiftMasterCode(cmd.getShiftMasterCode())
							, dom.getDisplayInfor()
							, new WorkInformation(cmd.getWorkTypeCd(), cmd.getWorkTimeSetCd())
							, dom.getImportCode()
						);
		}

		transaction.execute(() -> {
			persist.run();
		});

	}

	@AllArgsConstructor
	@NoArgsConstructor
	private static class MakeShiftMasterRequireImpl implements MakeShiftMasterService.Require {

		@Inject
		private ShiftMasterRepository shiftMasterRepo;

		private final String companyId = AppContexts.user().companyId();

		@Inject
		private BasicScheduleService service;

		@Inject
		private WorkTypeRepository workTypeRepo;

		@Inject
		private WorkTimeSettingRepository workTimeSettingRepository;
		
		@Inject
        private FixedWorkSettingRepository fixedWorkSettingRepository;
		
		@Inject
        private FlowWorkSettingRepository flowWorkSettingRepository;
		
		@Inject
        private FlexWorkSettingRepository flexWorkSettingRepository;
		
		@Inject
        private PredetemineTimeSettingRepository predetemineTimeSettingRepository;

		@Override
		public boolean checkExists(WorkTypeCode workTypeCd, Optional<WorkTimeCode> workTimeCd) {
			return shiftMasterRepo.checkExists(companyId, workTypeCd.v(), workTimeCd.map(WorkTimeCode::v).orElse(""));
		}

		@Override
		public boolean checkExistsByCode(ShiftMasterCode shiftMaterCode) {
			return shiftMasterRepo.checkExistsByCd(companyId, shiftMaterCode.v());
		}


		@Override
		public void insert(ShiftMaster shiftMaster) {
			shiftMasterRepo.insert(shiftMaster);
		}

		@Override
		public boolean checkDuplicateImportCode(ShiftMasterImportCode importCode) {
			return shiftMasterRepo.exists(companyId, importCode);
		}

		@Override
		public Optional<WorkType> getWorkType(String workTypeCd) {
			return workTypeRepo.findByPK(companyId, workTypeCd);
		}

		@Override
		public Optional<WorkTimeSetting> getWorkTime(String workTimeCode) {
			return workTimeSettingRepository.findByCode(companyId, workTimeCode);
		}


		@Override
		public SetupType checkNeededOfWorkTimeSetting(String workTypeCode) {
			 return service.checkNeededOfWorkTimeSetting(workTypeCode);
		}

		@Override
		public FixedWorkSetting getWorkSettingForFixedWork(WorkTimeCode code) {
		    return fixedWorkSettingRepository.findByKey(AppContexts.user().companyId(), code.v()).get();
		}

		@Override
		public FlowWorkSetting getWorkSettingForFlowWork(WorkTimeCode code) {
		    return flowWorkSettingRepository.find(AppContexts.user().companyId(), code.v()).get();
		}

		@Override
		public FlexWorkSetting getWorkSettingForFlexWork(WorkTimeCode code) {
		    return flexWorkSettingRepository.find(AppContexts.user().companyId(), code.v()).get();
		}

		@Override
		public PredetemineTimeSetting getPredetermineTimeSetting(WorkTimeCode wktmCd) {
		    return predetemineTimeSettingRepository.findByWorkTimeCode(AppContexts.user().companyId(), wktmCd.v()).get();
		}

	}

	@AllArgsConstructor
	@NoArgsConstructor
	private static class UpdateShiftMasterRequireImpl implements UpdateShiftMasterService.Require {

		private final String companyId = AppContexts.user().companyId();

		@Inject
		private ShiftMasterRepository shiftMasterRepo;

		@Inject
		private BasicScheduleService service;

		@Inject
		private WorkTypeRepository workTypeRepo;

		@Inject
		private WorkTimeSettingRepository workTimeSettingRepository;
		
		@Inject
        private FixedWorkSettingRepository fixedWorkSettingRepository;
		
		@Inject
        private FlowWorkSettingRepository flowWorkSettingRepository;
		
		@Inject
        private FlexWorkSettingRepository flexWorkSettingRepository;
		
		@Inject
        private PredetemineTimeSettingRepository predetemineTimeSettingRepository;

		@Override
		public void update(ShiftMaster shiftMater) {
			shiftMasterRepo.update(shiftMater);
		}

		@Override
		public Optional<ShiftMaster> getByShiftMaterCd(ShiftMasterCode shiftMasterCode) {
			return shiftMasterRepo.getByShiftMaterCd(companyId, shiftMasterCode.v());
		}

		@Override
		public Optional<ShiftMaster> getByWorkTypeAndWorkTime(WorkTypeCode workTypeCd, Optional<WorkTimeCode> workTimeCd) {
			return shiftMasterRepo.getByWorkTypeAndWorkTime(companyId, workTypeCd.v(), workTimeCd.map(WorkTimeCode::v).orElse(null));
		}

		@Override
		public boolean checkDuplicateImportCode(ShiftMasterImportCode importCode) {
			return shiftMasterRepo.checkExistByImportCd(companyId, importCode);
		}

		@Override
		public Optional<WorkType> getWorkType(String workTypeCd) {
			return workTypeRepo.findByPK(companyId, workTypeCd);
		}

		@Override
		public Optional<WorkTimeSetting> getWorkTime(String workTimeCode) {
			return workTimeSettingRepository.findByCode(companyId, workTimeCode);
		}


		@Override
		public SetupType checkNeededOfWorkTimeSetting(String workTypeCode) {
			 return service.checkNeededOfWorkTimeSetting(workTypeCode);
		}

		@Override
		public FixedWorkSetting getWorkSettingForFixedWork(WorkTimeCode code) {
		    return fixedWorkSettingRepository.findByKey(AppContexts.user().companyId(), code.v()).get();
		}

		@Override
		public FlowWorkSetting getWorkSettingForFlowWork(WorkTimeCode code) {
		    return flowWorkSettingRepository.find(AppContexts.user().companyId(), code.v()).get();
		}

		@Override
		public FlexWorkSetting getWorkSettingForFlexWork(WorkTimeCode code) {
		    return flexWorkSettingRepository.find(AppContexts.user().companyId(), code.v()).get();
		}

		@Override
		public PredetemineTimeSetting getPredetermineTimeSetting(WorkTimeCode wktmCd) {
		    return predetemineTimeSettingRepository.findByWorkTimeCode(AppContexts.user().companyId(), wktmCd.v()).get();
		}

	}
}
