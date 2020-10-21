/// <reference path="../../../../lib/nittsu/viewcontext.d.ts" />

module nts.uk.com.view.ccg034.d {

  // URL API backend
  const API = {
    // ...
  }

  const MENU_CREATION_LAYOUT_ID: string = 'menu-creation-layout';
  const ITEM_HIGHLIGHT_ID: string = 'item-highlight';
  const CELL_SIZE: number = 40;
  const CREATION_LAYOUT_WIDTH: number = 1920;
  const CREATION_LAYOUT_HEIGHT: number = 1080;

  @bean()
  export class ScreenModel extends ko.ViewModel {
    CommonModel: any = null;

    $menuCreationLayout: JQuery = null;
    $hoverHighlight: JQuery = null;
    $listPart: JQuery[] = [];
    partClientId: number = 0;
    mapPartData: any = {};
    layoutSizeText: KnockoutObservable<string> = ko.observable('');

    mounted() {
      const vm = this;
      // Init text resource
      vm.layoutSizeText(vm.$i18n('CCG034_50', [CREATION_LAYOUT_WIDTH.toString(), CREATION_LAYOUT_HEIGHT.toString()]));
      // Store creation layout as class variable for easier access
      vm.$menuCreationLayout = $(`#${MENU_CREATION_LAYOUT_ID}`);
      vm.$menuCreationLayout
        .outerWidth(CREATION_LAYOUT_WIDTH)
        .outerHeight(CREATION_LAYOUT_HEIGHT);
      // Init dragable item
      $(".menu-creation-option").draggable({
        appendTo: `#${MENU_CREATION_LAYOUT_ID}`,
        helper: "clone",
        start: (event, ui) => {
          vm.startDragItemFromMenu(ui);
        },
        drag: (event, ui) => {
          const partSize = vm.getPartSize(ui.helper.attr("data-part-type"));
          vm.renderHoveringItemOnDrag(ui, partSize.width, partSize.height);
        },
        stop: (event, ui) => {
          vm.removeHoveringItem();
          vm.createItemFromMenu(ui, ui.helper.attr("data-part-type"));
        },
      });
      // Init dropable layout
      vm.$menuCreationLayout.droppable({
        accept: ".menu-creation-item",
      });
    }

    /**
     * Start drag item from menu
     * @param item
     * @param width
     * @param height
     */
    private startDragItemFromMenu(item: JQueryUI.DraggableEventUIParams) {
      // Init size + style for dragging item
      item.helper.css({ 'opacity': '0.7' });
    }

    /**
     * Create new item on drop from menu
     * @param item
     */
    private createItemFromMenu(part: JQueryUI.DraggableEventUIParams, partType: string) {
      const vm = this;
      const partSize = vm.getPartSize(partType);
      // Calculate new part div position
      const positionTop: number = part.position.top > 0 ? Math.round(part.position.top / CELL_SIZE) * CELL_SIZE : 0;
      const positionLeft: number = part.position.left > 0 ? Math.round(part.position.left / CELL_SIZE) * CELL_SIZE : 0;
      // Create new part div
      const newPartData: PartData = vm.createDefaultPartData(partType, partSize, positionTop, positionLeft);
      const newPart: JQuery = vm.createDefaultPartDOM(partType, newPartData);
      // Check and remove overlap part (both DOM element and data by calling JQuery.remove())
      vm.filterOverlappingPart(newPartData);
      // Add new item to origin list
      vm.$listPart.push(newPart);
      // Append to creation layout
      vm.$menuCreationLayout.append(newPart);
      // Init selectable creation layout
      vm.$menuCreationLayout.selectable({
        selected: (event, ui) => {
          $(ui.selected)
            .addClass("ui-selected")
            .siblings()
            .removeClass("ui-selected");
          // Wait for UI refresh
          vm.$nextTick(() => {
            if ($(ui.selected).hasClass('menu-creation-item')) {
              // Only allow dragable + resize on selected menu item
              if ($(ui.selected).hasClass('ui-selected')) {
                $(ui.selected)
                  // Init/enable resize
                  .resizable({
                    disabled: false,
                    resize: (event, ui) => {
                      vm.renderHoveringItemOnResize(ui);
                    },
                    stop: (event, ui) => {
                      vm.removeHoveringItem();
                      vm.resizeItem(ui);
                    },
                  })
                  // Init/enable dragable
                  .draggable({
                    disabled: false,
                    containment: `#${MENU_CREATION_LAYOUT_ID}`,
                    drag: (event, ui) => {
                      const partData: PartData = ui.helper.data();
                      vm.renderHoveringItemOnDrag(ui, partData.width, partData.height);
                    },
                    stop: (event, ui) => {
                      vm.removeHoveringItem();
                      vm.moveItem(ui);
                    },
                  });
              } else {
                // Disable dragable + resize on unselected menu item
                $(ui.selected)
                  .resizable({ disabled: true })
                  .draggable({ disabled: true });
              }
            }
          });
        },
        unselected: (event, ui) => {
          // Disable dragable + resize on unselected menu item
          if ($(ui.unselected).hasClass('menu-creation-item')) {
            $(ui.unselected)
              .resizable({ disabled: true })
              .draggable({ disabled: true });
            $(ui.unselected)
              .find('.part-setting-popup')
              .css({ 'visibility': 'hidden' });
          }
        }
      });
    }

