/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.com.view.ccg015.a {

  @bean()
  export class ScreenModel extends ko.ViewModel {
    textButton: KnockoutComputed<string> = ko.computed(() => _.replace(this.$i18n('CCG015_49'), '\n', '<br/>'));

    mounted() {
      $('#A1_3').focus();
    }

    openScreenCCG034() {
      const vm = this;
      vm.$jump('/view/ccg/034/a/index.xhtml');
    }

    openDialogCCG030() {
      const vm = this;
      vm.$window.modal('/view/ccg/030/a/index.xhtml');
    }

    openScreenCCG018() {
      const vm = this;
      vm.$jump('/view/ccg/018/a/index.xhtml');
    }

    openScreenCCG015B() {
      const vm = this;
      vm.$jump('/view/ccg/015/b/index.xhtml');
    }

    openModalCCG015G() {
      const vm = this;
      vm.$window.modal('com', '/view/ccg/015/g/index.xhtml')
    }

    openModalCCG015H() {
      const vm = this;
      vm.$window.modal('com', '/view/ccg/015/h/index.xhtml');
    }

  }
}