/// <reference path="app-config.ts" />
/// <reference path="app-entity.ts" />
/// <reference path="app-service.ts" />
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
	.controller('projects$projectId', mv.projectsProjectIdConrollerFn)
    .controller('projects$projectId$edit', mv.projectsProjectIdEditControllerFn)
	.controller('projects$projectId$delete', mv.projectsProjectIdDeleteControllerFn)
    .controller('prjects$projectId$commits', mv.projectsProjectIdCommitsControllerFn)
})();

module MetaVersion {
    
    export interface IErrorData {
    }
    
    interface INavItemSwithes {
        projects :boolean;
        repositories :boolean;
        users :boolean;
    }
    
    interface IParentScope extends ng.IScope {
        size :number;
        totalSize :number;
        navItems :INavItemSwithes;
        appName :string;
        appVersion :string;
        appNameVersion :string;
        headTitle :string;
        title :string;
        makeTitle :(title :string) => string;
        logImporter :IBatch;
    }
    
    interface IErrorModalScope extends ng.IScope {
        error :IErrorData;
        close :() => void;
    }
    
    interface IWaitingModalScope extends ng.IScope {
        messages :string[];
    }
    
    interface IIndexScope extends IParentScope {
        projectNames :(subseq :string) => ng.IPromise<ng.resource.IResourceArray<string>>;
        submit :() => void;
        like :string
    }
    
    interface IProjectsScope extends IParentScope {
        open :boolean;
        cond :any;
        projectOrPathNames :(subseq :string) => ng.IPromise<ng.resource.IResourceArray<string>>;
        submit :() => void;
        pageChange :() => void;
        list :IProject[];
    }
    
    export function indexControllerFn($scope :IIndexScope,
        entities :IEntityService, paths :IPathService, modals :IModalService) {
		// サジェスト用の関数を作成・設定
		$scope.projectNames = function (partialName :string) {
			// APIを通じてプロジェクト名を取得して返す
			return entities.projectNames.query({like: partialName},
					angular.noop, modals.errorModal).$promise;
		};
		// 検索ボタンがクリックされたときにコールされる関数を作成・設定
		$scope.submit = function() {
			// プロジェクト一覧画面に遷移させる
			paths.stringToPath('projects', {like: $scope.like});
		};
	}
    
    export function errorModalControllerFn($scope :IErrorModalScope,
            $uibModalInstance :ng.ui.bootstrap.IModalServiceInstance, 
            $log :ng.ILogService, error :IErrorData) {
            
		// オブジェクトの階層化された表示など便利な面が多々あるためコンソールにも出力する
		$log.debug(error);
		// スコープに登録する
		$scope.error = error;
		// 「閉じる」ボタンのコールバック関数を登録する
		$scope.close = function () {
			$uibModalInstance.dismiss('cancel');
		};
    }
    
    export function waitingModalControllerFn($scope :IWaitingModalScope,
            $uibModalInstance :ng.ui.bootstrap.IModalServiceInstance, 
            $log :ng.ILogService, messages :string[]) {
		$scope.messages = messages;
	}
    
