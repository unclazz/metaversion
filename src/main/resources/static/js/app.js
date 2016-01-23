(function(angular) {
	var app = angular.module('app', ['ngResource', 'ui.bootstrap', 'ngRoute']);
	
	app.filter('excerpt', function () {
		return function (text, len) {
			if (len === undefined) {
				len = 100;
			}
			if(text !== undefined) {
				if(text.length > len) {
				return text.substring(0, len - 3) + '...';
				}
				else {
				return text;
				}
			}
		};
	})
	.factory('entities', function($log, $resource) {
		var entities = {};
		var pagingParams = {page: 1, size: 25};
		var suggestParams = {like: '', size: 25};
		var entity = function(entityName, urlPattern, urlParams, queryParams) {
			var xformResp = function(data) {
				var paginated = angular.fromJson(data);
				paginated.list = paginated.list.map(function (item) {
					return new entities[entityName](item)
				});
				return paginated;
			};
			var paginatedQuery = {
				method: 'GET',
				params: queryParams,
				isArray:false,
				transformResponse : xformResp
			};
			var customActions = {
				'query' : paginatedQuery
			};
			entities[entityName] = $resource(urlPattern,urlParams, customActions);
		};
		var suggest = function(entityName, urlPattern, urlParams, queryParams) {
			var paginatedQuery = {
				method: 'GET',
				params: queryParams,
				isArray: true
			};
			var customActions = {
				'query' : paginatedQuery
			};
			entities[entityName] = $resource(urlPattern,urlParams, customActions);
		};
		
		// UserエンティティのためのResourceオブジェクトを作成
		entity("User", "api/users/:id", {id: "@id"}, pagingParams);

		// SvnRepositoryエンティティのためのResourceオブジェクトを作成
		entity("Repository", "api/repositories/:id", {id: "@id"}, pagingParams);
		// SvnCommitエンティティのためのResourceオブジェクトを作成
		// ＊クエリ用パラメータのデフォルトとしてunlinkedプロパティを追加している
		entity("RepositoryCommit", "api/repositories/:repositoryId/commits/:commitId", 
				{repositoryId: "@repositoryId", commitId: "@commitId"},
				angular.extend({unlinked: false}, pagingParams));
		entity("RepositoryCommitStats", "api/repositories/:repositoryId/commitstats", 
				{repositoryId: "@repositoryId"}, pagingParams);
		entity("RepositoryCommitProject", "api/repositories/:repositoryId/commits/:commitId/projects", 
				{repositoryId: "@repositoryId", commitId: "@commitId"}, pagingParams);
		// SvnCommitPathエンティティのためのResourceオブジェクトを作成
		entity("RepositoryCommitChangedPath", "api/repositories/:repositoryId/commits/:commitId/changedpaths", 
				{repositoryId: "@repositoryId", commitId: "@commitId"}, pagingParams);
		
		// ProjectエンティティのためのResourceオブジェクトを作成
		entity("Project", "api/projects/:id", {id: "@id"}, pagingParams);
		entity("ProjectStats", "api/projectstats/:id", {id: "@id"}, pagingParams);
		// ProjectSvnCommitエンティティのためのResourceオブジェクトを作成
		entity("ProjectCommit", "api/projects/:projectId/commits/:commitId",
				{projectId: "@projectId", commitId: "@commitId"}, pagingParams);
		// ProjectChagedPathエンティティのためのResourceオブジェクトを作成
		entity("ProjectChangedPath", "api/projects/:projectId/changedpaths",
				{projectId: "@projectId"}, pagingParams);
		
		// サジェスト用のResourceオブジェクトを作成
		suggest("PathName", "api/pathnames", {}, suggestParams);
		suggest("ProjectName", "api/projectnames", {}, suggestParams);
		
		return entities;
	})
	.factory('paths', function($log, $location) {
		var queryToObject = function(defaultParams) {
			var params = defaultParams === undefined ? {} : angular.copy(defaultParams);
			var search = $location.search();
			if (search === undefined) {
				return params;
			}
			for (var k in search) {
				if (k in params) {
					var v = params[k];
					if (angular.isNumber(v)) {
						params[k] = search[k] - 0;
					} else if (angular.isString(v)) {
						params[k] = search[k] + '';
					} else if (v === true || v === false) {
						params[k] = search[k] == 'false' ? false : true;
					} else {
						params[k] = search[k];
					}
				} else {
					params[k] = search[k];
				}
			}
			return params;
		};
		var watch = function(scopeInstance, callback) {
			scopeInstance.$watch(function() {
				return $location.search().page;
			}, function(page) {
				callback(page === undefined ? 1 : page - 0);
			});
		}
		var stringToPath = function(path, data) {
			$location.path(path);
			$location.search(data);
		};
		var objectToQuery = function(data) {
			$location.search(data);
		};
		var entryToQuery = function(key, value) {
			var q = $location.search();
			q[key] = value;
			$location.search(q);
		};
		var pathToIds = function() {
			var vars = {};
			var url = $location.absUrl();
			var res = null;
			if (res = /\/users\/(\d+)/.exec(url)) {
				vars.userId = res[1] - 0;
			}
			if (res = /\/projects\/(\d+)/.exec(url)) {
				vars.projectId = res[1] - 0;
			}
			if (res = /\/repositories\/(\d+)/.exec(url)) {
				vars.repositoryId = res[1] - 0;
			}
			if (res = /\/commits\/(\d+)/.exec(url)) {
				vars.commitId = res[1] - 0;
			}
			return vars;
		};
		
		return {
			queryToObject: queryToObject,
			objectToQuery: objectToQuery,
			entryToQuery: entryToQuery,
			stringToPath: stringToPath,
			pathToIds: pathToIds,
			watchPage: watch
		};
	});
	
	angular.module('app')
	.config(function($routeProvider){
		$routeProvider.when('/', {
			templateUrl: 'js/templates/index.html'
		}).when('/projects', {
			templateUrl: 'js/templates/projects.html',
			reloadOnSearch: false
		}).when('/projects/:projectId', {
			templateUrl: 'js/templates/projects$projectId.html'
		}).when('/repositories', {
			templateUrl: 'js/templates/repositories.html',
			reloadOnSearch: false
		}).when('/repositories/:repositoryId', {
			templateUrl: 'js/templates/repositories$repositoryId.html',
			reloadOnSearch: false
		}).when('/repositories/:repositoryId/commits/:commitId', {
			templateUrl: 'js/templates/repositories$repositoryId$commits$commitId.html',
			reloadOnSearch: false
		}).when('/users', {
			templateUrl: 'js/templates/users.html',
			reloadOnSearch: false
		}).when('/users/new', {
			templateUrl: 'js/templates/users$userId$edit.html',
			reloadOnSearch: false
		}).when('/users/:userId/edit', {
			templateUrl: 'js/templates/users$userId$edit.html',
			reloadOnSearch: false
		}).when('/users/:userId/delete', {
			templateUrl: 'js/templates/users$userId$delete.html',
			reloadOnSearch: false
		}).otherwise({
			redirectTo: '/'
		});
	})
	.config(function($logProvider) {
		$logProvider.debugEnabled(true);
	})
	.controller('parent', function($scope, $location, $log) {
		$scope.navItems = {
				projects: false,
				repositories: false,
				users: false
		};
		$scope.$watch(function() {
			return $location.path();
		}, function(path) {
			for (var k in $scope.navItems) {
				$scope.navItems[k] = path.endsWith(k);
			}
		});
	})
	// トップ画面のためのコントローラ
	.controller('index', function($log, $scope, entities, paths) {
		// サジェスト用の関数を作成・設定
		$scope.projectNames = function (partialName) {
			// APIを通じてプロジェクト名を取得して返す
			return entities.ProjectName.query({like: partialName}).$promise;
		};
		// 検索ボタンがクリックされたときにコールされる関数を作成・設定
		$scope.submit = function() {
			// プロジェクト一覧画面に遷移させる
			paths.stringToPath('projects', {like: $scope.like});
		};
	})
	// プロジェクト一覧画面のためのコントローラ
	.controller('projects', function($log, $scope, $location, entities, paths) {
		// クエリ文字列をもとに検索条件を初期化
		$scope.cond = paths.queryToObject({page: 1, pathbase: false, like: ''});
		// サジェスト用の関数を作成・設定
		$scope.projectOrPathNames = function (partialName) {
			// 変更パス・ベースの検索かどうかをチェック
			if ($scope.cond.pathbase) {
				// 変更パス・ベースの検索の場合
				// APIを通じて変更パス名を取得して返す
				return entities.PathName.query({like: partialName}).$promise;
			} else {
				// そうでない場合
				// APIを通じてプロジェクト名を取得して返す
				return entities.ProjectName.query({like: partialName}).$promise;
			}
		};
		// 検索ボタンがクリックされたときにコールされる関数を作成・設定
		$scope.submit = function() {
			// プロジェクト一覧画面に遷移させる
			paths.objectToQuery($scope.cond);
		};
		// ページ変更時にコールされる関数を作成・設定
		$scope.pageChange = function() {
			paths.entryToQuery('page', $scope.cond.page)
		};
		// クエリ文字列が変化した際にコールされる関数を作成・設定
		$scope.$watch(function () {
			return $location.search();
		}, function(search) {
			// APIを介してプロジェクト一覧を取得
			entities.Project.query($scope.cond).$promise.then(function(paginated) {
				// 取得に成功したら結果を画面に反映させる
				$scope.totalSize = paginated.totalSize;
				$scope.size = paginated.size;
				$scope.list = paginated.list;
			});
		});
	})
	// プロジェクト詳細画面のためのコントローラ
	.controller('projects$projectId', function($log, $scope, $location, entities, paths) {
		$scope.project = entities.ProjectStats.get({id: paths.pathToIds().projectId});
	})
	// ユーザ一覧画面のためのコントローラ
	.controller('users', function($log, $scope, $location, entities, paths) {
		// クエリ文字列をもとに検索条件を初期化
		$scope.cond = paths.queryToObject({page: 1});
		// ページ変更時にコールされる関数を作成・設定
		$scope.pageChange = function() {
			paths.entryToQuery('page', $scope.cond.page)
		};
		// クエリ文字列が変化した際にコールされる関数を作成・設定
		paths.watchPage($scope, function(p) {
			// 変化後のページ番号を検索条件に反映させる
			$scope.cond.page = p;
			// APIを介してリポジトリ一覧を取得
			entities.User.query($scope.cond).$promise.then(function(paginated) {
				$scope.totalSize = paginated.totalSize;
				$scope.size = paginated.size;
				$scope.list = paginated.list;
			});
		});
	})
	// リポジトリ一覧画面のためのコントローラ
	.controller('repositories', function($log, $scope, $location, entities, paths) {
		// クエリ文字列をもとに検索条件を初期化
		$scope.cond = paths.queryToObject({page: 1});
		// ページ変更時にコールされる関数を作成・設定
		$scope.pageChange = function() {
			paths.entryToQuery('page', $scope.cond.page)
		};
		// クエリ文字列が変化した際にコールされる関数を作成・設定
		paths.watchPage($scope, function(p) {
			// 変化後のページ番号を検索条件に反映させる
			$scope.cond.page = p;
			// APIを介してリポジトリ一覧を取得
			entities.Repository.query($scope.cond).$promise.then(function(paginated) {
				$scope.totalSize = paginated.totalSize;
				$scope.size = paginated.size;
				$scope.list = paginated.list;
			});
		});
	})
	// コミット詳細画面のためのコントローラ
	.controller('repositories$repositoryId$commits$commitId', function($log, $scope, $location, entities, paths) {
		// パスからリポジトリIDやコミットIDを読み取る
		var ids = paths.pathToIds();
		// APIを介してコミット情報を取得
		$scope.commit = entities.RepositoryCommit.get(ids);
		// APIを介してコミットの関連プロジェクトを取得
		$scope.projectList = entities.RepositoryCommitProject.query(ids);
		// クエリ文字列をもとに検索条件を初期化
		$scope.cond = angular.extend(paths.queryToObject({page: 1}), ids);
		// ページ変更時にコールされる関数を作成・設定
		$scope.pageChange = function() {
			paths.entryToQuery('page', $scope.cond.page)
		};
		// クエリ文字列が変化した際にコールされる関数を作成・設定
		paths.watchPage($scope, function(p) {
			// 変化後のページ番号を検索条件に反映させる
			$scope.cond.page = p;
			// APIを介してコミットに紐づく変更パスを取得
			entities.RepositoryCommitChangedPath.query($scope.cond).$promise.then(function(paginated) {
				// 取得に成功したら結果を画面に反映させる
				$scope.totalSize = paginated.totalSize;
				$scope.size = paginated.size;
				$scope.list = paginated.list;
			});
		});
	})
	// リポジトリ詳細画面のためのコントローラ
	.controller('repositories$repositoryId', function($log, $scope, $location, entities, paths) {
		// パスからリポジトリIDを読み取る
		var ids = paths.pathToIds();
		// APIを介してリポジトリ情報を取得
		$scope.repository = entities.Repository.get({id: ids.repositoryId});
		// クエリ文字列をもとに検索条件を初期化
		$scope.cond = angular.extend(paths.queryToObject({page: 1}), ids);
		// ページ変更時にコールされる関数を作成・設定
		$scope.pageChange = function() {
			paths.entryToQuery('page', $scope.cond.page)
		};
		// クエリ文字列が変化した際にコールされる関数を作成・設定
		paths.watchPage($scope, function(p) {
			entities.RepositoryCommitStats.query($scope.cond).$promise.then(function(paginated) {
				$scope.totalSize = paginated.totalSize;
				$scope.size = paginated.size;
				$scope.list = paginated.list;
			});
		});
	});
	
	// mvApiTesterモジュールを追加
	angular.module('mvApiTester', ['mvCommon', 'ngResource', 'ui.bootstrap']);
	
	// mvApiTesterにmainコントローラを追加
	angular.module('mvApiTester')
	.controller("main", function($scope, $location, $http, $filter, $log){
		var REQUEST_METHODS = ['GET', 'POST', 'PUT', 'DELETE'];
		var REQUEST_PREFIX = null;
		
		if ($location.protocol() === 'file') {
			var absUrl = $location.absUrl();
			var restapiEndpointIndex = absUrl.indexOf('/templates/restapi-tester.html');
			REQUEST_PREFIX = $location.absUrl().slice(0, restapiEndpointIndex) + '/static';
		} else {
			var absUrl = $location.absUrl();
			var restapiEndpointIndex = absUrl.indexOf('/apitester');
			REQUEST_PREFIX = $location.absUrl().slice(0, restapiEndpointIndex);
		}
		
		$scope.requestPrefix = REQUEST_PREFIX;
		$scope.requestMethod = REQUEST_METHODS[0];
		$scope.requestPath = '';
		$scope.requestBody = '';
		$scope.responseBody = '';
		
		$scope.requestSubmitClick = function() {
			var conf = {};
			conf.method = $scope.requestMethod;
			conf.url = $scope.requestPath;
			if (conf.method === 'POST' || conf.method === 'PUT') {
				conf.data = $scope.requestBody;
			}
			
			$http(conf).success(function(data, status, headers, conf){
				$scope.responseStatus = status;
				if (typeof data === 'string') {
					$scope.responseBody = data;
				} else {
					$scope.responseBody = $filter('json')(data);
				}
			}).error(function(data, status, headers, conf){
				$scope.responseStatus = status;
				if (typeof data === 'string') {
					$scope.responseBody = data;
				} else {
					$scope.responseBody = $filter('json')(data);
				}
			});
		};
		
		$scope.requestPathChange = function() {
			if ($scope.requestPath.length > 0 && $scope.requestPath.slice(0,1) === '/') {
				$scope.submitDisabled = false;
			} else {
				$scope.submitDisabled = true;
			}
		};
		
		$scope.requestPathChange();
	});

})(angular);
