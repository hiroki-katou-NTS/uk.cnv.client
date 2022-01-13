/// <reference path='../../../../lib/nittsu/viewcontext.d.ts' />

module nts.uk.at.view.cmm052.a {

  @bean()
  export class ScreenModel extends ko.ViewModel {

    mounted(){
      $('#A1_3').focus();
    }

    public openScreenCas003() {
      const vm = this;
      vm.$jump("/view/cas/003/a/index.xhtml");
    }

    public openDialogCdl011() {
      const vm = this;
      vm.$window.modal("/view/cdl/011/a/index.xhtml", 1);
    }
  }
}