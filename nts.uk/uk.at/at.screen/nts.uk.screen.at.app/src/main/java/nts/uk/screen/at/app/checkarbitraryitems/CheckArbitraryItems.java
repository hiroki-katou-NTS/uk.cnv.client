package nts.uk.screen.at.app.checkarbitraryitems;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;

import nts.uk.ctx.at.function.dom.attendanceitemframelinking.AttendanceItemLinking;
import nts.uk.ctx.at.function.dom.attendanceitemframelinking.enums.FrameCategory;
import nts.uk.ctx.at.function.dom.attendanceitemframelinking.enums.TypeOfItem;
import nts.uk.ctx.at.function.dom.attendanceitemframelinking.repository.AttendanceItemLinkingRepository;
import nts.uk.ctx.at.shared.dom.scherec.optitem.CheckValueInputCorrectOuput;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItem;
import nts.uk.ctx.at.shared.dom.scherec.optitem.OptionalItemRepository;
import nts.uk.screen.at.app.dailymodify.query.DailyModifyResult;
import nts.uk.screen.at.app.dailyperformance.correction.dto.DPItemValue;
import nts.uk.shr.com.context.AppContexts;

/**
 * 任意項目の入力チェック
 * UKDesign.UniversalK.就業.KDW_日別実績.KDW003_日別実績の修正.A：日別実績の修正_NEW.アルゴリズム.計算、登録.チェック処理.計算前エラーチェック.任意項目の入力チェック
 * @author tutk
 *
 */
@Stateless
public class CheckArbitraryItems {
	
	@Inject
	private AttendanceItemLinkingRepository attendanceItemLinkingRepository;
	
	@Inject
	private OptionalItemRepository optionalItemRepository;
	
	public List<DPItemValue> check(List<DPItemValue> items,List<DailyModifyResult> itemValues) {
		String companyId = AppContexts.user().companyId();
		//【任意項目の勤怠項目ID】 641～740
		List<Integer> listAttendaceItem = new ArrayList<>();
		for(int i = 641;i<=740;i++) {
			listAttendaceItem.add(i);
		}
		List<DPItemValue> result = new ArrayList<>();
		// loc chua item can check
		List<DPItemValue> itemCanCheck = items.stream().filter(x -> listAttendaceItem.contains(x.getItemId()))
				.collect(Collectors.toList());
		if(itemCanCheck.isEmpty()) {
			return result;
		}
		List<Integer> listItemId = itemCanCheck.stream().map(c->c.getItemId()).collect(Collectors.toList());
		//ドメインモデル「勤怠項目と枠の紐付け」を取得する
		List<AttendanceItemLinking> listAttdItemLinking = attendanceItemLinkingRepository
				.getFullDataByAttdIdAndTypeAndCategory(listItemId, TypeOfItem.Daily, FrameCategory.AnyItem.value);
		for(AttendanceItemLinking ail : listAttdItemLinking) {
			//ドメインモデル「任意項目」を取得する
			Optional<OptionalItem> optOptionalItem = optionalItemRepository.findByItemNo(companyId, ail.getFrameNo().v());
			if(optOptionalItem.isPresent()) {
				DPItemValue dPItemValue = itemCanCheck.stream().filter(x->x.getItemId() == ail.getAttendanceItemId()).findFirst().get();
				if(Strings.isNotBlank(dPItemValue.getValue())) {
					CheckValueInputCorrectOuput checkError = optOptionalItem.get().checkInputValueCorrect(new BigDecimal(dPItemValue.getValue()));
					if(!checkError.isCheckResult()) {
						//返ってきた「入力値チェック結果.エラー内容」を全て表示する
						for(String error :checkError.getErrorContent()) {
							DPItemValue newobj = dPItemValue.createNewError(error);
							result.add(newobj);
						}
					}
				}
			}
		}
		
		return result;
	}

}
