package eu.icard.androidmobilepaymentsipgsdk.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import eu.icard.androidmobilepaymentsipgsdk.PopupWindowHelper
import eu.icard.androidmobilepaymentsipgsdk.R
import eu.icard.androidmobilepaymentsipgsdk.utils.Utils
import eu.icard.mobilepaymentssdk.ICardDirectSDK
import kotlinx.android.synthetic.main.fragment_settings.*
import java.util.*
import kotlin.collections.HashMap

class SettingsFragment : Fragment(), CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private lateinit var preferences            : SharedPreferences

    private lateinit var editor                 : SharedPreferences.Editor

    private lateinit var root: View

    private val languages                        = listOf("en", "bg", "de", "it", "ro")

    private val fonts                            = HashMap<String, String>()

    private val colorsAccent                     = HashMap<Int, String>()

    private val textColors                       = HashMap<Int, String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        root        = inflater.inflate(R.layout.fragment_settings, container, false)

        fonts[ICardDirectSDK.FONT_CAROSSOFT]    = "Carrosoft"
        fonts[ICardDirectSDK.FONT_LATO]         = "Lato"
        fonts[ICardDirectSDK.FONT_MONTSERRAT]   = "Montserrat"
        fonts[ICardDirectSDK.FONT_OPEN_SANS]    = "Open Sans"
        fonts[ICardDirectSDK.FONT_RALEWAY]      = "Releway"
        fonts[ICardDirectSDK.FONT_ROBOTO_SLAB]  = "Roboto Slab"
        fonts[ICardDirectSDK.FONT_ROBOTO_SLAB]  = "Sf Pro"

        colorsAccent[R.color.main_green]        = "Green"
        colorsAccent[R.color.white]             = "White"
        colorsAccent[R.color.dark_red]          = "red"

        textColors[R.color.white]               = "White"
        textColors[R.color.black]               = "Black"
        textColors[R.color.colorAccent]         = "Blue"
        textColors[R.color.darkGrayColor]       = "Gray"

        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        editor      = preferences.edit()

        // Inflate the layout for this fragment
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setData()
    }

    private fun setData() {
        val font                    = preferences.getString(Utils.PREFERENCES_FONT      , "")
        val languageIsoCode         = preferences.getString(Utils.PREFERENCES_LANGUAGE  , "en")
        val colorAccent             = preferences.getInt(Utils.PREFERENCES_COLOR_ACCENT , 0)
        val textColor               = preferences.getInt(Utils.PREFERENCES_TEXT_COLOR   , 0)

        val locale                  = Locale(languageIsoCode ?: "en")

        darkModeSwitch.isChecked    = preferences.getBoolean(Utils.PREFERENCES_IS_DARK_MODE, false)
        languageTxt.text            = locale.getDisplayLanguage(locale)

        fontTxt.text                = if(font.isNullOrEmpty()) "None" else fonts[font]
        colorAccentTxt.text         = if(colorAccent == 0) "None" else colorsAccent[colorAccent]
        textColorTxt.text           = if(textColor == 0) "None" else textColors[textColor]
    }

    private fun setListeners() {
        darkModeSwitch.setOnCheckedChangeListener(this)
        languageTxt.setOnClickListener(this)
        fontTxt.setOnClickListener(this)
        colorAccentTxt.setOnClickListener(this)
        textColorTxt.setOnClickListener(this)
    }

    private fun showLanguagePopUp() {
        val names = languages.map {
            val locale = Locale(it)
            locale.getDisplayLanguage(locale)
        }
        val popupWindowHelper = PopupWindowHelper(requireContext(), names, object : PopupWindowHelper.OnItemSelected {
            override fun onItemChosen(item: String, position: Int) {
                languageTxt.text    = item
                editor.putString(Utils.PREFERENCES_LANGUAGE, languages[position]).apply()
                ICardDirectSDK.setLanguage(languages[position])
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        })
        popupWindowHelper.showPopup(languageTxt)
    }

    private fun showFontsPopUp() {
        val fontsName = fonts.map { it.value }.toMutableList()
        fontsName.add(0, "None")
        val popupWindowHelper = PopupWindowHelper(requireContext(), fontsName, object : PopupWindowHelper.OnItemSelected {
            override fun onItemChosen(item: String, position: Int) {
                fontTxt.text    = item
                if(position > 0) {
                    fonts.forEach {
                        if(it.value == item) {
                            editor.putString(Utils.PREFERENCES_FONT, it.key).apply()
                            ICardDirectSDK.font = it.key
                        }
                    }
                } else {
                    editor.putString(Utils.PREFERENCES_FONT, null).apply()
                    ICardDirectSDK.font = null
                }
            }
        })
        popupWindowHelper.showPopup(fontTxt)
    }

    private fun showColorsPopUp(colors: HashMap<Int, String>, textView: TextView) {
        val colorsName = colors.map { it.value }.toMutableList()
        colorsName.add(0, "None")
        val popupWindowHelper = PopupWindowHelper(requireContext(), colorsName, object : PopupWindowHelper.OnItemSelected {
            override fun onItemChosen(item: String, position: Int) {
                textView.text    = item
                if(position > 0) {
                    colors.forEach {
                        if(it.value == item) {
                            if(textView.id == R.id.colorAccentTxt) {
                                editor.putInt(Utils.PREFERENCES_COLOR_ACCENT, it.key).apply()
                                ICardDirectSDK.buttonColor  = it.key
                                ICardDirectSDK.toolbarColor = it.key
                            }
                            else {
                                editor.putInt(Utils.PREFERENCES_TEXT_COLOR, it.key).apply()
                                ICardDirectSDK.toolbarTextColor    = it.key
                                ICardDirectSDK.buttonTextColor     = it.key
                            }
                        }
                    }
                } else {
                    if(textView.id == R.id.colorAccentTxt) {
                        editor.putInt(Utils.PREFERENCES_COLOR_ACCENT, 0).apply()
                        ICardDirectSDK.buttonColor  = null
                        ICardDirectSDK.toolbarColor = null
                    }
                    else {
                        editor.putInt(Utils.PREFERENCES_TEXT_COLOR, 0).apply()
                        ICardDirectSDK.toolbarTextColor    = null
                        ICardDirectSDK.buttonTextColor     = null
                    }
                }
            }
        })
        popupWindowHelper.showPopup(fontTxt)
    }

    override fun onCheckedChanged(p0: CompoundButton?, isChecked: Boolean) {
        ICardDirectSDK.isDarkMode   = isChecked
        preferences.edit()
            .putBoolean(Utils.PREFERENCES_IS_DARK_MODE, isChecked)
            .apply()
    }

    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.languageTxt -> showLanguagePopUp()

            R.id.fontTxt -> showFontsPopUp()

            R.id.colorAccentTxt -> showColorsPopUp(colorsAccent, colorAccentTxt)

            R.id.textColorTxt -> showColorsPopUp(textColors, textColorTxt)
        }
    }
}