package com.scgts.sctrace.feature.landing.composable.task_details.task_detail_sections

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.base.model.AssetProductInformationCardUiModel
import com.scgts.sctrace.feature.landing.R
import com.scgts.sctrace.ui.components.SCTraceProgressBar
import theme.Gray
import theme.SCGTSTheme

@Composable
fun AssetProductInformationList(
    list: List<AssetProductInformationCardUiModel>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        AssetProductInformationHeader()
        list.forEachIndexed { index, productInformation ->
            AssetProductInformationRow(
                index = index,
                assetProductInformation = productInformation
            )
        }
    }
}

@Composable
private fun AssetProductInformationHeader() {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Gray)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.pound_sign),
                style = MaterialTheme.typography.subtitle2,
                modifier = Modifier.padding(end = 24.dp)
            )
            Text(
                text = stringResource(R.string.product_description),
                style = MaterialTheme.typography.subtitle2,
            )
        }
        Divider()
    }
}

@Composable
private fun AssetProductInformationRow(
    index: Int,
    assetProductInformation: AssetProductInformationCardUiModel
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = (index + 1).toString(),
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(end = 24.dp)
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = assetProductInformation.productDescription,
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                assetProductInformation.contractNumber?.let { contractNumber ->
                    Text(
                        text = stringResource(R.string.contract_no_format, contractNumber),
                        style = MaterialTheme.typography.subtitle2
                    )
                }
                assetProductInformation.shipmentNumber?.let { shipmentNumber ->
                    Text(
                        text = stringResource(R.string.shipment_no_format, shipmentNumber),
                        style = MaterialTheme.typography.subtitle2
                    )
                }
                assetProductInformation.conditionName?.let { conditionName ->
                    Text(
                        text = stringResource(R.string.condition_format, conditionName),
                        style = MaterialTheme.typography.subtitle2
                    )
                }
                assetProductInformation.rackLocationName?.let { rackLocationName ->
                    Text(
                        text = stringResource(R.string.rack_location_format, rackLocationName),
                        style = MaterialTheme.typography.subtitle2
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                SCTraceProgressBar(
                    progress = assetProductInformation.percentCompletion,
                    modifier = Modifier
                        .height(8.dp)
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = assetProductInformation.totalTally,
                        style = MaterialTheme.typography.body2
                    )
                    Text(
                        text = assetProductInformation.expectedTally,
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        }
        Divider()
    }
}

@Preview
@Composable
private fun AssetProductInformationListPreview() {
    SCGTSTheme {
        Surface {
            AssetProductInformationList(
                list = listOf(
                    AssetProductInformationCardUiModel(
                        productDescription = "9-5/8” 53.50# P110 R-3 NEW VAM SF MED",
                        contractNumber = "AR-MSCI-101-7778",
                        shipmentNumber = "WYYK10908",
                        conditionName = "Prime",
                        rackLocationName = "561",
                        percentCompletion = 1f,
                        totalTally = "1 JT / 30.0 FT",
                        expectedTally = "1 JT / 30.0 FT"
                    ),
                    AssetProductInformationCardUiModel(
                        productDescription = "9-5/8” 53.50# P110 R-3 NEW VAM SF MED",
                        contractNumber = "AR-MSCI-101-7778",
                        shipmentNumber = "WYYK10908",
                        conditionName = "Prime",
                        rackLocationName = "562",
                        percentCompletion = 1f,
                        totalTally = "1 JT / 30.0 FT",
                        expectedTally = "1 JT / 30.0 FT"
                    ),
                    AssetProductInformationCardUiModel(
                        productDescription = "9-5/8” 53.50# P110 R-3 NEW VAM SF MED",
                        contractNumber = "AR-MSCI-101-7778",
                        shipmentNumber = "WYYK10908",
                        conditionName = "Prime",
                        rackLocationName = "563",
                        percentCompletion = 1f,
                        totalTally = "1 JT / 30.0 FT",
                        expectedTally = "1 JT / 30.0 FT"
                    ),
                    AssetProductInformationCardUiModel(
                        productDescription = "9-5/8” 53.50# P110 R-3 NEW VAM SF MED",
                        contractNumber = "AR-MSCI-101-7778",
                        shipmentNumber = "WYYK10908",
                        conditionName = "Prime",
                        rackLocationName = "564",
                        percentCompletion = 0f,
                        totalTally = "0 JT / 0.0 FT",
                        expectedTally = "1 JT / 30.0 FT"
                    )
                )
            )
        }
    }
}