package nts.uk.ctx.at.shared.app.command.remainingnumber.subhdmana;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.shared.dom.remainingnumber.algorithm.InterimRemainDataMngRegisterDateChange;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimBreakDayOffMngRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.breakdayoffmng.interim.InterimDayOffMng;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.ComDayOffManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.CompensatoryDayOffManaData;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveComDayOffManaRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveManaDataRepository;
import nts.uk.ctx.at.shared.dom.remainingnumber.subhdmana.LeaveManagementData;
import nts.uk.shr.com.context.AppContexts;

/**
 * UKDesign.UniversalK.就業.KDM_残数管理 (Quản lý số dư).KDM001_残数管理データの登録 (Đăng ký dữ liệu quản lý số dư).Ｂ：代休管理(Quản lý ngày nghỉ bù).アルゴリズム.休出代休管理データを削除.休出代休管理データを削除
 */
@Stateless
public class DeleteLeaveManagementDataCommandHandler extends CommandHandler<DeleteLeaveManagementDataCommand> {

	@Inject
	private LeaveManaDataRepository leaveManaDataRepository;
	
	@Inject
	private ComDayOffManaDataRepository comDayOffManaDataRepository;
	
	@Inject
	private LeaveComDayOffManaRepository leaveComDayOffManaRepository;
	
	@Inject
	private InterimBreakDayOffMngRepository interimBreakDayOffMngRepository;
	
	@Inject
	private InterimRemainDataMngRegisterDateChange interimRemainDataMngRegisterDateChange;

	@Override
	protected void handle(CommandHandlerContext<DeleteLeaveManagementDataCommand> context) {
		DeleteLeaveManagementDataCommand command = context.getCommand();

		// ドメインモデル「休出管理データ」を取得
		List<LeaveManagementData> leaveMana = this.leaveManaDataRepository.getListByLeaveId(command.getLeaveId());

		// ドメインモデル「休出管理データ」を削除
		if (!leaveMana.isEmpty()) {
			this.leaveManaDataRepository.deleteById(command.getLeaveId());
		}

		// ドメインモデル「代休管理データ」を取得
		List<CompensatoryDayOffManaData> comDayOff = this.comDayOffManaDataRepository.getListComdayOffId(command.getComDayOffID());

		// ドメインモデル「代休管理データ」を削除
		if (!comDayOff.isEmpty()) {
			this.comDayOffManaDataRepository.deleteById(command.getComDayOffID());
		}

		// ドメインモデル「休出代休紐付け管理」を削除
		if (!leaveMana.isEmpty() && !comDayOff.isEmpty()) {
			this.leaveComDayOffManaRepository.delete(leaveMana.size() > 0 ? leaveMana.get(0).getSID() : null,
					comDayOff.size() > 0 ? comDayOff.get(0).getSID() : null,
					leaveMana.stream().map(x -> x.getComDayOffDate().getDayoffDate().orElse(null)).collect(Collectors.toList()),
					comDayOff.stream().map(x-> x.getDayOffDate().getDayoffDate().orElse(null)).collect(Collectors.toList()));
		}
		
		// ドメインモデル「暫定代休管理データ」を取得
		List<InterimDayOffMng> interimDayOffMngList = this.interimBreakDayOffMngRepository
				.getDayOffByIds(command.getComDayOffID());
		// 取得した暫定振休管理データをチェック
		if (!interimDayOffMngList.isEmpty()) {
			String cid = AppContexts.user().companyId();
			Map<String, List<InterimDayOffMng>> dataMap = interimDayOffMngList.stream()
					.collect(Collectors.groupingBy(InterimDayOffMng::getSID));
			// 暫定データの登録
			dataMap.entrySet().forEach(entry -> {
				List<GeneralDate> dates = entry.getValue().stream().map(InterimDayOffMng::getYmd)
						.collect(Collectors.toList());
				this.interimRemainDataMngRegisterDateChange.registerDateChange(cid, entry.getKey(), dates);
			});
		}
	}

}
