$(document).ready(function(){

		

  $('.menu li').hover(function(){

    

  $(this).children('ul').show();

  

  $(this).focus().addClass('focusa')	

  

  },function(){

  

  $(this).children('ul').hide();



  $(this).focus().removeClass('focusa')	

    });	

  DD_belatedPNG.fix('ul,.more');

  });