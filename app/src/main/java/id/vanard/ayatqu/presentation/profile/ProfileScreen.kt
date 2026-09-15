package id.vanard.ayatqu.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import id.vanard.ayatqu.core.ui.icon.Bell
import id.vanard.ayatqu.core.ui.icon.CaretRight
import id.vanard.ayatqu.core.ui.icon.Globe
import id.vanard.ayatqu.core.ui.icon.Info
import id.vanard.ayatqu.core.ui.icon.SignOut
import id.vanard.ayatqu.core.ui.icon.Star
import id.vanard.ayatqu.core.ui.icon.Trash
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.stringResource
import id.vanard.ayatqu.R
import id.vanard.ayatqu.core.ui.theme.AyatQuTheme
import id.vanard.ayatqu.data.AdhanPreference
import id.vanard.ayatqu.data.LanguagePreference
import id.vanard.ayatqu.presentation.profile.contract.OnProfileEvent
import id.vanard.ayatqu.presentation.profile.contract.ProfileEvent
import id.vanard.ayatqu.presentation.profile.contract.ProfileState

// ── Main composable ───────────────────────────────────────────────────────────

@Composable
fun ProfileScreen(
    state: ProfileState,
    onEvent: OnProfileEvent,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // ── Profile header ────────────────────────────────────────────────────
        if (state.isLoggedIn) {
            ProfileHeader(state.displayName, state.email)
            Spacer(Modifier.height(24.dp))
        } else {
            GuestHeader(
                onLoginClick = { onEvent(ProfileEvent.LoginClicked) },
                onSignUpClick = { onEvent(ProfileEvent.SignUpClicked) },
            )
            Spacer(Modifier.height(24.dp))
        }

        // ── Settings section ──────────────────────────────────────────────────
        SectionLabel(stringResource(R.string.settings))

        MenuToggleItem(
            icon = Bell,
            title = stringResource(R.string.notifications),
            subtitle = stringResource(R.string.daily_prayer_reminders),
            checked = state.notificationsEnabled && state.remindersReady,
            onCheckedChange = { onEvent(ProfileEvent.NotificationsChanged(it)) },
            enabled = true,
        )

        if (state.notificationsEnabled) {
            MenuDivider()

            MenuNavigationItem(
                icon = Bell,
                title = stringResource(R.string.adhan_sound),
                subtitle = AdhanPreference.getDisplayName(state.adhanSoundType),
                onClick = { onEvent(ProfileEvent.SoundClicked) },
            )
        }

        MenuNavigationItem(
            icon = Info,
            title = stringResource(R.string.reminder_setup),
            subtitle = stringResource(if (state.remindersReady) R.string.reminders_ready else R.string.reminders_need_permission),
            onClick = { onEvent(ProfileEvent.ReminderSetupClicked) },
        )

        MenuDivider()

        MenuNavigationItem(
            icon = Globe,
            title = stringResource(R.string.language),
            subtitle = if (state.currentLanguage == LanguagePreference.LANGUAGE_INDONESIAN) {
                stringResource(R.string.indonesian)
            } else {
                stringResource(R.string.english)
            },
            onClick = { onEvent(ProfileEvent.LanguageClicked) }
        )

        Spacer(Modifier.height(24.dp))

        // ── Other section ─────────────────────────────────────────────────────
        SectionLabel(stringResource(R.string.other))

        MenuNavigationItem(
            icon = Star,
            title = stringResource(R.string.rate_app),
            subtitle = stringResource(R.string.enjoying_leave_review),
            onClick = { onEvent(ProfileEvent.RateAppClicked) }
        )

        MenuDivider()

        MenuNavigationItem(
            icon = Info,
            title = stringResource(R.string.about),
            subtitle = stringResource(R.string.version_label),
            onClick = { onEvent(ProfileEvent.AboutClicked) }
        )

        MenuDivider()

        MenuNavigationItem(
            icon = Trash,
            title = stringResource(R.string.clear_cache),
            subtitle = stringResource(R.string.free_up_storage),
            onClick = { onEvent(ProfileEvent.ClearCacheClicked) }
        )

        MenuDivider()

        if (state.isLoggedIn) {
            MenuActionItem(
                icon = SignOut,
                title = stringResource(R.string.logout),
                tint = AyatQuTheme.colors.error,
                onClick = { onEvent(ProfileEvent.LogoutClicked) },
            )
        }

        Spacer(Modifier.height(32.dp))
    }

    if (state.showSoundDialog) SoundSelectionDialog(state, onEvent)

    if (state.showReminderSetupDialog) {
        AlertDialog(
            onDismissRequest = { onEvent(ProfileEvent.DialogDismissed) },
            title = { Text(stringResource(R.string.reminder_setup)) },
            text = {
                Column {
                    Text(stringResource(R.string.reminder_setup_description))
                    TextButton(onClick = { onEvent(ProfileEvent.OpenAppSettingsClicked) }) {
                        Text(stringResource(R.string.open_app_settings))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { onEvent(ProfileEvent.ReminderSetupConfirmed) }) {
                    Text(stringResource(R.string.continue_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { onEvent(ProfileEvent.DialogDismissed) }) { Text(stringResource(R.string.cancel)) }
            },
        )
    }

    if (state.showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = { onEvent(ProfileEvent.LogoutConfirmed) },
            onDismiss = { onEvent(ProfileEvent.DialogDismissed) },
        )
    }

    if (state.showClearCacheDialog) {
        ClearCacheConfirmationDialog(
            onConfirm = { onEvent(ProfileEvent.ClearCacheConfirmed) },
            onDismiss = { onEvent(ProfileEvent.DialogDismissed) },
        )
    }

    if (state.showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = state.currentLanguage,
            onSelectLanguage = { onEvent(ProfileEvent.LanguageSelected(it)) },
            onDismiss = { onEvent(ProfileEvent.DialogDismissed) },
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
                text = stringResource(R.string.logout_title),
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        text = {
            Text(
                text = stringResource(R.string.logout_message),
                style = MaterialTheme.typography.bodyLarge,
                color = AyatQuTheme.colors.textMuted,
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = AyatQuTheme.colors.error,
                ),
            ) {
                Text(
                    text = stringResource(R.string.logout),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun ClearCacheConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.clear_cache_title),
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        text = {
            Text(
                text = stringResource(R.string.clear_cache_message),
                style = MaterialTheme.typography.bodyLarge,
                color = AyatQuTheme.colors.textMuted,
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = AyatQuTheme.colors.primary,
                ),
            ) {
                Text(
                    text = stringResource(R.string.clear),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun LanguageSelectionDialog(
    currentLanguage: String,
    onSelectLanguage: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val languages = listOf(
        LanguagePreference.LANGUAGE_ENGLISH to stringResource(R.string.english),
        LanguagePreference.LANGUAGE_INDONESIAN to stringResource(R.string.indonesian),
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.language),
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        text = {
            Column {
                languages.forEach { (code, name) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectLanguage(code) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .border(
                                    2.dp,
                                    if (currentLanguage == code) AyatQuTheme.colors.primary else AyatQuTheme.colors.textMuted,
                                    CircleShape,
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (currentLanguage == code) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(AyatQuTheme.colors.primary),
                                )
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
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
                .background(AyatQuTheme.colors.primary),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = initial,
                color = AyatQuTheme.colors.onPrimary,
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
            color = AyatQuTheme.colors.textMuted,
            textAlign = TextAlign.Center,
        )
    }
}

// ── Guest header (not logged in) ──────────────────────────────────────────────

@Composable
private fun GuestHeader(
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 8.dp, start = 20.dp, end = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Guest avatar
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(AyatQuTheme.colors.disabled),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = id.vanard.ayatqu.core.ui.icon.UserCircle,
                contentDescription = null,
                tint = AyatQuTheme.colors.textMuted,
                modifier = Modifier.size(56.dp),
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.welcome_guest),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.sign_in_sync),
            style = MaterialTheme.typography.bodyMedium,
            color = AyatQuTheme.colors.textMuted,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(20.dp))

        // Sign In button
        androidx.compose.material3.Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = AyatQuTheme.colors.primary,
                contentColor = AyatQuTheme.colors.onPrimary,
            ),
        ) {
            Text(
                text = stringResource(R.string.sign_in),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(Modifier.height(12.dp))

        // Sign Up button
        androidx.compose.material3.OutlinedButton(
            onClick = onSignUpClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = CircleShape,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = AyatQuTheme.colors.primary,
            ),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, AyatQuTheme.colors.primary),
        ) {
            Text(
                text = stringResource(R.string.create_account),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

// ── Section label ─────────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(label: String) {
    Text(
        text = label.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = AyatQuTheme.colors.textMuted,
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
    enabled: Boolean = true,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (enabled) AyatQuTheme.colors.primary else AyatQuTheme.colors.primary.copy(alpha = 0.4f),
            modifier = Modifier.size(24.dp),
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = if (enabled) MaterialTheme.colorScheme.onBackground
                        else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (enabled) AyatQuTheme.colors.textMuted else AyatQuTheme.colors.textMuted.copy(alpha = 0.4f),
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = AyatQuTheme.colors.onPrimary,
                checkedTrackColor = AyatQuTheme.colors.primary,
                uncheckedThumbColor = AyatQuTheme.colors.surface,
                uncheckedTrackColor = AyatQuTheme.colors.disabled,
                disabledCheckedThumbColor = AyatQuTheme.colors.onPrimary.copy(alpha = 0.4f),
                disabledCheckedTrackColor = AyatQuTheme.colors.primary.copy(alpha = 0.4f),
                disabledUncheckedThumbColor = AyatQuTheme.colors.surface.copy(alpha = 0.4f),
                disabledUncheckedTrackColor = AyatQuTheme.colors.disabled.copy(alpha = 0.4f),
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
            tint = AyatQuTheme.colors.primary,
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
                color = AyatQuTheme.colors.textMuted,
            )
        }
        Icon(
            imageVector = CaretRight,
            contentDescription = null,
            tint = AyatQuTheme.colors.textMuted,
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
        color = AyatQuTheme.colors.divider,
        thickness = 0.5.dp,
    )
}

// ── Sound type selector ──────────────────────────────────────────────────────

@Composable
private fun SoundSelectionDialog(state: ProfileState, onEvent: OnProfileEvent) {
    AlertDialog(
        onDismissRequest = { onEvent(ProfileEvent.SoundDismissed) },
        title = { Text(stringResource(R.string.adhan_sound)) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Text(stringResource(R.string.sound_preview_hint))
                AdhanPreference.soundTypes.forEach { type ->
                    Row(
                        Modifier.fillMaxWidth()
                            .clickable { onEvent(ProfileEvent.SoundTypeChanged(type)) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        androidx.compose.material3.RadioButton(
                            selected = state.pendingSoundType == type,
                            onClick = null,
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(if (type == AdhanPreference.SOUND_TYPE_SILENT) stringResource(R.string.silent)
                            else AdhanPreference.getDisplayName(type))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = state.pendingSoundType != null && state.soundPreviewReady,
                onClick = { onEvent(ProfileEvent.SoundConfirmed) },
            ) { Text(stringResource(R.string.ok)) }
        },
        dismissButton = {
            TextButton(onClick = { onEvent(ProfileEvent.SoundDismissed) }) { Text(stringResource(R.string.cancel)) }
        },
    )
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Light - Logged In")
@Composable
private fun ProfileScreenPreviewLight() {
    AyatQuTheme(darkTheme = false) {
        ProfileScreen(
            state = ProfileState(
                isLoggedIn = true,
                displayName = "Abdullah",
                email = "abdullah@email.com",
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, name = "Dark - Logged In")
@Composable
private fun ProfileScreenPreviewDark() {
    AyatQuTheme(darkTheme = true) {
        ProfileScreen(
            state = ProfileState(
                isLoggedIn = true,
                displayName = "Abdullah",
                email = "abdullah@email.com",
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true, name = "Guest - Not Logged In")
@Composable
private fun ProfileScreenPreviewGuest() {
    AyatQuTheme(darkTheme = false) {
        ProfileScreen(
            state = ProfileState(),
            onEvent = {},
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
