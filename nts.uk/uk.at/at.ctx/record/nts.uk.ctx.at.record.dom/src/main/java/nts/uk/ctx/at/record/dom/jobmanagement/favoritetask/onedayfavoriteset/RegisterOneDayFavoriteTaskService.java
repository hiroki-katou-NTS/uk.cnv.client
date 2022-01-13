package nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.onedayfavoriteset;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import nts.arc.task.tran.AtomTask;
import nts.uk.ctx.at.record.dom.jobmanagement.favoritetask.favoritetaskitem.FavoriteTaskName;

/**
 * DS: お気に入り作業を登録する
 * 
 * @author tutt
 * @name UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務実績.作業管理.お気に入り作業.1日お気に入りセット.お気に入り作業を登録する
 */
public class RegisterOneDayFavoriteTaskService {

	/**
	 * 
	 * @param require    Require
	 * @param employeeId 社員ID
	 * @param taskName   お気に入り作業名称
	 * @param contents   List<作業ブロック詳細内容>
	 * @return
	 */
	public static AtomTask add(Require require, String employeeId, FavoriteTaskName taskName,
			List<TaskBlockDetailContent> contents) {
		List<AtomTask> atomTasks = new ArrayList<>();

		// $追加お気に入り = 1日お気に入り作業セット#新規追加(社員ID, 名称, お気に入り内容)
		OneDayFavoriteSet favSet = OneDayFavoriteSet.addOneDayFavSet(employeeId, taskName, contents);

		// $追加お気に入り.お気に入りID
		String newFavId = favSet.getFavId();

		// $表示順 = require.表示順を取得する(社員ID)
		Optional<OneDayFavoriteTaskDisplayOrder> orderOpt = require.get(employeeId);

		// $新規表示順
		OneDayFavoriteTaskDisplayOrder newOrder = null;

		// if $表示順.isPresent
		if (orderOpt.isPresent()) {
			// $表示順.新しいお気に入りの表示順を追加する($追加お気に入り.お気に入りID)
			orderOpt.get().add(newFavId);
		} else {
			// $新規表示順 = 1日お気に入り作業の表示順#新規追加(社員ID, $追加お気に入り.お気に入りID)
			newOrder = OneDayFavoriteTaskDisplayOrder.addOneDayFavTaskDisorder(employeeId, newFavId);
		}
		// $登録対象.add(require.お気に入りを追加する($追加お気に入り)
		atomTasks.add(AtomTask.of(() -> require.insert(favSet)));

		// if $新規表示順.isPresent
		if (newOrder != null) {
			// $登録対象.add(require.表示順を追加する($新規表示順)
			atomTasks.add(AtomTask.of(() -> require.insert(OneDayFavoriteTaskDisplayOrder.addOneDayFavTaskDisorder(employeeId, newFavId))));
		} else {
			// $登録対象.add(require.表示順を更新する($表示順)
			atomTasks.add(AtomTask.of(() -> require.update(orderOpt.get())));
		}

		return AtomTask.bundle(atomTasks);
	}

	// ■Require
	public static interface Require {

		// [R-1] 表示順を取得する
		// 1日お気に入り作業の表示順Repository.Get(社員ID)
		Optional<OneDayFavoriteTaskDisplayOrder> get(String employeeId);

		// [R-2] お気に入りを追加する
		// 1日お気に入り作業セットRepository.Insert(1日お気に入り作業セット)
		void insert(OneDayFavoriteSet set);

		// [R-3] 表示順を追加する
		// 1日お気に入り作業の表示順Repository.Insert(1日お気に入り作業の表示順)
		void insert(OneDayFavoriteTaskDisplayOrder order);

		// [R-4] 表示順を更新する
		// 1日お気に入り作業の表示順Repository.Update(1日お気に入り作業の表示順)
		void update(OneDayFavoriteTaskDisplayOrder order);
	}
}
