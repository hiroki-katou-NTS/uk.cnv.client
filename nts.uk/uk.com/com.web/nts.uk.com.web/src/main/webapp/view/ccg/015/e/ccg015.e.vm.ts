/// <reference path='../../../../lib/nittsu/viewcontext.d.ts' />

module nts.uk.com.view.ccg015.e {

  const MENU_CREATION_LAYOUT_ID = 'menu-creation-layout';
  const CSS_CLASS_MENU_CREATION_ITEM_CONTAINER = 'menu-creation-item-container';

  @bean()
  export class ScreenModel extends ko.ViewModel {
    params: any = {};
    topPageCode: KnockoutObservable<string> = ko.observable('');
    layoutNo: KnockoutObservable<number> = ko.observable(0);
    $menuCreationLayout: JQuery = null;
    isMouseInsideLayout: KnockoutObservable<boolean> = ko.observable(false);
    itemList: KnockoutObservableArray<ItemModel> = ko.observableArray([]);
    isRenderKTG001: KnockoutObservable<boolean> = ko.observable(true);
    isRenderKTG004: KnockoutObservable<boolean> = ko.observable(true);
    isRenderKTG005: KnockoutObservable<boolean> = ko.observable(true);
    sortedIds: string[] = [];

    created(params: any) {
      const vm = this;
      vm.params = params;
    }

    mounted() {
      const vm = this;
      // Init dropable layout
      vm.$menuCreationLayout = $(`#${MENU_CREATION_LAYOUT_ID}`);
      vm.$menuCreationLayout
        .mouseenter(() => {
          vm.isMouseInsideLayout(true);
        })
        .mouseleave(() => {
          vm.isMouseInsideLayout(false);
        });
      // Init dragable item
      let menuPosition = -1;
      $(".menu-creation-option:not(.disabled)").draggable({
        connectToSortable: `#${MENU_CREATION_LAYOUT_ID}`,
        helper: "clone",
        start: (event, ui) => {
          LayoutUtils.startDragItemFromMenu(ui);
        },
        drag: (event, ui) => {
          menuPosition = _.findIndex(vm.$menuCreationLayout.children(), (item) => item.classList.contains('menu-creation-option'));
        },
        stop: (event, ui) => {
          const itemType = ui.helper.prevObject.attr('id');
          // Remove drag item
          ui.helper.remove();
          setTimeout(() => {
            if (vm.isMouseInsideLayout() && menuPosition >= 0) {
              vm.createItem(itemType, menuPosition);
            }
          }, 300);
        },
      });
      vm.loadData(vm.params);
    }

    /**
     * Create new item on drop from menu
     * @param item
     */
    private createItem(partType: string, position: number) {
      const vm = this;
      // Disable menu
      LayoutUtils.disableMenu(partType);
      let lastOrder = 0;
      // Find max order in list
      for (const item of vm.itemList()) {
        if (item.order > lastOrder) {
          lastOrder = item.order;
        }
      }
      // Add new item
      const newItem: ItemModel = LayoutUtils.convertWidgetToItem(partType, lastOrder + 1);
      if (newItem) {
        vm.itemList.splice(position, 0, newItem);
      }
      vm.checkData();
    }

    // ウィジェットを取消する
    public removeItem(itemType: string) {
      const vm = this;
      // Remove item in menu creation layout
      const itemindex = _.findIndex(vm.itemList(), item => item.itemType === itemType);
      if (itemindex >= 0) {
        // Remove item data
        const tempList = vm.itemList();
        tempList.splice(itemindex, 1);
        vm.itemList(tempList);
        // Remove item DOM
        vm.$menuCreationLayout
          .find(`.${CSS_CLASS_MENU_CREATION_ITEM_CONTAINER}[id=${itemType}]`)
          .remove();
      }
      // Enable item drag in menu
      LayoutUtils.enableMenu(itemType);
    }

