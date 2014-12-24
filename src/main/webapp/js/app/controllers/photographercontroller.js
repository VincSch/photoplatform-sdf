/**
 * Photographer Controller.
 */
photoplatformControllers.controller(
    'PhotographerCtrl',
    [
        '$scope',
        '$rootScope',
        '$location',
        '$http',
        '$cookieStore',
        'UserService',
        'PhotographerService',
        '$route',
        function ($scope, $rootScope, $location, $http, $cookieStore, UserService, PhotographerService, $route) {

            var start = 0;
            var count = 100;

            // Photographer collection
            $scope.collections = [];
            $scope.showcase = [];

            $scope.copy = angular.copy;

            /**
             * Get all user collections
             */
            $scope.getAllCollections = function () {
                PhotographerService.getCollections(start, count).success(function (collections) {
                    $scope.collections = collections;
                });
            };

            /**
             * Get all user collections in showcase
             */
            $scope.getShowcase = function () {
                PhotographerService.getShowcase(start, count).success(function (collections) {
                    $scope.showcase = collections;
                });
            };

            /**
             * Create new collection
             * @param collection
             */
            $scope.createCollection = function (collection) {
                PhotographerService.createCollection(collection).success(function (newCollection) {
                    $scope.collections.unshift(newCollection);
                    $rootScope.success = 'Deine Sammlung ' + newCollection.name + ' wurde erfolgreich erstellt';
                    collection = null;
                }).error(function (data) {
                    $scope.errors = data.errors;
                })
            };

            /**
             * Create new collection
             * @param collection
             */
            $scope.updateCollection = function (editCollection, originCollection, status) {
                PhotographerService.updateCollection(editCollection).success(function (collection) {
                    angular.extend(originCollection, collection);
                    status.editing = false;
                    $rootScope.success = 'Erfolgreich aktualisiert';
                }).error(function (data) {
                    $scope.errors = data.errors;
                });
            };

            /**
             * Publish collection and show in showcase.
             */
            $scope.updateCollectionShowcase = function (collection, isPublic) {
                PhotographerService.updateCollectionShowcase(collection.id, isPublic).success(function (message) {
                    collection.public = isPublic;
                    $rootScope.success = message;
                }).error(function (data) {
                    $scope.errors = data.errors;
                })
            };

            /**
             * Publish collection and show in showcase.
             */
            $scope.deleteCollection = function (collection, index) {
                PhotographerService.deleteCollection(collection.id).success(function (message) {
                    $scope.collections.splice(index, 1);
                    $rootScope.success = message;
                }).error(function (data) {
                    $scope.errors = data.errors;
                })
            };
        }]);