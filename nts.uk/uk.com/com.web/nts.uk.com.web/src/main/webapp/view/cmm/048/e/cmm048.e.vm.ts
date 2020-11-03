/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />
module nts.uk.com.view.cmm048.e {

  @bean()
  export class ViewModel extends ko.ViewModel {
    fileId: KnockoutObservable<string> = ko.observable('');

    created(params: string) {
      const vm = this;
      vm.fileId(params);
    }

    mounted() {
      const vm = this;
      $("#upload").ready(() => {
        $(".comfirm-checkbox").remove();
        $(`<button data-bind="click: openDialogE2"> ${vm.$i18n('CMM048_107')} </button>`)
          .attr('class', 'upload-webcam')
          .insertAfter(".upload-btn");
        ko.applyBindings(vm, $(".upload-webcam")[0]);
      });
      if (vm.fileId()) {
        $("#upload").ntsImageEditor("selectByFileId", vm.fileId());
      }
    }

    public openDialogE2() {
      const vm = this;
      vm.$window.modal("/view/cmm/048/f/index.xhtml").then((uri: string) => {
        if (uri) {
          $("#upload").ntsImageEditor("showByUrl", { url: uri });
        }
      });
    }

    public closeDialog() {
      const vm = this;
      vm.$window.close(vm.fileId());
    }

    public upload() {
      const vm = this;
      if (nts.uk.ui.errors.hasError()) {
        return;
      }
      vm.$blockui('grayout');
      const isImageLoaded = $("#upload").ntsImageEditor("getImgStatus");
      if (isImageLoaded.imgOnView) {
        if ($("#upload").data("cropper") === undefined) {
          vm.closeDialog();
          return;
        }
        if ($("#upload").data("cropper").cropped) {
          $("#upload").ntsImageEditor("upload", { stereoType: "image" }).then((data: any) => {
            vm.fileId(data.id);
            vm.closeDialog();
          }).fail((error: any) => {
            vm.$blockui('clear')
            vm.$dialog.error(error);
          })
            .always(() => {
              vm.$blockui('clear');
            });
        } else {
          vm.closeDialog();
        }
      } else {
        vm.$dialog.error({ messageId: "Msg_617" });
        vm.$blockui('clear')
      }
    }
  }
}