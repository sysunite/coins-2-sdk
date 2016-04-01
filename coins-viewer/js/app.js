var materialAdmin = angular.module('materialAdmin', [
    'ngAnimate',
    'ngResource',
    'ui.router',
    'ui.bootstrap',
    'angular-loading-bar',
    'oc.lazyLoad',
    'nouislider',
    'ngTable',
    'restangular'
])

.constant('MOCK', false)

.config(function ($stateProvider, $urlRouterProvider, RestangularProvider){
    RestangularProvider.setBaseUrl('http://localhost:8080/api/')

    $stateProvider

        //------------------------------
        // BASE
        //------------------------------
        .state ('base', {
          templateUrl: 'views/base.html'
        })

        //------------------------------
        // CLASSES
        //------------------------------

        .state ('base.classes', {
          url: '/classes',
          templateUrl: 'views/classes.html'
        })

        .state ('base.class', {
          url: '/class?uri',
          templateUrl: 'views/class.html'
         })

        //------------------------------
        // INDIVIDUALS
        //------------------------------

        .state ('base.individuals', {
          url: '/individuals',
          templateUrl: 'views/individuals.html'
        })

        .state ('base.individual', {
          url: '/individual?uri',
          templateUrl: 'views/individual.html'
        })
})

.run(function($state, $rootScope, $location){

    if ($location.path() === '' || $location.path() === '/') {
      $state.go('base.classes')
    }

  });