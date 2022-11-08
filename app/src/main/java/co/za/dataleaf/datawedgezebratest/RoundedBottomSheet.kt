package co.za.dataleaf.datawedgezebratest

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.za.dataleaf.datawedgezebratest.databinding.BottomSheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * BottomSheetDialog fragment that uses a custom
 * theme which sets a rounded background to the dialog
 * and doesn't dim the navigation bar
 */
open class RoundedBottomSheet : BottomSheetDialogFragment() {
    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)
}

class ShowRoundDialogFragment : BottomSheetDialogFragment() {

    private lateinit var _binding: BottomSheetDialogBinding
    companion object {
        fun newInstance(): ShowRoundDialogFragment {
            return ShowRoundDialogFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = BottomSheetDialogBinding.inflate(inflater)

        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding.btnClose.setOnClickListener {
            this.dismiss()
        }

        _binding.btnSetCameraScanner.setOnClickListener {
            Log.i("TAG", "Set camera as default scanner ...")
        }
    }
}

object DialogHelper {

    @JvmStatic
    fun bottomShowRoundDialogBuild(): ShowRoundDialogFragment {
        return ShowRoundDialogFragment.newInstance()
    }
}