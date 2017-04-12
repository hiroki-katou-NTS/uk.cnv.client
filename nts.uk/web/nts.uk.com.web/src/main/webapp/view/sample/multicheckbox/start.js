__viewContext.ready(function () {
    var ScreenModel = (function () {
        function ScreenModel() {
            this.count = 10;
            var self = this;
            self.itemList = ko.observableArray([]);
            for (var i = 1; i < 10; i++) {
                self.itemList.push(new BoxModel(i, 'box ' + i));
            }
            self.selectedValues = ko.observableArray([
                new BoxModel(1, 'box 1'),
                new BoxModel(3, 'box 3')
            ]);
            self.selectedIds = ko.observableArray([1, 2]);
            self.enable = ko.observable(true);
        }
        ScreenModel.prototype.addBoxes = function () {
            var self = this;
            self.itemList.push(new BoxModel(self.count, 'box ' + self.count));
            self.count++;
        };
        ScreenModel.prototype.removeBoxes = function () {
            var self = this;
            self.itemList.pop();
        };
        return ScreenModel;
    }());
    var BoxModel = (function () {
        function BoxModel(id, name) {
            var self = this;
            self.id = id;
            self.name = name;
        }
        return BoxModel;
    }());
    this.bind(new ScreenModel());
});
