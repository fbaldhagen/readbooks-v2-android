package com.fbaldhagen.readbooks.ui.reader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.util.AbsoluteUrl
import androidx.core.net.toUri
import com.fbaldhagen.readbooks.ui.reader.presentation.ReaderViewModel
import org.readium.r2.navigator.epub.css.RsProperties
import org.readium.r2.navigator.input.InputListener
import org.readium.r2.navigator.input.TapEvent

@OptIn(ExperimentalReadiumApi::class)
@AndroidEntryPoint
class ReaderFragment : Fragment() {

    private val viewModel: ReaderViewModel by activityViewModels()
    private var navigator: EpubNavigatorFragment? = null
    private val navigatorContainerId = View.generateViewId()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FrameLayout(requireContext()).apply {
        id = navigatorContainerId
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val state = viewModel.state.value
        val publication = state.publication ?: return

        if (savedInstanceState == null) {
            val factory = EpubNavigatorFactory(publication)
            val fragmentFactory = factory.createFragmentFactory(
                initialLocator = state.initialLocator,
                initialPreferences = state.preferences.toEpubPreferences(),
                listener = navigatorListener,
                configuration = EpubNavigatorFragment.Configuration(
                    readiumCssRsProperties = RsProperties(
                        overrides = mapOf(
                            "padding-top" to "40px",
                            "padding-bottom" to "55px"
                        )
                    )
                )
            )

            childFragmentManager.fragmentFactory = fragmentFactory
            childFragmentManager.beginTransaction()
                .replace(
                    navigatorContainerId,
                    EpubNavigatorFragment::class.java,
                    Bundle(),
                    NAVIGATOR_TAG
                )
                .commit()
        }

        childFragmentManager.addFragmentOnAttachListener { _, fragment ->
            if (fragment is EpubNavigatorFragment) {
                navigator = fragment
                observeNavigator()
                observePreferences()
            }
        }
    }

    private fun observeNavigator() {
        navigator?.addInputListener(inputListener)

        navigator?.currentLocator
            ?.onEach { locator ->
                viewModel.onLocatorChanged(locator)
            }
            ?.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private val inputListener = object : InputListener {
        override fun onTap(event: TapEvent): Boolean {
            viewModel.onToggleBars()
            return true
        }
    }

    private fun observePreferences() {
        viewModel.state
            .distinctUntilChangedBy { it.preferences }
            .onEach { state ->
                navigator?.submitPreferences(state.preferences.toEpubPreferences())
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private val navigatorListener = object : EpubNavigatorFragment.Listener {
        override fun onExternalLinkActivated(url: AbsoluteUrl) {
            val intent = android.content.Intent(
                android.content.Intent.ACTION_VIEW,
                url.toString().toUri()
            )
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        navigator?.removeInputListener(inputListener)
        navigator = null
    }

    companion object {
        private const val NAVIGATOR_TAG = "epub_navigator"
    }
}