    export function parentControllerFn($scope :IParentScope,
        $location :ng.ILocationService, $interval :ng.IIntervalService, 
        $log :ng.ILogService, entities :IEntityService) {
        
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
			$scope.makeTitle(undefined);
            $scope.navItems.projects = endsWith(path, "projects");
            $scope.navItems.repositories = endsWith(path, "repositories");
            $scope.navItems.users = endsWith(path, "users");
		});
		
		var updateLogImporter = function() {
			entities.batches.query({}, function(data :{list :IBatch[]}) {
				const list = data.list;
				for (var i in list) {
					const item = list[i];
					if (item.program === 'LOG_IMPORTER') {
						$scope.logImporter = item;
						break;
					}
				}
			}, function(error :any) {
				$scope.logImporter = <IBatch>{};
			});
		};
		updateLogImporter();
		$interval(updateLogImporter, 60000);
		$scope.appName = angular.element(document.getElementById('app-name')).text();
		$scope.headTitle = $scope.appName;
		$scope.title = '';
		$scope.appVersion = angular.element(document.getElementById('app-version')).text();
		$scope.appNameVersion = $scope.appName + ' ' + $scope.appVersion;
		$scope.makeTitle = function(title) {
			$scope.title = title;
			$scope.headTitle = (title !== undefined ? (title + ' | ') : '') + $scope.appName;
			return title;
		}
    }
    
	// プロジェクト一覧画面のためのコントローラ
	export function projectsControllerFn($log :ng.ILogService, 
            $scope :IProjectsScope, $location :ng.ILocationService,
            entities :IEntityService, paths :IPathService, modals :IModalService) {
		
        $scope.open = true;
		// クエリ文字列をもとに検索条件を初期化
		$scope.cond = paths.queryToObject({page: 1, pathbase: 0, like: ''});
		// サジェスト用の関数を作成・設定
		$scope.projectOrPathNames = function (partialName) {
			if (partialName.length < 3) return;
			// 変更パス・ベースの検索かどうかをチェック
			if ($scope.cond.pathbase) {
				// 変更パス・ベースの検索の場合
				// APIを通じて変更パス名を取得して返す
				return entities.pathNames.query({like: partialName},
						angular.noop, modals.errorModal).$promise;
			} else {
				// そうでない場合
				// APIを通じてプロジェクト名を取得して返す
				return entities.projectNames.query({like: partialName},
						angular.noop, modals.errorModal).$promise;
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
			entities.projects.query($scope.cond, function(paginated :IPaginated<IProject>) {
				// 取得に成功したら結果を画面に反映させる
				$scope.totalSize = paginated.totalSize;
				$scope.size = paginated.size;
				$scope.list = paginated.list;
				if (paginated.page > 1) paths.entryToQuery('page', paginated.page);
			}, modals.errorModal);
		};
		// 初期表示
		$scope.pageChange();
	}
    
    interface IProjectsProjectIdScope extends IParentScope {
        project :IProjectStats;
    }
    
    export function projectsProjectIdConrollerFn($log :ng.ILogService,
        $scope :IProjectsProjectIdScope, entities :IEntityService, 
        paths :IPathService, modals :IModalService) {
		$scope.project = entities.projectStats.get({id: paths.pathToIds().projectId},
				angular.noop, modals.errorModal)
    }
    
	// プロジェクト編集画面のためのコントローラ
	// .controller('projects$projectId$edit',
    
    interface IProjectsProjectIdEditScope extends IParentScope {
        submit : () => void;
        project : IProject;
    } 
    
    export function projectsProjectIdEditControllerFn($log :ng.ILogService, 
        $scope :IProjectsProjectIdEditScope, $location :ng.ILogService, 
        entities :IEntityService, paths :IPathService, modals :IModalService) {

		const ids = paths.pathToIds();
		if (ids.projectId !== undefined) {
			$scope.project = entities.projects.get({id: paths.pathToIds().projectId},
					angular.noop, modals.errorModal);
		} else {
			$scope.project = <IProject>({id: undefined});
		}
		
		$scope.submit = function() {
			if (ids.projectId !== undefined) {
                entities.projects.resave($scope.project, successCallback, modals.errorModal);
			} else {
                entities.projects.save($scope.project, successCallback, modals.errorModal);
			}
		};
        
        function successCallback(data :IProject) {
            paths.stringToPath('projects/' + data.id);
        }
	}
    
    export function projectsProjectIdDeleteControllerFn ($log :ng.ILogService,
        $scope :IProjectsProjectIdEditScope, entities :IEntityService, 
        paths :IPathService, modals :IModalService) {

		var ids = paths.pathToIds();
		$scope.project = entities.projects.get({id: paths.pathToIds().projectId},
				angular.noop, modals.errorModal);
		
		$scope.submit = function() {
            entities.projects.resave($scope.project, function (data) {
				paths.stringToPath('projects');
			}, modals.errorModal);
		};
	}
    
    interface IProjectsProjectIdCommitsScope extends IParentScope {
        project :IProjectStats;
        list :IProjectCommit[];
        open :boolean;
        cond :any;
        pathNames :(subseq :string) => ng.IPromise<ng.resource.IResourceArray<string>>;
        submit :() => void;
        pageChange :() => void;
    }

	// プロジェクトコミット一覧画面のためのコントローラ
    export function projectsProjectIdCommitsControllerFn($log :ng.ILogService, 
        $scope :IProjectsProjectIdCommitsScope, entities :IEntityService, 
        paths :IPathService, modals :IModalService) {
		// パスからID情報を取得
		var ids = paths.pathToIds();
		// プロジェクト情報を取得
		$scope.project = entities.projectStats.get({id: ids.projectId},
				angular.noop, modals.errorModal);
		// 検索条件欄はデフォルトでは閉じておく
		$scope.open = false;

		// クエリ文字列をもとに検索条件を初期化
		$scope.cond = angular.extend(paths.queryToObject({page: 1, pathbase: false, like: ''}), ids);
		// サジェスト用の関数を作成・設定
		$scope.pathNames = function (partialName) {
			if (partialName.length < 3) return;
			// 変更パス・ベースの検索かどうかをチェック
			if ($scope.cond.pathbase) {
				// 変更パス・ベースの検索の場合
				// APIを通じて変更パス名を取得して返す
				return entities.pathNames.query({like: partialName},
						angular.noop, modals.errorModal).$promise;
			} else {
				// そうでない場合（型消去を前提にして強引に検証をパスさせる）
				return {} as ng.IPromise<ng.resource.IResourceArray<string>>;
			}
		};
		// 検索ボタンがクリックされたときにコールされる関数を作成・設定
		$scope.submit = function() {
			// プロジェクト一覧画面に遷移させる
			paths.objectToQuery($scope.cond);
		};
		// ページ変更時にコールされる関数を作成・設定
		$scope.pageChange = function() {
			entities.projectCommits.query($scope.cond, function(paginated :IPaginated<IProjectCommit>) {
				$scope.totalSize = paginated.totalSize;
				$scope.size = paginated.size;
				$scope.list = paginated.list;
				if (paginated.page > 1) paths.entryToQuery('page', paginated.page);
			}, modals.errorModal);
		};
		// 初期表示
		$scope.pageChange();
	}
    
    function endsWith(target: string, subseq :string) {
        const tl = target.length;
        const sl = subseq.length;
        const p = target.lastIndexOf(subseq);
        return tl === (sl + p);
    }
}

