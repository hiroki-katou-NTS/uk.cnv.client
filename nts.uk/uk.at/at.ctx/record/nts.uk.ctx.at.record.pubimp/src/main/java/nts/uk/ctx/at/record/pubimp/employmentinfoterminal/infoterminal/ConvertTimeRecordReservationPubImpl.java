package nts.uk.ctx.at.record.pubimp.employmentinfoterminal.infoterminal;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import lombok.AllArgsConstructor;
import lombok.Setter;
import nts.arc.task.tran.AtomTask;
import nts.arc.time.GeneralDate;
import nts.arc.time.GeneralDateTime;
import nts.uk.ctx.at.record.dom.adapter.employeemanage.EmployeeManageRCAdapter;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.EmpInfoTerminal;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.EmpInfoTerminalCode;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.TimeRecordReqSetting;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.log.TopPageAlarmEmpInfoTer;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.log.TopPgAlTrRepository;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.receive.ReservationReceptionData;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.repo.EmpInfoTerminalRepository;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.repo.TimeRecordReqSettingRepository;
import nts.uk.ctx.at.record.dom.employmentinfoterminal.infoterminal.service.ConvertTimeRecordReservationService;
import nts.uk.ctx.at.record.dom.reservation.bento.BentoReservation;
import nts.uk.ctx.at.record.dom.reservation.bento.BentoReservationRepository;
import nts.uk.ctx.at.record.dom.reservation.bento.ReservationDate;
import nts.uk.ctx.at.record.dom.reservation.bento.WorkLocationCode;
import nts.uk.ctx.at.record.dom.reservation.bentomenu.BentoMenu;
import nts.uk.ctx.at.record.dom.reservation.bentomenu.BentoMenuRepository;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.ContractCode;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCard;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampCardRepository;
import nts.uk.ctx.at.record.dom.stamp.card.stampcard.StampNumber;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampRecord;
import nts.uk.ctx.at.record.dom.workrecord.stampmanagement.stamp.StampRecordRepository;
import nts.uk.ctx.at.record.pub.employmentinfoterminal.infoterminal.ConvertTimeRecordReservationPub;
import nts.uk.ctx.at.record.pub.employmentinfoterminal.infoterminal.ReservReceptDataExport;

@Stateless
public class ConvertTimeRecordReservationPubImpl implements ConvertTimeRecordReservationPub {

	@Inject
	private EmpInfoTerminalRepository empInfoTerminalRepository;

	@Inject
	private TimeRecordReqSettingRepository timeRecordReqSettingRepository;

	@Inject
	private BentoMenuRepository bentoMenuRepository;

	@Inject
	private BentoReservationRepository bentoReservationRepository;

	@Inject
	private StampRecordRepository stampRecordRepository;

	@Inject
	private StampCardRepository stampCardRepository;

	@Inject
	private EmployeeManageRCAdapter employeeManageRCAdapter;

	@Inject
	private TopPgAlTrRepository executionLog;

	@Override
	public Optional<AtomTask> convertData(String empInfoTerCode, String contractCode,
			ReservReceptDataExport reservReceptData) {

		RequireImpl requireImpl = new RequireImpl("", empInfoTerminalRepository, timeRecordReqSettingRepository,
				employeeManageRCAdapter, bentoMenuRepository, bentoReservationRepository, stampRecordRepository,
				stampCardRepository, executionLog);
		return ConvertTimeRecordReservationService.convertData(requireImpl, new EmpInfoTerminalCode(empInfoTerCode),
				new ContractCode(contractCode),
				new ReservationReceptionData(reservReceptData.getIdNumber(), reservReceptData.getMenu(),
						reservReceptData.getYmd(), reservReceptData.getTime(), reservReceptData.getQuantity()));
	}

	@AllArgsConstructor
	public static class RequireImpl implements ConvertTimeRecordReservationService.Require {

		@Setter
		private String companyId;

		private final EmpInfoTerminalRepository empInfoTerminalRepository;

		private final TimeRecordReqSettingRepository timeRecordReqSettingRepository;

		private final EmployeeManageRCAdapter employeeManageRCAdapter;

		private final BentoMenuRepository bentoMenuRepository;

		private final BentoReservationRepository bentoReservationRepository;

		private final StampRecordRepository stampRecordRepository;

		private final StampCardRepository stampCardRepository;

		private final TopPgAlTrRepository executionLog;

		@Override
		public void reserve(BentoReservation bentoReservation) {

			bentoReservationRepository.add(bentoReservation);
		}

		@Override
		public Optional<EmpInfoTerminal> getEmpInfoTerminal(EmpInfoTerminalCode empInfoTerCode,
				ContractCode contractCode) {
			return empInfoTerminalRepository.getEmpInfoTerminal(empInfoTerCode, contractCode);
		}

		@Override
		public Optional<StampRecord> getStampRecord(ContractCode contractCode, StampNumber stampNumber,
				GeneralDateTime dateTime) {
			return stampRecordRepository.findByKey(stampNumber, dateTime);
		}

		@Override
		public Optional<TimeRecordReqSetting> getTimeRecordReqSetting(EmpInfoTerminalCode empInfoTerCode,
				ContractCode contractCode) {

			Optional<TimeRecordReqSetting> timeOpt = timeRecordReqSettingRepository
					.getTimeRecordReqSetting(empInfoTerCode, contractCode);

			if (timeOpt.isPresent()) {
				companyId = timeOpt.get().getCompanyId().v();
			}
			return timeOpt;
		}

		@Override
		public void insert(StampRecord stampRecord) {
			stampRecordRepository.insert(stampRecord);

		}

		@Override
		public Optional<StampCard> getByCardNoAndContractCode(ContractCode contractCode, StampNumber stampNumber) {
			return stampCardRepository.getByCardNoAndContractCode(stampNumber.v(), contractCode.v());
		}

		@Override
		public List<String> getListEmpID(String companyID, GeneralDate referenceDate) {
			return employeeManageRCAdapter.getListEmpID(companyID, referenceDate);
		}

		@Override
		public void insertLogAll(TopPageAlarmEmpInfoTer alEmpInfo) {
			executionLog.insertLogAll(alEmpInfo);

		}

		@Override
		public BentoMenu getBentoMenu(ReservationDate reservationDate, Optional<WorkLocationCode> workLocationCode) {
			return bentoMenuRepository.getBentoMenu(companyId, reservationDate.getDate(), workLocationCode);

		}
	}

}
