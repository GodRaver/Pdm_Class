package pt.ipca.projetopdm.UserInterface.news.presentation.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CountryTabRow(


    pagerState: PagerState,
    countries: List<String>,
    onTabSelected: (Int) -> Unit,

    ) {

    ScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
        edgePadding = 0.dp,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {

        countries.forEachIndexed {index, category ->

            Tab(selected = pagerState.currentPage == index,
                onClick = {
                    onTabSelected(index) },
                content = {

                    Text(
                        text = category,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

            )
        }
    }
}