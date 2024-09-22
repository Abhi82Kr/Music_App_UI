package com.example.musicappui.ui.theme

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.primarySurface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.musicappui.MainViewModel
import com.example.musicappui.R
import com.example.musicappui.Screen
import com.example.musicappui.ScreenBottom
import com.example.musicappui.screenDrawer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MainView() {

    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val scope: CoroutineScope = rememberCoroutineScope()

    val isSheetFullScreen by remember { mutableStateOf(false) }

    val modifier = if(isSheetFullScreen)Modifier.fillMaxSize()  else Modifier.fillMaxWidth()
    val dialogOpen = remember {
        mutableStateOf(false)
    }

    // Allow us to find out on which view we are
    val controller: NavController = rememberNavController()
    val navBackStackEntry by controller.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val viewModel: MainViewModel = viewModel()
    val currentScreen = remember {
        viewModel.currentScreen.value
    }

    val title = remember {
        mutableStateOf(currentScreen.title)
    }

    val modalSheetState= androidx.compose.material.rememberModalBottomSheetState(
        initialValue= ModalBottomSheetValue.Hidden,
        confirmValueChange = {it!= ModalBottomSheetValue.HalfExpanded}
    )

    val roundCornerRadius= if(isSheetFullScreen) 0.dp else 15.dp
    val bottomBar: @Composable () -> Unit = {

        if (currentScreen is Screen.DrawerScreen || currentScreen == Screen.BottomScreen.Home) {
            BottomNavigation(Modifier.wrapContentSize()) {
                ScreenBottom.forEach { item ->
                    val isSelected = currentRoute == item.Route
                    Log.d(
                        "Navigation",
                        "Item: ${item.bTitle}, Current Route : $currentRoute  is Selected"
                    )
                    val tint = if (isSelected) Color.White else Color.Black
                    BottomNavigationItem(
                        selected = currentRoute == item.bRoute,
                        onClick = { controller.navigate(item.bRoute)
                                  title.value=item.bTitle
                                  },

                        icon = {

                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = item.bTitle,
                                tint = tint
                            )
                        },
                        label = { Text(text = item.bTitle, color = tint) },
//                        selectedContentColor = Color.White,
//                        unselectedContentColor = Color.Gray
                    )
                }
            }
        }

    }


    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(topStart = roundCornerRadius, topEnd = roundCornerRadius),
        sheetContent = {
        MoreBottomSheet(modifier = modifier)
    }) {


        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(title.value) },
                    actions = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if(modalSheetState.isVisible) modalSheetState.hide()
                                    else modalSheetState.show()
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            // Handle drawer opening here
                            scope.launch {
                                scaffoldState.drawerState.open()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Account"
                            )
                        }
                    }
                )
            },
            scaffoldState = scaffoldState,
            drawerContent = {
                LazyColumn(Modifier.padding(15.dp)) {
                    items(screenDrawer) { item ->
                        DrawerItem(selected = currentRoute == item.dRoute, item = item) {
                            scope.launch {
                                scaffoldState.drawerState.close()
                            }
                            if (item.dRoute == "add_account") {
                                //Alert dialog
                                dialogOpen.value = true
                            } else {
                                controller.navigate(item.dRoute)
                                title.value = item.dTitle
                            }
                        }
                    }
                }
            },
            bottomBar = bottomBar

        ) { paddingValues ->
            Navigation(navController = controller, viewModel = viewModel, pd = paddingValues)
            AccountDialog(dialogOpen = dialogOpen)
        }
    }
}

@Composable
fun DrawerItem(
    selected:Boolean,
    item:Screen.DrawerScreen,
    onDrawerItemClicked :()-> Unit
){

    val background = if(selected) Color.Gray else Color.White
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 20.dp).background(background)
            .clickable {
                onDrawerItemClicked()
            }){
        Icon(
            painter = painterResource(id=item.icon),
            contentDescription = item.dTitle,
            Modifier.padding(end = 10.dp, top = 5.dp)

        )
        Text(
            text= item.dTitle,
            style = MaterialTheme.typography.h5
        )
    }
}


@Composable
fun Navigation(navController:NavController,viewModel: MainViewModel,pd:PaddingValues){


    NavHost(navController = navController as NavHostController,
        startDestination =Screen.DrawerScreen.Account.Route,Modifier.padding(pd) ){

        composable(Screen.DrawerScreen.Account.Route){
            AccountView()
        }
        composable(Screen.DrawerScreen.Subscription.Route){
            Subscription()
        }
        composable(Screen.BottomScreen.Home.bRoute){
            // Homescreen
            Home()
        }
        composable(Screen.BottomScreen.Browse.bRoute){
            //Browse
            Browse()
        }

        composable(Screen.BottomScreen.Library.bRoute){
            //
            library()
        }
    }
}

@Composable
fun MoreBottomSheet(modifier: Modifier){

    Box(
        Modifier.fillMaxWidth().height(300.dp).background(MaterialTheme.colors.primarySurface)
    ){
        Column (Modifier.padding(15.dp), verticalArrangement = Arrangement.SpaceBetween){
            Row(Modifier.padding(15.dp)){
                Icon(modifier = Modifier.padding(end=10.dp),
                    painter= painterResource(id= R.drawable.baseline_settings_24),
                    contentDescription = "Setting"
                )
                Text("Setting", fontSize = 20.sp, color = Color.White)

            }
            Row(Modifier.padding(15.dp)){
                Icon(modifier = Modifier.padding(end=10.dp),
                    painter= painterResource(id= R.drawable.baseline_share_24),
                    contentDescription = "Setting"
                )
                Text("Share", fontSize = 20.sp, color = Color.White)

            }
            Row(Modifier.padding(15.dp)){
                Icon(modifier = Modifier.padding(end=10.dp),
                    painter= painterResource(id= R.drawable.baseline_help_center_24),
                    contentDescription = "Setting"
                )
                Text("Help Center", fontSize = 20.sp, color = Color.White)

            }
            Row(Modifier.padding(15.dp)){
                Icon(modifier = Modifier.padding(end=10.dp),
                    painter= painterResource(id= R.drawable.baseline_star_24),
                    contentDescription = "Setting"
                )
                Text("Rate Us", fontSize = 20.sp, color = Color.White)

            }
        }
    }

}