/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.com.view.oem002.a {

  const API = {
    insert: "ctx/office/equipment/information/insert",
    update: "ctx/office/equipment/information/update",
    delete: "ctx/office/equipment/information/delete/{0}",
    initScreen: "com/screen/oem002/initScreen",
    getEquipmentInfo: "com/screen/oem002/getEquipmentInfo/{0}",
  };

  @bean()
  export class ScreenModel extends ko.ViewModel {

    equipmentInfoList: KnockoutObservableArray<EquipmentInformationDto> = ko.observableArray([]);
    equipmentClsList: KnockoutObservableArray<EquipmentClassificationDto> = ko.observableArray([]);
    selectedEquipmentInfo: KnockoutObservable<EquipmentInformation> = ko.observable(new EquipmentInformation());
    selectedEquipmentInfoCode: KnockoutObservable<string> = ko.observable(null);
    columns: KnockoutObservableArray<any> = ko.observableArray([]);
    isNewMode: KnockoutObservable<boolean> = ko.observable(true).extend({ notify: 'always' });

    created() {
      const vm = this;
      vm.columns([
        { headerText: vm.$i18n("OEM002_9"), key: 'equipmentClsName', width: 150 },
        { headerText: vm.$i18n("OEM002_10"), key: 'code', width: 70 },
        { headerText: vm.$i18n("OEM002_11"), key: 'name', width: 200 },
      ]);

      vm.selectedEquipmentInfoCode.subscribe(value => {
        if (value) {
          const api = nts.uk.text.format(API.getEquipmentInfo, value);
          vm.$ajax(api).then(result => {
            vm.isNewMode(false);
            vm.$errors("clear");
            vm.selectedEquipmentInfo(EquipmentInformation.createFromDto(result, vm.equipmentClsList()));

            vm.fixReadonlyA2_9();
          })
        } else {
          vm.isNewMode(true);
          vm.selectedEquipmentInfo(new EquipmentInformation());
          vm.selectedEquipmentInfo.valueHasMutated();

          vm.fixReadonlyA2_9();
        }
      });
      vm.isNewMode.subscribe(() => vm.$nextTick(() => $("#A2_10").focus()));
    }

    mounted() {
      const vm = this;
      vm.$blockui("grayout");
      vm.getAll().always(() => vm.$blockui("clear"));
      $("#A2_10").focus();
      vm.$nextTick(() => vm.fixReadonlyA2_9());
    }

    private getAll(selectedCode?: string): JQueryPromise<any> {
      const vm = this;
      return vm.$ajax(API.initScreen).then(result => {
        vm.isNewMode.valueHasMutated();
        vm.equipmentClsList(result.equipmentClassificationList);
        _.forEach(result.equipmentInformationList, data => data.equipmentClsName = _.find(vm.equipmentClsList(), { code: data.equipmentClsCode }).name);
        vm.equipmentInfoList(result.equipmentInformationList);
        if (vm.equipmentInfoList().length > 0) {
          if (!!selectedCode) {
            vm.selectedEquipmentInfoCode(selectedCode);
          } else {
            vm.selectedEquipmentInfoCode(vm.equipmentInfoList()[0].code);
          }
        }
      });
    }

    public resetNewMode(): void {
      const vm = this;
      vm.isNewMode(true);
      vm.selectedEquipmentInfoCode(null);
      vm.selectedEquipmentInfoCode.valueHasMutated();
    }

    public processSave(): void {
      const vm = this;
      vm.$validate().then(isValid => {
        if (isValid && !nts.uk.ui.errors.hasError()) {
          vm.$blockui("grayout");
          const param = EquipmentInformationDto.createFromData(vm.selectedEquipmentInfo());
          const code = param.code;
          const api = vm.isNewMode() ? API.insert : API.update;
          vm.$ajax(api, param)
              .then(() => vm.$dialog.info({ messageId: "Msg_15" }).then(() => vm.getAll(code)))
              .fail(err => vm.$dialog.error({ messageId: err.messageId }))
              .always(() => vm.$blockui("clear"));
        }
      });
    }

    public processDelete(): void {
      const vm = this;
      vm.$dialog.confirm({ messageId: "Msg_18" }).then((result: "yes" | "no") => {
        if (result === "yes") {
          vm.$blockui("grayout");
          const length = vm.equipmentInfoList().length;
          const index = _.findIndex(vm.equipmentInfoList(), { "code": vm.selectedEquipmentInfoCode() });
          const api = nts.uk.text.format(API.delete, vm.selectedEquipmentInfoCode());
          vm.$ajax(api).then(() => {
            vm.$dialog.info({ messageId: "Msg_16" }).then(() => {
              const newIndex = length === 1 ? null : (length === index + 1 ? index - 1 : index + 1);
              if (!!newIndex) {
                return vm.getAll(vm.equipmentInfoList()[newIndex].code);
              } else {
                return vm.getAll().then(() => vm.resetNewMode());
              }
            })
          }).always(() => vm.$blockui("clear"));
        }
      });
    }

    public processExport(): void {
      const vm = this;
      vm.$blockui("grayout");
      const program = nts.uk.ui._viewModel.kiban.programName().split(" ");
      let domainType = "OEM002_設備の登録";
      if (program.length > 1) {
        program.shift();
        domainType = domainType + program.join(" ");
      }
      nts.uk.request.exportFile('/masterlist/report/print', {
        domainId: "EquipmentInformation",
        domainType: domainType,
        languageId: 'ja', reportType: 0,
      }).fail(err => vm.$dialog.alert({ messageId: err.messageId }))
        .always(() => vm.$blockui("clear"));
    }

    public openDialogB(): void {
      const vm = this;
      vm.$window.modal("/view/oem/002/b/index.xhtml", vm.selectedEquipmentInfo().equipmentClsCode())
        .then(result => {
          if (result) {
            vm.selectedEquipmentInfo().equipmentClsCode(result.code);
            vm.selectedEquipmentInfo().equipmentClsName(result.name);
            vm.$validate("#A2_9");
            vm.fixReadonlyA2_9();
          }
        });
    }
    
    private fixReadonlyA2_9() {
      const vm = this;
      // Fix bug A2_9
      // Avoid using readonly attr because of tabindex
      vm.$nextTick(() => $("#A2_9").attr("readonly", "readonly"));
    }
  }

  export class EquipmentInformationDto {
    /**
     * コード
     */
    code: string;
    
    /**
     * 名称
     */
    name: string;
    
    /**
     * 有効開始日
     */
    effectiveStartDate: string;
    
    /**
     * 有効終了日
     */
    effectiveEndDate: string;
    
    /**
     * 備分類コード
     */
    equipmentClsCode: string;
    
    /**
     * 備考
     */
    remark: string;

    equipmentClsName: string;

    public static createFromData(data: EquipmentInformation): EquipmentInformationDto {
      let dto = new EquipmentInformationDto();
      dto.code = data.code();
      dto.effectiveStartDate = moment.utc(data.validPeriod().startDate, "YYYY/MM/DD").toISOString();
      dto.effectiveEndDate = moment.utc(data.validPeriod().endDate, "YYYY/MM/DD").toISOString();
      dto.equipmentClsCode = data.equipmentClsCode();
      dto.name = data.name();
      dto.remark = data.remark();
      return dto;
    }
  }

  export class EquipmentClassificationDto {
    /**
     * コード
     */
     code: string;
    
     /**
      * 名称
      */
     name: string;
  }

  export class EquipmentInformation {
    code: KnockoutObservable<string> = ko.observable(null);
    name: KnockoutObservable<string> = ko.observable(null);
    validPeriod: KnockoutObservable<any> = ko.observable({
      startDate: null,
      endDate: moment.utc("9999/12/31", "YYYY/MM/DD")
    });
    equipmentClsCode: KnockoutObservable<string> = ko.observable(null);
    equipmentClsName: KnockoutObservable<string> = ko.observable(null);
    remark: KnockoutObservable<string> = ko.observable(null);

    public static createFromDto(data: EquipmentInformationDto, clsList: EquipmentClassificationDto[]): EquipmentInformation {
      let info = new EquipmentInformation();
      info.code(data.code);
      info.name(data.name);
      info.validPeriod({
        startDate: moment.utc(data.effectiveStartDate, "YYYY/MM/DD"),
        endDate: moment.utc(data.effectiveEndDate ?? "9999/12/31", "YYYY/MM/DD")
      });
      info.equipmentClsCode(data.equipmentClsCode);
      info.equipmentClsName(_.find(clsList, { "code" : data.equipmentClsCode }).name);
      info.remark(data.remark);
      return info;
    }
  }
}