/// <reference path="app-config.ts" />
/// <reference path="app-entity.ts" />
/// <reference path="app-service.ts" />
/// <reference path="app-controller.ts" />
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
