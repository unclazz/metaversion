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
    
    export function projectsProjectIdControllerFn($log :ng.ILogService,
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
	
    interface IProjectsProjectIdCommitsLinkScope extends IParentScope {
        project :IProjectStats;
        list :IProjectCommit[];
        open :boolean;
        cond :any;
        projectOrPathNames :(subseq :string) => ng.IPromise<ng.resource.IResourceArray<string>>;
        submit :() => void;
        click :($event :Event) => void;
        pageChange :() => void;
    }
	
	// プロジェクトコミット紐付け画面のためのコントローラ
	export function projectsProjectIdCommitsLinkControllerFn($log :ng.ILogService, 
			$scope :IProjectsProjectIdCommitsLinkScope, 
			entities :IEntityService, paths :IPathService, modals :IModalService) {
		
		// パスからID情報を取得
		var ids = paths.pathToIds();
		// プロジェクト情報を取得
		$scope.project = entities.projectStats.get({id: ids.projectId}, angular.noop, modals.errorModal);
		// クエリ文字列をもとに検索条件を初期化
		$scope.cond = angular.extend(paths.queryToObject({page: 1, pathbase: 0, like: ''}), ids);
		$scope.cond.unlinked = true;
		// 検索条件欄はデフォルトでは閉じておくがキーワード条件が存在する場合は開いておく
		$scope.open = $scope.cond.like !== undefined && $scope.cond.like.length > 0;
		// サジェスト用の関数を作成・設定
		$scope.projectOrPathNames = function (partialName :string) {
			if (partialName.length < 3) return;
			// 変更パス・ベースの検索かどうかをチェック
			if ($scope.cond.pathbase) {
				// 変更パス・ベースの検索の場合
				// APIを通じて変更パス名を取得して返す
				return entities.pathNames.query({like: partialName},
						angular.noop, modals.errorModal).$promise;
			} else {
				// そうでない場合
				return null;
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
		$scope.click = function($event :Event) {
			var commitId = angular.element($event.target).attr('data-commit-id');
			entities.projectCommits.save(
				{commitId: commitId, projectId: ids.projectId},
				$scope.pageChange, modals.errorModal);
		};
		// 初期表示
		$scope.pageChange();
	}
	
    interface IProjectsProjectIdCommitsCommitIdDeleteScope extends IParentScope {
        project :IProjectStats;
        commit :IProjectCommit;
        submit :() => void;
    }
	
	// プロジェクトコミット紐付け解除画面のためのコントローラ
	export function projectsProjectIdCommitsCommitIdDeleteControllerFn($log :ng.ILogService, 
			$scope :IProjectsProjectIdCommitsCommitIdDeleteScope, 
			entities :IEntityService, paths :IPathService, modals :IModalService) {
		var ids = paths.pathToIds();
		$scope.project = entities.projectStats.get({id: ids.projectId},
				angular.noop, modals.errorModal);
		$scope.commit = entities.projectCommits.get(ids, angular.noop, modals.errorModal);

		$scope.submit = function() {
			var p = entities.projectCommits.remove(ids, function (data :any) {
				paths.stringToPath('projects/' + ids.projectId + '/commits');
			}, modals.errorModal);
		};
	}
    
    interface IProjectsProjectIdChangedpathsScope extends IParentScope {
        project :IProjectStats;
		list: IProjectChangedPath[];
		cond: any;
        commit :IProjectCommit;
        csvDowloadUrl :() => string;
        pageChange :() => void;
    }
	
	// プロジェクト変更パス一覧画面のためのコントローラ
	export function projectsProjectIdChangedpathsControllerFn ($log :ng.ILogService,
			$scope :IProjectsProjectIdChangedpathsScope, 
			$location :ng.ILocationService, entities :IEntityService,
			paths :IPathService, modals :IModalService) {
				
		var ids = paths.pathToIds();
		$scope.project = entities.projectStats.get({id: ids.projectId},
				angular.noop, modals.errorModal);
		// クエリ文字列をもとに検索条件を初期化
		$scope.cond = angular.extend(paths.queryToObject({page: 1}), ids);
		// ページ変更時にコールされる関数を作成・設定
		$scope.pageChange = function() {
			entities.projectChangedPaths.query($scope.cond, 
			function(paginated :IPaginated<IProjectChangedPath>) {
				$scope.totalSize = paginated.totalSize;
				$scope.size = paginated.size;
				$scope.list = paginated.list;
				if (paginated.page > 1) paths.entryToQuery('page', paginated.page);
			}, modals.errorModal);
		};
		// 初期表示
		$scope.pageChange();
		$scope.csvDowloadUrl = function () {
			var nativePath = window.location.pathname.replace(/index$/, '');
			var ngPath = $location.path();
			return nativePath + 'csv' + ngPath;
		};
	}
	
    interface IProjectsProjectIdParallelsScope extends IParentScope {
        project :IProjectStats;
		list: IProjectParallel[];
		cond: any;
        csvDowloadUrl :() => string;
        pageChange :() => void;
    }
	
	export function projectsProjectIdParallelsControllerFn ($log :ng.ILogService, 
			$scope :IProjectsProjectIdParallelsScope, $location :ng.ILocationService, 
			entities :IEntityService, paths :IPathService, modals :IModalService) {
				
		var ids = paths.pathToIds();
		$scope.project = entities.projectStats.get({id: ids.projectId},
				angular.noop, modals.errorModal);
		// クエリ文字列をもとに検索条件を初期化
		$scope.cond = angular.extend(paths.queryToObject({page: 1}), ids);
		// ページ変更時にコールされる関数を作成・設定
		$scope.pageChange = function() {
			entities.projectParallels.query($scope.cond, 
			function(paginated :IPaginated<IProjectParallel>) {
				$scope.totalSize = paginated.totalSize;
				$scope.size = paginated.size;
				markRepeatedItems(paginated.list);
				$scope.list = paginated.list;
				if (paginated.page > 1) paths.entryToQuery('page', paginated.page);
			}, modals.errorModal);
		};
		// 初期表示
		$scope.pageChange();
		$scope.csvDowloadUrl = function () {
			var nativePath = window.location.pathname.replace(/index$/, '');
			var ngPath = $location.path();
			return nativePath + 'csv' + ngPath;
		};
		var markRepeatedItems = function name(list :IProjectParallel[]) {
			var previous :any = {}
			var repeated = function(name :string, value :string) {
				var prevValue = previous[name];
				previous[name] = value;
				return (prevValue !== undefined && prevValue == value); 
			};
			for (var i = 0; i < list.length; i ++) {
				var item :any = list[i];
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

    interface IRepositoriesScope extends IParentScope {
		list: IRepository[];
		cond: any;
        csvDowloadUrl :() => string;
        pageChange :() => void;
    }
	
	// リポジトリ一覧画面のためのコントローラ
	export function repositoriesControllerFn ($log :ng.ILogService,
			$location :ng.ILocationService, $scope :IRepositoriesScope, 
			entities :IEntityService, paths :IPathService, modals :IModalService) {
		// クエリ文字列をもとに検索条件を初期化
		$scope.cond = paths.queryToObject({page: 1});
		// ページ変更時にコールされる関数を作成・設定
		$scope.pageChange = function() {
			// APIを介してリポジトリ一覧を取得
			entities.repositories.query($scope.cond, 
			function(paginated :IPaginated<IRepository>) {
				$scope.totalSize = paginated.totalSize;
				$scope.size = paginated.size;
				$scope.list = paginated.list;
				if (paginated.page > 1) paths.entryToQuery('page', paginated.page);
			}, modals.errorModal);
		};
		// 初期表示
		$scope.pageChange();
	}
	
    interface IRepositoriesRepositoryIdScope extends IParentScope {
		repository: IRepository;
    }
	
	// リポジトリ詳細画面のためのコントローラ
	export function repositoriesRepositoryIdControllerFn ($log :ng.ILogService,
			$location :ng.ILocationService, $scope :IRepositoriesRepositoryIdScope, 
			entities :IEntityService, paths :IPathService, modals :IModalService) {
				
		// パスからリポジトリIDを読み取る
		var ids = paths.pathToIds();
		// APIを介してリポジトリ情報を取得
		$scope.repository = entities.repositories.get({id: ids.repositoryId},
				angular.noop, modals.errorModal);
	}
	
    interface IRepositoriesRepositoryIdCommitsScope extends IParentScope {
		repository: IRepository;
		cond :any;
		list : IRepositoryCommitStats[];
		pageChange : () => void;
    }
	
	// リポジトリコミット一覧画面のためのコントローラ
	export function repositoriesRepositoryIdCommitsControllerFn ($log :ng.ILogService,
			$location :ng.ILocationService, $scope :IRepositoriesRepositoryIdCommitsScope, 
			entities :IEntityService, paths :IPathService, modals :IModalService) {
				
		// パスからリポジトリIDを読み取る
		var ids = paths.pathToIds();
		// APIを介してリポジトリ情報を取得
		$scope.repository = entities.repositories.get({id: ids.repositoryId},
				angular.noop, modals.errorModal);
		// クエリ文字列をもとに検索条件を初期化
		$scope.cond = angular.extend(paths.queryToObject({page: 1}), ids);

		// ページ変更時にコールされる関数を作成・設定
		$scope.pageChange = function() {
			entities.repositoryCommitStats.query($scope.cond,
			function(paginated :IPaginated<IRepositoryCommitStats>) {
				$scope.list = paginated.list;
				$scope.totalSize = paginated.totalSize;
				$scope.size = paginated.size;
				if (paginated.page > 1) paths.entryToQuery('page', paginated.page);
			}, modals.errorModal);
		};
		
		// 初期表示
		$scope.pageChange();
	}
	
    interface IRepositoriesRepositoryIdEditScope extends IParentScope {
		repository: IRepository;
		cond :any;
		list : IRepositoryCommitStats[];
		submit : () => void;
    }
	
	// リポジトリ編集画面のためのコントローラ
	export function repositoriesRepositoryIdEditControllerFn ($log :ng.ILogService,
			$location :ng.ILocationService, $scope :IRepositoriesRepositoryIdEditScope, 
			entities :IEntityService, paths :IPathService, modals :IModalService) {
		// パスからIDを読み取る
		var ids = paths.pathToIds();
		if (ids.repositoryId !== undefined) {
			$scope.repository = entities.repositories.get({id: ids.repositoryId},
					angular.noop, modals.errorModal);
		} else {
			$scope.repository = new entities.repositories({
				id: undefined,
				baseUrl: 'http://www.example.com/svn',
				trunkPathPattern: '/trunk',
				branchPathPattern: '/branches/\\\w+',
				username: '',
				password: ''
			});
		}
		
		$scope.submit = function() {
			var waitingModal = modals.waitingModal('リポジトリの登録・更新処理を実行中です。');
			function successFn (data :IRepository) {
				// 作成/更新が成功したらリポジトリ詳細に遷移
				paths.stringToPath('repositories/' + data.id);
				// モーダルはクローズ
				waitingModal.close({});
			}
			function errorFn (error :any) {
				// 作成/更新が失敗したらエラー情報を表示
				modals.errorModal(error).result.then(angular.noop,function() {
					waitingModal.close();
				});
			}
			
			if (ids.repositoryId !== undefined) {
				entities.repositories.resave($scope.repository, successFn, errorFn);
			} else {
				entities.repositories.save($scope.repository, successFn, errorFn);
			}
		};
	}
	
    interface IRepositoriesRepositoryIdDeleteScope extends IParentScope {
		repository: IRepository;
		submit : () => void;
    }
	
	// リポジトリ削除画面のためのコントローラ
	export function repositoriesRepositoryIdDeleteControllerFn ($log :ng.ILogService,
			$location :ng.ILocationService, $scope :IRepositoriesRepositoryIdEditScope, 
			entities :IEntityService, paths :IPathService, modals :IModalService) {
		
		// パスからIDを読み取る
		var ids = paths.pathToIds();
		$scope.repository = entities.repositories.get({id: paths.pathToIds().repositoryId},
				angular.noop, modals.errorModal);
		
		$scope.submit = function() {
			entities.repositories.remove(function (data :IRepository) {
				paths.stringToPath('repositories');
			}, modals.errorModal);
		};
	}
	
    interface IRepositoriesRepositoryIdCommitsCommitIdScope extends IParentScope {
		commit: IRepositoryCommitStats;
		projectList : IRepositoryCommitProject[];
    }
	
	// コミット詳細画面のためのコントローラ
	export function repositoriesRepositoryIdCommitsCommitIdControllerFn ($log :ng.ILogService,
			$location :ng.ILocationService, $scope :IRepositoriesRepositoryIdCommitsCommitIdScope, 
			entities :IEntityService, paths :IPathService, modals :IModalService) {
				
		// パスからリポジトリIDやコミットIDを読み取る
		var ids = paths.pathToIds();
		// APIを介してコミット情報を取得
		$scope.commit = entities.repositoryCommitStats.get(ids,
				angular.noop, modals.errorModal);
		// APIを介してコミットの関連プロジェクトを取得
		$scope.projectList = entities.repositoryCommitProjects.query(ids,
				angular.noop, modals.errorModal);
	}
	
    interface IRepositoriesRepositoryIdCommitsCommitIdLinkScope extends IParentScope {
		commit: IRepositoryCommitStats;
		open: boolean;
		cond: any;
		projectOrPathNames: (s:string) => ng.IPromise<ng.resource.IResourceArray<string>>;
		projectList : IRepositoryCommitProject[];
		list : IProject[];
		submit: () => void;
		click: () => void;
		pageChange: () => void;
    }
	
	export function repositoriesRepositoryIdCommitsCommitIdLinkControllerFn ($log :ng.ILogService,
			$location :ng.ILocationService, $scope :IRepositoriesRepositoryIdCommitsCommitIdLinkScope, 
			entities :IEntityService, paths :IPathService, modals :IModalService) {
				
		// パスからリポジトリIDやコミットIDを読み取る
		var ids = paths.pathToIds();
		// APIを介してコミット情報を取得
		$scope.commit = entities.repositoryCommits.get(ids,
				angular.noop, modals.errorModal);
		$scope.open = false;
		// クエリ文字列をもとに検索条件を初期化
		$scope.cond = paths.queryToObject({
			page: 1,
			pathbase: 0,
			like: '',
			unlinkedCommitId: ids.commitId
		});
		// 検索条件欄はデフォルトでは閉じておくがキーワード条件が存在する場合は開いておく
		$scope.open = $scope.cond.like !== undefined && $scope.cond.like.length > 0;
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
			entities.projects.query($scope.cond, 
			function(paginated :IPaginated<IProject>) {
				// 取得に成功したら結果を画面に反映させる
				$scope.totalSize = paginated.totalSize;
				$scope.size = paginated.size;
				appendSelectedStatus(paginated.list);
				$scope.list = paginated.list;
				if (paginated.page > 1) paths.entryToQuery('page', paginated.page);
			}, modals.errorModal);
		};
		// 初期表示
		$scope.pageChange();
		
		$scope.click = function() {
			for (var i = 0; i < $scope.list.length; i ++) {
				var item :any = $scope.list[i];
				if (!item.selected) continue;
				var link = new entities.projectCommits({commitId: ids.commitId, projectId: item.id});
				entities.projectCommits.save(link, $scope.pageChange, modals.errorModal);
			}
		};
		
		var appendSelectedStatus = function (list :any) {
			for (var i = 0; i < list.length; i ++) {
				list[i].selected = false;
			}
		}
	}
	
    interface IRepositoriesRepositoryIdCommitsCommitIdChangedpathsScope extends IParentScope {
		commit: IRepositoryCommitStats;
		projectList : IRepositoryCommitProject[];
		cond: any;
		projectOrPathNames: (s:string) => ng.IPromise<ng.resource.IResourceArray<string>>;
		list : IRepositoryCommitChangedPath[];
		pageChange: () => void;
    }
	
	export function repositoriesRepositoryIdCommitsCommitIdChangedpathsControllerFn ($log :ng.ILogService,
			$location :ng.ILocationService, $scope :IRepositoriesRepositoryIdCommitsCommitIdChangedpathsScope, 
			entities :IEntityService, paths :IPathService, modals :IModalService) {
				
		// パスからリポジトリIDやコミットIDを読み取る
		var ids = paths.pathToIds();
		// APIを介してコミット情報を取得
		$scope.commit = entities.repositoryCommits.get(ids,
				angular.noop, modals.errorModal);
		// APIを介してコミットの関連プロジェクトを取得
		$scope.projectList = entities.repositoryCommitProjects.query(ids,
				angular.noop, modals.errorModal);
		// クエリ文字列をもとに検索条件を初期化
		$scope.cond = angular.extend(paths.queryToObject({page: 1}), ids);
		// ページ変更時にコールされる関数を作成・設定
		$scope.pageChange = function() {
			entities.repositoryCommitChangedPaths.query($scope.cond, 
			function(paginated :IPaginated<IRepositoryCommitChangedPath>) {
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
	
    interface IUsersScope extends IParentScope {
		cond: any;
		list: IUser[];
		pageChange: () => void;
    }
	
	// ユーザ一覧画面のためのコントローラ
	export function usersControllerFn ($log :ng.ILogService,
			$location :ng.ILocationService, $scope :IUsersScope, 
			entities :IEntityService, paths :IPathService, modals :IModalService) {
		// クエリ文字列をもとに検索条件を初期化
		$scope.cond = paths.queryToObject({page: 1});
		// ページ変更時にコールされる関数を作成・設定
		$scope.pageChange = function() {
			entities.users.query($scope.cond, 
			function(paginated :IPaginated<IUser>) {
				$scope.totalSize = paginated.totalSize;
				$scope.size = paginated.size;
				$scope.list = paginated.list;
				if (paginated.page > 1) paths.entryToQuery('page', paginated.page);
			}, modals.errorModal);
		};
		// 初期表示
		$scope.pageChange();
	}
	
    interface IUsersUserIdEditScope extends IParentScope {
		user: IUser;
		submit: () => void;
    }
	
	// ユーザ編集画面のためのコントローラ
	export function usersUserIdEditControllerFn ($log :ng.ILogService,
			$location :ng.ILocationService, $scope :IUsersUserIdEditScope, 
			entities :IEntityService, paths :IPathService, modals :IModalService) {
		// パスからIDを読み取る
		var ids = paths.pathToIds();
		if (ids.userId !== undefined) {
			$scope.user = entities.users.get({id: paths.pathToIds().userId},
					angular.noop, modals.errorModal);
			$scope.user.password = null;
		} else {
			$scope.user = new entities.users({id: undefined});
		}
		
		$scope.submit = function() {
			function successFn(data: any) {
				paths.stringToPath('users');
			}
			if (ids.userId !== undefined) {
				entities.users.resave($scope.user, successFn, modals.errorModal);
			} else {
				entities.users.save($scope.user, successFn, modals.errorModal);
			}
		};
	}
	
	// ユーザ削除画面のためのコントローラ
	export function usersUserIdDeleteControllerFn ($log :ng.ILogService,
			$location :ng.ILocationService, $scope :IUsersUserIdEditScope, 
			entities :IEntityService, paths :IPathService, modals :IModalService) {
				
		// パスからIDを読み取る
		var ids = paths.pathToIds();
		$scope.user = entities.users.get({id: paths.pathToIds().userId},
				angular.noop, modals.errorModal);
		
		$scope.submit = function() {
			entities.users.remove(function (data :any) {
				paths.stringToPath('users');
			}, modals.errorModal);
		};
	}
	
	
    function endsWith(target: string, subseq :string) {
        const tl = target.length;
        const sl = subseq.length;
        const p = target.lastIndexOf(subseq);
        return tl === (sl + p);
    }
}