package eu.icard.androidmobilepaymentsipgsdk

import android.content.Context
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.PopupWindow

class PopupWindowHelper(private val context: Context, private val values: List<String>, private val listener: OnItemSelected ) : AdapterView.OnItemClickListener {

    private var popupWindow: PopupWindow

    interface OnItemSelected {
        fun onItemChosen(item: String, position: Int)
    }

    init {
        val popupContentView            = LayoutInflater.from(context).inflate(R.layout.popup_window, null)
        popupWindow                     = PopupWindow(context)
        popupWindow.contentView         = popupContentView
        popupWindow.width               = ViewGroup.LayoutParams.WRAP_CONTENT
        popupWindow.height              = ViewGroup.LayoutParams.WRAP_CONTENT
        popupWindow.isOutsideTouchable  = true
        popupWindow.setBackgroundDrawable(null)


        setAdapter(popupContentView.findViewById(R.id.popupListView))
    }

    private fun setAdapter(listView: ListView){
        val adapter = PopUpWindowAdapter(context, values)
        listView.adapter = adapter

        listView.onItemClickListener = this
    }

    fun showPopup(clickedView: View){
        if(!popupWindow.isShowing)
            popupWindow.showAsDropDown(clickedView)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        popupWindow.dismiss()
        listener.onItemChosen(values[position], position)
    }
}