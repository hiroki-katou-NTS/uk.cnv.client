/// <reference path='../../../../lib/nittsu/viewcontext.d.ts' />
module nts.uk.at.view.cdl011.a {

  const API = {
    findData: "/screen/com/cdl011/findData/{0}",
    register: "/sys/env/mailnoticeset/maildestination/register"
  };

  @bean()
  export class ScreenModel extends ko.ViewModel {

    functionId: number;
    mailFunctionName: KnockoutObservable<string> = ko.observable("");
    isCompany: KnockoutObservable<boolean> = ko.observable(false);
    isCompanyMobile: KnockoutObservable<boolean> = ko.observable(false);
    isPersonal: KnockoutObservable<boolean> = ko.observable(false);
    isPersonalMobile: KnockoutObservable<boolean> = ko.observable(false);

    created(param: number) {
      const vm = this;
      vm.$blockui("grayout");
      if (!_.isNil(param)) {
        vm.functionId = param;
      } else {
        // Fall back to support old UI
        vm.functionId = nts.uk.ui.windows.getShared("CDL011_PARAM");
      }
      switch (vm.functionId) {
        case 1:
          vm.mailFunctionName(vm.$i18n("CDL011_2")); break;
        case 6:
          vm.mailFunctionName(vm.$i18n("CDL011_3")); break;
        case 9:
          vm.mailFunctionName(vm.$i18n("CDL011_4")); break;
      }
      vm.findData(vm.functionId)
        .always(() => vm.$blockui("clear").then(() => vm.$nextTick(() => $("#A2_1 input").focus())));
    }

    private findData(functionId: number): JQueryPromise<void> {
      const vm = this;
      return vm.$ajax(nts.uk.text.format(API.findData, functionId))
        .then((result: MailDestinationFunctionManageDto) => {
          vm.isCompany(result.useCompanyMailAddress === 1);
          vm.isCompanyMobile(result.useCompanyMobileMailAddress === 1);
          vm.isPersonal(result.usePersonalMailAddress === 1);
          vm.isPersonalMobile(result.usePersonalMobileMailAddress === 1);
        });
    }

    private registerData(): JQueryPromise<void> {
      const vm = this;
      const param: MailDestinationFunctionManageDto = {
        functionId: vm.functionId,
        useCompanyMailAddress: vm.isCompany() ? 1 : 0,
        useCompanyMobileMailAddress: vm.isCompanyMobile() ? 1 : 0,
        usePersonalMailAddress: vm.isPersonal() ? 1 : 0,
        usePersonalMobileMailAddress: vm.isPersonalMobile() ? 1 : 0
      };
      return vm.$ajax(API.register, param)
        .then(() => vm.$dialog.info({ messageId: "Msg_15" }));
    }

    public processRegister() {
      const vm = this;
      vm.$blockui("grayout");
      vm.registerData()
        .always(() => vm.$blockui("clear").then(() => vm.closeDialog()));
    }

    public closeDialog() {
      const vm = this;
      vm.$window.close();
    }
  }

  export class MailDestinationFunctionManageDto {
    // 機能ID
    functionId: number;

    // 会社メールアドレスを利用する
    useCompanyMailAddress: number;

    // 会社携帯メールアドレスを利用する
    useCompanyMobileMailAddress: number;

    // 個人メールアドレスを利用する
    usePersonalMailAddress: number;

    // 個人携帯メールアドレスを利用する
    usePersonalMobileMailAddress: number;
  }
}
