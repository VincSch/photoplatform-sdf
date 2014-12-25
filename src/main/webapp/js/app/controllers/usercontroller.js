/**
 * Profile Controller.
 */
photoplatformControllers.controller('UserCtrl', ['$scope', '$routeParams', '$rootScope', '$location', '$http', '$cookieStore', 'UserService', '$route',
    function ($scope, $routeParams, $rootScope, $location, $http, $cookieStore, UserService, $route) {
        //if user is not logged in or authorized redirect to login page
        var user = $cookieStore.get('user');
        if (undefined == user || !$rootScope.isLoggedIn()) {
            $location.path("/login");
            return;
        }

        /**
         * Become Photographer
         * @param user photographer
         */
        $scope.becomePhotographer = function (user) {
            UserService.becomePhotographer(user).success(function (data) {
                $location.path("/profile");
                $rootScope.success = "Du hast dich erfolgreich als Fotograf beworben. Ein Admin muss zuerst dein Account freischalten!";
                $rootScope.transferSuccess = true;
            }).error(function (data) {
                $scope.errors = data.errors;
            });
        };

        /**
         * Update a Photographer.
         * @param user the user
         */
        $scope.updatePhotographer = function (user) {
            UserService.updatePhotographer(user).success(function (data) {
                console.log("Update Photographer");
            }).error(function (data) {
                $scope.errors = data.errors;
            });
        };

        //check param
        $scope.userId = $routeParams.userId;
        if ($scope.userId == undefined) {
            $scope.userId = user.id;
        }

        //get user ProfileData
        UserService.getUserProfileData($scope.userId).success(function (responseUserProfileData) {
            $scope.userProfileData = responseUserProfileData;
            $scope.roles = responseUserProfileData.roles;
        }).error(function (error) {
            console.log(error);
            $rootScope.error(error);
        });

        /**
         * save input from edited field
         */
        $scope.save = function () {
            UserService.updateUserProfileData($scope.userProfileData).success(function (data) {
                $rootScope.success = "Profil erfolgreich aktualisiert";
            }).error(function (data) {
                $scope.errors = data.errors;
            });
            $location.path("/profile/view");
        };

        /**
         * cancel fieldinput and reset data in field
         * @param fieldToCancel
         */
        $scope.cancel = function (fieldToCancel) {
            UserService.getUserProfileData($scope.userId).success(function (responseUserProfileData) {
                $scope.userProfileData = responseUserProfileData;
                $scope.roles = responseUserProfileData.roles;
            }).error(function (error) {
                console.log(error);
                $rootScope.error(error);
            });
        };

        /**
         * change user password and close modal on success
         */
        $scope.changePassword = function (pw) {
            UserService.changePassword(pw).success(function (user) {
                $rootScope.success = "Passwort erfolgreich geändert";
                $('#pwModal').modal('hide');
                // set the new token
                $rootScope.user = user;
                $http.defaults.headers.common[xAuthTokenHeaderName] = user.secToken;
                $cookieStore.put('user', user);
            }).error(function (data) {
                console.log(data);
                $scope.errors = data.errors;
            });
            $location.path("/profile/view");
        };
    }
]);
