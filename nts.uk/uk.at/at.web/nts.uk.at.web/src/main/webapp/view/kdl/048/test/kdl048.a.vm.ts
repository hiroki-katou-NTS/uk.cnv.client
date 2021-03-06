/// <reference path='../../../../lib/nittsu/viewcontext.d.ts' />
module nts.uk.at.view.kdl047.test.screenModel {

  import setShared = nts.uk.ui.windows.setShared;
  import getShared = nts.uk.ui.windows.getShared;

  @bean()
  export class ViewModel extends ko.ViewModel {
    itemList: KnockoutObservableArray<any>;

    position: KnockoutObservable<number> = ko.observable(1);
    attendanceItemName: KnockoutObservable<string> = ko.observable('勤務種類');
    attendanceCode: KnockoutObservable<string> = ko.observable('02');
    attendanceName: KnockoutObservable<string> = ko.observable('出勤簿');
    columnIndex: KnockoutObservable<number> = ko.observable(1);
    tab: KnockoutObservable<number> = ko.observable(1);
    tabs: KnockoutObservableArray<any>;
    isDisplayItemName: KnockoutObservable<boolean> = ko.observable(true);
    isDisplayTitle: KnockoutObservable<boolean> = ko.observable(true);
    isEnableComboBox: KnockoutObservable<number> = ko.observable(1);
    isEnableTextEditor: KnockoutObservable<number> = ko.observable(1);
    data:  KnockoutObservableArray<SelectedTimeListParam> = ko.observableArray([]);
    currentCodeList2 : KnockoutObservableArray<any> = ko.observableArray([]);
    columnSelectedItemList: KnockoutObservableArray<any> = ko.observableArray([
      {
        headerText: this.$i18n("KDL048_11"),
        prop: "operator",
        width: 55,
        columnCssClass: "text-center",
      },
      { headerText: 'ID', prop: "itemId", width: 55  },
    ]);
     shareParam = new SharedParams();
    created() {
      const vm = this;
      vm.itemList = ko.observableArray([
        {
          id: 1,
          name: 'UPPER'
        },
        {
          id: 2,
          name: 'LOWER'
        }
      ])
      vm.tabs = ko.observableArray([
        {
          id: 1,
          name: 'Tab-1'
        },
        {
          id: 2,
          name: 'Tab-2'
        }
      ]);

    }

