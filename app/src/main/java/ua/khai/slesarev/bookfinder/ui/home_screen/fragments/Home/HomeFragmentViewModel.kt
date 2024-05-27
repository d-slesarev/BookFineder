package ua.khai.slesarev.bookfinder.ui.home_screen.fragments.Home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ua.khai.slesarev.bookfinder.data.local.database.BookFinderDatabase
import ua.khai.slesarev.bookfinder.data.models.local.books.AdventuresBook
import ua.khai.slesarev.bookfinder.data.models.local.books.FantasyBook
import ua.khai.slesarev.bookfinder.data.models.local.books.FictionBook
import ua.khai.slesarev.bookfinder.data.models.local.books.MysteryBook
import ua.khai.slesarev.bookfinder.data.models.local.books.RecentBook
import ua.khai.slesarev.bookfinder.data.models.local.books.RomanceBook
import ua.khai.slesarev.bookfinder.data.models.local.books.SelfDevelopmentBook
import ua.khai.slesarev.bookfinder.data.models.local.books.ThrillersBook
import ua.khai.slesarev.bookfinder.data.paging.mediators.AdventuresRemoteMediator
import ua.khai.slesarev.bookfinder.data.paging.mediators.FantasyRemoteMediator
import ua.khai.slesarev.bookfinder.data.paging.mediators.FictionRemoteMediator
import ua.khai.slesarev.bookfinder.data.paging.mediators.MysteryRemoteMediator
import ua.khai.slesarev.bookfinder.data.paging.mediators.RecentRemoteMediator
import ua.khai.slesarev.bookfinder.data.paging.mediators.RomanceRemoteMediator
import ua.khai.slesarev.bookfinder.data.paging.mediators.SelfDevelopmentRemoteMediator
import ua.khai.slesarev.bookfinder.data.paging.mediators.ThrillersRemoteMediator
import ua.khai.slesarev.bookfinder.data.remote.api.google_books.BooksService
import ua.khai.slesarev.bookfinder.data.repository.book.BookType
import ua.khai.slesarev.bookfinder.data.repository.book.BooksRepository
import ua.khai.slesarev.bookfinder.data.repository.book.ConfigShelves
import ua.khai.slesarev.bookfinder.data.util.MY_TAG
import ua.khai.slesarev.bookfinder.ui.home_screen.fragments.Home.recycler_adapter.bookadapters.AdventuresListAdapter
import ua.khai.slesarev.bookfinder.ui.home_screen.fragments.Home.recycler_adapter.bookadapters.FantasyListAdapter
import ua.khai.slesarev.bookfinder.ui.home_screen.fragments.Home.recycler_adapter.bookadapters.FictionListAdapter
import ua.khai.slesarev.bookfinder.ui.home_screen.fragments.Home.recycler_adapter.bookadapters.MysteryListAdapter
import ua.khai.slesarev.bookfinder.ui.home_screen.fragments.Home.recycler_adapter.bookadapters.RecentListAdapter
import ua.khai.slesarev.bookfinder.ui.home_screen.fragments.Home.recycler_adapter.bookadapters.RomanceListAdapter
import ua.khai.slesarev.bookfinder.ui.home_screen.fragments.Home.recycler_adapter.bookadapters.SelfDevelopmentListAdapter
import ua.khai.slesarev.bookfinder.ui.home_screen.fragments.Home.recycler_adapter.bookadapters.ThrillersListAdapter

class HomeFragmentViewModel(private val application: Application) : AndroidViewModel(application) {
    private val bookRepo: BooksRepository = BooksRepository()

    val configList: List<ConfigShelves>
    val shelfTitles: List<String>

    init {
        configList = bookRepo.configShelvesList
        shelfTitles = bookRepo.shelfTitlesList
        Log.e(MY_TAG, "shelfTitles: $shelfTitles")
    }

    private val service: BooksService = BooksService()
    private val database = BookFinderDatabase.getInstance()

