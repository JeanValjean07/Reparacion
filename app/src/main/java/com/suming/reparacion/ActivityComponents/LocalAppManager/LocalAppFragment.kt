package com.suming.reparacion.ActivityComponents.LocalAppManager

import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment

class LocalAppFragment : DialogFragment() {
    companion object {
        fun newInstance(): LocalAppFragment = LocalAppFragment().apply { arguments = bundleOf(  ) }
    }


}