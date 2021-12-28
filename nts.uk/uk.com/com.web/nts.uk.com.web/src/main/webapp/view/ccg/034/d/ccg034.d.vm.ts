/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.com.view.ccg034.d {

  // URL API backend
  const API = {
    generateHtml: "sys/portal/createflowmenu/generateHtml",
    updateLayout: "sys/portal/createflowmenu/updateLayout",
    getMenuList: "sys/portal/standardmenu/findByMenuAndWebMenu",
    copyFile: "sys/portal/createflowmenu/copyFile/{0}"
  };

  const KEY_DATA_PART_TYPE = 'data-part-type';
  const MENU_CREATION_LAYOUT_ID = 'menu-creation-layout';
  const ITEM_HIGHLIGHT_ID = 'item-highlight';
  const ITEM_COPY_PLACEHOLDER_ID = 'item-copy-placeholder';
  const KEY_DATA_ITEM_CLIENT_ID = 'data-item-client-id';
  const CSS_CURSOR_NOT_ALLOWED = 'not-allowed';
  const CSS_CLASS_UI_SELECTED = 'ui-selected';
  const CSS_CLASS_UI_RESIZABLE_S = 'ui-resizable-s';
  const CSS_CLASS_UI_RESIZABLE_E = 'ui-resizable-e';
  const CSS_CLASS_UI_RESIZABLE_SE = 'ui-resizable-se';
  const CSS_CLASS_PART_SETTING_POPUP_OPTION = 'part-setting-popup-option';
  const CSS_CLASS_MENU_CREATION_ITEM_CONTAINER = 'menu-creation-item-container';
  const CSS_CLASS_MENU_CREATION_ITEM = 'menu-creation-item';
  const CSS_CLASS_MENU_CREATION_ITEM_COPY_PLACEHOLDER = 'menu-creation-item-copy-placeholder';
  const CSS_CLASS_CCG034_HYPERLINK = 'ccg034-hyperlink';
  const CELL_SIZE = 40;
  const CREATION_LAYOUT_WIDTH = 1920;
  const CREATION_LAYOUT_HEIGHT = 1080;

  @bean()
  export class ScreenModel extends ko.ViewModel {
    $menuCreationLayoutContainer: JQuery = null;
    $menuCreationLayout: JQuery = null;
    $hoverHighlight: JQuery = null;
    $copyPlaceholder: JQuery = null;
    $listPart: JQuery[] = [];
    partClientId: number = 0;
    mapPartData: any = {};
    modifiedPartList: ModifiedPartList = new ModifiedPartList();
    maxHeight: KnockoutObservable<number> = ko.observable(0);
    maxWidth: KnockoutObservable<number> = ko.observable(0);
    layoutSizeText: KnockoutObservable<string>;
    flowMenuCode: KnockoutObservable<string> = ko.observable(null);
    flowMenuFileId: KnockoutObservable<string> = ko.observable(null);
    flowMenuData: KnockoutObservable<FlowMenuLayoutDto> = ko.observable(null);
    menuName: KnockoutObservable<string> = ko.observable(null);

    isMouseInsideLayout: KnockoutObservable<boolean> = ko.observable(false);
    isCopying: KnockoutObservable<boolean> = ko.observable(false);
    copyingPartId: KnockoutObservable<number> = ko.observable(null);
    layoutOffsetLeft: KnockoutObservable<number> = ko.observable(null);
    layoutOffsetTop: KnockoutObservable<number> = ko.observable(null);

    created(params: any) {
      const vm = this;
      vm.flowMenuCode(params.flowMenuCode);
      if (params.flowMenuData) {
        const flowMenuData: FlowMenuLayoutDto = params.flowMenuData;
        vm.flowMenuFileId(flowMenuData.fileId);
        vm.flowMenuData(flowMenuData);
        vm.menuName(`${vm.flowMenuCode()} ${vm.flowMenuData().flowMenuName}`);
      }
      // Init text resource
      vm.layoutSizeText = ko.computed(() => vm.$i18n('CCG034_50', [vm.maxWidth().toString(), vm.maxHeight().toString()]));
    }

    mounted() {
      const vm = this;
      // Store creation layout as class variable for easier access
      vm.$menuCreationLayout = $(`#${MENU_CREATION_LAYOUT_ID}`);
      vm.$menuCreationLayoutContainer = $('.menu-creation-layout-container');
      vm.$menuCreationLayout
        .outerWidth(CREATION_LAYOUT_WIDTH)
        .outerHeight(CREATION_LAYOUT_HEIGHT);
      // Init dragable item
      $(".menu-creation-option").draggable({
        appendTo: `#${MENU_CREATION_LAYOUT_ID}`,
        helper: "clone",
        start: (event, ui) => {
          LayoutUtils.startDragItemFromMenu(ui);
        },
        drag: (event, ui) => {
          const partSize = LayoutUtils.getPartSize(ui.helper.attr(KEY_DATA_PART_TYPE));
          vm.renderHoveringItemOnDrag(ui, partSize.width, partSize.height);
        },
        stop: (event, ui) => {
          vm.$hoverHighlight.remove();
          if (vm.isMouseInsideLayout()) {
            vm.createItemFromMenu(ui, ui.helper.attr(KEY_DATA_PART_TYPE));
          }
        },
      });
      // Init dropable layout
      vm.$menuCreationLayout
        .droppable({
          accept: ".menu-creation-item-container",
        })
        .mouseenter(() => vm.isMouseInsideLayout(true))
        .mouseleave(() => vm.isMouseInsideLayout(false))
        .mousedown((event) => {
          $(".part-setting").filter((index, e) => $(e).css('display') === ('block')).removeAttr("style");
          // If layout in copy mode and mouse cursor is stay inside layout
          if (vm.isCopying() && vm.isMouseInsideLayout()) {
            // Stop copy mode
            vm.isCopying(false);
            // Clear mouse event handler
            vm.$menuCreationLayout.off('mousemove');
            // Remove placeholder
            vm.$copyPlaceholder.remove();
            vm.$hoverHighlight.remove();
            // Create copy item
            const offsetX = event.pageX - vm.layoutOffsetLeft();
            const offsetY = event.pageY - vm.layoutOffsetTop();
            // Calculate copy item div position
            const oldPartData = vm.mapPartData[vm.copyingPartId()];
            const positionTop: number = LayoutUtils.calculatePositionTop(oldPartData.height, offsetY);
            const positionLeft: number = LayoutUtils.calculatePositionLeft(oldPartData.width, offsetX);
            // Check overlap
            const overlappingParts: JQuery[] = vm.getOverlappingPart(new PartDataModel({
              width: oldPartData.width,
              height: oldPartData.height,
              positionTop: positionTop,
              positionLeft: positionLeft,
            }));
            if (!overlappingParts.length) {
              // Create new part div
              const newPartData: PartDataModel = vm.copyPartData(oldPartData, positionTop, positionLeft);
              vm.createDOMFromData(newPartData);
            }
          }
        });
      // Load part DOMs to creation layout
      if (vm.flowMenuData()) {
        vm.$blockui('grayout');
        vm.loadPartDomToLayout(vm.flowMenuData());
        vm.$blockui('clear');
      }
      // Get standardMenu
      vm.getStandardMenu();
      // Re-calculate resolution
      vm.calculateResolution();
      // Focus
      $("#D6").focus();
    }

    /**
     * Load part DOMs to creation layout
     */
    private loadPartDomToLayout(flowData: any): void {
      const vm = this;
      const $partDOMs: JQuery[] = [];
      // MenuSettingDto
      for (const partDataDto of flowData.menuData) {
        const newPartData = vm.createPartDataFromDtoMenu(partDataDto);
        // Set part data to layout
        $partDOMs.push(vm.createDOMFromData(newPartData));
      }
      // LabelSettingDto
      for (const partDataDto of flowData.labelData) {
        const newPartData = vm.createPartDataFromDtoLabel(partDataDto);
        // Set part data to layout
        $partDOMs.push(vm.createDOMFromData(newPartData));
      }
      // LinkSettingDto
      for (const partDataDto of flowData.linkData) {
        const newPartData = vm.createPartDataFromDtoLink(partDataDto);
        // Set part data to layout
        $partDOMs.push(vm.createDOMFromData(newPartData));
      }
      // FileAttachmentSettingDto
      for (const partDataDto of flowData.fileAttachmentData) {
        const newPartData = vm.createPartDataFromDtoFileAttachment(partDataDto);
        if (LayoutUtils.isValidFile(newPartData.fileId)) {
          vm.$ajax("/shr/infra/file/storage/isexist/" + newPartData.fileId).then((isExist: boolean) => {
            if (isExist) {
              vm.$ajax("/shr/infra/file/storage/infor/" + newPartData.fileId)
              .then(res => {
                newPartData.fileName = res.originalName;
                // Set part data to layout
                $partDOMs.push(vm.createDOMFromData(newPartData));
              });
            } else {
              // Set part data to layout
              $partDOMs.push(vm.createDOMFromData(newPartData));
            }
          });
        } else {
          // Set part data to layout
          $partDOMs.push(vm.createDOMFromData(newPartData));
        }
      }
      // ImageSettingDto
      for (const partDataDto of flowData.imageData) {
        const newPartData = vm.createPartDataFromDtoImage(partDataDto);
        // Set part data to layout
        $partDOMs.push(vm.createDOMFromData(newPartData));
      }
      // ArrowSettingDto
      for (const partDataDto of flowData.arrowData) {
        const newPartData = vm.createPartDataFromDtoArrow(partDataDto);
        // Set part data to layout
        $partDOMs.push(vm.createDOMFromData(newPartData));
      }
      // Append new part to layout
      vm.$menuCreationLayout.append($partDOMs);
      // Re-calculate resolution
      vm.calculateResolution();
    }

    /**
     * Create new item on drop from menu
     * @param item
     */
    private createItemFromMenu(part: JQueryUI.DraggableEventUIParams, partType: string) {
      const vm = this;
      const partSize = LayoutUtils.getPartSize(partType);
      // Calculate new part div position
      const positionTop: number = part.position.top > 0 ? Math.round(part.position.top / CELL_SIZE) * CELL_SIZE : 0;
      const positionLeft: number = part.position.left > 0 ? Math.round(part.position.left / CELL_SIZE) * CELL_SIZE : 0;
      // Check overlap
      const overlappingParts: JQuery[] = vm.getOverlappingPart(new PartDataModel({
        width: partSize.width,
        height: partSize.height,
        positionTop: positionTop,
        positionLeft: positionLeft,
      }));
      if (!overlappingParts.length) {
        // Create new part div
        const newPartData: PartDataModel = vm.createDefaultPartData(partType, partSize, positionTop, positionLeft);
        // Check if overlap is allowed or not
        const $newPart: JQuery = vm.createDOMFromData(newPartData);
        // Open PartSetting Dialog
        vm.openPartSettingDialog($newPart, true);
      }
    }

    /**
     * Create new DOM based on part data
     */
    private createDOMFromData(partData: PartDataModel): JQuery {
      const vm = this;
      let $newPartTemplate = null;
      let isInside = false;
      switch (partData.partType) {
        case MenuPartType.PART_MENU:
          $newPartTemplate = $("<div>", { "class": CSS_CLASS_MENU_CREATION_ITEM_CONTAINER }).append($('<div>', { 'class': `${CSS_CLASS_MENU_CREATION_ITEM} part-menu` }));
          break;
        case MenuPartType.PART_LABEL:
          $newPartTemplate = $("<div>", { "class": CSS_CLASS_MENU_CREATION_ITEM_CONTAINER }).append($('<div>', { 'class': `${CSS_CLASS_MENU_CREATION_ITEM} part-label` }));
          break;
        case MenuPartType.PART_LINK:
          $newPartTemplate = $("<div>", { "class": CSS_CLASS_MENU_CREATION_ITEM_CONTAINER }).append($('<div>', { 'class': `${CSS_CLASS_MENU_CREATION_ITEM} part-link` }));
          break;
        case MenuPartType.PART_ATTACHMENT:
          $newPartTemplate = $("<div>", { "class": CSS_CLASS_MENU_CREATION_ITEM_CONTAINER }).append($('<div>', { 'class': `${CSS_CLASS_MENU_CREATION_ITEM} part-attachment` }));
          break;
        case MenuPartType.PART_IMAGE:
          $newPartTemplate = $("<div>", { "class": CSS_CLASS_MENU_CREATION_ITEM_CONTAINER }).append($('<div>', { 'class': `${CSS_CLASS_MENU_CREATION_ITEM} part-image` }));
          break;
        case MenuPartType.PART_ARROW:
          $newPartTemplate = $("<div>", { "class": CSS_CLASS_MENU_CREATION_ITEM_CONTAINER }).append($('<div>', { 'class': `${CSS_CLASS_MENU_CREATION_ITEM} part-arrow` }));
          break;
        default:
          $newPartTemplate = $("<div>", { "class": CSS_CLASS_MENU_CREATION_ITEM_CONTAINER }).append($('<div>', { 'class': `${CSS_CLASS_MENU_CREATION_ITEM} part-menu` }));
          break;
      }
      const $newPart: JQuery = LayoutUtils.renderPartDOM($newPartTemplate, partData)
        .hover(
          handlerIn => LayoutUtils.onPartHover($newPart, true),
          handlerOut => LayoutUtils.onPartHover($newPart, false))
        .on("mousedown", event => LayoutUtils.onPartClickSetting($newPart, isInside));
      // Render div setting
      const $partSetting: JQuery = $("<div>", { "class": 'part-setting' })
        .hover(
          (handlerIn) => isInside = true,
          (handlerOut) => isInside = false)
        .on("click", event => {
          event.stopPropagation();
          // Ignore when clicked inside the popup option
          if (event.target.classList.contains("part-setting-popup-option")) {
            isInside = false;
          }
          LayoutUtils.onPartClickSetting($newPart, isInside);
        });
      const $partSettingPopup: JQuery = $("<div>", { "class": 'part-setting-popup' })
        .css({ 'display': 'none' })
        .append($("<div>", { "class": CSS_CLASS_PART_SETTING_POPUP_OPTION, text: vm.$i18n('CCG034_150') })
          .on('click', (event) => {
            LayoutUtils.onPartClickSetting($newPart, false);
            vm.openPartSettingDialog($newPart);
          }))
        .append($("<div>", { "class": CSS_CLASS_PART_SETTING_POPUP_OPTION, text: vm.$i18n('CCG034_151') })
          .on('click', (event) => {
            LayoutUtils.onPartClickSetting($newPart, false);
            vm.copyPart($newPart);
          }))
        .append($("<div>", { "class": CSS_CLASS_PART_SETTING_POPUP_OPTION, text: vm.$i18n('CCG034_152') })
          .on('click', (event) => {
            LayoutUtils.onPartClickSetting($newPart, false);
            vm.removePart($newPart);
          }));
      $partSettingPopup.appendTo($partSetting);
      $partSetting.appendTo($newPart);
      // Check and remove overlap part (both DOM element and data by calling JQuery.remove())
      // vm.filterOverlappingPart(partData); // No need for filter overlap part
      // Add new item to origin list
      vm.$listPart.push($newPart);
      // Append to creation layout
      vm.$menuCreationLayout.append($newPart);
      // Init selectable creation layout
      vm.$menuCreationLayout.selectable({
        // To disable lasso, set this distance value (pixels) high enough so that selecting will not start
        distance: 1000000,
        selected: (event, ui) => {
          $(ui.selected)
            .addClass(CSS_CLASS_UI_SELECTED)
            .siblings()
            .removeClass(CSS_CLASS_UI_SELECTED);
        },
        unselected: (event, ui) => {
          // Hide part setting on unselected menu item
          if ($(ui.unselected).hasClass(CSS_CLASS_MENU_CREATION_ITEM_CONTAINER)) {
            $(ui.unselected)
              .find('.part-setting-popup')
              .css({ 'display': 'none' });
          }
        }
      });
      // Wait for UI refresh
      vm.$nextTick(() => {
        $newPart
          // Init/enable resize
          .resizable({
            disabled: false,
            resize: (eventResizing, uiResizing) => vm.onItemResizing(eventResizing, uiResizing),
            stop: (eventResizeStop, uiResizeStop) => vm.onItemStopResize(eventResizeStop, uiResizeStop),
          })
          // Init/enable dragable
          .draggable({
            disabled: false,
            containment: `#${MENU_CREATION_LAYOUT_ID}`,
            drag: (eventDraging, uiDraging) => vm.onItemDraging(eventDraging, uiDraging),
            stop: (eventDragStop, uiDragStop) => vm.onItemDragStop(eventDragStop, uiDragStop),
          })
          // Fix bug when resizable/draggable conflict with selectable
          .on("click", () => {
            $newPart
              .addClass(CSS_CLASS_UI_SELECTED)
              .siblings()
              .removeClass(CSS_CLASS_UI_SELECTED);
          });
        vm.$menuCreationLayout.on("click", (e) => {
          if (e.target.id === MENU_CREATION_LAYOUT_ID) {
            $(`.${CSS_CLASS_UI_SELECTED}`).removeClass(CSS_CLASS_UI_SELECTED);
            LayoutUtils.onPartClickSetting($newPart, false);
          }
        });
      });
      // Re-calculate resolution
      vm.calculateResolution();
      return $newPart;
    }

    private onItemResizing(event: Event, ui: JQueryUI.ResizableUIParams) {
      const vm = this;
      const partClientId = Number(ui.element.attr(KEY_DATA_ITEM_CLIENT_ID));
      const partData: PartDataModel = vm.mapPartData[partClientId];
      vm.renderHoveringItemOnResize(ui, partData);
    }

    private onItemStopResize(event: Event, ui: JQueryUI.ResizableUIParams) {
      const vm = this;
      vm.$hoverHighlight.remove();
      vm.resizeItem(ui);
    }

    private onItemDraging(event: Event, ui: JQueryUI.DraggableEventUIParams) {
      const vm = this;
      const partDataClientId = Number(ui.helper.attr(KEY_DATA_ITEM_CLIENT_ID));
      const partData: PartDataModel = vm.mapPartData[partDataClientId];
      vm.renderHoveringItemOnDrag(ui, partData.width, partData.height);
    }

    private onItemDragStop(event: Event, ui: JQueryUI.DraggableEventUIParams) {
      const vm = this;
      vm.$hoverHighlight.remove();
      if (vm.isMouseInsideLayout()) {
        vm.moveItem(ui);
      } else {
        vm.cancelMoveItem(ui);
      }
    }

    /**
     * Render hovering highlight effect on drag
     * @param item
     */
    private renderHoveringItemOnDrag(item: JQueryUI.DraggableEventUIParams, width: number, height: number) {
      const vm = this;
      // Parent layout must have position: relative for item.position to be corrected
      const partClientId = Number(item.helper.attr(KEY_DATA_ITEM_CLIENT_ID));
      // Calculate highlight div position
      const positionTop: number = LayoutUtils.calculatePositionTop(height, item.position.top);
      const positionLeft: number = LayoutUtils.calculatePositionLeft(width, item.position.left);
      vm.renderHoveringItem(item.helper, partClientId, width, height, positionTop, positionLeft);
    }

    /**
     * Render hovering highlight effect on resize
     * @param item
     * @param minWidth
     * @param minHeight
     */
    private renderHoveringItemOnResize(item: JQueryUI.ResizableUIParams, partData: PartDataModel) {
      const vm = this;
      // Calculate highlight div size
      const width: number = item.element.width() > partData.minWidth ? Math.ceil(item.element.width() / CELL_SIZE) * CELL_SIZE : partData.minWidth;
      const height: number = item.element.height() > partData.minHeight ? Math.ceil(item.element.height() / CELL_SIZE) * CELL_SIZE : partData.minHeight;
      vm.renderHoveringItem(item.element, partData.clientId, width, height, partData.positionTop, partData.positionLeft);
    }

    /**
     * Render hovering highlight effect
     * @param width
     * @param height
     * @param positionTop
     * @param positionLeft
     */
    private renderHoveringItem($originPart: JQuery, partClientId: number, width: number, height: number, positionTop: number, positionLeft: number) {
      const vm = this;
      // If not existed, create new highlight div
      if (!vm.$hoverHighlight) {
        vm.$hoverHighlight = $("<div>", { id: ITEM_HIGHLIGHT_ID, "class": "menu-creation-item-highlight" });
      }
      // Set more attr (highlight width, height, position)
      if (vm.isMouseInsideLayout()) {
        vm.$hoverHighlight
          .outerWidth(width)
          .outerHeight(height)
          .css({
            'visibility': 'visible',
            'top': `${positionTop}px`,
            'left': `${positionLeft}px`
          });
      } else {
        vm.$hoverHighlight.css({
          'visibility': 'hidden',
        });
      }
      // Append to creation layout
      vm.$menuCreationLayout.append(vm.$hoverHighlight);
      // Check overlap
      const overlappingParts: JQuery[] = vm.getOverlappingPart(new PartDataModel({
        clientId: partClientId,
        width: width,
        height: height,
        positionTop: positionTop,
        positionLeft: positionLeft,
      }));
      if (overlappingParts.length) {
        // Overlapped, change cursor to not allow
        $originPart.css({ 'cursor': CSS_CURSOR_NOT_ALLOWED });
        $originPart
          .children(`.${CSS_CLASS_UI_RESIZABLE_E}`)
          .css({ 'cursor': CSS_CURSOR_NOT_ALLOWED });
        $originPart
          .children(`.${CSS_CLASS_UI_RESIZABLE_S}`)
          .css({ 'cursor': CSS_CURSOR_NOT_ALLOWED });
        $originPart
          .children(`.${CSS_CLASS_UI_RESIZABLE_SE}`)
          .css({ 'cursor': CSS_CURSOR_NOT_ALLOWED });
      } else {
        // Not overlapped, change cursor to normal
        $originPart.css({ 'cursor': 'auto' });
        $originPart
          .children(`.${CSS_CLASS_UI_RESIZABLE_E}`)
          .css({ 'cursor': 'e-resize' });
        $originPart
          .children(`.${CSS_CLASS_UI_RESIZABLE_S}`)
          .css({ 'cursor': 's-resize' });
        $originPart
          .children(`.${CSS_CLASS_UI_RESIZABLE_SE}`)
          .css({ 'cursor': 'se-resize' });
      }
    }

    /**
     * Resize item on stop resizing
     * @param item
     */
    private resizeItem(item: JQueryUI.ResizableUIParams) {
      const vm = this;
      const partClientId = Number(item.element.attr(KEY_DATA_ITEM_CLIENT_ID));
      const partData: PartDataModel = vm.mapPartData[partClientId];
      // Calculate highlight div size
      const width: number = item.element.width() > partData.minWidth ? Math.ceil(item.element.width() / CELL_SIZE) * CELL_SIZE : partData.minWidth;
      const height: number = item.element.height() > partData.minHeight ? Math.ceil(item.element.height() / CELL_SIZE) * CELL_SIZE : partData.minHeight;
      // Update width + height
      const resizedPartData = $.extend({}, partData);
      resizedPartData.width = width;
      resizedPartData.height = height;
      // Check overlap
      const overlappingParts: JQuery[] = vm.getOverlappingPart(resizedPartData);
      if (overlappingParts.length) {
        // Revert
        item.element.css({ 'cursor': 'auto' });
        item.element
          .children(`.${CSS_CLASS_UI_RESIZABLE_E}`)
          .css({ 'cursor': 'e-resize' });
        item.element
          .children(`.${CSS_CLASS_UI_RESIZABLE_S}`)
          .css({ 'cursor': 's-resize' });
        item.element
          .children(`.${CSS_CLASS_UI_RESIZABLE_SE}`)
          .css({ 'cursor': 'se-resize' });
        vm.cancelResizeItem(item);
      } else {
        // Update part data to map, Update part DOM, Check and remove overlap part (both DOM element and data by calling JQuery.remove())
        vm.mapPartData[partClientId] = resizedPartData;
        LayoutUtils.renderPartDOM(item.element, resizedPartData);
        // Re-calculate resolution
        vm.calculateResolution();
      }
    }

    /**
     * Move item on stop dragging
     * @param item
     */
    private moveItem(item: JQueryUI.DraggableEventUIParams) {
      const vm = this;
      const partClientId = Number(item.helper.attr(KEY_DATA_ITEM_CLIENT_ID));
      const partData: PartDataModel = vm.mapPartData[partClientId];
      // Calculate highlight div position
      const positionTop: number = LayoutUtils.calculatePositionTop(partData.height, item.position.top);
      const positionLeft: number = LayoutUtils.calculatePositionLeft(partData.width, item.position.left);
      // Update positionTop + positionLeft
      const movedPartData = $.extend({}, partData);
      movedPartData.positionTop = positionTop;
      movedPartData.positionLeft = positionLeft;
      // Check overlap
      const overlappingParts: JQuery[] = vm.getOverlappingPart(movedPartData);
      if (overlappingParts.length) {
        // Revert
        item.helper.css({ 'cursor': 'auto' });
        item.helper
          .children(`.${CSS_CLASS_UI_RESIZABLE_E}`)
          .css({ 'cursor': 'e-resize' });
        item.helper
          .children(`.${CSS_CLASS_UI_RESIZABLE_S}`)
          .css({ 'cursor': 's-resize' });
        item.helper
          .children(`.${CSS_CLASS_UI_RESIZABLE_SE}`)
          .css({ 'cursor': 'se-resize' });
        vm.cancelMoveItem(item);
      } else {
        // Update part data to map, Update part DOM, Check and remove overlap part (both DOM element and data by calling JQuery.remove())
        vm.mapPartData[partClientId] = movedPartData;
        LayoutUtils.renderPartDOM(item.helper, movedPartData);
        // Re-calculate resolution
        vm.calculateResolution();
      }
    }

    /**
     * Cancel resize item
     */
    private cancelResizeItem(item: JQueryUI.ResizableUIParams) {
      const vm = this;
      const partClientId = Number(item.element.attr(KEY_DATA_ITEM_CLIENT_ID));
      const partData: PartDataModel = vm.mapPartData[partClientId];
      // Update part DOM
      LayoutUtils.renderPartDOM(item.element, partData);
    }

    /**
     * Cancel move item
     */
    private cancelMoveItem(item: JQueryUI.DraggableEventUIParams) {
      const vm = this;
      const partClientId = Number(item.helper.attr(KEY_DATA_ITEM_CLIENT_ID));
      const partData: PartDataModel = vm.mapPartData[partClientId];
      // Update part DOM
      LayoutUtils.renderPartDOM(item.helper, partData);
    }

    /**
     * Check and remove overlapping part from creation layout
     * @param checkingPart
     */
    private filterOverlappingPart(checkingPart: PartDataModel) {
      const vm = this;
      // Check and remove overlap part (both DOM element and data by calling JQuery.remove())
      const overlappingParts: JQuery[] = vm.getOverlappingPart(checkingPart);
      _.forEach(overlappingParts, (part) => part.remove());
      // Filter overlap part reference from origin list
      vm.$listPart = _.filter(vm.$listPart, ($part) => {
        const partClientId = Number($part.attr(KEY_DATA_ITEM_CLIENT_ID));
        return !LayoutUtils.isItemOverlapping(checkingPart, vm.mapPartData[partClientId]);
      });
    }

    /**
     * Get overlapping Part
     */
    private getOverlappingPart(checkingPart: PartDataModel): JQuery[] {
      const vm = this;
      // Check and remove overlap part (both DOM element and data by calling JQuery.remove())
      return _.filter(vm.$listPart, ($part) => {
        const partClientId = Number($part.attr(KEY_DATA_ITEM_CLIENT_ID));
        return LayoutUtils.isItemOverlapping(checkingPart, vm.mapPartData[partClientId]);
      });
    }

    /**
     * Create default part data
     * @param partType
     * @param partSize
     * @param positionTop
     * @param positionLeft
     */
    private createDefaultPartData(partType: string, partSize: PartSize, positionTop: number, positionLeft: number): PartDataModel {
      const vm = this;
      let newPartData: PartDataModel = null;
      switch (partType) {
        case MenuPartType.PART_MENU:
          newPartData = new PartDataMenuModel({
            // PartData
            clientId: vm.partClientId,
            width: partSize.width,
            height: partSize.height,
            minWidth: CELL_SIZE,
            minHeight: CELL_SIZE,
            partType: partType,
            positionTop: positionTop,
            positionLeft: positionLeft,
            // PartDataMenuModel
          });
          break;
        case MenuPartType.PART_LABEL:
          newPartData = new PartDataLabelModel({
            // PartData
            clientId: vm.partClientId,
            width: partSize.width,
            height: partSize.height,
            minWidth: CELL_SIZE,
            minHeight: CELL_SIZE,
            partType: partType,
            positionTop: positionTop,
            positionLeft: positionLeft,
            // PartDataLabelModel
          });
          break;
        case MenuPartType.PART_LINK:
          newPartData = new PartDataLinkModel({
            // PartData
            clientId: vm.partClientId,
            width: partSize.width,
            height: partSize.height,
            minWidth: CELL_SIZE,
            minHeight: CELL_SIZE,
            partType: partType,
            positionTop: positionTop,
            positionLeft: positionLeft,
            // PartDataLinkModel
          });
          break;
        case MenuPartType.PART_ATTACHMENT:
          newPartData = new PartDataAttachmentModel({
            // PartData
            clientId: vm.partClientId,
            width: partSize.width,
            height: partSize.height,
            minWidth: CELL_SIZE,
            minHeight: CELL_SIZE,
            partType: partType,
            positionTop: positionTop,
            positionLeft: positionLeft,
            // PartDataAttachmentModel
          });
          break;
        case MenuPartType.PART_IMAGE:
          newPartData = new PartDataImageModel({
            // PartData
            clientId: vm.partClientId,
            width: partSize.width,
            height: partSize.height,
            minWidth: CELL_SIZE,
            minHeight: CELL_SIZE,
            partType: partType,
            positionTop: positionTop,
            positionLeft: positionLeft,
            // PartDataImageModel
          });
          break;
        case MenuPartType.PART_ARROW:
          newPartData = new PartDataArrowModel({
            // PartData
            clientId: vm.partClientId,
            width: partSize.width,
            height: partSize.height,
            minWidth: CELL_SIZE,
            minHeight: CELL_SIZE,
            partType: partType,
            positionTop: positionTop,
            positionLeft: positionLeft,
            // PartDataArrowModel
          });
          break;
        default:
          newPartData = new PartDataMenuModel();
          break;
      }
      // Set part data to map
      vm.mapPartData[vm.partClientId] = newPartData;
      vm.partClientId++;
      // Re-calculate resolution
      vm.calculateResolution();
      return newPartData;
    }

    /**
     * Create part data from MenuSettingDto
     */
    private createPartDataFromDtoMenu(dto: MenuSettingDto): PartDataMenuModel {
      const vm = this;
      const newPartData: PartDataMenuModel = new PartDataMenuModel({
        // Common data
        clientId: vm.partClientId,
        width: dto.width,
        height: dto.height,
        minWidth: CELL_SIZE,
        minHeight: CELL_SIZE,
        partType: MenuPartType.PART_MENU,
        positionTop: dto.row * CELL_SIZE,
        positionLeft: dto.column * CELL_SIZE,
        // Menu data
        alignHorizontal: dto.horizontalPosition,
        alignVertical: dto.verticalPosition,
        menuCode: dto.menuCode,
        menuName: dto.menuName,
        menuClassification: dto.menuClassification,
        systemType: dto.systemType,
        fontSize: dto.fontSize,
        isBold: dto.bold === 1,
        menuUrl: null,
        textColor: dto.textColor,
        isFixed: dto.isFixed,
        ratio: dto.ratio,
        fileId: dto.fileId,
        fileName: dto.fileName,
        originalFileId: dto.fileId
      });
      // Set part data to map
      vm.mapPartData[vm.partClientId] = newPartData;
      vm.partClientId++;
      return newPartData;
    }

    /**
     * Create part data from LabelSettingDto
     */
    private createPartDataFromDtoLabel(dto: LabelSettingDto): PartDataLabelModel {
      const vm = this;
      const newPartData: PartDataLabelModel = new PartDataLabelModel({
        // Common data
        clientId: vm.partClientId,
        width: dto.width,
        height: dto.height,
        minWidth: CELL_SIZE,
        minHeight: CELL_SIZE,
        partType: MenuPartType.PART_LABEL,
        positionTop: dto.row * CELL_SIZE,
        positionLeft: dto.column * CELL_SIZE,
        // Label data
        alignHorizontal: dto.horizontalPosition,
        alignVertical: dto.verticalPosition,
        labelContent: dto.labelContent,
        fontSize: dto.fontSize,
        isBold: dto.bold === 1,
        textColor: dto.textColor,
        backgroundColor: dto.backgroundColor,
      });
      // Set part data to map
      vm.mapPartData[vm.partClientId] = newPartData;
      vm.partClientId++;
      return newPartData;
    }

    /**
     * Create part data from LinkSettingDto
     */
    private createPartDataFromDtoLink(dto: LinkSettingDto): PartDataLinkModel {
      const vm = this;
      const newPartData: PartDataLinkModel = new PartDataLinkModel({
        // Common data
        clientId: vm.partClientId,
        width: dto.width,
        height: dto.height,
        minWidth: CELL_SIZE,
        minHeight: CELL_SIZE,
        partType: MenuPartType.PART_LINK,
        positionTop: dto.row * CELL_SIZE,
        positionLeft: dto.column * CELL_SIZE,
        // Link data
        alignHorizontal: dto.horizontalPosition,
        alignVertical: dto.verticalPosition,
        url: dto.url,
        linkContent: dto.linkContent,
        fontSize: dto.fontSize,
        isBold: dto.bold === 1,
      });
      // Set part data to map
      vm.mapPartData[vm.partClientId] = newPartData;
      vm.partClientId++;
      return newPartData;
    }

    /**
     * Create part data from FileAttachmentSettingDto
     */
    private createPartDataFromDtoFileAttachment(dto: FileAttachmentSettingDto): PartDataAttachmentModel {
      const vm = this;
      const newPartData: PartDataAttachmentModel = new PartDataAttachmentModel({
        // Common data
        clientId: vm.partClientId,
        width: dto.width,
        height: dto.height,
        minWidth: CELL_SIZE,
        minHeight: CELL_SIZE,
        partType: MenuPartType.PART_ATTACHMENT,
        positionTop: dto.row * CELL_SIZE,
        positionLeft: dto.column * CELL_SIZE,
        // Attachment data
        alignHorizontal: dto.horizontalPosition,
        alignVertical: dto.verticalPosition,
        fileId: dto.fileId,
        fileSize: null,
        fileName: null,
        fileLink: null,
        linkContent: dto.linkContent,
        fontSize: dto.fontSize,
        isBold: dto.bold === 1,
        originalFileId: dto.fileId
      });
      // Set part data to map
      vm.mapPartData[vm.partClientId] = newPartData;
      vm.partClientId++;
      return newPartData;
    }

    /**
     * Create part data from ImageSettingDto
     */
    private createPartDataFromDtoImage(dto: ImageSettingDto): PartDataImageModel {
      const vm = this;
      const newPartData: PartDataImageModel = new PartDataImageModel({
        // Common data
        clientId: vm.partClientId,
        width: dto.width,
        height: dto.height,
        minWidth: CELL_SIZE,
        minHeight: CELL_SIZE,
        partType: MenuPartType.PART_IMAGE,
        positionTop: dto.row * CELL_SIZE,
        positionLeft: dto.column * CELL_SIZE,
        // Image data
        fileId: dto.fileId,
        fileName: dto.fileName,
        uploadedFileName: null,
        uploadedFileSize: null,
        isFixed: dto.isFixed,
        ratio: dto.ratio,
        originalFileId: dto.fileId
      });
      // Set part data to map
      vm.mapPartData[vm.partClientId] = newPartData;
      vm.partClientId++;
      return newPartData;
    }

    /**
     * Create part data from ArrowSettingDto
     */
    private createPartDataFromDtoArrow(dto: ArrowSettingDto): PartDataArrowModel {
      const vm = this;
      const newPartData: PartDataArrowModel = new PartDataArrowModel({
        // Common data
        clientId: vm.partClientId,
        width: dto.width,
        height: dto.height,
        minWidth: CELL_SIZE,
        minHeight: CELL_SIZE,
        partType: MenuPartType.PART_ARROW,
        positionTop: dto.row * CELL_SIZE,
        positionLeft: dto.column * CELL_SIZE,
        // Arrow data
        fileName: dto.fileName,
        fileSrc: dto.fileName,
      });
      // Set part data to map
      vm.mapPartData[vm.partClientId] = newPartData;
      vm.partClientId++;
      return newPartData;
    }

    /**
     * Copy part data
     */
    private copyPartData(originPartData: PartDataModel, positionTop: number, positionLeft: number): PartDataModel {
      const vm = this;
      let newPartData: PartDataModel = null;
      switch (originPartData.partType) {
        case MenuPartType.PART_MENU:
          newPartData = new PartDataMenuModel(originPartData);
          const menuFileId = (originPartData as PartDataMenuModel).fileId;
          if (LayoutUtils.isValidFile(menuFileId)) {
            vm.$ajax(nts.uk.text.format(API.copyFile, (originPartData as PartDataMenuModel).fileId))
            .then(res => {
              (newPartData as PartDataMenuModel).fileId = res.fileId;
              (newPartData as PartDataMenuModel).originalFileId = null;
              vm.modifiedPartList.added.push(res.fileId);
            });
          } else {
            (newPartData as PartDataMenuModel).fileId = "";
            (newPartData as PartDataMenuModel).originalFileId = null;
            vm.modifiedPartList.added.push(null);
          }
          break;
        case MenuPartType.PART_LABEL:
          newPartData = new PartDataLabelModel(originPartData);
          break;
        case MenuPartType.PART_LINK:
          newPartData = new PartDataLinkModel(originPartData);
          break;
        case MenuPartType.PART_ATTACHMENT:
          newPartData = new PartDataAttachmentModel(originPartData);
          const attachmentFileId = (originPartData as PartDataAttachmentModel).fileId;
          if (LayoutUtils.isValidFile(attachmentFileId)) {
            vm.$ajax(nts.uk.text.format(API.copyFile, (originPartData as PartDataAttachmentModel).fileId))
            .then(res => {
              (newPartData as PartDataAttachmentModel).fileId = res.fileId;
              (newPartData as PartDataAttachmentModel).originalFileId = null;
              vm.modifiedPartList.added.push(res.fileId);
            });
          } else {
            (newPartData as PartDataAttachmentModel).fileId = "";
            (newPartData as PartDataAttachmentModel).originalFileId = null;
            vm.modifiedPartList.added.push(null);
          }
          break;
        case MenuPartType.PART_IMAGE:
          newPartData = new PartDataImageModel(originPartData);
          const imageFileId = (originPartData as PartDataImageModel).fileId;
          if ((originPartData as PartDataImageModel).isFixed === 1) {
            if (LayoutUtils.isValidFile(imageFileId)) {
              vm.$ajax(nts.uk.text.format(API.copyFile, (originPartData as PartDataImageModel).fileId))
              .then(res => {
                (newPartData as PartDataImageModel).fileId = res.fileId;
                (newPartData as PartDataImageModel).originalFileId = null;
                vm.modifiedPartList.added.push(res.fileId);
              });
            } else {
              (newPartData as PartDataImageModel).fileId = "";
              (newPartData as PartDataImageModel).originalFileId = null;
              vm.modifiedPartList.added.push(null);
            }
          }
          break;
        case MenuPartType.PART_ARROW:
          newPartData = new PartDataArrowModel(originPartData);
          break;
        default:
          newPartData = new PartDataMenuModel(originPartData);
          break;
      }
      // Set part data to map
      newPartData.clientId = vm.partClientId;
      newPartData.positionTop = positionTop;
      newPartData.positionLeft = positionLeft;
      vm.mapPartData[vm.partClientId] = newPartData;
      vm.partClientId++;
      return newPartData;
    }

    /**
     * Open Part Setting Dialog
     * @param partClientId
     */
    private openPartSettingDialog($part: JQuery, isCreateDialog: boolean = false) {
      const vm = this;
      const partClientId = Number($part.attr(KEY_DATA_ITEM_CLIENT_ID));
      const selectedPartData: PartDataModel = vm.mapPartData[partClientId];
      if (!selectedPartData) {
        return;
      }
      switch (selectedPartData.partType) {
        case MenuPartType.PART_MENU:
          vm.$window.modal('/view/ccg/034/f/index.xhtml', selectedPartData)
            .then((result: PartDataModel) => {
              if (result && !nts.uk.text.isNullOrEmpty(String(result.clientId))) {
                const partMenu = (result as PartDataMenuModel);
                // Only prepare for delete if has changes
                if (!_.find(vm.modifiedPartList.added, partMenu.fileId) && partMenu.fileId !== partMenu.originalFileId) {
                  vm.modifiedPartList.added.push(partMenu.fileId);
                }
                if (!_.find(vm.modifiedPartList.deleted, partMenu.originalFileId) 
                  && (partMenu.fileId !== partMenu.originalFileId || (partMenu.isFixed !== 1 && !_.isEmpty(partMenu.originalFileId)))) {
                  vm.modifiedPartList.deleted.push(partMenu.originalFileId);
                }
                // Update part data
                vm.mapPartData[partClientId] = result;
                // Update part DOM
                LayoutUtils.renderPartDOMMenu($part, result as PartDataMenuModel);
              } else if (isCreateDialog) {
                // If this is dialog setitng when create => remove part
                vm.removePart($part);
              }
            });
          break;
        case MenuPartType.PART_LABEL:
          vm.$window.modal('/view/ccg/034/e/index.xhtml', selectedPartData)
            .then((result: PartDataModel) => {
              if (result && !nts.uk.text.isNullOrEmpty(String(result.clientId))) {
                // Update part data
                vm.mapPartData[partClientId] = result;
                // Update part DOM
                LayoutUtils.renderPartDOMLabel($part, result as PartDataLabelModel);
              } else if (isCreateDialog) {
                // If this is dialog setitng when create => remove part
                vm.removePart($part);
              }
            });
          break;
        case MenuPartType.PART_LINK:
          vm.$window.modal('/view/ccg/034/g/index.xhtml', selectedPartData)
            .then((result: PartDataModel) => {
              if (result && !nts.uk.text.isNullOrEmpty(String(result.clientId))) {
                // Update part data
                vm.mapPartData[partClientId] = result;
                // Update part DOM
                LayoutUtils.renderPartDOMLink($part, result as PartDataLinkModel);
              } else if (isCreateDialog) {
                // If this is dialog setitng when create => remove part
                vm.removePart($part);
              }
            });
          break;
        case MenuPartType.PART_ATTACHMENT:
          vm.$window.modal('/view/ccg/034/h/index.xhtml', selectedPartData)
            .then((result: any) => {
              if (result.isSaving) {
                const partAttachment = (result.partData as PartDataAttachmentModel);
                // Only prepare for delete if has changes
                if (!_.find(vm.modifiedPartList.added, partAttachment.fileId) && partAttachment.fileId !== partAttachment.originalFileId) {
                  vm.modifiedPartList.added.push(partAttachment.fileId);
                }
                if (!_.find(vm.modifiedPartList.deleted, partAttachment.originalFileId) && partAttachment.fileId !== partAttachment.originalFileId) {
                  vm.modifiedPartList.deleted.push(partAttachment.originalFileId);
                }
                // Update part data
                vm.mapPartData[partClientId] = result.partData;
                // Update part DOM
                LayoutUtils.renderPartDOMAttachment($part, result.partData as PartDataAttachmentModel);
              } else {
                if (isCreateDialog) {
                  // If this is dialog setitng when create => remove part
                  vm.removePart($part);
                }
                vm.removeFile(result.fileId);
              }
            });
          break;
        case MenuPartType.PART_IMAGE:
          vm.$window.modal('/view/ccg/034/i/index.xhtml', selectedPartData)
            .then((result: any) => {
              if (result.isSaving) {
                const partImage = (result.partData as PartDataImageModel);
                // Only prepare for delete if has changes
                if (!_.find(vm.modifiedPartList.added, partImage.fileId) && partImage.fileId !== partImage.originalFileId) {
                  vm.modifiedPartList.added.push(partImage.fileId);
                }
                if (!_.find(vm.modifiedPartList.deleted, partImage.originalFileId) && partImage.fileId !== partImage.originalFileId) {
                  vm.modifiedPartList.deleted.push(partImage.originalFileId);
                }
                // Update part data
                vm.mapPartData[partClientId] = result.partData;
                // Update part DOM
                LayoutUtils.renderPartDOMImage($part, result.partData as PartDataImageModel);
              } else {
                if (isCreateDialog) {
                  // If this is dialog setitng when create => remove part
                  vm.removePart($part);
                }
                vm.removeFile(result.fileId);
              }
            });
          break;
        case MenuPartType.PART_ARROW:
          vm.$window.modal('/view/ccg/034/j/index.xhtml', selectedPartData)
            .then((result: PartDataModel) => {
              if (result && !nts.uk.text.isNullOrEmpty(String(result.clientId))) {
                // Update part data
                vm.mapPartData[partClientId] = result;
                // Update part DOM
                LayoutUtils.renderPartDOMArrow($part, result as PartDataArrowModel);
              } else if (isCreateDialog) {
                // If this is dialog setitng when create => remove part
                vm.removePart($part);
              }
            });
          break;
        default:
          break;
      }
      // Re-calculate resolution
      vm.calculateResolution();
    }

    /**
     * Copy part
     * @param $part
     */
    private copyPart($part: JQuery) {
      const vm = this;
      const partClientId = Number($part.attr(KEY_DATA_ITEM_CLIENT_ID));
      const partData = vm.mapPartData[partClientId];
      // Start copy mode
      vm.isCopying(true);
      vm.copyingPartId(partClientId);
      // Create new placeholder div
      switch (partData.partType) {
        case MenuPartType.PART_MENU:
          vm.$copyPlaceholder = $("<div>", { id: ITEM_COPY_PLACEHOLDER_ID, "class": CSS_CLASS_MENU_CREATION_ITEM_COPY_PLACEHOLDER })
            .append($('<div>', { 'class': `${CSS_CLASS_MENU_CREATION_ITEM} part-menu` }));
          break;
        case MenuPartType.PART_LABEL:
          vm.$copyPlaceholder = $("<div>", { id: ITEM_COPY_PLACEHOLDER_ID, "class": CSS_CLASS_MENU_CREATION_ITEM_COPY_PLACEHOLDER })
            .append($('<div>', { 'class': `${CSS_CLASS_MENU_CREATION_ITEM} part-label` }));
          break;
        case MenuPartType.PART_LINK:
          vm.$copyPlaceholder = $("<div>", { id: ITEM_COPY_PLACEHOLDER_ID, "class": CSS_CLASS_MENU_CREATION_ITEM_COPY_PLACEHOLDER })
            .append($('<div>', { 'class': `${CSS_CLASS_MENU_CREATION_ITEM} part-link` }));
          break;
        case MenuPartType.PART_ATTACHMENT:
          vm.$copyPlaceholder = $("<div>", { id: ITEM_COPY_PLACEHOLDER_ID, "class": CSS_CLASS_MENU_CREATION_ITEM_COPY_PLACEHOLDER })
            .append($('<div>', { 'class': `${CSS_CLASS_MENU_CREATION_ITEM} part-attachment` }));
          break;
        case MenuPartType.PART_IMAGE:
          vm.$copyPlaceholder = $("<div>", { id: ITEM_COPY_PLACEHOLDER_ID, "class": CSS_CLASS_MENU_CREATION_ITEM_COPY_PLACEHOLDER })
            .append($('<div>', { 'class': `${CSS_CLASS_MENU_CREATION_ITEM} part-image` }));
          break;
        case MenuPartType.PART_ARROW:
          vm.$copyPlaceholder = $("<div>", { id: ITEM_COPY_PLACEHOLDER_ID, "class": CSS_CLASS_MENU_CREATION_ITEM_COPY_PLACEHOLDER })
            .append($('<div>', { 'class': `${CSS_CLASS_MENU_CREATION_ITEM} part-arrow` }));
          break;
        default:
          vm.$copyPlaceholder = $("<div>");
          break;
      }
      // Set more attr (highlight width, height, position)
      LayoutUtils.renderPartDOM(vm.$copyPlaceholder, partData);
      // Append to creation layout
      vm.$menuCreationLayout.append(vm.$copyPlaceholder);
      // Move placeholder on mouse move
      const layoutOffset = vm.$menuCreationLayout.offset();
      vm.layoutOffsetTop(layoutOffset.top);
      vm.layoutOffsetLeft(layoutOffset.left);
      vm.$menuCreationLayout.mousemove((event) => {
        const offsetX = event.pageX - layoutOffset.left;
        const offsetY = event.pageY - layoutOffset.top;
        vm.$copyPlaceholder.css({ 'top': `${offsetY}px`, 'left': `${offsetX}px` });
        // Calculate highlight div position
        const positionTop: number = LayoutUtils.calculatePositionTop(partData.height, offsetY);
        const positionLeft: number = LayoutUtils.calculatePositionLeft(partData.width, offsetX);
        vm.renderHoveringItem(vm.$copyPlaceholder, vm.partClientId, partData.width, partData.height, positionTop, positionLeft);
      });
    }

    /**
     * Remove part
     */
    private removePart($part: JQuery) {
      const vm = this;
      const partClientId = Number($part.attr(KEY_DATA_ITEM_CLIENT_ID));
      const partData: PartDataModel = vm.mapPartData[partClientId];
      switch (partData.partType) {
        case MenuPartType.PART_MENU:
        case MenuPartType.PART_ATTACHMENT:
        case MenuPartType.PART_IMAGE:
          const part = partData as any;
          // If newly added component -> delete immediately
          // Or else will put to remove list
          if (!part.originalFileId) {
            vm.removeFile(part.fileId);
          } else {
            vm.modifiedPartList.deleted.push(part.fileId);
            vm.modifiedPartList.deleted.push(part.originalFileId);
          }
          break;
      }
      delete vm.mapPartData[partClientId];
      vm.$listPart = _.filter(vm.$listPart, ($item) => Number($item.attr(KEY_DATA_ITEM_CLIENT_ID)) !== partClientId);
      $part.remove();
      // Re-calculate resolution
      vm.calculateResolution();
    }

    /**
     * Close dialog
     */
    public closeDialog() {
      const vm = this;
      _.forEach(_.uniq(vm.modifiedPartList.added), fileId => vm.removeFile(fileId));
      vm.$window.close();
    }

    /**
     * Open preview dialog
     */
    public openPreviewDialog() {
      const vm = this;
      const $layout: JQuery = $('<div>')
        .css({ 'width': CREATION_LAYOUT_WIDTH, 'height': CREATION_LAYOUT_HEIGHT });
      for (const partClientId in vm.mapPartData) {
        $layout.append(LayoutUtils.buildPartHTML(vm.mapPartData[partClientId]));
      }
      const params = {
        fileId: vm.flowMenuFileId(),
        htmlSrc: vm.createHTMLLayout($layout),
      };
      vm.$window.modal('/view/ccg/034/b/index.xhtml', params, {
        width: Math.round(Number(window.parent.innerWidth) * 70 / 100),
        height: Math.round(Number(window.parent.innerHeight) * 80 / 100),
        resizable: true,
      });
    }

    /**
     * Save layout
     */
    public saveLayout() {
      const vm = this;
      // Save html as file
      const listPartData: PartDataModel[] = [];
      const $layout: JQuery = $('<div>')
        .css({ 'width': CREATION_LAYOUT_WIDTH, 'height': CREATION_LAYOUT_HEIGHT });
      for (const partClientId in vm.mapPartData) {
        listPartData.push(vm.mapPartData[partClientId]);
        $layout.append(LayoutUtils.buildPartHTML(vm.mapPartData[partClientId]));
      }
      vm.$blockui('grayout');
      const generateHtmlParams: any = {
        flowMenuCode: vm.flowMenuCode(),
        htmlContent: vm.createHTMLLayout($layout),
      };
      // アップロードファイルの保存及びHTMLのファイルを作成ZIPファイルとし保存する
      vm.$ajax(API.generateHtml, generateHtmlParams)
        // [After] generate html file
        .then((res: { taskId: string }) => {
          vm.flowMenuFileId(res.taskId);
          // Prepare command
          const listMenuSettingDto: MenuSettingDto[] = _.chain(listPartData)
            .filter((data: PartDataModel) => data.partType === MenuPartType.PART_MENU)
            .map((data: PartDataMenuModel) => new MenuSettingDto({
              flowMenuCode: vm.flowMenuCode(),
              column: (data.positionLeft / CELL_SIZE),
              row: (data.positionTop / CELL_SIZE),
              width: data.width,
              height: data.height,
              fontSize: data.fontSize,
              bold: data.isBold ? 1 : 0,
              horizontalPosition: _.isNil(data.isFixed) ? data.alignHorizontal : HorizontalAlign.MIDDLE,
              verticalPosition: _.isNil(data.isFixed) ? data.alignVertical : VerticalAlign.BOTTOM,
              systemType: data.systemType,
              menuClassification: data.menuClassification,
              menuCode: data.menuCode,
              menuName: data.menuName,
              textColor: data.textColor,
              isFixed: data.isFixed,
              ratio: data.ratio,
              fileId: data.isFixed === 1 ? data.fileId : null,
              fileName: data.fileName
            }))
            .value();
          const listLabelSettingDto: LabelSettingDto[] = _.chain(listPartData)
            .filter((data: PartDataModel) => data.partType === MenuPartType.PART_LABEL)
            .map((data: PartDataLabelModel) => new LabelSettingDto({
              flowMenuCode: vm.flowMenuCode(),
              column: (data.positionLeft / CELL_SIZE),
              row: (data.positionTop / CELL_SIZE),
              width: data.width,
              height: data.height,
              labelContent: data.labelContent,
              fontSize: data.fontSize,
              bold: data.isBold ? 1 : 0,
              textColor: data.textColor,
              backgroundColor: data.backgroundColor,
              horizontalPosition: data.alignHorizontal,
              verticalPosition: data.alignVertical,
            }))
            .value();
          const listLinkSettingDto: LinkSettingDto[] = _.chain(listPartData)
            .filter((data: PartDataModel) => data.partType === MenuPartType.PART_LINK)
            .map((data: PartDataLinkModel) => new LinkSettingDto({
              flowMenuCode: vm.flowMenuCode(),
              column: (data.positionLeft / CELL_SIZE),
              row: (data.positionTop / CELL_SIZE),
              width: data.width,
              height: data.height,
              linkContent: data.linkContent,
              url: data.url,
              fontSize: data.fontSize,
              bold: data.isBold ? 1 : 0,
              horizontalPosition: data.alignHorizontal,
              verticalPosition: data.alignVertical,
            }))
            .value();
          const listFileAttachmentSettingDto: FileAttachmentSettingDto[] = _.chain(listPartData)
            .filter((data: PartDataModel) => data.partType === MenuPartType.PART_ATTACHMENT)
            .map((data: PartDataAttachmentModel) => new FileAttachmentSettingDto({
              flowMenuCode: vm.flowMenuCode(),
              column: (data.positionLeft / CELL_SIZE),
              row: (data.positionTop / CELL_SIZE),
              width: data.width,
              height: data.height,
              fileId: data.fileId,
              linkContent: data.linkContent,
              fontSize: data.fontSize,
              bold: data.isBold ? 1 : 0,
              horizontalPosition: data.alignHorizontal,
              verticalPosition: data.alignVertical,
            }))
            .value();
          const listImageSettingDto: ImageSettingDto[] = _.chain(listPartData)
            .filter((data: PartDataModel) => data.partType === MenuPartType.PART_IMAGE)
            .map((data: PartDataImageModel) => new ImageSettingDto({
              flowMenuCode: vm.flowMenuCode(),
              column: (data.positionLeft / CELL_SIZE),
              row: (data.positionTop / CELL_SIZE),
              width: data.width,
              height: data.height,
              fileId: data.fileId,
              fileName: data.fileName,
              isFixed: data.isFixed,
              ratio: nts.uk.ntsNumber.getDecimal(Number(data.ratio), 2)
            }))
            .value();
          const listArrowSettingDto: ArrowSettingDto[] = _.chain(listPartData)
            .filter((data: PartDataModel) => data.partType === MenuPartType.PART_ARROW)
            .map((data: PartDataArrowModel) => new ArrowSettingDto({
              flowMenuCode: vm.flowMenuCode(),
              column: (data.positionLeft / CELL_SIZE),
              row: (data.positionTop / CELL_SIZE),
              width: data.width,
              height: data.height,
              fileName: data.fileName,
            }))
            .value();
          const updateLayoutParams: UpdateFlowMenuLayoutCommand = new UpdateFlowMenuLayoutCommand({
            flowMenuCode: vm.flowMenuCode(),
            flowMenuLayout: new FlowMenuLayoutDto({
              fileId: res.taskId,
              menuSettings: listMenuSettingDto,
              labelSettings: listLabelSettingDto,
              linkSettings: listLinkSettingDto,
              fileAttachmentSettings: listFileAttachmentSettingDto,
              imageSettings: listImageSettingDto,
              arrowSettings: listArrowSettingDto,
            }),
          });
          // Filter for unused fileIds and deleted them
          _.remove(vm.modifiedPartList.deleted,
            fileId => _.isEmpty(_.filter(listMenuSettingDto, data => data.isFixed !== null && data.fileId === fileId))
                   && _.isEmpty(_.filter(listFileAttachmentSettingDto, data => data.fileId === fileId))
                   && _.isEmpty(_.filter(listImageSettingDto, data => data.fileId === fileId)))
          .forEach(fileId => vm.removeFile(fileId));
          _.remove(vm.modifiedPartList.added, 
            fileId => !_.isEmpty(_.filter(listMenuSettingDto, data => data.isFixed !== null && data.fileId === fileId))
                   || !_.isEmpty(_.filter(listFileAttachmentSettingDto, data => data.fileId === fileId))
                   || !_.isEmpty(_.filter(listImageSettingDto, data => data.fileId === fileId)));
          return vm.$ajax(API.updateLayout, updateLayoutParams);
        })
        // [After] save layout data
        .then(() => {
          vm.$blockui('clear');
          vm.modifiedPartList = new ModifiedPartList();
          vm.updateOriginalFileId();
          vm.$dialog.info({ messageId: 'Msg_15' });
        })
        .fail((err) => vm.$dialog.error({ messageId: err.messageId }))
        .always(() => vm.$blockui('clear'));
    }

    /**
     * create HTML Layout
     */
    private createHTMLLayout($layout: JQuery): string {
      const vm = this;
      let htmlContent = `<!DOCTYPE html>`;
      htmlContent += `<html xmlns="http://www.w3.org/1999/xhtml"`;
      htmlContent += ` xmlns:ui="http://java.sun.com/jsf/facelets"`;
      htmlContent += ` xmlns:com="http://xmlns.jcp.org/jsf/component"`;
      htmlContent += ` xmlns:com="http://xmlns.jcp.org/jsf/html"`;
      htmlContent += `<head>`;
      htmlContent += `<style>`;
      htmlContent += `.ccg034-component-container { word-break: break-all; word-break: break-word; word-wrap: break-word }`;
      htmlContent += `</style>`;
      htmlContent += `<link rel="stylesheet" type="text/css" href="/nts.uk.com.js.web/lib/nittsu/ui/style/stylesheets/base.css">`;
      htmlContent += `</head>`;
      htmlContent += `<body>`;
      htmlContent += `<div class="content-container" style="width: ${vm.maxWidth()}px; height: ${vm.maxHeight()}px; margin: auto; position: relative;">`;
      htmlContent += $layout.html();
      htmlContent += `</div>`;
      htmlContent += `</body>`;
      htmlContent += `</html>`;
      return htmlContent;
    }

    /**
     * Calculate layout resolution
     */
    private calculateResolution() {
      const vm = this;
      let topHeight = 0;
      let topWidth = 0;

      for (const partClientId in vm.mapPartData) {
        const part: PartDataModel = vm.mapPartData[partClientId];
        if (part.height + part.positionTop > topHeight) {
          topHeight = part.height + part.positionTop;
        }
        if (part.width + part.positionLeft > topWidth) {
          topWidth = part.width + part.positionLeft;
        }
      }
      vm.maxHeight(topHeight);
      vm.maxWidth(topWidth);
    }

    /**
     * Get standard menu list
     */
    private getStandardMenu() {
      const vm = this;
      vm.$ajax(API.getMenuList).then((data: any[]) => {
        for (const partClientId in vm.mapPartData) {
          const part: PartDataModel = vm.mapPartData[partClientId];
          if (part.partType === MenuPartType.PART_MENU) {
            const menuPart: PartDataMenuModel = part as PartDataMenuModel;
            const chosenMenu: any = _.find(data, { 'code': menuPart.menuCode, 'system': menuPart.systemType, 'classification': menuPart.menuClassification });
            if (chosenMenu) {
              if (nts.uk.text.isNullOrEmpty(chosenMenu.queryString)) {
                menuPart.menuUrl = `${chosenMenu.url}`;
              } else {
                menuPart.menuUrl = `${chosenMenu.url}?${chosenMenu.queryString}`;
              }
            }
            vm.mapPartData[partClientId] = menuPart;
          }
        }
      });
    }

    /**
     * 変更前のファイルは削除
     */
    private removeFile(fileId: string) {
      if (!nts.uk.text.isNullOrEmpty(fileId)) {
        (nts.uk.request as any).file.remove(fileId);
      }
    }

    private updateOriginalFileId() {
      const vm = this;
      for (const partClientId in vm.mapPartData) {
        const part: PartDataModel = vm.mapPartData[partClientId];
        switch (part.partType) {
          case MenuPartType.PART_ATTACHMENT:
            const attachmentPartData = part as PartDataAttachmentModel;
            attachmentPartData.originalFileId = attachmentPartData.fileId;
            break;
          case MenuPartType.PART_IMAGE:
            const imagePartData = part as PartDataImageModel;
            imagePartData.originalFileId = imagePartData.fileId;
            break;
        }
      }
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

    /**
     * On click part setting
     * @param partClientId
     */
    static onPartClickSetting($part: JQuery, visible: boolean) {
      const $partSetting: JQuery = $part.find('.part-setting');
      const $partSettingPopup: JQuery = $part.find('.part-setting-popup');
      if ($partSettingPopup) {
        if (visible) {
          $partSettingPopup.css('display', 'block');
          $partSetting.css('display', 'block');
        } else {
          $partSettingPopup.css('display', 'none');
        }
      }
    }

    static onPartHover($part: JQuery, isHoverIn: boolean) {
      const $partSetting: JQuery = $part.find('.part-setting');
      const $partSettingPopup: JQuery = $part.find('.part-setting-popup');
      if ($partSettingPopup.css("display") === "none") {
        if (isHoverIn) {
          $partSetting.css('display', 'block');
        } else {
          $partSetting.css('display', 'none');
        }
      }
    }

    /**
     * Create part class
     * @param partType
     */
    static renderPartDOM($part: JQuery, partData: PartDataModel): JQuery {
      const vm = this;
      switch (partData.partType) {
        case MenuPartType.PART_MENU:
          return vm.renderPartDOMMenu($part, partData as PartDataMenuModel);
        case MenuPartType.PART_LABEL:
          return vm.renderPartDOMLabel($part, partData as PartDataLabelModel);
        case MenuPartType.PART_LINK:
          return vm.renderPartDOMLink($part, partData as PartDataLinkModel);
        case MenuPartType.PART_ATTACHMENT:
          return vm.renderPartDOMAttachment($part, partData as PartDataAttachmentModel);
        case MenuPartType.PART_IMAGE:
          return vm.renderPartDOMImage($part, partData as PartDataImageModel);
        case MenuPartType.PART_ARROW:
          return vm.renderPartDOMArrow($part, partData as PartDataArrowModel);
        default:
          return vm.renderPartDOMMenu($part, partData as PartDataMenuModel);
      }
    }

    /**
     * Render PartDataMenuModel
     * @param partData
     */
    static renderPartDOMMenu($partContainer: JQuery, partData: PartDataMenuModel): JQuery {
      const vm = this;
      const hasImg = !_.isNil(partData.isFixed);
      $partContainer
        // Set PartData attr
        .outerWidth(partData.width)
        .outerHeight(partData.height)
        .css({
          'top': `${partData.positionTop}px`,
          'left': `${partData.positionLeft}px`,
        })
        // Update item data object
        .attr(KEY_DATA_ITEM_CLIENT_ID, partData.clientId);
      const $part = $partContainer.find(`.${CSS_CLASS_MENU_CREATION_ITEM}`);
      $part
        // Set PartDataLabelModel attr
        .css({
          'display': 'flex',
          'justify-content': vm.getHorizontalClass(!hasImg ? partData.alignHorizontal : HorizontalAlign.MIDDLE),
          'align-items': vm.getVerticalClass(!hasImg ? partData.alignVertical : VerticalAlign.BOTTOM),
        });
      // Render menu image
      if (hasImg) {
        $part.css({ 'display': 'grid', 'grid-auto-columns': '100%', 'grid-auto-rows': 'minmax(0, 1fr) max-content' });
        let $imageContent = $part.find('.part-image-content');
        const src = (partData.isFixed === 0) ? partData.fileName : (this.isValidFile(partData.fileId) ? (nts.uk.request as any).liveView(partData.fileId) : '');
        if (!$imageContent.length) {
          $imageContent = $("<img>", { 'class': 'part-image-content' });
        }
        $imageContent
          .attr('src', src);
        // Set image scale by original ratio
        const partRatio = partData.height / partData.width;
        const imageRatio = partData.ratio;
        if (partRatio > imageRatio) {
          $imageContent.css({
            'width': '100%',
            'height': 'auto',
          });
        } else {
          $imageContent.css({
            'width': 'auto',
            'height': '100%',
          });
        }
        $imageContent.css({
          'display': 'block',
          'margin': '5px auto auto',
          'max-height': '90%',
          'object-fit': 'contain'
        });
        $imageContent.appendTo($part);
      } else {
        $part.removeProp("grid-auto-columns");
        $part.removeProp("grid-auto-rows");
        $part.find("img").remove();
      }
      // Render label
      let $menuName = $part.find('.part-menu-name');
      if (!$menuName.length) {
        $menuName = $("<span>", { 'class': 'part-menu-name' });
      }
      if (hasImg) {
        $menuName.addClass("limited-label").css("text-align", "center");
      } else {
        $menuName.removeProp("text-align");
        $menuName.removeClass("limited-label");
      }
      $menuName
        .text(partData.menuName)
        .css({
          'font-size': partData.fontSize,
          'font-weight': partData.isBold ? 'bold' : 'normal',
          'color': partData.textColor
        })
        .addClass('hyperlink');
      $menuName.appendTo($part);
      return $partContainer;
    }

    /**
     * Render PartDataLabelModel
     * @param partData
     */
    static renderPartDOMLabel($partContainer: JQuery, partData: PartDataLabelModel): JQuery {
      const vm = this;
      $partContainer
        // Set PartData attr
        .outerWidth(partData.width)
        .outerHeight(partData.height)
        .css({
          'top': `${partData.positionTop}px`,
          'left': `${partData.positionLeft}px`,
        })
        // Update item data object
        .attr(KEY_DATA_ITEM_CLIENT_ID, partData.clientId);
      const $part = $partContainer.find(`.${CSS_CLASS_MENU_CREATION_ITEM}`);
      $part
        // Set PartDataLabelModel attr
        .css({
          'background-color': partData.backgroundColor,
          'display': 'flex',
          'justify-content': vm.getHorizontalClass(partData.alignHorizontal),
          'align-items': vm.getVerticalClass(partData.alignVertical),
        });
      // Render label
      let $labelContent = $part.find('.part-label-content');
      if (!$labelContent.length) {
        $labelContent = $("<span>", { 'class': 'part-label-content' });
      }
      $labelContent
        .text(partData.labelContent)
        .css({
          'color': partData.textColor,
          'font-size': partData.fontSize,
          'font-weight': partData.isBold ? 'bold' : 'normal',
          'white-space': 'pre-wrap',
        });
      $labelContent.appendTo($part);
      return $partContainer;
    }

    /**
     * Render PartDataLinkModel
     * @param partData
     */
    static renderPartDOMLink($partContainer: JQuery, partData: PartDataLinkModel): JQuery {
      const vm = this;
      $partContainer
        // Set PartData attr
        .outerWidth(partData.width)
        .outerHeight(partData.height)
        .css({
          'top': `${partData.positionTop}px`,
          'left': `${partData.positionLeft}px`,
        })
        // Update item data object
        .attr(KEY_DATA_ITEM_CLIENT_ID, partData.clientId);
      const $part = $partContainer.find(`.${CSS_CLASS_MENU_CREATION_ITEM}`);
      $part
        // Set PartDataLabelModel attr
        .css({
          'display': 'flex',
          'justify-content': vm.getHorizontalClass(partData.alignHorizontal),
          'align-items': vm.getVerticalClass(partData.alignVertical),
        });
      // Render label
      let $linkContent = $part.find('.part-link-content');
      if (!$linkContent.length) {
        $linkContent = $("<span>", { 'class': 'part-link-content' });
      }
      $linkContent
        .text(partData.linkContent || partData.url)
        .css({
          'font-size': partData.fontSize,
          'font-weight': partData.isBold ? 'bold' : 'normal',
        })
        .addClass('hyperlink');
      $linkContent.appendTo($part);
      return $partContainer;
    }

    /**
     * Render PartDataAttachmentModel
     * @param partData
     */
    static renderPartDOMAttachment($partContainer: JQuery, partData: PartDataAttachmentModel): JQuery {
      const vm = this;

      $partContainer
        // Set PartData attr
        .outerWidth(partData.width)
        .outerHeight(partData.height)
        .css({
          'top': `${partData.positionTop}px`,
          'left': `${partData.positionLeft}px`,
        })
        // Update item data object
        .attr(KEY_DATA_ITEM_CLIENT_ID, partData.clientId);
      const $part = $partContainer.find(`.${CSS_CLASS_MENU_CREATION_ITEM}`);
      $part
        // Set PartDataLabelModel attr
        .css({
          'display': 'flex',
          'justify-content': vm.getHorizontalClass(partData.alignHorizontal),
          'align-items': vm.getVerticalClass(partData.alignVertical),
        });
      // Render label
      let $fileContent = $part.find('.part-file-content');
      if (!$fileContent.length) {
        $fileContent = $("<span>", { 'class': 'part-file-content' });
      }
      $fileContent
        .text(partData.linkContent || partData.fileName)
        .css({
          'font-size': partData.fontSize,
          'font-weight': partData.isBold ? 'bold' : 'normal',
        })
        .addClass('hyperlink');
      $fileContent.appendTo($part);
      return $partContainer;
    }

    /**
     * Render PartDataImageModel
     * @param partData
     */
    static renderPartDOMImage($partContainer: JQuery, partData: PartDataImageModel): JQuery {
      $partContainer
        // Set PartData attr
        .outerWidth(partData.width)
        .outerHeight(partData.height)
        .css({
          'top': `${partData.positionTop}px`,
          'left': `${partData.positionLeft}px`,
          'align-items': 'center'
        })
        // Update item data object
        .attr(KEY_DATA_ITEM_CLIENT_ID, partData.clientId);
      const $part = $partContainer.find(`.${CSS_CLASS_MENU_CREATION_ITEM}`);
      $part
        // Set PartDataLabelModel attr
        .css({
          'display': 'flex',
        });
      // Render label
      let $imageContent = $part.find('.part-image-content');
      if (!$imageContent.length) {
        $imageContent = $("<img>", { 'class': 'part-image-content' });
      }
      $imageContent
        .attr('src', (partData.isFixed === 0) ? partData.fileName : (this.isValidFile(partData.fileId) ? (nts.uk.request as any).liveView(partData.fileId) : ''));
      // Set image scale by original ratio
      const partRatio = partData.height / partData.width;
      const imageRatio = partData.ratio;
      if (partRatio > imageRatio) {
        $imageContent.css({
          'width': '100%',
          'height': 'auto',
        });
      } else {
        $imageContent.css({
          'width': 'auto',
          'height': '100%',
        });
      }
      $imageContent.appendTo($part);
      return $partContainer;
    }

    /**
     * Render PartDataArrowModel
     * @param partData
     */
    static renderPartDOMArrow($partContainer: JQuery, partData: PartDataArrowModel): JQuery {
      $partContainer
        // Set PartData attr
        .outerWidth(partData.width)
        .outerHeight(partData.height)
        .css({
          'top': `${partData.positionTop}px`,
          'left': `${partData.positionLeft}px`,
        })
        // Update item data object
        .attr(KEY_DATA_ITEM_CLIENT_ID, partData.clientId);
      const $part = $partContainer.find(`.${CSS_CLASS_MENU_CREATION_ITEM}`);
      $part
        // Set PartDataLabelModel attr
        .css({
          'display': 'flex',
        });
      // Render label
      let $arrowContent = $part.find('.part-arrow-content');
      if (!$arrowContent.length) {
        $arrowContent = $("<img>", { 'class': 'part-arrow-content' });
      }
      $arrowContent.attr('src', partData.fileSrc);
      $arrowContent.css({
        'width': '100%',
        'height': '100%',
      });
      $arrowContent.appendTo($part);
      return $partContainer;
    }

    /**
     * getHorizontalClass
     */
    static getHorizontalClass(alignHorizontal: number): string {
      let horizontalPosition: string = null;
      switch (alignHorizontal) {
        case HorizontalAlign.LEFT:
          horizontalPosition = 'flex-start';
          break;
        case HorizontalAlign.MIDDLE:
          horizontalPosition = 'center';
          break;
        case HorizontalAlign.RIGHT:
          horizontalPosition = 'flex-end';
          break;
        default:
          horizontalPosition = 'flex-start';
          break;
      }
      return horizontalPosition;
    }

    /**
     * getVerticalClass
     */
    static getVerticalClass(alignVertical: number): string {
      let verticalPosition: string = null;
      switch (alignVertical) {
        case VerticalAlign.TOP:
          verticalPosition = 'flex-start';
          break;
        case VerticalAlign.CENTER:
          verticalPosition = 'center';
          break;
        case VerticalAlign.BOTTOM:
          verticalPosition = 'flex-end';
          break;
        default:
          verticalPosition = 'flex-start';
          break;
      }
      return verticalPosition;
    }

    static calculatePositionTop(itemHeight: number, positionTop: number): number {
      const maxPositionTop = CREATION_LAYOUT_HEIGHT - itemHeight;
      if (positionTop <= 0) {
        return 0;
      }
      return positionTop + itemHeight <= CREATION_LAYOUT_HEIGHT
        ? Math.round(positionTop / CELL_SIZE) * CELL_SIZE
        : (maxPositionTop / CELL_SIZE) * CELL_SIZE;
    }

    static calculatePositionLeft(itemWidth: number, positionLeft: number): number {
      const maxPositionLeft = CREATION_LAYOUT_WIDTH - itemWidth;
      if (positionLeft <= 0) {
        return 0;
      }
      return positionLeft + itemWidth <= CREATION_LAYOUT_WIDTH
        ? Math.round(positionLeft / CELL_SIZE) * CELL_SIZE
        : (maxPositionLeft / CELL_SIZE) * CELL_SIZE;
    }

    /**
     * Detects if two item part are colliding
     * https://gist.github.com/jtsternberg/c272d7de5b967cec2d3d
     * @param partData1
     * @param partData2
     */
    static isItemOverlapping(partData1: PartDataModel, partData2: PartDataModel): boolean {
      if (partData1.clientId === partData2.clientId) {
        return false;
      }
      // Part data 1
      const partData1DistanceFromTop = partData1.positionTop + partData1.height;
      const partData1DistanceFromLeft = partData1.positionLeft + partData1.width;
      // Part data 2
      const partData2DistanceFromTop = partData2.positionTop + partData2.height;
      const partData2DistanceFromLeft = partData2.positionLeft + partData2.width;

      const notColliding = (partData1DistanceFromTop <= partData2.positionTop
        || partData1.positionTop >= partData2DistanceFromTop
        || partData1DistanceFromLeft <= partData2.positionLeft
        || partData1.positionLeft >= partData2DistanceFromLeft);

      // Return whether it IS colliding
      return !notColliding;
    }

    /**
     * Get part size by type
     * @param partType
     */
    static getPartSize(partType: string): PartSize {
      switch (partType) {
        case MenuPartType.PART_MENU:
          // 3 x 3 cell
          return new PartSize({ width: CELL_SIZE * 3, height: CELL_SIZE * 3 });
        case MenuPartType.PART_LABEL:
          // 4 x 2 cell
          return new PartSize({ width: CELL_SIZE * 4, height: CELL_SIZE * 2 });
        case MenuPartType.PART_LINK:
          // 4 x 2 cell
          return new PartSize({ width: CELL_SIZE * 4, height: CELL_SIZE * 2 });
        case MenuPartType.PART_ATTACHMENT:
          // 4 x 2 cell
          return new PartSize({ width: CELL_SIZE * 4, height: CELL_SIZE * 2 });
        case MenuPartType.PART_IMAGE:
          // 2 x 2 cell
          return new PartSize({ width: CELL_SIZE * 2, height: CELL_SIZE * 2 });
        case MenuPartType.PART_ARROW:
          // 2 x 2 cell
          return new PartSize({ width: CELL_SIZE * 2, height: CELL_SIZE * 2 });
        default:
          // 4 x 2 cell
          return new PartSize({ width: CELL_SIZE * 4, height: CELL_SIZE * 2 });
      }
    }

    /**
     * build PartHTML from PartData
     */
    static buildPartHTML(partData: PartDataModel): JQuery {
      let $partHTML = null;
      switch (partData.partType) {
        case MenuPartType.PART_MENU:
          const partDataMenuModel: PartDataMenuModel = (partData as PartDataMenuModel);
          partDataMenuModel.originalFileId = partDataMenuModel.fileId;
          const hasImg = !_.isNil(partDataMenuModel.isFixed);
          const $partMenuHTML: JQuery = $('<a>', { 'href': `${location.origin}${partDataMenuModel.menuUrl}`, 'target': '_top' })
            .text(partDataMenuModel.menuName)
            .addClass(CSS_CLASS_CCG034_HYPERLINK)
            .css({
              'font-size': `${partDataMenuModel.fontSize}px`,
              'font-weight': partDataMenuModel.isBold ? 'bold' : 'normal',
              'color': partDataMenuModel.textColor,
              'text-decoration': 'underline',
              'cursor': 'pointer',
            });
          $partHTML = $("<div>")
            .addClass("ccg034-component-container")
            .css({
              'position': 'absolute',
              'top': `${partDataMenuModel.positionTop}px`,
              'left': `${partDataMenuModel.positionLeft}px`,
              'width': `${partDataMenuModel.width}px`,
              'height': `${partDataMenuModel.height}px`,
              'display': 'flex',
              'justify-content': LayoutUtils.getHorizontalClass(!hasImg ? partDataMenuModel.alignHorizontal : HorizontalAlign.MIDDLE),
              'align-items': LayoutUtils.getVerticalClass(!hasImg ? partDataMenuModel.alignHorizontal : VerticalAlign.BOTTOM),
              'overflow': 'hidden',
              'text-overflow': 'ellipsis',
            });
          if (hasImg) {
            const src = (partDataMenuModel.isFixed === 0) ? partDataMenuModel.fileName : (this.isValidFile(partDataMenuModel.fileId) ? (nts.uk.request as any).liveView(partDataMenuModel.fileId) : '');
            const partRatio = partDataMenuModel.height / partDataMenuModel.width;
            const imageRatio = partDataMenuModel.ratio;
            $partHTML.css({ 'display': 'grid', 'grid-auto-columns': '100%', 'grid-auto-rows': 'minmax(0, 1fr) max-content' });
            const $partImageContainer = $('<a>', { 'href': `${location.origin}${partDataMenuModel.menuUrl}`, 'target': '_top' })
              .addClass(CSS_CLASS_CCG034_HYPERLINK)
              .css({ "align-self": "start", "height": "100%" });
            const $partImage = $("<img>")
              .addClass("ccg034-hyperlink")
              .attr("src", src);
            if (partRatio > imageRatio) {
              $partImage.css({
                'width': '100%',
                'height': 'auto',
              });
            } else {
              $partImage.css({
                'width': 'auto',
                'height': '100%',
              });
            }
            $partImage.css({
              'display': 'block',
              'margin': '5px auto auto',
              'max-height': '90%',
              'object-fit': 'contain'
            });
            $partMenuHTML.addClass("limited-label").css("text-align", "center");
            $partHTML.prepend($partImageContainer.append($partImage));
          }
          $partHTML.append($partMenuHTML);
          break;
        case MenuPartType.PART_LABEL:
          const partDataLabelModel: PartDataLabelModel = (partData as PartDataLabelModel);
          const $partLabelHTML: JQuery = $('<span>')
            .text(partDataLabelModel.labelContent)
            .css({
              'font-size': `${partDataLabelModel.fontSize}px`,
              'font-weight': partDataLabelModel.isBold ? 'bold' : 'normal',
              'color': partDataLabelModel.textColor,
              'white-space': 'pre-wrap',
            });
          $partHTML = $("<div>")
            .addClass("ccg034-component-container")
            .css({
              'position': 'absolute',
              'top': `${partDataLabelModel.positionTop}px`,
              'left': `${partDataLabelModel.positionLeft}px`,
              'width': `${partDataLabelModel.width}px`,
              'height': `${partDataLabelModel.height}px`,
              'display': 'flex',
              'align-items': LayoutUtils.getVerticalClass(partDataLabelModel.alignVertical),
              'justify-content': LayoutUtils.getHorizontalClass(partDataLabelModel.alignHorizontal),
              'overflow': 'hidden',
              'text-overflow': 'ellipsis',
              'background-color': partDataLabelModel.backgroundColor,
            })
            .append($partLabelHTML);
          break;
        case MenuPartType.PART_LINK:
          const partDataLinkModel: PartDataLinkModel = (partData as PartDataLinkModel);
          const $partLinkHTML: JQuery = $('<a>', { 'href': partDataLinkModel.url, 'target': '_blank' })
            .text(partDataLinkModel.linkContent || partDataLinkModel.url)
            .addClass(CSS_CLASS_CCG034_HYPERLINK)
            .css({
              'font-size': `${partDataLinkModel.fontSize}px`,
              'font-weight': partDataLinkModel.isBold ? 'bold' : 'normal',
              'color': '#0066CC',
              'text-decoration': 'underline',
              'cursor': 'pointer',
            });
          $partHTML = $("<div>")
            .addClass("ccg034-component-container")
            .css({
              'position': 'absolute',
              'top': `${partDataLinkModel.positionTop}px`,
              'left': `${partDataLinkModel.positionLeft}px`,
              'width': `${partDataLinkModel.width}px`,
              'height': `${partDataLinkModel.height}px`,
              'display': 'flex',
              'align-items': LayoutUtils.getVerticalClass(partDataLinkModel.alignVertical),
              'justify-content': LayoutUtils.getHorizontalClass(partDataLinkModel.alignHorizontal),
              'overflow': 'hidden',
              'text-overflow': 'ellipsis',
            })
            .append($partLinkHTML);
          break;
        case MenuPartType.PART_ATTACHMENT:
          const partDataAttachmentModel: PartDataAttachmentModel = (partData as PartDataAttachmentModel);
          partDataAttachmentModel.originalFileId = partDataAttachmentModel.fileId;
          const fileLink: string = `${location.origin}/nts.uk.com.web/webapi/shr/infra/file/storage/get/${partDataAttachmentModel.fileId}`;
          const $partAttachmentHTML: JQuery = $('<a>', { 'href': fileLink, 'target': '_blank' })
            .text(partDataAttachmentModel.linkContent || partDataAttachmentModel.fileName)
            .addClass(CSS_CLASS_CCG034_HYPERLINK)
            .css({
              'font-size': `${partDataAttachmentModel.fontSize}px`,
              'font-weight': partDataAttachmentModel.isBold ? 'bold' : 'normal',
              'color': '#0066CC',
              'text-decoration': 'underline',
              'cursor': 'pointer',
            });
          $partHTML = $("<div>")
            .addClass("ccg034-component-container")
            .css({
              'position': 'absolute',
              'top': `${partDataAttachmentModel.positionTop}px`,
              'left': `${partDataAttachmentModel.positionLeft}px`,
              'width': `${partDataAttachmentModel.width}px`,
              'height': `${partDataAttachmentModel.height}px`,
              'display': 'flex',
              'align-items': LayoutUtils.getVerticalClass(partDataAttachmentModel.alignVertical),
              'justify-content': LayoutUtils.getHorizontalClass(partDataAttachmentModel.alignHorizontal),
              'overflow': 'hidden',
              'text-overflow': 'ellipsis',
            })
            .append($partAttachmentHTML);
          break;
        case MenuPartType.PART_IMAGE:
          const partDataImageModel: PartDataImageModel = (partData as PartDataImageModel);
          partDataImageModel.originalFileId = partDataImageModel.fileId;
          const $partImageHTML: JQuery = $('<img>', {
            'src': partDataImageModel.isFixed === 0
              ? partDataImageModel.fileName
              : partDataImageModel.fileId
                ? (nts.uk.request as any).liveView(partDataImageModel.fileId)
                : null
          })
            .css({
              'width': (partDataImageModel.width > partDataImageModel.height) ? 'auto' : '100%',
              'height': (partDataImageModel.width > partDataImageModel.height) ? '100%' : 'auto',
            });
          $partHTML = $("<div>")
            .css({
              'position': 'absolute',
              'top': `${partDataImageModel.positionTop}px`,
              'left': `${partDataImageModel.positionLeft}px`,
              'width': `${partDataImageModel.width}px`,
              'height': `${partDataImageModel.height}px`,
              'display': 'flex',
              'align-items': 'center',
              'justify-content': 'center',
              'overflow': 'hidden',
            })
            .append($partImageHTML);
          break;
        case MenuPartType.PART_ARROW:
          const partDataArrowModel: PartDataArrowModel = (partData as PartDataArrowModel);
          const $partArrowHTML: JQuery = $('<img>', { 'src': partDataArrowModel.fileSrc })
            .css({
              'width': '100%',
              'height': '100%',
            });
          $partHTML = $("<div>")
            .css({
              'position': 'absolute',
              'top': `${partDataArrowModel.positionTop}px`,
              'left': `${partDataArrowModel.positionLeft}px`,
              'width': `${partDataArrowModel.width}px`,
              'height': `${partDataArrowModel.height}px`,
              'display': 'flex',
              'align-items': 'center',
              'justify-content': 'center',
              'overflow': 'hidden',
            })
            .append($partArrowHTML);
          break;
        default:
          $partHTML = $("<div>")
            .css({
              'position': 'absolute',
              'top': `${partData.positionTop}px`,
              'left': `${partData.positionLeft}px`,
              'width': `${partData.width}px`,
              'height': `${partData.height}px`,
            });
          break;
      }
      return $partHTML;
    }

    static isValidFile(fileId: string): boolean {
      return !nts.uk.text.isNullOrEmpty(fileId);
    }
  }

  export class PartSize {
    width: number;
    height: number;

    constructor(init?: Partial<PartSize>) {
      $.extend(this, init);
    }
  }

  export enum MenuPartType {
    PART_MENU = '1',
    PART_LABEL = '2',
    PART_LINK = '3',
    PART_ATTACHMENT = '4',
    PART_IMAGE = '5',
    PART_ARROW = '6',
  }

  export enum HorizontalAlign {
    LEFT = 0,
    MIDDLE = 1,
    RIGHT = 2,
  }

  export enum VerticalAlign {
    TOP = 0,
    CENTER = 1,
    BOTTOM = 2,
  }

  export class PartDataModel {
    clientId: number;
    width: number;
    height: number;
    minWidth: number;
    minHeight: number;
    partType: string;
    positionTop: number;
    positionLeft: number;

    constructor(init?: Partial<PartDataModel>) {
      $.extend(this, init);
    }
  }

  export class PartDataMenuModel extends PartDataModel {
    alignHorizontal: number = HorizontalAlign.MIDDLE;
    alignVertical: number = VerticalAlign.CENTER;
    menuCode: string = null;
    menuName = "";
    menuClassification = 0;
    systemType = 0;
    fontSize = 11;
    isBold = true;
    menuUrl: string = null;
    textColor: string = "#000000";
    isFixed: number = null;
    ratio: number = null;
    fileId: string = "";
    fileName: string = "";
    originalFileId: string = "";

    constructor(init?: Partial<PartDataMenuModel>) {
      super(init);
      $.extend(this, init);
    }
  }

  export class PartDataLabelModel extends PartDataModel {
    // Default data
    alignHorizontal: number = HorizontalAlign.LEFT;
    alignVertical: number = VerticalAlign.CENTER;
    labelContent = '';
    fontSize = 11;
    isBold = true;
    textColor = '#000000';
    backgroundColor = '#ffffff';

    constructor(init?: Partial<PartDataLabelModel>) {
      super(init);
      $.extend(this, init);
    }
  }

  export class PartDataLinkModel extends PartDataModel {
    // Default data
    alignHorizontal: number = HorizontalAlign.LEFT;
    alignVertical: number = VerticalAlign.CENTER;
    url: string = null;
    linkContent = '';
    fontSize = 11;
    isBold = true;

    constructor(init?: Partial<PartDataLinkModel>) {
      super(init);
      $.extend(this, init);
    }
  }

  export class PartDataAttachmentModel extends PartDataModel {
    // Default data
    alignHorizontal: number = HorizontalAlign.LEFT;
    alignVertical: number = VerticalAlign.CENTER;
    fileId: string = null;
    fileSize = 0;
    fileName: string = null;
    linkContent = '';
    fileLink: string = null;
    fontSize = 11;
    isBold: boolean = true;
    originalFileId: string = null;

    constructor(init?: Partial<PartDataAttachmentModel>) {
      super(init);
      $.extend(this, init);
    }
  }

  export class PartDataImageModel extends PartDataModel {
    // Default data
    fileId: string = null;
    fileName: string = null;
    uploadedFileName: string = null;
    uploadedFileSize = 0;
    isFixed = 0;
    ratio = 1;
    originalFileId: string = null;

    constructor(init?: Partial<PartDataImageModel>) {
      super(init);
      $.extend(this, init);
    }
  }

  export class PartDataArrowModel extends PartDataModel {
    // Default data
    fileName: string = null;
    fileSrc: string = null;

    constructor(init?: Partial<PartDataArrowModel>) {
      super(init);
      $.extend(this, init);
    }
  }

  class UpdateFlowMenuLayoutCommand {
    flowMenuCode: string;
    flowMenuLayout: FlowMenuLayoutDto;

    constructor(init?: Partial<UpdateFlowMenuLayoutCommand>) {
      $.extend(this, init);
    }
  }

  class FlowMenuLayoutDto {
    fileId: string;
    flowMenuCode: string;
    flowMenuName: string;
    menuSettings: MenuSettingDto[];
    labelSettings: LabelSettingDto[];
    linkSettings: LinkSettingDto[];
    fileAttachmentSettings: FileAttachmentSettingDto[];
    imageSettings: ImageSettingDto[];
    arrowSettings: ArrowSettingDto[];

    constructor(init?: Partial<FlowMenuLayoutDto>) {
      $.extend(this, init);
    }
  }

  export class MenuSettingDto {
    flowMenuCode: string;
    column: number;   // pixel / cellsize
    row: number;      // pixel / cellsize
    width: number;    // pixel
    height: number;    // pixel
    fontSize: number;
    bold: number;
    horizontalPosition: number;
    verticalPosition: number;
    systemType: number;
    menuClassification: number;
    menuCode: string;
    menuName: string;
    textColor: string;
    isFixed: number;
    ratio: number;
    fileId: string;
    fileName: string;

    constructor(init?: Partial<MenuSettingDto>) {
      $.extend(this, init);
    }
  }

  export class LabelSettingDto {
    flowMenuCode: string;
    column: number;   // pixel / cellsize
    row: number;      // pixel / cellsize
    width: number;    // pixel
    height: number;    // pixel
    labelContent: string;
    fontSize: number;
    bold: number;
    textColor: string;
    backgroundColor: string;
    horizontalPosition: number;
    verticalPosition: number;

    constructor(init?: Partial<LabelSettingDto>) {
      $.extend(this, init);
    }
  }

  export class LinkSettingDto {
    flowMenuCode: string;
    column: number;   // pixel / cellsize
    row: number;      // pixel / cellsize
    width: number;    // pixel
    height: number;    // pixel
    linkContent: string;
    url: string;
    fontSize: number;
    bold: number;
    horizontalPosition: number;
    verticalPosition: number;

    constructor(init?: Partial<LinkSettingDto>) {
      $.extend(this, init);
    }
  }

  export class FileAttachmentSettingDto {
    flowMenuCode: string;
    column: number;   // pixel / cellsize
    row: number;      // pixel / cellsize
    width: number;    // pixel
    height: number;    // pixel
    fileId: string;
    linkContent: string;
    fontSize: number;
    bold: number;
    horizontalPosition: number;
    verticalPosition: number;

    constructor(init?: Partial<FileAttachmentSettingDto>) {
      $.extend(this, init);
    }
  }

  export class ImageSettingDto {
    flowMenuCode: string;
    column: number;   // pixel / cellsize
    row: number;      // pixel / cellsize
    width: number;    // pixel
    height: number;    // pixel
    fileId: string;
    fileName: string;
    isFixed: number;
    ratio: number;

    constructor(init?: Partial<ImageSettingDto>) {
      $.extend(this, init);
    }
  }

  export class ArrowSettingDto {
    flowMenuCode: string;
    column: number;   // pixel / cellsize
    row: number;      // pixel / cellsize
    width: number;    // pixel
    height: number;
    fileName: string;

    constructor(init?: Partial<ArrowSettingDto>) {
      $.extend(this, init);
    }
  }

  export class ModifiedPartList {
    added?: string[] = [];
    deleted?: string[] = [];
  }
}