    lateinit var RecentFlow: Flow<PagingData<RecentBook>>
    lateinit var AdventuresFlow: Flow<PagingData<AdventuresBook>>
    lateinit var ThrillersFlow: Flow<PagingData<ThrillersBook>>
    lateinit var MysteryFlow: Flow<PagingData<MysteryBook>>
    lateinit var FantasyFlow: Flow<PagingData<FantasyBook>>
    lateinit var RomanceFlow: Flow<PagingData<RomanceBook>>
    lateinit var SelfDevelopmentFlow: Flow<PagingData<SelfDevelopmentBook>>
    lateinit var FictionFlow: Flow<PagingData<FictionBook>>

    val RecentAdapter = RecentListAdapter()
    val AdventuresAdapter = AdventuresListAdapter()
    val ThrillersAdapter = ThrillersListAdapter()
    val MysteryAdapter = MysteryListAdapter()
    val FantasyAdapter = FantasyListAdapter()
    val RomanceAdapter = RomanceListAdapter()
    val SelfDevelopmentAdapter = SelfDevelopmentListAdapter()
    val FictionAdapter = FictionListAdapter()

    @OptIn(ExperimentalPagingApi::class)
    fun initContent() {
        val remoteMediatorResults = mutableListOf<Flow<RemoteMediator.MediatorResult>>()

        configList.forEach { config ->
            when (config.type) {
                BookType.RECENT -> {
                    Log.i(MY_TAG, "configList.forEach: RECENT")
                    try {
                        val (flow, remoteMediatorFlow) = createFlow(
                            { database.RecentBookDao().getAllBooks() as PagingSource<Int, RecentBook> },
                            RecentRemoteMediator(application, config.typeCallAPI, service, database)
                        )
                        RecentFlow = flow
                        remoteMediatorResults.add(remoteMediatorFlow)
                    } catch (e:Exception){
                        Log.e(MY_TAG, "initContent()-RECENT: ${e.message}")
                    }
                }
                BookType.ADVENTURES -> {
                    Log.i(MY_TAG, "configList.forEach: ADVENTURES")
                    try {
                        val (flow, remoteMediatorFlow) = createFlow(
                            { database.AdventuresBookDao().getAllBooks() as PagingSource<Int, AdventuresBook> },
                            AdventuresRemoteMediator(application, config.typeCallAPI, service, database)
                        )
                        AdventuresFlow = flow
                        remoteMediatorResults.add(remoteMediatorFlow)
                    } catch (e:Exception){
                        Log.e(MY_TAG, "initContent()-ADVENTURES: ${e.message}")
                    }
                }
                BookType.THRILLERS -> {
                    Log.i(MY_TAG, "configList.forEach: THRILLERS")
                    try {
                        val (flow, remoteMediatorFlow) = createFlow(
                            { database.ThrillersBookDao().getAllBooks() as PagingSource<Int, ThrillersBook> },
                            ThrillersRemoteMediator(application, config.typeCallAPI, service, database)
                        )
                        ThrillersFlow = flow
                        remoteMediatorResults.add(remoteMediatorFlow)
                    } catch (e:Exception){
                        Log.e(MY_TAG, "initContent()-THRILLERS: ${e.message}")
                    }
                }
                BookType.MYSTERY -> {
                    Log.i(MY_TAG, "configList.forEach: MYSTERY")
                    try {
                        val (flow, remoteMediatorFlow) = createFlow(
                            { database.MysteryBookDao().getAllBooks() as PagingSource<Int, MysteryBook> },
                            MysteryRemoteMediator(application, config.typeCallAPI, service, database)
                        )
                        MysteryFlow = flow
                        remoteMediatorResults.add(remoteMediatorFlow)
                    }  catch (e:Exception){
                        Log.e(MY_TAG, "initContent()-MYSTERY: ${e.message}")
                    }
                }
                BookType.FANTASY -> {
                    Log.i(MY_TAG, "configList.forEach: FANTASY")
                    try {
                        val (flow, remoteMediatorFlow) = createFlow(
                            { database.FantasyBookDao().getAllBooks() as PagingSource<Int, FantasyBook> },
                            FantasyRemoteMediator(application, config.typeCallAPI, service, database)
                        )
                        FantasyFlow = flow
                        remoteMediatorResults.add(remoteMediatorFlow)
                    } catch (e:Exception){
                        Log.e(MY_TAG, "initContent()-FANTASY: ${e.message}")
                    }
                }
                BookType.ROMANCE -> {
                    Log.i(MY_TAG, "configList.forEach: ROMANCE")
                    try {
                        val (flow, remoteMediatorFlow) = createFlow(
                            { database.RomanceBookDao().getAllBooks() as PagingSource<Int, RomanceBook> },
                            RomanceRemoteMediator(application, config.typeCallAPI, service, database)
                        )
                        RomanceFlow = flow
                        remoteMediatorResults.add(remoteMediatorFlow)
                    } catch (e:Exception){
                        Log.e(MY_TAG, "initContent()-ROMANCE: ${e.message}")
                    }
                }
                BookType.SELF_DEVELOPMENT -> {
                    Log.i(MY_TAG, "configList.forEach: SELF_DEVELOPMENT")
                    try {
                        val (flow, remoteMediatorFlow) = createFlow(
                            { database.SelfDevelopmentBookDao().getAllBooks() as PagingSource<Int, SelfDevelopmentBook> },
                            SelfDevelopmentRemoteMediator(application, config.typeCallAPI, service, database)
                        )
                        SelfDevelopmentFlow = flow
                        remoteMediatorResults.add(remoteMediatorFlow)
                    } catch (e:Exception){
                        Log.e(MY_TAG, "initContent()-SELF_DEVELOPMENT: ${e.message}")
                    }
                }
                BookType.FICTION -> {
                    Log.i(MY_TAG, "configList.forEach: FICTION")
                    try {
                        val (flow, remoteMediatorFlow) = createFlow(
                            pagingSourceFactory = { database.FictionBookDao().getAllBooks() as PagingSource<Int, FictionBook> },
                            remoteMediator = FictionRemoteMediator(application, config.typeCallAPI, service, database)
                        )
                        FictionFlow = flow
                        remoteMediatorResults.add(remoteMediatorFlow)
                    } catch (e:Exception){
                        Log.e(MY_TAG, "initContent()-FICTION: ${e.message}")
                    }
                }
            }
        }

//        _uiState.value = "Success!"
//
//        combine(remoteMediatorResults) { results ->
//            results.count { it is RemoteMediator.MediatorResult.Success }
//        }.onEach { successCount ->
//            _loadingStates.value = successCount
//            Log.e(MY_TAG, "combine(): called!")
//        }.launchIn(viewModelScope)
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun <T : Any> createFlow(
        pagingSourceFactory: () -> PagingSource<Int, T>,
        remoteMediator: RemoteMediator<Int, T>
    ): Pair<Flow<PagingData<T>>, Flow<RemoteMediator.MediatorResult>> {
        Log.e(MY_TAG, "createFlow(): called!")
        val remoteMediatorFlow = MutableSharedFlow<RemoteMediator.MediatorResult>()

        val mediator = object : RemoteMediator<Int, T>() {
            override suspend fun load(
                loadType: LoadType,
                state: PagingState<Int, T>
            ): MediatorResult {
                val result = remoteMediator.load(loadType, state)
                remoteMediatorFlow.emit(result)
                return result
            }
        }

        val pagingFlow = Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = true,
                prefetchDistance = 10,
                initialLoadSize = 40
            ),
            remoteMediator = mediator,
            pagingSourceFactory = pagingSourceFactory
        ).flow.cachedIn(viewModelScope)

        return Pair(pagingFlow, remoteMediatorFlow)
    }
}