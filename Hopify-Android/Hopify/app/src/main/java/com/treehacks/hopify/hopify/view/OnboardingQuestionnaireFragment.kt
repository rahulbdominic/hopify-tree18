package com.treehacks.hopify.hopify.view

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlaceAutocomplete.*
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.changes
import com.jakewharton.rxbinding2.widget.textChanges
import com.jakewharton.rxrelay2.Relay
import com.treehacks.hopify.hopify.R
import com.treehacks.hopify.hopify.model.QuestionnaireViewModel
import kotterknife.bindView

/**
 * Created by rahul on 17/02/2018.
 */

class OnboardingQuestionnaireFragment : Fragment() {
    private val locationChooseButton by bindView<Button>(R.id.questionnaire_choose_location_button)
    private val radiusEditText by bindView<EditText>(R.id.questionnaire_radius_edit_text)
    private val hoursEditText by bindView<EditText>(R.id.questionnaire_hours_edit_text)
    private val priceSeekBar by bindView<SeekBar>(R.id.questionnaire_price_seek_bar)
    private val nextButton by bindView<Button>(R.id.questionnaire_next_button)

    private lateinit var relay: Relay<QuestionnaireViewModel>

    private var viewModel = QuestionnaireViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.onboarding_questionnaire_fragment, container, false) as LinearLayout
    }

    override fun onResume() {
        super.onResume()
        setUpUiElements()
    }

    private fun setUpUiElements() {
        viewModel = viewModel.withParams(
                hours = hoursEditText.text.toString().toInt(),
                radius = radiusEditText.text.toString().toInt() * 1000,
                maxPrice = priceSeekBar.progress
        )
        locationChooseButton.clicks().subscribe {
            try {
                val intent = IntentBuilder(MODE_FULLSCREEN).build(activity)
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
            } catch (e: GooglePlayServicesRepairableException) {
                Log.e(LOG_TAG, e.message)
            } catch (e: GooglePlayServicesNotAvailableException) {
                Log.i(LOG_TAG, e.message)
            }
        }
        hoursEditText.textChanges().subscribe {
            viewModel = viewModel.withParams(hours = if (it.toString() == "") 0 else it.toString().toInt())
        }
        radiusEditText.textChanges().subscribe {
            viewModel = viewModel.withParams(radius = if (it.toString() == "") 0 else it.toString().toInt() * 1000)
        }
        priceSeekBar.changes().subscribe {
            viewModel = viewModel.withParams(maxPrice = it)
        }
        nextButton.clicks().subscribe {
            if (viewModel.nextButtonEnabled) {
                relay.accept(viewModel)
            }
        }
    }

    private fun toggleButtonColor() {
        val pl = locationChooseButton.paddingLeft
        val pt = locationChooseButton.paddingTop
        val pr = locationChooseButton.paddingRight
        val pb = locationChooseButton.paddingBottom

        locationChooseButton.background =
                ContextCompat.getDrawable(context, R.drawable.interests_tile_background_selected)

        locationChooseButton.setPadding(pl, pt, pr, pb)
        locationChooseButton.setTextColor(ContextCompat.getColor(context, R.color.grey))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    val place = getPlace(context, data)
                    locationChooseButton.text = place.name

                    toggleButtonColor()

                    viewModel = viewModel.withParams(
                            lat = place.latLng.latitude,
                            lng = place.latLng.longitude
                    )
                }
                RESULT_ERROR -> {
                    val status = getStatus(context, data)
                    Log.i(LOG_TAG, status.statusMessage)
                }
                RESULT_CANCELED -> {
                }
            }
        }
    }

    companion object {
        private val LOG_TAG: String = OnboardingQuestionnaireFragment::class.java.name
        private const val PLACE_AUTOCOMPLETE_REQUEST_CODE: Int = 1

        fun newInstance(relay: Relay<QuestionnaireViewModel>): OnboardingQuestionnaireFragment {
            val fragment = OnboardingQuestionnaireFragment()
            fragment.relay = relay
            return fragment
        }
    }
}