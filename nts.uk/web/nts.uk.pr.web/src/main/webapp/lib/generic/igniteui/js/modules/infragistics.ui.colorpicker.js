﻿/*!@license
 * Infragistics.Web.ClientUI ColorPicker 16.1.20161.2145
 *
 * Copyright (c) 2011-2016 Infragistics Inc.
 * <Licensing info>
 *
 * http://www.infragistics.com/
 *
 * Depends on:
 *   jquery-1.9.1.js
 *   jquery.ui.core.js
 *   jquery.ui.widget.js
 *   infragistics.util.js
 *   infragistics.ui.shared.js
 */

/*global jQuery */
if (typeof jQuery !== "function") {
    throw new Error("jQuery is undefined");
}

(function ($) {
    /*
		The igColorPicker is a jQuery based widget which allow you to pick a color.
	*/
    $.widget("ui.igColorPicker", {
        options: {
            /* type="array" Default colors. */
            colors: [
				[ "#ffffff", "#000000", "#EEECE1", "#1F497D", "#4F81BD",
                    "#C0504D", "#9BBB59", "#8064A2", "#4BACC6", "#F79646" ],
				[ "#F2F2F2", "#7F7F7F", "#DDD9C3", "#C6D9F0", "#DBE5F1",
                    "#F2DCDB", "#EBF1DD", "#E5E0EC", "#DBEEF3", "#FDEADA" ],
				[ "#D8D8D8", "#595959", "#C4BD97", "#8DB3E2", "#B8CCE4",
                    "#E5B9B7", "#D7E3BC", "#CCC1D9", "#B7DDE8", "#FAC08F" ],
				[ "#BFBFBF", "#3F3F3F", "#938953", "#548DD4", "#95B3D7",
                    "#D99694", "#C3D69B", "#B2A1C7", "#92CDDC", "#FAC08F" ],
				[ "#A5A5A5", "#262626", "#494429", "#17365D", "#366092",
                    "#953734", "#76923C", "#5F497A", "#31859B", "#E36C09" ],
				[ "#7F7F7F", "#0C0C0C", "#1D1B10", "#0F243E", "#244061",
                    "#632423", "#4F6128", "#3F3151", "#205867", "#974806" ]
            ],
            /* type="array" Standard colors. */
            standardColors: [ "#C00000", "#FF0000", "#FFC000", "#FFFF00",
                "#92D050", "#00B050", "#00B0F0", "#0070C0", "#002060", "#7030A0" ]
        },
        events: {
            /* cancel="false" Event fired after the color is selected. */
            colorSelected: "colorSelected"
        },
        css: {
            /* The row class css. */
            standardColorsRow: "ui-colorpicker-standardcolors",
            /* The widget base class css. */
            baseClass: "ui-igColorPicker",
            /* The widget color table class css. */
            colorTable: "igColorPicker-table",
            /* The widget custom colors class css. */
            customColors: "igColorPicker-customColors",
            /* The widget default colors class css. */
            defaultColors: "ui-colorpicker-standardcolors",
            /* The widget colors row class css. */
            colorsRow: "igColorPicker-row",
            /* The widget colors col class css. */
            colorsCol: "igColorPicker-col",
            /* The widget color picker class css. */
            colorpickerColor: "igColorPicker-color"
        },
        _create: function () {
            this._colorTable = $("<div>");
            this._colorTable.addClass(this.css.colorTable);
            this._colorTable.appendTo(this.element);

            this._addOrChangeColors(this.options.colors);
            this._addOrChangeStandardColors(this.options.standardColors);

        },
        _addOrChangeColors: function(colors) {
            var colsLength, row, col,
                rowsLength = colors.length,
                customColorsHtml = "";

            if (this._customColors && this._customColors.length > 0) {
                this._customColors.html("");
            }else {
                this._customColors = $("<div>").addClass(this.css.customColors);
                this._customColors.appendTo(this._colorTable);
            }

            for (row = 0; row < rowsLength; row++) {
                customColorsHtml += "<div class= " + this.css.colorsRow + ">";
                colsLength = colors[ row ].length; //taking second dimension size

                for (col = 0; col < colsLength; col++) {
                    customColorsHtml += "<div class=" + this.css.colorpickerColor +
                        ' style="background-color: ' + colors[ row ][ col ] + ';"></div>';
                }
                customColorsHtml += "</div>";
            }

            this._customColors.html(customColorsHtml);

        },
        _addOrChangeStandardColors: function(colors) {
            var item,
                defaultColorsHtml = "";

            if (this._defaultColors && this._defaultColors.length > 0) {
                this._defaultColors.html("");
            }else {
                this._defaultColors = $("<div>").addClass(this.css.defaultColors);
                this._defaultColors.appendTo(this._colorTable);
            }

            for (item = 0; item < colors.length; item++) {
                defaultColorsHtml += "<div class=" + this.css.colorpickerColor +
                     ' style="background-color: ' + colors[ item ] + ';"></div>';
            }

            this._defaultColors.html(defaultColorsHtml);
        },
        _getColor: function ($element) {
            return $element.css("background-color");
        },
        _init: function () {
            this.element.addClass(this.css.baseClass);
            this._bindEvents();
        },
        _bindEvents: function () {
            var self = this;
            this._colorTable.delegate("." + this.css.colorpickerColor, "click", function (e) {
                var target = $(e.target);
                e.preventDefault();

                self._changeSelectedColor(target);
                self._trigger(self.events.colorSelected, e, { color: self._getColor(target) });
            });
        },
        _changeSelectedColor: function (target) {
            this._colorTable.find("div.selected-color").removeClass("selected-color");

            target.addClass("selected-color");
        },

        _setOption: function( key, value ) {
            var options = this.options;

            if (options[ key ] === value) {
                return;
            }

            $.Widget.prototype._setOption.apply(this, arguments);

            switch (key) {
                case "standardColors":
                    this._addOrChangeStandardColors(value);
                    break;
                case "colors" :
                    this._addOrChangeColors(value);
                    break;
            }
        },
        _rgbToHex: function(color) {
            var r, g, b, colHex;

            if (color.charAt(0) === "r")
            {
                color = color.replace("rgb(", "").replace(")", "").split(",");
                r = parseInt(color[ 0 ], 10).toString(16);
                g = parseInt(color[ 1 ], 10).toString(16);
                b = parseInt(color[ 2 ], 10).toString(16);
                r = r.length === 1 ? "0" + r : r; g = g.length === 1 ?
                    "0" + g : g; b = b.length === 1 ? "0" + b : b;
                colHex = "#" + r + g + b;
                return colHex;
            }
        },

        selectColor: function(color) {
            /* select a given color to the widget. */
            var self = this;
            this._colorTable.find("div.selected-color").removeClass("selected-color");

            var matching = this._colorTable.find("div").filter(function(index, item) {
                var hexColor = self._rgbToHex(item.style.backgroundColor);
                return hexColor && hexColor === color.toLowerCase();
            });

            if (matching.length > 0) {
                matching.addClass("selected-color");
            }
        }
    });

    $.extend($.ui.igColorPicker, { version: "16.1.20161.2145" });
}(jQuery));
