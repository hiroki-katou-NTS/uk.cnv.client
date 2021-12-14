package nts.uk.ctx.exio.dom.input.canonicalize.domains.employee;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.val;
import nts.arc.enums.EnumAdaptor;
import nts.uk.ctx.at.shared.dom.workingcondition.ManageAtr;
import nts.uk.ctx.at.shared.dom.workingcondition.WorkScheduleBasicCreMethod;
import nts.uk.ctx.exio.dom.input.ExecutionContext;
import nts.uk.ctx.exio.dom.input.canonicalize.CanonicalItem;
import nts.uk.ctx.exio.dom.input.canonicalize.CanonicalItemList;
import nts.uk.ctx.exio.dom.input.canonicalize.CanonicalizeUtil;
import nts.uk.ctx.exio.dom.input.canonicalize.domaindata.DomainDataColumn;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.DomainCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.ItemNoMap;
import nts.uk.ctx.exio.dom.input.canonicalize.domains.generic.EmployeeHistoryCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.history.HistoryType;
import nts.uk.ctx.exio.dom.input.canonicalize.methods.EmployeeCodeCanonicalization;
import nts.uk.ctx.exio.dom.input.canonicalize.methods.IntermediateResult;
import nts.uk.ctx.exio.dom.input.errors.ExternalImportError;

/**
 * 労働条件の正準化 
 */
public class WorkConditionCanonicalization extends EmployeeHistoryCanonicalization{

	/** 社員コードの正準化 */
	private final EmployeeCodeCanonicalization employeeCodeCanonicalization;
	
	public WorkConditionCanonicalization() {
		super(HistoryType.PERSISTENERESIDENT);
		this.employeeCodeCanonicalization = new EmployeeCodeCanonicalization(getItemNoMap());
	}

	@Override
	public ItemNoMap getItemNoMap() {
		return ItemNoMap.reflection(Items.class);
	}

	public static class Items {
		public static final int 社員コード = 1;
		public static final int 開始日 = 2;
		public static final int 終了日 = 3;
		public static final int 労働制 = 4;
		public static final int 勤務時間の自動設定 = 5;
		public static final int 加給時間帯コード = 6;
		public static final int 時間給者区分 = 7;
		public static final int 契約時間 = 8;
		public static final int 加算時間1日 = 9;
		public static final int 加算時間午前 = 10;
		public static final int 加算時間午後 = 11;
		public static final int スケジュール管理設定 = 12;
		public static final int スケジュール作成方法 = 13;
		public static final int 営業日カレンダーの参照先 = 14;
		public static final int 就業時間帯の参照先 = 15;
		public static final int 月間パターンコード = 16;
		public static final int 平日時の勤務種類コード = 18;
		public static final int 平日時の就業時間帯コード = 19;
		public static final int 平日時の開始時刻1 = 20;
		public static final int 平日時の終了時刻1 = 21;
		public static final int 平日時の開始時刻2 = 22;
		public static final int 平日時の終了時刻2 = 23;
		public static final int 休日時の勤務種類コード = 24;
		public static final int 休日出勤時の勤務種類コード = 25;
		public static final int 休日出勤時の就業時間帯コード = 26;
		public static final int 休日出勤時の開始時刻1 = 27;
		public static final int 休日出勤時の終了時刻1 = 28;
		public static final int 休日出勤時の開始時刻2 = 29;
		public static final int 休日出勤時の終了時刻2 = 30;
		public static final int 法定内休出時の勤務種類コード = 31;
		public static final int 法定外休出時の勤務種類コード = 32;
		public static final int 祝日時の勤務種類コード = 33;
		public static final int 月曜の就業時間帯コード = 34;
		public static final int 月曜の開始時刻1 = 35;
		public static final int 月曜の終了時刻1 = 36;
		public static final int 月曜の開始時刻2 = 37;
		public static final int 月曜の終了時刻2 = 38;
		public static final int 火曜の就業時間帯コード = 39;
		public static final int 火曜の開始時刻1 = 40;
		public static final int 火曜の終了時刻1 = 41;
		public static final int 火曜の開始時刻2 = 42;
		public static final int 火曜の終了時刻2 = 43;
		public static final int 水曜の就業時間帯コード = 44;
		public static final int 水曜の開始時刻1 = 45;
		public static final int 水曜の終了時刻1 = 46;
		public static final int 水曜の開始時刻2 = 47;
		public static final int 水曜の終了時刻2 = 48;
		public static final int 木曜の就業時間帯コード = 49;
		public static final int 木曜の開始時刻1 = 50;
		public static final int 木曜の終了時刻1 = 51;
		public static final int 木曜の開始時刻2 = 52;
		public static final int 木曜の終了時刻2 = 53;
		public static final int 金曜の就業時間帯コード = 54;
		public static final int 金曜の開始時刻1 = 55;
		public static final int 金曜の終了時刻1 = 56;
		public static final int 金曜の開始時刻2 = 57;
		public static final int 金曜の終了時刻2 = 58;
		public static final int 土曜の就業時間帯コード = 59;
		public static final int 土曜の開始時刻1 = 60;
		public static final int 土曜の終了時刻1 = 61;
		public static final int 土曜の開始時刻2 = 62;
		public static final int 土曜の終了時刻2 = 63;
		public static final int 日曜の就業時間帯コード = 64;
		public static final int 日曜の開始時刻1 = 65;
		public static final int 日曜の終了時刻1 = 66;
		public static final int 日曜の開始時刻2 = 67;
		public static final int 日曜の終了時刻2 = 68;
		public static final int SID = 101;
		public static final int HIST_ID = 102;
		public static final int 就業時間帯の自動設定区分 = 103;
		public static final int 休暇加算時間利用区分 = 104;
	}

