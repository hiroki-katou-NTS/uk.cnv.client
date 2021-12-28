package nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nts.arc.task.tran.AtomTask;

/**
 * DS: お気に入り作業を削除する
 * 
 * @author tutt
 * @name UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.お気に入り作業.1日お気に入りセット.お気に入り作業を削除する
 */
public class DeleteOneDayFavoriteTaskService {
	
	public static AtomTask create(Require require, String employeeId, String favoriteId) {
		List<AtomTask> atomTasks = new ArrayList<>();

		// $表示順 = require.表示順を取得する(社員ID)
		Optional<OneDayFavoriteTaskDisplayOrder> favoriteTaskDisplayOrder = require.get(employeeId);

		// $表示順.お気に入りの表示順を削除する(お気に入りID)
		if (favoriteTaskDisplayOrder.isPresent()) {
			favoriteTaskDisplayOrder.get().delete(favoriteId);
		}

		// return Atom Task:
		// $登録対象.add(require.お気に入りを削除する(社員ID, お気に入りID)
		
		atomTasks.add(AtomTask.of(() -> require.delete(employeeId, favoriteId)));

		if (favoriteTaskDisplayOrder.isPresent()) {
			if (favoriteTaskDisplayOrder.get().getDisplayOrders().isEmpty()) {
				atomTasks.add(AtomTask.of(() -> require.delete(employeeId)));
			} else {
				atomTasks.add(AtomTask.of(() -> require.update(favoriteTaskDisplayOrder.get())));
				require.deleteByFavId(favoriteId);
			}
		}
		
		return AtomTask.bundle(atomTasks);
	}

	// ■Require
	public static interface Require {
		// [R-1] 表示順を取得する
		// 1日お気に入り作業の表示順Repository.Get(社員ID)
		Optional<OneDayFavoriteTaskDisplayOrder> get(String employeeId);

		// [R-2] お気に入りを削除する
		// 1日お気に入り作業セットRepository.Delete(社員ID,お気に入りID)
		void delete(String employeeId, String favoriteId);

		// [R-3] 表示順を削除する
		// 1日お気に入り作業の表示順Repository.Delete(社員ID)
		void delete(String employeeId);

		// [R-4] 表示順を更新する
		// 1日お気に入り作業の表示順Repository.Update(1日お気に入り作業の表示順)
		void update(OneDayFavoriteTaskDisplayOrder favoriteTaskDisplayOrder);
		
		void deleteByFavId(String favoriteId);
	}

}
