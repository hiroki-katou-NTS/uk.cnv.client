__viewContext.ready(function () {
    var ScreenModel = (function () {
        function ScreenModel() {
            var self = this;
        }
        ScreenModel.prototype.resize = function () {
            if ($("#tabs-complex").width() > 700)
                $("#tabs-complex").width(700);
            else
                $("#tabs-complex").width("auto");
        };
        return ScreenModel;
    }());
    this.bind(new ScreenModel());
});
