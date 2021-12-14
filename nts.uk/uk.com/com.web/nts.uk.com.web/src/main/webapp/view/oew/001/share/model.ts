module nts.uk.com.view.oew001.share.model {

  /**
   * 設備情報
   */
  export class EquipmentInformationDto {

    // コード
    code: string;

    // 名称
    name: string;

    // 有効開始日
    effectiveStartDate: string;

    // 有効終了日
    effectiveEndDate: string;

    // 備分類コード
    equipmentClsCode: string;

    // 備考
    remark: string;

    constructor(init?: Partial<EquipmentInformationDto>) {
      $.extend(this, init);
    }
  }

  /**
   * 設備分類
   */
  export class EquipmentClassificationDto {

    // コード
    code: string;

    // 名称
    name: string;
  }

  /**
   * 設備利用実績データ
   */
  export class EquipmentDataDto {

    // 入力日
    inputDate: string;

    // 利用日
    useDate: string;

    // 利用者ID
    sid: string;

    // 設備コード
    equipmentCode: string;

    // 設備分類コード
    equipmentClassificationCode: string;

    // 項目データ
    itemDatas: ItemDataDto[];

    constructor(init?: Partial<EquipmentDataDto>) {
      $.extend(this, init);
    }
  }

  /**
   * 設備の実績入力フォーマット設定
   */
  export class EquipmentPerformInputFormatSettingDto {

    // 会社ID
    cid: string;

    // 項目表示設定
    itemDisplaySettings: ItemDisplayDto[];
  }

  /**
   * 設備利用実績の項目設定
   */
  export class EquipmentUsageRecordItemSettingDto {
    
    // 会社ID
    cid: string;
    
    // 項目NO
    itemNo: string;
    
    // 項目入力制御
    inputControl: ItemInputControlDto;
    
    // 項目の表示
    items: DisplayOfItemsDto;
  }

  /**
   * 実績データ
   */
  export class ItemDataDto {

    // 項目NO
    itemNo: string;

    // 項目分類
    itemClassification: number;

    // 項目値
    actualValue: string;

    constructor(init?: Partial<ItemDataDto>) {
      $.extend(this, init);
    }
  }

  /**
   * 項目表示
   */
  export class ItemDisplayDto {

    // 表示幅
    displayWidth: number;

    // 表示順番
    displayOrder: number;

    // 項目NO
    itemNo: string;
  }

  /**
   * 項目入力制御
   */
  export class ItemInputControlDto {

    // 項目分類
    itemCls: number;
    
    // 必須
    require: boolean;
    
    // 桁数
    digitsNo: number;
    
    // 最大値
    maximum: number;
    
    // 最小値
    minimum: number;
  }

  /**
   * 項目の表示
   */
  export class DisplayOfItemsDto {

    // 項目名称
    itemName: string;
    
    // 単位
    unit: string;
    
    // 説明
    memo: string;
  }

  /**
   * 社員
   */
  export class EmployeeInfoDto {
    
    // 社員ID
    employeeId: string; 

    // 社員コード
    employeeCode: string; 

    // ビジネスネーム
    businessName: string; 
  }

  export class OptionalItem {
    itemNo: string;
    itemName: string;
    itemCls: number;
    value: KnockoutObservable<string> = ko.observable(null);
    unit: string;
    width: string;
    displayOrder: number;
    required: boolean;
    memo: string;
    helpContent: string;
    constraint: ui.vm.Constraint;
    constraintName: string;

    constructor(init?: Partial<OptionalItem>) {
      $.extend(this, init);
    }
  }

  export class Oew001BData {
    isNewMode: boolean;
    equipmentClsCode: string;
    equipmentClsName: string;
    sid: string;
    employeeName: KnockoutObservable<string>;
    equipmentInfoCode: string;
    equipmentInfoName: string;
    useDate: KnockoutObservable<string>;
    inputDate: string;
    required: boolean;
    optionalItems: KnockoutObservableArray<model.OptionalItem> = ko.observableArray([]);
    validStartDate: moment.Moment;
    validEndDate: moment.Moment;

    constructor(init?: Partial<Oew001BData>) {
      $.extend(this, init);
    }
  }

  export function setReadOnly(itemId: string) {
    $(itemId).attr("readonly", "readonly");
  }
}

module nts.uk.com.view.oew001.share.model.constants {
  export const YYYY_MM_DD = "YYYY/MM/DD";
  export const SELECT_ALL_CODE = ' ';
  // 固定値Ａ
  export const FIXED_VALUE_A = 9;
  export const MAXIMUM_COL_WIDTH = 450;
  export const MAXIMUM_GRID_WIDTH = 1200;
  export const NTS_GRID_CACHE_KEY = `${window.location.href}/A6`;
}

module nts.uk.com.view.oew001.share.model.enums {
  export enum ItemClassification {
    // 文字
    TEXT = 0,

    // 数字
    NUMBER = 1,

    // 時間
    TIME = 2
  }

  export enum PrintType {
    EXCEL = 0,
    CSV = 1
  }
}