    /**
     * Render hovering highlight effect on drag
     * @param item
     */
    private renderHoveringItemOnDrag(item: JQueryUI.DraggableEventUIParams, width: number, height: number) {
      // Parent layout must have position: relative for item.position to be corrected
      const vm = this;
      // Calculate highlight div position
      let positionTop: number = item.position.top > 0 ? Math.round(item.position.top / CELL_SIZE) * CELL_SIZE : 0;
      let positionLeft: number = item.position.left > 0 ? Math.round(item.position.left / CELL_SIZE) * CELL_SIZE : 0;
      vm.renderHoveringItem(width, height, positionTop, positionLeft);
    }

    /**
     * Render hovering highlight effect on resize
     * @param item
     * @param minWidth
     * @param minHeight
     */
    private renderHoveringItemOnResize(item: JQueryUI.ResizableUIParams) {
      // Parent layout must have position: relative for item.position to be corrected
      const vm = this;
      const partData: PartData = item.element.data();
      // Calculate highlight div size
      const width: number = item.element.width() > partData.minWidth ? Math.ceil(item.element.width() / CELL_SIZE) * CELL_SIZE : partData.minWidth;
      const height: number = item.element.height() > partData.minHeight ? Math.ceil(item.element.height() / CELL_SIZE) * CELL_SIZE : partData.minHeight;
      vm.renderHoveringItem(width, height, partData.positionTop, partData.positionLeft);
    }

    /**
     * Render hovering highlight effect
     * @param width
     * @param height
     * @param positionTop
     * @param positionLeft
     */
    private renderHoveringItem(width: number, height: number, positionTop: number, positionLeft: number) {
      const vm = this;
      // If not existed, create new highlight div
      if (!vm.$hoverHighlight) {
        vm.$hoverHighlight = $("<div>", { id: ITEM_HIGHLIGHT_ID, "class": "menu-creation-item-highlight" });
      }
      // Set more attr (highlight width, height, position)
      vm.$hoverHighlight
        .outerWidth(width)
        .outerHeight(height)
        .css({ 'top': `${positionTop}px`, 'left': `${positionLeft}px` });
      // Append to creation layout
      vm.$menuCreationLayout.append(vm.$hoverHighlight);
    }

    /**
     * Remove hovering highlight effect on drag stop
     */
    private removeHoveringItem() {
      const vm = this;
      vm.$hoverHighlight.remove();
    }

