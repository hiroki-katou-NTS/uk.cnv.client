<<<<<<< HEAD
__viewContext.ready(function () {
    var ScreenModel = (function () {
        function ScreenModel() {
            var self = this;
            self.dataList = ko.observableArray([]);
            for (var i = 0; i <= 10; i++) {
                self.dataList.push({ id: i, name: "Item " + i });
            }
            // Menu 1 setup
            this.menu1 = new nts.uk.ui.contextmenu.ContextMenu(".context-menu", [
                new nts.uk.ui.contextmenu.ContextMenuItem("cut", // key
                "Cut", // Text
                function (ui) { alert("Cut: " + $(ui).data("test")); }, // Handler
                "ui-icon ui-icon-scissors", // Icon class
                true, // Enable
                true // Visible
                ),
                new nts.uk.ui.contextmenu.ContextMenuItem("copy", "Copy", function (ui) { alert("Copy"); }, "ui-icon ui-icon-copy"),
                new nts.uk.ui.contextmenu.ContextMenuItem("paste", "Paste", function (ui) { alert("Paste"); }, "ui-icon ui-icon-clipboard"),
                new nts.uk.ui.contextmenu.ContextMenuItem("divider"),
                new nts.uk.ui.contextmenu.ContextMenuItem("delete", "Delete", function (ui) { alert("Delete"); }, "ui-icon ui-icon-trash")
            ]);
            // Menu 2 setup
            this.menu2 = new nts.uk.ui.contextmenu.ContextMenu(".context-menu2", [
                new nts.uk.ui.contextmenu.ContextMenuItem("cut", "切り取り", function (ui) { alert("切り取り2: " + $(ui).data("test")); }, "icon icon-dot"),
                new nts.uk.ui.contextmenu.ContextMenuItem("copy", "コピー", function (ui) { alert("コピー"); }, "icon icon-dot"),
                new nts.uk.ui.contextmenu.ContextMenuItem("paste", "貼り付け", function (ui) { alert("貼り付け"); }, "icon icon-dot"),
                new nts.uk.ui.contextmenu.ContextMenuItem("divider"),
                new nts.uk.ui.contextmenu.ContextMenuItem("delete", "削除", function (ui) { alert("削除"); }, "icon icon-close")
            ]);
            // Enable ContextMenu
            self.enable = ko.observable(true);
            self.enable.subscribe(function (value) {
                self.menu1.setEnable(value);
                self.menu2.setEnable(value);
            });
            // Enable "Cut/Copy" Item
            self.enableCut = ko.observable(true);
            self.enableCut.subscribe(function (value) {
                self.menu1.setEnableItem(value, 0); // Set enable item by index
                self.menu1.setEnableItem(value, 1);
                self.menu2.setEnableItem(value, 0);
                self.menu2.setEnableItem(value, 1);
            });
            // Visible "Copy" Item
            self.visibleCopy = ko.observable(true);
            self.visibleCopy.subscribe(function (value) {
                self.menu1.setVisibleItem(value, "copy"); // Set visible item by key
                self.menu2.setVisibleItem(value, "copy");
            });
        }
        ScreenModel.prototype.addItem = function () {
            this.menu1.addItem(new nts.uk.ui.contextmenu.ContextMenuItem("love", "Love", function (ui) {
                alert("Love: " + $(ui).data("test"));
            }, "ui-icon ui-icon-heart"));
            this.menu2.addItem(new nts.uk.ui.contextmenu.ContextMenuItem("love", "好き", function (ui) {
                alert("Love: " + $(ui).data("test"));
            }, "ui-icon ui-icon-heart"));
        };
        ScreenModel.prototype.removeItem = function () {
            this.menu1.removeItem(this.menu1.items.length - 1); // remove last item
            this.menu2.removeItem(this.menu2.items.length - 1);
        };
        return ScreenModel;
    }());
    this.bind(new ScreenModel());
});
=======
__viewContext.ready(function () {
    var ScreenModel = (function () {
        function ScreenModel() {
            var self = this;
            self.dataList = ko.observableArray([]);
            for (var i = 0; i <= 10; i++) {
                self.dataList.push({ id: i, name: "Item " + i });
            }
            this.menu1 = new nts.uk.ui.contextmenu.ContextMenu(".context-menu", [
                new nts.uk.ui.contextmenu.ContextMenuItem("cut", "Cut", function (ui) { alert("Cut: " + $(ui).data("test")); }, "ui-icon ui-icon-scissors", true, true),
                new nts.uk.ui.contextmenu.ContextMenuItem("copy", "Copy", function (ui) { alert("Copy"); }, "ui-icon ui-icon-copy"),
                new nts.uk.ui.contextmenu.ContextMenuItem("paste", "Paste", function (ui) { alert("Paste"); }, "ui-icon ui-icon-clipboard"),
                new nts.uk.ui.contextmenu.ContextMenuItem("divider"),
                new nts.uk.ui.contextmenu.ContextMenuItem("delete", "Delete", function (ui) { alert("Delete"); }, "ui-icon ui-icon-trash")
            ]);
            this.menu2 = new nts.uk.ui.contextmenu.ContextMenu(".context-menu2", [
                new nts.uk.ui.contextmenu.ContextMenuItem("cut", "切り取り", function (ui) { alert("切り取り2: " + $(ui).data("test")); }, "icon icon-dot"),
                new nts.uk.ui.contextmenu.ContextMenuItem("copy", "コピー", function (ui) { alert("コピー"); }, "icon icon-dot"),
                new nts.uk.ui.contextmenu.ContextMenuItem("paste", "貼り付け", function (ui) { alert("貼り付け"); }, "icon icon-dot"),
                new nts.uk.ui.contextmenu.ContextMenuItem("divider"),
                new nts.uk.ui.contextmenu.ContextMenuItem("delete", "削除", function (ui) { alert("削除"); }, "icon icon-close")
            ]);
            self.enable = ko.observable(true);
            self.enable.subscribe(function (value) {
                self.menu1.setEnable(value);
                self.menu2.setEnable(value);
            });
            self.enableCut = ko.observable(true);
            self.enableCut.subscribe(function (value) {
                self.menu1.setEnableItem(value, 0);
                self.menu1.setEnableItem(value, 1);
                self.menu2.setEnableItem(value, 0);
                self.menu2.setEnableItem(value, 1);
            });
            self.visibleCopy = ko.observable(true);
            self.visibleCopy.subscribe(function (value) {
                self.menu1.setVisibleItem(value, "copy");
                self.menu2.setVisibleItem(value, "copy");
            });
        }
        ScreenModel.prototype.addItem = function () {
            this.menu1.addItem(new nts.uk.ui.contextmenu.ContextMenuItem("love", "Love", function (ui) {
                alert("Love: " + $(ui).data("test"));
            }, "ui-icon ui-icon-heart"));
            this.menu2.addItem(new nts.uk.ui.contextmenu.ContextMenuItem("love", "好き", function (ui) {
                alert("Love: " + $(ui).data("test"));
            }, "ui-icon ui-icon-heart"));
        };
        ScreenModel.prototype.removeItem = function () {
            this.menu1.removeItem(this.menu1.items.length - 1);
            this.menu2.removeItem(this.menu2.items.length - 1);
        };
        return ScreenModel;
    }());
    this.bind(new ScreenModel());
});
>>>>>>> basic/develop
//# sourceMappingURL=start.js.map