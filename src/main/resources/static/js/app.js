var MetaVersion;
(function (MetaVersion) {
    function routeConfigurerFn($routeProvider) {
        $routeProvider.when('/', {
            templateUrl: 'js/templates/index.html'
        }).when('/projects', {
            templateUrl: 'js/templates/projects.html'
        }).when('/projects/new', {
            templateUrl: 'js/templates/projects$projectId$edit.html'
        }).when('/projects/:projectId', {
            templateUrl: 'js/templates/projects$projectId.html'
        }).when('/projects/:projectId/link', {
            templateUrl: 'js/templates/projects$projectId$link.html'
        }).when('/projects/:projectId/edit', {
            templateUrl: 'js/templates/projects$projectId$edit.html'
        }).when('/projects/:projectId/delete', {
            templateUrl: 'js/templates/projects$projectId$delete.html'
        }).when('/projects/:projectId/commits', {
            templateUrl: 'js/templates/projects$projectId$commits.html'
        }).when('/projects/:projectId/virtualchangedpaths', {
            templateUrl: 'js/templates/projects$projectId$virtualchangedpaths.html'
        }).when('/projects/:projectId/virtualchangedpaths/add', {
            templateUrl: 'js/templates/projects$projectId$virtualchangedpaths$add.html'
        }).when('/projects/:projectId/virtualchangedpaths/:virtualChangedPathId/delete', {
            templateUrl: 'js/templates/projects$projectId$virtualchangedpaths$id$delete.html'
        }).when('/projects/:projectId/commits/:commitId/delete', {
            templateUrl: 'js/templates/projects$projectId$commits$commitId$delete.html'
        }).when('/projects/:projectId/changedpaths', {
            templateUrl: 'js/templates/projects$projectId$changedpaths.html'
        }).when('/projects/:projectId/parallels', {
            templateUrl: 'js/templates/projects$projectId$parallels.html'
        }).when('/repositories', {
            templateUrl: 'js/templates/repositories.html'
        }).when('/repositories/new', {
            templateUrl: 'js/templates/repositories$repositoryId$edit.html'
        }).when('/repositories/:repositoryId', {
            templateUrl: 'js/templates/repositories$repositoryId.html'
        }).when('/repositories/:repositoryId/commits', {
            templateUrl: 'js/templates/repositories$repositoryId$commits.html'
        }).when('/repositories/:repositoryId/edit', {
            templateUrl: 'js/templates/repositories$repositoryId$edit.html'
        }).when('/repositories/:repositoryId/delete', {
            templateUrl: 'js/templates/repositories$repositoryId$delete.html'
        }).when('/repositories/:repositoryId/commits/:commitId', {
            templateUrl: 'js/templates/repositories$repositoryId$commits$commitId.html'
        }).when('/repositories/:repositoryId/commits/:commitId/link', {
            templateUrl: 'js/templates/repositories$repositoryId$commits$commitId$link.html'
        }).when('/repositories/:repositoryId/commits/:commitId/changedpaths', {
            templateUrl: 'js/templates/repositories$repositoryId$commits$commitId$changedpaths.html'
        }).when('/users', {
            templateUrl: 'js/templates/users.html'
        }).when('/users/new', {
            templateUrl: 'js/templates/users$new.html'
        }).when('/users/:userId/edit', {
            templateUrl: 'js/templates/users$userId$edit.html'
        }).when('/users/:userId/delete', {
            templateUrl: 'js/templates/users$userId$delete.html'
        }).otherwise({
            redirectTo: '/'
        });
    }
    MetaVersion.routeConfigurerFn = routeConfigurerFn;
    function logConfigurerFn($logProvider) {
        $logProvider.debugEnabled(true);
    }
    MetaVersion.logConfigurerFn = logConfigurerFn;
    function userPasswordValidateFactoryFn() {
        var d = {};
        d.require = 'ngModel';
        d.link = function (scope, elm, attrs, ctrl) {
            var c = ctrl;
            c.$parsers.push(function (viewValue) {
                var len = viewValue.length;
                if (len == 0 || len >= 8) {
                    c.$setValidity('userPassword', true);
                    return viewValue;
                }
                else {
                    c.$setValidity('userPassword', false);
                    return undefined;
                }
            });
        };
        return d;
    }
    MetaVersion.userPasswordValidateFactoryFn = userPasswordValidateFactoryFn;
    function regexValidateDirectiveFactoryFn() {
        var d = {};
        d.require = 'ngModel';
        d.link = function (scope, elm, attrs, ctrl) {
            var c = ctrl;
            c.$parsers.push(function (viewValue) {
                try {
                    var re = new RegExp(viewValue);
                    c.$setValidity('regex', true);
                    return re.source;
                }
                catch (e) {
                    c.$setValidity('regex', false);
                    return undefined;
                }
            });
        };
        return d;
    }
    MetaVersion.regexValidateDirectiveFactoryFn = regexValidateDirectiveFactoryFn;
    function excerptFilterFactoryFn() {
        return function (text, length) {
            if (length === undefined) {
                length = 100;
            }
            if (text !== null && text !== undefined) {
                if (text.length > length) {
                    return text.substring(0, length - 3) + '...';
                }
                else {
                    return text;
                }
            }
            return undefined;
        };
    }
    MetaVersion.excerptFilterFactoryFn = excerptFilterFactoryFn;
})(MetaVersion || (MetaVersion = {}));
var MetaVersion;
(function (MetaVersion) {
    function indexControllerFn($scope, entities, paths, modals) {
        $scope.projectNames = function (partialName) {
            return entities.projectNames.query({ like: partialName }, angular.noop, modals.errorModal).$promise;
        };
        $scope.submit = function () {
            paths.stringToPath('projects', { like: $scope.like });
        };
    }
    MetaVersion.indexControllerFn = indexControllerFn;
    function errorModalControllerFn($scope, $uibModalInstance, $log, error) {
        $log.debug(error);
        $scope.error = error;
        $scope.close = function () {
            $uibModalInstance.dismiss('cancel');
        };
    }
    MetaVersion.errorModalControllerFn = errorModalControllerFn;
    function waitingModalControllerFn($scope, $uibModalInstance, $log, messages) {
        $scope.messages = messages;
    }
    MetaVersion.waitingModalControllerFn = waitingModalControllerFn;
    function parentControllerFn($scope, $location, $interval, $log, entities) {
        $scope.size = 25;
        $scope.totalSize = Number.MAX_VALUE;
        $scope.navItems = {
            projects: false,
            repositories: false,
            users: false
        };
        $scope.$watch(function () {
            return $location.path();
        }, function (path) {
            $scope.makeTitle(undefined);
            $scope.navItems.projects = endsWith(path, "projects");
            $scope.navItems.repositories = endsWith(path, "repositories");
            $scope.navItems.users = endsWith(path, "users");
        });
        var updateLogImporter = function () {
            entities.batches.query({}, function (data) {
                var list = data.list;
                for (var i in list) {
                    var item = list[i];
                    if (item.program === 'LOG_IMPORTER') {
                        $scope.logImporter = item;
                        break;
                    }
                }
            }, function (error) {
                $scope.logImporter = {};
            });
        };
        updateLogImporter();
        $interval(updateLogImporter, 60000);
        $scope.appName = angular.element(document.getElementById('app-name')).text();
        $scope.headTitle = $scope.appName;
        $scope.title = '';
        $scope.appVersion = angular.element(document.getElementById('app-version')).text();
        $scope.appNameVersion = $scope.appName + ' ' + $scope.appVersion;
        $scope.makeTitle = function (title) {
            $scope.title = title;
            $scope.headTitle = (title !== undefined ? (title + ' | ') : '') + $scope.appName;
            return title;
        };
    }
    MetaVersion.parentControllerFn = parentControllerFn;
    function projectsControllerFn($log, $scope, $location, entities, paths, modals) {
        $scope.open = true;
        $scope.cond = paths.queryToObject({ page: 1, pathbase: 0, like: '' });
        $scope.projectOrPathNames = function (partialName) {
            if (partialName.length < 3)
                return;
            if ($scope.cond.pathbase) {
                return entities.pathNames.query({ like: partialName }, angular.noop, modals.errorModal).$promise;
            }
            else {
                return entities.projectNames.query({ like: partialName }, angular.noop, modals.errorModal).$promise;
            }
        };
        $scope.submit = function () {
            paths.objectToQuery($scope.cond);
        };
        $scope.pageChange = function () {
            entities.projects.query($scope.cond, function (paginated) {
                $scope.totalSize = paginated.totalSize;
                $scope.size = paginated.size;
                $scope.list = paginated.list;
                if (paginated.page > 1)
                    paths.entryToQuery('page', paginated.page);
            }, modals.errorModal);
        };
        $scope.pageChange();
    }
    MetaVersion.projectsControllerFn = projectsControllerFn;
    function projectsProjectIdControllerFn($log, $scope, entities, paths, modals) {
        $scope.project = entities.projectStats.get({ id: paths.pathToIds().projectId }, angular.noop, modals.errorModal);
    }
    MetaVersion.projectsProjectIdControllerFn = projectsProjectIdControllerFn;
    function projectsProjectIdEditControllerFn($log, $scope, $location, entities, paths, modals, $filter) {
        var ids = paths.pathToIds();
        if (ids.projectId !== undefined) {
            $scope.project = entities.projects.get({ id: paths.pathToIds().projectId }, angular.noop, modals.errorModal);
        }
        else {
            $scope.project = ({ id: undefined });
        }
        $scope.submit = function () {
            if (ids.projectId !== undefined) {
                entities.projects.resave($scope.project, successCallback, modals.errorModal);
            }
            else {
                entities.projects.save($scope.project, successCallback, modals.errorModal);
            }
        };
        function successCallback(data) {
            paths.stringToPath('projects/' + data.id);
        }
        $scope.dpOptions = {
            showWeeks: false,
            initDate: null
        };
        $scope.dpChange = function () {
            $scope.project.scheduledReleaseDate = $filter('date')($scope.dpDate, 'yyyy/MM/dd HH:mm:ss.sss');
        };
    }
    MetaVersion.projectsProjectIdEditControllerFn = projectsProjectIdEditControllerFn;
    function projectsProjectIdDeleteControllerFn($log, $scope, entities, paths, modals) {
        var ids = paths.pathToIds();
        $scope.project = entities.projects.get({ id: paths.pathToIds().projectId }, angular.noop, modals.errorModal);
        $scope.submit = function () {
            entities.projects.resave($scope.project, function (data) {
                paths.stringToPath('projects');
            }, modals.errorModal);
        };
    }
    MetaVersion.projectsProjectIdDeleteControllerFn = projectsProjectIdDeleteControllerFn;
    function projectsProjectIdCommitsControllerFn($log, $scope, entities, paths, modals) {
        var ids = paths.pathToIds();
        $scope.project = entities.projectStats.get({ id: ids.projectId }, angular.noop, modals.errorModal);
        $scope.open = false;
        $scope.cond = angular.extend(paths.queryToObject({ page: 1, pathbase: false, like: '' }), ids);
        $scope.pathNames = function (partialName) {
            if (partialName.length < 3)
                return;
            if ($scope.cond.pathbase) {
                return entities.pathNames.query({ like: partialName }, angular.noop, modals.errorModal).$promise;
            }
            else {
                return {};
            }
        };
        $scope.submit = function () {
            paths.objectToQuery($scope.cond);
        };
        $scope.pageChange = function () {
            entities.projectCommits.query($scope.cond, function (paginated) {
                $scope.totalSize = paginated.totalSize;
                $scope.size = paginated.size;
                $scope.list = paginated.list;
                if (paginated.page > 1)
                    paths.entryToQuery('page', paginated.page);
            }, modals.errorModal);
        };
        $scope.pageChange();
    }
    MetaVersion.projectsProjectIdCommitsControllerFn = projectsProjectIdCommitsControllerFn;
    function projectsProjectIdVirtualChangedPathsControllerFn($log, $scope, $location, entities, paths, modals) {
        var ids = paths.pathToIds();
        $scope.project = entities.projectStats.get({ id: ids.projectId }, angular.noop, modals.errorModal);
        $scope.cond = angular.extend(paths.queryToObject({ page: 1 }), ids);
        $scope.pageChange = function () {
            entities.projectVirtualChangedPaths.query($scope.cond, function (paginated) {
                $scope.totalSize = paginated.totalSize;
                $scope.size = paginated.size;
                $scope.list = paginated.list;
                if (paginated.page > 1)
                    paths.entryToQuery('page', paginated.page);
            }, modals.errorModal);
        };
        $scope.pageChange();
        $scope.csvDowloadUrl = function () {
            var nativePath = window.location.pathname.replace(/index$/, '');
            var ngPath = $location.path();
            return nativePath + 'csv' + ngPath;
        };
    }
    MetaVersion.projectsProjectIdVirtualChangedPathsControllerFn = projectsProjectIdVirtualChangedPathsControllerFn;
    function projectsProjectIdVirtualChangedPathsIdDeleteControllerFn($log, $scope, entities, paths, modals) {
        var ids = paths.pathToIds();
        $scope.project = entities.projectStats.get({ id: ids.projectId }, angular.noop, modals.errorModal);
        $scope.path = entities.projectVirtualChangedPaths.get(ids, angular.noop, modals.errorModal);
        $scope.submit = function () {
            var p = entities.projectVirtualChangedPaths.remove(ids, function (data) {
                paths.stringToPath('projects/' + ids.projectId + '/virtualchangedpaths');
            }, modals.errorModal);
        };
    }
    MetaVersion.projectsProjectIdVirtualChangedPathsIdDeleteControllerFn = projectsProjectIdVirtualChangedPathsIdDeleteControllerFn;
    function projectsProjectIdVirtualChangedPathsAddControllerFn($log, $scope, entities, paths, modals) {
        var ids = paths.pathToIds();
        $scope.project = entities.projectStats.get({ id: ids.projectId }, angular.noop, modals.errorModal);
        $scope.cond = angular.extend(paths.queryToObject({ page: 1, repositoryId: 0, like: '' }), ids);
        $scope.cond.unlinkedTo = ids.projectId;
        $scope.open = true;
        $scope.repositories = [];
        $scope.submit = function () {
            paths.objectToQuery($scope.cond);
        };
        entities.repositories.query({ size: 999 }, function name(paginated) {
            $scope.repositories = paginated.list;
            if ($scope.repositories.length > 0) {
                if ($scope.cond.repositoryId === 0) {
                    $scope.cond.repositoryId = $scope.repositories[0].id;
                }
                $scope.pageChange();
            }
        });
        $scope.pageChange = function () {
            if ($scope.cond.repositoryId == 0) {
                return;
            }
            entities.repositoryPathNames.query($scope.cond, function (paginated) {
                $scope.totalSize = paginated.totalSize;
                $scope.size = paginated.size;
                $scope.list = paginated.list;
                if (paginated.page > 1)
                    paths.entryToQuery('page', paginated.page);
            }, modals.errorModal);
        };
        $scope.click = function ($event) {
            var path = angular.element($event.target).attr('data-changed-path');
            entities.projectVirtualChangedPaths.save({ path: path, projectId: ids.projectId, repositoryId: $scope.cond.repositoryId }, $scope.pageChange, modals.errorModal);
        };
        $scope.pageChange();
    }
    MetaVersion.projectsProjectIdVirtualChangedPathsAddControllerFn = projectsProjectIdVirtualChangedPathsAddControllerFn;
    function projectsProjectIdCommitsLinkControllerFn($log, $scope, entities, paths, modals) {
        var ids = paths.pathToIds();
        $scope.project = entities.projectStats.get({ id: ids.projectId }, angular.noop, modals.errorModal);
        $scope.cond = angular.extend(paths.queryToObject({ page: 1, pathbase: 0, like: '' }), ids);
        $scope.cond.unlinked = true;
        $scope.open = $scope.cond.like !== undefined && $scope.cond.like.length > 0;
        $scope.projectOrPathNames = function (partialName) {
            if (partialName.length < 3)
                return;
            if ($scope.cond.pathbase) {
                return entities.pathNames.query({ like: partialName }, angular.noop, modals.errorModal).$promise;
            }
            else {
                return null;
            }
        };
        $scope.submit = function () {
            paths.objectToQuery($scope.cond);
        };
        $scope.pageChange = function () {
            entities.projectCommits.query($scope.cond, function (paginated) {
                $scope.totalSize = paginated.totalSize;
                $scope.size = paginated.size;
                $scope.list = paginated.list;
                if (paginated.page > 1)
                    paths.entryToQuery('page', paginated.page);
            }, modals.errorModal);
        };
        $scope.click = function ($event) {
            var commitId = angular.element($event.target).attr('data-commit-id');
            entities.projectCommits.save({ commitId: commitId, projectId: ids.projectId }, $scope.pageChange, modals.errorModal);
        };
        $scope.pageChange();
    }
    MetaVersion.projectsProjectIdCommitsLinkControllerFn = projectsProjectIdCommitsLinkControllerFn;
    function projectsProjectIdCommitsCommitIdDeleteControllerFn($log, $scope, entities, paths, modals) {
        var ids = paths.pathToIds();
        $scope.project = entities.projectStats.get({ id: ids.projectId }, angular.noop, modals.errorModal);
        $scope.commit = entities.projectCommits.get(ids, angular.noop, modals.errorModal);
        $scope.submit = function () {
            var p = entities.projectCommits.remove(ids, function (data) {
                paths.stringToPath('projects/' + ids.projectId + '/commits');
            }, modals.errorModal);
        };
    }
    MetaVersion.projectsProjectIdCommitsCommitIdDeleteControllerFn = projectsProjectIdCommitsCommitIdDeleteControllerFn;
    function projectsProjectIdChangedpathsControllerFn($log, $scope, $location, entities, paths, modals) {
        var ids = paths.pathToIds();
        $scope.project = entities.projectStats.get({ id: ids.projectId }, angular.noop, modals.errorModal);
        $scope.cond = angular.extend(paths.queryToObject({ page: 1 }), ids);
        $scope.pageChange = function () {
            entities.projectChangedPaths.query($scope.cond, function (paginated) {
                $scope.totalSize = paginated.totalSize;
                $scope.size = paginated.size;
                $scope.list = paginated.list;
                if (paginated.page > 1)
                    paths.entryToQuery('page', paginated.page);
            }, modals.errorModal);
        };
        $scope.pageChange();
        $scope.csvDowloadUrl = function () {
            var nativePath = window.location.pathname.replace(/index$/, '');
            var ngPath = $location.path();
            return nativePath + 'csv' + ngPath;
        };
    }
    MetaVersion.projectsProjectIdChangedpathsControllerFn = projectsProjectIdChangedpathsControllerFn;
    function projectsProjectIdParallelsControllerFn($log, $scope, $location, entities, paths, modals) {
        var ids = paths.pathToIds();
        $scope.project = entities.projectStats.get({ id: ids.projectId }, angular.noop, modals.errorModal);
        $scope.cond = angular.extend(paths.queryToObject({ page: 1 }), ids);
        $scope.pageChange = function () {
            entities.projectParallels.query($scope.cond, function (paginated) {
                $scope.totalSize = paginated.totalSize;
                $scope.size = paginated.size;
                markRepeatedItems(paginated.list);
                $scope.list = paginated.list;
                if (paginated.page > 1)
                    paths.entryToQuery('page', paginated.page);
            }, modals.errorModal);
        };
        $scope.pageChange();
        $scope.csvDowloadUrl = function () {
            var nativePath = window.location.pathname.replace(/index$/, '');
            var ngPath = $location.path();
            return nativePath + 'csv' + ngPath;
        };
        var markRepeatedItems = function name(list) {
            var previous = {};
            var repeated = function (name, value) {
                var prevValue = previous[name];
                previous[name] = value;
                return (prevValue !== undefined && prevValue == value);
            };
            for (var i = 0; i < list.length; i++) {
                var item = list[i];
                var repositoryRepeated = repeated('repositoryId', item.repositoryId);
                if (repositoryRepeated) {
                    item.repositoryRepeated = true;
                    var pathRepeated = repeated('path', item.path);
                    if (pathRepeated) {
                        item.pathRepeated = true;
                    }
                }
            }
        };
    }
    MetaVersion.projectsProjectIdParallelsControllerFn = projectsProjectIdParallelsControllerFn;
    function repositoriesControllerFn($log, $location, $scope, entities, paths, modals) {
        $scope.cond = paths.queryToObject({ page: 1 });
        $scope.pageChange = function () {
            entities.repositories.query($scope.cond, function (paginated) {
                $scope.totalSize = paginated.totalSize;
                $scope.size = paginated.size;
                $scope.list = paginated.list;
                if (paginated.page > 1)
                    paths.entryToQuery('page', paginated.page);
            }, modals.errorModal);
        };
        $scope.pageChange();
    }
    MetaVersion.repositoriesControllerFn = repositoriesControllerFn;
    function repositoriesRepositoryIdControllerFn($log, $location, $scope, entities, paths, modals) {
        var ids = paths.pathToIds();
        $scope.repository = entities.repositories.get({ id: ids.repositoryId }, angular.noop, modals.errorModal);
    }
    MetaVersion.repositoriesRepositoryIdControllerFn = repositoriesRepositoryIdControllerFn;
    function repositoriesRepositoryIdCommitsControllerFn($log, $location, $scope, entities, paths, modals) {
        var ids = paths.pathToIds();
        $scope.repository = entities.repositories.get({ id: ids.repositoryId }, angular.noop, modals.errorModal);
        $scope.cond = angular.extend(paths.queryToObject({ page: 1 }), ids);
        $scope.pageChange = function () {
            entities.repositoryCommitStats.query($scope.cond, function (paginated) {
                $scope.list = paginated.list;
                $scope.totalSize = paginated.totalSize;
                $scope.size = paginated.size;
                if (paginated.page > 1)
                    paths.entryToQuery('page', paginated.page);
            }, modals.errorModal);
        };
        $scope.pageChange();
    }
    MetaVersion.repositoriesRepositoryIdCommitsControllerFn = repositoriesRepositoryIdCommitsControllerFn;
    function repositoriesRepositoryIdEditControllerFn($log, $location, $scope, entities, paths, modals) {
        var ids = paths.pathToIds();
        if (ids.repositoryId !== undefined) {
            $scope.repository = entities.repositories.get({ id: ids.repositoryId }, angular.noop, modals.errorModal);
        }
        else {
            $scope.repository = new entities.repositories({
                id: undefined,
                baseUrl: 'http://www.example.com/svn',
                trunkPathPattern: '/trunk',
                branchPathPattern: '/branches/\\\w+',
                username: '',
                password: ''
            });
        }
        $scope.submit = function () {
            var waitingModal = modals.waitingModal('リポジトリの登録・更新処理を実行中です。');
            function successFn(data) {
                paths.stringToPath('repositories/' + data.id);
                waitingModal.close({});
            }
            function errorFn(error) {
                modals.errorModal(error).result.then(angular.noop, function () {
                    waitingModal.close();
                });
            }
            if (ids.repositoryId !== undefined) {
                entities.repositories.resave($scope.repository, successFn, errorFn);
            }
            else {
                entities.repositories.save($scope.repository, successFn, errorFn);
            }
        };
    }
    MetaVersion.repositoriesRepositoryIdEditControllerFn = repositoriesRepositoryIdEditControllerFn;
    function repositoriesRepositoryIdDeleteControllerFn($log, $location, $scope, entities, paths, modals) {
        var ids = paths.pathToIds();
        $scope.repository = entities.repositories.get({ id: paths.pathToIds().repositoryId }, angular.noop, modals.errorModal);
        $scope.submit = function () {
            entities.repositories.remove(function (data) {
                paths.stringToPath('repositories');
            }, modals.errorModal);
        };
    }
    MetaVersion.repositoriesRepositoryIdDeleteControllerFn = repositoriesRepositoryIdDeleteControllerFn;
    function repositoriesRepositoryIdCommitsCommitIdControllerFn($log, $location, $scope, entities, paths, modals) {
        var ids = paths.pathToIds();
        $scope.commit = entities.repositoryCommitStats.get(ids, angular.noop, modals.errorModal);
        $scope.projectList = entities.repositoryCommitProjects.query(ids, angular.noop, modals.errorModal);
    }
    MetaVersion.repositoriesRepositoryIdCommitsCommitIdControllerFn = repositoriesRepositoryIdCommitsCommitIdControllerFn;
    function repositoriesRepositoryIdCommitsCommitIdLinkControllerFn($log, $location, $scope, entities, paths, modals) {
        var ids = paths.pathToIds();
        $scope.commit = entities.repositoryCommits.get(ids, angular.noop, modals.errorModal);
        $scope.open = false;
        $scope.cond = paths.queryToObject({
            page: 1,
            pathbase: 0,
            like: '',
            unlinkedCommitId: ids.commitId
        });
        $scope.open = $scope.cond.like !== undefined && $scope.cond.like.length > 0;
        $scope.projectOrPathNames = function (partialName) {
            if (partialName.length < 3)
                return;
            if ($scope.cond.pathbase) {
                return entities.pathNames.query({ like: partialName }, angular.noop, modals.errorModal).$promise;
            }
            else {
                return entities.projectNames.query({ like: partialName }, angular.noop, modals.errorModal).$promise;
            }
        };
        $scope.submit = function () {
            paths.objectToQuery($scope.cond);
        };
        $scope.pageChange = function () {
            entities.projects.query($scope.cond, function (paginated) {
                $scope.totalSize = paginated.totalSize;
                $scope.size = paginated.size;
                appendSelectedStatus(paginated.list);
                $scope.list = paginated.list;
                if (paginated.page > 1)
                    paths.entryToQuery('page', paginated.page);
            }, modals.errorModal);
        };
        $scope.pageChange();
        $scope.click = function () {
            for (var i = 0; i < $scope.list.length; i++) {
                var item = $scope.list[i];
                if (!item.selected)
                    continue;
                var link = new entities.projectCommits({ commitId: ids.commitId, projectId: item.id });
                entities.projectCommits.save(link, $scope.pageChange, modals.errorModal);
            }
        };
        var appendSelectedStatus = function (list) {
            for (var i = 0; i < list.length; i++) {
                list[i].selected = false;
            }
        };
    }
    MetaVersion.repositoriesRepositoryIdCommitsCommitIdLinkControllerFn = repositoriesRepositoryIdCommitsCommitIdLinkControllerFn;
    function repositoriesRepositoryIdCommitsCommitIdChangedpathsControllerFn($log, $location, $scope, entities, paths, modals) {
        var ids = paths.pathToIds();
        $scope.commit = entities.repositoryCommits.get(ids, angular.noop, modals.errorModal);
        $scope.projectList = entities.repositoryCommitProjects.query(ids, angular.noop, modals.errorModal);
        $scope.cond = angular.extend(paths.queryToObject({ page: 1 }), ids);
        $scope.pageChange = function () {
            entities.repositoryCommitChangedPaths.query($scope.cond, function (paginated) {
                $scope.totalSize = paginated.totalSize;
                $scope.size = paginated.size;
                $scope.list = paginated.list;
                if (paginated.page > 1)
                    paths.entryToQuery('page', paginated.page);
            }, modals.errorModal);
        };
        $scope.pageChange();
    }
    MetaVersion.repositoriesRepositoryIdCommitsCommitIdChangedpathsControllerFn = repositoriesRepositoryIdCommitsCommitIdChangedpathsControllerFn;
    function usersControllerFn($log, $location, $scope, entities, paths, modals) {
        $scope.cond = paths.queryToObject({ page: 1 });
        $scope.pageChange = function () {
            entities.users.query($scope.cond, function (paginated) {
                $scope.totalSize = paginated.totalSize;
                $scope.size = paginated.size;
                $scope.list = paginated.list;
                if (paginated.page > 1)
                    paths.entryToQuery('page', paginated.page);
            }, modals.errorModal);
        };
        $scope.pageChange();
    }
    MetaVersion.usersControllerFn = usersControllerFn;
    function usersUserIdEditControllerFn($log, $location, $scope, entities, paths, modals) {
        var ids = paths.pathToIds();
        if (ids.userId !== undefined) {
            $scope.user = entities.users.get({ id: paths.pathToIds().userId }, angular.noop, modals.errorModal);
            $scope.user.password = null;
        }
        else {
            $scope.user = new entities.users({ id: undefined });
        }
        $scope.submit = function () {
            function successFn(data) {
                paths.stringToPath('users');
            }
            if (ids.userId !== undefined) {
                entities.users.resave($scope.user, successFn, modals.errorModal);
            }
            else {
                entities.users.save($scope.user, successFn, modals.errorModal);
            }
        };
    }
    MetaVersion.usersUserIdEditControllerFn = usersUserIdEditControllerFn;
    function usersUserIdDeleteControllerFn($log, $location, $scope, entities, paths, modals) {
        var ids = paths.pathToIds();
        $scope.user = entities.users.get({ id: paths.pathToIds().userId }, angular.noop, modals.errorModal);
        $scope.submit = function () {
            entities.users.remove(function (data) {
                paths.stringToPath('users');
            }, modals.errorModal);
        };
    }
    MetaVersion.usersUserIdDeleteControllerFn = usersUserIdDeleteControllerFn;
    function endsWith(target, subseq) {
        var tl = target.length;
        var sl = subseq.length;
        var p = target.lastIndexOf(subseq);
        return tl === (sl + p);
    }
})(MetaVersion || (MetaVersion = {}));
var MetaVersion;
(function (MetaVersion) {
    function entitiesFactoryFn($log, $resource) {
        var entities = {};
        var pagingParams = { page: 1, size: 25 };
        var projectsPagingParams = angular.extend({
            like: '', pathbase: false, unlinkedCommitId: 0
        }, pagingParams);
        var suggestParams = { like: '', size: 25 };
        entities.batches = entityResource("Batches", "api/batches/:programId", { programId: "@programId" }, pagingParams);
        entities.projectChangedPaths = entityResource("ProjectChangedPath", "api/projects/:projectId/changedpaths", { projectId: "@projectId" }, pagingParams);
        entities.projectVirtualChangedPaths = entityResource("ProjectVirtualChangedPath", "api/projects/:projectId/virtualchangedpaths/:virtualChangedPathId", { projectId: "@projectId", virtualChangedPathId: "@virtualChangedPathId" }, pagingParams);
        entities.projectCommits = entityResource("ProjectCommit", "api/projects/:projectId/commits/:commitId", { projectId: "@projectId", commitId: "@commitId" }, pagingParams);
        entities.projectParallels = entityResource("ProjectParallels", "api/projects/:projectId/parallels", { projectId: "@projectId" }, pagingParams);
        entities.projects = entityResource("Project", "api/projects/:id", { id: "@id" }, projectsPagingParams);
        entities.projectStats = entityResource("ProjectStats", "api/projectstats/:id", { id: "@id" }, pagingParams);
        entities.repositories = entityResource("Repository", "api/repositories/:id", { id: "@id" }, pagingParams);
        entities.repositoryCommitChangedPaths = entityResource("RepositoryCommitChangedPath", "api/repositories/:repositoryId/commits/:commitId/changedpaths", { repositoryId: "@repositoryId", commitId: "@commitId" }, pagingParams);
        entities.repositoryCommitProjects = entityResource("RepositoryCommitProject", "api/repositories/:repositoryId/commits/:commitId/projects", { repositoryId: "@repositoryId", commitId: "@commitId" }, pagingParams);
        entities.repositoryCommits = entityResource("RepositoryCommit", "api/repositories/:repositoryId/commits/:commitId", { repositoryId: "@repositoryId", commitId: "@commitId" }, angular.extend({ unlinked: false }, pagingParams));
        entities.repositoryCommitStats = entityResource("RepositoryCommitStats", "api/repositories/:repositoryId/commitstats/:commitId", { repositoryId: "@repositoryId", commitId: "@commitId" }, pagingParams);
        entities.users = entityResource("User", "api/users/:id", { id: "@id" }, pagingParams);
        entities.pathNames = suggestResource("PathName", "api/pathnames", {}, suggestParams);
        entities.projectNames = suggestResource("ProjectName", "api/projectnames", {}, suggestParams);
        entities.repositoryPathNames = entityResource("RepositoryPathName", "api/repositories/:repositoryId/pathnames", { repositoryId: "@repositoryId" }, pagingParams);
        return entities;
        function entityResource(entityName, urlPattern, urlParams, queryParams) {
            var xformResp = function (data) {
                var paginated = angular.fromJson(data);
                if (paginated.list === undefined) {
                    return data;
                }
                return paginated;
            };
            var paginatedQueryAction = {
                method: 'GET',
                params: queryParams,
                isArray: false,
                transformResponse: xformResp
            };
            var resaveAction = {
                method: 'PUT',
                isArray: false
            };
            var customActions = {
                'query': paginatedQueryAction,
                'resave': resaveAction
            };
            return $resource(urlPattern, urlParams, customActions);
        }
        function suggestResource(entityName, urlPattern, urlParams, queryParams) {
            var paginatedQueryAction = {
                method: 'GET',
                params: queryParams,
                isArray: true
            };
            var customActions = {
                'query': paginatedQueryAction
            };
            return $resource(urlPattern, urlParams, customActions);
        }
    }
    MetaVersion.entitiesFactoryFn = entitiesFactoryFn;
})(MetaVersion || (MetaVersion = {}));
var MetaVersion;
(function (MetaVersion) {
    function pathsFactoryFn($log, $location) {
        var p = {};
        p.entryToQuery = function (key, value) {
            var q = $location.search();
            q[key] = value;
            $location.search(q);
        };
        p.objectToQuery = function (data) {
            $location.search(data);
        };
        p.pathToIds = function () {
            var ids = {};
            var url = $location.absUrl();
            var res = null;
            if (res = /\/users\/(\d+)/.exec(url)) {
                ids.userId = +res[1];
            }
            if (res = /\/projects\/(\d+)/.exec(url)) {
                ids.projectId = +res[1];
            }
            if (res = /\/repositories\/(\d+)/.exec(url)) {
                ids.repositoryId = +res[1];
            }
            if (res = /\/commits\/(\d+)/.exec(url)) {
                ids.commitId = +res[1];
            }
            if (res = /\/virtualchangedpaths\/(\d+)/.exec(url)) {
                ids.virtualChangedPathId = +res[1];
            }
            return ids;
        };
        p.queryToObject = function (defaultParams) {
            var params = defaultParams === undefined ? {} : angular.copy(defaultParams);
            var search = $location.search();
            if (search === undefined) {
                return params;
            }
            for (var k in search) {
                if (k in params) {
                    var v = params[k];
                    if (angular.isNumber(v)) {
                        if (search[k] === 'true') {
                            params[k] = 1;
                        }
                        else if (search[k] === 'false') {
                            params[k] = 0;
                        }
                        else {
                            params[k] = search[k] - 0;
                        }
                    }
                    else if (angular.isString(v)) {
                        params[k] = search[k] + '';
                    }
                    else if (v === true || v === false) {
                        params[k] = search[k] == 'false' ? false : true;
                    }
                    else {
                        params[k] = search[k];
                    }
                }
                else {
                    params[k] = search[k];
                }
            }
            return params;
        };
        p.stringToPath = function (path, data) {
            $location.path(path);
            if (data !== undefined) {
                $location.search(data);
            }
        };
        return p;
    }
    MetaVersion.pathsFactoryFn = pathsFactoryFn;
    function modalsFactoryFn($log, $uibModal, $location) {
        var m = {};
        m.errorModal = function (error) {
            return $uibModal.open({
                templateUrl: 'js/templates/errorModal.html',
                controller: 'errorModal',
                size: 'lg',
                backdrop: 'static',
                resolve: {
                    error: function () {
                        return error;
                    }
                }
            });
        };
        m.waitingModal = function (messages) {
            return $uibModal.open({
                templateUrl: 'js/templates/waitingModal.html',
                controller: 'waitingModal',
                size: 'lg',
                backdrop: 'static',
                resolve: {
                    messages: function () {
                        return angular.isString(messages) ? [messages] : messages;
                    }
                }
            });
        };
        return m;
    }
    MetaVersion.modalsFactoryFn = modalsFactoryFn;
})(MetaVersion || (MetaVersion = {}));
(function () {
    var mv = MetaVersion;
    var app = angular.module('app', ['ngResource', 'ngRoute', 'ui.bootstrap']);
    app
        .config(mv.logConfigurerFn)
        .config(mv.routeConfigurerFn)
        .directive('regexValidate', mv.regexValidateDirectiveFactoryFn)
        .directive('userPasswordValidate', mv.userPasswordValidateFactoryFn)
        .filter('excerpt', mv.excerptFilterFactoryFn)
        .factory('modals', mv.modalsFactoryFn)
        .factory('paths', mv.pathsFactoryFn)
        .factory('entities', mv.entitiesFactoryFn)
        .controller('parent', mv.parentControllerFn)
        .controller('errorModal', mv.errorModalControllerFn)
        .controller('waitingModal', mv.waitingModalControllerFn)
        .controller('index', mv.indexControllerFn)
        .controller('projects', mv.projectsControllerFn)
        .controller('projects$projectId', mv.projectsProjectIdControllerFn)
        .controller('projects$projectId$edit', mv.projectsProjectIdEditControllerFn)
        .controller('projects$projectId$delete', mv.projectsProjectIdDeleteControllerFn)
        .controller('projects$projectId$commits', mv.projectsProjectIdCommitsControllerFn)
        .controller('projects$projectId$virtualchangedpaths', mv.projectsProjectIdVirtualChangedPathsControllerFn)
        .controller('projects$projectId$virtualchangedpaths$add', mv.projectsProjectIdVirtualChangedPathsAddControllerFn)
        .controller('projects$projectId$virtualchangedpaths$id$delete', mv.projectsProjectIdVirtualChangedPathsIdDeleteControllerFn)
        .controller('projects$projectId$commits$link', mv.projectsProjectIdCommitsLinkControllerFn)
        .controller('projects$projectId$commits$commitId$delete', mv.projectsProjectIdCommitsCommitIdDeleteControllerFn)
        .controller('projects$projectId$changedpaths', mv.projectsProjectIdChangedpathsControllerFn)
        .controller('projects$projectId$parallels', mv.projectsProjectIdParallelsControllerFn)
        .controller('repositories', mv.repositoriesControllerFn)
        .controller('repositories$repositoryId', mv.repositoriesRepositoryIdControllerFn)
        .controller('repositories$repositoryId$commits', mv.repositoriesRepositoryIdCommitsControllerFn)
        .controller('repositories$repositoryId$edit', mv.repositoriesRepositoryIdEditControllerFn)
        .controller('repositories$repositoryId$delete', mv.repositoriesRepositoryIdDeleteControllerFn)
        .controller('repositories$repositoryId$commits$commitId', mv.repositoriesRepositoryIdCommitsCommitIdControllerFn)
        .controller('repositories$repositoryId$commits$commitId$link', mv.repositoriesRepositoryIdCommitsCommitIdLinkControllerFn)
        .controller('repositories$repositoryId$commits$commitId$changedpaths', mv.repositoriesRepositoryIdCommitsCommitIdChangedpathsControllerFn)
        .controller('users', mv.usersControllerFn)
        .controller('users$userId$edit', mv.usersUserIdEditControllerFn)
        .controller('users$userId$delete', mv.usersUserIdDeleteControllerFn);
})();
//# sourceMappingURL=app.js.map