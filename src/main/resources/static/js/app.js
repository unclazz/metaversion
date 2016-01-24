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
			var paginatedQueryAction = {
					method: 'GET',
					params: queryParams,
					isArray:false,
					transformResponse : xformResp
				};
			var resaveAction = {
					method: 'PUT',
					isArray:false
				};
			var customActions = {
				'query' : paginatedQueryAction,
				'resave' : resaveAction
			};
			entities[entityName] = $resource(urlPattern,urlParams, customActions);
		};
		var suggest = function(entityName, urlPattern, urlParams, queryParams) {
			var paginatedQueryAction = {
				method: 'GET',
				params: queryParams,
				isArray: true
			};
			var customActions = {
				'query' : paginatedQueryAction
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
		entity("Project", "api/projects/:id", {id: "@id"}, 
				angular.extend({like: '', pathbase: false, unlinkedCommitId: 0}, pagingParams));
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
	.factory('modals', function($log, $uibModal, $location) {
		var errorModal = function(error) {
			var modalInstance = $uibModal.open({
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
			return modalInstance;
		};
		
		var waitingModal = function(messages) {
			var modalInstance = $uibModal.open({
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
			return modalInstance;
		};
		
		return {
			errorModal: errorModal,
			waitingModal: waitingModal
		};
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
			if (data !== undefined) {
				$location.search(data);
			}
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
			templateUrl: 'js/templates/projects.html'
		}).when('/projects/new', {
			templateUrl: 'js/templates/projects$projectId$edit.html'
		}).when('/projects/:projectId', {
			templateUrl: 'js/templates/projects$projectId.html'
		}).when('/projects/:projectId/edit', {
			templateUrl: 'js/templates/projects$projectId$edit.html'
		}).when('/projects/:projectId/delete', {
			templateUrl: 'js/templates/projects$projectId$delete.html'
		}).when('/projects/:projectId/commits', {
			templateUrl: 'js/templates/projects$projectId$commits.html'
		}).when('/projects/:projectId/commits/:commitId/delete', {
			templateUrl: 'js/templates/projects$projectId$commits$commitId$delete.html'
		}).when('/projects/:projectId/changedpaths', {
			templateUrl: 'js/templates/projects$projectId$changedpaths.html'
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
	})
	.config(function($logProvider) {
		$logProvider.debugEnabled(true);
	})
	.controller('parent', function($scope, $location, $log) {
		// Paginationディレクティブのためのデフォルト値
		// ＊外部スコープにてページネーションに関わる値─とくにtotalSizeを初期化することで、
		// 画面初期表示時にページ番号が強制的にリセットされてしまう問題への対策としている。
		$scope.size = 25;
		$scope.totalSize = Number.MAX_VALUE;
		
		// ナビの項目のアクティブ/非アクティブを制御するためのマップ
		$scope.navItems = {
				projects: false,
				repositories: false,
				users: false
		};
		// パスの変化を監視するためのコールバックを作成・設定
		$scope.$watch(function() {
			return $location.path();
		}, function(path) {
			for (var k in $scope.navItems) {
				$scope.navItems[k] = path.endsWith(k);
			}
		});
	})
	.controller('errorModal', function ($scope, $uibModalInstance, $log, error) {
		// オブジェクトの階層化された表示など便利な面が多々あるためコンソールにも出力する
		$log.debug(error);
		// スコープに登録する
		$scope.error = error;
		// 「閉じる」ボタンのコールバック関数を登録する
		$scope.close = function () {
			$uibModalInstance.dismiss('cancel');
		};
	})
	.controller('waitingModal', function ($scope, $uibModalInstance, $log, messages) {
		$scope.messages = messages;
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
		$scope.open = true;
		// クエリ文字列をもとに検索条件を初期化
		$scope.cond = paths.queryToObject({page: 1, pathbase: false, like: ''});
		// サジェスト用の関数を作成・設定
		$scope.projectOrPathNames = function (partialName) {
			if (partialName.length < 3) return;
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
			// APIを介してプロジェクト一覧を取得
			entities.Project.query($scope.cond).$promise.then(function(paginated) {
				// 取得に成功したら結果を画面に反映させる
				$scope.totalSize = paginated.totalSize;
				$scope.size = paginated.size;
				$scope.list = paginated.list;
				if (paginated.page > 1) paths.entryToQuery('page', paginated.page);
			});
		};
		// 初期表示
		$scope.pageChange();
	})
	// プロジェクト詳細画面のためのコントローラ
	.controller('projects$projectId', function($log, $scope, $location, entities, paths) {
		$scope.project = entities.ProjectStats.get({id: paths.pathToIds().projectId});
	})
	// プロジェクト編集画面のためのコントローラ
	.controller('projects$projectId$edit', function($log, $scope, $location, entities, paths, modals) {
		// パスからIDを読み取る
		var ids = paths.pathToIds();
		if (ids.projectId !== undefined) {
			$scope.project = entities.Project.get({id: paths.pathToIds().projectId});
		} else {
			$scope.project = new entities.Project({id: undefined});
		}
		
		$scope.submit = function() {
			var p;
			if (ids.projectId !== undefined) {
				p = $scope.project.$resave();
			} else {
				p = $scope.project.$save();
			}
			$log.debug(p);
			p.then(function (data) {
				paths.stringToPath('projects/' + data.id);
			}, modals.errorModal);
		};
	})
	// プロジェクト削除画面のためのコントローラ
	.controller('projects$projectId$delete', function($log, $scope, $location, entities, paths, modals) {
		// パスからIDを読み取る
		var ids = paths.pathToIds();
		$scope.project = entities.Project.get({id: paths.pathToIds().projectId});
		
		$scope.submit = function() {
			var p = $scope.project.$remove();
			$log.debug(p);
			p.then(function (data) {
				paths.stringToPath('projects');
			}, modals.errorModal);
		};
	})
	// プロジェクトコミット一覧画面のためのコントローラ
	.controller('prjects$projectId$commits', function($log, $scope, $location, entities, paths) {
		var ids = paths.pathToIds();
		$scope.project = entities.ProjectStats.get({id: ids.projectId});

		// クエリ文字列をもとに検索条件を初期化
		$scope.cond = angular.extend(paths.queryToObject({page: 1}), ids);
		// ページ変更時にコールされる関数を作成・設定
		$scope.pageChange = function() {
			entities.ProjectCommit.query($scope.cond).$promise.then(function(paginated) {
				$scope.totalSize = paginated.totalSize;
				$scope.size = paginated.size;
				$scope.list = paginated.list;
				if (paginated.page > 1) paths.entryToQuery('page', paginated.page);
			});
		};
		// 初期表示
		$scope.pageChange();
	})
	// プロジェクトコミット紐付け解除画面のためのコントローラ
	.controller('projects$projectId$commits$commitId$delete', function($log, $scope, $location, entities, paths) {
		var ids = paths.pathToIds();
		$scope.project = entities.ProjectStats.get({id: ids.projectId});
		$scope.commit = entities.ProjectCommit.get(ids);

		$scope.submit = function() {
			var p = $scope.commit.$remove();
			$log.debug(p);
			p.then(function (data) {
				paths.stringToPath('projects/' + ids.projectId + '/commits');
			}, modals.errorModal);
		};
	})
	// プロジェクト変更パス一覧画面のためのコントローラ
	.controller('prjects$projectId$changedpaths', function($log, $scope, $location, entities, paths) {
		var ids = paths.pathToIds();
		$scope.project = entities.ProjectStats.get({id: ids.projectId});
		// クエリ文字列をもとに検索条件を初期化
		$scope.cond = angular.extend(paths.queryToObject({page: 1}), ids);
		// ページ変更時にコールされる関数を作成・設定
		$scope.pageChange = function() {
			entities.ProjectChangedPath.query($scope.cond).$promise.then(function(paginated) {
				$scope.totalSize = paginated.totalSize;
				$scope.size = paginated.size;
				$scope.list = paginated.list;
				if (paginated.page > 1) paths.entryToQuery('page', paginated.page);
			});
		};
		// 初期表示
		$scope.pageChange();
	})
	// リポジトリ一覧画面のためのコントローラ
	.controller('repositories', function($log, $scope, $location, entities, paths) {
		// クエリ文字列をもとに検索条件を初期化
		$scope.cond = paths.queryToObject({page: 1});
		// ページ変更時にコールされる関数を作成・設定
		$scope.pageChange = function() {
			// APIを介してリポジトリ一覧を取得
			entities.Repository.query($scope.cond).$promise.then(function(paginated) {
				$scope.totalSize = paginated.totalSize;
				$scope.size = paginated.size;
				$scope.list = paginated.list;
				if (paginated.page > 1) paths.entryToQuery('page', paginated.page);
			});
		};
		// 初期表示
		$scope.pageChange();
	})
	// リポジトリ詳細画面のためのコントローラ
	.controller('repositories$repositoryId', function($log, $scope, $location, entities, paths) {
		// パスからリポジトリIDを読み取る
		var ids = paths.pathToIds();
		// APIを介してリポジトリ情報を取得
		$scope.repository = entities.Repository.get({id: ids.repositoryId});
	})
	// リポジトリコミット一覧画面のためのコントローラ
	.controller('repositories$repositoryId$commits', function($log, $scope, $location, entities, paths) {
		// パスからリポジトリIDを読み取る
		var ids = paths.pathToIds();
		// APIを介してリポジトリ情報を取得
		$scope.repository = entities.Repository.get({id: ids.repositoryId});
		// クエリ文字列をもとに検索条件を初期化
		$scope.cond = angular.extend(paths.queryToObject({page: 1}), ids);

		// ページ変更時にコールされる関数を作成・設定
		$scope.pageChange = function() {
			entities.RepositoryCommitStats.query($scope.cond).$promise.then(function(paginated) {
				$scope.list = paginated.list;
				$scope.totalSize = paginated.totalSize;
				$scope.size = paginated.size;
				if (paginated.page > 1) paths.entryToQuery('page', paginated.page);
			});
		};
		
		// 初期表示
		$scope.pageChange();
	})
	// リポジトリ編集画面のためのコントローラ
	.controller('repositories$repositoryId$edit', function($log, $scope, entities, paths, modals) {
		// パスからIDを読み取る
		var ids = paths.pathToIds();
		if (ids.repositoryId !== undefined) {
			$scope.repository = entities.Repository.get({id: ids.repositoryId});
		} else {
			$scope.repository = new entities.Repository({
				id: undefined,
				baseUrl: 'http://www.example.com/svn',
				trunkPathPattern: '/trunk',
				branchPathPattern: '/branches/\\\w+',
				username: '',
				password: ''
			});
		}
		
		$scope.submit = function() {
			var p;
			if (ids.repositoryId !== undefined) {
				p = $scope.repository.$resave();
			} else {
				p = $scope.repository.$save();
			}
			var waitingModal = modals.waitingModal('リポジトリの登録・更新処理を実行中です。');
			p.then(function (data) {
				paths.stringToPath('repositories/' + data.id);
				waitingModal.close({});
			}, function(error) {
				modals.errorModal(error).result.then(angular.noop,function() {
					waitingModal.close();
				});
			});
		};
	})
	// リポジトリ削除画面のためのコントローラ
	.controller('repositories$repositoryId$delete', function($log, $scope, $location, entities, paths, modals) {
		// パスからIDを読み取る
		var ids = paths.pathToIds();
		$scope.repository = entities.Repository.get({id: paths.pathToIds().repositoryId});
		
		$scope.submit = function() {
			var p = $scope.repository.$remove();
			$log.debug(p);
			p.then(function (data) {
				paths.stringToPath('repositories');
			}, modals.errorModal);
		};
	})
	// コミット詳細画面のためのコントローラ
	.controller('repositories$repositoryId$commits$commitId', function($log, $scope, entities, paths) {
		// パスからリポジトリIDやコミットIDを読み取る
		var ids = paths.pathToIds();
		// APIを介してコミット情報を取得
		$scope.commit = entities.RepositoryCommit.get(ids);
		// APIを介してコミットの関連プロジェクトを取得
		$scope.projectList = entities.RepositoryCommitProject.query(ids);
	})
	.controller('repositories$repositoryId$commits$commitId$link', function($log, $scope, entities, paths) {
		// パスからリポジトリIDやコミットIDを読み取る
		var ids = paths.pathToIds();
		// APIを介してコミット情報を取得
		$scope.commit = entities.RepositoryCommit.get(ids);
		$scope.open = false;
		// クエリ文字列をもとに検索条件を初期化
		$scope.cond = paths.queryToObject({
			page: 1,
			pathbase: false,
			like: '',
			unlinkedCommitId: ids.commitId
		});
		// サジェスト用の関数を作成・設定
		$scope.projectOrPathNames = function (partialName) {
			if (partialName.length < 3) return;
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
			// APIを介してプロジェクト一覧を取得
			entities.Project.query($scope.cond).$promise.then(function(paginated) {
				// 取得に成功したら結果を画面に反映させる
				$scope.totalSize = paginated.totalSize;
				$scope.size = paginated.size;
				appendSelectedStatus(paginated.list);
				$scope.list = paginated.list;
				if (paginated.page > 1) paths.entryToQuery('page', paginated.page);
			});
		};
		// 初期表示
		$scope.pageChange();
		
		$scope.click = function() {
			for (var i = 0; i < $scope.list.length; i ++) {
				var item = $scope.list[i];
				if (!item.selected) continue;
				var link = new entities.ProjectCommit({commitId: ids.commitId, projectId: item.id});
				link.$save().then((function(item) {
					return function(data) {
						item.selected = false;
					}
				})(item));
			}
		};
		
		var appendSelectedStatus = function (list) {
			for (var i = 0; i < list.length; i ++) {
				list[i].selected = false;
			}
		}
	})
	.controller('repositories$repositoryId$commits$commitId$changedpaths', function($log, $scope, entities, paths) {
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
			entities.RepositoryCommitChangedPath.query($scope.cond).$promise.then(function(paginated) {
				// 取得に成功したら結果を画面に反映させる
				$scope.totalSize = paginated.totalSize;
				$scope.size = paginated.size;
				$scope.list = paginated.list;
				if (paginated.page > 1) paths.entryToQuery('page', paginated.page);
			});
		};
		// 初期表示
		$scope.pageChange();
	})
	// ユーザ一覧画面のためのコントローラ
	.controller('users', function($log, $scope, $location, entities, paths) {
		// クエリ文字列をもとに検索条件を初期化
		$scope.cond = paths.queryToObject({page: 1});
		// ページ変更時にコールされる関数を作成・設定
		$scope.pageChange = function() {
			entities.User.query($scope.cond).$promise.then(function(paginated) {
				$scope.totalSize = paginated.totalSize;
				$scope.size = paginated.size;
				$scope.list = paginated.list;
				if (paginated.page > 1) paths.entryToQuery('page', paginated.page);
			});
		};
		// 初期表示
		$scope.pageChange();
	})
	// ユーザ編集画面のためのコントローラ
	.controller('users$userId$edit', function($log, $scope, $location, entities, paths, modals) {
		// パスからIDを読み取る
		var ids = paths.pathToIds();
		if (ids.userId !== undefined) {
			$scope.user = entities.User.get({id: paths.pathToIds().userId});
			$scope.user.password = null;
		} else {
			$scope.user = new entities.User({id: undefined});
		}
		
		$scope.submit = function() {
			var p;
			if (ids.userId !== undefined) {
				p = $scope.user.$resave();
			} else {
				p = $scope.user.$save();
			}
			p.then(function (data) {
				paths.stringToPath('users');
			}, modals.errorModal);
		};
	})
	// ユーザ削除画面のためのコントローラ
	.controller('users$userId$delete', function($log, $scope, $location, entities, paths, modals) {
		// パスからIDを読み取る
		var ids = paths.pathToIds();
		$scope.user = entities.User.get({id: paths.pathToIds().userId});
		
		$scope.submit = function() {
			var p = $scope.user.$remove();
			p.then(function (data) {
				paths.stringToPath('users');
			}, modals.errorModal);
		};
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