	@Override
	protected String getParentTableName() {
		return "KSHMT_WORKCOND_HIST";
	}

	@Override
	protected List<String> getChildTableNames() {
		return Arrays.asList("KSHMT_WORKCOND_HIST_ITEM",
										 "KSHMT_WORKCOND_SCHE_METH",
										 "KSHMT_WORKCOND_WORKINFO",
										 "KSHMT_WORKCOND_WORK_TS");
	}

	@Override
	protected List<DomainDataColumn> getDomainDataKeys() {
		return Arrays.asList(
				DomainDataColumn.SID,
				DomainDataColumn.HIST_ID);
	}
	
	@Override
	public void canonicalize(DomainCanonicalization.RequireCanonicalize require, ExecutionContext context) {
		
		CanonicalizeUtil.forEachEmployee(require, context, employeeCodeCanonicalization, interms -> {
			List<ExternalImportError> employeeErrors = new ArrayList<>();
			
			for(val interm : interms) {
				ErrorChecker.check(interm).ifPresent(error ->{
					employeeErrors.add(error);
					require.add(context, error);
				});
			}
			if(employeeErrors.isEmpty()) {
				//既存データのチェックと保存は継承先に任せる
				super.canonicalizeHistory(require, context, interms)
					.forEach(result -> {
						require.save(context, result.complete());
					});				
			}
		});
	}
	
	/**
	 * 追加の正準化処理が必要ならoverrideすること
	 */
	@Override
	protected List<Container> canonicalizeExtends(
			DomainCanonicalization.RequireCanonicalize require,
			ExecutionContext context,
			String employeeId,
			List<Container> targetContainers) {

		List<Container> results = new ArrayList<>();
		
		for (val container : targetContainers) {
			
			IntermediateResult interm = container.getInterm();
			
			interm = interm.addCanonicalized(new CanonicalItemList()
					.add(Items.就業時間帯の自動設定区分, 0)
					.add(Items.休暇加算時間利用区分, 
							interm.isImporting(Items.加算時間1日) ?  1 : 0))
			.optionalItem(new CanonicalItem(Items.スケジュール作成方法, Long.valueOf(0)))
			.optionalItem(new CanonicalItem(Items.営業日カレンダーの参照先, Long.valueOf(0)))
			.optionalItem(new CanonicalItem(Items.就業時間帯の参照先, Long.valueOf(0)))
			;
			
			results.add(new Container(interm, container.getAddingHistoryItem()));
		}
		return results;
	}
	
