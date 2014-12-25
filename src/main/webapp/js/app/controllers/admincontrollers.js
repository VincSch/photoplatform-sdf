/**
 * ViewController for admin page.
 */
photoplatformControllers.controller(
    'AdminMenuCtrl',
    [
        '$scope',
        '$rootScope',
        '$location',
        '$http',
        '$cookieStore',
        'UserService',
        '$route',
        function ($scope, $rootScope, $location, $http, $cookieStore, UserService) {
            var user = $cookieStore.get('user');
            //if user is not logged in or authorized redirect to login page
            if (undefined == user || !$rootScope.isLoggedIn() || !$rootScope.isAdmin()) {
                $location.path("/login");
                return;
            }

            $scope.enablePhotographer = function (id) {
                UserService.enablePhotographer(id).success(function (user) {
                    var RemovedIndex = -1;
                    $scope.photographTableFilteredUsers.forEach(function (elem, index) {
                        if (elem.id == user.id) {
                            RemovedIndex = index;
                        }
                    });
                    $scope.photographTableFilteredUsers.splice(RemovedIndex, 1);
                }).error(function () {
                });

            };

            $scope.makeAdmin = function (id) {
                UserService.makeAdmin(id).success(function (user) {
                    $scope.userTableUsers.forEach(function (elem, index) {
                        if (elem.id == user.id) {
                            $scope.userTableUsers[index].admin = user.admin;
                        }
                    });
                    angular.element('#make-admin-' + id).attr('disabled', true);

                }).error(function () {
                });
            };

            $scope.lockUser = function (id) {
                UserService.lockUser(id)
                    .success(function (lockedUser) {
                        $scope.userTableUsers.forEach(function (elem, index) {
                            if (elem.id == lockedUser.id)
                                $scope.userTableUsers[index].banned = lockedUser.banned;
                        });
                        //angular.element('#locked-user-'+id).css('display','none');
                        //angular.element('#unlocked-user-'+id).css('display','show');
                        angular.element('#locked-user-' + id).attr('disabled', true);
                        angular.element('#unlocked-user-' + id).attr('disabled', false);
                    })
                    .error(function () {

                    });
            };

            $scope.unlockUser = function (id) {
                UserService.unlockUser(id)
                    .success(function (lockedUser) {
                        $scope.userTableUsers.forEach(function (elem, index) {
                            if (elem.id == lockedUser.id)
                                $scope.userTableUsers[index].banned = lockedUser.banned;
                        });
                        //angular.element('#locked-user-'+id).css('display','show');
                        //angular.element('#unlocked-user-'+id).css('display','none');
                        angular.element('#locked-user-' + id).attr('disabled', false);
                        angular.element('#unlocked-user-' + id).attr('disabled', true);
                    })
                    .error(function () {

                    });
            };
            //Photograph table.
            $scope.photographTableCurrentPage = 1;
            $scope.photographTableNumPerPage = 5;
            UserService.getBecomePhotographers($rootScope.PHOTOGRAPHER).success(function (users) {
                $scope.photographTablePhotographs = users;
                $scope.photographTableTotalItems = users.length;
                $scope.photographTableShowPagination = users.length > $scope.photographTableNumPerPage;

                // Set watch on pagination numbers
                $scope.$watch('photographTableCurrentPage + photographTableNumPerPage', function () {
                    var begin = (($scope.photographTableCurrentPage - 1) * $scope.photographTableNumPerPage);
                    var end = begin + $scope.photographTableNumPerPage;
                    $scope.photographTableFilteredUsers = $scope.photographTablePhotographs.slice(begin, end);
                });
            }).error(function (error) {
                console.log(error);
                $rootScope.error(error);
            });
            $scope.photographTablePageChanged = function () {
                //check if max count was achieved, than load next photographs.
                //not implemented yet.
            };

            //user table
            $scope.userTableSearchUsername = "";
            $scope.userTableCurrentPage = 1;
            $scope.userTableNumPerPage = 5;
            var start = 0;
            var count = 100;
            UserService.getEnabledUsers(start, count).success(function (users) {
                $scope.userTableUsers = users;
                $scope.userTableTotalItems = users.length;
                $scope.userTableShowPagination = users.length > $scope.userTableNumPerPage;

                // Set watch on pagination numbers
                $scope.$watch('userTableCurrentPage + userTableNumPerPage', function () {
                    var begin = (($scope.userTableCurrentPage - 1) * $scope.userTableNumPerPage);
                    var end = begin + $scope.userTableNumPerPage;
                    $scope.userTableFilteredUsers = $scope.userTableUsers.slice(begin, end);
                });
            }).error(function (error) {
                console.log(error);
                $rootScope.error(error);
            });

            $scope.userTablePageChanged = function () {
                //check if max count was achieved, than load next 100 users.
                //not implemented yet.
            };

            $scope.edit = function (userId) {
                $location.path("profile/admin/edit/user/" + userId);
            };

        }

    ]);

/**
 * AdminEditUser controller.
 */
photoplatformControllers.controller(
    'AdminEditUserCtrl',
    [
        '$scope',
        '$routeParams',
        '$rootScope',
        '$location',
        '$http',
        '$cookieStore',
        'UserService',
        '$route',
        function ($scope, $routeParams, $rootScope, $location, $http, $cookieStore, UserService, $route) {
            //if user is not logged in or authorized redirect to login page
            var user = $cookieStore.get('user');
            if (undefined == user || !$rootScope.isLoggedIn()) {
                $location.path("/login");
                return;
            }
            //check param
            $scope.userId = $routeParams.userId;
            if ($scope.userId == undefined) {
                $scope.userId = user.id;
            }

            UserService.getUserProfileData($scope.userId).success(function (responseUserProfileData) {
                $scope.userProfileData = responseUserProfileData;
                $scope.roles = responseUserProfileData.roles;
            }).error(function (error) {
                console.log(error);
                $rootScope.error(error);
            });

            $scope.save = function () {
                UserService.updateUserProfileData($scope.userProfileData).success(function () {
                    $rootScope.success = "Profil erfolgreich aktualisiert";
                }).error(function (error) {
                    console.log(error);
                    $rootScope.error(error);
                });
                $location.path("/profile/admin");
            };

            $scope.cancel = function () {
                $location.path("/profile/admin");
            };

            $scope.isUserPhotographer = function () {
                if ($scope.userProfileData !== undefined) {
                    for (var i = 0; i < $scope.userProfileData.roles.length; ++i) {
                        if ($scope.userProfileData.roles[i] == $rootScope.PHOTOGRAPHER) {
                            return true;
                        }
                    }
                }
                return false;
            };

        }]);