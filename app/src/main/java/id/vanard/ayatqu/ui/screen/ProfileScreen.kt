package id.vanard.ayatqu.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import id.vanard.ayatqu.ui.icons.Bell
import id.vanard.ayatqu.ui.icons.CaretRight
import id.vanard.ayatqu.ui.icons.Globe
import id.vanard.ayatqu.ui.icons.Info
import id.vanard.ayatqu.ui.icons.Moon
import id.vanard.ayatqu.ui.icons.SignOut
import id.vanard.ayatqu.ui.icons.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.vanard.ayatqu.core.ui.theme.AyatQuTheme
import id.vanard.ayatqu.viewmodel.AuthViewModel
import org.koin.androidx.compose.koinViewModel

// ── Design tokens ─────────────────────────────────────────────────────────────
private val ProfileAvatarBg      = Color(0xFF2D6B8C)
private val ProfileAvatarText    = Color(0xFFFFFFFF)
private val SectionLabelColor    = Color(0xFF8E8E93)
private val DividerColor         = Color(0xFFF0F0F0)
private val MenuIconTint         = Color(0xFF2D6B8C)
private val LogoutRed            = Color(0xFFE53E3E)

// ── Main composable ───────────────────────────────────────────────────────────

@Composable
fun ProfileScreen(
    viewModel: AuthViewModel = koinViewModel(),
    onLogout: () -> Unit = {},
) {
    val authState by viewModel.uiState.collectAsState()
    val firebaseUser = authState.user
    val displayName = firebaseUser?.displayName
        ?: firebaseUser?.email?.substringBefore("@")
        ?: "User"
    val email = firebaseUser?.email ?: ""

    ProfileScreenContent(
        displayName = displayName,
        email = email,
        onLogout = {
            viewModel.signOut()
            onLogout()
        },
    )
}

@Composable
internal fun ProfileScreenContent(
    displayName: String,
    email: String,
    onLogout: () -> Unit = {},
) {
    var notificationsEnabled by remember { mutableStateOf(false) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // ── Profile header ────────────────────────────────────────────────────
        ProfileHeader(displayName, email)

        Spacer(Modifier.height(24.dp))

        // ── Settings section ──────────────────────────────────────────────────
        SectionLabel("Settings")

        MenuToggleItem(
            icon = Bell,
            title = "Notifications",
            subtitle = "Daily reminders & alerts",
            checked = notificationsEnabled,
            onCheckedChange = { notificationsEnabled = it }
        )

        MenuDivider()

        MenuToggleItem(
            icon = Moon,
            title = "Theme",
            subtitle = if (darkModeEnabled) "Dark mode" else "Light mode",
            checked = darkModeEnabled,
            onCheckedChange = { darkModeEnabled = it }
        )

        MenuDivider()

        MenuNavigationItem(
            icon = Globe,
            title = "Language",
            subtitle = "English",
            onClick = { /* TODO */ }
        )

        Spacer(Modifier.height(24.dp))

        // ── Other section ─────────────────────────────────────────────────────
        SectionLabel("Other")

        MenuNavigationItem(
            icon = Star,
            title = "Rate App",
            subtitle = "Enjoying? Leave a review",
            onClick = { /* TODO */ }
        )

        MenuDivider()

        MenuNavigationItem(
            icon = Info,
            title = "About",
            subtitle = "Version 1.0.0",
            onClick = { /* TODO */ }
        )

        MenuDivider()

        MenuActionItem(
            icon = SignOut,
            title = "Logout",
            tint = LogoutRed,
            onClick = { showLogoutDialog = true },
        )

        Spacer(Modifier.height(32.dp))
    }

    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = {
                showLogoutDialog = false
                onLogout()
            },
            onDismiss = { showLogoutDialog = false },
        )
    }
}

@Composable
private fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Logout?",
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        text = {
            Text(
                text = "Are you sure you want to logout? You'll need to sign in again to access your account.",
                style = MaterialTheme.typography.bodyLarge,
                color = SectionLabelColor,
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = LogoutRed,
                ),
            ) {
                Text(
                    text = "Logout",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

// ── Profile header ────────────────────────────────────────────────────────────

@Composable
private fun ProfileHeader(displayName: String, email: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Avatar circle with initial
        val initial = displayName.firstOrNull()?.uppercase() ?: "?"
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(ProfileAvatarBg),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = initial,
                color = ProfileAvatarText,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(Modifier.height(16.dp))

        // Display name
        Text(
            text = displayName,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(4.dp))

        // Email
        Text(
            text = email,
            style = MaterialTheme.typography.bodyLarge,
            color = SectionLabelColor,
            textAlign = TextAlign.Center,
        )
    }
}

// ── Section label ─────────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(label: String) {
    Text(
        text = label.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = SectionLabelColor,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
    )
}

// ── Menu items ────────────────────────────────────────────────────────────────

@Composable
private fun MenuToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MenuIconTint,
            modifier = Modifier.size(24.dp),
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = SectionLabelColor,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MenuIconTint,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFD1D5DB),
            ),
        )
    }
}

@Composable
private fun MenuNavigationItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MenuIconTint,
            modifier = Modifier.size(24.dp),
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = SectionLabelColor,
            )
        }
        Icon(
            imageVector = CaretRight,
            contentDescription = null,
            tint = SectionLabelColor,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun MenuActionItem(
    icon: ImageVector,
    title: String,
    tint: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = tint,
            modifier = Modifier.size(24.dp),
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = tint,
        )
    }
}

@Composable
private fun MenuDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 20.dp),
        color = DividerColor,
        thickness = 0.5.dp,
    )
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Light")
@Composable
private fun ProfileScreenPreviewLight() {
    AyatQuTheme(darkTheme = false) {
        ProfileScreenContent(
            displayName = "Abdullah",
            email = "abdullah@email.com",
        )
    }
}

@Preview(showBackground = true, name = "Dark")
@Composable
private fun ProfileScreenPreviewDark() {
    AyatQuTheme(darkTheme = true) {
        ProfileScreenContent(
            displayName = "Abdullah",
            email = "abdullah@email.com",
        )
    }
}

@Preview(showBackground = true, name = "Logout Dialog")
@Composable
private fun LogoutDialogPreview() {
    AyatQuTheme(darkTheme = false) {
        LogoutConfirmationDialog(
            onConfirm = {},
            onDismiss = {},
        )
    }
}
