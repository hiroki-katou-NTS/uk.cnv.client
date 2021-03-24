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

      //add button take photo in ntsImageEditor
      $("#upload").ready(() => {
        $(".comfirm-checkbox").remove();
        $(".edit-action-container").hide();
        $(`<button data-bind="click: openDialogE2"> ${vm.$i18n('CMM048_107')} </button>`)
          .attr('class', 'upload-webcam')
          .insertAfter(".upload-btn");
      });

      if (vm.fileId()) {
        $(".edit-action-container").show();
        $("#upload").ntsImageEditor("selectByFileId", vm.fileId());
        $('.image-preview-container').css('height', "240px");
        $('.container-no-upload-background').css('height', "240px");
      } else {
        $('.image-preview-container').css('height', "325px");
        $('.container-no-upload-background').css('height', "325px");
      }
      $("#upload").bind("imgloaded", () => {
        $(".edit-action-container").show();
      });
      try {
        $("#upload").ntsImageEditor("selectByFileId", {
          actionOnClose: () => {
            $(".checkbox-holder").hide();
            $('.upload-btn').focus();
          }
        });
      } catch (Error) {
        $('.upload-btn').focus();
      }

      //reset hight of image preview when image is loaded
      $("#upload").bind("imgloaded", (evt: any, query?: any) => {
        if (query) {
          $('.image-preview-container').css('height', "240px");
          $('.container-no-upload-background').css('height', "240px");
        }
      });

      //check that computer have webcam or not
      navigator.getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.msGetUserMedia;
      if (navigator.getUserMedia) {
        navigator.getUserMedia({ video: true }, () => {
          $(".upload-webcam").attr('disabled', 'false');
        }, () => {
          $(".upload-webcam").attr('disabled', 'true');
          $(`<span>${vm.$i18n('CMM048_115')}</span>`)
          .css("color","red")
          .insertAfter(".upload-webcam");
        });
      } else {
        $(".upload-webcam").attr('disabled', 'true');
        $(`<span>${vm.$i18n('CMM048_115')}</span>`)
        .css("color","red")
        .insertAfter(".upload-webcam");
      }
      ko.applyBindings(vm, $(".upload-webcam")[0]);
    }

    public openDialogE2() {
      const vm = this;
      vm.$window.modal("/view/cmm/048/f/index.xhtml").then((uri: string) => {
        if (uri) {
          $(".edit-action-container").show();
          $("#upload").ntsImageEditor("showByUrl", { url: uri });
        }
      });
    }

    public closeDialog() {
      const vm = this;
      const fileId: string = vm.fileId();
      vm.$window.close(fileId);
    }

    public upload() {
      let vm = this;
      if (nts.uk.ui.errors.hasError()) {
        return;
      }
      vm.$blockui('grayout');
      let isImageLoaded = $("#upload").ntsImageEditor("getImgStatus");
      if (isImageLoaded.imgOnView) {
        if ($("#upload").data("cropper") == undefined) {
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