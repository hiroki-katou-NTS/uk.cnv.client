/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />
module nts.uk.at.view.kal012.a {
  @bean()
  class ViewModel extends ko.ViewModel {

    constructor(params: any) {
      super();
      const vm = this;
    }

    created(params: any) {
      const vm = this;
    }

    mounted() {
      const vm = this;
      
      $('#KAL013').focus();
    }

    goToScreenKAL013() {
      nts.uk.request.jump('/view/kal/013/a/index.xhtml');
    }

    goToScreenKAL014() {
      nts.uk.request.jump('/view/kal/014/a/index.xhtml');
    }

    goToScreenKAL002B() {
      nts.uk.request.jump('/view/kal/002/b/index.xhtml');
    }
  }
}