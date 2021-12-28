/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.com.view.ccg034.f {
  import getText = nts.uk.resource.getText;
  import CCG034D = nts.uk.com.view.ccg034.d;

  // URL API backend
  const API = {
    getMenuList: "sys/portal/standardmenu/findByMenuAndWebMenu"
  };
  const MAXIMUM_IMAGE_COUNT = 4;

  @bean()
  export class ScreenModel extends ko.ViewModel {
    //Data
    partData: CCG034D.PartDataMenuModel = null;
    // System combo box
    selectedSystemType: KnockoutObservable<number> = ko.observable(0);
    systemList: KnockoutObservableArray<ItemModel> = ko.observableArray([
      { code: 0, name: getText("CCG034_70") },
      { code: 1, name: getText("Enum_System_COMMON") },
      { code: 2, name: getText("Enum_System_TIME_SHEET") },
      { code: 3, name: getText("Enum_System_OFFICE_HELPER") },
      { code: 4, name: getText("Enum_System_KYUYOU") },
      { code: 5, name: getText("Enum_System_JINJIROU") }
    ]);
    // Menu list
    selectedMenuCode: KnockoutObservable<string> = ko.observable('');
    displayMenuName: KnockoutObservable<string> = ko.observable('');
    menuName: KnockoutObservable<string> = ko.observable('');
    menuCode: KnockoutObservable<string> = ko.observable('');
    menuClassification: KnockoutObservable<number> = ko.observable(0);
    menuSystemType: KnockoutObservable<number> = ko.observable(0);
    menuList: KnockoutObservableArray<Menu> = ko.observableArray([]);
    filteredMenuList: KnockoutObservableArray<Menu> = ko.observableArray([]);
    menuColumns: any[] = [
      { headerText: '', key: 'id', hidden: true },
      { headerText: getText('CCG034_72'), key: 'code', width: 60 },
      { headerText: getText('CCG034_73'), key: 'name', width: 300 }
    ];
    menuUrl: KnockoutObservable<string> = ko.observable(null);
    // Common text attribute
    fontSize: KnockoutObservable<number> = ko.observable(11);
    isBold: KnockoutObservable<boolean> = ko.observable(false);
    textColorValue: KnockoutObservable<string> = ko.observable(null);
    horizontalAlign: KnockoutObservable<number> = ko.observable(nts.uk.com.view.ccg034.share.model.HorizontalAlign.LEFT);
    verticalAlign: KnockoutObservable<number> = ko.observable(nts.uk.com.view.ccg034.share.model.VerticalAlign.TOP);
    horizontalAlignList: ItemModel[] = [
      { code: HorizontalAlign.LEFT, name: getText('CCG034_79') },
      { code: HorizontalAlign.MIDDLE, name: getText('CCG034_80') },
      { code: HorizontalAlign.RIGHT, name: getText('CCG034_81') }
    ];
    verticalAlignList: ItemModel[] = [
      { code: VerticalAlign.TOP, name: getText('CCG034_83') },
      { code: VerticalAlign.CENTER, name: getText('CCG034_84') },
      { code: VerticalAlign.BOTTOM, name: getText('CCG034_85') }
    ];
    // Image menu
    originalFileId: string = null;
    imageOption: ItemModel[] = [
      { code: -1, name: getText('CCG034_131') },
      { code: 0, name: getText('CCG034_132') },
      { code: 1, name: getText('CCG034_133') }
    ];
    imageType: KnockoutObservable<number> = ko.observable(null);
    imageSrc: KnockoutObservable<string> = ko.observable(null);
    imageList: ItemModel[] = [];
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
    isDisplayFileSize: KnockoutComputed<boolean>;

    constructor() {
      super();
      const vm = this;
      vm.isDisplayFileSize = ko.computed(() => {
        if (vm.imageType() === 1 && !_.isNil(vm.fileId()) && !_.isEmpty(vm.fileId())) {
          const isExist = ko.observable(false);
          vm.$ajax("/shr/infra/file/storage/isexist/" + vm.fileId()).then((result: boolean) => isExist(result));
          return isExist();
        }
        return false;
      });
    }

    created(params: any) {
      const vm = this;
      vm.partData = params;

      vm.selectedMenuCode.subscribe(value => {
        const item = _.find(vm.menuList(), { id: value });
        if (item) {
          vm.displayMenuName(`${item.code} ${item.name}`);
          vm.menuName(item.name);
          vm.menuCode(item.code);
          vm.menuClassification(item.menuClassification);
          vm.menuSystemType(item.systemType);
          if (nts.uk.text.isNullOrEmpty(item.queryString)) {
            vm.menuUrl(item.url);
          } else {
            vm.menuUrl(`${item.url}?${item.queryString}`);
          }
          //Revalidate
          vm.$validate("#F6_2");
        }
      });

      vm.selectedSystemType.subscribe(value => {
        if (Number(value) > 0) {
          vm.filteredMenuList(_.filter(vm.menuList(), { systemType: value - 1 }));
        } else {
          vm.filteredMenuList(vm.menuList());
        }
      });

      vm.imageType.subscribe(value => {
        $("#F9_10").ntsPopup("hide");
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
        // Fix tabindex
        $("#F9_3_3 .browser-button").attr("tabindex", value === 1 ? 7 : -1);
      });
    }

    mounted() {
      const vm = this;
      // Binding part data
      vm.horizontalAlign(vm.partData.alignHorizontal);
      vm.verticalAlign(vm.partData.alignVertical);
      vm.menuName(vm.partData.menuName);
      vm.menuCode(vm.partData.menuCode);
      vm.menuClassification(vm.partData.menuClassification);
      vm.menuSystemType(vm.partData.systemType);
      vm.fontSize(vm.partData.fontSize);
      vm.isBold(vm.partData.isBold);
      vm.menuUrl(vm.partData.menuUrl);
      vm.textColorValue(vm.partData.textColor);
      vm.fileId(vm.partData.fileId);
      vm.imageType(vm.partData.isFixed ?? -1);
      vm.originalFileId = vm.fileId();
      if (!nts.uk.text.isNullOrEmpty(vm.partData.fileName)) {
        vm.imageSrc(vm.partData.fileName);
      }

      vm.findMenuData();
      vm.createPopUp();
      $("#F6_2").focus();
    }

    private findMenuData() {
      const vm = this;
      vm.$blockui("grayout");
      vm.$ajax(API.getMenuList)
        .then((data: any) => {
          vm.menuList(_.map(data, (menu: any) => ({
            id: nts.uk.util.randomId(),
            code: menu.code,
            name: menu.displayName,
            systemType: menu.system,
            menuClassification: menu.classification,
            url: menu.url,
            queryString: menu.queryString
          })));
          vm.filteredMenuList(vm.menuList());
        })
        .then(() => {
          if (vm.menuCode()) {
           vm.selectedMenuCode(_.find(vm.menuList(), { code: vm.menuCode(), menuClassification: vm.menuClassification(), systemType: vm.menuSystemType() }).id);
           vm.menuName(vm.partData.menuName);
          }
        })
        .always(() => vm.$blockui("clear"));
    }

    uploadFinished(data: any) {
      const vm = this;
      if (vm.fileId() !== vm.originalFileId && !nts.uk.text.isNullOrEmpty(vm.fileId())) {
        (nts.uk.request as any).file.remove(vm.fileId());
      }
      vm.fileId(data.id);
      vm.fileSize(Math.round(Number(data.originalSize) / 1024));
      vm.updatePreview();
    }

    private updatePreview() {
      const vm = this;
      const container = $("#F9_4_3");
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
          toAppend += `<img id="F9_10_1_${imageCol}" data-index="${imageCol}" src="${vm.imageList[imageCol].name}" class="pic-choose" data-bind="click: chooseImage" tabindex="8" />`;
        }
        const template = `<div>${toAppend}</div>`;
        $("#F9_10").append(template);
        // Rebind Knockout for the newly added div
        ko.applyBindings(vm, $("#F9_10 > div:last-child")[0]);
      }

      $("#F9_10").ntsPopup({
        position: {
          my: "left top",
          at: "left bottom",
          of: "#F9_3_2"
        },
        showOnStart: false,
        dismissible: false
      });
      $("#F9_3_2").on("click", () => $("#F9_10").ntsPopup("toggle"));
    }

    chooseImage(data: any, event: any) {
      const vm = this;
      const index: number = $(event.currentTarget).data("index");
      const item = vm.imageList[index];
      vm.imageSrc(item.name);
      $("#F9_10").ntsPopup("hide");
    }

    /**
     * Close dialog
     */
    public closeDialog() {
      const vm = this;
      if (vm.fileId() !== vm.partData.originalFileId && !nts.uk.text.isNullOrEmpty(vm.fileId())) {
        (nts.uk.request as any).file.remove(vm.fileId());
      }
      vm.$window.close();
    }

    /**
    * Update part data and close dialog
    */
    public updatePartDataAndCloseDialog() {
      const vm = this;
      vm.$validate().then((valid: boolean) => {
        if (valid) {
          const image = new Image();
          // Update part data
          vm.partData.alignHorizontal = vm.horizontalAlign();
          vm.partData.alignVertical = vm.verticalAlign();
          vm.partData.menuCode = vm.menuCode();
          vm.partData.menuName = vm.menuName();
          vm.partData.menuClassification = vm.menuClassification();
          vm.partData.systemType = vm.menuSystemType();
          vm.partData.fontSize = Number(vm.fontSize());
          vm.partData.isBold = vm.isBold();
          vm.partData.menuUrl = vm.menuUrl();
          vm.partData.textColor = vm.textColorValue();
          vm.partData.isFixed = vm.imageType() === -1 ? null : vm.imageType();
          // ImageType === 0
          if (vm.imageType() === 0) {
            vm.partData.fileName = vm.imageSrc();
            image.src = vm.imageSrc();
            if (vm.fileId() !== vm.partData.originalFileId && !nts.uk.text.isNullOrEmpty(vm.fileId())) {
              (nts.uk.request as any).file.remove(vm.fileId());
            }
          } else if (vm.imageType() === 1) {
            // ImageType === 1
            vm.partData.fileId = vm.fileId();
            image.src = vm.fileId() ? (nts.uk.request as any).liveView(vm.fileId()) : null;
            vm.partData.originalFileId = vm.originalFileId;
          }
          
          vm.partData.ratio = (image.naturalHeight / image.naturalWidth) || 0;
          // Return data
          vm.$window.close(vm.partData);
        }
      });
    }
  }

  export interface ItemModel {
    code: number;
    name: string;
  }

  export interface Menu {
    id?: string;
    code: string;
    name: string;
    systemType: number;
    menuClassification: number;
    url: string;
    queryString: string;
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
