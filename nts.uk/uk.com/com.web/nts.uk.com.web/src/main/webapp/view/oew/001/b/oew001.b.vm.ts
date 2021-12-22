/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.com.view.oew001.b {
  import model = oew001.share.model;

  const API = {
    getEmployeeInfo: "com/screen/oew001/getEmployeeInfo/{0}",
    insert: "ctx/office/equipment/data/insert",
    update: "ctx/office/equipment/data/update",
    delete: "ctx/office/equipment/data/delete",
  };

  @bean()
  export class ScreenModel extends ko.ViewModel {

    data: KnockoutObservable<model.Oew001BData> = ko.observable(null);
    employeeList: KnockoutObservableArray<model.EmployeeInfoDto> = ko.observableArray([]);

    created(param: any) {
      const vm = this;
      vm.data(new model.Oew001BData(param));
      vm.data().employeeName = ko.observable(param.employeeName);
      vm.data().useDate = ko.observable(param.useDate);
      _.forEach(param.optionalItems, data => {
        if (param.isNewMode) {
          data.value = ko.observable("");
        } else if (data.itemCls === model.enums.ItemClassification.NUMBER && !!data.value) {
          data.value = ko.observable(Number(data.value));
        } else {
          data.value = ko.observable(_.isNil(data.value) ? "" : data.value);
        }
        data.helpContent = vm.buildHelpContent(data);
      });
      vm.data().optionalItems = ko.observableArray(param.optionalItems);
      vm.data.valueHasMutated();

      // Add constraint
      _.each(vm.data().optionalItems(), data => __viewContext.primitiveValueConstraints[data.constraintName] = data.constraint);
      vm.data().useDate.subscribe(value => vm.validateUseDate(moment.utc(value, model.constants.YYYY_MM_DD)));
    }

    mounted() {
      const vm = this;
      vm.$blockui("grayout");
      vm.getEmployeeInfo().then(() => {
        vm.data().employeeName(_.find(vm.employeeList(), { "employeeId": vm.data().sid }).businessName);
        vm.data.valueHasMutated();
      })
      .always(() => {
        if (vm.data().isNewMode) {
          $("#B2_2").focus();
        } else {
          $(".B2_2_1")[0].focus();
        }
        vm.$blockui("clear");
      });
    }

    private getEmployeeInfo(): JQueryPromise<any> {
      const vm = this;
      return vm.$ajax(nts.uk.text.format(API.getEmployeeInfo, vm.data().sid)).then(result => vm.employeeList(result));
    }

    /**
     * Ｂ：利用実績の新規登録をする
     */
    private insert(param: model.EquipmentDataDto): JQueryPromise<any> {
      const vm = this;
      return vm.$ajax(API.insert, param)
        .then(() => {
          return vm.$dialog.info({ messageId: "Msg_15" }).then(() => vm.$window.close({
            isSaveSuccess: true
          }));
        }, err => {
          if (!!err.messageId) {
            return vm.$dialog.error({ messageId: err.messageId });
          } else if (!!err.errors) {
            return (nts.uk.ui.dialog as any).bundledErrors(err);
          }
        });
    }

    /**
     * Ｂ：利用実績の更新をする
     */
    private update(param: model.EquipmentDataDto): JQueryPromise<any> {
      const vm = this;
      return vm.$ajax(API.update, param)
      .then(() => {
        return vm.$dialog.info({ messageId: "Msg_15" }).then(() => vm.$window.close({
          isSaveSuccess: true
        }));
      }, err => {
        if (!!err.messageId) {
          return vm.$dialog.error({ messageId: err.messageId }).then(() => vm.$window.close({
            isMsg2319: err.messageId === "Msg_2319"
          }));
        } else if (!!err.errors) {
          return (nts.uk.ui.dialog as any).bundledErrors(err);
        }
      });
    }

    /**
     * Ｂ：利用実績の削除をする
     */
    private delete(param: any): JQueryPromise<any> {
      const vm = this;
      return vm.$ajax(API.delete, param)
      .then(() => {
        return vm.$dialog.info({ messageId: "Msg_16" });
      })
      .fail(err => {
        return vm.$dialog.error({ messageId: err.messageId });
      });
    }

    public processSave() {
      const vm = this;
      vm.$validate().then(isValid => {
        if (!isValid) {
          return;
        }

        vm.$blockui("grayout");
        const input = vm.data();
        const itemDatas = _.map(input.optionalItems(), data => new model.ItemDataDto({
          itemNo: data.itemNo,
          itemClassification: data.itemCls,
          actualValue: data.value()
        }));
        const param = new model.EquipmentDataDto({
          equipmentClassificationCode: input.equipmentClsCode,
          equipmentCode: input.equipmentInfoCode,
          inputDate: vm.data().isNewMode ? moment.utc().toISOString() : moment.utc(input.inputDate).toISOString(),
          useDate: moment.utc(input.useDate(), model.constants.YYYY_MM_DD).toISOString(),
          sid: input.sid,
          itemDatas: itemDatas
        });
        let call = vm.data().isNewMode ? vm.insert(param) : vm.update(param);
        call.always(() => vm.$blockui("clear"));
      });
    }

    public processDelete() {
      const vm = this;
      vm.$dialog.confirm({ messageId: "Msg_18" }).then((result: "yes" | "no") => {
        if (result === "yes") {
          vm.$blockui("grayout");
          const input = vm.data();
          const param = {
            equipmentCode: input.equipmentInfoCode,
            sid: vm.data().sid,
            inputDate: moment.utc(input.inputDate).toISOString(),
            useDate: moment.utc(input.useDate(), model.constants.YYYY_MM_DD).toISOString(),
          };
          vm.delete(param).then(() => vm.$window.close({
            isDeleteSuccess: true
          }))
          .always(() => vm.$blockui("clear"));
        }
      });
    }

    public processCancel() {
      const vm = this;
      vm.$window.close();
    }

    private buildHelpContent(item: model.OptionalItem): string {
      const vm = this;
      let temp: string;
      let maximum: string, minimum: string;
      switch(item.itemCls) {
        case model.enums.ItemClassification.TEXT:
          temp = `${vm.$i18n("OEW001_63")}　${item.constraint.maxLength}${vm.$i18n("OEW001_64")}\n${vm.$i18n("OEW001_67")}${item.memo}`;
          break;
        case model.enums.ItemClassification.NUMBER:
          maximum = nts.uk.ntsNumber.formatNumber(Number(item.constraint.max), { formatId: 'Number_Separated' });
          minimum = nts.uk.ntsNumber.formatNumber(Number(item.constraint.min), { formatId: 'Number_Separated' });
          temp = `${vm.$i18n("OEW001_65")}　${minimum}　${vm.$i18n("OEW001_66")}　${maximum}\n${vm.$i18n("OEW001_67")}${item.memo}`;
          break;
        case model.enums.ItemClassification.TIME:
          maximum = String(item.constraint.max);
          minimum = String(item.constraint.min);
          temp = `${vm.$i18n("OEW001_65")}　${minimum}　${vm.$i18n("OEW001_66")}　${maximum}\n${vm.$i18n("OEW001_67")}${item.memo}`;
          break;
      }
      return temp;
    }

    private validateUseDate(date: moment.Moment) {
      const vm = this;
      if (date.isBefore(vm.data().validStartDate) || date.isAfter(vm.data().validEndDate)) {
        vm.$dialog.error({ messageId: "Msg_2233" });
      }
    }
  }
}