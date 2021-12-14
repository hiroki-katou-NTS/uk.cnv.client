package nts.uk.ctx.at.schedule.dom.importschedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import nts.arc.time.GeneralDate;

/**
 * 取り込み内容
 * UKDesign.ドメインモデル.NittsuSystem.UniversalK.就業.contexts.勤務予定.取込.取り込み内容
 * @author kumiko_otake
 */
@Getter
@ToString(callSuper=true)
@EqualsAndHashCode
public class CapturedRawData {

	/** 取り込み内容 **/
	private final List<CapturedRawDataOfCell> contents;
	/** 社員の並び順(OrderdList) **/
	private final List<String> employeeCodes;



	/**
	 * コンストラクタ
	 * @param contents 取り込み内容(List)
	 * @param employeeCodes 社員の並び順(OrderedList)
	 */
	public CapturedRawData(List<CapturedRawDataOfCell> contents, List<String> employeeCodes) {

		this.contents = contents;
		this.employeeCodes = Collections.unmodifiableList(new ArrayList<>( employeeCodes ));

	}



	/**
	 * 年月日リストを取得する
	 * @return 年月日リスト
	 */
	public List<GeneralDate> getYmdList() {
		return this.contents.stream()
				.map( CapturedRawDataOfCell::getYmd )
				.distinct()
				.sorted()
				.collect(Collectors.toList());
	}

}
