/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.com.view.ccg034.h {
  import CCG034D = nts.uk.com.view.ccg034.d;
  import getText = nts.uk.resource.getText;

  const MAX_FILE_SIZE_MB = 10;

  @bean()
  export class ScreenModel extends ko.ViewModel {
    partData: CCG034D.PartDataAttachmentModel = null;
    // File name
    fileName: KnockoutObservable<string> = ko.observable('');
    // Upload file
    uploadedFileName: KnockoutObservable<string> = ko.observable('');
    fileSize: KnockoutObservable<number> = ko.observable(0);
    displayFileSize: KnockoutObservable<string> = ko.computed(() => {
      const vm = this;
      return nts.uk.text.format(getText("CCG034_105"), vm.fileSize()) + "KB";
    });
    fileId: KnockoutObservable<string> = ko.observable('');
    // Common text attribute
    fontSize: KnockoutObservable<number> = ko.observable(11);
    isBold: KnockoutObservable<boolean> = ko.observable(true);
    horizontalAlign: KnockoutObservable<number> = ko.observable(HorizontalAlign.LEFT);
    verticalAlign: KnockoutObservable<number> = ko.observable(VerticalAlign.TOP);
    horizontalAlignList: ItemModel[] = [
      { code: HorizontalAlign.LEFT, name: getText('CCG034_110') },
      { code: HorizontalAlign.MIDDLE, name: getText('CCG034_111') },
      { code: HorizontalAlign.RIGHT, name: getText('CCG034_112') }
    ];
    verticalAlignList: ItemModel[] = [
      { code: VerticalAlign.TOP, name: getText('CCG034_114') },
      { code: VerticalAlign.CENTER, name: getText('CCG034_115') },
      { code: VerticalAlign.BOTTOM, name: getText('CCG034_116') }
    ];

    created(params: any) {
      const vm = this;
      vm.partData = params;
    }

    mounted() {
      const vm = this;
      // Binding part data
      vm.horizontalAlign(vm.partData.alignHorizontal);
      vm.verticalAlign(vm.partData.alignVertical);
      vm.fileId(vm.partData.fileId);
      vm.fileName(vm.partData.linkContent);
      vm.fontSize(vm.partData.fontSize);
      vm.isBold(vm.partData.isBold);
      vm.uploadedFileName(vm.partData.fileName);
      vm.fileSize(vm.partData.fileSize);

      if (vm.fileId()) {
        nts.uk.request.ajax("/shr/infra/file/storage/infor/" + vm.fileId())
          .done((res: any) => {
          $("#H2_2 .filenamelabel").text(res.originalName);
          vm.fileSize(Math.round(Number(res.originalSize) / 1024));
         }).fail(() => {
          vm.$dialog.error({ messageId: 'Msg_70', messageParams: [ String(MAX_FILE_SIZE_MB) ] });
         });
      }
      $("#H1_2").focus();
    }


    public uploadFinished(data: any) {
      const vm = this;
      vm.fileId(data.id);
      vm.fileSize(Math.round(Number(data.originalSize) / 1024));
      if (!vm.fileName()) {
        vm.fileName(data.originalName);
      }
    }

    /**
     * Close dialog
     */
    public closeDialog() {
      const vm = this;
      vm.$window.close();
    }

     /**
     * Update part data and close dialog
     */
    public updatePartDataAndCloseDialog() {
      const vm = this;
      vm.$validate("#H1_2", "#H2_2").then((validUpload: boolean) => {
        if (validUpload || vm.fileId()) {
          vm.$validate().then((valid: boolean) => {
            if (valid) {
              if (vm.fileSize() / 1024 <= MAX_FILE_SIZE_MB) {
                 // Update part data
                vm.partData.alignHorizontal = vm.horizontalAlign();
                vm.partData.alignVertical = vm.verticalAlign();
                vm.partData.linkContent = vm.fileName().trim() || vm.uploadedFileName();
                vm.partData.fontSize = Number(vm.fontSize());
                vm.partData.isBold = vm.isBold();
                vm.partData.fileId = vm.fileId();
                vm.partData.fileName = vm.uploadedFileName();
                vm.partData.fileSize = vm.fileSize();
                vm.partData.fileLink = (nts.uk.request as any).liveView(vm.fileId());

                // Return data
                vm.$window.close(vm.partData);
              } else {
                vm.$dialog.error({ messageId: 'Msg_70', messageParams: [ String(MAX_FILE_SIZE_MB) ] });
              }
            }
          });
        }
      });
    }
  }

  export interface ItemModel {
    code: number;
    name: string;
  }

  enum HorizontalAlign {
    LEFT = 0,
    MIDDLE = 1,
    RIGHT = 2,
  }

  enum VerticalAlign {
    TOP = 0,
    CENTER = 1,
    BOTTOM = 2,
  }
}
