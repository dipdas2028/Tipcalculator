package com.example.tiptime

import android.icu.text.NumberFormat
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tiptime.ui.theme.TipTimeTheme
import kotlin.math.ceil

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TipTimeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TipTimeLayout()
                }
            }
        }
    }
}

@Composable
fun TipTimeLayout() {
    var amountInput by rememberSaveable { mutableStateOf("") }
    var tipInput by rememberSaveable { mutableStateOf("") }
    var peopleInput by rememberSaveable { mutableStateOf("1") }
    var roundUp by rememberSaveable { mutableStateOf(false) }

    val amount = amountInput.toDoubleOrNull() ?: 0.0
    val tipPercent = tipInput.toDoubleOrNull() ?: 0.0
    val peopleCount = (peopleInput.toIntOrNull() ?: 1).coerceAtLeast(1)

    val tip = calculateTip(amount, tipPercent, roundUp)
    val total = amount + tip
    val perPerson = total / peopleCount

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 40.dp)
            .verticalScroll(rememberScrollState())
            .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
            Row(modifier = Modifier.padding(9.dp)) {
                Text(
                    text = stringResource(R.string.calculate_tip),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(bottom = 6.dp, top = 10.dp),
                    )
                Spacer(modifier = Modifier.height(10.dp))

    }
Spacer(modifier = Modifier.height(80.dp))
        EditNumberField(
            label = R.string.bill_amount,
            value = amountInput,
            onValueChanged = { amountInput = it },
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(10, 15, 20).forEach { percent ->
                ExtendedFloatingActionButton(onClick = { tipInput = percent.toString() }) {
                    Text("$percent%")
                }
            }
        }

        EditNumberField(
            label = R.string.how_was_the_service,
            value = tipInput,
            onValueChanged = { tipInput = it },
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth()
        )

        RoundTheTipRow(
            roundUp = roundUp,
            onRoundUpChanged = { roundUp = it },
            modifier = Modifier.padding(bottom = 16.dp)
        )

        EditNumberField(
            label = R.string.people_count,
            value = peopleInput,
            onValueChanged = { peopleInput = it },
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
        )

        Text(
            text = stringResource(R.string.tip_amount, formatCurrency(tip)),
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = stringResource(R.string.total_amount, formatCurrency(total)),
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = stringResource(R.string.per_person_amount, formatCurrency(perPerson)),
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun EditNumberField(
    @StringRes label: Int,
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChanged,
        label = { Text(stringResource(label)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        modifier = modifier
    )
}

@Composable
fun RoundTheTipRow(
    roundUp: Boolean,
    onRoundUpChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.round_up_tip),
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = roundUp,
            onCheckedChange = onRoundUpChanged
        )
    }
}

private fun calculateTip(amount: Double, tipPercent: Double, roundUp: Boolean): Double {
    var tip = tipPercent / 100 * amount
    if (roundUp) {
        tip = ceil(tip)
    }
    return tip
}

private fun formatCurrency(amount: Double): String {
    return NumberFormat.getCurrencyInstance().format(amount)
}

@Preview(showBackground = true)
@Composable
fun TipTimeLayoutPreview() {
    TipTimeTheme {
        TipTimeLayout()
    }
}
