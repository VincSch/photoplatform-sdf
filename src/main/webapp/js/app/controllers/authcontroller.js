var photoplatformControllers = angular.module('photoplatformControllers', []);

/**
 * Authentication Controller.
 * Handles authentication, registering, forgot password.
 */
photoplatformControllers.controller(
    'AuthCtrl',
    [
        '$scope',
        '$rootScope',
        '$location',
        '$http',
        '$cookieStore',
        'AuthService',
        '$route',
        '$routeParams',
        function ($scope, $rootScope, $location, $http, $cookieStore, AuthService, $route, $routeParams) {

            /**
             * Login user.
             * @param email the username
             * @param password the password
             */
            $scope.login = function (email, password) {
                AuthService.login(email, password).success(function (user) {
                    $rootScope.user = user;
                    $http.defaults.headers.common[xAuthTokenHeaderName] = user.secToken;
                    $cookieStore.put('user', user);
                    console.log(user);
                    if ($rootScope.isCustomer())
                        $location.path("/profile");
                    if ($rootScope.isPhotographer())
                        $location.path("/profile");
                    if ($rootScope.isAdmin())
                        $location.path("/profile");
                }).error(function (data) {
                    $scope.errors = data.errors;
                });
            };

            /**
             * Register user.
             * @param user the user to register.
             */
            $scope.register = function (user) {
                AuthService.register(user).success(function (data) {
                    $location.path("/");
                    $rootScope.success = "Du hast dich erfolgreich registriert. Du kannst dich nun mit deinen Daten anmelden!";
                    $rootScope.transferSuccess = true;
                }).error(function (data) {
                    $scope.errors = data.errors;
                });
            };

            /**
             * Lost password.
             * @param user the user to register.
             */
            $scope.passwordLost = function (email) {
                AuthService.passwordLost(email).success(function () {
                    $('#passwordLost').modal('toggle');
                    $location.path("/");
                    $rootScope.success = "Wir haben die eine E-Mail geschickt mit weiteren Informationen!";
                    $rootScope.transferSuccess = true;
                }).error(function (data) {
                    $scope.errors = data.errors;
                });
            };

            /**
             * @param prf password reset form
             */
            $scope.passwordReset = function (prf) {
                // Password reset form data
                prf.passwordResetToken = $routeParams.token;
                AuthService.passwordReset(prf).success(function (data) {
                    $location.path("/");
                    $location.search('token', null);
                    $rootScope.success = "Passwort wurde erfolgreich geändert! Du kannst dich mit deinem neuen jetzt einloggen.";
                    $rootScope.transferSuccess = true;
                }).error(function (data) {
                    $scope.errors = data.errors;
                });
            };
        }]);