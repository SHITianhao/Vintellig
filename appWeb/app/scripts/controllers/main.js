'use strict';

/**
 * @ngdoc function
 * @name appWebApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the appWebApp
 */
angular.module('appWebApp')
  .controller('MainCtrl', ['$scope','$routeParams',function ($scope,$routeParams) {
    $scope.awesomeThings = [
      'HTML5 Boilerplate',
      'AngularJS',
      'Karma'
    ];
    $scope.id = $routeParams.id;
    var fs = require('fs');
    $scope.rename = function(){
      fs.rename('/Users/sth/develope/Vintellig/appWeb/download/hello.txt', '/Users/sth/develope/Vintellig/appWeb/download/hello1.txt', function (err) {
        if (err) {
          console.log(err);
        }
        console.log('renamed complete');
      });
    };


  }]);
