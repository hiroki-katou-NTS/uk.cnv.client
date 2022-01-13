/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.com.view.ccg034.b {

  // URL API backend
  const API = {
    extract: "sys/portal/createflowmenu/extract/{0}"
  };

  @bean()
  export class ScreenModel extends ko.ViewModel {

    fileId: KnockoutObservable<string> = ko.observable('');
    htmlSrc: KnockoutObservable<string> = ko.observable('');

    created(params: any) {
      const vm = this;
      vm.fileId(params.fileId);
      vm.htmlSrc(params.htmlSrc);
    }

    mounted() {
      const vm = this;
      vm.extract();
      $("#B1_1").focus();
    }

    private extract() {
      const vm = this;
      if (vm.htmlSrc()) {
        vm.renderHTML(vm.htmlSrc());
      } else {
        // Render from file
        vm.$blockui("grayout");
        const path = nts.uk.text.format(API.extract, vm.fileId());
        vm.$ajax(path)
          .then((res: any) => {
            if (!!res) {
              vm.htmlSrc(res.htmlContent);
              vm.renderHTML(vm.htmlSrc());
            }
          })
          .always(() => vm.$blockui("clear"));
      }
    }

    private renderHTML(htmlSrc: string) {
      const vm = this;
      const $iframe = $("#B1_1");
      const ifr = document.getElementById('B1_1');
      const iframedoc = (ifr as any).contentDocument || (ifr as any).contentWindow.document;
      // If browser supports srcdoc for iframe
      // then add src to srcdoc attr
      if ("srcdoc" in $iframe) {
        $iframe.attr("srcdoc", htmlSrc);
      } else {
        // Fallback to IE... (doesn't support srcdoc)
        // Write directly into iframe body
        iframedoc.body.innerHTML = htmlSrc;
      }
      ccg034.share.model.customload.loadLimitedLabelForIframe();
      vm.$nextTick(() => {
        $("#B1_1").width($("#B1_1").contents().find(".content-container").width());
        $("#B1_1").height($("#B1_1").contents().find(".content-container").height());
        _.each(iframedoc.getElementsByClassName("ccg034-hyperlink"), link => {
          link.addEventListener('click', (event: Event) => event.preventDefault());
        });
      });
    }

    public closeDialog() {
      const vm = this;
      vm.$window.close();
    }
  }
}
