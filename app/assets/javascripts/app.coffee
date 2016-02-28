
require [
  './services',
  './directives',
  './controllers',
  './filters'
],
  () ->
    mod = angular.module('AuctionHouse', ['AuctionHouse.services', 'AuctionHouse.directives', 'AuctionHouse.controllers', 'AuctionHouse.filters', 'ngRoute'])

    mod.config(['$httpProvider', '$routeProvider',
      ($httpProvider, $routeProvider) ->
        $httpProvider.interceptors.push 'authInterceptor'
        $routeProvider
          .when '/home',
            { templateUrl: 'templates/home.html',            controller: 'HomeCtrl',      controllerAs: 'homeCtrl' }
          .when '/bidders',
            { templateUrl: 'templates/bidders.html',         controller: 'BiddersCtrl',   controllerAs: 'biddersCtrl' }
          .when '/items',
            { templateUrl: 'templates/items.html',           controller: 'ItemsCtrl',     controllerAs: 'itemsCtrl' }
          .when '/bidentry',
            { templateUrl: 'templates/bidentry.html',        controller: 'BidEntryCtrl',  controllerAs: 'bidEntryCtrl' }
          .when '/reconciliation',
            { templateUrl: 'templates/reconciliation.html',  controller: 'ReconCtrl',     controllerAs: 'reconCtrl' }
          .otherwise { redirectTo: '/home' }
        null
      ])

    angular.bootstrap(document, ['AuctionHouse'])

    mod
