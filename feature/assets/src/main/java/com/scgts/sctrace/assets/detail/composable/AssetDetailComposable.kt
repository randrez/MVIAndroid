package com.scgts.sctrace.assets.detail.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.scgts.sctrace.assets.confirmation.R.string.*
import com.scgts.sctrace.assets.detail.AssetDetailMvi.ViewState
import com.scgts.sctrace.base.model.AssetDetail
import com.scgts.sctrace.root.components.R
import theme.Gray
import theme.N900
import theme.SCGTSTheme
import theme.Slate

@Composable
fun AssetDetailComposable(
    viewState: LiveData<ViewState>,
    onBackClick: () -> Unit
) {
    viewState.observeAsState().value?.let { state ->
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Surface(
                color = Color.White,
                elevation = 4.dp
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .padding(
                            top = 24.dp,
                            start = 12.dp,
                            end = 12.dp,
                            bottom = 12.dp
                        )
                        .fillMaxWidth()
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .then(Modifier.size(24.dp)),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_chevron_left),
                            contentDescription = stringResource(id = R.string.back_button_icon_description),
                            modifier = Modifier
                                .size(24.dp)
                        )
                    }
                    Text(
                        text = stringResource(id = R.string.asset_details),
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(top = 25.dp)
                    )
                    Text(
                        text = state.assetDescription,
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 15.dp)
                    )
                }
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                items(state.assetDetailList) { assetDetail ->
                    RowAssetDetail(assetDetail.label, assetDetail.value)
                    Divider(
                        color = Gray,
                        thickness = 1.dp
                    )
                }
            }
        }
    }
}

@Composable
fun RowAssetDetail(idLabel: Int, value: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = stringResource(id = idLabel),
            style = MaterialTheme.typography.body1,
            color = Slate
        )
        Text(text = value, style = MaterialTheme.typography.body1, color = N900)
    }
}

@Preview
@Composable
private fun PreviewAssetDetail() {
    val viewState = MutableLiveData(
        ViewState(
            assetDescription = "9-5/8” 53.50# P110 R-3 NEW VAM SD SMLS",
            assetDetailList = listOf(
                AssetDetail(mill_work_no, "millWorkNumber"),
                AssetDetail(heat_no, "heatNumber"),
                AssetDetail(pipe_no, "pipeNumber")
            )
        )
    )
    SCGTSTheme {
        AssetDetailComposable(
            viewState = viewState,
            onBackClick = { }
        )
    }
}