    public onClickSetting(itemType: String) {
      const vm = this;
      const StandardWidgetTypeKTG004 = "0003";
      const StandardWidgetTypeKTG005 = "0001";
      const StandardWidgetTypeKTG001 = "0002";
      if (itemType === MenuPartType.PART_KTG_004) {
        vm.$window
          .modal('at', '/view/ktg/004/b/index.xhtml', StandardWidgetTypeKTG004)
          .then(() => vm.isRenderKTG004(false))
          .then(() => vm.isRenderKTG004(true));
      } else if (itemType === MenuPartType.PART_KTG_005) {
        vm.$window
          .modal('at', '/view/ktg/005/b/index.xhtml', StandardWidgetTypeKTG005)
          .then(() => vm.isRenderKTG005(false))
          .then(() => vm.isRenderKTG005(true));
      } else {
        vm.$window
          .modal('at', '/view/ktg/001/b/index.xhtml', StandardWidgetTypeKTG001)
          .then(() => vm.isRenderKTG001(false))
          .then(() => vm.isRenderKTG001(true));
      }
    }

    close() {
      const vm = this;
      vm.$window.close();
    }

    loadData(params: any) {
      const vm = this;
      if (params) {
        if (params.topPageModel && params.topPageModel.topPageCode) {
          vm.topPageCode(params.topPageModel.topPageCode);
        }
        if (params.frame === 2) {
          vm.layoutNo(1);
        } else if (params.frame === 3) {
          vm.layoutNo(2);
        }
      }
      const layoutRquest = {
        topPageCode: vm.topPageCode(),
        layoutNo: vm.layoutNo()
      }
      vm.$blockui("grayout");
      vm.$ajax('/toppage/getLayout', layoutRquest)
        .then((result: any) => {
          if (result.widgetSettings && result.widgetSettings.length) {
            // Save item list
            const itemList: ItemModel[] = _.chain(result.widgetSettings)
              .map((item: any) => new WidgetTypeModel({
                order: item.order,
                widgetType: item.widgetType,
              }))
              .orderBy('order', 'asc')
              .map((widget) => LayoutUtils.convertWidgetToItem(widget.widgetType.toString(), widget.order))
              .filter((item) => item)
              .value();
            vm.itemList(itemList);
            // Disable menu
            for (const item of itemList) {
              LayoutUtils.disableMenu(item.itemType);
            }
          }
        })
        .always(() => vm.$blockui("clear"));
    }
   
    
    saveData() {
      const vm = this;
      const sortedWidgetList: WidgetTypeModel[] = _.map(vm.itemList(), (item, index) => new WidgetTypeModel({
        order: index, // Set oder similar to list item order
        widgetType: Number(item.itemType),
      }));
      const requestParams: any = {
        topPageCode: vm.topPageCode(),
        layoutNo: vm.layoutNo(),
        layoutType: 3,
        widgetSettings: sortedWidgetList,
      };
      vm.$blockui("grayout");
      vm.$ajax('/toppage/saveLayoutWidget', requestParams)
        .then(() => {
          vm.$blockui("clear");
          vm.$dialog.info({ messageId: "Msg_15" });
        })
        .always(() => vm.$blockui("clear"));
    }

    checkData() {
      const vm = this;
      const sortedWidgetList: WidgetTypeModel[] = _.map(vm.itemList(), (item, index) => new WidgetTypeModel({
        order: index, // Set oder similar to list item order
        widgetType: Number(item.itemType),
      }));
      const listItem: number[] = _.map(sortedWidgetList,(item: WidgetTypeModel) => item.widgetType);
      vm.$ajax('/toppage/checkData', listItem).then((flag: boolean) => {
        if (!flag) {
          vm.$dialog.error({ messageId: "Msg_2140"});
        }
      });
    }

  }

  export class LayoutUtils {

    /**
     * Start drag item from menu
     * @param item
     * @param width
     * @param height
     */
    static startDragItemFromMenu(item: JQueryUI.DraggableEventUIParams) {
      // Init size + style for dragging item
      item.helper.css({ 'opacity': '0.7' });
    }

    static enableMenu(partType: string) {
      $(`#menu-item-container #${partType}.menu-creation-option`)
        .removeClass('disabled')
        .draggable('enable');
    }

