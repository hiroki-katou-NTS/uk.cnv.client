var nts;
(function (nts) {
    var uk;
    (function (uk) {
        var ui;
        (function (ui) {
            var koExtentions;
            (function (koExtentions) {
                var NtsHelpButtonBindingHandler = (function () {
                    function NtsHelpButtonBindingHandler() {
                    }
                    NtsHelpButtonBindingHandler.prototype.init = function (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
                        var data = valueAccessor();
                        var image = ko.unwrap(data.image);
                        var textId = ko.unwrap(data.textId);
                        var textParams = ko.unwrap(data.textParams);
                        var enable = (data.enable !== undefined) ? ko.unwrap(data.enable) : true;
                        var position = ko.unwrap(data.position);
                        var isImage = !uk.util.isNullOrUndefined(image);
                        var myPositions = position.replace(/[^a-zA-Z ]/gmi, "").split(" ");
                        var atPositions = position.split(" ");
                        var operator = 1;
                        var marginDirection = "";
                        var caretDirection = "";
                        var caretPosition = "";
                        if (myPositions[0].search(/(top|left)/i) !== -1) {
                            operator = -1;
                        }
                        if (myPositions[0].search(/(left|right)/i) === -1) {
                            atPositions[0] = atPositions.splice(1, 1, atPositions[0])[0];
                            myPositions[0] = myPositions.splice(1, 1, myPositions[0])[0];
                            caretDirection = myPositions[1] = uk.text.reverseDirection(myPositions[1]);
                            caretPosition = "left";
                            marginDirection = "margin-top";
                        }
                        else {
                            caretDirection = myPositions[0] = uk.text.reverseDirection(myPositions[0]);
                            caretPosition = "top";
                            marginDirection = "margin-left";
                        }
                        $(element).on("click", function () {
                            if ($popup.is(":visible")) {
                                $popup.hide();
                            }
                            else {
                                var CARET_WIDTH = parseFloat($caret.css("font-size")) * 2;
                                $popup.show()
                                    .css(marginDirection, 0)
                                    .position({
                                    my: myPositions[0] + " " + myPositions[1],
                                    at: atPositions[0] + " " + atPositions[1],
                                    of: $(element),
                                    collision: "none"
                                })
                                    .css(marginDirection, CARET_WIDTH * operator);
                                $caret.css(caretPosition, parseFloat($popup.css(caretPosition)) * -1);
                            }
                        }).wrap($("<div class='ntsControl ntsHelpButton'></div>"));
                        var $container = $(element).closest(".ntsHelpButton");
                        var $content;
                        if (isImage) {
                            $content = $("<img src='" + uk.request.resolvePath(image) + "' />");
                        }
                        else {
                            $content = $("<span>").text(uk.resource.getText(textId, textParams));
                            $content.css('white-space', 'pre-line');
                        }
                        var $caret = $("<span class='caret-helpbutton caret-" + caretDirection + "'></span>");
                        var $popup = $("<div class='nts-help-button-image'></div>")
                            .append($caret)
                            .append($content)
                            .appendTo($container).hide();
                        if (!isImage) {
                            var CHARACTER_DEFAULT_WIDTH = 7;
                            var DEFAULT_SPACE = 5;
                            var textLengths = _.map($content.text().split(/\r\n/g), function (o) { return nts.uk.text.countHalf(o); });
                            var WIDTH_SHOULD_NEED = CHARACTER_DEFAULT_WIDTH * _.max(textLengths) + DEFAULT_SPACE;
                            $popup.width(WIDTH_SHOULD_NEED > 300 ? 300 : WIDTH_SHOULD_NEED);
                        }
                        $("html").on("click", function (event) {
                            if (!$container.is(event.target) && $container.has(event.target).length === 0) {
                                $container.find(".nts-help-button-image").hide();
                            }
                        });
                    };
                    NtsHelpButtonBindingHandler.prototype.update = function (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
                        var data = valueAccessor();
                        var enable = (data.enable !== undefined) ? ko.unwrap(data.enable) : true;
                        (enable === true) ? $(element).removeAttr("disabled") : $(element).attr("disabled", "disabled");
                    };
                    return NtsHelpButtonBindingHandler;
                }());
                ko.bindingHandlers['ntsHelpButton'] = new NtsHelpButtonBindingHandler();
            })(koExtentions = ui.koExtentions || (ui.koExtentions = {}));
        })(ui = uk.ui || (uk.ui = {}));
    })(uk = nts.uk || (nts.uk = {}));
})(nts || (nts = {}));
//# sourceMappingURL=helpbutton-ko-ext.js.map