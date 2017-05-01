var resuscitator;
+function ($) {

  'use strict';

  $(function() {
    var _h = $('header').outerHeight(true);
    var _f = $('footer').outerHeight(true);
    $('main').css({
      paddingTop: ~~_h,
      paddingBottom: ~~($('html').hasClass('no-flexbox') ? _f : 0),
      marginBottom: ~~($('html').hasClass('no-flexbox') ? -_f : 0)
    });

    $('#menu .modal-dialog,#me .modal-dialog').css({
      marginTop: ~~_h,
      marginBottom: ~~_f,
      height: 'auto'
    });
    $('.modal.lefty,.modal.righten').on('show.bs.modal', function(e) {
      var $this = $(this);
      var $modal = $('.modal-dialog', $this);
      var id = $this.prop('id');
      $modal.addClass($this.hasClass('lefty') ? 'animated fadeInLeft' : 'animated fadeInRight');
      $(e.relatedTarget).addClass('open');
      $('[data-toggle="modal"][data-target="#' + id + '"]').addClass('open');
    }).on('hide.bs.modal', function(e) {
      var $this = $(this);
      var $modal = $('.modal-dialog', $this);
      $modal.removeClass('animated').removeClass($this.hasClass('lefty') ? 'fadeInLeft' : 'fadeInRight');
    }).on('hidden.bs.modal', function(e) {
      var $this = $(this);
      var id = $this.prop('id');
      $('[data-toggle="modal"][data-target="#' + id + '"]').removeClass('open');
    });
  });

  $(document).on('click', '[data-signout]', function () {
    $('#signout').submit();
  });
}(jQuery);
