/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.com.view.oew001.a {
  import model = oew001.share.model;

  const API = {
    initEquipmentInfo: "com/screen/oew001/initEquipmentInfo",
    initEquipmentSetting: "com/screen/oew001/initEquipmentSetting",
    getResultHistory: "com/screen/oew001/getResultHistory",
    getEquipmentInfoList: "com/screen/oew001/getEquipmentInfoList",
  };

  @bean()
  export class ScreenModel extends ko.ViewModel {
    selectedEquipmentClsCode: KnockoutObservable<string> = ko.observable("");
    equipmentClsName: KnockoutObservable<string> = ko.observable("");
    selectedEquipmentInfoCode: KnockoutObservable<string> = ko.observable("");
    yearMonth: KnockoutObservable<string> = ko.observable("");
    hasExtractData: KnockoutObservable<boolean> = ko.observable(false);
    isInitComplete: boolean = false;
    isErr3251: KnockoutObservable<boolean> = ko.observable(false);

    // dto
    equipmentClassification: KnockoutObservable<model.EquipmentClassificationDto> = ko.observable(null);
    equipmentInformationList: KnockoutObservableArray<model.EquipmentInformationDto> = ko.observableArray([]);
    itemSettings: KnockoutObservableArray<model.EquipmentUsageRecordItemSettingDto> = ko.observableArray([]);
    formatSetting: KnockoutObservable<model.EquipmentPerformInputFormatSettingDto> = ko.observable(null);

    // grid
    columns: KnockoutObservableArray<any> = ko.observableArray([]);
    staticColumns: any[];
    dataSource: KnockoutObservableArray<DisplayData> = ko.observableArray([]);
    optionalItems: KnockoutObservableArray<model.OptionalItem> = ko.observableArray([]);

    created() {
      const vm = this;
      vm.staticColumns = [
        { headerText: "", key: "id", dataType: "string", ntsControl: "Label" },
        { headerText: "", key: "editRecord", width: "85px", dataType: "boolean", ntsControl: "EditButton" },
        { headerText: vm.$i18n("OEW001_19"), key: "useDate", width: "100px", dataType: "string" },
        { headerText: vm.$i18n("OEW001_20"), key: "employeeName", width: "150px", dataType: "string" }
      ];
      vm.columns.push(_.clone(vm.staticColumns));

      // subscribe
      vm.equipmentClassification.subscribe(value => {
        vm.selectedEquipmentClsCode(value.code);
        vm.equipmentClsName(value.name);
        vm.performSearchData();
      });

      vm.selectedEquipmentInfoCode.subscribe(() => vm.focusOnItemAfterInit("#A4_3"));

      vm.yearMonth.subscribe(() => vm.focusOnItemAfterInit("#A4_3"));
    }

    mounted() {
      const vm = this;
      vm.yearMonth(moment.utc().format("YYYYMM"));
      vm.initScreen();
    }

    // Ａ：設備利用実績の一覧の初期表示
    private initScreen() {
      const vm = this;
      vm.$blockui("grayout");
      vm.restoreCharacteristic().then(result => vm.initEquipmentInfo(result)).then(() => vm.initEquipmentSetting())
      .then(() => vm.performSearchData())
      .always(() => {
        vm.isInitComplete = true;
        $("#A5_1").focus();
        vm.$blockui("clear");
      });
    }

    // Ａ1：設備分類と設備情報を取得する
    private initEquipmentInfo(param: any): JQueryPromise<any> {
      const vm = this;
      return vm.$ajax(API.initEquipmentInfo, param).then(result => {
        if (result) {
          vm.equipmentClassification(result.equipmentClassification);
          vm.equipmentInformationList(result.equipmentInformationList);
          vm.selectedEquipmentInfoCode(param.equipmentCode);
        }
      }, err => vm.$dialog.error({ messageId: err.messageId }));
    }

    // Ａ2：「設備利用実績の項目設定」を取得する
    private initEquipmentSetting(): JQueryPromise<any> {
      const vm = this;
      vm.columns(_.clone(vm.staticColumns));
      return vm.$ajax(API.initEquipmentSetting).then(result => {
        if (result) {
          vm.itemSettings(result.itemSettings);
          vm.formatSetting(result.formatSetting);
        }
      }, err => {
        vm.$dialog.error({ messageId: err.messageId });
        vm.isErr3251(err.messageId === "Msg_3251");
      });
    }

    // Ａ3：「実績の抽出処理」を実行する
    private searchData(param: SearchDataParam): JQueryPromise<any> {
      const vm = this;
      const currentSid = __viewContext.user.employeeId;
      return vm.$ajax(API.getResultHistory, param).then(result => {
        if (_.isEmpty(result.equipmentDatas)) {
          vm.dataSource([]);
          let temp = new DisplayData();
          temp.createOptionalItems(null, vm.itemSettings(), vm.formatSetting());
          vm.createColumns(temp.optionalItems);
          return;
        }
        vm.hasExtractData(true);
        // Create data source
        let dataSource = _.map(result.equipmentDatas, (data: model.EquipmentDataDto) => {
          let displayData = new DisplayData(data, result.employeeInfos, currentSid);
          displayData.createOptionalItems(data, vm.itemSettings(), vm.formatSetting());
          return displayData;
        });
        // Sort by 日付、社員CD、入力日
        dataSource = _.orderBy(dataSource, ['useDate', 'scd', 'inputDate'], ['asc', 'asc', 'asc']);
        vm.dataSource(dataSource);
        if (vm.dataSource().length > 0) {
          // Get first data for column format reference
          const optionalItems = dataSource[0].optionalItems;
          vm.createColumns(optionalItems);
        }
      })
    }

    private initGrid() {
      const vm = this;
      // Reset grid
      if ($("#A6").data("igGrid")) {
        $("#A6").ntsGrid("destroy");
      }
      // Reset cache
      localStorage.removeItem(model.constants.NTS_GRID_CACHE_KEY);
      const cellStates = _.chain(vm.dataSource()).map(data => {
        if (!data.editRecord) {
          return new CellState(data.id, "editRecord", ['disabled']);
        }
      }).filter(data => !!data).value();
      const maxWidth = _.chain(vm.columns()).map(data => Number(data.width?.substring(0, data.width.length - 2) | 0)).sum().value();
      let param: any = {
        height: '316px',
        rows: 10,
        dataSource: vm.dataSource(),
        primaryKey: 'id',
        rowVirtualization: true,
        virtualization: true,
        virtualizationMode: 'continuous',
        hidePrimaryKey: true,
        columns: vm.columns(),
        ntsControls: [
          { name: 'EditButton', text: vm.$i18n("OEW001_21"), click: (item: any) => vm.openDialogB(item), controlType: 'Button', enable: true }
        ],
        features: [
          {
            name: 'Selection',
            mode: 'row',
            multipleSelection: false
          },
        ],
        ntsFeatures: [
          {
            name: 'CellState',
            rowId: 'rowId',
            columnKey: 'columnKey',
            state: 'state',
            states: cellStates
          },
        ]
      };

      // Set grid maxWidth
      if (maxWidth > model.constants.MAXIMUM_GRID_WIDTH) {
        param.width = `${model.constants.MAXIMUM_GRID_WIDTH}px`;
      }
      $("#A6").ntsGrid(param);
      // Set disable A8_1
      $("#A6_container").ready(() => $("#A6 .disabled button").attr("disabled", "disabled"));
    }

    public openDialogB(input?: any) {
      const vm = this;
      const selectedEquipmentInfo = _.find(vm.equipmentInformationList(), { code: vm.selectedEquipmentInfoCode() });
      const param = new model.Oew001BData({
        isNewMode: !input,
        equipmentClsCode: vm.selectedEquipmentClsCode(),
        equipmentClsName: vm.equipmentClsName(),
        equipmentInfoCode: vm.selectedEquipmentInfoCode(),
        equipmentInfoName: selectedEquipmentInfo.name,
        employeeName: ko.observable(null),
        validStartDate: moment.utc(selectedEquipmentInfo.effectiveStartDate, model.constants.YYYY_MM_DD),
        validEndDate: moment.utc(selectedEquipmentInfo.effectiveEndDate, model.constants.YYYY_MM_DD),
      });
      if (param.isNewMode) {
        param.inputDate = moment.utc().format(model.constants.YYYY_MM_DD);
        param.useDate = ko.observable(moment.utc().format(model.constants.YYYY_MM_DD));
        param.optionalItems = ko.observableArray(vm.optionalItems());
        param.sid = __viewContext.user.employeeId;
      } else {
        const item = _.find(vm.dataSource(), { id: input.id });
        param.inputDate = item.inputDate;
        param.useDate = ko.observable(item.useDate);
        param.optionalItems = ko.observableArray(item.optionalItems);
        param.sid = item.sid;
      }
      
      vm.$window.modal("/view/oew/001/b/index.xhtml", ko.toJS(param))
        .then((result: any) => {
          if (!!result) {
            let topPos: number;
            if (!param.isNewMode) {
              topPos = $("#A6_scrollContainer").scrollTop();
            }
            vm.performSearchData(param.isNewMode && result.isSaveSuccess, topPos);
            if (result.isSaveSuccess) {
              vm.saveCharacteristic();
            }
          }
          vm.$nextTick(() => $("#A5_1").focus());
        });
    }

    public openDialogC() {
      const vm = this;
      const param = {
        equipmentClsCode: vm.selectedEquipmentClsCode(),
        equipmentClsName: vm.equipmentClsName(),
        equipmentCode: vm.selectedEquipmentInfoCode(),
        formatSetting: vm.formatSetting(),
        itemSettings: vm.itemSettings(),

        yearMonth: vm.yearMonth()
      }
      vm.$window.modal("/view/oew/001/c/index.xhtml", param);
    }

    public openDialogD() {
      const vm = this;
      const paramDialogD = {
        equipmentClsCode: vm.selectedEquipmentClsCode(),
        isOpenFromA: true
      }
      vm.$window.modal("/view/oew/001/d/index.xhtml", paramDialogD)
        .then(result => {
          if (!!result) {
            vm.$blockui("grayout");
            vm.selectedEquipmentClsCode(result.code);
            vm.equipmentClsName(result.name);

            const param = {
              equipmentClsCode: vm.selectedEquipmentClsCode(),
              baseDate: moment.utc().toISOString(),
              isInput: true
            };
            vm.$ajax(API.getEquipmentInfoList, param).then(result => vm.equipmentInformationList(result)).always(() => vm.$blockui("clear"));
            vm.$nextTick(() => $("#A4_3").focus());
          }
        });
    }

    public performSearchData(isScrollBottom: boolean = true, topPos: number = 0) {
      const vm = this;
      vm.$blockui("grayout");
      const param: SearchDataParam = new SearchDataParam(vm.selectedEquipmentClsCode(), vm.selectedEquipmentInfoCode(), vm.yearMonth());
      vm.searchData(param).then(() => vm.initGrid()).then(() => {
        if (isScrollBottom) {
          // #120568
          $("#A6").igGrid("virtualScrollTo", vm.dataSource().length - 1);
        } else {
          vm.$nextTick(() => $("#A6").igGrid("virtualScrollTo", topPos + "px"));
        }
      }).always(() => vm.$blockui("clear"));
    }

    private createColumns(optionalItems: model.OptionalItem[]) {
      const vm = this;
      // Init default optionalItems
      vm.optionalItems(_.clone(optionalItems));
      // Create column info
      vm.columns(_.clone(vm.staticColumns));
      _.each(optionalItems, data => vm.columns().push(vm.getColumnHeader(data)));
    }
    
    private saveCharacteristic() {
      const vm = this;
      (nts.uk as any).characteristics.save("OEW001_設備利用実績の入力"
      + "_companyId_" + __viewContext.user.companyId
      + "_userId_" + __viewContext.user.employeeId, {
        equipmentCode: vm.selectedEquipmentInfoCode(),
        equipmentClsCode: vm.selectedEquipmentClsCode()
      });
    }

    private restoreCharacteristic(): JQueryPromise<any> {
      const vm = this;
      return (nts.uk as any).characteristics.restore("OEW001_設備利用実績の入力"
      + "_companyId_" + __viewContext.user.companyId
      + "_userId_" + __viewContext.user.employeeId).then((result: any) => {
        vm.selectedEquipmentClsCode(result?.equipmentClsCode || null);
        vm.selectedEquipmentInfoCode(result?.equipmentCode || null);

        if (!result) {
          return {
            equipmentCode: '',
            equipmentClsCode: ''
          };
        } else {
          return result;
        }
      });
    }

    private getColumnHeader(data: model.OptionalItem): any {
      const key = `value${data.displayOrder}`;
      const headerText = nts.uk.text.isNullOrEmpty(data.unit) ? data.itemName : `${data.itemName}(${data.unit})`;
      const cssClass = "limited-label " + (data.itemCls === model.enums.ItemClassification.TEXT ? "halign-left" : "halign-right");
      const columnHeader = { 
        headerText: headerText, 
        template: `<div class="${cssClass}">` + '${' + key + '}</div>',
        dataType: "String", key: `${key}`, width: data.width
      };
      if (data.itemCls !== model.enums.ItemClassification.TEXT) {

      }
      return columnHeader;
    }

    private focusOnItemAfterInit(itemId: string) {
      const vm = this;
      if (vm.isInitComplete) {
        $(itemId).focus();
        vm.performSearchData();
      }
    }
  }

  export class DisplayData {
    id: string = nts.uk.util.randomId();
    editRecord: boolean;
    inputDate: string;
    useDate: string;
    sid: string;
    scd: string;
    employeeName: string;
    optionalItems: model.OptionalItem[];

    constructor(data?: model.EquipmentDataDto, employees?: model.EmployeeInfoDto[], currentSid?: string) {
      if (!!data && !!currentSid) {
        const employeeInfo = _.find(employees, { employeeId: data.sid });
        this.editRecord = data.sid === currentSid || __viewContext.user.role.isInCharge.attendance;
        this.inputDate = data.inputDate;
        this.useDate = data.useDate;
        this.sid = data.sid;
        this.scd = employeeInfo.employeeCode;
        this.employeeName = employeeInfo.businessName;
      }
    }

    // Create grid optional datas from acquired datas
    public createOptionalItems(data: model.EquipmentDataDto, itemSettings: model.EquipmentUsageRecordItemSettingDto[],
      formatSetting: model.EquipmentPerformInputFormatSettingDto): void {
      if (itemSettings.length === 0 || !formatSetting.itemDisplaySettings) {
        this.optionalItems = [];
        return;
      }

      // Map datas to grid
      this.optionalItems = _.chain(itemSettings).map(itemSetting => {
        const itemData = !!data ? _.find(data.itemDatas, itemData => itemData.itemNo === itemSetting.itemNo 
          && !nts.uk.text.isNullOrEmpty(itemData.actualValue) && itemData.actualValue !== "null") : null;
        const itemDisplay = _.find(formatSetting.itemDisplaySettings, { itemNo: itemSetting.itemNo });
        const itemCls = itemSetting.inputControl.itemCls;
        let actualValue = !!itemData ? itemData.actualValue : null;

        const constraint: ui.vm.Constraint = {
          valueType: itemCls === model.enums.ItemClassification.TEXT ? "String" : (itemCls === model.enums.ItemClassification.NUMBER ? "Integer" : "Clock"),
          charType: itemCls === model.enums.ItemClassification.NUMBER ? "Numeric" : "Any",
          min: itemCls === model.enums.ItemClassification.TIME ? (nts.uk.time as any).format.byId("Time_Short_HM", itemSetting.inputControl.minimum) : itemSetting.inputControl.minimum,
          max: itemCls === model.enums.ItemClassification.TIME ? (nts.uk.time as any).format.byId("Time_Short_HM", itemSetting.inputControl.maximum) : itemSetting.inputControl.maximum,
          maxLength: itemSetting.inputControl.digitsNo
        };
        const width = itemDisplay.displayWidth * model.constants.FIXED_VALUE_A;
        
        const optionalItem = new model.OptionalItem({
          itemName: itemSetting.items.itemName,
          itemCls: itemCls,
          value: ko.observable(actualValue),
          width: `${Math.min(model.constants.MAXIMUM_COL_WIDTH, width)}px`,
          displayOrder: itemDisplay.displayOrder,
          constraint: constraint,
          constraintName: `CustomContraint${itemDisplay.displayOrder}`,
          itemNo: itemSetting.itemNo,
          memo: itemSetting.items.memo,
          required: itemSetting.inputControl.require,
          unit: itemSetting.items.unit
        });

        // Format value to display in grid
        if (actualValue !== null) {
          if (itemCls === model.enums.ItemClassification.NUMBER) {
            actualValue = nts.uk.ntsNumber.formatNumber(Number(actualValue), { formatId: 'Number_Separated' });
          } else if (itemCls === model.enums.ItemClassification.TIME) {
            actualValue = (nts.uk.time as any).format.byId("Time_Short_HM", actualValue);
          }
        }
        (this as any)["value" + itemDisplay.displayOrder] = actualValue;
        return optionalItem;
        // Sort optionalItems by 表示順番
      }).orderBy("displayOrder").value();
    }
  }

  export class SearchDataParam {
    equipmentClsCode: string;
    equipmentCode: string;
    ym: string;

    constructor(equipmentClsCode: string, equipmentCode: string, ym: string) {
      this.equipmentClsCode = equipmentClsCode;
      this.equipmentCode = equipmentCode;
      this.ym = ym;
    }
  }

  export class CellState {
    rowId: number;
    columnKey: string;
    state: any[];
    constructor(rowId: any, columnKey: string, state: any[]) {
        this.rowId = rowId;
        this.columnKey = columnKey;
        this.state = state;
    }
}
}
