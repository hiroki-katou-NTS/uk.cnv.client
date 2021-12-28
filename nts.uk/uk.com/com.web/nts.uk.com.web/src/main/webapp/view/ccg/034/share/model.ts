module nts.uk.com.view.ccg034.share.model {

  export class HorizontalAlign {
    static LEFT = 0;
    static MIDDLE = 1;
    static RIGHT = 2;
  }

  export class VerticalAlign {
    static TOP = 0;
    static CENTER = 1;
    static BOTTOM = 2;
  }

  export module customload {
    export function loadLimitedLabelForIframe() {
      $("iframe").contents().on('mouseenter', '.limited-label', e => {
        let $label = $(e.target);

        // Check if contents is overflow
        if (isOverflow($label)) {
          let $view = $('<div />').addClass('limited-label-view')
            .text($label.text() || $label.val())
            .appendTo($("iframe").contents().find("body"))
            .position({
              my: 'left top',
              at: 'left bottom',
              of: $label,
              collision: 'flipfit'
            }).css("z-index", "999");

          if ($label.attr("disabled")) {
            let id = "#" + $label.attr("id");
            $(document).on('mouseleave.limitedlabel', id, () => {
              $(document).off('mouseleave.limitedlabel', id);
              $view.remove();
            });
            return;
          }

          $label.bind('mouseleave.limitedlabel', () => {
            $label.unbind('mouseleave.limitedlabel');
            $view.remove();
          });

          $label.on('remove', () => {
            $view.remove();
          });
        }
      });
    }

    function isOverflow($label: any) {
      if ($label[0].nodeName === "INPUT"
        && (window.navigator.userAgent.indexOf("MSIE") > -1
          || !!window.navigator.userAgent.match(/trident/i))) {
        let $div = $("<div/>").appendTo($(document.body));
        let style = $label[0].currentStyle;
        if (style) {
          for (let p in style) {
            $div[0].style[p as any] = style[p];
          }
        }

        $div.html($label.val());
        let width = $div.outerWidth();
        let scrollWidth = $div[0].scrollWidth;
        $div.remove();
        return width < scrollWidth;
      }

      return $label[0].offsetWidth < $label[0].scrollWidth;
    }
  }
}
