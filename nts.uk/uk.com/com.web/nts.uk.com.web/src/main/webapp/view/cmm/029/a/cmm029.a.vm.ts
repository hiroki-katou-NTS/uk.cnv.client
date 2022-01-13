/// <reference path='../../../../lib/nittsu/viewcontext.d.ts' />

module nts.uk.com.view.cmm029.a {

  const API = {
    initDisplay: "com/screen/cmm029/initDisplay",
    register: "com/screen/cmm029/register",
  };

  @bean()
  export class ScreenModel extends ko.ViewModel {

    enableRegister: KnockoutComputed<boolean>;

    tabs: KnockoutObservableArray<any>;
    selectedTab: KnockoutObservable<string> = ko.observable("tab-1");

    useAtrValuesA1: KnockoutObservableArray<any>;
    displayDatas: KnockoutObservableArray<DisplayData> = ko.observableArray([]);
    taskOperationMethods: KnockoutObservableArray<any>;

    constructor() {
      super();
      const vm = this;
      vm.tabs = ko.observableArray([
        { id: 'tab-1', title: vm.$i18n("CMM029_3"), content: '.tab-content-1' },
        { id: 'tab-2', title: vm.$i18n("CMM029_4"), content: '.tab-content-2' },
        { id: 'tab-3', title: vm.$i18n("CMM029_5"), content: '.tab-content-3' },
        { id: 'tab-4', title: vm.$i18n("CMM029_6"), content: '.tab-content-4' },
      ]);
      vm.useAtrValuesA1 = ko.observableArray([
        { value: true, name: vm.$i18n("CMM029_69") },
        { value: false, name: vm.$i18n("CMM029_70") },
      ]);
      vm.taskOperationMethods = ko.observableArray([
        { value: '2', name: vm.$i18n("CMM029_66") },
        { value: '0', name: vm.$i18n("CMM029_67") },
        { value: '1', name: vm.$i18n("CMM029_68") },
      ]);
      vm.enableRegister = ko.computed(() => _.includes(["tab-1", "tab-4"], vm.selectedTab()));
    }

    mounted() {
      const vm = this;
      vm.initDisplay();
    }

    // 表示初期データを取得する
    private initDisplay(): JQueryPromise<any> {
      const vm = this;
      vm.$blockui("grayout");
      return vm.$ajax(API.initDisplay).then((data: IDisplayData[]) => vm.displayDatas(_.map(data, d => new DisplayData(d))))
        .then(() => vm.$nextTick(() => vm.displayDataOf("CMM029_42").taskOperationMethod.valueHasMutated()))
        .always(() => vm.$blockui("clear"));
    }

    // 設定機能を登録する
    public performRegister() {
      const vm = this;
      vm.$blockui("grayout");
      const param = {
        datas: ko.toJS(vm.displayDatas())
      };
      vm.$ajax(API.register, param).then(() => vm.$dialog.info({ messageId: "Msg_15" }))
        .then(() => vm.initDisplay())
        .always(() => vm.$blockui("clear"));
    }

    public performHyperlink(element: any) {
      const vm = this;
      const programId: string = $(element).data("program");
      const id = $(element).attr("id");
      const displayData = _.find(vm.displayDatas(), { programId: programId, system: 1 });

      if (id === "A4_214") {
        const temp = _.find(vm.displayDatas(), { programId: "CMM029_42", system: 1 });
        if (Number(temp.taskOperationMethod()) !== 1) return;
      }
      if (_.isNil(displayData)) return;
      if (!nts.uk.text.isNullOrEmpty(displayData.errorMessage)) {
        vm.$dialog.error({ messageId: displayData.errorMessage });
      } else if (!nts.uk.text.isNullOrEmpty(displayData.url)) {
        var webApp: nts.uk.ui.vm.WEB_APP, url: string;
        // Check if target is in com.web
        if (displayData.url.indexOf("/nts.uk.com.web") > -1) {
          webApp = "com";
          url = displayData.url.replace("/nts.uk.com.web", "");
        } 
        // Check if target is in at.web
        else if (displayData.url.indexOf("/nts.uk.at.web") > -1) {
          webApp = "at";
          url = displayData.url.replace("/nts.uk.at.web", "");
        }
        vm.$jump(webApp, url);
      }
    }

    public displayDataOf(programId: string): DisplayData {
      const vm = this;
      return _.find(vm.displayDatas(), { programId: programId });
    }
  }

  export class DisplayData {

    // システム
    system: number;

    // プログラムID
    programId: string;

    // URL
    url: string;

    // エラーメッセージ
    errorMessage: string;

    // 作業運用方法
    taskOperationMethod: KnockoutObservable<string>;

    // 利用区分
    useAtr: KnockoutObservable<boolean>;

    constructor(data: IDisplayData) {
      this.system = data.system;
      this.programId = data.programId;
      this.url = data.url;
      this.errorMessage = data.errorMessage;
      this.taskOperationMethod = ko.observable(String(data.taskOperationMethod));
      this.useAtr = ko.observable(data.useAtr);

      this.taskOperationMethod.subscribe(value => {
        if (Number(value) === 1) {
          $("#A4_214").css("cursor", "pointer");
        } else {
          $("#A4_214").css("cursor", "auto");
        }
      });
    }

    // Return useAtr text for tab 3 and 4
    public getUseValue(element: any): string {
      const className = $(element).attr("class");
      if (className.indexOf("manage-remain-number") > -1) {
        return this.useAtr() ? "CMM029_71" : "CMM029_72";
      } else {
        return this.useAtr() ? "CMM029_69" : "CMM029_70";
      }
    }
  }

  export interface IDisplayData {

    // システム
    system: number;

    // プログラムID
    programId: string;

    // URL
    url: string;

    // エラーメッセージ
    errorMessage: string;

    // 作業運用方法
    taskOperationMethod: number;

    // 利用区分
    useAtr: boolean;
  }
}