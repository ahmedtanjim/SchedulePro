package com.shiftboard.schedulepro.content.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.compose.material.ExperimentalMaterialApi
import androidx.core.app.ShareCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.shiftboard.schedulepro.FlavorSettings
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.activities.auth.AuthManager
import com.shiftboard.schedulepro.activities.root.BottomNavFragment
import com.shiftboard.schedulepro.common.prefs.AppPrefs
import com.shiftboard.schedulepro.content.profile.unavailability.UnavailabilityActivity
import com.shiftboard.schedulepro.core.common.analytics.PageViewAnalyticEvents
import com.shiftboard.schedulepro.core.common.analytics.PageViewEvent
import com.shiftboard.schedulepro.databinding.ProfileFragmentBinding
import com.shiftboard.schedulepro.ui.lazyViewBinding
import com.shiftboard.schedulepro.ui.tryWith
import org.koin.androidx.viewmodel.ext.android.viewModel


@ExperimentalMaterialApi
class ProfileFragment: BottomNavFragment() {
    override val titleRes: Int = R.string.profile
    override val layoutRes: Int = R.layout.profile_fragment

    private val boundView by lazyViewBinding<ProfileFragmentBinding>()
    private val viewModel by viewModel<ProfileViewModel>()

    override fun createAnalyticsEvent(): PageViewEvent = PageViewAnalyticEvents.Profile

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tryWith(boundView) {

            logout.setOnClickListener {
                lifecycleScope.launchWhenStarted {
                    AuthManager().logout(requireActivity())
                }
            }

            feedback.setOnClickListener {
                startEmailIntent()
            }

            availability.setOnClickListener {
                startActivity(Intent(requireContext(), UnavailabilityActivity::class.java))
            }

            viewModel.userDetailsLiveData.observe(viewLifecycleOwner) {

                profileTitle.text = getString(R.string.full_name, it.firstName, it.lastName)
            }

            libraries.setOnClickListener {
                startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java))
            }

            privacy.setOnClickListener {
                startUrlIntent(getString(R.string.privacy_url))
            }

            terms.setOnClickListener {
                startUrlIntent(getString(R.string.terms_url))
            }

            buildInfo.text = getString(R.string.version_identifier, FlavorSettings.versionName())
        }
    }

    private fun startEmailIntent() {
        ShareCompat.IntentBuilder.from(requireActivity())
            .setType("message/rfc822")
            .addEmailTo(AppPrefs.environment.feedbackEmail)
            .setSubject(getString(R.string.feedback_subject_line))
            .setChooserTitle(getString(R.string.feedback_chooser_title))
            .startChooser()
    }

    private fun startUrlIntent(url : String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        if (activity?.packageManager?.let { intent.resolveActivity(it) } != null) {
            startActivity(intent)
        }
    }
}