    static disableMenu(partType: string) {
      $(`#menu-item-container #${partType}.menu-creation-option`)
        .addClass('disabled')
        .draggable('disable');
    }

    static convertWidgetToItem(type: string, order: number): ItemModel {
      let newItem: ItemModel = null;
      switch (type) {
        case MenuPartType.PART_KTG_005:
          newItem = new ItemModel({
            itemType: type,
            url: '/nts.uk.at.web/view/ktg/005/a/index.xhtml',
            order: order,
          });
          break;
        case MenuPartType.PART_KTG_001:
          newItem = new ItemModel({
            itemType: type,
            url: '/nts.uk.at.web/view/ktg/001/a/index.xhtml',
            order: order,
          });
          break;
        case MenuPartType.PART_KTG_004:
          newItem = new ItemModel({
            itemType: type,
            url: '/nts.uk.at.web/view/ktg/004/a/index.xhtml',
            order: order,
          });
          break;
        case MenuPartType.PART_KTG_026:
          newItem = new ItemModel({
            itemType: type,
            url: '/nts.uk.at.web/view/ktg/026/a/index.xhtml',
            order: order,
          });
          break;
        case MenuPartType.PART_KTG_027:
          newItem = new ItemModel({
            itemType: type,
            url: '/nts.uk.at.web/view/ktg/027/a/index.xhtml',
            order: order,
          });
          break;
        case MenuPartType.PART_KDP_001:
          newItem = new ItemModel({
            itemType: type,
            url: '/nts.uk.at.web/view/kdp/001/a/index.xhtml',
            order: order,
          });
          break;
        case MenuPartType.PART_KTG_031:
          newItem = new ItemModel({
            itemType: type,
            url: '/nts.uk.at.web/view/ktg/031/a/index.xhtml',
            order: order,
          });
          break;
        case MenuPartType.PART_CCG_005:
          newItem = new ItemModel({
            itemType: type,
            url: '/nts.uk.com.web/view/ccg/005/a/index.xhtml',
            order: order,
          });
          break;
        default:
          break;
      }
      return newItem;
    }
  }

  export enum MenuPartType {
    PART_KTG_005 = '0', // Iframe Have setting
    PART_KTG_001 = '1', // Iframe
    PART_KTG_004 = '2', // Iframe Have setting
    PART_KTG_026 = '3', // Component
    PART_KTG_027 = '4', // Component
    PART_KDP_001 = '5', // Iframe
    PART_KTG_031 = '6', // Component
    PART_CCG_005 = '7', // Component
  }

  export class WidgetTypeModel {
    widgetType: number;
    order: number;

    constructor(init?: Partial<WidgetTypeModel>) {
      $.extend(this, init);
    }
  }

  export class ItemModel {
    order: number;
    itemType: string;
    url: string;
    width: number;
    height: number;
    isShowSetting: boolean;

    constructor(init?: Partial<ItemModel>) {
      $.extend(this, init);
      this.isShowSetting = (init.itemType === MenuPartType.PART_KTG_004 
        || init.itemType === MenuPartType.PART_KTG_005 
        || init.itemType === MenuPartType.PART_KTG_001);
    }

    public isComponent() {
      return this.isKTG026() || this.isKTG027() || this.isKTG031() || this.isCCG005() || this.isKTG001() || this.isKTG005 || this.isKTG004 || this.isKDP001(); 
    }
    public isKTG026() {
      return this.itemType === MenuPartType.PART_KTG_026;
    }
    public isKTG027() {
      return this.itemType === MenuPartType.PART_KTG_027;
    }
    public isKTG031() {
      return this.itemType === MenuPartType.PART_KTG_031;
    }
    public isCCG005() {
      return this.itemType === MenuPartType.PART_CCG_005;
    }
    public isKTG001() {
      return this.itemType === MenuPartType.PART_KTG_001;
    }
    public isKTG004() {
      return this.itemType === MenuPartType.PART_KTG_004;
    }
    public isKTG005() {
      return this.itemType === MenuPartType.PART_KTG_005;
    }
    public isKDP001() {
      return this.itemType === MenuPartType.PART_KDP_001;
    }
  }
}