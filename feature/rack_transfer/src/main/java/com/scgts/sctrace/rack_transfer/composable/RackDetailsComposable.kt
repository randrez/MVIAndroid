package com.scgts.sctrace.rack_transfer.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgts.sctrace.base.model.AssetCardUiModel
import com.scgts.sctrace.base.model.RackTransferModel
import com.scgts.sctrace.rack_transfer.R
import com.scgts.sctrace.rack_transfer.rackdetails.RackDetailsMvi.ViewState
import com.scgts.sctrace.ui.components.assetsList.AssetsListHeader
import com.scgts.sctrace.ui.components.assetsList.AssetsListRow
import theme.N900
import theme.SCGTSTheme
import theme.Slate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RackDetailsComposable(
    viewState: LiveData<ViewState>,
    onBackClicked: () -> Unit,
    onAssetClicked: (String) -> Unit,
) {
    viewState.observeAsState().value?.let { state ->
        LazyColumn(modifier = Modifier
            .padding(top = 39.dp, start = 12.dp, end = 12.dp)
            .fillMaxWidth()) {
            stickyHeader {
                Box(Modifier
                    .background(Color.White)
                    .fillMaxWidth()) {
                    Column {
                        Image(
                            painter = painterResource(id = R.drawable.ic_chevron_left),
                            contentDescription = stringResource(R.string.back),
                            Modifier.clickable(enabled = true, onClick = { onBackClicked() })
                        )
                        Text(
                            text = stringResource(R.string.transfer_details),
                            modifier = Modifier.padding(top = 25.dp),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = Slate
                        )
                        Text(
                            text = state.assetsRackTransfer?.rackLocationName ?: "",
                            modifier = Modifier.padding(top = 15.dp),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            color = N900
                        )
                    }
                }
            }
            item {
                Column {
                    Text(
                        text = stringResource(R.string.attributes),
                        modifier = Modifier.padding(top = 32.dp),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Slate
                    )
                    Divider(modifier = Modifier.padding(top = 24.dp))
                    Row(modifier = Modifier.padding(top = 5.dp)) {
                        Text(
                            text = stringResource(R.string.product),
                            modifier = Modifier.weight(1f),
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = Slate,
                            textAlign = TextAlign.Left
                        )
                        Text(
                            text = state.assetsRackTransfer?.productDescription ?: "",
                            modifier = Modifier.weight(1f),
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = N900,
                            textAlign = TextAlign.Right
                        )
                    }
                    Divider(modifier = Modifier.padding(top = 11.dp))
                    Row(modifier = Modifier.padding(top = 5.dp)) {
                        Text(
                            text = stringResource(R.string.mill_work_no),
                            modifier = Modifier.weight(1f),
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = Slate,
                            textAlign = TextAlign.Left
                        )
                        Text(
                            text = state.assetsRackTransfer?.millWorkNum ?: "",
                            modifier = Modifier.weight(1f),
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = N900,
                            textAlign = TextAlign.Right
                        )
                    }
                    Divider(modifier = Modifier.padding(top = 11.dp))
                    Text(
                        text = stringResource(R.string.asset_list),
                        modifier = Modifier.padding(top = 46.dp),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Slate
                    )
                    Row(modifier = Modifier.padding(top = 25.dp, bottom = 28.dp)) {
                        Text(
                            text = stringResource(R.string.tally),
                            modifier = Modifier.weight(.1f),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Slate,
                            textAlign = TextAlign.Left
                        )
                        Text(
                            text = state.assetsRackTransfer?.totalJointsAndLength ?: "",
                            modifier = Modifier.weight(.9f),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = N900,
                            textAlign = TextAlign.Left
                        )
                    }
                }
            }
            item {
                AssetsListHeader(showConsumptionStatus = false)
            }
            itemsIndexed(state.assets) { index, asset ->
                AssetsListRow(
                    index = state.assets.size - index,
                    asset = asset,
                    onAssetClicked = onAssetClicked,
                    modifier = Modifier.height(IntrinsicSize.Max),
                )
            }
        }
    }
}

@Preview
@Composable
fun preview() {
    val assets = listOf(
        AssetCardUiModel(
            id = "",
            name = "3.6 9.3 J55 EUE 8RD R-1 SMLS",
            heatNumber = "847563",
            pipeNumber = "102",
            numTags = 3,
            tally = 30.3,
        )
    )
    val viewState =
        MutableLiveData(
            ViewState(
                assetsRackTransfer = RackTransferModel(
                    productDescription = "Rack 1"
                )
            )
        )
    SCGTSTheme {
        RackDetailsComposable(
            viewState, onBackClicked = { }, onAssetClicked = {})
    }
}