    /**
     * Resize item on stop resizing
     * @param item
     */
    private resizeItem(item: JQueryUI.ResizableUIParams) {
      const vm = this;
      const partData: PartData = item.element.data();
      // Calculate highlight div size
      const width: number = item.element.width() > partData.minWidth ? Math.ceil(item.element.width() / CELL_SIZE) * CELL_SIZE : partData.minWidth;
      const height: number = item.element.height() > partData.minHeight ? Math.ceil(item.element.height() / CELL_SIZE) * CELL_SIZE : partData.minHeight;
      // Update width + height
      partData.width = width;
      partData.height = height;
      // Update part data to map
      vm.mapPartData[partData.clientId] = partData;
      // Update part DOM
      const newPart: JQuery = vm.createDefaultPartDOM(partData.partType, partData);
      item.element.replaceWith(newPart);
      // Check and remove overlap part (both DOM element and data by calling JQuery.remove())
      vm.filterOverlappingPart(partData);
    }

    /**
     * Move item on stop dragging
     * @param item
     */
    private moveItem(item: JQueryUI.DraggableEventUIParams) {
      const vm = this;
      const partData: PartData = item.helper.data();
      // Calculate highlight div position
      const positionTop: number = item.position.top > 0 ? Math.round(item.position.top / CELL_SIZE) * CELL_SIZE : 0;
      const positionLeft: number = item.position.left > 0 ? Math.round(item.position.left / CELL_SIZE) * CELL_SIZE : 0;
      // Update positionTop + positionLeft
      partData.positionTop = positionTop;
      partData.positionLeft = positionLeft;
      // Update part data to map
      vm.mapPartData[partData.clientId] = partData;
      // Update part DOM
      const newPart: JQuery = vm.createDefaultPartDOM(partData.partType, partData);
      item.helper.replaceWith(newPart);
      // Check and remove overlap part (both DOM element and data by calling JQuery.remove())
      vm.filterOverlappingPart(partData);
    }

    /**
     * Check and remove overlapping part from creation layout
     * @param checkingPart
     */
    private filterOverlappingPart(checkingPart: PartData) {
      const vm = this;
      // Check and remove overlap part (both DOM element and data by calling JQuery.remove())
      const overlappingParts: JQuery[] = _.filter(vm.$listPart, (part) => vm.isItemOverlapping(checkingPart, part.data()));
      _.forEach(overlappingParts, (part) => part.remove());
      // Filter overlap part reference from origin list
      vm.$listPart = _.filter(vm.$listPart, (part) => !vm.isItemOverlapping(checkingPart, part.data()));
    }

