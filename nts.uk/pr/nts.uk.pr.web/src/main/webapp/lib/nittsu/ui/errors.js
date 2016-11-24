var nts;
(function (nts) {
    var uk;
    (function (uk) {
        var ui;
        (function (ui) {
            var errors;
            (function (errors) {
                var ErrorsViewModel = (function () {
                    function ErrorsViewModel() {
                        this.title = "エラー一覧";
                        this.headers = ko.observableArray([
                            new ErrorHeader("tab", "タブ", 90, true),
                            new ErrorHeader("location", "エラー箇所", 115, true),
                            new ErrorHeader("message", "エラー詳細", 250, true)]);
                        this.errors = ko.observableArray([]);
                        this.option = ko.mapping.fromJS(new ui.option.ErrorDialogOption());
                    }
                    ErrorsViewModel.prototype.closeButtonClicked = function () {
                    };
                    ErrorsViewModel.prototype.open = function () {
                        this.option.show(true);
                    };
                    ErrorsViewModel.prototype.hide = function () {
                        this.option.show(false);
                    };
                    return ErrorsViewModel;
                }());
                errors.ErrorsViewModel = ErrorsViewModel;
                var ErrorHeader = (function () {
                    function ErrorHeader(name, text, width, visible) {
                        this.name = name;
                        this.text = text;
                        this.width = width;
                        this.visible = visible;
                    }
                    return ErrorHeader;
                }());
                errors.ErrorHeader = ErrorHeader;
                function errorsViewModel() {
                    return nts.uk.ui._viewModel.kiban.errorDialogViewModel;
                }
                function show() {
                    errorsViewModel().open();
                }
                errors.show = show;
                function hide() {
                    errorsViewModel().hide();
                }
                errors.hide = hide;
                function add(error) {
                }
                errors.add = add;
            })(errors = ui.errors || (ui.errors = {}));
        })(ui = uk.ui || (uk.ui = {}));
    })(uk = nts.uk || (nts.uk = {}));
})(nts || (nts = {}));