    open(): void {
      const vm = this;
      vm.shareParam.titleLine.displayFlag = vm.isDisplayTitle();
      vm.shareParam.titleLine.layoutCode = vm.attendanceCode();
      vm.shareParam.titleLine.layoutName = vm.attendanceName();
      const positionText = vm.position() === 1 ? "上" : "下";
      vm.shareParam.titleLine.directText = vm.$i18n('KWR002_131') + vm.columnIndex() + vm.$i18n('KWR002_132') + positionText + vm.$i18n('KWR002_133');
      vm.shareParam.itemNameLine.displayFlag = vm.isDisplayItemName();
      vm.shareParam.itemNameLine.displayInputCategory = vm.isEnableTextEditor();
      vm.shareParam.itemNameLine.name = vm.attendanceItemName();
      vm.shareParam.attribute.selectionCategory = vm.isEnableComboBox();
      vm.shareParam.attribute.attributeList = [
        new AttendaceType(1, vm.$i18n('KWR002_141')),
        new AttendaceType(2, vm.$i18n('KWR002_142')),
        new AttendaceType(3, vm.$i18n('KWR002_143')),
        new AttendaceType(4, vm.$i18n('KWR002_180')),
        new AttendaceType(5, vm.$i18n('KWR002_181')),
        new AttendaceType(6, vm.$i18n('KWR002_182')),
        new AttendaceType(7, vm.$i18n('KWR002_183'))
      ]

      vm.shareParam.diligenceProjectList = [
        new DiligenceProject(1, '予定勤務種類', '', 0),
        new DiligenceProject(28, '勤務種類', '勤務種類', 28),
        new DiligenceProject(2, '予定就業時間帯', '予定就業時間帯', 2),
        new DiligenceProject(3, '予定出勤時刻1', '予定出勤時刻1', 3),
        new DiligenceProject(4, '予定退勤時刻1', '予定退勤時刻1', 4),
        new DiligenceProject(7, '予定休憩開始時刻1', '予定休憩開始時刻1', 7),
        new DiligenceProject(8, '予定休憩終了時刻1', '予定休憩終了時刻1', 8),
        new DiligenceProject(27, '予定時間', '予定時間', 27),
        new DiligenceProject(216, '残業１', '残業１', 216),
        new DiligenceProject(461, '勤務回数', '勤務回数', 461),
        new DiligenceProject(534, '休憩回数', '休憩回数', 534),
        new DiligenceProject(641, 'aaaaaaaaa回数', 'aaaaaaaaa回数', 641),
        new DiligenceProject(642, 'tukijikan回数', 'tukijikan回数', 642),
        new DiligenceProject(643, 'tukikin回数', 'tukikin回数', 643),
        new DiligenceProject(644, '出有ｵﾝ無ｵﾌ有ｶｳﾝﾄ（日次ﾄﾘｶﾞ）ｄ回数', '出有ｵﾝ無ｵﾌ有ｶｳﾝﾄ（日次ﾄﾘｶﾞ）ｄ回数', 644),
        new DiligenceProject(645, '出有ｵﾝ有ｵﾌ無ｶｳﾝﾄ（日次ﾄﾘｶﾞ）（bb）回数', '出有ｵﾝ有ｵﾌ無ｶｳﾝﾄ（日次ﾄﾘｶﾞ）（bb）回数', 645),
        new DiligenceProject(680, '任意項目４０回数', '任意項目４０回数', 680),
        new DiligenceProject(681, '任意項目４１回数', '任意項目４１回数', 681),
        new DiligenceProject(682, '任意項目４２月別回数', '任意項目４２月別回数', 682),
        new DiligenceProject(683, '任意項目４３回数', '任意項目４３回数', 683),
        new DiligenceProject(267, '振替休日１', '振替休日１', 267),
        new DiligenceProject(268, '計算休日出勤１回数', '計算休日出勤１回数', 268),
        new DiligenceProject(269, '計算振替休日１回数', '計算振替休日１回数', 269),
        new DiligenceProject(270, '事前休日出勤１', '事前休日出勤１', 270)
      ];
      
      vm.shareParam.diligenceProjectList.length
      setShared('attendanceItem', vm.shareParam, true);

      nts.uk.ui.windows.sub.modal('/view/kdl/048/index.xhtml').onClosed(() => {
        const attendanceItem = getShared('attendanceRecordExport');
        if (!attendanceItem) {
          return;
        }
        vm.attendanceItemName(attendanceItem.attendanceItemName);
        alert('Code selection in combo: ' +attendanceItem.attribute.selected + 'itemNameLine'+ attendanceItem.itemNameLine.name)
        vm.data(attendanceItem.selectedTimeList);
        vm.shareParam.selectedTimeList = attendanceItem.selectedTimeList;
        vm.shareParam.attribute.selected = attendanceItem.attribute.selected
        vm.shareParam.itemNameLine.name =  attendanceItem.itemNameLine.name
      });
    }

  }


    // Display object mock
    export class SharedParams {
      // タイトル行
      titleLine: TitleLineObject = new TitleLineObject();
      // 項目名行
      itemNameLine: ItemNameLineObject = new ItemNameLineObject();
      // 属性
      attribute: AttributeObject = new AttributeObject();
      // List<勤怠項目>
      diligenceProjectList: DiligenceProject[] = [];
      // List<選択済み勤怠項目>
      selectedTimeList:  SelectedTimeListParam[] = [];
      // 選択済み勤怠項目ID
      selectedTime: number;
  }
  export class SelectedTimeListParam {
      // 項目ID
      itemId: any | null = null;
      // 演算子
      operator: String | null = null;

      constructor(itemId: any, operator: String) {
        this.itemId = itemId;
        this.operator = operator;
    }
  }
  export class TitleLineObject {
      // 表示フラグ
      displayFlag: boolean = false;
      // 出力項目コード
      layoutCode: String | null = null;
      // 出力項目名
      layoutName: String | null = null;
      // コメント
      directText: String | null = null;
  }
  export class ItemNameLineObject {
      // 表示フラグ
      displayFlag: boolean = false;
      // 表示入力区分
      displayInputCategory: number = 1;
      // 名称
      name: String | null = null;
  }
  export class AttributeObject {
      // 選択区分
      selectionCategory: number = 2;
      // List<属性>
      attributeList: AttendaceType[] = [];
      // 選択済み
      selected: number = 1;
  }

  export class AttendaceType {
      attendanceTypeCode: number;
      attendanceTypeName: string;
      constructor(attendanceTypeCode: number, attendanceTypeName: string) {
          this.attendanceTypeCode = attendanceTypeCode;
          this.attendanceTypeName = attendanceTypeName;
      }
  }

  export class DiligenceProject {
      // ID
      id: any;
      // 名称
      name: any;
      // 属性
      attributes: any;
      // 表示番号
      indicatesNumber: any;

      constructor(id: any, name: any, attributes: any, indicatesNumber:any) {
          this.id = id;
          this.name = name;
          this.attributes = attributes;
          this.indicatesNumber = indicatesNumber;
      }
  }

}