    /**
     * Detects if two item part are colliding
     * https://gist.github.com/jtsternberg/c272d7de5b967cec2d3d
     * @param partData1
     * @param partData2
     */
    private isItemOverlapping(partData1: PartData, partData2: PartData): boolean {
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
    private getPartSize(partType: string): PartSize {
      switch (partType) {
        case MenuPartType.PART_MENU:
          // 4 x 2 cell
          return new PartSize({ width: 160, height: 80 });
        case MenuPartType.PART_LABEL:
          // 4 x 2 cell
          return new PartSize({ width: 160, height: 80 });
        case MenuPartType.PART_LINK:
          // 4 x 2 cell
          return new PartSize({ width: 160, height: 80 });
        case MenuPartType.PART_ATTACHMENT:
          // 4 x 2 cell
          return new PartSize({ width: 160, height: 80 });
        case MenuPartType.PART_IMAGE:
          // 2 x 2 cell
          return new PartSize({ width: 80, height: 80 });
        case MenuPartType.PART_ARROW:
          // 2 x 2 cell
          return new PartSize({ width: 80, height: 80 });
        default:
          // 4 x 2 cell
          return new PartSize({ width: 160, height: 80 });
      }
    }

    /**
     * Create default part data
     * @param partType
     * @param partSize
     * @param positionTop
     * @param positionLeft
     */
    private createDefaultPartData(partType: string, partSize: PartSize, positionTop: number, positionLeft: number): PartData {
      const vm = this;
      let newPartData: PartData = null;
      switch (partType) {
        case MenuPartType.PART_MENU:
          newPartData = new PartData({
            // PartData
            clientId: vm.partClientId,
            width: partSize.width,
            height: partSize.height,
            minWidth: CELL_SIZE,
            minHeight: CELL_SIZE,
            partType: partType,
            positionTop: positionTop,
            positionLeft: positionLeft,
            // PartDataMenu
          });
          break;
        case MenuPartType.PART_LABEL:
          newPartData = new PartDataLabel({
            // PartData
            clientId: vm.partClientId,
            width: partSize.width,
            height: partSize.height,
            minWidth: CELL_SIZE,
            minHeight: CELL_SIZE,
            partType: partType,
            positionTop: positionTop,
            positionLeft: positionLeft,
            // PartDataLabel
          });
          break;
        case MenuPartType.PART_LINK:
          newPartData = new PartData({
            // PartData
            clientId: vm.partClientId,
            width: partSize.width,
            height: partSize.height,
            minWidth: CELL_SIZE,
            minHeight: CELL_SIZE,
            partType: partType,
            positionTop: positionTop,
            positionLeft: positionLeft,
            // PartDataLink
          });
          break;
        case MenuPartType.PART_ATTACHMENT:
          newPartData = new PartData({
            // PartData
            clientId: vm.partClientId,
            width: partSize.width,
            height: partSize.height,
            minWidth: CELL_SIZE,
            minHeight: CELL_SIZE,
            partType: partType,
            positionTop: positionTop,
            positionLeft: positionLeft,
            // PartDataAttachment
          });
          break;
        case MenuPartType.PART_IMAGE:
          newPartData = new PartData({
            // PartData
            clientId: vm.partClientId,
            width: partSize.width,
            height: partSize.height,
            minWidth: CELL_SIZE,
            minHeight: CELL_SIZE,
            partType: partType,
            positionTop: positionTop,
            positionLeft: positionLeft,
            // PartDataImage
          });
          break;
        case MenuPartType.PART_ARROW:
          newPartData = new PartData({
            // PartData
            clientId: vm.partClientId,
            width: partSize.width,
            height: partSize.height,
            minWidth: CELL_SIZE,
            minHeight: CELL_SIZE,
            partType: partType,
            positionTop: positionTop,
            positionLeft: positionLeft,
            // PartDataArrow
          });
          break;
        default:
          newPartData = new PartData({
            // PartData
            clientId: vm.partClientId,
            width: partSize.width,
            height: partSize.height,
            minWidth: CELL_SIZE,
            minHeight: CELL_SIZE,
            partType: partType,
            positionTop: positionTop,
            positionLeft: positionLeft,
            // PartDataMenu
          });
          break;
      }
      // Set part data to map
      vm.mapPartData[vm.partClientId] = newPartData;
      vm.partClientId++;
      return newPartData;
    }

    /**
     * Create part class
     * @param partType
     */
    private createDefaultPartDOM(partType: string, partData: PartData): JQuery {
      const vm = this;
      switch (partType) {
        case MenuPartType.PART_MENU:
          return vm.renderPartDataMenu(partData as PartDataMenu);
        case MenuPartType.PART_LABEL:
          return vm.renderPartDataLabel(partData as PartDataLabel);
        case MenuPartType.PART_LINK:
          return vm.renderPartDataLink(partData as PartDataLink);
        case MenuPartType.PART_ATTACHMENT:
          return vm.renderPartDataAttachment(partData as PartDataAttachment);
        case MenuPartType.PART_IMAGE:
          return vm.renderPartDataImage(partData as PartDataImage);
        case MenuPartType.PART_ARROW:
          return vm.renderPartDataArrow(partData as PartDataArrow);
        default:
          return vm.renderPartDataMenu(partData as PartDataMenu);
      }
    }

    /**
     * Render PartDataMenu
     * @param partData
     */
    private renderPartDataMenu(partData: PartDataMenu): JQuery {
      const vm = this;
      // TODO
      return $("<div>", { "class": 'menu-creation-item part-menu' });
    }

    /**
     * Render PartDataLabel
     * @param partData
     */
    private renderPartDataLabel(partData: PartDataLabel): JQuery {
      const vm = this;
      const $part = $("<div>", { "class": 'menu-creation-item part-label' })
        // Set PartData attr
        .outerWidth(partData.width)
        .outerHeight(partData.height)
        .css({ 'top': `${partData.positionTop}px`, 'left': `${partData.positionLeft}px` })
        // Update item data object
        .data(partData)
        // Set PartDataLabel attr
        .css({
          'font-size': partData.fontSize,
          'font-weight': partData.isBold ? 'bold' : 'normal',
          'color': partData.textColor,
          'background-color': partData.backgroundColor,
        })
        .text(partData.labelContent);
      const $partSetting: JQuery = $("<div>", { "class": 'part-setting' })
        .on('click', (event) => vm.onPartClickSetting($part));
      const $partSettingPopup: JQuery = $("<div>", { "class": 'part-setting-popup' })
        .css({ 'visibility': 'hidden' })
        .append($("<div>", { "class": 'part-setting-popup-option', text: vm.$i18n('CCG034_150') })
          .on('click', (event) => vm.openPartSettingDialog($part)))
        .append($("<div>", { "class": 'part-setting-popup-option', text: vm.$i18n('CCG034_151') })
          .on('click', (event) => vm.copyPart($part)))
        .append($("<div>", { "class": 'part-setting-popup-option', text: vm.$i18n('CCG034_152') })
          .on('click', (event) => vm.removePart($part)));
      const $partSettingContainer: JQuery = $("<div>", { "class": 'part-setting-container' })
        .append($partSetting)
        .append($partSettingPopup);
      $partSettingContainer.appendTo($part);
      return $part;
    }

    /**
     * Render PartDataLink
     * @param partData
     */
    private renderPartDataLink(partData: PartDataLink): JQuery {
      const vm = this;
      // TODO
      return $("<div>", { "class": 'menu-creation-item part-link' });
    }

    /**
     * Render PartDataAttachment
     * @param partData
     */
    private renderPartDataAttachment(partData: PartDataAttachment): JQuery {
      const vm = this;
      // TODO
      return $("<div>", { "class": 'menu-creation-item part-attachment' });
    }

    /**
     * Render PartDataImage
     * @param partData
     */
    private renderPartDataImage(partData: PartDataImage): JQuery {
      const vm = this;
      // TODO
      return $("<div>", { "class": 'menu-creation-item part-image' });
    }

    /**
     * Render PartDataArrow
     * @param partData
     */
    private renderPartDataArrow(partData: PartDataArrow): JQuery {
      const vm = this;
      // TODO
      return $("<div>", { "class": 'menu-creation-item part-arrow' });
    }

    /**
     * On click part setting
     * @param partClientId
     */
    private onPartClickSetting($part: JQuery) {
      const $partSettingPopup: JQuery = $part.find('.part-setting-popup');
      if ($partSettingPopup) {
        $partSettingPopup.css('visibility', (i, visibility) => (visibility === 'visible') ? 'hidden' : 'visible');
      }
    }

    /**
     * Open Part Setting Dialog
     * @param partClientId
     */
    private openPartSettingDialog($part: JQuery) {
      const vm = this;
      const partClientId = $part.data().clientId;
      const selectedPartData: PartData = vm.mapPartData[partClientId];
      if (selectedPartData) {
        switch (selectedPartData.partType) {
          case MenuPartType.PART_MENU:
            vm.$window.modal('/view/ccg/034/f/index.xhtml', selectedPartData)
              .then((result: PartData) => {
                if (result) {
                  // Update part data
                  vm.mapPartData[partClientId] = result;
                  // Update part DOM
                  const $newPart: JQuery = vm.renderPartDataMenu(result as PartDataMenu);
                  $part.replaceWith($newPart);
                  $part.data(result);
                }
              });
            break;
          case MenuPartType.PART_LABEL:
            vm.$window.modal('/view/ccg/034/e/index.xhtml', selectedPartData)
              .then((result: PartData) => {
                if (result) {
                  // Update part data
                  vm.mapPartData[partClientId] = result;
                  // Update part DOM
                  const $newPart: JQuery = vm.renderPartDataLabel(result as PartDataLabel);
                  $part.replaceWith($newPart);
                  $part.data(result);
                }
              });
            break;
          case MenuPartType.PART_LINK:
            vm.$window.modal('/view/ccg/034/g/index.xhtml', selectedPartData)
              .then((result: PartData) => {
                if (result) {
                  // Update part data
                  vm.mapPartData[partClientId] = result;
                  // Update part DOM
                  const $newPart: JQuery = vm.renderPartDataLink(result as PartDataLink);
                  $part.replaceWith($newPart);
                  $part.data(result);
                }
              });
            break;
          case MenuPartType.PART_ATTACHMENT:
            vm.$window.modal('/view/ccg/034/h/index.xhtml', selectedPartData)
              .then((result: PartData) => {
                if (result) {
                  // Update part data
                  vm.mapPartData[partClientId] = result;
                  // Update part DOM
                  const $newPart: JQuery = vm.renderPartDataAttachment(result as PartDataAttachment);
                  $part.replaceWith($newPart);
                  $part.data(result);
                }
              });
            break;
          case MenuPartType.PART_IMAGE:
            vm.$window.modal('/view/ccg/034/i/index.xhtml', selectedPartData)
              .then((result: PartData) => {
                if (result) {
                  // Update part data
                  vm.mapPartData[partClientId] = result;
                  // Update part DOM
                  const $newPart: JQuery = vm.renderPartDataImage(result as PartDataImage);
                  $part.replaceWith($newPart);
                  $part.data(result);
                }
              });
            break;
          case MenuPartType.PART_ARROW:
            vm.$window.modal('/view/ccg/034/j/index.xhtml', selectedPartData)
              .then((result: PartData) => {
                if (result) {
                  // Update part data
                  vm.mapPartData[partClientId] = result;
                  // Update part DOM
                  const $newPart: JQuery = vm.renderPartDataArrow(result as PartDataArrow);
                  $part.replaceWith($newPart);
                  $part.data(result);
                }
              });
            break;
          default:
            break;
        }
      }
    }

    /**
     * Copy part
     * @param $part
     */
    private copyPart($part: JQuery) {
      // TODO
    }

    /**
     * Remove part
     */
    private removePart($part: JQuery) {
      // TODO
    }

    /**
     * Close dialog
     */
    public closeDialog() {
      const vm = this;
      vm.$window.close();
    }

    public test() {

    }

  }

