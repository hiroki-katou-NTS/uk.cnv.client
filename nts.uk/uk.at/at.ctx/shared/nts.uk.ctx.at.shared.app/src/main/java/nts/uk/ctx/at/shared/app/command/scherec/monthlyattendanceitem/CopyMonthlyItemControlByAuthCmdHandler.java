package nts.uk.ctx.at.shared.app.command.scherec.monthlyattendanceitem;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import nts.arc.layer.app.command.CommandHandler;
import nts.arc.layer.app.command.CommandHandlerContext;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattendanceitem.DisplayAndInputMonthly;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattendanceitem.MonthlyItemControlByAuthRepository;
import nts.uk.ctx.at.shared.dom.scherec.monthlyattendanceitem.MonthlyItemControlByAuthority;
import nts.uk.shr.com.context.AppContexts;

/**
 * 
 * @author xuannt
 *	UKDesign.UniversalK.就業.KDW_日別実績.KDW002_勤怠項目利用設定.勤怠項目の利用設定.ユースケース.他の権限に複写する (Copy vào quyền khác).他の権限に複写する.システム.アルゴリズム「現在の設定を複写して登録する」を実行する (Tiến hành xử lý "Copy cài đặt hiện thời và đăng ký")
 *
 */
@Stateless
public class CopyMonthlyItemControlByAuthCmdHandler extends CommandHandler<MonthlyItemControlByAuthCopyCmd> {
	
	@Inject 
	private MonthlyItemControlByAuthRepository repo;

	@Override
	protected void handle(CommandHandlerContext<MonthlyItemControlByAuthCopyCmd> context) {
		String companyId = AppContexts.user().companyId();
		MonthlyItemControlByAuthCopyCmd command = context.getCommand();
		String roleID = command.getRoleID();
		List<String> destinationList = command.getDestinationList();
		if(roleID.isEmpty() || destinationList.isEmpty())
			return;
		
		Optional<MonthlyItemControlByAuthority> data =repo.getMonthlyAttdItem(companyId, command.getRoleID());
		if(data.isPresent()) {
			MonthlyItemControlByAuthority monthlyItem = data.get();
			List<DisplayAndInputMonthly> displayAndInputList = monthlyItem.getListDisplayAndInputMonthly();
			for(String des : destinationList) {
				repo.delete(companyId, des);
				repo.addMonthlyAttdItemAuth(new MonthlyItemControlByAuthority(companyId, des, displayAndInputList));
			}
		}
	}
}