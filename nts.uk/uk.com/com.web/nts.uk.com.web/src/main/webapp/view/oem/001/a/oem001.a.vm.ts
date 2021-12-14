/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.com.view.oem001.a {

  @bean()
  export class ScreenModel extends ko.ViewModel {

    mounted() {
      $("#A1_5").focus();
    }

    public openScreenOEM002(): void {
      const vm = this;
      vm.$jump("/view/oem/002/a/index.xhtml");
    }

    public openScreenOEM003(): void {
      const vm = this;
      vm.$jump("/view/oem/003/a/index.xhtml");
    }

    public openScreenOEM004(): void {
      const vm = this;
      vm.$jump("/view/oem/004/a/index.xhtml");
    }
  }
}