  export class PartData {
    clientId: number;
    width: number;
    height: number;
    minWidth: number;
    minHeight: number;
    partType: string;
    positionTop: number;
    positionLeft: number;

    constructor(init?: Partial<PartData>) {
      $.extend(this, init);
    }
  }

  export class PartDataMenu extends PartData {
    listMenu: any;

    constructor(init?: Partial<PartDataMenu>) {
      super(init);
      $.extend(this, init);
    }
  }

  export class PartDataLabel extends PartData {
    // Default data
    labelContent: string = '';
    fontSize: number = 11;
    isBold: boolean = false;
    textColor: string = '#000000';
    backgroundColor: string = '#ffffff';

    constructor(init?: Partial<PartDataMenu>) {
      super(init);
      $.extend(this, init);
    }
  }

  export class PartDataLink extends PartData {
    // Default data

    constructor(init?: Partial<PartDataLink>) {
      super(init);
      $.extend(this, init);
    }
  }

  export class PartDataAttachment extends PartData {
    // Default data

    constructor(init?: Partial<PartDataAttachment>) {
      super(init);
      $.extend(this, init);
    }
  }

  export class PartDataImage extends PartData {
    // Default data

    constructor(init?: Partial<PartDataImage>) {
      super(init);
      $.extend(this, init);
    }
  }

  export class PartDataArrow extends PartData {
    // Default data

    constructor(init?: Partial<PartDataArrow>) {
      super(init);
      $.extend(this, init);
    }
  }

  class PartSize {
    width: number;
    height: number;

    constructor(init?: Partial<PartSize>) {
      $.extend(this, init);
    }
  }

  enum MenuPartType {
    PART_MENU = '1',
    PART_LABEL = '2',
    PART_LINK = '3',
    PART_ATTACHMENT = '4',
    PART_IMAGE = '5',
    PART_ARROW = '6',
  }

}