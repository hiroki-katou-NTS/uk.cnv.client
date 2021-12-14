package nts.uk.ctx.at.request.dom.application.businesstrip.service;

import nts.arc.time.GeneralDate;
import nts.uk.ctx.at.request.dom.application.businesstrip.BusinessTripInfo;
import nts.uk.ctx.at.request.dom.application.businesstrip.BusinessTripInfoOutput;
import nts.uk.ctx.at.request.dom.application.common.service.other.output.ActualContentDisplay;
import nts.uk.ctx.at.request.dom.application.common.service.setting.output.AppDispInfoStartupOutput;
import nts.uk.ctx.at.request.dom.setting.employment.appemploymentsetting.AppEmploymentSet;
import nts.uk.ctx.at.request.dom.setting.employment.appemploymentsetting.BusinessTripAppWorkType;
import nts.uk.ctx.at.shared.dom.common.time.AttendanceTime;
import nts.uk.ctx.at.shared.dom.worktype.WorkType;
import nts.uk.ctx.at.shared.dom.worktype.WorkTypeClassification;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BusinessTripService {

    /**
     * UKDesign.UniversalK.就業.KAF_申請.KAF008_出張申請.A:出張の申請（新規）.アルゴリズム.出張申請画面初期（新規）.アルゴリズム「出張申請未承認申請を取得」を実行する
     *
     * @param sid                       社員ID
     * @param appDate                   申請対象日リスト
     * @param opActualContentDisplayLst 申請表示情報.申請表示情報(基準日関係あり).表示する実績内容
     */
    void getBusinessTripNotApproved(String sid, List<GeneralDate> appDate, Optional<List<ActualContentDisplay>> opActualContentDisplayLst);

    /**
     * アルゴリズム「出張申請画面初期（更新）」を実行する
     * @param companyId
     * @param appId
     * @param appDispInfoStartupOutput
     * @return
     */
    DetailScreenB getDataDetail(String companyId, String appId, AppDispInfoStartupOutput appDispInfoStartupOutput);

    /**
     * UKDesign.UniversalK.就業.KAF_申請.KAF008_出張申請.A:出張の申請（新規）.アルゴリズム.出張申請画面初期（新規）.アルゴリズム「出張申請勤務種類を取得する」を実行する
     *
     * @param appEmploymentSet       ドメインモデル「雇用別申請承認設定」
     * @param workStyle              出勤休日区分
     * @param workTypeClassification 勤務分類(LIST)
     */
    List<WorkType> getBusinessAppWorkType(Optional<AppEmploymentSet> appEmploymentSet, BusinessTripAppWorkType workStyle, List<WorkTypeClassification> workTypeClassification);

    /**
     * UKDesign.UniversalK.就業.KAF_申請.KAF008_出張申請.A:出張の申請（新規）.アルゴリズム.出張申請個別エラーチェック.アルゴリズム「出張申請就業時間帯チェック」を実行する
     * @param wkTypeCd              対象日の画面の勤務種類コード
     * @param wkTimeCd              対象日の画面の就業時間帯コード
     * @param startWorkTime         対象日の出勤時刻
     * @param endWorkTime           対象日の退勤時刻
     * @param checkInputTime        入力チェック
     */
    ResultCheckInputCode checkRequireWorkTimeCode(String wkTypeCd, String wkTimeCd, Integer startWorkTime, Integer endWorkTime, boolean checkInputTime);

    /**
     * UKDesign.UniversalK.就業.KAF_申請.KAF008_出張申請.A:出張の申請（新規）.アルゴリズム.出張申請勤務変更ダイアログ用情報の取得.アルゴリズム「出張申請勤務種類分類内容取得」を実行する
     * @param   workType INPUT.勤務種類
     * @return  出勤日(True/False)
     */
    boolean getBusinessTripClsContent(WorkType workType);

    /**
     * UKDesign.UniversalK.就業.KAF_申請.KAF008_出張申請.A:出張の申請（新規）.アルゴリズム.出張申請登録前エラーチェック.アルゴリズム「出張申請個別エラーチェック」を実行する
     * @param infos     出張申請（入力内容）
     * @param output    出張申請の表示情報
     */
    void businessTripIndividualCheck(List<BusinessTripInfo> infos, BusinessTripInfoOutput output, Map<GeneralDate, ScreenWorkInfoName> screenWorkInfoName) ;

    /**
     * 勤務種類コードを入力する
     * @param   inputDate     年月日＝変更対象の年月日
     * @param   infoOutput    出張申請の表示情報
     * @param   inputCode     勤務種類コード　＝　画面入力した勤務種類CD
     * @return  infoOutput    出張申請の表示情報
     */
    BusinessTripInfoOutput checkChangeWorkTypeCode(GeneralDate inputDate, BusinessTripInfoOutput infoOutput, String inputCode);
    
    /**
     * 出張申請就業時刻の初期値をセットする
     * @param input 出張申請の表示情報
     * @return 出張申請の表示情報
     */
    BusinessTripInfoOutput setInitValueAppWorkTime(BusinessTripInfoOutput input);
    
    /**
     * 出張申請就業時刻を取得する
     * @param workType 勤務種類
     * @param workTypeCd 就業時間帯コード
     * @return 開始時刻１(Optional）
                                    終了時刻１(Optional）
                                    開始時刻２(Optional）
                                    終了時刻２(Optional）

     */
    WorkTimeGetOuput getWorkTimeBusinessTrip(WorkType workType, String workTypeCd, List<BusinessTripWorkingHours> workingHours);
    
    /**
     * 勤務種類により出退勤時刻をチェックする
     * @param date
     * @param workTypeCode
     * @param startTime
     * @param endTime
     */
    void checkTimeByWorkType(GeneralDate date, String workTypeCode, Integer startTime, Integer endTime);

}
