package app.wowdrobe.com.rewards

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import app.wowdrobe.com.R
import app.wowdrobe.com.firebase.firestore.ProfileInfo
import app.wowdrobe.com.firebase.firestore.updateInfoToFirebase
import app.wowdrobe.com.location.LocationViewModel
import app.wowdrobe.com.ui.theme.appBackground
import app.wowdrobe.com.ui.theme.monteBold
import app.wowdrobe.com.ui.theme.monteNormal
import app.wowdrobe.com.ui.theme.monteSB
import app.wowdrobe.com.ui.theme.textColor
import app.wowdrobe.com.utils.AutoResizedText
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.jet.firestore.JetFirestore
import com.jet.firestore.getListOfObjects
import kotlinx.coroutines.delay

@Composable
fun RewardDetails(
    navController: NavHostController,
    email: String,
    name: String,
    viewModel: LocationViewModel
) {
    var profileList by remember {
        mutableStateOf<List<ProfileInfo>?>(null)
    }
    var userAddress by remember {
        mutableStateOf("")
    }
    var phoneNumber by remember {
        mutableStateOf("")
    }
    var gender by remember {
        mutableStateOf("")
    }
    var organization by remember {
        mutableStateOf("")
    }
    var pointsEarned by remember {
        mutableStateOf(0)
    }
    var pointsRedeemed by remember {
        mutableStateOf(0)
    }
    var noOfTimesReported by remember {
        mutableStateOf(0)
    }
    var noOfTimesCollected by remember {
        mutableStateOf(0)
    }
    var noOfTimesActivity by remember {
        mutableStateOf(0)
    }
    var communities by remember {
        mutableStateOf(mutableListOf(""))
    }
    JetFirestore(path = {
        collection("ProfileInfo")
    }, onRealtimeCollectionFetch = { value, _ ->
        profileList = value?.getListOfObjects()
    }) {
        if (profileList != null) {
            for (i in profileList!!) {
                if (i.email == email) {
                    userAddress = i.address ?: ""
                    gender = i.gender ?: ""
                    phoneNumber = i.phoneNumber ?: ""
                    organization = i.organization ?: ""
                    pointsEarned = i.pointsEarned
                    pointsRedeemed = i.pointsRedeemed
                    noOfTimesReported = i.noOfTimesReported
                    noOfTimesCollected = i.noOfTimesCollected
                    noOfTimesActivity = i.noOfTimesActivity
                    communities = i.communities.toMutableList()
                }
            }
        }


        var isCOinVisible by remember {
            mutableStateOf(false)
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(appBackground)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 35.dp,
                                bottom = 35.dp,
                                start = 10.dp,
                                end = 20.dp
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(top = 0.dp, start = 0.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBackIos,
                                contentDescription = "",
                                tint = textColor,
                                modifier = Modifier
                                    .padding(start = 10.dp, end = 7.dp)
                                    .size(25.dp)
                                    .clickable {
                                        navController.popBackStack()
                                    }
                            )
                            Column {
                                Text(
                                    text = "Rewards",
                                    color = textColor,
                                    fontSize = 25.sp,
                                    fontFamily = monteBold,
                                )
                                Text(
                                    text = "Grab exciting rewards",
                                    color = Color.LightGray,
                                    fontSize = 13.sp,
                                    fontFamily = monteBold,
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 15.dp, end = 0.dp, start = 20.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.padding(end = 25.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.coins),
                                    contentDescription = "coins",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(end = 5.dp),
                                    tint = Color.Unspecified
                                )
                                AutoResizedText(
                                    text = pointsEarned.toString(),
                                    color = textColor,
                                    fontSize = 15.sp,
                                    softWrap = true,
                                    fontFamily = monteNormal,
                                )
                            }

                        }
                    }
                    AsyncImage(
                        model = viewModel.rewardImage.value,
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = viewModel.rewardTitle.value,
                        color = textColor,
                        fontSize = 15.sp,
                        fontFamily = monteBold,
                        modifier = Modifier.padding(start = 20.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = viewModel.rewardDescription.value,
                        color = textColor,
                        fontSize = 15.sp,
                        fontFamily = monteNormal,
                        modifier = Modifier.padding(start = 20.dp)
                    )

                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Card(
                    modifier = Modifier.padding(0.dp),
                    backgroundColor = appBackground,
                    shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
                    elevation = 40.dp
                ) {
                    val context = LocalContext.current
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Points : ${viewModel.rewardNoOfPoints.value}",
                            color = textColor,
                            fontSize = 15.sp,
                            fontFamily = monteBold,
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 20.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    if (pointsEarned >= viewModel.rewardNoOfPoints.value) {
                                        isCOinVisible = true
                                        Toast.makeText(
                                            context,
                                            "Congratulations for claiming your reward",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        updateInfoToFirebase(
                                            context,
                                            name = name,
                                            email = email,
                                            phoneNumber = phoneNumber,
                                            gender = gender,
                                            organization = organization,
                                            address = userAddress,
                                            pointsEarned = pointsEarned - viewModel.rewardNoOfPoints.value,
                                            pointsRedeemed = pointsRedeemed + viewModel.rewardNoOfPoints.value,
                                            noOfTimesReported = noOfTimesReported,
                                            noOfTimesCollected = noOfTimesCollected + 1,
                                            noOfTimesActivity = noOfTimesActivity,
                                            communities = communities
                                        )

                                    } else {
                                        Toast.makeText(
                                            context,
                                            "You do not have enough points to claim this reward",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color(0xFFFFFFFF),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.padding(start = 10.dp)
                            ) {
                                Text(
                                    text = "Claim Now",
                                    color = Color.Black,
                                    fontSize = 12.sp,
                                    fontFamily = monteSB,
                                    modifier = Modifier.padding(bottom = 4.dp),
                                    maxLines = 1,
                                    softWrap = true
                                )
                            }
                        }
                    }
                }
            }
            if (isCOinVisible) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    val currenanim by rememberLottieComposition(
                        spec = LottieCompositionSpec.Asset("confetti.json")
                    )
                    LottieAnimation(
                        composition = currenanim,
                        iterations = 1,
                        contentScale = ContentScale.Crop,
                        speed = 1f,
                        modifier = Modifier
                            .fillMaxSize()
                            .size(250.dp)
                    )
                }
                LaunchedEffect(key1 = isCOinVisible) {
                    if (isCOinVisible) {
                        delay(4000)
                        navController.popBackStack()
                    }
                }

            }

        }
    }
}