	/**
	 * このドメインにエラーが発生してるかチェックする 
	 */
	private static class ErrorChecker{
		/**
		 * チェック依頼窓口 
		 */
		public static Optional<ExternalImportError> check(IntermediateResult interm){
			//休暇加算時間
			if(!hasTimeAllItemNoOrAllNothing(interm, addingTime.keySet())) {
				return  Optional.of(ExternalImportError.record(interm.getRowNo(),
						addingTime.values().stream().collect(Collectors.joining("、")) 
						+ "は同時に受入れなければなりません。"
					));
			}
			//スケジュール
			return checkSchedule(interm);
		}
		
		/**
		 * All or Nothing でTrue,歯抜けの時はは抜けてる項目名 
		 */
		private static boolean hasTimeAllItemNoOrAllNothing(IntermediateResult interm, Set<Integer> items) {
			return (items.stream().allMatch(t -> interm.isImporting(t))
			|| items.stream().allMatch(t -> !interm.isImporting(t)));
		}
		
		private static Optional<ExternalImportError> checkSchedule(IntermediateResult interm) {
			if(useSchedule(interm)) {
				if(interm.isImporting(Items.スケジュール作成方法)) {
					val createMethod = EnumAdaptor.valueOf(interm.getItemByNo(Items.スケジュール作成方法).get().getJavaInt(),WorkScheduleBasicCreMethod.class);
					switch(createMethod) {
						case BUSINESS_DAY_CALENDAR:
							return lackScheduleItem("カレンダー", calender, interm);
						case MONTHLY_PATTERN:
							return lackScheduleItem("月間パターン", monthPattern, interm);
					   //曜日別は特にチェック無
						case PERSONAL_DAY_OF_WEEK:
							return Optional.empty();
						default :
							throw new RuntimeException("unknown value :" + createMethod);
					}
				}
				return Optional.of(ExternalImportError.record(interm.getRowNo(), "スケジュール管理する場合はスケジュール作成方法 を受入れなければなりません。"));
			}
			return Optional.empty();
		}

		private static Optional<ExternalImportError> lackScheduleItem(String createScheduleMethod, Map<Integer, String> itemsMap, IntermediateResult interm) {
			if(itemsMap.keySet().stream().allMatch(t -> interm.isImporting(t))) {
				return Optional.empty();
			}
			return Optional.of(ExternalImportError.record(interm.getRowNo(),
					"スケジュール作成方法が" +createScheduleMethod +  "参照なので、"+ 
					itemsMap.values().stream().collect(Collectors.joining("、"))
					+" は必須です。"));
		}
		
		private static boolean useSchedule(IntermediateResult interm ) {
			return interm.getItemByNo(Items.スケジュール管理設定)
				.map(t -> t.getInt().intValue() == ManageAtr.USE.value) // スケジュール管理する 
				.orElse(false);
		}
		


		private static final Map<Integer,String> addingTime = new HashMap<>();
		static {
			addingTime.put(Items.加算時間1日, "加算時間1日");
			addingTime.put(Items.加算時間午前, "加算時間午前");
			addingTime.put(Items.加算時間午後, "加算時間午後");
		}
		
		private static final Map<Integer,String> calender = new HashMap<>();
		static {
			calender.put(Items.営業日カレンダーの参照先, "営業日カレンダーの参照先");
			calender.put(Items.就業時間帯の参照先, "就業時間帯の参照先");
		}		
		
		private static final Map<Integer,String> monthPattern = new HashMap<>();
		static {
			monthPattern.put(Items.月間パターンコード, "月間パターンコード");
			monthPattern.put(Items.就業時間帯の参照先, "就業時間帯の参照先");
		}		
	}
}
