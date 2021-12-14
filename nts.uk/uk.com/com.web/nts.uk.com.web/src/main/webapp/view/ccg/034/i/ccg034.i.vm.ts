/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.com.view.ccg034.i {
  import CCG034D = nts.uk.com.view.ccg034.d;
  import getText = nts.uk.resource.getText;

  const MAXIMUM_IMAGE_COUNT = 4;

  @bean()
  export class ScreenModel extends ko.ViewModel {
    partData: CCG034D.PartDataImageModel = null;
    originalFileId: string = null;
    //Choose file
    imageOption: ItemModel[] = [
      { code: 0, name: getText('CCG034_121') },
      { code: 1, name: getText('CCG034_123') }
    ];
    imageType: KnockoutObservable<number> = ko.observable(0);
    imageSrc: KnockoutObservable<string> = ko.observable(null);
    imageList: ItemModel[] = [];
    // Upload file
    uploadedFileName: KnockoutObservable<string> = ko.observable(null);
    fileSize: KnockoutObservable<number> = ko.observable(0);
    displayFileSize: KnockoutObservable<string> = ko.computed(() => {
      const vm = this;
      if (vm.fileSize() >= 1024) {
        return nts.uk.text.format(getText("CCG034_125"), vm.fileSize() / 1024) + "MB";
      }
      return nts.uk.text.format(getText("CCG034_125"), vm.fileSize()) + "KB";
    });
    fileId: KnockoutObservable<string> = ko.observable(null);
    uploadSrc: KnockoutObservable<string> = ko.observable(null);

    created(params: any) {
      const vm = this;
      vm.partData = params;
    }

    mounted() {
      const vm = this;
      vm.imageType.subscribe(value => {
        if (value === 1 && !nts.uk.text.isNullOrEmpty(vm.fileId())) {
          vm.$ajax("/shr/infra/file/storage/isexist/" + vm.fileId()).then((isExist: boolean) => {
            if (isExist) {
              vm.$ajax("/shr/infra/file/storage/infor/" + vm.fileId()).then((res: any) => {
                vm.fileSize(Math.round(Number(res.originalSize) / 1024));
                vm.updatePreview();
              });
            }
          });
        }
      });
      // Binding part data
      vm.fileId(vm.partData.fileId);
      vm.imageSrc(vm.partData.fileName);
      vm.uploadedFileName(vm.partData.uploadedFileName);
      vm.fileId(vm.partData.fileId);
      vm.fileSize(vm.partData.uploadedFileSize ? vm.partData.uploadedFileSize : 0);
      vm.imageType(vm.partData.isFixed);
      vm.originalFileId = vm.fileId();

      vm.createPopUp();
      $("#I2").focus();
    }

    uploadFinished(data: any) {
      const vm = this;
      if (vm.fileId() !== vm.originalFileId) {
        (nts.uk.request as any).file.remove(vm.fileId());
      }
      vm.fileId(data.id);
      vm.fileSize(Math.round(Number(data.originalSize) / 1024));
      vm.updatePreview();
    }

    private updatePreview() {
      const vm = this;
      const container = $("#I2_2_2");
      container.html("");
      container.append($("<img class='pic-preview'/>").attr("src", (nts.uk.request as any).liveView(vm.fileId())));
    }

    createPopUp() {
      const vm = this;
      // Generate image list
      for (let index = 0; index < 40; index++) {
        vm.imageList.push({ code: index, name: `../../share/resources/ccg034/i/CCG034I_${nts.uk.text.padLeft(String(index + 1), '0', 3)}.png` });
      }
      // Adding images inside popup
      for (let imageRow = 0; imageRow < vm.imageList.length; imageRow += MAXIMUM_IMAGE_COUNT) {
        let toAppend = "";
        for (let imageCol = imageRow; imageCol < imageRow + MAXIMUM_IMAGE_COUNT; imageCol++) {
          toAppend += `<img id="I2_2_1_${imageCol}" src="${vm.imageList[imageCol].name}" class="pic-choose" data-bind="click: chooseImage" />`;
        }
        const template = `<div>${toAppend}</div>`;
        $("#I2_4").append(template);
        // Rebind Knockout for the newly added div
        ko.applyBindings(vm, $("#I2_4 > div:last-child")[0]);
      }

      $("#I2_4").ntsPopup({
        trigger: "#I2_1_1",
        position: {
          my: "left top",
          at: "left bottom",
          of: "#I2_1_1"
        },
        showOnStart: false,
        dismissible: true
      });
    }

    chooseImage(data: any, event: any) {
      const vm = this;
      const itemId: string = event.currentTarget.id;
      const item = vm.imageList[Number(itemId.substr(7))];
      vm.imageSrc(item.name);
      $("#I2_4").ntsPopup("hide");
    }

    /**
    * Close dialog
    */
    public closeDialog() {
      const vm = this;
      if (vm.fileId() !== vm.partData.originalFileId) {
        vm.$window.close({ isSaving: false, fileId: vm.fileId() });
      } else {
        vm.$window.close({ isSaving: false });
      }
    }

    /**
     * Update part data and close dialog
     */
    public updatePartDataAndCloseDialog() {
      const vm = this;
      vm.$validate().then((valid: boolean) => {
        if (valid) {
          // Update part data
          const image = new Image();
          // ImageType === 0
          vm.partData.fileName = vm.imageSrc();
          image.src = vm.imageSrc();
          // ImageType === 1
          vm.partData.fileId = vm.fileId();
          vm.partData.uploadedFileName = vm.uploadedFileName();
          vm.partData.uploadedFileSize = vm.fileSize();
          image.src = vm.fileId() ? (nts.uk.request as any).liveView(vm.fileId()) : null;
          vm.partData.originalFileId = vm.originalFileId;
          // Common
          vm.partData.ratio = image.naturalHeight / image.naturalWidth;
          vm.partData.isFixed = vm.imageType();
          // Return data
          vm.$window.close({ isSaving: true, partData: vm.partData });
        }
      });
    }
  }

  export interface ItemModel {
    code: number;
    name: string;
  }
}
