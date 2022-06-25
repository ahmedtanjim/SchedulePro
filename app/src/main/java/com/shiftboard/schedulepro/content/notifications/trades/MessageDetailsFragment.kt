package com.shiftboard.schedulepro.content.notifications.trades;

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shiftboard.schedulepro.R
import com.shiftboard.schedulepro.common.prefs.AppPrefs
import com.shiftboard.schedulepro.core.common.PageState
import com.shiftboard.schedulepro.core.common.setState
import com.shiftboard.schedulepro.core.common.utils.*
import com.shiftboard.schedulepro.core.network.model.notification.BulkMessaging
import com.shiftboard.schedulepro.databinding.MessageDetailsDialogBinding
import com.shiftboard.schedulepro.ui.lazyViewBinding
import com.shiftboard.schedulepro.ui.tryWith

import org.threeten.bp.OffsetDateTime

class MessageDetailsFragment(
        private val notification: BulkMessaging,
        private val sentDateUtc: OffsetDateTime
        ) : BottomSheetDialogFragment() {
        private val boundView by lazyViewBinding<MessageDetailsDialogBinding>()
        override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?,
        ): View? {
                return inflater.inflate(R.layout.message_details_dialog, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)

                tryWith(boundView) {
                        updateFields()
                }
        }

        private fun updateFields() {
                tryWith(boundView) {
                        stateFlipper.setState(PageState.SUCCESS)
                        dialogContent.headerTitle.text = notification.subject
                        dialogContent.messageManagerName.text = notification.fromEmployeeName
                        dialogContent.messageDetailsText.text = notification.message
                        dialogContent.messageDate.text = sentDateUtc.modifiedTimestamp(
                                AppPrefs.militaryTime)
                        dialogContent.messageDetailsText.movementMethod = ScrollingMovementMethod()
                        dialogContent.imageView.setOnClickListener {
                                this@MessageDetailsFragment.dismiss()
                        }
                }
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                val dialog: BottomSheetDialog =
                        super.onCreateDialog(savedInstanceState) as BottomSheetDialog

                dialog.setOnShowListener {

                        val bottomSheet =
                                (it as BottomSheetDialog).findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)

                        val behavior = bottomSheet?.let { layout -> BottomSheetBehavior.from(layout) }

                        behavior?.peekHeight = Resources.getSystem().displayMetrics.heightPixels

                }
                return dialog
        }

}
